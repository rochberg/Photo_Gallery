package com.photogallery.uploadingfiles.storage;

import java.util.concurrent.atomic.AtomicBoolean;

public class ExecuteOnce {
    private final AtomicBoolean done = new AtomicBoolean();
    public void run(Runnable task) {
        if (done.get()) return;
        if (done.compareAndSet(false, true)) {
            task.run();
        }
    }
}