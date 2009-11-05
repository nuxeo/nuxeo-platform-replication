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
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 */

package org.nuxeo.ecm.platform.replication.importer;

import java.io.IOException;
import java.util.Collections;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.io.DocumentTranslationMap;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.core.io.impl.AbstractDocumentModelWriter;

/**
 * A writer which is updating the already existing document obtained in
 * replication import process. The most important: it uses the core import
 * method instead session.save() for documents with no ref, like versions.
 *
 * @author btatar
 *
 */
public class ReplicationDocumentModelWriter extends AbstractDocumentModelWriter {

    private DocumentModel document;

    public ReplicationDocumentModelWriter(CoreSession session,
            DocumentModel document, int saveInterval) {
        super(session, "/", saveInterval);
        this.document = document;
    }

    @Override
    public DocumentTranslationMap write(ExportedDocument xdoc)
            throws IOException {
        try {
            if (document.getRef() == null) {
                loadSchemas(xdoc, document, xdoc.getDocument());
                session.importDocuments(Collections.singletonList(document));
                unsavedDocuments += 1;
                saveIfNeeded();
            } else {
                updateDocument(xdoc, document);
            }
        } catch (ClientException e) {
            IOException ioe = new IOException(
                    "Failed to import document in repository: "
                            + e.getMessage());
            throw ioe;
        }
        return null;
    }

}
