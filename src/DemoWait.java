import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DemoWait {


    public static void main(String[] args) {
        EvenCounter evenCounter = new EvenCounter();
        OddCounter oddCounter = new OddCounter();
        List<Integer> list = new ArrayList<>();
        SharedSpace sharedSpace = new SharedSpace(new Object());
        sharedSpace.setList(list);

        evenCounter.setSharedSpace(sharedSpace);
        oddCounter.setShared(sharedSpace);


        evenCounter.start();
        oddCounter.start();

        try {
            evenCounter.join();
            oddCounter.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(sharedSpace.getList());
    }
}


class OddCounter extends Thread {

    public SharedSpace getShared() {
        return sharedSpace;
    }

    public void setShared(SharedSpace shared) {
        this.sharedSpace = shared;
    }

    private SharedSpace sharedSpace;


    @Override
    public void run() {

        while (this.sharedSpace.getList().size() <= 25) {
            this.sharedSpace.addOddNumber();
        }

    }


}

class EvenCounter extends Thread {

    public SharedSpace getShared() {
        return sharedSpace;
    }


    private SharedSpace sharedSpace;

    public void setSharedSpace(SharedSpace sharedSpace) {
        this.sharedSpace = sharedSpace;
    }

    @Override
    public void run() {
        while(this.sharedSpace.getList().size() < 25) {
            this.sharedSpace.addEvenNumber();
        }
    }

}


class SharedSpace {
    private List<Integer> list;

    public Object getObj() {
        return obj;
    }

    public SharedSpace(Object obj) {
        this.obj=obj;
    }

    private final Object obj;


    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }

    public void addEvenNumber() {
        synchronized (obj) {
            if(this.getList().isEmpty() || this.getList().getLast()%2 == 0) {
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            if(this.getList().getLast()%2==1) {
                this.getList().add(Math.abs(genRandom()*2));
                System.out.println(this.getList());
                obj.notify();
            }

        }
    }

    private Integer genRandom() {
        int generated = new Random().nextInt();

        return generated < 0 ? generated * -1 : generated;
    }

    public void addOddNumber() {
        synchronized (obj) {
            if(this.getList().isEmpty()) {
                this.getList().add(1);
                obj.notify();
            }
            if(this.getList().getLast()%2==1) {
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if(this.getList().getLast()%2==0) {
                this.getList().add(Math.abs(genRandom()*2+1));
                System.out.println(this.getList());

                obj.notify();
            }
        }
    }
}


