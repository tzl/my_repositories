package misc;

import java.util.Properties;

public class TryCatchThread {
    public static void main(String[] args) {
        try {
            System.out.println("enter main thread");
            Runnable r = new ExcepitonRunnable();
            new Thread(r).start();
            // r.run();
            System.out.println("leave main thread");
        }
        catch (Exception e) {
            // e.printStackTrace();
            System.out.println("main thread caught the Exception raising from sub thread");
        }
        finally {
            System.out.println("end process");
        }
    }
}

class ExcepitonRunnable implements Runnable {
    @Override public void run() {
        System.out.println("enter subthread");
        int v = 10 / 0;
        System.out.println("leave subthread");
    }
}
