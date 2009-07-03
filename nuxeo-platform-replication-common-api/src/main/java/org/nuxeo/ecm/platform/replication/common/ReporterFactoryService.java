/*
 * (C) Copyright 2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */

package org.nuxeo.ecm.platform.replication.common;

import java.io.File;

import org.nuxeo.ecm.core.api.ClientException;

/**
 * Factory of the Reporter. To be used to have the reporter instance working.
 * 
 * @author cpriceputu
 * 
 */
public interface ReporterFactoryService {

	/**
	 * Constructs the reporter instance based on the log file location.
	 * 
	 * @param location
	 * @return
	 * @throws ClientException
	 */
	public Reporter getReporter(File location) throws ClientException;
}
