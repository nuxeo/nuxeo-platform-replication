package org.nuxeo.ecm.platform.replication.common;

public interface StatusListener {
    public static final int OK = 0;

    public static final int ERROR = 1;

    public static final int DOC_WRITE_SUCCESS = 2;

    public static final int EXPORT_STOPPED = 3;

    public static final int DONE = 4;

    public void onUpdateStatus(Object... params);
}
