/**
 * @file Test.java
 */

package main;

import util.TypeCast;
import util.ByteParse;
import util.Misc;

public class Test
{
    public static byte lock[] = new byte[0];
    public String _str = null;
    public static char[] temp = {'0', '1', '2', '3', '4',
                        '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    public String[] strArray = {"1", "2", "3", "a", "3", "2", "4", "5"};

    public void testMiscDelDup()
    {
        int i;

        for (i = 0; i < strArray.length; ++i) {
            System.out.print(" " + i + "--" + strArray[i]);
        }
        System.out.println("");


        Misc.removeDupEl(strArray);

        for (i = 0; i < strArray.length; ++i) {
            System.out.print(" " + i + "--" + strArray[i]);
        }
        System.out.println("");
    }

    public Test()
    {
        byte[] ip = new byte[4];

        String host = resolveHostName("ms-help://MS.MSDNQTR.2004JAN.1033/winsock/winsock/gethostbyname_2.htm");
        System.out.println(host);

        if (checkHostName("192.168.80.abc", ip)) {
            printArray(ip, 0, 4);
        }

        AnotherClass ac = AnotherClass.getInstance();
        ac.setter(this);
        this._str = "hello";
        double a = 1.0 / 3.0;
        String str = "double one/third=" + a + "ÖÐ¹ú";
        byte[] array = str.getBytes();

        System.out.println(str);
        printArray(array, 0, array.length);

        ac.go();
    }

    public static void main(String args[])
    {
        Test test = new Test();
//        test.go();
    }

    public void go()
    {
//        System.out.println("field [str]:" + _str);
//
//        printArray();
//        getRandomChar(2);
//        printArray();

        String strNum = "000002D3";
        int a = getDecimal(strNum);
        byte[] ar = getHexArray(strNum);
        printArray(ar, 0, ar.length);

        System.out.println("str-" + strNum + " digital-" + a);
        a = TypeCast.getMsbDecimal(strNum);
        ar = TypeCast.toArray(0xD3020000);
        printArray(ar, 0, ar.length);
        System.out.println("str-" + strNum + " digital-" + 0xD3020000);

        strNum = "hexStr should be \"00000295\" rather than \"0x00000295";
        ByteParse bp = new ByteParse(strNum.getBytes());
        int i = bp.getLastIndex("0295", 26);
        int j = bp.getLastIndex("0295", strNum.length() - 1);

        System.out.println("i=" + i + " j=" + j);
        printArray("0295".getBytes(), 0, 4);
        printArray(strNum.getBytes(), i, 10);

        String aStr = "123456";
        aStr.concat("789");
        System.out.println("--" + aStr);
    }

    byte[] resolveHostName(String url)
    {
        if (null == url) {
            return null;
        }
        else {
            String hostName;
            byte[] ipv4 = null;
            int pos1, pos2;
            ByteParse bp = new ByteParse(url.getBytes());

            /*get host name literally*/
            pos1 = bp.getLastIndex(new byte[]{'/'}, 9/* "https://" */);
            if (-1 == pos1) {
                return null;
            }
            ++pos1;

            pos2 = bp.getIndex(new byte[]{':'}, pos1);
            if (-1 == pos2) {
                pos2 = bp.getIndex(new byte[]{'/'}, pos1);
                if (-1 == pos2) {
                    pos2 = url.length() -1;
                }
            }
            hostName = url.substring(pos1, pos2);

            /*get IPv4 byte array*/
            try {
                ipv4 = java.net.InetAddress.getByName(hostName).getAddress();
            }
            catch (java.net.UnknownHostException uhe) {
            }

            return ipv4;
        }
    }

    int getDecimal(String hexStr)
    {
        //hexStr should be "00000295" rather than "0x00000295"
        byte size, i, digital;
        char c, c0 = '0', c9 = '9', ca = 'a', cA = 'A', cf = 'f', cF = 'F';
        int num;

        num = 0;
        size = (byte)hexStr.length();
        for (i = 0; i < size; ++i) {
            c = hexStr.charAt(i);

            if (c0 <= c && c9 >= c) {
                digital = (byte)(c - '0');
            }
            else if (ca <= c && cf >= c) {
                digital = (byte)(c - 'a' + 10);
            }
            else if (cA <= c && cF >= c) {
                digital = (byte)(c - 'A' + 10);
            }
            else {
                digital = (byte)0;
            }

            if (0 > digital || 15 < digital) {
                //error handler goes here
                return -1;
            }

            num += (digital << 4 * (size - i - 1));
        }

        return num;
    }

    byte[] getHexArray(String hexStr)
    {
        /*hexStr should be "00000295" rather than "0x00000295"*/
        char c, c0 = '0', c9 = '9', ca = 'a', cA = 'A', cf = 'f', cF = 'F';
        byte i, size, digital;
        byte[] hexArray = new byte[4];

        if (null == hexStr || 8 != hexStr.length()) {
            hexArray[0] = hexArray[1] = hexArray[2] = hexArray[3] = 0;
            return hexArray;
        }

        size = (byte)hexStr.length();
        for (i = 0; i < size; ++i) {
            c = hexStr.charAt(i);

            if (c0 <= c && c9 >= c) {
                digital = (byte)(c - '0');
            }
            else if (ca <= c && cf >= c) {
                digital = (byte)(c - 'a' + 10);
            }
            else if (cA <= c && cF >= c) {
                digital = (byte)(c - 'A' + 10);
            }
            else {
                digital = (byte)0;
            }

            if (0 == i % 2) {
                hexArray[((i + 1) >> 1)] += (digital << 4);
            }
            else {
                hexArray[((i + 1) >> 1) - 1] += digital;
            }
        }

        return hexArray;
    }

    static void getRandomChar(int i)
    {
        int k, j, size = temp.length;
        char c;
        char[] buf;
        int nextPos, curPos, blockSize, overWrite, reverse = 0;

        if (0 >= i) {
            return;
        }

        i %= size;
        if (i > (size >> 1)) {
            reverse = 1;
            i = size - i;
        }

        blockSize = (size + i - 1) / i;
        System.out.println("blockSize: " + blockSize);
        System.out.println("i: " + i);

        overWrite = 0;
        c = temp[0];
        buf = new char[blockSize];
        System.arraycopy(temp, 0, buf, 0, blockSize);
        for (j = 0; j < i; ++j) {
            for (k = 0; k < blockSize; ++k) {
                curPos = (j + k * i) % size;

                if (j-1 == curPos) {
                    System.out.println("j:" + j + " curPos:" + curPos + " overWrite:" + overWrite);
                    if (j == overWrite) {
                        ++overWrite;
                    }
                    else {
                        continue;
                    }
                }
                nextPos = (j + (k+1) * i) % size;
                System.out.println("["+nextPos+"]-->["+curPos+"] "+temp[nextPos]+"-->"+temp[curPos]);

//                temp[curPos] = temp[nextPos];
                temp[curPos] = (0 == nextPos)? c: temp[nextPos];
            }
        }

        System.arraycopy(buf, 0, temp, size - blockSize - 1, blockSize);
    }

    public static void printArray(byte[] array, int offset, int length)
    {
        if (null == array) {
            System.out.println("array is null");
        }
        else {
            int i;

            System.out.print("array data:");
            if (offset < array.length && offset + length <= array.length) {
                for (i = offset; i < offset + length; ++i) {
                    System.out.print(" " + array[i]);
                }
            }
            System.out.println("|");
        }
    }

    static public boolean checkHostName(String hostName, byte[] ip)
    {
        byte c;
        int iSection, lastPos, dotPos;
        String temp;

        iSection = 0;
        lastPos = 0;
        while (-1 != (dotPos = hostName.indexOf('.',  lastPos))) {
            temp = hostName.substring(lastPos, dotPos);

            try {
                ip[iSection++] = (byte)(0xff & Integer.parseInt(temp));
            }
            catch (NumberFormatException nfe) {
                return false;
            }
            lastPos = dotPos + 1;
        }

        if (3 != iSection) {
            return false;
        }

        temp = hostName.substring(lastPos);

        try {
            ip[iSection++] = (byte)(0xff & Integer.parseInt(temp));
        }
        catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }
}

class AnotherClass
{
    private static AnotherClass _instance = null;
    public Test _testClass = null;

    private AnotherClass() {}

    public static AnotherClass getInstance()
    {
        if (null == _instance) {
            _instance = new AnotherClass();
        }

        return _instance;
    }

    public void setter(Test testClass)
    {
        _testClass = testClass;
    }

    public void go()
    {
        if (null == _testClass) {
            System.out.println("delegate not set");
        }
        else {
            _testClass.go();
        }
    }
}
