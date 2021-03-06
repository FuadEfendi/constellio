package com.constellio.sdk.tests;

import static com.constellio.data.conf.DigitSeparatorMode.THREE_LEVELS_OF_ONE_DIGITS;
import static com.constellio.data.conf.HashingEncoding.BASE32;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.constellio.app.conf.AppLayerConfiguration;
import com.constellio.app.services.extensions.plugins.ConstellioPluginManager;
import com.constellio.app.services.factories.AppLayerFactory;
import com.constellio.app.services.factories.ConstellioFactoriesDecorator;
import com.constellio.data.conf.DataLayerConfiguration;
import com.constellio.data.conf.PropertiesDataLayerConfiguration;
import com.constellio.model.conf.FoldersLocator;
import com.constellio.model.conf.ModelLayerConfiguration;

public class TestConstellioFactoriesDecorator extends ConstellioFactoriesDecorator {

	boolean backgroundThreadsEnabled;
	boolean mockPluginManager;
	String systemLanguage;
	File setupProperties;
	File importationFolder;
	File configManagerFolder;
	File appTempFolder;
	File contentFolder;
	File pluginsFolder, pluginsToMoveOnStartup;
	List<DataLayerConfigurationAlteration> dataLayerConfigurationAlterations = new ArrayList<>();
	List<ModelLayerConfigurationAlteration> modelLayerConfigurationAlterations = new ArrayList<>();
	List<AppLayerConfigurationAlteration> appLayerConfigurationAlterations = new ArrayList<>();
	final private boolean checkRollback;
	private File transactionLogWorkFolder;

	public TestConstellioFactoriesDecorator(boolean backgroundThreadsEnabled, boolean mockPluginManager, boolean checkRollback) {
		this.backgroundThreadsEnabled = backgroundThreadsEnabled;
		this.checkRollback = checkRollback;
		this.mockPluginManager = mockPluginManager;
	}

	@Override
	public DataLayerConfiguration decorateDataLayerConfiguration(DataLayerConfiguration dataLayerConfiguration) {
		PropertiesDataLayerConfiguration spiedDataLayerConfiguration = spy(
				(PropertiesDataLayerConfiguration) dataLayerConfiguration);
		doNothing().when(spiedDataLayerConfiguration).writeProperty(anyString(), anyString());
		doReturn(configManagerFolder).when(spiedDataLayerConfiguration).getSettingsFileSystemBaseFolder();
		doReturn(appTempFolder).when(spiedDataLayerConfiguration).getTempFolder();
		doReturn(contentFolder).when(spiedDataLayerConfiguration).getContentDaoFileSystemFolder();
		doReturn(backgroundThreadsEnabled).when(spiedDataLayerConfiguration).isBackgroundThreadsEnabled();
		doReturn(checkRollback).when(spiedDataLayerConfiguration).isInRollbackTestMode();
		doReturn(transactionLogWorkFolder).when(spiedDataLayerConfiguration).getSecondTransactionLogBaseFolder();

		spiedDataLayerConfiguration.setContentDaoFileSystemDigitsSeparatorMode(THREE_LEVELS_OF_ONE_DIGITS);
		spiedDataLayerConfiguration.setHashingEncoding(BASE32);

		if (transactionLogWorkFolder != null) {
			doReturn(true).when(spiedDataLayerConfiguration).isSecondTransactionLogEnabled();
		}

		for (DataLayerConfigurationAlteration alteration : dataLayerConfigurationAlterations) {
			alteration.alter(spiedDataLayerConfiguration);
		}

		return spiedDataLayerConfiguration;
	}

	@Override
	public ModelLayerConfiguration decorateModelLayerConfiguration(ModelLayerConfiguration modelLayerConfiguration) {
		File key = new File(configManagerFolder, "key.txt");
		ModelLayerConfiguration spiedModelLayerConfiguration = spy(modelLayerConfiguration);
		doReturn(key).when(spiedModelLayerConfiguration).getConstellioEncryptionFile();
		doReturn(importationFolder).when(spiedModelLayerConfiguration).getImportationFolder();
		if (systemLanguage != null) {
			doReturn(systemLanguage).when(spiedModelLayerConfiguration).getMainDataLanguage();
		} else {
			doReturn("fr").when(spiedModelLayerConfiguration).getMainDataLanguage();
		}

		for (ModelLayerConfigurationAlteration alteration : modelLayerConfigurationAlterations) {
			alteration.alter(spiedModelLayerConfiguration);
		}

		return spiedModelLayerConfiguration;
	}

