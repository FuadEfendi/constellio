package com.constellio.app.api.cmis.accept;

import static com.constellio.model.entities.security.Role.READ;
import static com.constellio.model.entities.security.Role.WRITE;
import static com.constellio.model.entities.security.global.UserCredentialStatus.ACTIVE;
import static java.util.Arrays.asList;
import static org.apache.chemistry.opencmis.commons.enums.AclPropagation.REPOSITORYDETERMINED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.enums.Action;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.assertj.core.api.ListAssert;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.constellio.app.api.cmis.accept.CmisAcceptanceTestSetup.Records;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.security.Authorization;
import com.constellio.model.entities.security.global.AuthorizationBuilder;
import com.constellio.model.entities.security.global.UserCredential;
import com.constellio.model.services.migrations.ConstellioEIMConfigs;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.schemas.MetadataSchemaTypesAlteration;
import com.constellio.model.services.schemas.MetadataSchemasManager;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypesBuilder;
import com.constellio.model.services.security.AuthorizationsServices;
import com.constellio.model.services.taxonomies.TaxonomiesManager;
import com.constellio.model.services.taxonomies.TaxonomiesSearchServices;
import com.constellio.model.services.users.UserServices;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.annotations.DriverTest;
import com.constellio.sdk.tests.annotations.SlowTest;
import com.constellio.sdk.tests.setups.Users;

