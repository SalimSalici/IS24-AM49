package it.polimi.ingsw.am49.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The IntervalTimer class provides a mechanism to schedule a recurring task at a fixed rate.
 * It uses a ScheduledExecutorService to manage the scheduling of tasks.
 */
public class IntervalTimer {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledFuture;
    private final Runnable task;
    private final long initialDelay;
    private final long period;
    private final TimeUnit unit;

    /**
     * Constructs an IntervalTimer with the specified task, initial delay, period, and time unit.
     *
     * @param task the task to be executed periodically
     * @param initialDelay the initial delay before the task is first executed
     * @param period the period between successive executions of the task
     * @param unit the time unit of the initial delay and period
     */
    public IntervalTimer(Runnable task, long initialDelay, long period, TimeUnit unit) {
        this.task = task;
        this.initialDelay = initialDelay;
        this.period = period;
        this.unit = unit;
    }

    /**
     * Starts the interval timer. If the timer is already running, this method does nothing.
     */
    public void start() {
        if (scheduledFuture == null || scheduledFuture.isCancelled()) {
            scheduledFuture = scheduler.scheduleAtFixedRate(task, initialDelay, period, unit);
        }
    }

    /**
     * Stops the interval timer. If the timer is not running, this method does nothing.
     */
    public void stop() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            scheduledFuture = null;
        }
    }

    /**
     * Shuts down the scheduler. Once the scheduler is shut down, it cannot be restarted.
     */
    public void shutdown() {
        scheduler.shutdown();
    }
}
