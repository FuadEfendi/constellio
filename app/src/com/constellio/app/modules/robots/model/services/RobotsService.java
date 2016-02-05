package com.constellio.app.modules.robots.model.services;

import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.from;
import static com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators.where;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.constellio.app.modules.robots.model.wrappers.Robot;
import com.constellio.app.modules.robots.services.RobotSchemaRecordServices;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.search.SearchServices;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;

public class RobotsService {
	private final RecordServices recordServices;
	private final SearchServices searchServices;
	private final RobotSchemaRecordServices robots;

	public RobotsService(String collection, AppLayerFactory appLayerFactory) {
		ModelLayerFactory factory = appLayerFactory.getModelLayerFactory();
		recordServices = factory.newRecordServices();
		searchServices = factory.newSearchServices();
		robots = new RobotSchemaRecordServices(collection, appLayerFactory);
	}

	public Robot newRobot(Robot parent) {
		Robot robot = robots.newRobot();
		if (parent != null) {
			robot.setParent(parent);
			robot.setSchemaFilter(parent.getSchemaFilter());
		}
		return robot;
	}

	public Robot newRobot(String parentId) {
		Robot parent = parentId != null ? robots.getRobot(parentId) : null;
		return newRobot(parent);
	}

	public List<Robot> loadAncestors(Robot robot) {
		String path = robot.getPaths().get(0);
		List<String> lineage = Arrays.asList(path.substring(1).split("/"));
		// TODO: The sort is just a quick workaround
		LogicalSearchQuery query = new LogicalSearchQuery(from(robots.robot.schemaType()).where(Schemas.IDENTIFIER).isIn(lineage))
				.sortAsc(Schemas.CREATED_ON);
		return robots.searchRobots(query);
	}

	public List<Robot> loadAncestors(String robotId) {
		return loadAncestors(robots.getRobot(robotId));
	}

	public List<String> loadIdTreeOf(String robotId) {
		List<String> result = new ArrayList<>();
		result.add(robotId);
		List<String> generation = loadGenerationIds(Arrays.asList(robotId));
		while (!generation.isEmpty()) {
			result.addAll(generation);
			generation = loadGenerationIds(generation);
		}
		return result;
	}

	private List<String> loadGenerationIds(List<String> robotIds) {
		LogicalSearchQuery query = new LogicalSearchQuery(
				from(robots.robot.schemaType()).where(robots.robot.parent()).isIn(robotIds));
		return searchServices.searchRecordIds(query);
	}

	public void deleteRobotHierarchy(Robot robot) {
		for (Robot child : getChildRobots(robot.getId())) {
			deleteRobotHierarchy(child);
		}
		recordServices.logicallyDelete(robot.getWrappedRecord(), User.GOD);
		recordServices.physicallyDelete(robot.getWrappedRecord(), User.GOD);
	}

	public void deleteRobotHierarchy(String robotId) {
		deleteRobotHierarchy(robots.getRobot(robotId));
	}

	public List<Robot> getRootRobots() {
		return robots.searchRobots(where(robots.robot.parent()).isNull());
	}

	public List<Robot> getChildRobots(String robotId) {
		return robots.searchRobots(where(robots.robot.parent()).isEqualTo(robotId));
	}
}