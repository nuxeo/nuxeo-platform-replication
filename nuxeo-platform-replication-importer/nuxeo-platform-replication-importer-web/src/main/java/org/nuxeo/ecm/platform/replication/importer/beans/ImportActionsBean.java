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

import java.io.File;
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.platform.replication.common.StatusListener;
import org.nuxeo.ecm.platform.replication.importer.DefaultDocumentTypeSelector;
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

    private static final Log log = LogFactory.getLog(ImportActionsBean.class);

    private static final long serialVersionUID = 1L;

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
            importService.setTypeSelector(new DefaultDocumentTypeSelector());
            importService.setListener(this);
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
        done = false;
        fileCount = 0;
        importService.importDocuments(null, new File(path), true, true, true,
                useMultiThread, true);
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
            done = true;
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
