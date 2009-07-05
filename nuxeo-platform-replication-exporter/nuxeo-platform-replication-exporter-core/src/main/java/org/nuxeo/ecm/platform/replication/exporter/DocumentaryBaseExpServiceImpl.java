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
import org.nuxeo.ecm.platform.replication.common.StatusListener;

/**
 * Implementation for export documentary base service.
 * 
 * @author cpriceputu
 * 
 */
public class DocumentaryBaseExpServiceImpl // extends ServiceMBeanSupport
        implements DocumentaryBaseExpServiceImplMBean, Runnable {
    private static final Logger log = Logger.getLogger(DocumentaryBaseExpServiceImpl.class);

    private String domain = null;

    private UnrestrictedExporter exp = null;

    private StatusListener listener = null;

    private File path = null;

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
}
