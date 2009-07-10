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

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.runtime.api.Framework;

/**
 * MBean implementation.
 *
 * @author cpriceputu
 *
 */
public class Exporter implements ExporterMBean {

    private DocumentaryBaseExporterService service = null;

    public Exporter() throws Exception {
    }

    public void export(String domain, File path) throws ClientException {
        Thread.currentThread().setContextClassLoader(
                Framework.class.getClassLoader());
        getExportService().export(domain, null, path, false, false, false);
    }

    public void stop() {
        service.stop();
    }

    protected DocumentaryBaseExporterService getExportService()
            throws ClientException {
        if (service == null) {
            try {
                service = Framework
                        .getService(DocumentaryBaseExporterService.class);
            } catch (Exception e) {
                throw new ClientException("Error getting the ExporterService",
                        e);
            }
        }
        if (service == null) {
            // avoid ugly NPE
            throw new ClientException("Error getting the ExporterService: null");
        }
        return service;
    }

}
