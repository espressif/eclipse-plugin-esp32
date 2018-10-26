/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *     Espressif Systems Ltd — ESP32 support
 *******************************************************************************/

package com.espressif.esp32.debug.gdbjtag.openocd;

import com.espressif.esp32.debug.gdbjtag.openocd.ui.Messages;

import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.IValueVariableInitializer;

public class VariableInitializer implements IValueVariableInitializer {

	// ------------------------------------------------------------------------

	public static final String VARIABLE_OPENOCD_EXECUTABLE = "openocd_executable";
	public static final String VARIABLE_OPENOCD_PATH = "openocd_path";
	public static final String VARIABLE_XTENSA_PATH = "xtensa_tools";
	public static final String VARIABLE_IDF_PATH = "idf_path";

	static final String UNDEFINED_PATH = "undefined_path";

	// ------------------------------------------------------------------------

	@Override
	public void initialize(IValueVariable variable) {

		String value;

		if (VARIABLE_OPENOCD_EXECUTABLE.equals(variable.getName())) {

			value = DefaultPreferences.getExecutableName();
			if (value == null) {
				value = DefaultPreferences.GDB_SERVER_EXECUTABLE_DEFAULT_NAME;
			}
			variable.setValue(value);
			variable.setDescription(Messages.Variable_executable_description);

		} else if (VARIABLE_OPENOCD_PATH.equals(variable.getName())) {

			value = DefaultPreferences.getInstallFolder();
			if (value == null) {
				value = UNDEFINED_PATH;
			}
			variable.setValue(value);
			variable.setDescription(Messages.Variable_path_description);

		}
		else if (VARIABLE_XTENSA_PATH.equals(variable.getName())) {

			value = DefaultPreferences.getXtensaPath();
			if (value == null) {
				value = UNDEFINED_PATH;
			}
			variable.setValue(value);
			variable.setDescription(Messages.Variable_xtensa_tools_description);

		}
		else if (VARIABLE_IDF_PATH.equals(variable.getName())) {

			value = DefaultPreferences.getIdfPath();
			if (value == null) {
				value = UNDEFINED_PATH;
			}
			variable.setValue(value);
			variable.setDescription(Messages.Variable_idf_path_description);

		}
	}

	// ------------------------------------------------------------------------
}
