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
 * Base class of entries logged in replication tool.
 *
 * @author rux
 */
public abstract class ReporterEntry {

    public String documentId;
    public String documentPath;
    public String documentName;

    protected ReporterEntry() {
    }

    protected ReporterEntry(String documentId, String documentName,
            String documentPath) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.documentPath = documentPath;
    }

    protected String getDocumentIdentifier() {
        if (documentPath != null) {
            return documentPath;
        } else if (documentName != null) {
            return documentName;
        } else if (documentId != null) {
            return documentId;
        } else {
            return "Unknown";
        }
    }

    public abstract String getRepresentation();

}
