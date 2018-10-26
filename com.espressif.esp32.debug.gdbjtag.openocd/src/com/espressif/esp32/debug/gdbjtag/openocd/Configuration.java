/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Liviu Ionescu - initial version
 *    Espressif Systems Ltd — ESP32 support
 *******************************************************************************/

package com.espressif.esp32.debug.gdbjtag.openocd;

import com.espressif.esp32.core.EclipseUtils;
import com.espressif.esp32.core.StringUtils;
import com.espressif.esp32.debug.gdbjtag.DebugUtils;
import com.espressif.esp32.debug.gdbjtag.dsf.GnuArmFinalLaunchSequence;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.IGdbDebugPreferenceConstants;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;

@SuppressWarnings("restriction")
public class Configuration {

	// ------------------------------------------------------------------------

	public static String getGdbServerCommand(ILaunchConfiguration configuration) {

		String executable = null;

		try {
			if (!configuration.getAttribute(ConfigurationAttributes.DO_START_GDB_SERVER,
					DefaultPreferences.DO_START_GDB_SERVER_DEFAULT))
				return null;

			executable = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_EXECUTABLE,
					DefaultPreferences.GDB_SERVER_EXECUTABLE_DEFAULT);
			// executable = Utils.escapeWhitespaces(executable).trim();
			executable = executable.trim();
			if (executable.length() == 0)
				return null;

			executable = DebugUtils.resolveAll(executable, configuration.getAttributes());

			ICConfigurationDescription buildConfig = EclipseUtils.getBuildConfigDescription(configuration);
			if (buildConfig != null) {
				executable = DebugUtils.resolveAll(executable, buildConfig);
			}

		} catch (CoreException e) {
			Activator.log(e);
			return null;
		}

