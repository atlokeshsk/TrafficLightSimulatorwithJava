public class Main {

    public static void main(String[] args) {

        // create threads and start them using the class RunnableWorker
        var workerx = new Thread(new RunnableWorker(),"worker-x");
        var workery = new Thread(new RunnableWorker(),"worker-y");
        var workerz = new Thread(new RunnableWorker(),"worker-z");

        workerx.start();
        workery.start();
        workerz.start();
    }
}

// Don't change the code below       
class RunnableWorker implements Runnable {

    @Override
    public void run() {
        final String name = Thread.currentThread().getName();

        if (name.startsWith("worker-")) {
            System.out.println("too hard calculations...");
        } else {
            return;
        }
    }
}