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

package org.nuxeo.ecm.platform.replication.importer;

/**
 * Defaults the selection of documents, type based, to some
 *
 * @author rux
 *
 */
public class DefaultDocumentTypeSelector implements DocumentTypeSelector {

    static final String[] deniedTypes = { "UserDataRoot" };

    public boolean accept(String documentType) {
        if (documentType == null) {
            // null type, do not import
            return false;
        }
        for (String denyType : deniedTypes) {
            if (denyType.equals(documentType)) {
                return false;
            }
        }
        return true;
    }

}
