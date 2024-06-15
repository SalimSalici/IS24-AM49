package it.polimi.ingsw.am49.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class IntervalTimer {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledFuture;
    private final Runnable task;
    private final long initialDelay;
    private final long period;
    private final TimeUnit unit;

    public IntervalTimer(Runnable task, long initialDelay, long period, TimeUnit unit) {
        this.task = task;
        this.initialDelay = initialDelay;
        this.period = period;
        this.unit = unit;
    }

    public void start() {
        if (scheduledFuture == null || scheduledFuture.isCancelled()) {
            scheduledFuture = scheduler.scheduleAtFixedRate(task, initialDelay, period, unit);
        }
    }

    public void stop() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            scheduledFuture = null;
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
