/**
 * @file Misc.java
 */

package util;

public class Misc
{
    public static int removeDupEl(Object[] array)
    {
        int lastPos, curPos, i;

        if (null == array || 0 == array.length) {
            return 0;
        }

        curPos = 0;
        lastPos = array.length - 1;

        while (curPos < lastPos) {
            for (i = curPos + 1; i <= lastPos; ++i) {
                if (array[i].equals(array[curPos])) {
                    while (i == lastPos || array[lastPos].equals(array[curPos])) {
                        array[lastPos] = null;
                        --lastPos;
                        if (lastPos == curPos) {
                            return lastPos + 1;
                        }
                    }

                    if (lastPos < i) {
                        break;
                    }

                    array[i] = array[lastPos];
                    array[lastPos] = null;
                    --lastPos;
                }
            }

            ++curPos;
        }

        return lastPos + 1;
    }
}