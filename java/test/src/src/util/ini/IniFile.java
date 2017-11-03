/**
 * file name: IniFile.java
 */

package util.ini;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import util.ByteParse;

public class IniFile
{
    public static final int MAX_PATH = 256;
    public static final String LINE_SEP = System.getProperty("line.separator");
    private RandomAccessFile iniFile;
    private File rawFile;
    private String fileName;

    public IniFile(String fileName) throws IOException
    {
        if (null == fileName) {
            throw new IOException("file name (null)");
        }

        rawFile = new File(fileName);
        if (0 == rawFile.length()) {
            throw new IOException("file length is 0 | no such a file: \"" + fileName + "\"");
        }

        try {
            iniFile = new RandomAccessFile(fileName, "rw");
            this.fileName = fileName;
        }
        catch (Exception e) {
            throw new IOException("can not open file: \"" + fileName + "\"");
        }
    }

    public void close()
    {
        try {
            iniFile.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getSectionPos(String section)
    {
        int tempPos, readCount, secLen, i, ret = -1;
        byte[] buf = new byte[MAX_PATH];
        ByteParse bp = new ByteParse(buf);
        StringBuffer temp = new StringBuffer();;

        section = temp.append("[").append(section).append("]").toString();
        secLen = section.length();

        tempPos = 0;
        do {
            try {
                readCount = iniFile.read(buf, tempPos, MAX_PATH - tempPos);
            }
            catch (IOException e) {
                System.out.println("failed to read file");
                break;
            }
            if (0 < readCount) {
                readCount += tempPos;
                for (i = readCount; i < MAX_PATH; ++i) {
                    buf[i] = 0;
                }

                tempPos = bp.getIndex(section, 0);
                if (-1 == tempPos) {
                    if (readCount <= secLen) {
                        ret = -1;
                        break;
                    }
                    else {
                        System.arraycopy(buf, readCount - secLen, buf, 0, secLen);
                        tempPos = secLen;
                        continue;
                    }
                }
                else {
                    try {
                        ret = (int)iniFile.getFilePointer() - readCount + tempPos + secLen;
                        break;
                    }
                    catch (IOException e) {
                        System.out.println("failed to get file pointer");
                        ret = -1;
                        break;
                    }
                }
            }
            else if (-1 == readCount) {
                break;
            }
        } while (true);

        try {
            iniFile.seek(0);
        }
        catch (IOException e) {
            System.out.println("failed to reset file pointer");
        }

        return ret;
    }

    private int getNextSectionPos(long offset)
    {
        int tempPos, readCount, i, ret = -1;
        byte[] buf = new byte[MAX_PATH];
        ByteParse bp = new ByteParse(buf);

        if (0 < offset) {
            try {
                iniFile.seek(offset);
            }
            catch (IOException e) {
                System.out.println("file seek() error");
            }
        }

        do {
            try {
                readCount = iniFile.read(buf);
            }
            catch (IOException e) {
                System.out.println("failed to read file");
                break;
            }
            if (0 < readCount) {
                for (i = readCount; i < MAX_PATH; ++i) {
                    buf[i] = 0;
                }

                tempPos = bp.getIndex("[", 0);
                if (-1 != tempPos) {
                    try {
                        ret = (int)iniFile.getFilePointer() - readCount + tempPos - 1;
                    }
                    catch (IOException e) {
                        System.out.println("failed to get file pointer");
                    }
                    break;
                }
            }
            else {
                break;
            }
        } while (true);

        if (-1 == ret) {
            try {
                ret = (int)iniFile.getFilePointer();
            }
            catch (IOException e) {
                System.out.println("failed to get file pointer");
            }
        }

        try {
            iniFile.seek(0);
        }
        catch (IOException e) {
            System.out.println("failed to reset file pointer");
        }

        return ret;
    }

    public String getKeyValue(String sectionName, String key)
    {
        int sectionBgn, sectionEnd, sectionLen;
        int tempPos1, tempPos2, offset, length;
        byte[] buf = null;
        ByteParse bp = new ByteParse();
        String szKeyEx, value = null;

        if (null == sectionName || null == key) {
            return null;
        }

        sectionBgn = getSectionPos(sectionName);
        if (-1 == sectionBgn) {
            return null;
        }

        sectionEnd = getNextSectionPos(sectionBgn);
        if (-1 == sectionEnd) {
            return null;
        }

        sectionLen = sectionEnd - sectionBgn + 1;
        buf = new byte[sectionLen + 1];
        try {
            iniFile.seek(sectionBgn);
            length = iniFile.read(buf);
        }
        catch (IOException e) {
            try {
                iniFile.seek(0);
            }
            catch (IOException e2) {
                System.out.println("failed to reset file pointer");
            }
            System.out.println("IniReader operation error:" + e.getMessage());
            return null;
        }

        szKeyEx = new StringBuffer(key).append("=").toString();
        bp.setArray(buf);
        tempPos1 = bp.getIndex(szKeyEx, 0);
        if (-1 == tempPos1) {
            try {
                iniFile.seek(0);
            }
            catch (IOException e) {
                System.out.println("failed to reset file pointer");
            }
            buf = null;
            return null;
        }
        else {
            tempPos2 = bp.getIndex(LINE_SEP, tempPos1);

            offset = tempPos1 + szKeyEx.length();
            if (-1 != tempPos2) {
                length = tempPos2 - tempPos1 - szKeyEx.length();
                if ('\r' == buf[tempPos2 - 1]) {
                    --length;
                }
            }
            else {
                length -= (tempPos1 + szKeyEx.length());
            }

            value = new String(buf, offset, length);
        }

        try {
            iniFile.seek(0);
        }
        catch (IOException e) {
            System.out.println("failed to reset file pointer");
        }

        return value;
    }

    public void setKeyValue(String szSection, String szKey, String szValue) throws IOException, NullPointerException
    {
        if (null == szSection || null == szKey || null == szValue) {
            throw new NullPointerException("IniFile.setKeyValue() param null");
        }

        StringBuffer strBuf;
        String szKeyEx;
        int lSectionBgn, lSectionEnd, lSectionLength;
        byte[] szBuf;
        int iRplcBegin, iSrcLength, szSubStr, szKeyEnd;
        ByteParse bp;

        strBuf = new StringBuffer();
        lSectionBgn = getSectionPos(szSection);
        /* 没有[section] */
        if (-1 == lSectionBgn) {
            /* 文件已经打开(或者新建并以读写方式打开) */
            if (0 != rawFile.length()) {
                strBuf.append(LINE_SEP);
            }
            strBuf.append("[").append(szSection).append("]").append(LINE_SEP);
            strBuf.append(szKey).append("=").append(szValue);
            iniFile.seek(rawFile.length());
            iniFile.writeBytes(strBuf.toString());
            iniFile.close();
            iniFile = new RandomAccessFile(rawFile, "rw");
            return;
        }

        /* [section]已经存在 */
        lSectionEnd = getNextSectionPos(lSectionBgn);
        if (-1 == lSectionEnd) {
            strBuf.delete(0, strBuf.length());
            strBuf.append("Invalid Ini file content, can not find section[");
            strBuf.append(szSection).append("].");
            throw new IOException(strBuf.toString());
        }

        lSectionLength = lSectionEnd - lSectionBgn;
        szBuf = new byte[lSectionLength + 1];
        iniFile.seek(lSectionBgn);
        iniFile.read(szBuf, 0, lSectionLength);

        szKeyEx = szKey + "=";
        bp = new ByteParse(szBuf);
        szSubStr = bp.getIndex(szKeyEx.getBytes(), 0);
        /* [section]虽然存在,但是没有key= */
        if (-1 == szSubStr) {
            szBuf = null;
            /* 这里的2是因为：['=']1＋['\0']1＝3 */
            strBuf.append(LINE_SEP);
            strBuf.append(szKey).append("=").append(szValue);

            replaceText(lSectionEnd, 0, strBuf.toString());
            return;
        }
        /* [section]和key=都已经存在 */
        else {
            iRplcBegin = lSectionBgn + szSubStr + szKeyEx.length();
            szKeyEnd = bp.getIndex(LINE_SEP.getBytes(), szSubStr);
            if (-1 != szKeyEnd) {
                iSrcLength = szKeyEnd - szSubStr - szKeyEx.length();
                replaceText(iRplcBegin, iSrcLength, szValue);
            }
            else {
                szSubStr += szKeyEx.length();
                iSrcLength = lSectionLength - szSubStr;
                replaceText(iRplcBegin, iSrcLength, szValue);
            }
        }
    }

    /**
     * 把文件中从iRplcBegin开始一共iSrcLength长度的字符替换成字符串szString.
     */
    private void replaceText(int iRplcBegin, /* 替换的起始位置 */
                             int iSrcLength, /* 被替换的字符串的长度 */
                             String szString)
            throws IOException, NullPointerException
    {
        byte[] buf;
        int ret, overWrite;
        RandomAccessFile bakFile;
        File bakRawFile;

        if (null == szString) {
            throw new NullPointerException(
                    "IniFile.replaceText() parameter null.");
        }

        bakFile = new RandomAccessFile(fileName.concat(".bak"), "rw");
        buf = new byte[1024];
        iniFile.seek(0);
        overWrite = iRplcBegin;
        while (0 < overWrite && 0 < (ret = iniFile.read(buf, 0, overWrite))) {
            bakFile.write(buf, 0, ret);
            overWrite -= ret;
        }

        bakFile.writeBytes(szString);

        iniFile.seek(iRplcBegin + iSrcLength);
        while (0 < (ret = iniFile.read(buf))) {
            bakFile.write(buf, 0, ret);
        }

        bakFile.close();
        iniFile.close();

        rawFile.delete();
        rawFile = new File(fileName.concat(".bak"));
        bakRawFile = new File(fileName);
        if (false == rawFile.renameTo(bakRawFile)) {
            throw new IOException("failed to rename file");
        }
        rawFile = bakRawFile;
        iniFile = new RandomAccessFile(rawFile, "rw");
    }
}
