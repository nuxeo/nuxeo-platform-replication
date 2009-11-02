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

package org.nuxeo.ecm.platform.replication.exporter.reporter;

import java.util.List;

import org.apache.log4j.Logger;
import org.nuxeo.ecm.platform.replication.summary.Reporter;
import org.nuxeo.ecm.platform.replication.summary.ReporterEntry;
import org.nuxeo.ecm.platform.replication.summary.ReporterEntryDocumentStructure;
import org.nuxeo.ecm.platform.replication.summary.ReporterEntryMissingBlob;
import org.nuxeo.ecm.platform.replication.summary.ReporterEntryMissingLiveDocument;
import org.nuxeo.ecm.platform.replication.summary.ReporterEntryMissingVersion;
import org.nuxeo.ecm.platform.replication.summary.ReporterEntryUnknownError;

/**
 * Singleton to report the export summary.
 *
 * @author rux
 *
 */
public class ExporterReporter extends Reporter {

    private static final Logger log = Logger.getLogger(ExporterReporter.class);

    private static Reporter reporter = null;

    private ExporterReporter() {
    }

    public static Reporter getInstance() {
        if (reporter == null) {
            reporter = new ExporterReporter();
        }
        return reporter;
    }

    @Override
    public void dumpLog() {
        log.info("Summary of export action");
        log.info(getDocumentNumber() + " documents attempted to export");

        List<ReporterEntry> list = getEntries().get(
                ReporterEntryUnknownError.UNKNOWN_ERROR_KEY);
        int numberOfThem = 0;
        if (list != null) {
            numberOfThem = list.size();
        }
        if (numberOfThem > 0) {
            log.info("  " + numberOfThem + " documents yields unexpected error.");
            log.info("  Their status is undefined from the exporter perspective.");
            for (ReporterEntry entry : list) {
                log.info("    " + entry.getRepresentation());
            }
            numberOfThem = 0;
        }

        list = getEntries().get(
                ReporterEntryDocumentStructure.DOCUMENT_STRUCTURE_KEY);
        if (list != null) {
            numberOfThem = list.size();
        }
        if (numberOfThem > 0) {
            log.info("  " + numberOfThem + " documents are compromised.");
            log.info("  They couldn't be exported. Check log for more details.");
            for (ReporterEntry entry : list) {
                log.info("    " + entry.getRepresentation());
            }
            numberOfThem = 0;
        }

        list = getEntries().get(
                ReporterEntryMissingVersion.MISSING_VERSION_KEY);
        if (list != null) {
            numberOfThem = list.size();
        }
        if (numberOfThem > 0) {
            log.info("  " + numberOfThem + " documents are missing a version.");
            log.info("  They are still available for import with no versions attached.");
            for (ReporterEntry entry : list) {
                log.info("     " + entry.getRepresentation());
            }
            numberOfThem = 0;
        }

        list = getEntries().get(
                ReporterEntryMissingLiveDocument.MISSING_LIVEDOC_KEY);
        if (list != null) {
            numberOfThem = list.size();
        }
        if (numberOfThem > 0) {
            log.info("  " + numberOfThem + " versions are orphans.");
            log.info("  They are still available for import with no live document attached.");
            for (ReporterEntry entry : list) {
                log.info("     " + entry.getRepresentation());
            }
            numberOfThem = 0;
        }

        list = getEntries().get(ReporterEntryMissingBlob.MISSING_BLOB_KEY);
        if (list != null) {
            numberOfThem = list.size();
        }
        if (numberOfThem > 0) {
            log.info("  " + numberOfThem + " documents are missing a blob file.");
            log.info("  Still they are available for import with a fake blob file instead.");
            for (ReporterEntry entry : list) {
                log.info("    " + entry.getRepresentation());
            }
            numberOfThem = 0;
        }
    }

}
