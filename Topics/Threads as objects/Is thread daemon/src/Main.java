class ThreadUtil {
    public static void printIfDaemon(Thread thread) {
        System.out.println(thread.isDaemon() ? "daemon" : "not daemon");
    }
}