package com.constellio.app.ui;

import org.junit.Before;
import org.junit.Test;

import com.constellio.app.modules.rm.RMTestRecords;
import com.constellio.app.modules.robots.model.wrappers.Robot;
import com.constellio.app.modules.robots.services.RobotSchemaRecordServices;
import com.constellio.model.entities.records.Transaction;
import com.constellio.model.services.records.RecordServicesException;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.annotations.MainTest;
import com.constellio.sdk.tests.annotations.MainTestDefaultStart;
import com.constellio.sdk.tests.annotations.UiTest;
import com.constellio.sdk.tests.selenium.adapters.constellio.ConstellioWebDriver;
import com.constellio.sdk.tests.setups.Users;

@UiTest
@MainTest
public class StartConstellioRobotDemoAcceptTest extends ConstellioTest {
	ConstellioWebDriver driver;
	RMTestRecords records;
	Users users = new Users();

	@Before
	public void setUp()
			throws Exception {
		givenCollection(zeCollection).withMockedAvailableModules(false).withConstellioRMModule().withConstellioESModule()
				.withRobotsModule().withAllTestUsers();
		records = new RMTestRecords(zeCollection).setup(getAppLayerFactory()).withFoldersAndContainersOfEveryStatus();
	}

	@Test
	@MainTestDefaultStart
	public void startOnHomePageAsAdmin()
			throws Exception {
		//users.adminIn(zeCollection).setCollectionAllAccess(true);
		driver = newWebDriver(loggedAsUserInCollection(admin, zeCollection));
		waitUntilICloseTheBrowsers();
	}

	private void createRobotsHierarchy()
			throws RecordServicesException {
		RobotSchemaRecordServices robots = new RobotSchemaRecordServices(zeCollection, getAppLayerFactory());

		Transaction transaction = new Transaction();
		Robot rootRobot = robots.newRobotWithId("root").setCode("root").setTitle("Root robot");
		Robot child1 = robots.newRobotWithId("child1").setCode("child1").setTitle("Child 1").setParent(rootRobot);
		Robot child2 = robots.newRobotWithId("child2").setCode("child2").setTitle("Child 2").setParent(rootRobot);
		Robot child11 = robots.newRobotWithId("child11").setCode("child11").setTitle("Child 1.1").setParent(child1);
		Robot child12 = robots.newRobotWithId("child12").setCode("child12").setTitle("Child 1.2").setParent(child1);
		Robot child21 = robots.newRobotWithId("child21").setCode("child21").setTitle("Child 2.1").setParent(child2);

		transaction.add(rootRobot);
		transaction.add(child1);
		transaction.add(child2);
		transaction.add(child11);
		transaction.add(child12);
		transaction.add(child21);

		getModelLayerFactory().newRecordServices().execute(transaction);
	}
}
