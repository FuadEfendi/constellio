package com.constellio.app.services.extensions.plugins.pluginDetector;

import java.io.File;
import java.util.Collection;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import net.xeoh.plugins.base.util.uri.ClassURI;

import com.constellio.app.entities.modules.InstallableModule;

public class FromLibPluginDetector implements PluginDetector {
	@Override
	public Collection<InstallableModule> detectPlugin(PluginManager pluginManager, File pluginJar)
			throws NoInstallableModuleInJar, MoreThanOneInstallableModuleInJar {
		PluginManagerUtil util = new PluginManagerUtil(pluginManager);
		pluginManager.addPluginsFrom(ClassURI.CLASSPATH);
		return util.getPlugins(InstallableModule.class);
	}
}
