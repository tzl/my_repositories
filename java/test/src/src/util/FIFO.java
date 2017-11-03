/**
 * @file FIFO.java
 */

package util;

public class FIFO
{
    private int h, t;
    private Object[] buf;


    public FIFO(int size)
    {
        if (0 < size) {
            buf = new Object[size];

            h = 0;
            t = 0;
        }
    }

    public synchronized Object getEl()
    {
        if (null != buf[h]) {
            Object temp = buf[h];

            buf[h] = null;
            h = (h + 1) % buf.length;

            return temp;
        }
        else {
            return null;
        }
    }

    public synchronized void addEl(Object el)
    {
        int next = (t + 1) % buf.length;

        if (null == el) {
            return;
        }

        if (h == next && null != buf[t]) {
            Object temp[] = new Object[buf.length << 1];
            buf[t] = el;
            System.arraycopy(buf, h, temp, 0, buf.length - h);
            System.arraycopy(buf, 0, temp, buf.length - h, h);
            h = 0;
            next = buf.length + 1;
            buf = null;
            buf = temp;
        }
        else {
            buf[t] = el;
        }

        t = next;
    }
}