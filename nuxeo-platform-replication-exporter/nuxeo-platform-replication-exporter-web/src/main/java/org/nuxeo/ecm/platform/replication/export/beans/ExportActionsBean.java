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
package org.nuxeo.ecm.platform.replication.export.beans;

import java.io.File;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.platform.replication.common.StatusListener;
import org.nuxeo.ecm.platform.replication.exporter.DocumentaryBaseExporterService;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.nuxeo.runtime.api.Framework;

/**
 * Export action bean
 * 
 * @author cpriceputu@nuxeo.com
 * 
 */
@Scope(ScopeType.SESSION)
@Name("exportActions")
public class ExportActionsBean implements Serializable, StatusListener {

    private static final Logger log = Logger.getLogger(ExportActionsBean.class);

    private static final long serialVersionUID = 1L;

    @In(create = true, required = false)
    private transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    @In(create = true)
    protected transient ResourcesAccessor resourcesAccessor;

    private long startTime = 0;

    private long endTime = 0;

    private DocumentaryBaseExporterService exportService = null;

    private String repo;

    private long fileCount = 0;

    private long oldFileCount = 0;

    private boolean done = false;

    private String path = null;

    @Create
    public void initialize() throws Exception {
        exportService = Framework.getService(DocumentaryBaseExporterService.class);
        exportService.setListener(this);
    }

    public String startExport() throws ClientException {
        setDone(false);
        setFileCount(0);
        exportService.export(documentManager.getRepositoryName(), null,
                new File(getPath()), false, false, false);
        return null;
    }

    public void onUpdateStatus(Object... params) {
        if ((Integer) params[0] == StatusListener.DOC_PROCESS_SUCCESS) {
            if (params[1] instanceof ExportedDocument[]) {
                fileCount += ((ExportedDocument[]) params[1]).length;
            } else {
                fileCount++;
            }
            endTime = System.currentTimeMillis();
            long time = Math.abs(endTime - startTime) / 1000;
            time = time != 0 ? time : 1;
            if (endTime - startTime > 10000) {
                log.info("Documents Exported: " + fileCount);
                log.info("Docs/sec : " + ((fileCount - oldFileCount) / time));
                endTime = startTime;
                oldFileCount = fileCount;
            }
        } else if ((Integer) params[0] == StatusListener.DONE) {
            setDone(true);
            endTime = System.currentTimeMillis();
            long time = Math.abs(endTime - startTime) / 1000;
            time = time != 0 ? time : 1;
            log.info("Export completed.");
            log.info("Documents Exported: " + fileCount);
            log.info("Docs/sec : " + ((fileCount - oldFileCount) / time));
        } else if ((Integer) params[0] == StatusListener.STARTED) {
            startTime = System.currentTimeMillis();
            fileCount = 0;
            oldFileCount = 1;
        }
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getRepo() {
        return repo;
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

    public long getFileCount() {
        return fileCount;
    }

    public boolean getDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
