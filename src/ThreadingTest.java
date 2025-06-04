import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadingTest {

    public static void main(String[] args) {
        try {
            MyExecutor.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

class MyExecutor {

    private static final int MAX = 10000;

    public static void start() throws InterruptedException, Exception {
        int ctr=0;
        long start = System.nanoTime();
        for(int i=0;i<MAX;i++) {
            ctr++;
        }
        long end = System.nanoTime();
        long duration = end-start;
        System.out.println(duration);

        int THREADS_N = 10;
        int partitions = MAX/THREADS_N;

        AtomicInteger atomicCtr = new AtomicInteger();

        start = System.nanoTime();

        CountDownLatch startCountdown = new CountDownLatch(0);

        CountDownLatch endCountdown = new CountDownLatch(10);

        for(int i=0;i<THREADS_N;i++) {
            Runnable r = () -> {
                try {
                    startCountdown.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for(int j=0;j<partitions;j++) {
                    atomicCtr.getAndIncrement();
                }
                endCountdown.countDown();

            };

            new Thread(r).start();
        }

        start = System.nanoTime();

        startCountdown.countDown();


        endCountdown.await();

        end = System.nanoTime();

        duration = end-start;

        System.out.println(duration);
    }
}
