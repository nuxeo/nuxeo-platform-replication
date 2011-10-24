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

import org.nuxeo.ecm.core.api.ClientException;

/**
 * The MBean implementation.
 *
 * @author btatar
 *
 */
public interface DocumentaryBaseImpServiceMBean {

    /**
     * Default import.
     *
     * @param pathFile
     * @throws ClientException
     */
    void importDocuments(String pathFile) throws ClientException;

    /**
     * Import with resuming option. If a document to import already exists in
     * the repository, it will be ignored.
     *
     * @param pathFile
     * @param resume true to resume. false will run a normal import.
     * @throws ClientException
     */
    void resumeDocumentImport(String pathFile, boolean resume)
            throws ClientException;

}
