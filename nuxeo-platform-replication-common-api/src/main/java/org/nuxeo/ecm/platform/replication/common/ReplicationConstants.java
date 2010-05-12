/*
 * (C) Copyright 2009 Nuxeo SA (http://nuxeo.com/) and contributors.
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

/**
 * Utility class used for registering constants.
 *
 * @author btatar
 */
public class ReplicationConstants {

    private ReplicationConstants() {
    }

    public static final String METADATA_FILE_NAME = "metadata.properties";

    public static final String DOCUMENT_FILE_NAME = "document.xml";

    public static final String DOCUMENTARY_BASE_LOCATION_NAME = "Documentary Base";

    public static final String USUAL_DOCUMENTS_LOCATION_NAME = "Usual documents";

    public static final String VERSIONS_LOCATION_NAME = "Versions";

    public static final String PROXIES_LOCATION_NAME = "Proxies";

    public static final String GO_HOME = "home";

    public static final String START_REPLICATION_IMPORT_PROCESS = "StartReplicationImportProcess";

    public static final String REPLICATION_IMPORT_PATH = "path";

    public static final String IMPORT_LISTENER = "importListener";

    public static final String REPLICATION_IMPORT_USE_MULTI_THREAD = "useMultiThread";

}
