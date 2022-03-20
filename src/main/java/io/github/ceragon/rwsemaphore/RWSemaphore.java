package io.github.ceragon.rwsemaphore;

import io.github.ceragon.spinlock.SpinLock;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

public class RWSemaphore {
    private static final long LOW_MASK = 0xFFFFFFFFL;

    private final static int RWSEM_WAITING_FOR_READ = 1;
    private final static int RWSEM_WAITING_FOR_WRITE = 2;

    private static final int RWSEM_WAKE_ANY = 0;
    private static final int RWSEM_WAKE_NO_ACTIVE = 1;
    private static final int RWSEM_WAKE_READ_OWNED = 2;

    private static final long RWSEM_ACTIVE_MASK = 0xffff_ffffL;
    private static final long RWSEM_UNLOCKED_VALUE = 0x0000_0000L;
    private static final long RWSEM_ACTIVE_BIAS = 0x0000_0001L;
    private static final long RWSEM_WAITING_BIAS = -RWSEM_ACTIVE_MASK - 1;
    private static final long RWSEM_ACTIVE_READ_BIAS = RWSEM_ACTIVE_BIAS;
    private static final long RWSEM_ACTIVE_WRITE_BIAS = RWSEM_WAITING_BIAS + RWSEM_ACTIVE_BIAS;


    private AtomicLong count = new AtomicLong();
    private SpinLock waitLock = new SpinLock();
    private Deque<RWSemWaiter> waitList = new ArrayDeque<>();

    public void downRead() throws InterruptedException {
        long newCount = count.addAndGet(1L);
        if (newCount >= 0) {
            // 加锁成功
            return;
        }
        rwSemDownFailedCommon(RWSEM_WAITING_FOR_READ, -RWSEM_ACTIVE_READ_BIAS);
    }

    public void upRead() {
        long tmp = count.getAndAdd(-RWSEM_ACTIVE_READ_BIAS);
        if (tmp - RWSEM_ACTIVE_READ_BIAS >= 0) {
            // 解锁成功
            return;
        }

        callRWSemWake(tmp);
    }

    public void downWrite() throws InterruptedException {
        long tmp = count.getAndAdd(RWSEM_ACTIVE_WRITE_BIAS);
        if (tmp == 0) {
            // 加锁成功
            return;
        }
        rwSemDownFailedCommon(RWSEM_WAITING_FOR_WRITE, -RWSEM_ACTIVE_WRITE_BIAS);
    }

    public void upWrite() {
        long tmp = count.getAndAdd(-RWSEM_ACTIVE_WRITE_BIAS);
        if (tmp - RWSEM_ACTIVE_WRITE_BIAS >= 0) {
            // 解锁成功
            return;
        }
        callRWSemWake(tmp);
    }

    private void callRWSemWake(long oldCount) {
        if ((oldCount & LOW_MASK) - 1 != 0) {
            // 解锁成功
            return;
        }
        rwSemWake();
    }

    private void rwSemWake() {
        waitLock.lock();
        if (!waitList.isEmpty()) {
            rwSemDoWake(RWSEM_WAKE_ANY);
        }
        waitLock.unlock();
    }

    private void rwSemDownFailedCommon(int flags, long adjustment) throws InterruptedException {
        waitLock.lock();
        Thread current = Thread.currentThread();
        RWSemWaiter waiter = new RWSemWaiter();
        waiter.setTask(current);
        waiter.setFlags(flags);

        if (waitList.isEmpty()) {
            adjustment += RWSEM_WAITING_BIAS;
        }

        waitList.addLast(waiter);

        long tmp = count.getAndAdd(adjustment);

        long expectCount = tmp + adjustment;

        if (expectCount == RWSEM_WAITING_BIAS) {
            rwSemDoWake(RWSEM_WAKE_NO_ACTIVE);
        } else if (expectCount > RWSEM_WAITING_BIAS
                && adjustment == -RWSEM_ACTIVE_WRITE_BIAS) {
            rwSemDoWake(RWSEM_WAKE_READ_OWNED);
        }

        waitLock.unlock();

        while (waiter.getTask() != null) {
            current.wait();
        }

        // 获取锁成功
    }

    private void rwSemDoWake(int wakeType) {
        Thread tsk;
        long oldcount, woken, loop, adjustment;
        Iterator<RWSemWaiter> iterator = waitList.iterator();
        RWSemWaiter waiter = iterator.next();
        if ((waiter.getFlags() & RWSEM_WAITING_FOR_WRITE) == 0) {
            if (wakeType == RWSEM_WAKE_ANY
                    && rwsemAtomicUpdate(0L) < RWSEM_WAITING_BIAS) {
                return;
            }
            woken = 0;

            do {
                woken++;
                if (!iterator.hasNext()) {
                    break;
                }
                waiter = iterator.next();
            } while ((waiter.getFlags() & RWSEM_WAITING_FOR_READ) > 0);
            adjustment = woken * RWSEM_ACTIVE_READ_BIAS;
            if ((waiter.getFlags() & RWSEM_WAITING_FOR_READ) > 0) {
                adjustment -= RWSEM_WAITING_BIAS;
            }
            rwsemAtomicAdd(adjustment);
            for (loop = woken; loop > 0; loop--) {
                waiter = waitList.pollFirst();
                tsk = waiter.getTask();
                waiter.setTask(null);
                tsk.notify();
            }
            return;
        }
        if (wakeType == RWSEM_WAKE_READ_OWNED) {
            return;
        }
        adjustment = RWSEM_ACTIVE_WRITE_BIAS;
        if (waiter == waitList.getLast()) {
            adjustment -= RWSEM_WAITING_BIAS;
        }

        for (; ; ) {
            oldcount = rwsemAtomicUpdate(adjustment) - adjustment;
            if ((oldcount & RWSEM_ACTIVE_MASK) > 0L) {
                if ((rwsemAtomicUpdate(-adjustment) & RWSEM_ACTIVE_MASK) > 0) {
                    return;
                }
                continue;
            }

            waitList.pollFirst();
            tsk = waiter.getTask();
            waiter.setTask(null);

            tsk.notify();
            return;
        }

    }


    private long rwsemAtomicUpdate(long delta) {
        return count.getAndAdd(delta) + delta;
    }

    private void rwsemAtomicAdd(long delta) {
        count.addAndGet(delta);
    }
}
