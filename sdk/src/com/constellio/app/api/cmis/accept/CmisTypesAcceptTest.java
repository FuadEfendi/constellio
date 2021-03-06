package com.constellio.app.api.cmis.accept;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;

import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.enums.Cardinality;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.junit.Before;
import org.junit.Test;

import com.constellio.app.api.cmis.accept.CmisAcceptanceTestSetup.Records;
import com.constellio.model.entities.schemas.MetadataValueType;
import com.constellio.model.services.records.RecordServices;
import com.constellio.model.services.schemas.MetadataSchemasManager;
import com.constellio.model.services.schemas.MetadataSchemasManagerException.OptimisticLocking;
import com.constellio.model.services.schemas.builders.MetadataSchemaTypesBuilder;
import com.constellio.model.services.security.authentification.AuthenticationService;
import com.constellio.model.services.taxonomies.TaxonomiesManager;
import com.constellio.model.services.taxonomies.TaxonomiesSearchServices;
import com.constellio.model.services.users.UserServices;
import com.constellio.sdk.tests.ConstellioTest;
import com.constellio.sdk.tests.annotations.DriverTest;
import com.constellio.sdk.tests.setups.Users;

@DriverTest
public class CmisTypesAcceptTest extends ConstellioTest {

	String anotherCollection = "anotherCollection";
	CmisAcceptanceTestSetup anotherCollectionSchemas = new CmisAcceptanceTestSetup(anotherCollection);
	UserServices userServices;
	TaxonomiesManager taxonomiesManager;
	MetadataSchemasManager metadataSchemasManager;
	RecordServices recordServices;
	Users users = new Users();
	CmisAcceptanceTestSetup zeCollectionSchemas = new CmisAcceptanceTestSetup(zeCollection);
	Records zeCollectionRecords;
	Records anotherCollectionRecords;
	TaxonomiesSearchServices taxonomiesSearchServices;
	AuthenticationService authenticationService;

	Session cmisSession;

	String bobKey = "bob-key";
	String chuckNorrisKey = "chuckNorris-key";
	String bobToken, chuckNorrisToken;

	@Before
	public void setUp()
			throws Exception {

		authenticationService = getModelLayerFactory().newAuthenticationService();
		userServices = getModelLayerFactory().newUserServices();
		taxonomiesManager = getModelLayerFactory().getTaxonomiesManager();
		metadataSchemasManager = getModelLayerFactory().getMetadataSchemasManager();
		recordServices = getModelLayerFactory().newRecordServices();

		taxonomiesSearchServices = getModelLayerFactory().newTaxonomiesSearchService();

		users.setUp(userServices);

		defineSchemasManager().using(zeCollectionSchemas);
		defineSchemasManager().using(anotherCollectionSchemas);
		taxonomiesManager.addTaxonomy(zeCollectionSchemas.getTaxonomy1(), metadataSchemasManager);
		taxonomiesManager.addTaxonomy(zeCollectionSchemas.getTaxonomy2(), metadataSchemasManager);
		taxonomiesManager.setPrincipalTaxonomy(zeCollectionSchemas.getTaxonomy1(), metadataSchemasManager);
		taxonomiesManager.addTaxonomy(anotherCollectionSchemas.getTaxonomy1(), metadataSchemasManager);
		taxonomiesManager.setPrincipalTaxonomy(anotherCollectionSchemas.getTaxonomy1(), metadataSchemasManager);
		zeCollectionRecords = zeCollectionSchemas.givenRecords(recordServices);
		anotherCollectionRecords = anotherCollectionSchemas.givenRecords(recordServices);

		userServices.addUpdateUserCredential(
				userServices.getUserCredential(bobGratton).withServiceKey(bobKey).withSystemAdminPermission());
		userServices.addUpdateUserCredential(
				userServices.getUserCredential(chuckNorris).withServiceKey(chuckNorrisKey).withSystemAdminPermission());
		bobToken = userServices.generateToken(bobGratton);
		chuckNorrisToken = userServices.generateToken(chuckNorris);
		userServices.addUserToCollection(users.bob(), zeCollection);
		userServices.addUserToCollection(users.chuckNorris(), zeCollection);
		userServices.addUserToCollection(users.chuckNorris(), anotherCollection);

	}

