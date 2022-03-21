package io.github.ceragon.rwsemaphore;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RWSemaphoreTest {

    @Test
    void downRead() throws InterruptedException {
        RWSemaphore semaphore = new RWSemaphore();
        Thread r1 = new Thread(()->{
            try {
                semaphore.downRead();
                semaphore.upRead();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        semaphore.downWrite();
        r1.start();
        Thread.sleep(1L);
        semaphore.upWrite();

        r1.join();
        assertEquals(semaphore.getCount().get(), 0L);
        assertTrue(semaphore.getWaitList().isEmpty());
    }


//    @Test
//    void downWrite() {
//    }


}