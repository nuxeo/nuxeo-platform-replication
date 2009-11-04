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
 * The entry marking the error trying to retrieve the versions of the current
 * document (the one just to export).
 *
 * @author rux
 *
 */
public class ReporterEntryNoVersions extends ReporterEntry {

    public static final String NO_VERSIONS_KEY = "noVersions";

    public ReporterEntryNoVersions(String documentId, String documentName,
            String documentPath) {
        super(documentId, documentName, documentPath);
    }

    public ReporterEntryNoVersions() {
    }

    @Override
    public String getRepresentation() {
        return "for document " + getDocumentIdentifier();
    }

}
