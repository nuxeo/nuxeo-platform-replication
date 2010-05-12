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
package org.nuxeo.ecm.platform.replication.summary;

/**
 * The entry marking a unrecoverable error in the document structure.
 *
 * @author rux
 */
public class ReporterEntryDocumentStructure extends ReporterEntry {

    public static final String DOCUMENT_STRUCTURE_KEY = "documentStructure";

    public ReporterEntryDocumentStructure(String documentId,
            String documentName, String documentPath) {
        super(documentId, documentName, documentPath);
    }

    public ReporterEntryDocumentStructure() {
    }

    @Override
    public String getRepresentation() {
        return "document " + getDocumentIdentifier();
    }

}
