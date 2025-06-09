import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;


/**
 *
 * Threads alternating and printing out to 
 * console numbers 1 to 100
 *
 * Using RentrantLock, Condition object
 * await() and signalAll() methods
 */
public class ThreadingCounter {
    public static void main(String[] args) {

        SharedObject sharedObj = new SharedObject();

        sharedObj.setTurn(0);

        IntStream.range(0,3).forEach(i -> {
            CustomCounterThread th = new CustomCounterThread(sharedObj, i);

            th.start();
        });
    }
}

class CustomCounterThread extends Thread {



    private SharedObject sharedObj;

    private Integer threadId;

    public CustomCounterThread(SharedObject sharedObj, Integer threadId) {
        this.sharedObj =sharedObj;
        this.threadId = threadId;

    }



    @Override
    public void run() {
        int ctr=0;
        while(ctr<100){
            this.sharedObj.getLock().lock();
            if(this.sharedObj.getTurn().intValue()%3!=this.threadId.intValue()) {
                try {
                    this.sharedObj.getCondition().await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println(Thread.currentThread().getName());
            this.sharedObj.setTurn(this.sharedObj.getTurn()+1);
            this.sharedObj.getCondition().signalAll();
            ctr++;
        }
        this.sharedObj.getCondition().signalAll();
    }

}

class SharedObject {

    private Lock lock = new ReentrantLock();

    private Condition condition;

    private Integer threadId;

    private Integer turn;

    public Integer getThreadId() {
        return threadId;
    }

    public void setThreadId(Integer threadId) {
        this.threadId = threadId;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public SharedObject(){
        condition = lock.newCondition();
    }


    public void printer() {
        int ctr = 0;
        while(ctr < 100) {
            waitTillTurn();
        }
    }

    public void waitTillTurn() {
        if(this.getThreadId().intValue()!=turn.intValue()%3){
            try {
                condition.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(Thread.currentThread().getName());
        turn++;
        condition.signalAll();
    }
}