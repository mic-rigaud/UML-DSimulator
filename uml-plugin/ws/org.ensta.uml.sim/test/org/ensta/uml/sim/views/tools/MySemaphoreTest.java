package org.ensta.uml.sim.views.tools;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MySemaphoreTest {

    private MySemaphore sem;

    @Test
    public void testRelease() {
        sem = new MySemaphore(0, 2);
        assertEquals(0, sem.availablePermits());
        sem.release();
        assertEquals(1, sem.availablePermits());
        sem.release();
        sem.release();
        assertEquals(2, sem.availablePermits());
    }

    @Test
    public void testAvailablePermits() {
        sem = new MySemaphore(5, 2);
        assertEquals(5, sem.availablePermits());
    }

    @Test
    public void testGetMaximumEverAvailable() {
        sem = new MySemaphore(5, 2);
        assertEquals(2, sem.getMaximumEverAvailable());
    }
}
