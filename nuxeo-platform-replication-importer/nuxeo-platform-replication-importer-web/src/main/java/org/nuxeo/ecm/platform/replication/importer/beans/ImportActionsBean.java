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
package org.nuxeo.ecm.platform.replication.importer.beans;

import static org.nuxeo.ecm.platform.replication.common.ReplicationConstants.*;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.EventContextImpl;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.platform.replication.common.StatusListener;
import org.nuxeo.ecm.platform.replication.importer.DocumentaryBaseImporterService;
import org.nuxeo.runtime.api.Framework;

/**
 * Seam component used to perform the import of documents in the process of
 * replication.
 * 
 * @author btatar
 * 
 */
@Scope(ScopeType.SESSION)
@Name("importActions")
public class ImportActionsBean implements Serializable, StatusListener {

    private static final Logger log = Logger.getLogger(ImportActionsBean.class);

    private static final long serialVersionUID = 1L;

    @In(create = true, required = false)
    private transient CoreSession documentManager;

    // returns the number of imported documents
    private int fileCount;

    // returns the replication import process status
    private boolean done;

    // the path where the source of the replication status is located
    private String path;

    // used to specify whether multi thread is used
    private boolean useMultiThread;

    private DocumentaryBaseImporterService importService;

    @Create
    public void initialize() throws Exception {
        try {
            importService = Framework.getService(DocumentaryBaseImporterService.class);
        } catch (Exception e) {
            log.debug("Could not initialize the import service ...");
        }
    }

    /**
     * Performs the replication import process.
     * 
     * @return
     * @throws ClientException
     */
    public String startImport() throws Exception {
        log.debug("Starting replication import process...");
        setDone(false);
        setFileCount(0);
        Map<String, Serializable> options = new HashMap<String, Serializable>();
        options.put(REPLICATION_IMPORT_PATH, path);
        options.put(REPLICATION_IMPORT_USE_MULTI_THREAD, useMultiThread);
        // options.put(IMPORT_LISTENER, this);
//         fireEvent(START_REPLICATION_IMPORT_PROCESS, options);
        importService.importDocuments(documentManager, null, new File(path),
                true, true, true, useMultiThread);
        return null;
    }

    /**
     * Updates the replication import process status.
     */
    public void onUpdateStatus(Object... params) {
        if ((Integer) params[0] == StatusListener.DOC_PROCESS_SUCCESS) {
            if (params[1] instanceof ExportedDocument[]) {
                fileCount += ((ExportedDocument[]) params[1]).length;
            } else {
                fileCount++;
            }

        } else if ((Integer) params[0] == StatusListener.DONE) {
            setDone(true);
        }

    }

    private void fireEvent(String eventName, Map<String, Serializable> options)
            throws Exception {

        EventProducer producer = Framework.getService(EventProducer.class);

        if (producer != null) {
            EventContext context = new EventContextImpl(null, options);
            context.setCoreSession(documentManager);
            Event event = context.newEvent(eventName);
            try {
                event.setIsCommitEvent(true);
                event.setInline(true);
                producer.fireEvent(event);
            } catch (ClientException ce) {
                log.error("EventProducer.fireEvent(event); FAILED", ce);
                throw ce;
            }
        }
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    public int getFileCount() {
        return fileCount;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isUseMultiThread() {
        return useMultiThread;
    }

    public void setUseMultiThread(boolean useMultiThread) {
        this.useMultiThread = useMultiThread;
    }
}
