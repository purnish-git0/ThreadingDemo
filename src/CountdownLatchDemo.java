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
        ExecutorClassCountdownLatch.startExec();
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
