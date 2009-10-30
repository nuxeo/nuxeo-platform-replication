package org.nuxeo.ecm.platform.replication.exporter.reporter;

public class ReporterEntry {
    private String documentId;

    private String blobName;

    private String documentName;

    public ReporterEntry() {

    }

    public ReporterEntry(String documentId, String blobName, String documentName) {
        setDocumentId(documentId);
        setBlobName(blobName);
        setDocumentName(documentName);
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setBlobName(String blobName) {
        this.blobName = blobName;
    }

    public String getBlobName() {
        return blobName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentName() {
        return documentName;
    }
}
