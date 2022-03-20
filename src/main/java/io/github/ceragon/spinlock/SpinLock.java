package io.github.ceragon.spinlock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class SpinLock {
    private static final long LOCK_MASK = 0x100000000L;
    private static final long LOW_MASK = 0xFFFFFFFFL;
    private final AtomicLong sLock = new AtomicLong(0);

    public void lock() {
        long oldValue = sLock.getAndAdd(LOCK_MASK);
        while (high(oldValue) != low(oldValue)) {
            oldValue = sLock.get();
        }
        // 加锁成功
    }

    public void unlock() {
        sLock.incrementAndGet();
    }

    private long high(long value) {
        return value >>> 32;
    }

    private long low(long value) {
        return value & LOW_MASK;
    }

    private static volatile int cnt = 0;

    public static void main(String[] args) throws InterruptedException {
        SpinLock lock = new SpinLock();
        List<Thread> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Thread t = new Thread(() -> {
                lock.lock();
                cnt++;
                lock.unlock();
            });
            list.add(t);
        }
        for (Thread thread : list) {
            thread.start();
        }
        for (Thread thread : list) {
            thread.join();
        }
        System.out.println(cnt);
    }
}
