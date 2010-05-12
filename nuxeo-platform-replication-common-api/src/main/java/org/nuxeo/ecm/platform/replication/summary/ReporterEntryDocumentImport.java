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
 * The entry marking a unrecoverable error when core importing. An error message
 * is stored.
 *
 * @author rux
 */
public class ReporterEntryDocumentImport extends ReporterEntry {

    public static final String DOCUMENT_IMPORT_KEY = "documentImport";

    public String cause;

    public ReporterEntryDocumentImport(String documentId, String documentName,
            String documentPath, String cause) {
        super(documentId, documentName, documentPath);
        this.cause = cause;
    }

    public ReporterEntryDocumentImport() {
    }

    @Override
    public String getRepresentation() {
        return "document " + getDocumentIdentifier() + " because: " + cause;
    }

}
