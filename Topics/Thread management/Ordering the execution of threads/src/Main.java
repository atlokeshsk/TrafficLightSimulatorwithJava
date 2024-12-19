class Invoker {

    public static void invokeMethods(Thread t1, Thread t2, Thread t3) throws InterruptedException {
        // start passed instances here .
        // Start t3 first, then t2, then t1
        t3.start();
        t3.join(); // Wait for t3 to finish

        t2.start();
        t2.join(); // Wait for t2 to finish

        t1.start();
        t1.join(); // Wait for t1 to finish

    }
}