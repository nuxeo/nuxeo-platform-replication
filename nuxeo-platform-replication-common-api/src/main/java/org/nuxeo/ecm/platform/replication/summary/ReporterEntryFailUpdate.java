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
 * The entry marking error in custom processing of the XML structure. It stores
 * the error message, if any.
 *
 * @author rux
 */
public class ReporterEntryFailUpdate extends ReporterEntry {

    public String errorMessage;

    public static final String FAIL_UPDATE_KEY = "failUpdate";

    public ReporterEntryFailUpdate(String documentId, String documentName,
            String documentPath, String errorMessage) {
        super(documentId, documentName, documentPath);
        this.errorMessage = errorMessage;
    }

    public ReporterEntryFailUpdate() {
    }

    @Override
    public String getRepresentation() {
        return "document " + getDocumentIdentifier()
                + " with the error message: " + errorMessage;
    }

}