		return executable;
	}

	public static String getGdbServerCommandLine(ILaunchConfiguration configuration) {

		String cmdLineArray[] = getGdbServerCommandLineArray(configuration);
		return StringUtils.join(cmdLineArray, " ");
	}

	public static String[] getGdbServerCommandLineArray(ILaunchConfiguration configuration) {

		List<String> lst = new ArrayList<String>();

		try {
			if (!configuration.getAttribute(ConfigurationAttributes.DO_START_GDB_SERVER,
					DefaultPreferences.DO_START_GDB_SERVER_DEFAULT))
				return null;

			String executable = getGdbServerCommand(configuration);
			if (executable == null || executable.length() == 0)
				return null;

			lst.add(executable);

			lst.add("-c");
			lst.add("gdb_port "
					+ Integer.toString(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER,
							DefaultPreferences.GDB_SERVER_GDB_PORT_NUMBER_DEFAULT)));

			lst.add("-c");
			lst.add("telnet_port "
					+ Integer.toString(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER,
							DefaultPreferences.GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT)));

			String other = configuration
					.getAttribute(ConfigurationAttributes.GDB_SERVER_OTHER, DefaultPreferences.GDB_SERVER_OTHER_DEFAULT)
					.trim();

			other = DebugUtils.resolveAll(other, configuration.getAttributes());

			if (!other.isEmpty()) {
				lst.addAll(StringUtils.splitCommandLineOptions(other));
			}

		} catch (CoreException e) {
			Activator.log(e);
			return null;
		}

		// Added as a marker, it is displayed if the configuration was processed
		// properly.
		lst.add("-c");
		lst.add("echo \"Started by ESP32 Eclipse\"");

		return lst.toArray(new String[0]);
	}

	public static String getGdbServerCommandName(ILaunchConfiguration config) {

		String fullCommand = getGdbServerCommand(config);
		return StringUtils.extractNameFromPath(fullCommand);
	}

	public static String getGdbServerOtherConfig(ILaunchConfiguration config) throws CoreException {

		return config
				.getAttribute(ConfigurationAttributes.GDB_SERVER_OTHER, DefaultPreferences.GDB_SERVER_OTHER_DEFAULT)
				.trim();
	}

	// ------------------------------------------------------------------------

	public static String getGdbClientCommand(ILaunchConfiguration configuration) {

		String executable = null;
		try {
			String defaultGdbCommand = Platform.getPreferencesService().getString(GdbPlugin.PLUGIN_ID,
					IGdbDebugPreferenceConstants.PREF_DEFAULT_GDB_COMMAND,
					IGDBLaunchConfigurationConstants.DEBUGGER_DEBUG_NAME_DEFAULT, null);

			executable = configuration.getAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
					defaultGdbCommand);
			executable = DebugUtils.resolveAll(executable, configuration.getAttributes());

			ICConfigurationDescription buildConfig = EclipseUtils.getBuildConfigDescription(configuration);
			if (buildConfig != null) {
				executable = DebugUtils.resolveAll(executable, buildConfig);
			}

		} catch (CoreException e) {
			Activator.log(e);
			return null;
		}

		return executable;
	}

	public static String[] getGdbClientCommandLineArray(ILaunchConfiguration configuration) {

		List<String> lst = new ArrayList<String>();

		String executable = getGdbClientCommand(configuration);
		if (executable == null || executable.length() == 0)
			return null;

		lst.add(executable);

		// We currently work with MI version 2. Don't use just 'mi' because
		// it points to the latest MI version, while we want mi2 specifically.
		lst.add("--interpreter=mi2");

		// Don't read the gdbinit file here. It is read explicitly in
		// the LaunchSequence to make it easier to customise.
		lst.add("--nx");

		String other;
		try {
			other = configuration.getAttribute(ConfigurationAttributes.GDB_CLIENT_OTHER_OPTIONS,
					DefaultPreferences.GDB_CLIENT_OTHER_OPTIONS_DEFAULT).trim();

			other = DebugUtils.resolveAll(other, configuration.getAttributes());

			if (!other.isEmpty()) {
				lst.addAll(StringUtils.splitCommandLineOptions(other));
			}
		} catch (CoreException e) {
			Activator.log(e);
		}

		return lst.toArray(new String[0]);
	}

	public static String getGdbClientCommandLine(ILaunchConfiguration configuration) {

		String cmdLineArray[] = getGdbClientCommandLineArray(configuration);
		return StringUtils.join(cmdLineArray, " ");
	}

	public static String getGdbClientCommandName(ILaunchConfiguration config) {

		String fullCommand = getGdbClientCommand(config);
		return StringUtils.extractNameFromPath(fullCommand);
	}

	// ------------------------------------------------------------------------

	public static boolean getDoStartGdbServer(ILaunchConfiguration config) throws CoreException {

		return config.getAttribute(ConfigurationAttributes.DO_START_GDB_SERVER,
				DefaultPreferences.DO_START_GDB_SERVER_DEFAULT);
	}

	public static boolean getDoAddServerConsole(ILaunchConfiguration config) throws CoreException {

		return getDoStartGdbServer(config)
				&& config.getAttribute(ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE,
						DefaultPreferences.DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT);
	}

    // ------------------------------------------------------------------------
    public static String getSysveiwStartCommand(ILaunchConfiguration config) {

		
		String executable = null;

		try {
//			GnuArmFinalLaunchSequence.getCurrentConfig();
            
            String pro_file = config.getAttribute(ConfigurationAttributes.SYSVIEW_PRO_CPU_FILE, DefaultPreferences.SYSVIEW_PRO_CPU_FILE_DEFAULT).trim();
            String app_file = config.getAttribute(ConfigurationAttributes.SYSVIEW_APP_CPU_FILE, DefaultPreferences.SYSVIEW_APP_CPU_FILE_DEFAULT).trim();

            String pool_period = config.getAttribute(ConfigurationAttributes.SYSVIEW_POOL_PERIOD, DefaultPreferences.SYSVIEW_POOL_PERIOD_DEFAULT).trim();
            String trace_size = config.getAttribute(ConfigurationAttributes.SYSVIEW_TRACE_SIZE, DefaultPreferences.SYSVIEW_TRACE_SIZE_DEFAULT).trim();
            String stop_tmo = config.getAttribute(ConfigurationAttributes.SYSVIEW_STOP_TMO, DefaultPreferences.SYSVIEW_STOP_TMO_DEFAULT).trim();

            pro_file = pro_file.replace('\\', '/');
            executable =  "file://" + pro_file + " ";
            app_file = app_file.replace('\\', '/');
            executable += "file://" + app_file + " ";
            executable += pool_period + " ";
            executable += trace_size + " ";
            executable += stop_tmo + " ";
		} catch (CoreException e) {
			Activator.log(e);
			return null;
		}

		return executable;
	}
    
    public static String getSysveiwStartCommand() {
    
    	ILaunchConfiguration config = GnuArmFinalLaunchSequence.getCurrentConfig();
    	return getSysveiwStartCommand(config);
    }


    // ------ Stsrem View


}