	@Override
	public AppLayerConfiguration decorateAppLayerConfiguration(AppLayerConfiguration appLayerConfiguration) {

		AppLayerConfiguration spiedAppLayerConfiguration = spy(appLayerConfiguration);

		doReturn(setupProperties).when(spiedAppLayerConfiguration).getSetupProperties();
		doReturn(pluginsFolder).when(spiedAppLayerConfiguration).getPluginsFolder();
		doReturn(pluginsToMoveOnStartup).when(spiedAppLayerConfiguration).getPluginsManagementOnStartupFile();

		for (AppLayerConfigurationAlteration alteration : appLayerConfigurationAlterations) {
			alteration.alter(spiedAppLayerConfiguration);
		}

		return spiedAppLayerConfiguration;
	}

	@Override
	public AppLayerFactory decorateAppServicesFactory(AppLayerFactory appLayerFactory) {
		AppLayerFactory spiedAppLayerFactory = spy(appLayerFactory);

		if (mockPluginManager) {
			ConstellioPluginManager pluginManager = mock(ConstellioPluginManager.class, "pluginManager");
			when(spiedAppLayerFactory.getPluginManager()).thenReturn(pluginManager);
		}

		return spiedAppLayerFactory;
	}

	@Override
	public FoldersLocator decorateFoldersLocator(FoldersLocator foldersLocator) {
		FoldersLocator spiedFoldersLocator = spy(foldersLocator);
		doReturn(appTempFolder).when(spiedFoldersLocator).getDefaultTempFolder();
		return spiedFoldersLocator;
	}

	public TestConstellioFactoriesDecorator setSetupProperties(File setupProperties) {
		this.setupProperties = setupProperties;
		return this;
	}

	public TestConstellioFactoriesDecorator setImportationFolder(File importationFolder) {
		this.importationFolder = importationFolder;
		return this;
	}

	public TestConstellioFactoriesDecorator setConfigManagerFolder(File configManagerFolder) {
		this.configManagerFolder = configManagerFolder;
		return this;
	}

	public TestConstellioFactoriesDecorator setAppTempFolder(File appTempFolder) {
		this.appTempFolder = appTempFolder;
		return this;
	}

	public TestConstellioFactoriesDecorator setContentFolder(File contentFolder) {
		this.contentFolder = contentFolder;
		return this;
	}

	public TestConstellioFactoriesDecorator setPluginsFolder(File pluginsFolder) {
		this.pluginsFolder = pluginsFolder;
		return this;
	}

	public TestConstellioFactoriesDecorator setPluginsToMoveOnStartupFile(File pluginsFolder) {
		this.pluginsToMoveOnStartup = pluginsFolder;
		return this;
	}

	public TestConstellioFactoriesDecorator setDataLayerConfigurationAlterations(
			List<DataLayerConfigurationAlteration> dataLayerConfigurationAlterations) {
		this.dataLayerConfigurationAlterations = dataLayerConfigurationAlterations;
		return this;
	}

	public TestConstellioFactoriesDecorator setModelLayerConfigurationAlterations(
			List<ModelLayerConfigurationAlteration> modelLayerConfigurationAlterations) {
		this.modelLayerConfigurationAlterations = modelLayerConfigurationAlterations;
		return this;
	}

	public TestConstellioFactoriesDecorator setAppLayerConfigurationAlterations(
			List<AppLayerConfigurationAlteration> appLayerConfigurationAlterations) {
		this.appLayerConfigurationAlterations = appLayerConfigurationAlterations;
		return this;
	}

	public void setSystemLanguage(String systemLanguage) {
		this.systemLanguage = systemLanguage;
	}

	public void setTransactionLogWorkFolder(File transactionLogWorkFolder) {
		this.transactionLogWorkFolder = transactionLogWorkFolder;
	}
}