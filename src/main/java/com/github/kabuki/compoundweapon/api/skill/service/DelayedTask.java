package com.github.kabuki.compoundweapon.api.skill.service;

import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class DelayedTask implements Comparable<DelayedTask> {

    private static final int STOP = 0;
    private static final int RUN = 1;
    private static final int CANCEL = 2;
    private static final int DONE = 3;

    private int state;
    private long delay;
    private long lastTime;
    private Runnable callback_fun;

    public DelayedTask(long delay, Runnable callback_fun) {
        Objects.requireNonNull(callback_fun, "callback cannot be null");
        if(delay < 0) throw new IllegalArgumentException("delay cannot be negative");
        state = delay == 0 ? DONE : RUN;
        onChangeDelay(delay);
        this.callback_fun = callback_fun;
        this.lastTime = System.currentTimeMillis();
    }

    public long getDelay()
    {
        return delay;
    }

    public void setDelay(long delay) {
        if(delay < 0) throw new IllegalArgumentException("Delay cannot be negative");
        if(state == CANCEL) throw new CancellationException("The task has been cancelled");
        if(delay == 0)
            state = DONE;
        this.delay = delay;
        onChangeDelay(delay);
    }

    public void update() {
        if(state != RUN) return;
        if(delay > 0)
        {
            long now = System.currentTimeMillis();
            delay -= now - lastTime;
            onChangeDelay(delay);
            this.lastTime = now;
        }

        if(delay <= 0)
        {
            state = STOP;
            callback_fun.run();
            state = DONE;
        }
    }

    @Override
    public int compareTo(DelayedTask o) {
        return (int) (delay - o.delay);
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        if(state >= CANCEL || !mayInterruptIfRunning) {
            return false;
        }
        state = CANCEL;
        return true;
    }

    public void done() throws ExecutionException {
        if(state > RUN) {
            throw new ExecutionException(new Throwable("The task isDone or isCancel"));
        }
        state = STOP;
        callback_fun.run();
        state = DONE;
        return;
    }

    public boolean isDone() {
        return state == DONE;
    }

    public boolean isCancelled() {
        return state == CANCEL;
    }

    protected void onChangeDelay(long changed)
    {
    }
}
