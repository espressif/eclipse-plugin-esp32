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

package com.espressif.esp32.core;

import org.eclipse.ui.progress.UIJob;

public abstract class SystemUIJob extends UIJob {

	public SystemUIJob(String string) {
		super(string);
		this.setSystem(true);
	}
}
