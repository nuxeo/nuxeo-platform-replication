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
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 */
package org.nuxeo.ecm.platform.replication.importer.listener;

import static org.nuxeo.ecm.platform.replication.common.ReplicationConstants.IMPORT_LISTENER;
import static org.nuxeo.ecm.platform.replication.common.ReplicationConstants.REPLICATION_IMPORT_PATH;
import static org.nuxeo.ecm.platform.replication.common.ReplicationConstants.REPLICATION_IMPORT_USE_MULTI_THREAD;
import static org.nuxeo.ecm.platform.replication.common.ReplicationConstants.START_REPLICATION_IMPORT_PROCESS;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.platform.replication.common.StatusListener;
import org.nuxeo.ecm.platform.replication.importer.DocumentaryBaseImporterService;
import org.nuxeo.runtime.api.Framework;

/**
 * Listener used to start the replication import process asynchronous.
 *
 * @author btatar
 *
 */
public class ReplicationImportListener implements EventListener {

    private static final Log log = LogFactory.getLog(ReplicationImportListener.class);

    private DocumentaryBaseImporterService importService;

    public ReplicationImportListener() {
        try {
            importService = Framework.getService(DocumentaryBaseImporterService.class);
        } catch (Exception e) {
            log.debug("Could not initialize the import service ...");
        }
    }

    public void handleEvent(Event event) throws ClientException {
        if ((importService != null)
                && event.getName().equals(START_REPLICATION_IMPORT_PROCESS)) {
            log.debug("Starting the replication import process ...");
            EventContext context = event.getContext();
            String path = (String) context.getProperty(REPLICATION_IMPORT_PATH);
            boolean useMultiThread = (Boolean) context.getProperty(REPLICATION_IMPORT_USE_MULTI_THREAD);
            StatusListener listener = (StatusListener) context.getProperty(IMPORT_LISTENER);
            importService.setListener(listener);
            importService.importDocuments(null, new File(path), true, true,
                    true, useMultiThread, false);
        }
    }
}
