package com.constellio.app.services.extensions.plugins.pluginDetector;

import static com.constellio.app.services.extensions.plugins.PluginActivationFailureCause.MORE_THAN_ONE_INSTALLABLE_MODULE_PER_JAR;
import static com.constellio.app.services.extensions.plugins.PluginActivationFailureCause.NO_INSTALLABLE_MODULE_DETECTED_FROM_JAR;

import java.io.File;
import java.util.Collection;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.util.PluginManagerUtil;

import com.constellio.app.entities.modules.InstallableModule;

public class FromJarPluginDetector implements PluginDetector {
	@Override
	public Collection<InstallableModule> detectPlugin(PluginManager pluginManager, File pluginJar) throws NoInstallableModuleInJar, MoreThanOneInstallableModuleInJar{
		PluginManagerUtil util = new PluginManagerUtil(pluginManager);
		Collection<InstallableModule> pluginsBefore = util.getPlugins(InstallableModule.class);

		pluginManager.addPluginsFrom(pluginJar.toURI());
		util = new PluginManagerUtil(pluginManager);
		Collection<InstallableModule> pluginsAfter = util.getPlugins(InstallableModule.class);

		if (pluginsAfter.size() == pluginsBefore.size()) {
			throw new NoInstallableModuleInJar();
		} else if (pluginsAfter.size() > pluginsBefore.size() + 1) {
			throw new MoreThanOneInstallableModuleInJar();
		}
		return pluginsAfter;

	}
}
