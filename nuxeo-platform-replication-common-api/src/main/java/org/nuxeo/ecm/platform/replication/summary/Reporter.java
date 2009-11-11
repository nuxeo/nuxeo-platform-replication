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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.nuxeo.ecm.platform.replication.summary.ReporterEntry;

/**
 * The base class. This tool aims to summary the status of the replication
 * action. It is singleton to be called from different operations (import,
 * export), basically to count the number of documents in repository, to store
 * the items with errors and summary the situation in log. Being singleton, the
 * access needs to be synchronized. The main process creates, resets it and make
 * the summary. Every where needed, the workers register items.
 *
 * TODO: I feel that better wold be to move the immediate / overall velocity in
 * here instead letting it being implemented outside (in
 * GenericMultiThreadedImporter and DocumentaryBaseExpServiceImpl respectively),
 * but it is not a priority.
 *
 * @author rux
 *
 */
public abstract class Reporter {

    private Map<String, List<ReporterEntry>> entries = null;

    private int documentNumber;

    private long startTime = 0;

    protected Reporter() {
    }

    public void clear() {
        getEntries().clear();
        documentNumber = 0;
        startTime = System.currentTimeMillis();
    }

    /**
     * Gets the entries: map with the collected items organized on type of item.
     *
     * @return
     */
    public Map<String, List<ReporterEntry>> getEntries() {
        if (entries == null) {
            entries = new HashMap<String, List<ReporterEntry>>();
        }
        return entries;
    }

    /**
     * Stores an entry. The key is specific to type of entries.
     *
     * @param key
     * @param entry
     */
    public synchronized void log(String key, ReporterEntry entry) {
        getEntries();// ensure they are not null
        List<ReporterEntry> list = entries.get(key);
        if (list == null) {
            list = new ArrayList<ReporterEntry>();
            entries.put(key, list);
        }
        list.add(entry);
    }

    /**
     * Increment the processed document number.
     */
    public synchronized void incrementDocumentNumber() {
        documentNumber++;
    }

    protected int getDocumentNumber() {
        return documentNumber;
    }

    public void logDocumentStructure(String documentLocation) {
        ReporterEntryDocumentStructure reporterEntry = new ReporterEntryDocumentStructure();
        reporterEntry.documentPath = documentLocation;
        log(ReporterEntryDocumentStructure.DOCUMENT_STRUCTURE_KEY,
                reporterEntry);
    }

    public void logDocumentImport(String documentLocation, String cause) {
        ReporterEntryDocumentImport reporterEntry = new ReporterEntryDocumentImport();
        reporterEntry.documentPath = documentLocation;
        reporterEntry.cause = cause;
        log(ReporterEntryDocumentImport.DOCUMENT_IMPORT_KEY,
                reporterEntry);
    }

    public void logMissingBlob(String documentLocation, String blobLocation) {
        ReporterEntryMissingBlob reporterEntry = new ReporterEntryMissingBlob();
        reporterEntry.documentPath = documentLocation;
        reporterEntry.blobName = blobLocation;
        log(ReporterEntryMissingBlob.MISSING_BLOB_KEY, reporterEntry);
    }

    public void logMissingVersion(String documentLocation, String versionId) {
        ReporterEntryMissingVersion reporterEntry = new ReporterEntryMissingVersion();
        reporterEntry.documentPath = documentLocation;
        reporterEntry.versionName = versionId;
        log(ReporterEntryMissingVersion.MISSING_VERSION_KEY, reporterEntry);
    }

    public void logNoVersions(String documentLocation) {
        ReporterEntryNoVersions reporterEntry = new ReporterEntryNoVersions();
        reporterEntry.documentPath = documentLocation;
        log(ReporterEntryNoVersions.NO_VERSIONS_KEY, reporterEntry);
    }

    public void logNoChildren(String documentLocation) {
        ReporterEntryNoChildren reporterEntry = new ReporterEntryNoChildren();
        reporterEntry.documentPath = documentLocation;
        log(ReporterEntryNoChildren.NO_CHILDREN_KEY, reporterEntry);
    }

    public void logMissingLivedoc(String documentLocation) {
        ReporterEntryMissingLiveDocument reporterEntry = new ReporterEntryMissingLiveDocument();
        reporterEntry.documentPath = documentLocation;
        log(ReporterEntryMissingLiveDocument.MISSING_LIVEDOC_KEY, reporterEntry);
    }

    public void logUnknownError(String documentLocation, String errorMessage) {
        ReporterEntryUnknownError reporterEntry = new ReporterEntryUnknownError();
        reporterEntry.documentPath = documentLocation;
        reporterEntry.errorMessage = errorMessage;
        log(ReporterEntryUnknownError.UNKNOWN_ERROR_KEY, reporterEntry);
    }

    public void logFailUpdate(String documentLocation, String errorMessage) {
        ReporterEntryFailUpdate reporterEntry = new ReporterEntryFailUpdate();
        reporterEntry.documentPath = documentLocation;
        reporterEntry.errorMessage = errorMessage;
        log(ReporterEntryFailUpdate.FAIL_UPDATE_KEY, reporterEntry);
    }

    public void logTypeBlocked(String documentLocation, String typeString) {
        ReporterEntryTypeBlocked reporterEntry = new ReporterEntryTypeBlocked();
        reporterEntry.documentPath = documentLocation;
        reporterEntry.blockedType = typeString;
        log(ReporterEntryTypeBlocked.TYPE_BLOCKED_KEY, reporterEntry);
    }

    public void logACLFailed(String documentLocation) {
        ReporterEntryACLFailed reporterEntry = new ReporterEntryACLFailed();
        reporterEntry.documentPath = documentLocation;
        log(ReporterEntryACLFailed.ACL_FAILED_KEY, reporterEntry);
    }

    /**
     * Dumps the information into the log system.
     */
    public abstract void dumpLog();

    public String getTimeVelocity() {
        long endTime = System.currentTimeMillis();
        Period period = new Period(startTime, endTime);
        PeriodFormatter periodF = PeriodFormat.getDefault();
        String periodS = periodF.print(period);
        float seconds = ((float)(endTime - startTime)) / 1000 ;
        if (seconds <= 0) {
            //very unlikely, but being only log go easy
            seconds = 1;
        }
        float velocity = ((float) getDocumentNumber()) / seconds;
        return String.format("time elapsed: %s, velocity: %.2f",
                periodS, velocity);
    }
}
