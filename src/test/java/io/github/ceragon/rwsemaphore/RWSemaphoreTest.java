package io.github.ceragon.rwsemaphore;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RWSemaphoreTest {

    @Test
    void downRead() throws InterruptedException {
        RWSemaphore semaphore = new RWSemaphore();
        semaphore.downWrite();
        semaphore.downRead();
        semaphore.upWrite();
        semaphore.upRead();
        assertEquals(semaphore.getCount().get(), 0L);
        assertTrue(semaphore.getWaitList().isEmpty());
    }


//    @Test
//    void downWrite() {
//    }


}