@DriverTest
public class CmisACLAcceptanceTest extends ConstellioTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(CmisACLAcceptanceTest.class);

	UserServices userServices;
	TaxonomiesManager taxonomiesManager;
	MetadataSchemasManager metadataSchemasManager;
	RecordServices recordServices;
	Users users = new Users();
	CmisAcceptanceTestSetup zeCollectionSchemas = new CmisAcceptanceTestSetup(zeCollection);
	Records zeCollectionRecords;
	TaxonomiesSearchServices taxonomiesSearchServices;

	List<String> R = asList("cmis:read");
	List<String> RW = asList("cmis:read", "cmis:write");
	List<String> constellio_R = asList(READ);
	List<String> constellio_RW = asList(READ, WRITE);

	//	Session cmisSession;
	Session session;
	String adminToken;

	AuthorizationsServices authorizationsServices;

	String aliceId, bobId, charlesId, dakotaId, edouardId, chuckId, gandalfId, robinId, heroesId;

	@Before
	public void setUp()
			throws Exception {

		userServices = getModelLayerFactory().newUserServices();
		taxonomiesManager = getModelLayerFactory().getTaxonomiesManager();
		metadataSchemasManager = getModelLayerFactory().getMetadataSchemasManager();
		recordServices = getModelLayerFactory().newRecordServices();

		taxonomiesSearchServices = getModelLayerFactory().newTaxonomiesSearchService();

		users.setUp(userServices);

		defineSchemasManager().using(zeCollectionSchemas);
		taxonomiesManager.addTaxonomy(zeCollectionSchemas.getTaxonomy2(), metadataSchemasManager);
		taxonomiesManager.setPrincipalTaxonomy(zeCollectionSchemas.getTaxonomy2(), metadataSchemasManager);
		getModelLayerFactory().getMetadataSchemasManager().modify(zeCollection, new MetadataSchemaTypesAlteration() {
			@Override
			public void alter(MetadataSchemaTypesBuilder types) {
				types.getSchemaType(zeCollectionSchemas.administrativeUnit.type().getCode()).setSecurity(true);
				types.getSchemaType(zeCollectionSchemas.classificationStation.type().getCode()).setSecurity(true);
				types.getSchemaType(zeCollectionSchemas.documentFond.type().getCode()).setSecurity(false);
				types.getSchemaType(zeCollectionSchemas.category.type().getCode()).setSecurity(false);
				types.getSchemaType(zeCollectionSchemas.folderSchema.type().getCode()).setSecurity(true);
				types.getSchemaType(zeCollectionSchemas.documentSchema.type().getCode()).setSecurity(true);
			}
		});
		zeCollectionRecords = zeCollectionSchemas.givenRecords(recordServices);

		userServices.addUserToCollection(users.alice(), zeCollection);
		userServices.addUserToCollection(users.bob(), zeCollection);
		userServices.addUserToCollection(users.charles(), zeCollection);
		userServices.addUserToCollection(users.dakotaLIndien(), zeCollection);
		userServices.addUserToCollection(users.edouardLechat(), zeCollection);
		userServices.addUserToCollection(users.gandalfLeblanc(), zeCollection);
		userServices.addUserToCollection(users.chuckNorris(), zeCollection);
		userServices.addUserToCollection(users.sasquatch(), zeCollection);
		userServices.addUserToCollection(users.robin(), zeCollection);

		userServices.addUserToCollection(users.admin(), zeCollection);
		userServices.addUserToCollection(users.chuckNorris(), zeCollection);

		recordServices.update(users.adminIn(zeCollection).setCollectionReadAccess(true).setCollectionWriteAccess(true));
		recordServices.update(users.chuckNorrisIn(zeCollection).setCollectionReadAccess(true));

		userServices.addUpdateUserCredential(users.admin().withServiceKey("admin-key"));
		getModelLayerFactory().newAuthenticationService().changePassword(admin, "1qaz2wsx");
		adminToken = userServices.generateToken(admin);
		authorizationsServices = getModelLayerFactory().newAuthorizationsServices();

		aliceId = users.aliceIn(zeCollection).getId();
		bobId = users.bobIn(zeCollection).getId();
		charlesId = users.charlesIn(zeCollection).getId();
		dakotaId = users.dakotaIn(zeCollection).getId();
		edouardId = users.edouardIn(zeCollection).getId();
		gandalfId = users.gandalfIn(zeCollection).getId();
		chuckId = users.chuckNorrisIn(zeCollection).getId();
		heroesId = users.heroesIn(zeCollection).getId();
		robinId = users.robinIn(zeCollection).getId();

		givenConfig(ConstellioEIMConfigs.CMIS_NEVER_RETURN_ACL, false);
	}

	@Test
	public void applyACLOnlyEnabledOnRecordClassifiedInPrincipalTaxonomy()
			throws Exception {
		session = givenAdminSessionOnZeCollection();

		Folder cmisFolder2 = cmisFolder(zeCollectionRecords.folder2);
		assertThat(cmisFolder2.getAllowableActions().getAllowableActions()).contains(Action.CAN_APPLY_ACL, Action.CAN_GET_ACL);
		cmisFolder2.addAcl(asList(ace("heroes", R), ace(bobGratton, RW), ace(gandalf, RW)), REPOSITORYDETERMINED);

		//Can configure acl on principal taxonomy
		Folder cmisPrincipalTaxoRecord = cmisFolder(zeCollectionRecords.taxo2_unit1);
		assertThat(cmisPrincipalTaxoRecord.getAllowableActions().getAllowableActions())
				.contains(Action.CAN_APPLY_ACL, Action.CAN_GET_ACL);
		cmisPrincipalTaxoRecord.addAcl(asList(ace("heroes", R), ace(bobGratton, RW), ace(gandalf, RW)), REPOSITORYDETERMINED);

		//Can configure acl on secondary taxonomy
		Folder cmisSecondaryTaxoRecord = cmisFolder(zeCollectionRecords.taxo1_category2_1);
		assertThat(cmisSecondaryTaxoRecord.getAllowableActions().getAllowableActions())
				.doesNotContain(Action.CAN_APPLY_ACL, Action.CAN_GET_ACL);
		try {
			cmisSecondaryTaxoRecord.addAcl(asList(ace("heroes", R), ace(bobGratton, RW), ace(gandalf, RW)), REPOSITORYDETERMINED);
			fail("Exception expected");
		} catch (Exception e) {
			//OK
		}

	}

	@Test
	public void whenAddACLTo100UsersThenOk()
			throws Exception {
		session = givenAdminSessionOnZeCollection();

		List<String> users = createDummyUsers(100);

		Folder cmisFolder2 = cmisFolder(zeCollectionRecords.folder2);
		assertThat(cmisFolder2.getAllowableActions().getAllowableActions()).contains(Action.CAN_APPLY_ACL, Action.CAN_GET_ACL);
		List<Ace> aces = new ArrayList<>();
		for (String user : users) {
			aces.add(ace(user, R));
		}
		cmisFolder2.addAcl(aces, REPOSITORYDETERMINED);

		recordServices.refresh(zeCollectionRecords.folder2);

		for (String username : users) {
			User user = userServices.getUserInCollection(username, zeCollection);
			assertThat(user.hasReadAccess().on(zeCollectionRecords.folder2)).isTrue();
		}

	}

	@Test
	public void whenAddAceWith()
			throws Exception {
		session = givenAdminSessionOnZeCollection();

		List<String> users = createDummyUsers(5);
		Folder aFolder = cmisFolder(zeCollectionRecords.folder2);

		assertThat(userServices.getUserInCollection("bob", zeCollection).hasWriteAccess().on(zeCollectionRecords.folder2))
				.isFalse();
		assertThat(userServices.getUserInCollection("alice", zeCollection).hasWriteAccess().on(zeCollectionRecords.folder2))
				.isFalse();

		List<Ace> aces = new ArrayList<>();
		aces.add(ace("constellio:removeInheritance", RW));
		for (String user : users) {
			aces.add(ace(user, RW));
		}
		aces.add(ace("bob", RW));
		aces.add(ace("alice", RW));

		aFolder.setAcl(aces);

		Map<String, Object> subFolderParameters = new HashMap<>();
		subFolderParameters.put("cmis:name", "Sub folder ");
		subFolderParameters.put(PropertyIds.OBJECT_TYPE_ID, "folder_default");
		Folder subFolder = aFolder.createFolder(subFolderParameters);

		subFolder.setAcl(aces);

		Record subFolderRecord = recordServices.getDocumentById(subFolder.getId());
		for (String user : users) {
			assertThat(userServices.getUserInCollection(user, zeCollection).hasWriteAccess().on(subFolderRecord)).isTrue();
		}
		assertThat(userServices.getUserInCollection("bob", zeCollection).hasWriteAccess().on(subFolderRecord)).isTrue();
		assertThat(userServices.getUserInCollection("alice", zeCollection).hasWriteAccess().on(subFolderRecord)).isTrue();
	}

	@Test
	@SlowTest
	public void whenCreateHierarchyOfFoldersAndAddACLTo35UsersThenOK()
			throws Exception {
		session = givenAdminSessionOnZeCollection();

		List<String> users = createDummyUsers(35);

		Folder cmisFolder2 = cmisFolder(zeCollectionRecords.folder2);

		List<Ace> aces = new ArrayList<>();
		aces.add(ace("constellio:removeInheritance", RW));
		for (String user : users) {
			aces.add(ace(user, RW));
		}

		cmisFolder2.setAcl(aces);
		List<String> ids = new ArrayList<>();

		Map<String, List<String>> usersWithAccess = new HashMap<>();
		int counter = 0;

		for (int i = 0; i < 4; i++) {
			counter++;
			Map<String, Object> subFolderParameters = new HashMap<>();
			subFolderParameters.put("cmis:name", "Sub folder " + counter);
			subFolderParameters.put(PropertyIds.OBJECT_TYPE_ID, "folder_default");
			Folder subFolder = cmisFolder2.createFolder(subFolderParameters);
			List<Ace> subFolderAces = new ArrayList<>(aces);
			subFolderAces.remove(counter);
			subFolder.setAcl(subFolderAces);
			usersWithAccess.put(subFolder.getId(), usersWithAccess(subFolderAces));

			for (int j = 0; j < 5; j++) {
				counter++;
				Map<String, Object> subSubFolderParameters = new HashMap<>();
				subSubFolderParameters.put("cmis:name", "Sub folder " + counter);
				subSubFolderParameters.put(PropertyIds.OBJECT_TYPE_ID, "folder_default");
				Folder subSubFolder = cmisFolder2.createFolder(subSubFolderParameters);
				List<Ace> subSubFolderAces = new ArrayList<>(aces);
				subSubFolderAces.remove(counter);
				subSubFolder.setAcl(subSubFolderAces);
				usersWithAccess.put(subSubFolder.getId(), usersWithAccess(subSubFolderAces));
			}
		}

		for (Map.Entry<String, List<String>> entry : usersWithAccess.entrySet()) {
			Record record = recordServices.getDocumentById(entry.getKey());

			for (String username : users) {
				User user = userServices.getUserInCollection(username, zeCollection);

				if (entry.getValue().contains(username)) {
					assertThat(user.hasReadAccess().on(record))
							.describedAs(user.getUsername() + " access on record " + entry.getKey()).isTrue();
				} else {
					assertThat(user.hasReadAccess().on(record))
							.describedAs(user.getUsername() + " access on record " + entry.getKey()).isFalse();
				}
			}

		}
	}

	private List<String> usersWithAccess(List<Ace> aces) {
		List<String> users = new ArrayList<>();

		for (Ace ace : aces) {
			if (!"constellio:removeInheritance".equals(ace.getPrincipalId())) {
				users.add(ace.getPrincipalId());
			}
		}

		return users;
	}

	private List<String> createDummyUsers(int qty) {
		List<String> users = new ArrayList<>();
		for (int i = 1; i <= qty; i++) {
			String username = "grim.patron." + i;
			UserCredential userCredential = userServices.createUserCredential(username, "Grim", "Patron",
					username + "@constellio.com", new ArrayList<String>(), asList(zeCollection), ACTIVE);
			userServices.addUpdateUserCredential(userCredential);
			users.add(username);
		}
		for (String username : users) {
			User user = userServices.getUserInCollection(username, zeCollection);
			assertThat(user.hasReadAccess().on(zeCollectionRecords.folder2)).isFalse();
		}
		return users;
	}

	@Test
	public void whenSetInvalidUserOrGroupInACLThenNoModificationsAndHelpfulMessage()
			throws Exception {
		session = givenAdminSessionOnZeCollection();

		assertThat(session.getBinding().getAclService()).isNotNull();

		givenFolderInheritingTaxonomyAuthorizations();
		assertThatRecord(zeCollectionRecords.folder2).onlyUserWithReadPermissionAre(chuckNorris, admin, edouard);
		assertThatRecord(zeCollectionRecords.folder2_1).onlyUserWithWritePermissionAre(admin, edouard);

		session.getDefaultContext().setIncludeAcls(true);

		Folder cmisFolder2 = cmisFolder(zeCollectionRecords.folder2);
		assertThat(cmisFolder2.getAcl().getAces()).extracting("direct", "permissions", "principalId")
				.containsOnly(tuple(false, RW, "edouard"));

		//Add two ACE

		try {

			cmisFolder2.addAcl(asList(ace("heroes", R), ace(bobGratton, RW), ace(gandalf, RW), ace("roger", RW)),
					REPOSITORYDETERMINED);
			fail("exception expected");
		} catch (CmisRuntimeException e) {
			assertThat(e.getMessage()).isEqualTo(
					"An ace has invalid principal : No such user with username or group with code : 'roger'");
		}
		System.out.println("----------");
		try {

			cmisFolder2.addAcl(asList(ace("heroes", R), ace(bobGratton, RW), ace(gandalf, RW), ace(dakota, asList("READ"))),
					REPOSITORYDETERMINED);
			fail("exception expected");
		} catch (CmisRuntimeException e) {
			assertThat(e.getMessage())
					.isEqualTo("An ace has unsupported permission 'READ', only cmis:read/cmis:write are allowed");
		}
		System.out.println("----------");
		//		try {
		//
		//			cmisFolder2
		//					.addAcl(asList(ace("heroes", R), ace(bobGratton, RW), ace(gandalf, RW), ace(dakota, new ArrayList<String>())),
		//							REPOSITORYDETERMINED);
		//			fail("exception expected");
		//		} catch (CmisRuntimeException e) {
		//			assertThat(e.getMessage()).isEqualTo("An ace has no permission");
		//		}
		System.out.println("----------");
		try {

			cmisFolder2
					.addAcl(asList(ace("heroes", R), ace(bobGratton, RW), ace(gandalf, RW), ace("", RW)),
							REPOSITORYDETERMINED);
			fail("exception expected");
		} catch (CmisRuntimeException e) {
			assertThat(e.getMessage()).isEqualTo("An ace has no specified principal");
		}

		waitForBatchProcess();

		assertThat(cmisFolder2.getAcl().getAces()).extracting("direct", "permissions", "principalId")
				.containsOnly(tuple(false, RW, "edouard"));
	}

	@Test
	public void givenAclDisabledThenNotReturned()
			throws Exception {
		session = givenAdminSessionOnZeCollection();
		givenFolderInheritingTaxonomyAuthorizations();

		givenConfig(ConstellioEIMConfigs.CMIS_NEVER_RETURN_ACL, false);
		session.getDefaultContext().setIncludeAcls(false);
		assertThat(cmisFolder(zeCollectionRecords.folder2).getAcl()).isNull();

		givenConfig(ConstellioEIMConfigs.CMIS_NEVER_RETURN_ACL, false);
		session.getDefaultContext().setIncludeAcls(true);
		assertThat(cmisFolder(zeCollectionRecords.folder2).getAcl()).isNotNull();

		givenConfig(ConstellioEIMConfigs.CMIS_NEVER_RETURN_ACL, true);
		session.getDefaultContext().setIncludeAcls(false);
		assertThat(cmisFolder(zeCollectionRecords.folder2).getAcl()).isNull();

		givenConfig(ConstellioEIMConfigs.CMIS_NEVER_RETURN_ACL, true);
		session.getDefaultContext().setIncludeAcls(true);
		assertThat(cmisFolder(zeCollectionRecords.folder2).getAcl()).isNull();
	}

	@Test
	public void whenSetACLThenAuthorizationsCreated()
			throws Exception {
		session = givenAdminSessionOnZeCollection();

		assertThat(session.getBinding().getAclService()).isNotNull();

		givenFolderInheritingTaxonomyAuthorizations();
		assertThatRecord(zeCollectionRecords.folder2).onlyUserWithReadPermissionAre(chuckNorris, admin, edouard);
		assertThatRecord(zeCollectionRecords.folder2_1).onlyUserWithWritePermissionAre(admin, edouard);

		session.getDefaultContext().setIncludeAcls(true);

		Folder cmisFolder2 = cmisFolder(zeCollectionRecords.folder2);
		assertThat(cmisFolder2.getAcl().getAces()).extracting("direct", "permissions", "principalId")
				.containsOnly(tuple(false, RW, "edouard"));

		//Add two ACE
		cmisFolder2.addAcl(asList(ace("heroes", R), ace(bobGratton, RW), ace(gandalf, RW)), REPOSITORYDETERMINED);
		waitForBatchProcess();

		assertThatRecord(zeCollectionRecords.folder2)
				.onlyUserWithReadPermissionAre(chuckNorris, admin, gandalf, charles, dakota, bobGratton, edouard, robin)
				.onlyUserWithWritePermissionAre(admin, bobGratton, edouard, gandalf);

		assertThatRecordAuthorizations(zeCollectionRecords.folder2).containsOnly(
				tuple(constellio_RW, asList(edouardId), asList(zeCollectionRecords.taxo2_station2_1.getId())),
				tuple(constellio_R, asList(heroesId), asList(zeCollectionRecords.folder2.getId())),
				tuple(constellio_RW, asList(bobId, gandalfId), asList(zeCollectionRecords.folder2.getId()))
		);

		assertThatAcesOf(zeCollectionRecords.folder2).containsOnly(
				tuple(false, RW, edouard), tuple(true, R, "heroes"), tuple(true, RW, bobGratton), tuple(true, RW, gandalf));

		//Add the same bob ACE and a RW auth for heroes
		cmisFolder2.addAcl(asList(ace("heroes", RW), ace(bobGratton, RW)), REPOSITORYDETERMINED);
		waitForBatchProcess();

		assertThatRecord(zeCollectionRecords.folder2)
				.onlyUserWithReadPermissionAre(chuckNorris, admin, gandalf, charles, dakota, bobGratton, edouard, robin)
				.onlyUserWithWritePermissionAre(admin, bobGratton, edouard, gandalf, dakota, robin, charles);

		assertThatRecordAuthorizations(zeCollectionRecords.folder2).containsOnly(
				tuple(constellio_RW, asList(edouardId), asList(zeCollectionRecords.taxo2_station2_1.getId())),
				tuple(constellio_RW, asList(bobId, gandalfId, heroesId), asList(zeCollectionRecords.folder2.getId()))
		);

		assertThatAcesOf(zeCollectionRecords.folder2).containsOnly(
				tuple(false, RW, edouard), tuple(true, RW, "heroes"), tuple(true, RW, bobGratton),
				tuple(true, RW, gandalf));

		cmisFolder2.removeAcl(asList(ace("heroes", RW), ace(bobGratton, RW)), REPOSITORYDETERMINED);

		assertThatRecordAuthorizations(zeCollectionRecords.folder2).containsOnly(
				tuple(constellio_RW, asList(edouardId), asList(zeCollectionRecords.taxo2_station2_1.getId())),
				tuple(constellio_RW, asList(gandalfId), asList(zeCollectionRecords.folder2.getId()))
		);

		assertThatAcesOf(zeCollectionRecords.folder2).containsOnly(
				tuple(false, RW, edouard), tuple(true, RW, gandalf));

		cmisFolder2
				.setAcl(asList(ace("constellio:removeInheritance", new ArrayList<String>()), ace(gandalf, RW),
						ace(dakota, RW)));

		assertThatRecordAuthorizations(zeCollectionRecords.folder2).containsOnly(
				tuple(constellio_RW, asList(gandalfId, dakotaId), asList(zeCollectionRecords.folder2.getId()))
		);

		assertThatAcesOf(zeCollectionRecords.folder2).containsOnly(
				tuple(true, RW, gandalf), tuple(true, RW, dakota));

		assertThatAcesOf(zeCollectionRecords.folder2_1).containsOnly(
				tuple(false, RW, gandalf), tuple(false, RW, dakota));

		cmisFolder2
				.applyAcl(asList(ace(aliceWonderland, RW), ace(charles, RW)), asList(ace(gandalf, RW)), REPOSITORYDETERMINED);

		assertThatRecordAuthorizations(zeCollectionRecords.folder2).containsOnly(
				tuple(constellio_RW, asList(charlesId, dakotaId, aliceId), asList(zeCollectionRecords.folder2.getId()))
		);

		assertThatAcesOf(zeCollectionRecords.folder2).containsOnly(
				tuple(true, RW, aliceWonderland), tuple(true, RW, charles), tuple(true, RW, dakota));

	}

	private ListAssert<Tuple> assertThatAcesOf(Record record) {
		return assertThat(cmisFolder(record).getAcl().getAces()).extracting("direct", "permissions", "principalId");
	}

	private ListAssert<Tuple> assertThatRecordAuthorizations(Record record) {
		recordServices.refresh(record);

		return assertThat(authorizationsServices.getRecordAuthorizations(record))
				.extracting("detail.roles", "grantedToPrincipals", "grantedOnRecords");
	}

	private Ace ace(String principal, List<String> permissions) {
		return session.getObjectFactory().createAce(principal, permissions);
	}

	private Folder cmisFolder(Record record) {
		return (Folder) session.getObject(record.getId());
	}

	private void givenFolderInheritingTaxonomyAuthorizations() {
		Authorization authorization = new AuthorizationBuilder(zeCollection).forUsers(users.edouardIn(zeCollection))
				.on(zeCollectionRecords.taxo2_station2_1).givingReadWriteAccess();
		getModelLayerFactory().newAuthorizationsServices().add(authorization, users.adminIn(zeCollection));
		try {
			waitForBatchProcess();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private FolderRecordValidator assertThatRecord(Record folder) {
		return new FolderRecordValidator(folder.getId());
	}

	private class FolderRecordValidator {

		private String folderId;

		public FolderRecordValidator(String folderId) {
			this.folderId = folderId;
		}

		private FolderRecordValidator onlyUserWithReadPermissionAre(String... users) {

			List<String> userWithReadAccess = new ArrayList<>();
			UserServices userServices = getModelLayerFactory().newUserServices();
			Record record = getModelLayerFactory().newRecordServices().getDocumentById(folderId);
			for (User user : userServices.getAllUsersInCollection(zeCollection)) {
				if (user.hasReadAccess().on(record)) {
					userWithReadAccess.add(user.getUsername());
				}
			}
			assertThat(userWithReadAccess).describedAs("users with read access").containsOnly(users);
			return this;
		}

		private FolderRecordValidator onlyUserWithWritePermissionAre(String... users) {

			List<String> userWithWriteAccess = new ArrayList<>();
			UserServices userServices = getModelLayerFactory().newUserServices();
			Record record = getModelLayerFactory().newRecordServices().getDocumentById(folderId);
			for (User user : userServices.getAllUsersInCollection(zeCollection)) {
				if (user.hasWriteAccess().on(record)) {
					userWithWriteAccess.add(user.getUsername());
				}
			}
			assertThat(userWithWriteAccess).describedAs("users with write access").containsOnly(users);
			return this;
		}

		private FolderRecordValidator hasAutorizationsCount(int expectedCount) {

			List<String> userWithWriteAccess = new ArrayList<>();
			UserServices userServices = getModelLayerFactory().newUserServices();
			Record record = getModelLayerFactory().newRecordServices().getDocumentById(folderId);
			List<Authorization> authorizations = authorizationsServices.getRecordAuthorizations(record);
			assertThat(authorizations).describedAs("authorizations").hasSize(expectedCount);
			return this;
		}

	}

	private Session givenAdminSessionOnZeCollection() {
		return newCmisSessionBuilder().authenticatedBy("admin-key", adminToken).onCollection(zeCollection).build();
	}

}
