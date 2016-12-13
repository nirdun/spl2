package bgu.spl.a2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class VersionMonitorTest {
    private VersionMonitor vmTester;
    private boolean runnablePerformed = false;

    @Before
    public void setUp() throws Exception {
        vmTester = new VersionMonitor();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getVersion() throws Exception {
        int initialVer = vmTester.getVersion();
        for (int i = 0; i < 10; i++) {
            vmTester.inc();
        }
        assertEquals(initialVer + 10, vmTester.getVersion());
    }

    @Test
    public void inc() throws Exception {
        int initialVer = vmTester.getVersion();
        for (int i = 0; i < 18; i++) {
            vmTester.inc();
        }
        assertEquals(initialVer + 18, vmTester.getVersion());
    }

    @Test
    public void await() throws Exception {
        int initialVerNum = vmTester.getVersion();
        Thread t = new Thread(() -> {
            try {
                vmTester.await(initialVerNum);
                runnablePerformed = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        );
        t.start();
        vmTester.inc();
        try{
            t.join();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        assertTrue(runnablePerformed);
    }

}