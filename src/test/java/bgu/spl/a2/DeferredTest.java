package bgu.spl.a2;

import static org.junit.Assert.*;


public class DeferredTest {
    private Deferred<Object>  deferredTester;
    private Object resultTester;
    private boolean runnablePerformed = false;

    private class runTester implements Runnable{

        @Override
        public void run() {
            runnablePerformed = true;
        }
    }
    @org.junit.Before
    public void setUp() throws Exception {
        deferredTester = new Deferred<>();
        resultTester = new Object();
    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @org.junit.Test(expected = IllegalStateException.class)
    public void getBeforeResolved() throws Exception {
        // Check if getting value before resolve
        deferredTester.get();
    }

     @org.junit.Test
     public void getAfterResolved() throws Exception {
        //check if getting value after resolve
        deferredTester.resolve(resultTester);
        assertEquals(deferredTester.get(),resultTester);
        assertTrue(deferredTester.isResolved());
    }

    @org.junit.Test
    public void isResolvedBefore() throws Exception {

        //Check if false before resolved.
        assertFalse(deferredTester.isResolved());
    }

    @org.junit.Test
    public void isResolvedAfter() throws Exception {
        //Check if true after resolved
        deferredTester.resolve(resultTester);
        assertTrue(deferredTester.isResolved());

    }
    @org.junit.Test
    public void resolve() throws Exception {
        //Check if
        deferredTester.resolve(resultTester);
        assertTrue(deferredTester.isResolved());
        assertEquals(resultTester, deferredTester.get());
    }
    @org.junit.Test(expected = IllegalStateException.class)
    public void resolveTwice() throws Exception {
        deferredTester.resolve(resultTester);
        deferredTester.resolve(resultTester);
    }

    @org.junit.Test
    public void whenResolvedBefore() throws Exception {
        runTester callBack = new runTester();
        deferredTester.whenResolved(callBack);
        assertFalse(runnablePerformed);
    }

    @org.junit.Test
    public void whenResolvedAfter() throws Exception{
        runTester callBack = new runTester();
        deferredTester.resolve(resultTester);
        deferredTester.whenResolved(callBack);
        assertTrue(runnablePerformed);
    }
}