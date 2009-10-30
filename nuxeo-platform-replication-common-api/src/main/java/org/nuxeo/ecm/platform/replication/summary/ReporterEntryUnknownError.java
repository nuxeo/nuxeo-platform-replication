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
 * The entry marking an unknown error in processing. The state of document is
 * undefined. It stores the error message.
 *
 * @author rux
 *
 */
public class ReporterEntryUnknownError extends ReporterEntry {
    public String errorMessage;

    public static final String UNKNOWN_ERROR_KEY = "unknownError";

    public ReporterEntryUnknownError(String documentId, String documentName,
            String documentPath, String errorMessage) {
        super(documentId, documentName, documentPath);
        this.errorMessage = errorMessage;
    }

    public ReporterEntryUnknownError() {
    }

    @Override
    public String getRepresentation() {
        return "document " + getDocumentIdentifier() + " yields error: "
                + errorMessage;
    }

}
