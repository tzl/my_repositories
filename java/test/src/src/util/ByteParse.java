package util;

public class ByteParse
{
    private byte[] _array;

    public ByteParse()
    {
        _array = null;
    }

    public ByteParse(byte[] bArray)
    {
        _array = bArray;
    }

    public void setArray(byte[] bArray)
    {
        _array = bArray;
    }

    public int getIndex(String str, int offset)
    {
        if (null == str) {
            return -1;
        }

        return getIndex(str.getBytes(), offset);
    }

    public int getIndex(byte[] needle, int offset)
    {
        int i, j, hayLen, needleLen;

        if (null == _array || null == needle) {
            return -1;
        }

        hayLen = _array.length;
        needleLen = needle.length;

        if (0 > offset || hayLen < offset + needleLen) {
            return -1;
        }

        for (i = offset; i < hayLen; ++i) {
            if (needleLen <= hayLen - i) {
                for (j = 0; j < needleLen; ++j) {
                    if (needle[j] != _array[i + j]) {
                        break;
                    }
                }

                if (j == needleLen) {
                    return i;
                }
            }
            else {
                break;
            }
        }

        return -1;
    }

    public int getLastIndex(String str, int offset)
    {
        if (null == str) {
            return -1;
        }

        return getLastIndex(str.getBytes(), offset);
    }

    public int getLastIndex(byte[] needle, int offset)
    {
        int i, j, hayLen, needleLen;

        if (null == _array || null == needle) {
            return -1;
        }

        hayLen = _array.length;
        needleLen = needle.length;

        if (0 > offset || offset >= hayLen || offset < needleLen - 1) {
            System.out.println("offset:" + offset + " hayLen:" + hayLen + " needleLen:" + needleLen);
            return -1;
        }

        for (i = offset - needleLen + 1; i >= 0 ; --i) {
            for (j = 0; j < needleLen; ++j) {
                if (needle[j] != _array[i + j]) {
                    break;
                }
            }

            if (j == needleLen) {
                return i;
            }
            System.out.println("-- break------   [" + i + "]=" + _array[i + j] + "----  [" + j + "]:" + needle[j]);
        }

        return -1;
    }
}
