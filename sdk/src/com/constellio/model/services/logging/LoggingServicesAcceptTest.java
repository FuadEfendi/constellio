package com.constellio.model.services.logging;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import com.constellio.app.modules.rm.RMTestRecords;
import com.constellio.app.modules.rm.services.RMSchemasRecordsServices;
import com.constellio.app.modules.rm.services.events.RMEventsSearchServices;
import com.constellio.app.modules.rm.wrappers.Folder;
import com.constellio.model.entities.Taxonomy;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.Transaction;
import com.constellio.model.entities.records.wrappers.Event;
import com.constellio.model.entities.records.wrappers.EventType;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.schemas.Schemas;
import com.constellio.model.entities.security.Authorization;
import com.constellio.model.entities.security.AuthorizationDetails;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.records.SchemasRecordsServices;
import com.constellio.model.services.search.SearchServices;
import com.constellio.model.services.search.query.logical.LogicalSearchQuery;
import com.constellio.model.services.search.query.logical.LogicalSearchQueryOperators;
import com.constellio.model.services.search.query.logical.condition.LogicalSearchCondition;
import com.constellio.model.services.users.UserServices;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.TestRecord;
import com.constellio.sdk.tests.schemas.TestsSchemasSetup;
import com.constellio.sdk.tests.schemas.TestsSchemasSetup.ZeSchemaMetadatas;
import com.constellio.sdk.tests.setups.Users;

public class LoggingServicesAcceptTest extends ConstellioTest {

	LocalDateTime shishOClock = new LocalDateTime().minusHours(3);
	LocalDateTime tockOClock = new LocalDateTime().minusHours(2);
	LocalDateTime teaOClock = new LocalDateTime().minusHours(1);

	TestsSchemasSetup zeCollectionSetup = new TestsSchemasSetup(zeCollection);
	ZeSchemaMetadatas zeSchema = zeCollectionSetup.new ZeSchemaMetadatas();

	Users users = new Users();

	RecordServices recordServices;
	LoggingServices loggingServices;

	RMSchemasRecordsServices rm;
	private RMTestRecords records = new RMTestRecords(zeCollection);

	RMEventsSearchServices rmEventsSearchServices;
	SearchServices searchServices;
	private User alice;

	@Before
	public void setUp()
			throws Exception {

		prepareSystem(
				withZeCollection().withAllTestUsers().withConstellioRMModule().withRMTest(records)
						.withFoldersAndContainersOfEveryStatus()
		);
		inCollection(zeCollection).giveReadAccessTo(admin);

		recordServices = getModelLayerFactory().newRecordServices();
		loggingServices = getModelLayerFactory().newLoggingServices();

		defineSchemasManager().using(zeCollectionSetup);
		Taxonomy taxonomy = Taxonomy.createPublic("taxo", "taxo", zeCollection, asList("zeSchemaType"));
		getModelLayerFactory().getTaxonomiesManager().addTaxonomy(taxonomy,
				getModelLayerFactory().getMetadataSchemasManager());

		rm = new RMSchemasRecordsServices(zeCollection, getAppLayerFactory());
		rmEventsSearchServices = new RMEventsSearchServices(getModelLayerFactory(), zeCollection);
		searchServices = getModelLayerFactory().newSearchServices();
		UserServices userServices = getModelLayerFactory().newUserServices();
		users.setUp(userServices);
		userServices.addUserToCollection(users.charles(), zeCollection);
		userServices.addUserToCollection(users.alice(), zeCollection);
		recordServices.add(users.aliceIn(zeCollection).setCollectionWriteAccess(true).setCollectionDeleteAccess(true)
				.getWrappedRecord());
		userServices.addUserToCollection(users.bob(), zeCollection);
		users = records.getUsers();
		alice = users.aliceIn(zeCollection);
	}

	@Test
	public void whenSavingNewRecordsThenEventsCreated_run1()
			throws Exception {
		whenSavingNewRecordsThenEventsCreated();
	}

	@Test
	public void whenSavingNewRecordsThenEventsCreated_run2()
			throws Exception {
		whenSavingNewRecordsThenEventsCreated();
	}

	@Test
	public void whenSavingNewRecordsThenEventsCreated_run3()
			throws Exception {
		whenSavingNewRecordsThenEventsCreated();
	}

	@Test
	public void whenSavingNewRecordsThenEventsCreated_run4()
			throws Exception {
		whenSavingNewRecordsThenEventsCreated();
	}

