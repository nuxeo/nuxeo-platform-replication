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

package org.nuxeo.ecm.platform.replication.exporter.core;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.replication.exporter.api.DocumentaryBaseExporterService;

/**
 * Implementation for export documentary base service.
 *
 * @author cpriceputu
 *
 */
public class DocumentaryBaseExpServiceImpl implements
        DocumentaryBaseExporterService {
    private static final Logger LOG = Logger.getLogger(DocumentaryBaseExpServiceImpl.class);

    public DocumentaryBaseExpServiceImpl() {

    }

    public void export(String domain, Map<String, Serializable> parameter,
            File path, boolean resume, boolean exportVersions,
            boolean exportProxies) throws ClientException {
        UnrestrictedExporter exp = new UnrestrictedExporter(domain);
        exp.runUnrestricted();
    }

}
