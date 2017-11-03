/**
 * @file HexaUtil.java
 */

package util;

public class HexaUtil
{
    protected static final char[] num = {'0', '1', '2', '3', '4', '5', '6', '7',
                                         '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static void printArray(byte[] array, int offset, int length, String tag)
    {
        if (null == array) {
            System.out.println("array is null");
            return;
        }

        /**
         * addr         (\t)hexadecimal data ctnt                           (\t)character data ctnt
         * 00000010     01 01 09 01 05 04 03 FF 00 00 00 45 00 6E 00 67     ...........E.n.g
         * 00000020     00 6C 00 69 00 73 00 68 00 20 00 26 00 20 00 43     .l.i.s.h. .&. .C
         */

        int i, end, column, cnt;
        StringBuffer sb;

        if (0 < offset || offset > array.length) {
            offset = 0;
        }

        end = offset + length;
        if (end > array.length) {
            end = array.length;
        }

        if (null != tag) {
            System.out.println(tag);
        }

        cnt = column = 0;
        for (i = offset; i < end; ++i) {
            if (0 == column) { /*addr*/
                System.out.print(getMemAddr(cnt) + "\t");
            }

            /*hexadecimal data ctnt*/
            System.out.print(memToStr(array[i]) + " ");

            ++column;
            column &= 0xf;
            if (0 == column) { /*character data ctnt*/
                sb = new StringBuffer("\t");
                do {
                    sb.append(byteStr(array[cnt + offset]));

                    ++cnt;
                } while (0 != (cnt & 0xf));

                column = 0;
                System.out.println(sb.toString());
            }
        }

        if (0 != column) { /*character data ctnt*/
            int space = 16 - (i - offset - cnt);

            sb = new StringBuffer();
            for (i = 0; i < space; ++i) {
                sb.append("   ");
            }
            sb.append("\t");

            do {
                sb.append(byteStr(array[cnt + offset]));

                ++cnt;
            } while (end != (cnt + offset));
            System.out.println(sb.toString());
        }
    }

    private static String getMemAddr(int addr)
    {
        int index, i;
        StringBuffer sb = new StringBuffer();

        for (i = 7; i >= 0; --i) {
            index = (addr >> (i << 2)) & 0x0f;
            sb.append(num[index]);
        }

        return sb.toString();
    }

    private static String memToStr(byte b)
    {
        int index;
        StringBuffer sb = new StringBuffer();

        index = (b >> 4) & 0x0f;
        sb.append(num[index]);
        index = b & 0x0f;
        sb.append(num[index]);

        return sb.toString();
    }

    private static char byteStr(byte b)
    {
        if (31 < b && 127 > b) {
            return (char)b;
        }
        else {
            return (char)'.';
        }
    }
}