	@Test
	public void whenSavingNewRecordsThenEventsCreated_run5()
			throws Exception {
		whenSavingNewRecordsThenEventsCreated();
	}

	/*//TODO

	@Test
	public void whenGrantPermissionThenEventsCreated()
			throws Exception {
		assertThat(getEventSize()).isEqualTo(0);
		Authorization permission = newAuthorization("MANAGER", Arrays.asList(users.bobIn(zeCollection)), Arrays.asList(records.getFolder_A01().getWrappedRecord()));
		loggingServices.grantPermission(permission, alice);
		recordServices.flush();

	}


	@Test
	public void whenDeletePermissionThenEventsCreated()
			throws Exception {
		assertThat(getEventSize()).isEqualTo(0);
	}

	@Test
	public void whenModifyPermissionThenEventsCreated()
			throws Exception {
		assertThat(getEventSize()).isEqualTo(0);
	}

	@Test
	public void whenUserLoginThenEventCreated()
			throws Exception {
		assertThat(getEventSize()).isEqualTo(0);
	}

	@Test
	public void whenUserLogoutThenEventCreated()
			throws Exception {
		assertThat(getEventSize()).isEqualTo(0);
	}

	@Test
	public void whenBorrowRecordThenEventCreated()
			throws Exception {
		whenBorrowFolderThenEventCreated();
		whenBorrowDocumentThenEventCreated();
		whenBorrowContainerThenEventCreated();
	}

	@Test
	public void whenReturnRecordThenEventCreated()
			throws Exception {
		whenReturnFolderThenEventCreated();
		whenReturnDocumentThenEventCreated();
		whenReturnContainerThenEventCreated();
	}

	@Test
	public void whenviewRecordThenEventCreated()
			throws Exception {
		whenBorrowFolderThenEventCreated();
		whenBorrowDocumentThenEventCreated();
		whenBorrowContainerThenEventCreated();
	}

	@Test
	public void whenAddUserThenEventCreated()
			throws Exception {
		assertThat(getEventSize()).isEqualTo(0);

	}

	@Test
	public void whenRemoveUserThenEventCreated()
			throws Exception {

	}

	@Test
	public void whenAddGroupThenEventCreated()
			throws Exception {
		assertThat(getEventSize()).isEqualTo(0);

	}

	@Test
	public void whenRemoveGroupThenEventCreated()
			throws Exception {
		assertThat(getEventSize()).isEqualTo(0);

	}*/

	//TODO Francis Server integration Build 134
	private void whenSavingNewRecordsThenEventsCreated()
			throws Exception {

		Transaction transaction = new Transaction();
		transaction.add(new TestRecord(zeSchema, "recordAddedWithoutUserForWhichNoEventIsCreated"));
		recordServices.execute(transaction);

		givenTimeIs(shishOClock);
		Record record2 = new TestRecord(zeSchema, "record2").set(Schemas.FOLLOWERS, asList(aliceWonderland));
		transaction = new Transaction().setUser(users.aliceIn(zeCollection));
		transaction.add(record2);
		transaction.add(new TestRecord(zeSchema, "record1"));
		recordServices.execute(transaction);

		givenTimeIs(tockOClock);
		transaction = new Transaction().setUser(users.aliceIn(zeCollection));
		transaction.add(new TestRecord(zeSchema, "record3"));
		transaction.add(record2.set(Schemas.TITLE, "2"));
		recordServices.execute(transaction);

		givenTimeIs(teaOClock);
		recordServices.logicallyDelete(record2, users.aliceIn(zeCollection));
		recordServices.flush();

		List<Event> events = getAllEvents();

		assertThat(events).hasSize(4);
		assertThat(events.get(0).getCreatedOn()).isEqualTo(shishOClock);
		assertThat(events.get(0).getCollection()).isEqualTo(zeCollection);
		assertThat(events.get(0).getUsername()).isEqualTo(aliceWonderland);
		assertThat(events.get(0).getRecordId()).isEqualTo("record1");
		assertThat(events.get(0).getType()).isEqualTo("create_zeSchemaType");

		assertThat(events.get(1).getCreatedOn()).isEqualTo(shishOClock);
		assertThat(events.get(1).getCollection()).isEqualTo(zeCollection);
		assertThat(events.get(1).getUsername()).isEqualTo(aliceWonderland);
		assertThat(events.get(1).getRecordId()).isEqualTo("record2");
		assertThat(events.get(1).getType()).isEqualTo("create_zeSchemaType");

		assertThat(events.get(2).getCreatedOn()).isEqualTo(tockOClock);
		assertThat(events.get(2).getCollection()).isEqualTo(zeCollection);
		assertThat(events.get(2).getUsername()).isEqualTo(aliceWonderland);
		assertThat(events.get(2).getRecordId()).isEqualTo("record2");
		assertThat(events.get(2).getType()).isEqualTo("modify_zeSchemaType");

		assertThat(events.get(3).getCreatedOn()).isEqualTo(tockOClock);
		assertThat(events.get(3).getCollection()).isEqualTo(zeCollection);
		assertThat(events.get(3).getUsername()).isEqualTo(aliceWonderland);
		assertThat(events.get(3).getRecordId()).isEqualTo("record3");
		assertThat(events.get(3).getType()).isEqualTo("create_zeSchemaType");

	}

