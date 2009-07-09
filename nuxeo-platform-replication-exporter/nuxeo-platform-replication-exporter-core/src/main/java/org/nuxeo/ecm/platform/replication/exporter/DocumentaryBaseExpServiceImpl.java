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

package org.nuxeo.ecm.platform.replication.exporter;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.platform.replication.common.StatusListener;
import org.nuxeo.runtime.api.Framework;

/**
 * Implementation for export documentary base service.
 *
 * @author cpriceputu
 *
 */
public class DocumentaryBaseExpServiceImpl // extends ServiceMBeanSupport
        implements DocumentaryBaseExpServiceImplMBean, Runnable, StatusListener {
    private static final Logger log = Logger.getLogger(DocumentaryBaseExpServiceImpl.class);

    private String domain = null;

    private UnrestrictedExporter exp = null;

    private StatusListener listener = null;

    private File path = null;

    private long startTime = 0;

    private long endTime = 0;

    private long fileCount = 0;

    private long oldFileCount = 0;

    private boolean done = false;

    public DocumentaryBaseExpServiceImpl() {
    }

    public void export(String domain, Map<String, Serializable> parameter,
            File path, boolean resume, boolean exportVersions,
            boolean exportProxies) throws ClientException {
        setDomain(domain);
        setPath(path);
        log.info("Starting export of " + domain + " to "
                + path.getAbsolutePath());
        new Thread(this).start();
    }

    public void run() {
        stop();
        try {
            setDone(false);
            setFileCount(0);

            exp = new UnrestrictedExporter(domain, getPath());
            exp.setListener(listener);
            exp.runUnrestricted();

        } catch (Exception e) {
            log.error("Error exporting: ", e);
        }
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }

    public void stop() {
        if (exp != null) {
            exp.stop();
            exp = null;
        }
    }

    public void setListener(StatusListener listener) {
        this.listener = listener;
    }

    public void setPath(File path) {
        this.path = path;
    }

    public File getPath() {
        return path;
    }

    public void export(String domain, File path, boolean showLog)
            throws ClientException {
        Thread.currentThread().setContextClassLoader(
                Framework.class.getClassLoader());

        setListener(this);
        export(domain, null, path, true, true, true);
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

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isDone() {
        return done;
    }

    public long getFileCount() {
        return fileCount;
    }

    public void setFileCount(long fileCount) {
        this.fileCount = fileCount;
    }
}
