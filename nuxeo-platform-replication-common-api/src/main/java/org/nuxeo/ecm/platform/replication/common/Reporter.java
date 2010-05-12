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

package org.nuxeo.ecm.platform.replication.common;

import org.nuxeo.ecm.core.api.ClientException;

/**
 * The manager for the tracking log. The process keeps the track of the
 * operations in a log file (export.log, import.log respectively) located in the
 * same level with the “Replication Root”. This log file is serving for 2
 * purposes: allows resume and offers progress information. The log files
 * contains:
 * <p>
 * a header with overall informations (server connected, time of start, time of
 * end, etc.)
 * <p>
 * a section for documentary base listing one by one:
 * <p>
 * current time
 * <p>
 * current index
 * <p>
 * the ID of documents processed
 * <p>
 * a section for every other types
 *
 * <p>
 * An instance of the reporter can be obtained through the factory, don't get
 * one directly through constructor.
 *
 * @author cpriceputu
 *
 */
public interface Reporter {

    /**
     * Writes an entry at the end of the file.
     *
     * @param documentId
     * @param blobsSize
     * @throws ClientException
     */
    void writeEntry(String documentId, int blobsSize) throws ClientException;

    /**
     * Writes the header at the begin of the file.
     *
     * @param documentaryScope
     * @param resume
     * @throws ClientException
     */
    void writeHeader(String documentaryScope, boolean resume)
            throws ClientException;

    /**
     * Reads the header.
     *
     * @return
     * @throws ClientException
     */
    String getHeader() throws ClientException;

    /**
     * Reads the last entry.
     *
     * @return
     * @throws ClientException
     */
    String getLastEntry() throws ClientException;

    /**
     * Retrieves the number of entries.
     *
     * @return
     * @throws ClientException
     */
    int getNumberOfDocuments() throws ClientException;

    /**
     * Computes the sum of the blob size.
     *
     * @return
     * @throws ClientException
     */
    long getTotalSize() throws ClientException;

}
