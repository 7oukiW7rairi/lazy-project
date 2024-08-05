package org.lazy.app;

public class ThreadUtils {

    private ThreadUtils() {
    }

    public static Thread startDaemonThread(Runnable runnable, String name) {
        Thread thread = new Thread(runnable, name);
        thread.setDaemon(true);
        thread.start();
        return thread;
    }
}