	private List<Event> getAllEvents() {
		SearchServices searchServices = getModelLayerFactory().newSearchServices();
		SchemasRecordsServices schemas = new SchemasRecordsServices(zeCollection, getModelLayerFactory());

		LogicalSearchCondition condition = LogicalSearchQueryOperators.from(schemas.eventSchema()).returnAll();
		LogicalSearchQuery query = new LogicalSearchQuery(condition);
		query.sortAsc(Schemas.CREATED_ON).sortAsc(Schemas.IDENTIFIER);
		return schemas.wrapEvents(searchServices.search(query));

	}

	@Test
	public void whenGrantPermissionThenLogValidEvents()
			throws Exception {

		Authorization authorization = newAuthorization("MANAGER", Arrays.asList(users.bobIn(zeCollection)),
				Arrays.asList(records.getFolder_A01().getWrappedRecord()));
		User alice = users.aliceIn(zeCollection);
		loggingServices.grantPermission(authorization, alice);
		recordServices.flush();

		LogicalSearchQuery query = new LogicalSearchQuery();
		query.setCondition(
				LogicalSearchQueryOperators.from(rm.eventSchema()).where(
						rm.eventSchema().getMetadata(Event.TYPE)).isEqualTo(EventType.GRANT_PERMISSION_FOLDER));
		List<Record> events = searchServices.search(query);

		assertThat(events).hasSize(1);
		Event event = rm.wrapEvent(events.get(0));
		assertThat(event.getType()).isEqualTo(EventType.GRANT_PERMISSION_FOLDER);
	}

	private Authorization newAuthorization(String role, List<User> users, List<Record> records) {
		List<String> roles = new ArrayList<>();
		String zRole = role;
		roles.add(zRole);
		LocalDate startDate = new LocalDate();
		LocalDate endDate = new LocalDate();
		AuthorizationDetails detail = new AuthorizationDetails(zeCollection, "42", roles, startDate, endDate, false);
		List<String> grantedToPrincipals = new ArrayList<>();
		for (User user : users) {
			grantedToPrincipals.add(user.getId());
		}
		List<String> grantedOnRecords = new ArrayList<>();
		for (Record record : records) {
			grantedOnRecords.add(record.getId());
		}
		return new Authorization(detail, grantedToPrincipals, grantedOnRecords);
	}

	@Test
	public void whenDeletePermissionThenReturnValidEvents()
			throws Exception {

	}

	@Test
	public void whenDeleteFolderThenReturnValidEvents()
			throws Exception {
		getDataLayerFactory().getDataLayerLogger().monitor("00000000279");
		Folder folder_A01 = records.getFolder_A01();
		User adminUser = users.adminIn(zeCollection);
		loggingServices.logDeleteRecordWithJustification(folder_A01.getWrappedRecord(), adminUser, "");

		recordServices.flush();

		LogicalSearchQuery query = new LogicalSearchQuery();
		query.setCondition(
				LogicalSearchQueryOperators.from(rm.eventSchema()).where(
						rm.eventSchema().getMetadata(Event.TYPE)).isEqualTo(EventType.DELETE_FOLDER));
		SearchServices searchServices = getModelLayerFactory().newSearchServices();
		List<Record> folders = searchServices.search(query);
		assertThat(folders.size()).isEqualTo(1);
		Event event = rm.wrapEvent(folders.get(0));
		assertThat(event.getType()).isEqualTo(EventType.DELETE_FOLDER);
		//assertThat(event.getEventPrincipalPath()).isEqualTo(folder.getWrappedRecord().get(Schemas.PRINCIPAL_PATH));
	}

