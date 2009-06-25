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

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.importer.factories.ImporterDocumentModelFactory;
import org.nuxeo.ecm.platform.importer.source.SourceNode;

/**
 * Implements the document model factory as for replication needs. It core 
 * imports document and sets the document properties.
 * 
 * @author rux
 *
 */
public class ReplicationDocumentModelFactory implements
        ImporterDocumentModelFactory {

    protected CoreSession session;
    protected DocumentModel parent;
    protected SourceNode fileNode;
    
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
    
//    protected DocumentModel coreImportDocument() throws ClientException {
//        ImporterDocumentCreator.importUsualDocument(
//                session, type, id, name, parentPath, properties);
//    }

}
