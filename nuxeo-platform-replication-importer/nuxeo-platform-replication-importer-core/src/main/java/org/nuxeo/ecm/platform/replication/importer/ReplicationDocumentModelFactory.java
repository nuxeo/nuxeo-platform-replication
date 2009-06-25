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

import static org.nuxeo.ecm.platform.replication.common.ReplicationConstants.METADATA_FILE_NAME;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.core.io.impl.ExportedDocumentImpl;
import org.nuxeo.ecm.platform.importer.factories.ImporterDocumentModelFactory;
import org.nuxeo.ecm.platform.importer.source.SourceNode;
import org.nuxeo.ecm.platform.replication.common.StatusListener;

/**
 * Implements the document model factory as for replication needs. It core
 * imports document and sets the document properties.
 * 
 * @author rux
 * 
 */
public class ReplicationDocumentModelFactory implements
        ImporterDocumentModelFactory {

    private static final Log log = LogFactory.getLog(ReplicationDocumentModelFactory.class);

    protected CoreSession session;

    protected DocumentModel parent;

    protected SourceNode fileNode;

    protected boolean importProxies;

    protected StatusListener listener;

    protected DocumentXmlTransformer xmlTransformer;

    public ReplicationDocumentModelFactory(StatusListener listener,
            boolean importProxies) {
        this.listener = listener;
        this.importProxies = importProxies;
        xmlTransformer = null;
    }

    public DocumentModel createFolderishNode(CoreSession session,
            DocumentModel parent, SourceNode node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public DocumentModel createLeafNode(CoreSession session,
            DocumentModel parent, SourceNode node) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isTargetDocumentModelFolderish(SourceNode node) {
        // TODO Auto-generated method stub
        return false;
    }

    protected DocumentModel importDocument() throws ClientException {
        ExportedDocument xdoc = new ExportedDocumentImpl();
        try {
            xdoc.setDocument(ImporterDocumentCreator.loadXML(new File(
                    fileNode.getName() + File.separator + "document.xml")));
        } catch (ClientException ce) {
            log.warn("Didn't find xml for" + fileNode.getName());
            return null;
        }
        Properties properties = getPropertiesFile();
        if (properties.isEmpty()) {
            log.warn("Didn't find metadata for " + fileNode.getName());
            return null;
        }
        boolean isProxy = ImporterDocumentCreator.isProxy(properties);
        if ((importProxies && !isProxy) || (!importProxies && isProxy)) {
            // not to import
            return null;
        }
        // offer a chance to transform the document properties
        if (xmlTransformer != null) {
            try {
                Document transformedDocument = xmlTransformer.transform(xdoc.getDocument());
                if (transformedDocument != null) {
                    xdoc.setDocument(transformedDocument);
                }
            } catch (ClientException ce) {
                // don't crash in customized code
                log.warn("Transformation failed", ce);
            }
        }
        // create document
        DocumentModel documentModel = coreImportDocument(xdoc, properties);
        sendStatus(StatusListener.DOC_PROCESS_SUCCESS, documentModel);
        // update document properties
        return documentModel;
    }

    protected DocumentModel coreImportDocument(ExportedDocument doc,
            Properties properties) throws ClientException {
        // hack to obtain the name of the document: get the file path; later get
        // the name
        File currentDocumentFile = new File(fileNode.getName());
        return ImporterDocumentCreator.importDocument(session, doc.getType(),
                doc.getId(), currentDocumentFile.getName(),
                parent.getPathAsString(), properties);
    }

    /**
     * Utility method used to retrieve the .properties file from a source
     * node.This file contains data of a document that will be used in the
     * process of import.
     */
    private Properties getPropertiesFile() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(fileNode.getName()
                    + File.separator + METADATA_FILE_NAME));
        } catch (IOException e) {
            log.debug(String.format("Problems retrieving the %s file",
                    fileNode.getName() + METADATA_FILE_NAME));
        }
        return properties;
    }

    public void setListener(StatusListener listener) {
        this.listener = listener;
    }

    public StatusListener getListener() {
        return listener;
    }

    protected void sendStatus(Object... params) {
        if (listener != null) {
            listener.onUpdateStatus(params);
        }
    }

    public void setDocumentXmlTransformer(DocumentXmlTransformer transformer) {
        xmlTransformer = transformer;
    }

}
