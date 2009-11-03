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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.core.io.impl.ExportedDocumentImpl;
import org.nuxeo.ecm.core.io.impl.plugins.DocumentModelReader;
import org.nuxeo.ecm.platform.replication.exporter.reporter.ExporterReporter;

/**
 * Reader extension. The iterated query doesn't work (see NXP-3814). Collecting
 * all the documents in a list leads to exhaustive memory consumption and in
 * some case session time out. The mechanism of providing documents is based on
 * session.getChildren() and retrieving the list of the versions for current
 * document. The logic is a little more cumbersome because of that.
 *
 * @author cpriceputu
 * @author rux
 *
 */
public class ReplicationReader extends DocumentModelReader {

    private static final Logger log = Logger.getLogger(ReplicationReader.class);

    /**
     * The usual document list. It goes through the documents base following the
     * tree. A list is used instead of an iterator to be able to collect the
     * documents on different levels of the tree. A recursive mechanism can't be
     * used because of the pipe mechanism. The list contains at one moment in
     * time: first the versions of the last usual document exported, then the
     * children of last usual document exported, then the previous accumulated
     * documents. Every exported document is removed from the list but still the
     * list can grow quite large when the document have lot of versions and the
     * branch holding the exported document has lots of children on many levels.
     */
    protected LinkedList<DocumentModel> exportDocuments = null;

    /**
     * The usual documents are retrieved using session.getChildren(). For each,
     * the versions are also retrieved. Initially, start with the root.
     *
     * @param session
     * @throws ClientException
     */
    protected ReplicationReader(CoreSession session) throws ClientException {
        super(session);
        exportDocuments = new LinkedList<DocumentModel>(
                Collections.singleton(session.getRootDocument()));
        log.info("Exporting all documents");
    }

    @Override
    public ExportedDocument read() throws IOException {
        if (exportDocuments.isEmpty()) {
            log.info("Exporting job finsihed!");
            return null;
        }
        // document to be exported
        DocumentModel document = exportDocuments.remove();
        // is the current document version or proxy?
        if (document.isProxy() || document.isVersion()) {
            // just export it
            return new ExportedDocumentImpl(document, inlineBlobs);
        }
        String documentLocation = document.getPathAsString();
        try {
            // otherwise it is usual document: see if has versions and children
            DocumentModelList children = session.getChildren(document.getRef());
            if (children != null && !children.isEmpty()) {
                // add the children in front: exhaust a branch as soon as
                // possible rather than accumulating it
                exportDocuments.addAll(0, children);
                if (log.isDebugEnabled()) {
                    log.debug(">>>>>>>>>" + exportDocuments.size()
                            + " documents in list after adding children of "
                            + documentLocation);
                }
            }
        } catch (Exception e) {
            log.error("Couldn't retrieve children for " + documentLocation
                    + ", skipping them.", e);
            ExporterReporter.getInstance().logNoChildren(documentLocation);
        }
        try {
            List<DocumentModel> versions = session.getVersions(document.getRef());
            if (!versions.isEmpty()) {
                // add the versions in front: don't need to keep them, just
                // exhaust
                exportDocuments.addAll(0, versions);
                if (log.isDebugEnabled()) {
                    log.debug(">>>>>>>>>" + exportDocuments.size()
                            + " documents in list after adding versions of "
                            + documentLocation);
                }
            }
        } catch (Exception e) {
            log.error("Couldn't retrieve versions for " + documentLocation
                    + ", skipping them.", e);
            ExporterReporter.getInstance().logNoVersions(documentLocation);
        }
        return new ExportedDocumentImpl(document, inlineBlobs);
    }

}
