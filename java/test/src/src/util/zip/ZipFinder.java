/**
 * @file ZipFinder.java
 */

package util.zip;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipFinder extends IEntryHandler
{
    private String _needle;
    private RandomAccessFile _fRecord;

    /**
     * @param needle
     *  在zip中要查找的文件的名字(非目录名).
     * @param fRecord
     *  记录查找日志的文件.
     *   文件中记录的内容格式如下:
     *  +---------------------------+
     *  | zip file------- a.zip     |
     *  | --f-- Main1.java          |
     *  | --f-- Main2.java          |
     *  | --d-- a/                  |
     *  | --d-- b/                  |
     *  | --f-- a/folder.cnt        |
     *  +---------------------------+
     */
    public ZipFinder(String needle, RandomAccessFile fRecord)
    {
        _needle = needle;
        _fRecord = fRecord;
    }

    public int process(ZipInputStream zis, ZipEntry entry)
    {
        String itemName = entry.getName();

        if (entry.isDirectory()) {
            System.out.println("--d-- " + itemName);

            if (null != _fRecord) {
                try {
                    _fRecord.writeBytes("    --d-- " + itemName + "\n");
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        else {
            System.out.println("--f-- " + itemName);

            if (null != _fRecord) {
                try {
                    _fRecord.writeBytes("    f: " + itemName + "\n");
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }

            if (itemName.endsWith(_needle)) {
                return 1;
            }
        }

        return 0;
    }
}
