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
 * The entry marking the missing blob error. It contains, besides the abstract
 * base, the blob file name.
 *
 * @author rux
 *
 */
public class ReporterEntryMissingBlob extends ReporterEntry {
    /**
     * The blob name: file name on HDD.
     */
    public String blobName;

    public static final String MISSING_BLOB_KEY = "missingBlob";

    public ReporterEntryMissingBlob(String documentId, String documentName,
            String documentPath, String blobName) {
        super(documentId, documentName, documentPath);
        this.blobName = blobName;
    }

    public ReporterEntryMissingBlob() {
    }

    @Override
    public String getRepresentation() {
        return blobName + " for document " + getDocumentIdentifier();
    }

}
