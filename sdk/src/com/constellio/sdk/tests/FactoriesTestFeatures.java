package com.constellio.sdk.tests;

import static com.constellio.data.dao.dto.records.RecordsFlushing.NOW;
import static com.constellio.sdk.tests.TestUtils.asList;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.solr.common.params.ModifiableSolrParams;

import com.constellio.app.services.extensions.plugins.ConstellioPluginManager;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.services.factories.ConstellioFactories;
import com.constellio.app.ui.i18n.i18n;
import com.constellio.data.conf.ConfigManagerType;
import com.constellio.data.conf.ContentDaoType;
import com.constellio.data.conf.DataLayerConfiguration;
import com.constellio.data.dao.managers.StatefulService;
import com.constellio.data.dao.managers.StatefullServiceDecorator;
import com.constellio.data.dao.services.bigVault.solr.BigVaultException;
import com.constellio.data.dao.services.bigVault.solr.BigVaultException.CouldNotExecuteQuery;
import com.constellio.data.dao.services.bigVault.solr.BigVaultServer;
import com.constellio.data.dao.services.bigVault.solr.BigVaultServerTransaction;
import com.constellio.data.dao.services.factories.DataLayerFactory;
import com.constellio.data.extensions.DataLayerSystemExtensions;
import com.constellio.data.extensions.TransactionLogExtension;
import com.constellio.data.frameworks.extensions.ExtensionBooleanResult;
import com.constellio.data.io.IOServicesFactory;
import com.constellio.data.utils.Factory;
import com.constellio.model.conf.FoldersLocator;
import com.constellio.model.conf.ModelLayerConfiguration;
import com.constellio.model.entities.security.global.UserCredential;
import com.constellio.model.services.encrypt.EncryptionServices;
import com.constellio.model.services.factories.ModelLayerFactory;
import com.constellio.sdk.FakeEncryptionServices;

public class FactoriesTestFeatures {

	private boolean fakeEncryptionServices;
	private boolean useSDKPluginFolder;
	private boolean instanciated = false;
	private boolean backgroundThreadsEnabled = false;
	private boolean checkRollback;
	private List<String> loggingOfRecords = new ArrayList<>();
	private boolean dummyPasswords;

	private File initialState;
	private final FileSystemTestFeatures fileSystemTestFeatures;
	private ConstellioFactories factoriesInstance;
	private List<Class<?>> spiedClasses = new ArrayList<>();

	private TestConstellioFactoriesDecorator decorator;

	//	private Map<String, String> sdkProperties;
	private List<DataLayerConfigurationAlteration> dataLayerConfigurationAlterations = new ArrayList<>();
	private List<ModelLayerConfigurationAlteration> modelLayerConfigurationAlterations = new ArrayList<>();
	private List<AppLayerConfigurationAlteration> appLayerConfigurationAlterations = new ArrayList<>();
	private Map<String, String> configs = new HashMap<>();
	private String systemLanguage;

	public FactoriesTestFeatures(FileSystemTestFeatures fileSystemTestFeatures, Map<String, String> sdkProperties,
			boolean checkRollback) {
		this.fileSystemTestFeatures = fileSystemTestFeatures;
		this.checkRollback = checkRollback;
		//		this.sdkProperties = sdkProperties;
	}

	public void afterTest() {

		if (instanciated) {
			clear();
		}

		ConstellioFactories.clear();
		factoriesInstance = null;

	}

	public void clear() {
		factoriesInstance = getConstellioFactories();
		DataLayerConfiguration conf = factoriesInstance.getDataLayerConfiguration();
		for (BigVaultServer server : factoriesInstance.getDataLayerFactory().getSolrServers().getServers()) {
			deleteServerRecords(server);
		}

		if (ContentDaoType.HADOOP == conf.getContentDaoType()) {
			deleteFromHadoop(conf.getContentDaoHadoopUser(), conf.getContentDaoHadoopUrl());

		}

		if (ConfigManagerType.ZOOKEEPER == conf.getSettingsConfigType()) {
			deleteFromZooKeeper(conf.getSettingsZookeeperAddress());
		}

		i18n.clearBundles();
	}

