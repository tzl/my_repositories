package util.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Zip
{
    /**
     * @brief
     *  解压zip文件到指定的目录下.
     *
     * @param zipFileName
     *  需要解压的zip文件名.
     * @param path
     *  zip文件要解压到的目录.
     */
    public static void unZip(String zipFileName, String path) throws IOException
    {
        UnpackHandler unpackHandler;
        File file = new File(zipFileName);

        /**
         * 检查zip源文件是否存在,不存在则退出.否则当目的文件夹不存在的情况下会调用
         * UnpackHandler的构造函数将创建目的文件夹,而这个文件夹不应该被创建.
         */
        if (!file.exists()) {
            throw new IOException("can not find zip file - " + zipFileName);
        }

        unpackHandler = new UnpackHandler(path);
        processEntries(zipFileName, unpackHandler);
    }

    /**
     * @brief
     *  处理压缩文件.
     *
     * @param zipFileName
     *  需要处理的zip文件全名.
     * @param entryHandler
     *  指定的处理zip文件的处理接口.
     * @return
     *  如果遍历正常结束返回0;
     *  如果(entryHandler处理过程中)出现错误返回负数;
     *  如果entryHandler处理过程中希望中断处理返回正数.
     */
    public static int processEntries(String zipFileName, IEntryHandler entryHandler)
    {
        ZipInputStream zipIs;
        ZipEntry zipEntry;
        int ret;

        if (null == entryHandler) {
            return -1;
        }

        try {
            /*打开zip文件*/
            zipIs = new ZipInputStream(new FileInputStream(zipFileName));

            /*遍历zip文件中的每一个条目, 读出来创建文件或文件夹到目的目录*/
            do {
                zipEntry = zipIs.getNextEntry();
                if (null == zipEntry) {
                    ret  = 0;
                    break;
                }

                /*处理zip文件中的目录或文件*/
                ret = entryHandler.process(zipIs, zipEntry);
                if (0 != ret) {
                    zipIs.closeEntry();
                    break;
                }

                /*每一次操作完一个条目都要把条目关闭*/
                zipIs.closeEntry();
            } while (true);

            entryHandler.postProcess(zipIs);

            /*关闭zip文件*/
            zipIs.close();
        }
        catch (IOException ioe) {
            ret = -1;
            ioe.printStackTrace();
        }

        return ret;
    }
}

class UnpackHandler extends IEntryHandler
{
    private String unpackPath;
    private final StringBuffer strBuf = new StringBuffer();
    private File file;
    public static boolean debug = true;

    private UnpackHandler() {}

    public UnpackHandler(String path) throws IOException
    {
        this();

        /*先检查目的文件夹参数,如果有效建立文件夹*/
        if (null == path || path.isEmpty()) {
            path = "";
        }
        else if (!path.endsWith(File.separator)) {
            path = path.concat(File.separator);
            File file = new File(path);
            if (!file.isDirectory() && !file.mkdirs()) {
                throw new IOException("can not create folder " + path);
            }
        }

        unpackPath = path;
    }

    public int process(ZipInputStream zis, ZipEntry entry)
    {
        int ret;
        String itemName;

        itemName = entry.getName();

        if (entry.isDirectory()) {
            ret = processFolder(itemName);
        }
        else {
            ret = processFile(itemName, zis);
        }

        return ret;
    }

    private int processFile(String fileName, ZipInputStream fileStream)
    {
        int ret;
        FileOutputStream fileOs = null;
        File file;
        byte[] buf = new byte[1024];

        if (debug) {
            strBuf.delete(0, strBuf.length());
            strBuf.append("unzip file - ").append(fileName);
            strBuf.append(" to directory ").append(unpackPath);
            System.out.println(strBuf.toString());
        }

        /*生成要创建的文件的绝对路径*/
        strBuf.delete(0, strBuf.length());
        strBuf.append(unpackPath).append(fileName);

        /*创建文件*/
        try {
            file = new File(strBuf.toString());
            fileOs = new FileOutputStream(file);
            while (0 < (ret = fileStream.read(buf))) {
                fileOs.write(buf, 0, ret);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (null != fileOs) {
                try {
                    fileOs.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return 0;
    }

    private int processFolder(String zipFolderItem)
    {
        int ret;

        if (debug) {
            strBuf.delete(0, strBuf.length());
            strBuf.append("unzip fodler - ").append(zipFolderItem);
            strBuf.append(" to directory ").append(unpackPath);
            System.out.println(strBuf.toString());
        }

        /*生成要创建的目录的绝对路径*/
        strBuf.delete(0, strBuf.length());
        strBuf.append(unpackPath).append(zipFolderItem);

        /*创建目录*/
        file = new File(strBuf.toString());
        ret = file.mkdirs()? 0: 1;

        return ret;
    }
}
