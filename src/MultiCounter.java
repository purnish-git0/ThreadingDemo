import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 *
 * A Multi Counter which takes turns adding to a shared list
 * Insertion order is from thread id 0 to 9 then repeat
 * When adding to List, String contains info about thread id
 * adding to the list.
 * In the output can be seen that the insertion order is cyclic
 * based on thread id
 */
public class MultiCounter {


    public static void main(String[] args) {
        startAllThreads();
    }

    public static void startAllThreads() {

        SharedObj sharedObj = new SharedObj();

        sharedObj.setList(new ArrayList<>());

        for(int i=0;i<10;i++) {
            CounterThread thread = new CounterThread(i);
            thread.setSharedObj(sharedObj);
            thread.start();
        }

    }
}

class CounterThread extends Thread {

    private Integer id;


    public CounterThread(Integer id) {
        this.id = id;
    }

    private SharedObj sharedObj;

    public SharedObj getSharedObj() {
        return sharedObj;
    }

    public void setSharedObj(SharedObj sharedObj) {
        this.sharedObj = sharedObj;
    }



    @Override
    public void run() {
        int ctr=0;
        while(this.sharedObj.getList().size()<55) {
            this.getSharedObj().addElements(this.id);
            int size = this.getSharedObj().getList().size();
            System.out.println(size);
        }
    }


}


class SharedObj {

    private final Lock lock = new ReentrantLock();

    private final Object object = new Object();

    private Condition condition = lock.newCondition();

    private List<String> list;


    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public void checkTurnWithCondition(Integer threadId) {
        lock.lock();
        while(this.getList().size()%10 != threadId) {
            try {
                condition.await();
                if(this.getList().size() > 100) {
                    break;
                }
                checkTurnWithCondition(threadId);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        this.getList().add(new Random().nextInt() + "_" + threadId);
        condition.signalAll();


    }

    public void notifyAllThreads() {
        synchronized (object) {
            object.notifyAll();
        }
    }

    public void addElements(Integer threadId) {
        checkTurnWithCondition(threadId);
        System.out.println("List state:" +  this.getList());
    }

}