	private void deleteFromZooKeeper(String address) {
		try {
			RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
			CuratorFramework client = CuratorFrameworkFactory.newClient(address, retryPolicy);
			client.start();
			client.delete().deletingChildrenIfNeeded().forPath("/constellio");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void deleteFromHadoop(String user, String url) {
		System.setProperty("HADOOP_USER_NAME", user);
		Configuration hadoopConfig = new Configuration();

		if (url == null || user == null) {
			throw new RuntimeException("No config");
		}

		hadoopConfig.set("fs.defaultFS", url);
		hadoopConfig.set("hadoop.job.ugi", user);

		try {
			FileSystem hdfs = FileSystem.get(hadoopConfig);
			for (FileStatus file : hdfs.globStatus(new Path("*"))) {
				hdfs.delete(file.getPath(), true);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void deleteServerRecords(BigVaultServer server) {

		BigVaultServer vaultServer = server.clone();
		vaultServer.unregisterAllListeners();
		vaultServer.disableLogger();
		vaultServer.setExtensions(new DataLayerSystemExtensions());

		ModifiableSolrParams allRecordsSolrParams = new ModifiableSolrParams();
		allRecordsSolrParams.set("q", "*:*");

		try {
			vaultServer
					.addAll(new BigVaultServerTransaction(NOW).addDeletedQuery("*:*"));
		} catch (BigVaultException e) {
			throw new RuntimeException("Cannot deleteLogically by query *:*");
		}
		try {

			if (!vaultServer.query(allRecordsSolrParams).getResults().isEmpty()) {
				throw new RuntimeException("Invalid solr core initial state ");
			}

		} catch (CouldNotExecuteQuery couldNotExecuteQuery) {
			throw new RuntimeException(couldNotExecuteQuery);
		}
	}

	public synchronized ConstellioFactories getConstellioFactories() {
		instanciated = true;
		if (decorator == null) {

			StringBuilder setupPropertiesContent = new StringBuilder();
			setupPropertiesContent.append("admin.servicekey=adminkey\n");
			setupPropertiesContent.append("admin.password=password\n");
			File setupProperties = fileSystemTestFeatures.newTempFileWithContent(setupPropertiesContent.toString());

			decorator = new TestConstellioFactoriesDecorator(backgroundThreadsEnabled, true, checkRollback) {

				@Override
				public DataLayerFactory decorateDataLayerFactory(DataLayerFactory dataLayerFactory) {

					if (!loggingOfRecords.isEmpty()) {
						dataLayerFactory.getDataLayerLogger().setMonitoredIds(loggingOfRecords);
					}

					dataLayerFactory.getExtensions().getSystemWideExtensions().getTransactionLogExtensions()
							.add(new TransactionLogExtension() {
								@Override
								public ExtensionBooleanResult isDocumentFieldLoggedInTransactionLog(String field, String schema,
										String collection) {
									return ExtensionBooleanResult.FORCE_TRUE;
								}
							});

					if (spiedClasses.isEmpty()) {
						return dataLayerFactory;
					} else {
						return spy(dataLayerFactory);
					}
				}

				@Override
				public ModelLayerFactory decorateModelServicesFactory(final ModelLayerFactory modelLayerFactory) {

					if (spiedClasses.isEmpty()) {
						return modelLayerFactory;
					} else {
						return spy(modelLayerFactory);
					}
				}

				@Override
				public AppLayerFactory decorateAppServicesFactory(final AppLayerFactory appLayerFactory) {

					if (dummyPasswords) {
						appLayerFactory.add(new StatefulService() {
							@Override
							public void initialize() {
								try {
									ModelLayerFactory modelLayerFactory = appLayerFactory.getModelLayerFactory();
									List<UserCredential> users = modelLayerFactory.newUserServices().getAllUserCredentials();
									StringBuilder passwordFileContent = new StringBuilder();
									for (UserCredential user : users) {
										passwordFileContent.append(user.getUsername() + "=W6ph5Mm5Pz8GgiULbPgzG37mj9g\\=\n");
									}
									File settingsFolder = modelLayerFactory.getDataLayerFactory().getDataLayerConfiguration()
											.getSettingsFileSystemBaseFolder();
									File authenticationFile = new File(settingsFolder, "authentification.properties");
									try {
										FileUtils.write(authenticationFile, passwordFileContent.toString());
									} catch (IOException e) {
										throw new RuntimeException(e);
									}
								} catch (Exception e) {
									//e.printStackTrace();
								}
							}

							@Override
							public void close() {

							}
						});
					}

					return spy(appLayerFactory);
				}

				@Override
				public StatefullServiceDecorator getStatefullServiceDecorator() {
					return new StatefullServiceDecorator() {
						@Override
						public <T> T decorate(T service) {

							if (service != null && ConstellioPluginManager.class.isAssignableFrom(service.getClass())) {
								return spy(service);
							} else {

								for (Class<?> spiedClass : spiedClasses) {

									if (spiedClass.isAssignableFrom(service.getClass())) {
										return spy(service);
									}
								}
							}
							return service;
						}
					};

				}
			};

			if (fakeEncryptionServices) {
				modelLayerConfigurationAlterations.add(new ModelLayerConfigurationAlteration() {
					@Override
					public void alter(ModelLayerConfiguration configuration) {
						Factory<EncryptionServices> encryptionServicesFactory = new Factory<EncryptionServices>() {
							@Override
							public EncryptionServices get() {
								return new FakeEncryptionServices();
							}
						};
						when(configuration.getEncryptionServicesFactory()).thenReturn(encryptionServicesFactory);
					}
				});
			}

			File configManagerFolder = fileSystemTestFeatures.newTempFolderWithName("configManagerFolder");
			File contentFolder = fileSystemTestFeatures.newTempFolderWithName("contentFolder");
			File pluginsFolder;
			if (useSDKPluginFolder) {
				pluginsFolder = new SDKFoldersLocator().getPluginsJarsFolder();
			} else {
				pluginsFolder = fileSystemTestFeatures.newTempFolderWithName("plugins");
			}
			File tlogWorkFolder = fileSystemTestFeatures.newTempFolderWithName("tlogWorkFolder");

			decorator.setDataLayerConfigurationAlterations(dataLayerConfigurationAlterations);
			decorator.setModelLayerConfigurationAlterations(modelLayerConfigurationAlterations);
			decorator.setAppLayerConfigurationAlterations(appLayerConfigurationAlterations);
			decorator.setSetupProperties(setupProperties);
			decorator.setImportationFolder(fileSystemTestFeatures.newTempFolderWithName("importationFolder"));
			decorator.setConfigManagerFolder(configManagerFolder);
			decorator.setAppTempFolder(fileSystemTestFeatures.newTempFolderWithName("appTempFolder"));
			decorator.setContentFolder(contentFolder);
			decorator.setPluginsFolder(pluginsFolder)
					.setPluginsToMoveOnStartupFile(fileSystemTestFeatures.newTempFileWithContent(""));
			decorator.setTransactionLogWorkFolder(tlogWorkFolder);
			decorator.setSystemLanguage(systemLanguage);

			if (initialState != null) {
				if (!ConstellioTest.isCurrentPreservingState()) {
					File tempFolder = fileSystemTestFeatures.newTempFolder();
					try {
						SaveStateFeature
								.loadStateFrom(initialState, tempFolder, configManagerFolder, contentFolder, pluginsFolder,
										tlogWorkFolder, dummyPasswords);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}

		File propertyFile = new SDKFoldersLocator().getSDKProperties();

		if (!configs.isEmpty()) {

			File tempPropertyFile = fileSystemTestFeatures.newTempFileInNewTempFolder("temp-sdk.properties");
			StringBuilder basePropertyContent = new StringBuilder();
			try {
				basePropertyContent.append(FileUtils.readFileToString(propertyFile) + "\n");

				for (Map.Entry<String, String> entry : configs.entrySet()) {
					basePropertyContent.append(entry.getKey() + "=" + entry.getValue() + "\n");
				}

				FileUtils.writeStringToFile(tempPropertyFile, basePropertyContent.toString());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			propertyFile = tempPropertyFile;
		}

		factoriesInstance = ConstellioFactories.getInstance(propertyFile, decorator);

		return factoriesInstance;
	}

	public void configure(DataLayerConfigurationAlteration dataLayerConfigurationAlteration) {
		dataLayerConfigurationAlterations.add(dataLayerConfigurationAlteration);
	}

	public void configure(ModelLayerConfigurationAlteration modelLayerConfigurationAlteration) {
		modelLayerConfigurationAlterations.add(modelLayerConfigurationAlteration);
	}

	public void configure(AppLayerConfigurationAlteration appLayerConfigurationAlteration) {
		appLayerConfigurationAlterations.add(appLayerConfigurationAlteration);
	}

	public void load() {
		getConstellioFactories();
	}

	public DataLayerFactory newDaosFactory() {
		return getConstellioFactories().getDataLayerFactory();
	}

	public IOServicesFactory newIOServicesFactory() {
		return getConstellioFactories().getIoServicesFactory();
	}

	public ModelLayerFactory newModelServicesFactory() {
		return getConstellioFactories().getModelLayerFactory();
	}

	public AppLayerFactory newAppServicesFactory() {
		return getConstellioFactories().getAppLayerFactory();
	}

	public FoldersLocator getFoldersLocator() {
		return getConstellioFactories().getFoldersLocator();
	}

	public void withSpiedServices(Class<?>[] classes) {
		spiedClasses.addAll(asList(classes));
	}

	public void givenConstellioProperties(Map<String, String> configs) {
		this.configs.putAll(configs);

	}

	public FactoriesTestFeatures givenSystemInState(File state) {
		this.initialState = state;
		return this;
	}

	public FactoriesTestFeatures withPasswordsReset() {
		this.dummyPasswords = true;
		return this;
	}

	public void setSystemLanguage(String languageCode) {
		this.systemLanguage = languageCode;
	}

	public FactoriesTestFeatures withLoggingOfRecords(String... loggingOfRecordsArray) {
		loggingOfRecords.addAll(asList(loggingOfRecordsArray));
		return this;
	}

	public boolean isInitialized() {
		return factoriesInstance != null;
	}

	public void givenBackgroundThreadsEnabled() {
		backgroundThreadsEnabled = true;
	}

	public FactoriesTestFeatures withoutCheckForRollback() {
		checkRollback = false;
		return this;
	}

	public FactoriesTestFeatures withFakeEncryptionServices() {
		fakeEncryptionServices = true;
		return this;
	}

	public FactoriesTestFeatures withSDKPluginFolder() {
		useSDKPluginFolder = true;
		return this;
	}

	public boolean isCheckRollback() {
		return checkRollback;
	}

}
