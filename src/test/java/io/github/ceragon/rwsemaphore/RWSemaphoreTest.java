package io.github.ceragon.rwsemaphore;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RWSemaphoreTest {

    @Test
    void downRead() throws InterruptedException {
        RWSemaphore semaphore = new RWSemaphore();

        List<Thread> list = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            list.add(newWriter(i, semaphore));
            list.add(newWriter2Reader(i, semaphore));
            list.add(newReader(i, semaphore));
        }

        list.forEach(Thread::start);

        for (Thread thread : list) {
            thread.join();
        }

        assertEquals(semaphore.getCount().get(), 0L);
        assertTrue(semaphore.getWaitList().isEmpty());

    }

    private Thread newReader(int idx, RWSemaphore semaphore) {
        return new Thread(() -> {
            try {
                semaphore.downRead();
                semaphore.upRead();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "reader_" + idx);
    }

    private Thread newWriter(int idx, RWSemaphore semaphore) {
        return new Thread(() -> {
            try {
                semaphore.downWrite();
                semaphore.upWrite();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "writer_" + idx);
    }

    private Thread newWriter2Reader(int idx, RWSemaphore semaphore) {
        return new Thread(() -> {
            try {
                semaphore.downWrite();
                semaphore.downgradeWrite();
                semaphore.upRead();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "writer2reader_" + idx);
    }
}