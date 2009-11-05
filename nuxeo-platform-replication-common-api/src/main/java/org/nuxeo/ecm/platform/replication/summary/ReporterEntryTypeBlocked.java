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
 * The entry marking the choice of not importing the particular type of
 * documents. The type is stored.
 *
 * @author rux
 *
 */
public class ReporterEntryTypeBlocked extends ReporterEntry {

    public String blockedType;

    public static final String TYPE_BLOCKED_KEY = "typeBlocked";

    public ReporterEntryTypeBlocked(String documentId, String documentName,
            String documentPath, String typeString) {
        super(documentId, documentName, documentPath);
        this.blockedType = typeString;
    }

    public ReporterEntryTypeBlocked() {
    }

    @Override
    public String getRepresentation() {
        return "document " + getDocumentIdentifier() + " of type: "
                + blockedType;
    }

}
