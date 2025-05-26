import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Executor {

    public static void main(String[] args) {
        startAllThreads();

    }


    public static void startAllThreads() {
        Object fullLock = new ListFullLock();
        Object emptyLock = new ListEmptyLock();
        Object turn = new Object();
        LockingUtils lockingUtils = new LockingUtils(new Object(), new Object(), new Object(), new Object(), new Object());
        List<String> sharedList = new ArrayList<>();
        Thread producer = new Producer(sharedList, lockingUtils);
        Thread consumer = new Consumer(sharedList, lockingUtils);

        producer.start();
        consumer.start();

    }

}

class ListEmptyLock {

}

class ListFullLock {


}

class LockingUtils {
    private final Object emptyLock;

    private final Object fullLock;

    private final Object turnProducer;


    private final Object turnConsumer;

    public LockingUtils(Object emptyLock, Object fullLock, Object turn, Object turnProducer, Object turnConsumer) {
        this.emptyLock = emptyLock;
        this.fullLock=fullLock;
        this.turnProducer = turnProducer;
        this.turnConsumer = turnConsumer;
    }

    public void fullLock() {
        synchronized (this.fullLock) {
            try {
                this.fullLock.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void lockProducer() {
        synchronized (this.turnProducer) {
            try {
                this.turnProducer.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void lockConsumer() {
        synchronized (this.turnConsumer) {
            try {
                this.turnConsumer.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void turnProducer() {
        synchronized (this.turnProducer) {
            this.turnProducer.notify();

        }
    }

    public void turnConsumer() {
        synchronized (this.turnConsumer) {
            this.turnConsumer.notify();
        }
    }

    public void fullLockNotify() {
        synchronized (this.fullLock) {
            this.fullLock.notify();

        }
    }





    public void emptyLock() {
        synchronized (this.emptyLock) {
            try {
                this.emptyLock.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void emptyLockNotify() {
        synchronized (this.emptyLock) {
            this.emptyLock.notify();

        }
    }
}


class Producer extends Thread {

    List<String>  sharedList;

    LockingUtils lockingUtils;

    private int MAX_LENGTH = 10;

    private volatile boolean running;

    private int ctr = 0;


    public Producer (List<String> list, LockingUtils lockingUtils) {
        this.sharedList = list;
        this.lockingUtils = lockingUtils;
    }

    @Override
    public void run() {
        while(ctr<15) {
            ;ctr++;

            if(this.sharedList.size()==MAX_LENGTH) {
                System.out.println("List is full:full lock");
                this.lockingUtils.fullLock();
            } else {

                if(this.sharedList.isEmpty()) {
                    System.out.println("List is empty in producer:");
                    this.sharedList.add(String.valueOf(new Random().nextInt()));
                    this.lockingUtils.emptyLockNotify();
                }
                else {
                    if(this.sharedList.size()%2==0) {
                        System.out.println("turn producer in producer");
                        this.lockingUtils.turnProducer();
                        this.sharedList.add(String.valueOf(new Random().nextInt()));
                        System.out.println("lock consumer in producer");
                        this.lockingUtils.lockConsumer();
                    }
                    else {
//                        odd number producer
                        System.out.println("Turn consumer in producer");
                        this.lockingUtils.turnConsumer();


                    }
                    System.out.println("Producing normally:");
                    this.sharedList.add(String.valueOf(new Random().nextInt()));

                }
            }
        }

    }

}

class Consumer extends Thread{

    List<String> sharedList;

    private volatile boolean running;

    private int MAX_LENGTH = 10;

    private int ctr=0;

    LockingUtils lockingUtils;


    public Consumer(List<String> list, LockingUtils lockingUtils) {
        this.sharedList = list;
        this.lockingUtils = lockingUtils;
    }

    @Override
    public void run() {

        while (ctr<15) {

            ctr++;

            if (!this.sharedList.isEmpty()) {
                if(this.sharedList.size() == MAX_LENGTH) {
                    System.out.println("List full:notifying on lock full:"+this.sharedList.size());
                    this.sharedList.removeLast();
                    this.lockingUtils.fullLockNotify();
                }
                else {
                    if(this.sharedList.size()%2==0) {
                        System.out.println("turn consumer in consumer");
                        this.lockingUtils.turnConsumer();
                        System.out.println("Consuming normally:");
                        this.sharedList.removeLast();
                        System.out.println("Lock producer in consumer");
                        this.lockingUtils.lockProducer();

                    }
                    else {
                        System.out.println("Turn consumer in consumer");
                        this.lockingUtils.turnConsumer();
                        this.sharedList.removeLast();
                        System.out.println("Lock producer in consumer");
                        this.lockingUtils.lockProducer();
                    }
                    System.out.println("Consuming normally:"+this.sharedList.size());
                    this.sharedList.removeLast();
                }

            }
            if(this.sharedList.isEmpty()) {
                System.out.println("Empty:locking consumer");
                this.lockingUtils.emptyLock();
            }
        }

    }

}


