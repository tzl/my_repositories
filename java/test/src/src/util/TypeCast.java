/**
 * @file TypeCast.java
 */

package util;

public class TypeCast
{
    static public int toInt(byte[] buf, int offset)
    {
        if (null == buf || buf.length <= offset) {
            return 0;
        }
        else {
            int i, len, temp = 0;

            len = (buf.length - offset >= 4)? 4: buf.length - offset;
            for (i = len - 1; i >= 0; --i) {
                temp <<= 8;
                temp += (buf[i + offset] & 0xff);
            }

            return temp;
        }
    }

    static public byte[] toArray(int a)
    {
        int i;
        byte[] b = new byte[4];

        for (i = 0; i < 4; ++i) {
            b[i] = (byte)((a >> (i << 3)) & 0xff);
        }

        return b;
    }

    static public byte[] toArray(int[] ia)
    {
        if (null != ia) {
            byte[] b = new byte[ia.length << 2];
            int i, j;

            for (j = 0; j < ia.length; ++j) {
                for (i = 0; i < 4; ++i) {
                    b[i] = (byte)((ia[j] >> (i << 3)) & 0xff);
                }
            }
            return b;
        }
        else {
            return null;
        }
    }

    /**
     * "00000295" --> 0x295
     */
    public static int getLsbDecimal(String hexStr)
    {
        //hexStr should be "00000295" rather than "0x00000295"
        byte size, i, digital;
        char c, c0 = '0', c9 = '9', ca = 'a', cA = 'A', cf = 'f', cF = 'F';
        int num;

        if (null == hexStr) {
            return -1;
        }

        size = (byte)hexStr.length();
        if (8 != size) {
            return -2;
        }

        num = 0;
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
                digital = (byte)-1;
            }

            if (0 > digital || 15 < digital) {
                //error handler goes here
                return -3;
            }

            num += (digital << 4 * (size - i - 1));
        }

        return num;
    }

    public static int getMsbDecimal(String hexStr)
    {
        //hexStr should be "00000295" rather than "0x00000295"
        int num;
        byte[] hexArray;

        if (null == hexStr) {
            return -1;
        }

        if (8 != hexStr.length()) {
            return -2;
        }

        hexArray = getHexArray(hexStr);
        num = toInt(hexArray, 0);

        return num;
    }

    /**
     * "00000295" --> [0x00, 0x00, 0x02, 0x95]
     */
    public static byte[] getHexArray(String hexStr)
    {
        //hexStr should be "00000295" rather than "0x00000295"
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
}
