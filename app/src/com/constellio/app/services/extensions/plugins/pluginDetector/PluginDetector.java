package com.constellio.app.services.extensions.plugins.pluginDetector;

import java.io.File;
import java.util.Collection;

import net.xeoh.plugins.base.PluginManager;

import com.constellio.app.entities.modules.InstallableModule;

public interface PluginDetector {
	Collection<InstallableModule> detectPlugin(PluginManager pluginManager, File pluginJar) throws NoInstallableModuleInJar, MoreThanOneInstallableModuleInJar;
}
