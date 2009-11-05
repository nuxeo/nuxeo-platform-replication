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
 * Choice of importing or not a particular type of documents. In order to use it
 * just set the custom implementation before running import
 * {@link DocumentaryBaseImporterService}.
 *
 * @author rux
 *
 */
public interface DocumentTypeSelector {

    /**
     * Sees if the document type is acceptable to be imported.
     *
     * @param documentType the type of document to import
     * @return true if the document is granted to be imported, false otherwise
     */
    boolean accept(String documentType);
}
