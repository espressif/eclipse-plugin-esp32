/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *     Espressif Systems Ltd — ESP32 support
 *******************************************************************************/

package com.espressif.esp32.packs.core.data;

public interface IPacksDataManagerFactory {

	// Create a DataManager object
	public IPacksDataManager createDataManager();

	public void dispose();

}
