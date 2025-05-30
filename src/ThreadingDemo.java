import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ThreadingDemo {


    public static void main(String[] args) {
        execDemoLatch();
    }


    public static void execCallables() {
        Callable<Integer> callable1 = () -> {
            return new Random().nextInt();
        };

        Callable<Integer> callable2 = () -> {
            return new Random().nextInt();
        };

        try (ExecutorService executorService = Executors.newCachedThreadPool()) {

            List<Future<Integer>> futures = executorService.invokeAll(List.of(callable1, callable2));


        } catch (InterruptedException e) {
            Logger.getLogger("log").log(Level.SEVERE, "there is an error");
            e.printStackTrace();
        }
    }


    public static void execDemoLatch() {

        CountDownLatch startLatch = new CountDownLatch(1);

        int nThreads = 15;

        CountDownLatch endLatch = new CountDownLatch(nThreads);

        List<String> commonList = new ArrayList<>();


        Map<Integer, Object> locksMap = IntStream.range(0,15).boxed().collect(Collectors.toMap((x -> {
            return x;
        }), (x -> {
            return new Object();
        })));

        Locks locks = new Locks(locksMap);


        for(int i=0;i<nThreads;i++) {


            CountingThread th = new CountingThread();

            th.setLocks(locks);

            th.setCommonList(commonList);

            th.setName(String.valueOf(i));

            th.start();


        }
    }


}

class Locks {

    private final Map<Integer, Object> locks;

    public Locks(Map<Integer, Object> locks) {
        this.locks = locks;
    }




    public void askToAllToWaitExcept(Integer threadId) {
        for(Map.Entry<Integer, Object> entry: locks.entrySet()) {
            if(!entry.getKey().equals(threadId)) {
                synchronized (entry.getValue()) {
                    try {
                        entry.getValue().wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
            else {
                synchronized (entry.getValue()) {
                    entry.getValue().notify();

                }
            }
        }
    }

    public void waitOnLock(Integer threadId) {
        Object lockToWait = locks.get(threadId);
        synchronized (lockToWait) {
            try {
                lockToWait.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void notifyLocks(Integer threadId) {
        Object lockToNotify = locks.get(threadId);
        lockToNotify.notify();
    }

    public void notifyAllThreads() {
        locks.forEach((key,lockObj) -> {
            lockObj.notifyAll();
        });
    }


}

class CountingThread extends Thread {
    private List<String> commonList;

    private Locks locks;

    private static final int NUM_THREADS = 15;


    @Override
    public boolean equals(Object other) {
        Thread oth = (Thread)other;
        return this.getName().equals(oth.getName());
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(this.getName());
    }

    public List<String> getCommonList() {
        return this.commonList;
    }

    public void setCommonList(List<String> commonList) {
        this.commonList = commonList;
    }

    public void setLocks(Locks locks) {
        this.locks = locks;
    }

    @Override
    public void run() {
        System.out.println("Thread:"+this.getName());
        int max = 15;
        int ctr = 0;
        while(ctr < max) {
            Integer positionInList = this.commonList.size()%NUM_THREADS;
            locks.askToAllToWaitExcept(positionInList);
            this.commonList.add(this.getName());
            System.out.println("In thread id:"+this.getName()+":list is:"+this.getCommonList());
            locks.notifyAllThreads();
            ctr++;
        }
    }
}

