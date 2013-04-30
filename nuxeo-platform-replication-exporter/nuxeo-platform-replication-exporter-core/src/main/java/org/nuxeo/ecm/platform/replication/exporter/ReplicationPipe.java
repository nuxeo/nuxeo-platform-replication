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
 * Contributors:
 *     Nuxeo - initial API and implementation
 */
package org.nuxeo.ecm.platform.replication.exporter;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.io.DocumentTranslationMap;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.core.io.impl.DocumentPipeImpl;
import org.nuxeo.ecm.core.io.impl.DocumentTranslationMapImpl;
import org.nuxeo.ecm.platform.replication.common.StatusListener;

/**
 * Extension of basic DocumentPipeImpl to allow multithreading
 *
 * @author cpriceputu@nuxeo.com
 *
 */
public class ReplicationPipe extends DocumentPipeImpl {
    private static final Log log = LogFactory.getLog(ReplicationPipe.class);

    private StatusListener listener;

    protected int pageSize = 0;

    private boolean running = true;

    public ReplicationPipe(int pageSize) {
        super(pageSize);
        this.pageSize = pageSize;
    }

    public ReplicationPipe() {
        this(0);
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public DocumentTranslationMap run() throws Exception {

        if (getReader() == null) {
            throw new IllegalArgumentException("Pipe reader cannot be null");
        }
        if (getWriter() == null) {
            throw new IllegalArgumentException("Pipe writer cannot be null");
        }

        running = true;
        if (listener != null) {
            listener.onUpdateStatus(StatusListener.STARTED);
        }
        List<DocumentTranslationMap> maps = new Vector<DocumentTranslationMap>();
        readAndWriteDocs(maps);
        try {
            DocumentTranslationMap map = DocumentTranslationMapImpl.merge(maps);
            return map;
        } catch (Exception e) {
            log.warn("Error in pipe: ", e);
        }

        return null;
    }

    protected void readAndWriteDocs(List<DocumentTranslationMap> maps)
            throws IOException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(pageSize < 1 ? 1
                : pageSize);
        if (pageSize == 0) {
            // handle single doc case
            ExportedDocument doc = null;
            while (running) {
                synchronized (this) {
                    doc = getReader().read();
                    if (doc == null) {
                        break;
                    }
                }
                Runner r = new Runner(this, doc, maps);
                r.setListener(listener);
                executor.execute(new Runner(this, doc, maps));
            }
        } else {
            // handle multiple doc case
            ExportedDocument[] docs = null;
            while (running) {
                synchronized (this) {
                    docs = getReader().read(pageSize);
                }
                if (docs == null) {
                    break;
                }
                Runner r = new MultipleRunner(this, docs, maps);
                r.setListener(listener);
                executor.execute(r);
            }
        }

        executor.shutdown();
        boolean done = executor.awaitTermination(1, TimeUnit.SECONDS);
        while (!done) {
            if (!running) {
                executor.shutdownNow();
                break;
            }
            done = executor.awaitTermination(1, TimeUnit.SECONDS);
        }
        log.info("Multithread export finished...");
    }

    public void setListener(StatusListener listener) {
        this.listener = listener;
    }

    public StatusListener getListener() {
        return listener;
    }
}

class MultipleRunner extends Runner {

    private static final Log log = LogFactory.getLog(MultipleRunner.class);

    private ExportedDocument[] docs;

    public MultipleRunner(ReplicationPipe pipe, ExportedDocument[] docs,
            List<DocumentTranslationMap> maps) {
        super(pipe, null, maps);
        this.docs = docs;
    }

    public ExportedDocument[] getDocs() {
        return docs;
    }

    public void setDocs(ExportedDocument[] docs) {
        this.docs = docs;
    }

    @Override
    public void run() {
        if (!getPipe().isRunning()) {
            sendStatus(StatusListener.PROCESS_STOPPED);
            return;
        }
        try {
            if (docs.length != 0) {
                getPipe().applyTransforms(docs);
                DocumentTranslationMap map = getPipe().getWriter().write(docs);
                if (map != null) {
                    getMaps().add(map);
                }

                sendStatus(StatusListener.DOC_PROCESS_SUCCESS, docs);
            }
        } catch (Exception e) {
            log.error(e);
            sendStatus(StatusListener.ERROR, e);
        }

    }

}

class Runner implements Runnable {

    private ReplicationPipe pipe;

    private ExportedDocument doc;

    private List<DocumentTranslationMap> maps;

    private StatusListener listener;

    private static final Log log = LogFactory.getLog(Runner.class);

    public Runner(ReplicationPipe pipe, ExportedDocument doc,
            List<DocumentTranslationMap> maps) {
        this.pipe = pipe;
        this.doc = doc;
        this.maps = maps;
    }

    public void run() {
        if (!pipe.isRunning()) {
            sendStatus(StatusListener.PROCESS_STOPPED);
            return;
        }
        try {
            pipe.applyTransforms(doc);
            DocumentTranslationMap map = pipe.getWriter().write(doc);
            maps.add(map);
            sendStatus(StatusListener.DOC_PROCESS_SUCCESS, doc);
        } catch (Exception e) {
            log.error(e);
            sendStatus(StatusListener.ERROR, e);
        }
    }

    public void setPipe(ReplicationPipe pipe) {
        this.pipe = pipe;
    }

    public ReplicationPipe getPipe() {
        return pipe;
    }

    public void setDoc(ExportedDocument doc) {
        this.doc = doc;
    }

    public ExportedDocument getDoc() {
        return doc;
    }

    public void setMaps(List<DocumentTranslationMap> maps) {
        this.maps = maps;
    }

    public List<DocumentTranslationMap> getMaps() {
        return maps;
    }

    public void setListener(StatusListener listener) {
        this.listener = listener;
    }

    public StatusListener getListener() {
        return listener;
    }

    protected void sendStatus(Object... params) {
        if (listener != null) {
            listener.onUpdateStatus(params);
        }
    }

}
