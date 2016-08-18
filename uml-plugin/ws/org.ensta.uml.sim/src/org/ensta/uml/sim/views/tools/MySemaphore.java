package org.ensta.uml.sim.views.tools;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * <b>This class permit to have a semaphore with a maximal permit </b>
 * <p>
 * 
 * @author michael
 * @version 1.0
 */
public class MySemaphore {
    private final Semaphore semaphore;

    private int maxPermits;

    public MySemaphore(int initialAvailable, int maxPermits) {
        this.semaphore = new Semaphore(initialAvailable);
        this.maxPermits = maxPermits;
    }

    public void acquire() throws InterruptedException {
        semaphore.acquire();
    }

    public boolean tryAcquire(int time, TimeUnit unit) throws InterruptedException {
        boolean result = semaphore.tryAcquire(time, unit);
        return result;
    }

    public void release() {
        if (semaphore.availablePermits() < maxPermits) {
            semaphore.release();
        } else
            return;
    }

    public int availablePermits() {
        return semaphore.availablePermits();
    }

    public int getMaximumEverAvailable() {
        return maxPermits;
    }

}
