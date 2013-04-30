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

package org.nuxeo.ecm.platform.replication.importer.reporter;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.platform.replication.summary.Reporter;
import org.nuxeo.ecm.platform.replication.summary.ReporterEntry;
import org.nuxeo.ecm.platform.replication.summary.ReporterEntryACLFailed;
import org.nuxeo.ecm.platform.replication.summary.ReporterEntryDocumentImport;
import org.nuxeo.ecm.platform.replication.summary.ReporterEntryDocumentStructure;
import org.nuxeo.ecm.platform.replication.summary.ReporterEntryFailUpdate;
import org.nuxeo.ecm.platform.replication.summary.ReporterEntryTypeBlocked;
import org.nuxeo.ecm.platform.replication.summary.ReporterEntryUnknownError;

/**
 * Singleton to report the import summary.
 *
 * @author rux
 *
 */
public class ImporterReporter extends Reporter {

    private static final Log log = LogFactory.getLog(ImporterReporter.class);

    private static Reporter reporter;

    private ImporterReporter() {
    }

    public static Reporter getInstance() {
        if (reporter == null) {
            reporter = new ImporterReporter();
        }
        return reporter;
    }

    @Override
    public void dumpLog() {
        log.info("Summary of import action");
        log.info(getDocumentNumber() + " documents attempted to import");
        log.info(getTimeVelocity());

        List<ReporterEntry> entries = getEntries().get(
                ReporterEntryUnknownError.UNKNOWN_ERROR_KEY);
        int numberOfThem = 0;
        if (entries != null) {
            numberOfThem = entries.size();
        }
        boolean successful = true;
        if (numberOfThem > 0) {
            log.info("  " + numberOfThem + " documents yields unexpected error.");
            log.info("  Their status is undefined from the importer perspective.");
            for (ReporterEntry entry : entries) {
                log.info("    " + entry.getRepresentation());
            }
            numberOfThem = 0;
            successful = false;
        }

        entries = getEntries().get(
                ReporterEntryDocumentStructure.DOCUMENT_STRUCTURE_KEY);
        if (entries != null) {
            numberOfThem = entries.size();
        }
        if (numberOfThem > 0) {
            log.info("  " + numberOfThem + " documents' XML structure are compromised.");
            log.info("  They are imported but as empty documents - including the title.");
            for (ReporterEntry entry : entries) {
                log.info("    " + entry.getRepresentation());
            }
            numberOfThem = 0;
            successful = false;
        }

        entries = getEntries().get(
                ReporterEntryDocumentImport.DOCUMENT_IMPORT_KEY);
        if (entries != null) {
            numberOfThem = entries.size();
        }
        if (numberOfThem > 0) {
            log.info("  " + numberOfThem + " documents failed to be cloned in repository.");
            log.info("  They couldn't be imported. Check log for more details.");
            for (ReporterEntry entry : entries) {
                log.info("    " + entry.getRepresentation());
            }
            numberOfThem = 0;
            successful = false;
        }

        entries = getEntries().get(
                ReporterEntryFailUpdate.FAIL_UPDATE_KEY);
        if (entries != null) {
            numberOfThem = entries.size();
        }
        if (numberOfThem > 0) {
            log.info("  for " + numberOfThem + " documents custom schema update failed.");
            log.info("  The documents are imported as they are, without any custom change.");
            for (ReporterEntry entry : entries) {
                log.info("    " + entry.getRepresentation());
            }
            numberOfThem = 0;
            successful = false;
        }

        entries = getEntries().get(
                ReporterEntryTypeBlocked.TYPE_BLOCKED_KEY);
        if (entries != null) {
            numberOfThem = entries.size();
        }
        if (numberOfThem > 0) {
            log.info("  " + numberOfThem + " documents were rejected based on the type selection.");
            log.info("  The documents are not imported.");
            for (ReporterEntry entry : entries) {
                log.info("    " + entry.getRepresentation());
            }
            numberOfThem = 0;
            successful = false;
        }

        entries = getEntries().get(
                ReporterEntryACLFailed.ACL_FAILED_KEY);
        if (entries != null) {
            numberOfThem = entries.size();
        }
        if (numberOfThem > 0) {
            log.info("  for " + numberOfThem + " documents failure to update the ACL system.");
            log.info("  The documents are imported and preserved with default security rights.");
            for (ReporterEntry entry : entries) {
                log.info("    " + entry.getRepresentation());
            }
            numberOfThem = 0;
            successful = false;
        }

        if (successful) {
            log.info("Operation completed with no errors recorded.");
        }
    }

}
