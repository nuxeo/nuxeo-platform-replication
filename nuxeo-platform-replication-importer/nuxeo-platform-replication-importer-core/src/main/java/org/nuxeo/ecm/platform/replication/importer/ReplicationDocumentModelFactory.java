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
import static org.nuxeo.ecm.platform.replication.common.ReplicationConstants.VERSIONS_LOCATION_NAME;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.NXCore;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.impl.blob.StreamingBlob;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.impl.ACLImpl;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;
import org.nuxeo.ecm.core.io.DocumentWriter;
import org.nuxeo.ecm.core.io.ExportConstants;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.core.io.impl.ExportedDocumentImpl;
import org.nuxeo.ecm.core.model.NoSuchRepositoryException;
import org.nuxeo.ecm.core.model.Repository;
import org.nuxeo.ecm.core.storage.sql.RepositoryManagement;
import org.nuxeo.ecm.platform.importer.base.TxHelper;
import org.nuxeo.ecm.platform.importer.factories.ImporterDocumentModelFactory;
import org.nuxeo.ecm.platform.importer.source.SourceNode;
import org.nuxeo.ecm.platform.replication.common.StatusListener;
import org.nuxeo.runtime.services.streaming.FileSource;

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
        this.fileNode = node;
        this.session = session;
        log.info("importing folder " + node.getName());
        return importDocument();
    }

    public DocumentModel createLeafNode(CoreSession session,
            DocumentModel parent, SourceNode node) throws Exception {
        this.fileNode = node;
        this.session = session;
        log.info("importing leaf " + node.getName());
        return importDocument();
    }

    public boolean isTargetDocumentModelFolderish(SourceNode node) {
        return node.isFolderish();
    }

    protected void removeChildrenDepthFirst(DocumentRef ref)
            throws ClientException {
        DocumentModelList children = session.getChildren(ref);
        for (DocumentModel child : children) {
            removeChildrenDepthFirst(child.getRef());
            session.removeDocument(child.getRef());
        }
    }

    protected void removeChildrenLowLevel(DocumentRef ref)
            throws ClientException {
        session.removeChildren(ref);
        session.save();
        log.info("cleaned up Root before the import begins");
        TxHelper txHelper = new TxHelper();
        txHelper.grabCurrentTransaction(null);
        txHelper.commitOrRollbackTransaction();
        log.info("commited transaction before fushing cache");
        try {
            Repository repository = NXCore.getRepository(session.getRepositoryName());
            if (repository instanceof RepositoryManagement) {
                RepositoryManagement sqlRepoMng = (RepositoryManagement) repository;
                sqlRepoMng.clearCaches();
            } else {
                log.error("Can not clear cache since this is not a VCS repo");
                throw new ClientException("Can only import in a VCS repository");
            }
        } catch (NoSuchRepositoryException e) {
            log.warn(
                    "Error clearing SQL repo caches (may be normal in non JEE environment)",
                    e);
        }
        log.info("starts new transaction");
        txHelper.beginNewTransaction();
    }

    protected DocumentModel cleanUpRoot() throws ClientException {
        DocumentModel root = session.getRootDocument();
        removeChildrenDepthFirst(root.getRef());
        session.save();
        TxHelper txHelper = new TxHelper();
        txHelper.grabCurrentTransaction(null);
        txHelper.commitOrRollbackTransaction();
        log.info("starts new transaction");
        txHelper.beginNewTransaction();
        // removeChildrenLowLevel(root.getRef());
        return root;
    }

    protected DocumentModel importDocument() throws ClientException,
            IOException {
        if (fileNode.getName().endsWith(VERSIONS_LOCATION_NAME)) {
            return null;
        }
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
        DocumentModel documentModel = null;
        if (!xdoc.getType().equals("Root")) {
            xdoc.setPath(new Path(new File(fileNode.getName()).getName()));
            Path path = new Path(((Element) xdoc.getDocument().selectNodes(
                    "//system/path").get(0)).getText());
            path = path.removeLastSegments(1);
            documentModel = coreImportDocument(xdoc, path.toString(),
                    properties);
            loadSystemInfo(documentModel, xdoc.getDocument());
        } else {
            if (importProxies) {
                return session.getRootDocument();
            } else {
                return cleanUpRoot();
            }
        }

        sendStatus(StatusListener.DOC_PROCESS_SUCCESS, documentModel);
        // update document properties, basicaly set up the blobs and update

        DocumentWriter writer = new ReplicationDocumentModelWriter(session,
                documentModel, 1);

        File[] blobFiles = new File(fileNode.getName()).listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.contains(".blob");
            }

        });
        if (blobFiles.length > 0) {
            // set all the blobs
            for (File blobFile : blobFiles) {
                xdoc.putBlob(blobFile.getName(), new StreamingBlob(
                        new FileSource(blobFile)));
            }
        }
        writer.write(xdoc);

        return documentModel;
    }

    protected DocumentModel coreImportDocument(ExportedDocument doc,
            String parentPath, Properties properties) throws ClientException {
        // hack to obtain the name of the document: get the file path; later get
        // the name
        File currentDocumentFile = new File(fileNode.getName());
        return ImporterDocumentCreator.importDocument(session, doc.getType(),
                doc.getId(), currentDocumentFile.getName(), parentPath,
                properties);
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

    protected void loadSystemInfo(DocumentModel docModel, Document doc)
            throws ClientException {
        // how do I set the life cycle? would we set it?

        // TODO import security
        Element system = doc.getRootElement().element(
                ExportConstants.SYSTEM_TAG);
        Element accessControl = system.element(ExportConstants.ACCESS_CONTROL_TAG);
        if (docModel.getRef() == null || accessControl == null) {
            return;
        }
        Iterator<Element> it = accessControl.elementIterator(ExportConstants.ACL_TAG);
        while (it.hasNext()) {
            Element element = it.next();
            // import only the local acl
            if (ACL.LOCAL_ACL.equals(element.attributeValue(ExportConstants.NAME_ATTR))) {
                // this is the local ACL - import it
                List<Element> entries = element.elements();
                int size = entries.size();
                if (size > 0) {
                    ACP acp = new ACPImpl();
                    ACL acl = new ACLImpl(ACL.LOCAL_ACL);
                    acp.addACL(acl);
                    for (int i = 0; i < size; i++) {
                        Element el = entries.get(i);
                        String username = el.attributeValue(ExportConstants.PRINCIPAL_ATTR);
                        String permission = el.attributeValue(ExportConstants.PERMISSION_ATTR);
                        String grant = el.attributeValue(ExportConstants.GRANT_ATTR);
                        ACE ace = new ACE(username, permission,
                                Boolean.parseBoolean(grant));
                        acl.add(ace);
                    }
                    acp.addACL(acl);
                    session.setACP(docModel.getRef(), acp, false);
                }
            }

            if (ACL.INHERITED_ACL.equals(element.attributeValue(ExportConstants.NAME_ATTR))) {
                // this is the local ACL - import it
                List<Element> entries = element.elements();
                int size = entries.size();
                if (size > 0) {
                    ACP acp = new ACPImpl();
                    ACL acl = new ACLImpl(ACL.INHERITED_ACL);
                    acp.addACL(acl);
                    for (int i = 0; i < size; i++) {
                        Element el = entries.get(i);
                        String username = el.attributeValue(ExportConstants.PRINCIPAL_ATTR);
                        String permission = el.attributeValue(ExportConstants.PERMISSION_ATTR);
                        String grant = el.attributeValue(ExportConstants.GRANT_ATTR);
                        ACE ace = new ACE(username, permission,
                                Boolean.parseBoolean(grant));
                        acl.add(ace);
                    }
                    acp.addACL(acl);
                    session.setACP(docModel.getRef(), acp, false);
                }
            }
        }
    }

}