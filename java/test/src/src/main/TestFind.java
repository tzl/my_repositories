/**
 * @file TestFind.java
 */

package main;

import util.ByteParse;

public class TestFind
{
    public static byte[] lock = new byte[0];
    private String needle = null;

    public TestFind(String needle)
    {
        this.needle = needle;
    }

    public static void main(String args[])
    {
        if (args.length < 1) {
            System.out.println("usage:\n\t java TestFind\t{string_to_find}\n");
            return;
        }

        byte[] a = new byte[30];

        for (byte i = 0; i < 15; ++i) {
            a[i] = (byte)(i + (byte)1);
        }

        printArray(a);

        ByteParse bp = new ByteParse(a);
        int index = bp.getIndex(new byte[]{3, 7, 8, 9}, 0);
        System.out.println("the needle is found at " + index);

        System.out.println("---\"Thread-main\" begin---");
        TestFind t = new TestFind(args[0]);
        t.go();
        System.out.println("---\"Thread-main\" end---");
    }

    public void go()
    {
        String hay = "hay---1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        FindThread ft = new FindThread(hay, needle);
        ft.start();

        try {
            System.out.println("---\"Thread-main\" wait---");
            synchronized (lock) {
                lock.wait(3000);
            }
            System.out.println("---\"Thread-main\" go on ---");
            ft.join();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void printArray(byte[] array)
    {
        if (null == array) {
            System.out.println("array is null");
        }
        else {
            int i, length;

            length = array.length;
            System.out.print("array data:");
            for (i = 0; i < length; ++i) {
                System.out.print("" + array[i]);
            }
            System.out.println("|");
        }
    }
}

/*class FindThread extends Thread
{
    private ByteParse bp = null;
    private String needle = null;
    private String hay = null;

    private FindThread(){};

    public FindThread(String hay, String needle)
    {
        bp = new ByteParse();

        if (null != hay) {
            this.hay = hay;
            bp.setArray(hay.getBytes());
        }

        this.needle = needle;
    }

    public int getStartPos()
    {
        int pos;

        System.out.println("\"Thread-0\" before search");
        pos = bp.getIndex(needle, 0);
        System.out.println("\"Thread-0\" search end before notify main thread");

        synchronized (TestFind.lock) {
            TestFind.lock.notify();
        }

        System.out.println("\"Thread-0\" after notify main thread");

        return pos;
    }

    public void run()
    {
        System.out.println("\"Thread-0\" begin");
        int pos = getStartPos();

        System.out.println("HAY [" + hay + "]    NEEDLE [" + needle + "]    RESULT=" + pos);
        System.out.println("\"Thread-0\" end");
    }
}
*/
