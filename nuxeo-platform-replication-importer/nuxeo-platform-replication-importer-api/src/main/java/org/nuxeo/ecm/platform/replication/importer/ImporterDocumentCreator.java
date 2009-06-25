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

import java.util.Collections;
import java.util.Properties;

import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;

/**
 * Core imports various types of documents. The document can't be updated, so it
 * has not exist before.
 * 
 * @author rux
 * 
 */
public class ImporterDocumentCreator {

    /**
     * Imports the document through core session.
     * 
     * @param session
     * @param type
     * @param id
     * @param name
     * @param parentPath
     * @param properties
     * @return
     * @throws ClientException
     */
    public static DocumentModel importUsualDocument(CoreSession session,
            String type, String id, String name, String parentPath,
            Properties properties) throws ClientException {
        DocumentModel document = new DocumentModelImpl((String) null, type, id,
                new Path(name), null, null, new PathRef(parentPath), null,
                null, null, session.getRepositoryName());

        document.putContextData(CoreSession.IMPORT_LOCK,
                properties.getProperty(CoreSession.IMPORT_LOCK));
        String value = properties.getProperty(CoreSession.IMPORT_CHECKED_IN);
        if (value != null) {
            document.putContextData(CoreSession.IMPORT_CHECKED_IN, new Boolean(
                    value));
        }
        value = properties.getProperty(CoreSession.IMPORT_BASE_VERSION_ID);
        if (value != null) {
            document.putContextData(CoreSession.IMPORT_BASE_VERSION_ID, value);
        }
        value = properties.getProperty(CoreSession.IMPORT_VERSION_MAJOR);
        if (value != null) {
            document.putContextData(CoreSession.IMPORT_VERSION_MAJOR,
                    Long.valueOf(value));
        }
        value = properties.getProperty(CoreSession.IMPORT_VERSION_MINOR);
        if (value != null) {
            document.putContextData(CoreSession.IMPORT_VERSION_MINOR,
                    Long.valueOf(value));
        }
        document.setPathInfo(parentPath, name);
        session.importDocuments(Collections.singletonList(document));
        session.save();
        return document;
    }
}
