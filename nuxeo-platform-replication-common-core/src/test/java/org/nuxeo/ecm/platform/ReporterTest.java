package org.nuxeo.ecm.platform;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.replication.common.Reporter;
import org.nuxeo.ecm.platform.replication.common.ReporterFactory;
import org.nuxeo.ecm.platform.replication.common.ReporterFactoryService;

public class ReporterTest extends TestCase {

    private static final Logger LOG = Logger.getLogger(ReporterTest.class);

    static {
        BasicConfigurator.configure();
    }

    public static void main(String[] args) throws Exception {
        new ReporterTest().testFileChannel();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testSingleFileChannel() throws Exception {
        ReporterFactoryService rfs = new ReporterFactory();
        Reporter rp = rfs.getReporter(new File(System.getProperty("user.home"),
                "teste.synchro"));

        rp.writeEntry(UUID.randomUUID().toString(), 3000);

        LOG.info(rp.getLastEntry());
        LOG.info("Size of documents: " + rp.getTotalSize());

        // new File(System.getProperty("user.home"), "teste.synchro").delete();
    }

    public void testFileChannel() throws Exception {
        Executor exex = Executors.newCachedThreadPool();
        for (int i = 0; i < 130; i++) {
            exex.execute(new Runner());
        }
    }

    @Override
    protected void tearDown() throws Exception {
        // new File(System.getProperty("user.home"), "teste.synchro").delete();

        super.tearDown();
    }
}

class Runner implements Runnable {
    private static ReporterFactoryService rfs = new ReporterFactory();

    private static final Logger LOG = Logger.getLogger(Runner.class);

    public Runner() {

    }

    public void run() {
        try {
            Reporter rp = rfs.getReporter(new File(
                    System.getProperty("user.home"), "teste.synchro"));
            rp.writeEntry(UUID.randomUUID().toString(),
                    (int) System.currentTimeMillis());
        } catch (ClientException e) {
            LOG.error(e);
        }
    }

}
