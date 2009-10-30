package org.nuxeo.ecm.platform.replication.exporter.reporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Reporter {

    public static Reporter reporter = null;

    private List<ReporterEntry> entries = null;

    private Reporter() {

    }

    public void clear() {
        getEntries().clear();
    }

    public List<ReporterEntry> getEntries() {
        if (entries == null) {
            entries = Collections.synchronizedList(new ArrayList<ReporterEntry>());
        }
        return entries;
    }

    public static final Reporter getReporter() {
        if (reporter == null) {
            reporter = new Reporter();
        }

        return reporter;
    }

    public void log(String documentId, String documentName, String blobName) {

        ReporterEntry entry = new ReporterEntry(documentId, blobName,
                documentName);
        getEntries().add(entry);
    }

    public String getReportAsString() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        toStream(bos);
        bos.close();

        return bos.toString();
    }

    public void toStream(OutputStream os) throws IOException {
        os.write("Failed blobs -------------------".getBytes());
        os.write(System.getProperty("line.separator").getBytes());
        os.write(System.getProperty("line.separator").getBytes());

        for (ReporterEntry re : getEntries()) {
            os.write(re.getDocumentId().getBytes());
            os.write(" --> ".getBytes());
            os.write(re.getDocumentName().getBytes());
            os.write(" --> ".getBytes());
            os.write(re.getBlobName().getBytes());
            os.write(System.getProperty("line.separator").getBytes());
        }

        os.flush();
    }
}
