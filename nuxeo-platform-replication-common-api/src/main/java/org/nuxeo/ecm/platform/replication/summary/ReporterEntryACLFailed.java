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
 * The entry marking failure to update the ACL on document.
 *
 * @author rux
 */
public class ReporterEntryACLFailed extends ReporterEntry {

    public static final String ACL_FAILED_KEY = "aclFailed";

    public ReporterEntryACLFailed(String documentId,
            String documentName, String documentPath, String cause) {
        super(documentId, documentName, documentPath);
    }

    public ReporterEntryACLFailed() {
    }

    @Override
    public String getRepresentation() {
        return "document " + getDocumentIdentifier();
    }

}