	//TODO Nouha
	//@Test
	public void whenLoggingPermissionModificationThenReturnValidEvents()
			throws Exception {
		//List<Role> roles = getRolesWithReadPermissions();
		List<String> roles = new ArrayList<>();
		String zRole = "MANAGER";
		roles.add(zRole);
		LocalDate startDate = new LocalDate();
		LocalDate endDate = new LocalDate();
		AuthorizationDetails detail = new AuthorizationDetails(zeCollection, "42", roles, startDate, endDate, false);
		List<String> grantedToPrincipals = new ArrayList<>();
		User alice = users.aliceIn(zeCollection);
		User bob = users.bobIn(zeCollection);
		grantedToPrincipals.add(alice.getId());
		grantedToPrincipals.add(bob.getId());
		List<String> grantedOnRecords = new ArrayList<>();
		/*AdministrativeUnit administrativeUnit = records.getUnit10();
		Folder folder = createFolder(administrativeUnit);*/
		grantedOnRecords.addAll(Arrays.asList(new String[] { records.getFolder_A01().getId() }));
		Authorization authorization = new Authorization(detail, grantedToPrincipals, grantedOnRecords);

		List<String> grantedOnRecordsBefore = new ArrayList<>();
		grantedOnRecordsBefore.addAll(
				Arrays.asList(new String[] { records.getFolder_A01().getId(), records.getFolder_A02().getId() }));
		AuthorizationDetails detailBefore = new AuthorizationDetails(zeCollection, "43", roles, startDate, endDate.minusDays(1),
				false);
		Authorization authorizationBefore = new Authorization(detailBefore, grantedToPrincipals, grantedOnRecordsBefore);

		loggingServices.modifyPermission(authorization, authorizationBefore, alice);
		recordServices.flush();

		LogicalSearchQuery query = new LogicalSearchQuery();
		query.setCondition(
				LogicalSearchQueryOperators.from(rm.eventSchema()).where(
						rm.eventSchema().getMetadata(Event.TYPE)).isEqualTo(EventType.MODIFY_PERMISSION_FOLDER));
		SearchServices searchServices = getModelLayerFactory().newSearchServices();
		List<Record> events = searchServices.search(query);

		assertThat(events).hasSize(1);
		Event event = rm.wrapEvent(events.get(0));
		assertThat(event.getDelta().contains("-[" + records.getFolder_A02().getId() + "]"));
		assertThat(event.getDelta().contains("[" + startDate + ", " + endDate.minusDays(1) + "]"));
		assertThat(event.getUsername()).isEqualTo(alice.getUsername());
		if (alice.getAllRoles() != null) {
			assertThat(event.getUserRoles()).isNull();
		} else {
			assertThat(event.getUserRoles()).isEqualTo(StringUtils.join(alice.getAllRoles().toArray(), "; "));
		}

		assertThat(event.getPermissionUsers()).isEqualTo("Alice Wonderland; Bob 'Elvis' Gratton");
		assertThat(event.getPermissionDateRange()).isEqualTo("[" + startDate + ", " + endDate + "]");
		assertThat(event.getPermissionRoles()).isEqualTo(zRole);
		assertThat(event.getRecordId()).isEqualTo(records.getFolder_A01().getId());
		assertThat(event.getEventPrincipalPath())
				.isEqualTo(records.getFolder_A01().getWrappedRecord().get(Schemas.PRINCIPAL_PATH));
		assertThat(event.getType()).isEqualTo(EventType.MODIFY_PERMISSION_FOLDER);

	}

	@Test
	public void whenCreateFolderThenCreateValidEvent()
			throws Exception {

		getDataLayerFactory().getDataLayerLogger().monitor("00000000279");

		Folder folder = rm.newFolder().setTitle("Ze Folder").setRetentionRuleEntered(records.ruleId_1)
				.setAdministrativeUnitEntered(records.unitId_10a)
				.setCategoryEntered(records.categoryId_X110).setOpenDate(new LocalDate(2010, 4, 4));
		User alice = users.aliceIn(zeCollection);

		recordServices.add(folder.getWrappedRecord(), alice);

		recordServices.flush();

		LogicalSearchQuery query = new LogicalSearchQuery();
		query.setCondition(
				LogicalSearchQueryOperators.from(rm.eventSchema()).where(
						rm.eventSchema().getMetadata(Event.TYPE)).isEqualTo(EventType.CREATE_FOLDER));
		SearchServices searchServices = getModelLayerFactory().newSearchServices();
		List<Record> folders = searchServices.search(query);
		assertThat(folders.size()).isEqualTo(1);
		Event event = rm.wrapEvent(folders.get(0));
		assertThat(event.getType()).isEqualTo(EventType.CREATE_FOLDER);
		assertThat(event.getEventPrincipalPath()).isEqualTo(folder.getWrappedRecord().get(Schemas.PRINCIPAL_PATH));
	}

