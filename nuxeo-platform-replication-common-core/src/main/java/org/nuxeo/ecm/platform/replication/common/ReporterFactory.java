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
import java.util.Hashtable;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;

/**
 * Factory of reporter implementation.
 * 
 * @author cpriceputu
 * 
 */
public class ReporterFactory implements ReporterFactoryService {

    private final Map<File, Reporter> loggers = new Hashtable<File, Reporter>();

    /**
     * Constructs a reporter for each file. Returns the same one for all
     * requests to identical file.
     */
    public Reporter getReporter(File location) throws ClientException {
        Reporter logger = loggers.get(location);
        if (logger == null) {
            logger = new NXReporter(location);
            loggers.put(location, logger);
        }
        return logger;
    }

}
