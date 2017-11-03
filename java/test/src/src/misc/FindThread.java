import java.io.PrintStream;
import util.ByteParse;

class FindThread extends Thread
{

    private ByteParse bp;
    private String needle;
    private String hay;

    private FindThread()
    {
        bp = null;
        needle = null;
        hay = null;
    }

    public FindThread(String s, String s1)
    {
        bp = null;
        needle = null;
        hay = null;
        bp = new ByteParse();
        if(null != s) {
            hay = s;
            bp.setArray(s.getBytes());
        }
        needle = s1;
    }

    public int getStartPos()
    {
        System.out.println("\"Thread-0\" before search");
        int i = bp.getIndex(needle, 0);
        System.out.println("\"Thread-0\" search end before notify main thread");
        synchronized(Test.lock) {
            Test.lock.notify();
        }
        System.out.println("\"Thread-0\" after notify main thread");
        return i;
    }

    public void run()
    {
        System.out.println("\"Thread-0\" begin");
        int i = getStartPos();
        System.out.println((new StringBuilder()).append("HAY [").append(hay).append("]    NEEDLE [").append(needle).append("]    RESULT=").append(i).toString());
        System.out.println("\"Thread-0\" end");
    }
}