	@Test
	public void whenModifyFolderThenCreateValidEvent()
			throws Exception {

		Folder folder = rm.newFolder().setRetentionRuleEntered(records.ruleId_1)
				.setAdministrativeUnitEntered(records.unitId_10a).setCategoryEntered(records.categoryId_X110).setTitle("titre1")
				.setOpenDate(new LocalDate(2010, 1, 1));
		User alice = users.aliceIn(zeCollection);

		recordServices.add(folder.getWrappedRecord(), alice);

		recordServices.flush();

		Transaction transaction = new Transaction(folder.getWrappedRecord());
		transaction.setUser(alice);
		folder.setTitle("titre2");
		loggingServices.logTransaction(transaction);
		recordServices.flush();

		LogicalSearchQuery query = new LogicalSearchQuery();
		query.setCondition(
				LogicalSearchQueryOperators.from(rm.eventSchema()).where(
						rm.eventSchema().getMetadata(Event.TYPE)).isEqualTo(EventType.MODIFY_FOLDER));
		SearchServices searchServices = getModelLayerFactory().newSearchServices();
		List<Record> folders = searchServices.search(query);
		assertThat(folders.size()).isEqualTo(1);
		Event event = rm.wrapEvent(folders.get(0));
		assertThat(event.getType()).isEqualTo(EventType.MODIFY_FOLDER);
		assertThat(event.getEventPrincipalPath()).isEqualTo(folder.getWrappedRecord().get(Schemas.PRINCIPAL_PATH));
		String expectedDelta = "[ folder_default_title :\n" +
				"\tAvant : titre1\n" +
				"\tAprès : titre2]\n";
		assertThat(event.getDelta()).isEqualTo(expectedDelta);
	}

	@Test
	public void whenUserLoginThenCreateValidEvent()
			throws Exception {
		User alice = users.aliceIn(zeCollection);
		loggingServices.login(alice);
		recordServices.flush();
		LogicalSearchQuery query = new LogicalSearchQuery();
		query.setCondition(
				LogicalSearchQueryOperators.from(rm.eventSchema()).where(
						rm.eventSchema().getMetadata(Event.TYPE)).isEqualTo(EventType.OPEN_SESSION));
		SearchServices searchServices = getModelLayerFactory().newSearchServices();
		List<Record> folders = searchServices.search(query);
		assertThat(folders.size()).isEqualTo(1);
		Event event = rm.wrapEvent(folders.get(0));
		assertThat(event.getType()).isEqualTo(EventType.OPEN_SESSION);
		assertThat(event.getUsername()).isEqualTo(alice.getUsername());
	}

	@Test
	public void whenBorrowOrReturnThenCreateValidEvent()
			throws Exception {
		Folder folderA02 = records.getFolder_A02();
		Folder folderBorrowedByDakota = records.getFolder_A03();
		User alice = users.aliceIn(zeCollection);
		loggingServices.borrowRecord(folderA02.getWrappedRecord(), alice);
		loggingServices.returnRecord(folderA02.getWrappedRecord(), alice);
		User charles = users.charlesIn(zeCollection);
		loggingServices.borrowRecord(folderBorrowedByDakota.getWrappedRecord(), charles);
		recordServices.flush();

		LogicalSearchQuery query = new LogicalSearchQuery();
		query.setCondition(
				LogicalSearchQueryOperators.from(rm.eventSchema()).where(
						rm.eventSchema().getMetadata(Event.TYPE)).isEqualTo(EventType.BORROW_FOLDER));
		SearchServices searchServices = getModelLayerFactory().newSearchServices();
		long borrowEventsCount = searchServices.getResultsCount(query);

		assertThat(borrowEventsCount).isEqualTo(2l);
		query = new LogicalSearchQuery();
		query.setCondition(
				LogicalSearchQueryOperators.from(rm.eventSchema()).where(
						rm.eventSchema().getMetadata(Event.TYPE)).isEqualTo(EventType.RETURN_FOLDER));
		List<Record> events = searchServices.search(query);

		assertThat(events).hasSize(1);
		Event event = rm.wrapEvent(events.get(0));
		event.getUsername().contains(alice.getUsername());
	}

	private int getEventSize() {
		return getAllEvents().size();
	}

}
