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
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.io.DocumentPipe;
import org.nuxeo.ecm.core.io.DocumentTranslationMap;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.core.io.impl.DocumentPipeImpl;
import org.nuxeo.ecm.core.io.impl.plugins.DocumentTreeReader;
import org.nuxeo.ecm.core.io.impl.plugins.XMLDirectoryWriter;
import org.nuxeo.ecm.platform.replication.exporter.api.DocumentaryBaseExporterService;

/**
 * Implementation for export documentary base service.
 * 
 * @author cpriceputu
 * 
 */
public class DocumentaryBaseExpServiceImpl extends XMLDirectoryWriter implements
        DocumentaryBaseExporterService {
    private static final Logger LOG = Logger.getLogger(DocumentaryBaseExpServiceImpl.class);

    public DocumentaryBaseExpServiceImpl(File file) throws IOException {
        super(file);
    }

    @Override
    public DocumentTranslationMap write(ExportedDocument doc)
            throws IOException {
        LOG.info(doc);
        return super.write(doc);
    }

    public void export(CoreSession session,
            Map<String, Serializable> parameter, File path, boolean resume,
            boolean exportVersions, boolean exportProxies)
            throws ClientException {

        try {
            DocumentModel root = session.getDocument(new PathRef("/"));
            DocumentTreeReader reader = new DocumentTreeReader(session, root,
                    false);
            // ((DocumentModelReader)reader).setInlineBlobs(true);
            XMLDirectoryWriter writer = this;

            DocumentPipe pipe = new DocumentPipeImpl(10);
            pipe.setReader(reader);
            pipe.setWriter(writer);
            pipe.run();

        } catch (Exception e) {
            throw new ClientException(e);
        }
    }

}
