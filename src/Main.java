import utility.Timing;

public class Main {
    public static void main(String[] args) {
        System.out.println(Math.cos(Math.PI/2));
        foo g = new foo();
        new Thread(g::f).start();
        new Thread(g::f).start();
    }
}

class foo{
    void f(){
        System.out.println("begin");
        Timing.delay(1000);
        System.out.println("end");
    }
}

