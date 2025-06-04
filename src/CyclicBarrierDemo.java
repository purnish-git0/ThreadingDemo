import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.IntStream;
import java.util.stream.IntStream.Builder;

public class CyclicBarrierDemo {

    public static void main(String[] args) {
        ExecutorClass.startExec();
    }

}

class ExecutorClass {

    private static final Integer N_THREADS = 10;

    public static void startExec() {

        CyclicBarrier cyclicBarrier = new CyclicBarrier(N_THREADS);

        List<String> sharedList = new ArrayList<>();

        IntStream.range(0, N_THREADS).boxed()
                .forEach(i -> {
                    CounterRunner counterRunner = new CounterRunner();
                    counterRunner.setThreadId(i);
                    counterRunner.setSharedList(sharedList);
                    counterRunner.setCyclicBarrier(cyclicBarrier);
                    new Thread(counterRunner).start();
                });
    }
}

class CounterRunner implements Runnable {



    private List<String> sharedList;


    private CyclicBarrier cyclicBarrier;

    private Integer threadId;

    public Integer getThreadId() {
        return threadId;
    }

    public void setThreadId(Integer threadId) {
        this.threadId = threadId;
    }

    public List<String> getSharedList() {
        return sharedList;
    }

    public void setSharedList(List<String> sharedList) {
        this.sharedList = sharedList;
    }

    public CyclicBarrier getCyclicBarrier() {
        return cyclicBarrier;
    }

    public void setCyclicBarrier(CyclicBarrier cyclicBarrier) {
        this.cyclicBarrier = cyclicBarrier;
    }


    @Override
    public void run() {
        System.out.println("Running thread:" + this.threadId);
        this.getSharedList().add("_" + this.threadId);
        try {
            this.cyclicBarrier.await();
            System.out.println("Finished all");
            System.out.println(this.sharedList);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }
}
