/**
 * @file TestMisc.java
 */

package main;

import util.RingList;

public class TestMisc
{
    public int[] strArray = { 'a', 3, 5, 3, 2, 4, 51, 2, 4, 3, 'a', 3, 5, 3, 2, 4, 5};

    public void testRingList()
    {
        int i;
        RingList list = new RingList(5);
        El el = null, el2;

        for (i = 0; i < strArray.length; ++i) {
            el = new El(strArray[i]);
            el.print();
            list.addEl(el);
        }
        System.out.println("");

        System.out.println("length:" + strArray.length);

        el2 = (El)list.getEl(new El(51));
        if (null != el2) {
            el2.print();
            System.out.println("");
        }
        else {
            System.out.println("error:" + i);
        }
        el2 = (El)list.getEl(new El(97));
        if (null != el2) {
            el2.print();
            System.out.println("");
        }
        else {
            System.out.println("error:" + i);
        }

        for (i = 0; i < strArray.length; ++i) {
            el = new El(strArray[i]);
            el2 = (El)list.getEl(el);
            if (null != el2) {
                el2.print();
            }
            else {
                System.out.print(" ->(error:" + i + ")");
            }
        }

        System.out.println("");

    }

    public static void main(String args[])
    {
        TestMisc test = new TestMisc();
        test.testRingList();
    }
}

class El
{
    public int value;

    public El(int i)
    {
        value = i;
    }

    public boolean equals(Object el)
    {
        if (null == el) {
            return false;
        }

        return ((El)el).value == value;
    }

    public void print()
    {
        System.out.print(" ->" + value);
    }
}
