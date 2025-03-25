package org.ha.ckh637.service;

import lombok.Getter;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ConcurencyControl {
    private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
    @Getter
    private static final ReentrantReadWriteLock.ReadLock READ_LOCK = LOCK.readLock();
    @Getter
    private static final ReentrantReadWriteLock.WriteLock WRITE_LOCK = LOCK.writeLock();
}
