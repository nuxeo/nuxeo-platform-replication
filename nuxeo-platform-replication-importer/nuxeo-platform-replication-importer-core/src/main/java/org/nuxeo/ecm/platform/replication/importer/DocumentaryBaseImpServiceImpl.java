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
import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.importer.base.GenericMultiThreadedImporter;
import org.nuxeo.ecm.platform.importer.executor.AbstractImporterExecutor;
import org.nuxeo.ecm.platform.importer.filter.EventServiceConfiguratorFilter;
import org.nuxeo.ecm.platform.importer.filter.ImporterFilter;
import org.nuxeo.ecm.platform.replication.common.StatusListener;
import org.nuxeo.ecm.platform.replication.importer.reporter.ImporterReporter;
import org.nuxeo.runtime.api.Framework;

/**
 * Implementation for import documentary base service.
 *
 * @author rux
 *
 */
public class DocumentaryBaseImpServiceImpl extends AbstractImporterExecutor
        implements DocumentaryBaseImpServiceImplMBean {
    private static final Log log = LogFactory.getLog(DocumentaryBaseImpServiceImpl.class);

    private StatusListener listener;

    private DocumentXmlTransformer xmlTransformer;

    private DocumentTypeSelector typeSelector;

    private ImportRunner runner;

    public DocumentTypeSelector getTypeSelector() {
        return typeSelector;
    }

    public void setTypeSelector(DocumentTypeSelector typeSelector) {
        this.typeSelector = typeSelector;
    }

    public DocumentXmlTransformer getXmlTransformer() {
        return xmlTransformer;
    }

    public void setXmlTransformer(DocumentXmlTransformer xmlTransformer) {
        this.xmlTransformer = xmlTransformer;
    }

    public void importDocuments(Map<String, Serializable> parameter, File path,
            boolean resume, boolean exportVersions, boolean exportProxies,
            boolean useMultiThread, boolean asynchronous)
            throws ClientException {
        ImporterReporter.getInstance().clear();
        runner = new ImportRunner(parameter, path, resume, exportVersions,
                exportProxies, useMultiThread, this, listener);
        if (asynchronous) {
            runner.start();
        } else {
            runner.run();
        }
    }

    public void doSynchronImport(File root, boolean importProxies,
            boolean useMultiThread) throws ClientException {
        try {
            ReplicationSourceNode sourceNode = new ReplicationSourceNode(root);
            GenericMultiThreadedImporter importer = new GenericMultiThreadedImporter(
                    sourceNode, "/", 10, 5, getLogger());
            ReplicationDocumentModelFactory documentModelFactory = new ReplicationDocumentModelFactory(
                    listener, importProxies);
            // here is set the transformer
            documentModelFactory.setDocumentXmlTransformer(xmlTransformer);
            documentModelFactory.setDocumentTypeSelector(typeSelector);
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

    }

    public void importDocuments(File path) throws ClientException {
        Thread.currentThread().setContextClassLoader(
                Framework.class.getClassLoader());
        typeSelector = new DefaultDocumentTypeSelector();
        importDocuments(null, path, true, true, true, false, true);
    }
}

class ImportRunner implements Runnable {

    private static Logger LOG = Logger.getLogger(ImportRunner.class);

    private Map<String, Serializable> parameter;

    private File path;

    private boolean resume;

    private boolean exportVersions;

    private boolean exportProxies;

    private boolean useMultiThread;

    private DocumentaryBaseImpServiceImpl service;

    private StatusListener listener;

    public ImportRunner(Map<String, Serializable> parameter, File path,
            boolean resume, boolean exportVersions, boolean exportProxies,
            boolean useMultiThread, DocumentaryBaseImpServiceImpl service,
            StatusListener listener) {
        setParameter(parameter);
        setPath(path);
        setResume(resume);
        setExportVersions(exportVersions);
        setExportProxies(exportProxies);
        setUseMultiThread(useMultiThread);

        setService(service);
        setListener(listener);
    }

    public void start() {
        new Thread(this).start();
    }

    public void run() {
        try {
            LOG.info("Starting import. First, usual documents...");
            // we need to import the documentary base in order: usual documents,
            // versions, proxies
            File usualDocumentsRoot = new File(path.getPath() + File.separator
                    + DOCUMENTARY_BASE_LOCATION_NAME + File.separator
                    + USUAL_DOCUMENTS_LOCATION_NAME);
            service.doSynchronImport(usualDocumentsRoot, false, useMultiThread);

            File versionsRoot = new File(path.getPath() + File.separator
                    + DOCUMENTARY_BASE_LOCATION_NAME + File.separator
                    + VERSIONS_LOCATION_NAME);
            if (versionsRoot.exists()) {
                LOG.info("Second, version documents...");
                service.doSynchronImport(versionsRoot, false, useMultiThread);
            }

            File proxiesRoot = new File(path.getPath() + File.separator
                    + DOCUMENTARY_BASE_LOCATION_NAME + File.separator
                    + USUAL_DOCUMENTS_LOCATION_NAME);
            if (proxiesRoot.exists()) {
                LOG.info("Third, proxies documents...");
                service.doSynchronImport(proxiesRoot, true, useMultiThread);
            }
            if (listener != null) {
                listener.onUpdateStatus(StatusListener.DONE);
            }
        } catch (ClientException e) {
            LOG.error("Error", e);
        } finally {
            ImporterReporter.getInstance().dumpLog();
        }
    }

    public void setParameter(Map<String, Serializable> parameter) {
        this.parameter = parameter;
    }

    public Map<String, Serializable> getParameter() {
        return parameter;
    }

    public void setPath(File path) {
        this.path = path;
    }

    public File getPath() {
        return path;
    }

    public void setResume(boolean resume) {
        this.resume = resume;
    }

    public boolean isResume() {
        return resume;
    }

    public void setExportVersions(boolean exportVersions) {
        this.exportVersions = exportVersions;
    }

    public boolean isExportVersions() {
        return exportVersions;
    }

    public void setExportProxies(boolean exportProxies) {
        this.exportProxies = exportProxies;
    }

    public boolean isExportProxies() {
        return exportProxies;
    }

    public void setUseMultiThread(boolean useMultiThread) {
        this.useMultiThread = useMultiThread;
    }

    public boolean isUseMultiThread() {
        return useMultiThread;
    }

    public void setListener(StatusListener listener) {
        this.listener = listener;
    }

    public StatusListener getListener() {
        return listener;
    }

    public DocumentaryBaseImpServiceImpl getService() {
        return service;
    }

    public void setService(DocumentaryBaseImpServiceImpl service) {
        this.service = service;
    }
}
