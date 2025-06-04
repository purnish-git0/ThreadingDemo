import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

/**
 *
 * Every thread gets a chance to add to list.
 * Countdown is triggered only once for each threads
 * contribution to list. Subsequent additions do not
 * trigger countdown
 */

public class CountdownLatchDemo {

    public static void main(String[] args) {
        ExecutorClassCountdownLatch.countdownWithStart();
    }
}

class ExecutorClassCountdownLatch{

    private static final Integer N_THREADS = 15;

    private static final Integer LATCH_COUNT = 25;


    public static void startExec() {
        CountDownLatch countDownLatch = new CountDownLatch(LATCH_COUNT);
        List<String> sharedList = new CopyOnWriteArrayList<>();

        IntStream.range(0,N_THREADS).forEach(i -> {
            CounterDemoRunner counterDemoRunner = new CounterDemoRunner();
            counterDemoRunner.setSharedList(sharedList);
            counterDemoRunner.setCountDownLatch(countDownLatch);
            new Thread(counterDemoRunner).start();
        });

        try {
            countDownLatch.await();
            System.out.println(sharedList);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     *
     * Start countdownLatch is included so that
     * all threads start at the same time, when the
     * start is tripped post creation and start
     * of all threads and end is incremented
     * twice per thread.
     */
    public static void countdownWithStart() {
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(10);

        List<String> sharedList = new CopyOnWriteArrayList<>();

        for(int i=0;i<15;i++) {
            Thread t = new Thread(() -> {
                try {
                    start.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                int ctr = 0;
                while(ctr<3) {

                    if(Collections.frequency(sharedList,"_" + Thread.currentThread().getName())==0) {
                        sharedList.add("_" + Thread.currentThread().getName());

                        end.countDown();
                        end.countDown();

                    }

                    ctr++;
                }

            });

            t.start();
        }

        try {
            start.countDown();
            end.await();
            System.out.println("End exec");
            System.out.println(sharedList);
            System.out.println(sharedList.size());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }




    }
}

class CounterDemoRunner implements Runnable {


    private List<String> sharedList;

    private CountDownLatch countDownLatch;

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public List<String> getSharedList() {
        return sharedList;
    }

    public void setSharedList(List<String> sharedList) {
        this.sharedList = sharedList;
    }

    @Override
    public void run() {
        int ctr=0;
        while(ctr<2) {
            ctr++;
            int freq = Collections.frequency(this.getSharedList(),"_" + Thread.currentThread().getName());
            if(freq == 0) {
                this.getSharedList().add("_" + Thread.currentThread().getName());

            }
            if(freq==1) {
                countDownLatch.countDown();
                countDownLatch.countDown();

            }

        }

    }
}
