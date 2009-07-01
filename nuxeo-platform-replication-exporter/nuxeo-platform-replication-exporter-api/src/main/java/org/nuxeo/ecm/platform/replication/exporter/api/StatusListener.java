package org.nuxeo.ecm.platform.replication.exporter.api;

public interface StatusListener {
    public static final int OK = 0;

    public static final int ERROR = 1;

    public static final int DOC_PROCESS_SUCCESS = 2;

    public static final int PROCESS_STOPPED = 3;

    public static final int DONE = 4;

    public void onUpdateStatus(Object... params);
}