	@Test
	public void givenCmisSessionAndTypesDefinition()
			throws Exception {

		cmisSession = newCmisSessionBuilder().authenticatedBy(chuckNorrisKey, chuckNorrisToken).onCollection(zeCollection)
				.build();

		thenCanConnectOnEachCollections();
		thenAllPropertiesAreSetted();

	}

	private void thenCanConnectOnEachCollections() {
		assertThat(cmisSession.getRootFolder().getProperty("cmis:path").getValue()).isEqualTo("/");
	}

	private void thenAllPropertiesAreSetted() {

		ObjectType baseFolderType = cmisSession.getTypeDefinition("cmis:folder");
		Iterator<ObjectType> iterator = baseFolderType.getChildren().iterator();

		ObjectType classificationStationType = iterator.next();
		assertThat(classificationStationType.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(classificationStationType.getId()).isEqualTo("administrativeUnit_classificationStation");
		assertThat(classificationStationType.getDescription()).isEqualTo("administrativeUnit_classificationStation");
		assertThat(classificationStationType.getDisplayName()).isEqualTo("administrativeUnit_classificationStation");
		assertThat(classificationStationType.getLocalName()).isEqualTo("administrativeUnit_classificationStation");
		assertThat(classificationStationType.getParentTypeId()).isEqualTo(baseFolderType.getId());
		assertThat(
				classificationStationType.getPropertyDefinitions()
						.get("allauthorizations").getCardinality()).isEqualTo(
				Cardinality.MULTI);
		assertThat(
				classificationStationType.getPropertyDefinitions().get("datecreation")
						.getCardinality()).isEqualTo(Cardinality.SINGLE);
		assertThat(
				classificationStationType.getPropertyDefinitions().get("datecreation")
						.getPropertyType()).isEqualTo(PropertyType.DATETIME);
		assertThat(
				classificationStationType.getPropertyDefinitions().get("datecreation")
						.getUpdatability()).isEqualTo(Updatability.READONLY);
		assertThat(
				classificationStationType.getPropertyDefinitions().get("datecreation")
						.isInherited()).isEqualTo(false);
		assertThat(
				classificationStationType.getPropertyDefinitions().get("datecreation")
						.isRequired()).isEqualTo(false);
		assertThat(
				classificationStationType.getPropertyDefinitions().get("number")
						.getPropertyType()).isEqualTo(PropertyType.DECIMAL);
		assertThat(
				classificationStationType.getPropertyDefinitions().get("booleanTest")
						.getPropertyType()).isEqualTo(PropertyType.BOOLEAN);
		assertThat(
				classificationStationType.getPropertyDefinitions().get("reference")
						.getPropertyType()).isEqualTo(PropertyType.STRING);
		assertThat(
				classificationStationType.getPropertyDefinitions().get("id")
						.getCardinality()).isEqualTo(Cardinality.SINGLE);
		assertThat(
				classificationStationType.getPropertyDefinitions()
						.get("inheritedauthorizations").getCardinality()).isEqualTo(
				Cardinality.MULTI);
		assertThat(
				classificationStationType.getPropertyDefinitions().get("parent")
						.getCardinality()).isEqualTo(Cardinality.SINGLE);
		assertThat(
				classificationStationType.getPropertyDefinitions().get("parentpath")
						.getCardinality()).isEqualTo(Cardinality.MULTI);
		assertThat(
				classificationStationType.getPropertyDefinitions().get("path")
						.getCardinality()).isEqualTo(Cardinality.MULTI);
		assertThat(
				classificationStationType.getPropertyDefinitions().get("schema")
						.getCardinality()).isEqualTo(Cardinality.SINGLE);
		assertThat(
				classificationStationType.getPropertyDefinitions()
						.get("removedauthorizations").getCardinality()).isEqualTo(
				Cardinality.MULTI);
		assertThat(
				classificationStationType.getPropertyDefinitions().get("title")
						.getCardinality()).isEqualTo(Cardinality.SINGLE);
		assertThat(
				classificationStationType.getPropertyDefinitions().get("tokens")
						.getCardinality()).isEqualTo(Cardinality.MULTI);

		ObjectType administrativeUnitDefault = iterator.next();
		assertThat(administrativeUnitDefault.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(administrativeUnitDefault.getId()).isEqualTo("administrativeUnit_default");
		assertThat(administrativeUnitDefault.getDescription()).isEqualTo("administrativeUnit_default");
		assertThat(administrativeUnitDefault.getDisplayName()).isEqualTo("administrativeUnit_default");
		assertThat(administrativeUnitDefault.getLocalName()).isEqualTo("administrativeUnit_default");
		assertThat(administrativeUnitDefault.getParentTypeId()).isEqualTo(baseFolderType.getId());
		assertThat(
				administrativeUnitDefault.getPropertyDefinitions().get("allauthorizations")
						.getCardinality()).isEqualTo(Cardinality.MULTI);
		assertThat(administrativeUnitDefault.getPropertyDefinitions().get("id").getCardinality())
				.isEqualTo(Cardinality.SINGLE);
		assertThat(
				administrativeUnitDefault.getPropertyDefinitions().get("inheritedauthorizations")
						.getCardinality()).isEqualTo(Cardinality.MULTI);
		assertThat(administrativeUnitDefault.getPropertyDefinitions().get("parent").getCardinality())
				.isEqualTo(Cardinality.SINGLE);
		assertThat(
				administrativeUnitDefault.getPropertyDefinitions().get("parentpath").getCardinality())
				.isEqualTo(Cardinality.MULTI);
		assertThat(administrativeUnitDefault.getPropertyDefinitions().get("path").getCardinality())
				.isEqualTo(Cardinality.MULTI);
		assertThat(administrativeUnitDefault.getPropertyDefinitions().get("schema").getCardinality())
				.isEqualTo(Cardinality.SINGLE);
		assertThat(
				administrativeUnitDefault.getPropertyDefinitions().get("removedauthorizations")
						.getCardinality()).isEqualTo(Cardinality.MULTI);
		assertThat(administrativeUnitDefault.getPropertyDefinitions().get("title").getCardinality())
				.isEqualTo(Cardinality.SINGLE);
		assertThat(administrativeUnitDefault.getPropertyDefinitions().get("tokens").getCardinality())
				.isEqualTo(Cardinality.MULTI);

		ObjectType categoryDefault = iterator.next();
		assertThat(categoryDefault.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(categoryDefault.getId()).isEqualTo("category_default");
		assertThat(categoryDefault.getDescription()).isEqualTo("category_default");
		assertThat(categoryDefault.getDisplayName()).isEqualTo("category_default");
		assertThat(categoryDefault.getLocalName()).isEqualTo("category_default");
		//		assertThat(categoryDefault.getPropertyDefinitions().get("parent")).isNull();
		assertThat(categoryDefault.getParentTypeId()).isEqualTo(baseFolderType.getId());

		ObjectType collectionType = iterator.next();
		assertThat(collectionType.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(collectionType.getId()).isEqualTo("collection_default");
		assertThat(collectionType.getDescription()).isEqualTo("collection_default");
		assertThat(collectionType.getDisplayName()).isEqualTo("collection_default");
		assertThat(collectionType.getLocalName()).isEqualTo("collection_default");
		//		assertThat(collectionType.getPropertyDefinitions().get("parent")).isNull();
		assertThat(collectionType.getParentTypeId()).isEqualTo(baseFolderType.getId());

		ObjectType documentFondDefault = iterator.next();
		assertThat(documentFondDefault.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(documentFondDefault.getId()).isEqualTo("documentFond_default");
		assertThat(documentFondDefault.getDescription()).isEqualTo("documentFond_default");
		assertThat(documentFondDefault.getDisplayName()).isEqualTo("documentFond_default");
		assertThat(documentFondDefault.getLocalName()).isEqualTo("documentFond_default");
		//		assertThat(documentFondDefault.getPropertyDefinitions().get("parent")).isNull();
		assertThat(documentFondDefault.getParentTypeId()).isEqualTo(baseFolderType.getId());

		ObjectType documentDefault = iterator.next();
		assertThat(documentDefault.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(documentDefault.getId()).isEqualTo("document_default");
		assertThat(documentDefault.getDescription()).isEqualTo("document_default");
		assertThat(documentDefault.getDisplayName()).isEqualTo("document_default");
		assertThat(documentDefault.getLocalName()).isEqualTo("document_default");
		//		assertThat(documentDefault.getPropertyDefinitions().get("parent")).isNull();
		assertThat(documentDefault.getParentTypeId()).isEqualTo(baseFolderType.getId());

		ObjectType emailToSendDefault = iterator.next();
		assertThat(emailToSendDefault.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(emailToSendDefault.getId()).isEqualTo("emailToSend_default");

		ObjectType eventDefault = iterator.next();
		assertThat(eventDefault.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(eventDefault.getId()).isEqualTo("event_default");
		assertThat(eventDefault.getDescription()).isEqualTo("event_default");
		assertThat(eventDefault.getDisplayName()).isEqualTo("event_default");
		assertThat(eventDefault.getLocalName()).isEqualTo("event_default");
		//		assertThat(eventDefault.getPropertyDefinitions().get("parent")).isNull();
		assertThat(eventDefault.getParentTypeId()).isEqualTo(baseFolderType.getId());

		ObjectType facetDefault = iterator.next();
		assertThat(facetDefault.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(facetDefault.getId()).isEqualTo("facet_default");
		assertThat(facetDefault.getDescription()).isEqualTo("facet_default");

		ObjectType facetField = iterator.next();
		assertThat(facetField.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(facetField.getId()).isEqualTo("facet_field");
		assertThat(facetField.getDescription()).isEqualTo("facet_field");

		ObjectType facetQuery = iterator.next();
		assertThat(facetQuery.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(facetQuery.getId()).isEqualTo("facet_query");
		assertThat(facetQuery.getDescription()).isEqualTo("facet_query");

		ObjectType folderDefault = iterator.next();
		assertThat(folderDefault.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(folderDefault.getId()).isEqualTo("folder_default");
		assertThat(folderDefault.getDescription()).isEqualTo("folder_default");
		assertThat(folderDefault.getDisplayName()).isEqualTo("folder_default");
		assertThat(folderDefault.getLocalName()).isEqualTo("folder_default");
		//		assertThat(folderDefault.getPropertyDefinitions().get("parent")).isNull();
		assertThat(folderDefault.getParentTypeId()).isEqualTo(baseFolderType.getId());

		ObjectType groupDefault = iterator.next();
		assertThat(groupDefault.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(groupDefault.getId()).isEqualTo("group_default");
		assertThat(groupDefault.getDescription()).isEqualTo("group_default");
		assertThat(groupDefault.getDisplayName()).isEqualTo("group_default");
		assertThat(groupDefault.getLocalName()).isEqualTo("group_default");
		//		assertThat(groupDefault.getPropertyDefinitions().get("parent")).isNull();
		assertThat(groupDefault.getParentTypeId()).isEqualTo(baseFolderType.getId());

		ObjectType report = iterator.next();
		assertThat(report.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(report.getId()).isEqualTo("report_default");
		assertThat(report.getDescription()).isEqualTo("report_default");
		assertThat(report.getDisplayName()).isEqualTo("report_default");
		assertThat(report.getLocalName()).isEqualTo("report_default");
		//		assertThat(report.getPropertyDefinitions().get("parent")).isNull();
		assertThat(report.getParentTypeId()).isEqualTo(baseFolderType.getId());

		ObjectType savedSearch = iterator.next();
		assertThat(savedSearch.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(savedSearch.getId()).isEqualTo("savedSearch_default");

		ObjectType approvalTask = iterator.next();
		assertThat(approvalTask.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(approvalTask.getId()).isEqualTo("task_approval");
		assertThat(approvalTask.getDescription()).isEqualTo("task_approval");
		assertThat(approvalTask.getDisplayName()).isEqualTo("task_approval");
		assertThat(approvalTask.getLocalName()).isEqualTo("task_approval");
		//		assertThat(approvalTask.getPropertyDefinitions().get("parent")).isNull();
		assertThat(approvalTask.getParentTypeId()).isEqualTo(baseFolderType.getId());

		ObjectType taskType = iterator.next();
		assertThat(taskType.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(taskType.getId()).isEqualTo("task_default");
		assertThat(taskType.getDescription()).isEqualTo("task_default");
		assertThat(taskType.getDisplayName()).isEqualTo("task_default");
		assertThat(taskType.getLocalName()).isEqualTo("task_default");
		//		assertThat(taskType.getPropertyDefinitions().get("parent")).isNull();
		assertThat(taskType.getParentTypeId()).isEqualTo(baseFolderType.getId());

		ObjectType taxonomyType = iterator.next();
		assertThat(taxonomyType.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(taxonomyType.getId()).isEqualTo("taxonomy");
		assertThat(taxonomyType.getDescription()).isEqualTo("taxonomy");
		assertThat(taxonomyType.getDisplayName()).isEqualTo("taxonomy");
		assertThat(taxonomyType.getLocalName()).isEqualTo("taxonomy");
		//		assertThat(taxonomyType.getPropertyDefinitions().get("parent")).isNull();
		assertThat(taxonomyType.getParentTypeId()).isEqualTo(baseFolderType.getId());

		ObjectType userDocument = iterator.next();
		assertThat(userDocument.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(userDocument.getId()).isEqualTo("userDocument_default");
		assertThat(userDocument.getDescription()).isEqualTo("userDocument_default");
		assertThat(userDocument.getDisplayName()).isEqualTo("userDocument_default");
		assertThat(userDocument.getLocalName()).isEqualTo("userDocument_default");
		//		assertThat(userDocument.getPropertyDefinitions().get("parent")).isNull();
		assertThat(userDocument.getParentTypeId()).isEqualTo(baseFolderType.getId());

		ObjectType userDefault = iterator.next();
		assertThat(userDefault.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(userDefault.getId()).isEqualTo("user_default");
		assertThat(userDefault.getDescription()).isEqualTo("user_default");
		assertThat(userDefault.getDisplayName()).isEqualTo("user_default");
		assertThat(userDefault.getLocalName()).isEqualTo("user_default");
		//		assertThat(userDefault.getPropertyDefinitions().get("parent")).isNull();
		assertThat(userDefault.getParentTypeId()).isEqualTo(baseFolderType.getId());
	}

	private void whenChangeTypeDefinitionThenRefreshProperties() {
		MetadataSchemaTypesBuilder typesBuilder = metadataSchemasManager.modify(zeCollection);
		typesBuilder.getSchema("administrativeUnit_classificationStation").create("newMetadata")
				.setType(MetadataValueType.STRING);
		try {
			metadataSchemasManager.saveUpdateSchemaTypes(typesBuilder);
		} catch (OptimisticLocking e) {
			throw new RuntimeException(e);
		}

		cmisSession = newCmisSessionBuilder().authenticatedBy(chuckNorrisKey, chuckNorrisToken).onCollection(zeCollection)
				.build();

		ObjectType baseFolderType = cmisSession.getTypeDefinition("cmis:folder");
		Iterator<ObjectType> iterator = baseFolderType.getChildren().iterator();

		iterator.next();
		ObjectType classificationStationType = iterator.next();
		assertThat(classificationStationType.getBaseTypeId().value()).isEqualTo("cmis:folder");
		assertThat(classificationStationType.getId()).isEqualTo("administrativeUnit_classificationStation");
		assertThat(classificationStationType.getDescription()).isEqualTo("classificationStation");
		assertThat(classificationStationType.getDisplayName()).isEqualTo("administrativeUnit_classificationStation");
		assertThat(classificationStationType.getLocalName()).isEqualTo("administrativeUnit_classificationStation");
		assertThat(classificationStationType.getParentTypeId()).isEqualTo(baseFolderType.getId());
		assertThat(
				classificationStationType.getPropertyDefinitions().get("datecreation")
						.getCardinality()).isEqualTo(Cardinality.SINGLE);
		assertThat(
				classificationStationType.getPropertyDefinitions().get("newMetadata")
						.getPropertyType()).isEqualTo(PropertyType.STRING);
	}
}
