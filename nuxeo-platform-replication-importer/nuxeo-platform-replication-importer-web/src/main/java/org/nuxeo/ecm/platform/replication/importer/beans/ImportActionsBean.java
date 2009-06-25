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

import static org.nuxeo.ecm.platform.replication.common.ReplicationConstants.GO_HOME;

import java.io.File;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.platform.replication.common.StatusListener;
import org.nuxeo.ecm.platform.replication.importer.DocumentaryBaseImpServiceImpl;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
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

    @In(create = true)
    private transient NavigationContext navigationContext;

    @In(create = true, required = false)
    private transient CoreSession documentManager;

    private DocumentaryBaseImpServiceImpl importService;

    // returns the number of imported documents
    private int fileCount;

    // returns the replication import process status
    private boolean done;

    // the path where the source of the replication status is located
    private String path;

    @Create
    public void initialize() throws Exception {
        importService = Framework.getService(DocumentaryBaseImpServiceImpl.class);
        importService.setListener(this);
    }

    /**
     * Performs the replication import process.
     * 
     * @return
     * @throws ClientException
     */
    public String startImport() throws ClientException {
        log.debug("Starting replication import process...");
        setDone(false);
        setFileCount(0);
        importService.importDocuments(documentManager, null, new File(path),
                true, true, true);
        return goHome();
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

    /**
     * Utility method used to return the home path.
     * 
     * @return
     */
    private String goHome() {

        DocumentModel root;
        try {
            root = documentManager.getDocument(new PathRef("/"));
            navigationContext.setCurrentDocument(root);
            return navigationContext.navigateToDocument(root);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return GO_HOME;
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

    public boolean getDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
