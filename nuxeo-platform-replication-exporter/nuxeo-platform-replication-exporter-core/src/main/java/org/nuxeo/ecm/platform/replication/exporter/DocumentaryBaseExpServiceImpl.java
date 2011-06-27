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
        implements DocumentaryBaseExporterServiceMBean, DocumentaryBaseExporterService, Runnable, StatusListener {
    private static final Logger log = Logger.getLogger(DocumentaryBaseExpServiceImpl.class);

    private String domain;

    private UnrestrictedExporter exp;

    private StatusListener listener;

    private File path;

    private long startTime = 0;

    private long lastTime = 0;

    private long fileCount = 0;

    private long lastFileCount = 0;

    private boolean done = false;

    public void export(String domain, Map<String, Serializable> parameter,
            File path, boolean resume, boolean exportVersions,
            boolean exportProxies) throws ClientException {
        this.domain = domain;
        this.path = path;
        log.info("Starting export of " + domain + " to "
                + path.getAbsolutePath());
        new Thread(this).start();
    }

    public void run() {
        stop();
        try {
            done = false;
            fileCount = 0;
            startTime = System.currentTimeMillis();
            lastFileCount = 0;
            lastTime = startTime;
            exp = new UnrestrictedExporter(domain, path);
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

    public void export(String domain, String path, boolean showLog)
            throws ClientException {
        Thread.currentThread().setContextClassLoader(
                Framework.class.getClassLoader());

        listener = this;
        export(domain, null, new File(path), true, true, true);
    }

    public void onUpdateStatus(Object... params) {
        if ((Integer) params[0] == StatusListener.DOC_PROCESS_SUCCESS) {
            if (params[1] instanceof ExportedDocument[]) {
                fileCount += ((ExportedDocument[]) params[1]).length;
            } else {
                fileCount++;
            }
            long currentTime = System.currentTimeMillis();
            long time = (currentTime - lastTime) / 1000;
            if (time > 10) {
                float currentSpeed = ((float)(fileCount - lastFileCount)) / time;
                float overallSpeed = ((float)fileCount) / ((currentTime - startTime) / 1000);
                String aMessage = String.format("Exported %d documents at the rate %.2f", fileCount, overallSpeed);
                log.info(aMessage);
                aMessage = String.format("The immediate speed is: %.2f", currentSpeed);
                log.info(aMessage);
                lastTime = currentTime;
                lastFileCount = fileCount;
            }
        } else if ((Integer) params[0] == StatusListener.DONE) {
            done = true;
            //reporter takes care logging results, don't twice
        } else if ((Integer) params[0] == StatusListener.STARTED) {
            startTime = System.currentTimeMillis();
            fileCount = 0;
            lastFileCount = 0;
            lastTime = startTime;
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
