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

package org.nuxeo.ecm.platform.replication.importer;

import static org.nuxeo.ecm.platform.replication.common.ReplicationConstants.DOCUMENTARY_BASE_LOCATION_NAME;
import static org.nuxeo.ecm.platform.replication.common.ReplicationConstants.USUAL_DOCUMENTS_LOCATION_NAME;
import static org.nuxeo.ecm.platform.replication.common.ReplicationConstants.VERSIONS_LOCATION_NAME;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.importer.base.GenericMultiThreadedImporter;
import org.nuxeo.ecm.platform.importer.executor.AbstractImporterExecutor;
import org.nuxeo.ecm.platform.importer.filter.EventServiceConfiguratorFilter;
import org.nuxeo.ecm.platform.importer.filter.ImporterFilter;
import org.nuxeo.ecm.platform.replication.common.StatusListener;

/**
 * Implementation for import documentary base service.
 * 
 * @author rux
 * 
 */
public class DocumentaryBaseImpServiceImpl extends AbstractImporterExecutor
        implements DocumentaryBaseImporterService {
    private static final Log log = LogFactory.getLog(DocumentaryBaseImpServiceImpl.class);

    private StatusListener listener;

    private DocumentXmlTransformer xmlTransformer;

    public DocumentXmlTransformer getXmlTransformer() {
        return xmlTransformer;
    }

    public void setXmlTransformer(DocumentXmlTransformer xmlTransformer) {
        this.xmlTransformer = xmlTransformer;
    }

    public void importDocuments(Map<String, Serializable> parameter, File path,
            boolean resume, boolean exportVersions, boolean exportProxies,
            boolean useMultiThread) throws ClientException {
        log.info("Starting import. First, usual documents...");
        // we need to import the documentary base in order: usual documents,
        // versions, proxies
        File usualDocumentsRoot = new File(path.getPath() + File.separator
                + DOCUMENTARY_BASE_LOCATION_NAME + File.separator
                + USUAL_DOCUMENTS_LOCATION_NAME);
        doSynchronImport(usualDocumentsRoot, false, useMultiThread);

        File versionsRoot = new File(path.getPath() + File.separator
                + DOCUMENTARY_BASE_LOCATION_NAME + File.separator
                + VERSIONS_LOCATION_NAME);
        if (versionsRoot.exists()) {
            log.info("Second, version documents...");
            doSynchronImport(versionsRoot, false, useMultiThread);
        }

        File proxiesRoot = new File(path.getPath() + File.separator
                + DOCUMENTARY_BASE_LOCATION_NAME + File.separator
                + USUAL_DOCUMENTS_LOCATION_NAME);
        if (proxiesRoot.exists()) {
            log.info("Third, proxies documents...");
            doSynchronImport(proxiesRoot, true, useMultiThread);
        }
        if (listener != null) {
            listener.onUpdateStatus(StatusListener.DONE);
        }
    }

    protected void doSynchronImport(File root, boolean importProxies,
            boolean useMultiThread) throws ClientException {
        try {
            ReplicationSourceNode sourceNode = new ReplicationSourceNode(root);
            GenericMultiThreadedImporter importer = new GenericMultiThreadedImporter(
                    sourceNode, "/", 10, 5, getLogger());
            ReplicationDocumentModelFactory documentModelFactory = new ReplicationDocumentModelFactory(
                    listener, importProxies);
            // here is set the transformer
            documentModelFactory.setDocumentXmlTransformer(xmlTransformer);
            importer.setFactory(documentModelFactory);
            if (useMultiThread) {
                importer.setThreadPolicy(getThreadPolicy());
            } else {
                importer.setThreadPolicy(new MonoThreadPolicy());
            }
            ImporterFilter filter = new EventServiceConfiguratorFilter(true,
                    true, true, false);
            importer.addFilter(filter);
            doRun(importer, Boolean.TRUE);
        } catch (Exception e) {
            throw new ClientException(e);
        }
    }

    @Override
    protected Log getJavaLogger() {
        return log;
    }

    public void setListener(StatusListener listener) {
        this.listener = listener;
    }

    public void stop() {
        // TODO Auto-generated method stub

    }
}
