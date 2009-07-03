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
 */
package org.nuxeo.ecm.platform.replication.exporter;

import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.core.io.impl.ExportedDocumentImpl;
import org.nuxeo.ecm.core.io.impl.plugins.DocumentModelReader;

/**
 * Reader extension.
 * 
 * @author cpriceputu
 * 
 */
public class ReplicationReader extends DocumentModelReader {

    private static final Logger LOG = Logger.getLogger(ReplicationReader.class);

    protected Iterator<DocumentModel> iterator = null;

    /**
     * The documents are retrieved using a session query. The iterated query is
     * not working see NXP-3814. So the documents are collected in memory. When
     * the iterated query will be available the memory exhaustion can be
     * avoided.
     * 
     * @param session
     * @throws ClientException
     */
    protected ReplicationReader(CoreSession session) throws ClientException {
        super(session);
        DocumentModelList list = session.query("SELECT * FROM Document");
        list.add(session.getRootDocument());
        LOG.info("Exporting " + list.size() + " documents");
        iterator = list.iterator();
    }

    @Override
    public ExportedDocument read() throws IOException {
        if (iterator.hasNext()) {
            DocumentModel docModel = iterator.next();
            return new ExportedDocumentImpl(docModel, inlineBlobs);

        }
        return null;
    }

}
