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
     *  ��ѹzip�ļ���ָ����Ŀ¼��.
     *
     * @param zipFileName
     *  ��Ҫ��ѹ��zip�ļ���.
     * @param path
     *  zip�ļ�Ҫ��ѹ����Ŀ¼.
     */
    public static void unZip(String zipFileName, String path) throws IOException
    {
        UnpackHandler unpackHandler;
        File file = new File(zipFileName);

        /**
         * ���zipԴ�ļ��Ƿ����,���������˳�.����Ŀ���ļ��в����ڵ�����»����
         * UnpackHandler�Ĺ��캯��������Ŀ���ļ���,������ļ��в�Ӧ�ñ�����.
         */
        if (!file.exists()) {
            throw new IOException("can not find zip file - " + zipFileName);
        }

        unpackHandler = new UnpackHandler(path);
        processEntries(zipFileName, unpackHandler);
    }

    /**
     * @brief
     *  ����ѹ���ļ�.
     *
     * @param zipFileName
     *  ��Ҫ�����zip�ļ�ȫ��.
     * @param entryHandler
     *  ָ���Ĵ���zip�ļ��Ĵ���ӿ�.
     * @return
     *  �������������������0;
     *  ���(entryHandler���������)���ִ��󷵻ظ���;
     *  ���entryHandler���������ϣ���жϴ���������.
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
            /*��zip�ļ�*/
            zipIs = new ZipInputStream(new FileInputStream(zipFileName));

            /*����zip�ļ��е�ÿһ����Ŀ, �����������ļ����ļ��е�Ŀ��Ŀ¼*/
            do {
                zipEntry = zipIs.getNextEntry();
                if (null == zipEntry) {
                    ret  = 0;
                    break;
                }

                /*����zip�ļ��е�Ŀ¼���ļ�*/
                ret = entryHandler.process(zipIs, zipEntry);
                if (0 != ret) {
                    zipIs.closeEntry();
                    break;
                }

                /*ÿһ�β�����һ����Ŀ��Ҫ����Ŀ�ر�*/
                zipIs.closeEntry();
            } while (true);

            entryHandler.postProcess(zipIs);

            /*�ر�zip�ļ�*/
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

        /*�ȼ��Ŀ���ļ��в���,�����Ч�����ļ���*/
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

        /*����Ҫ�������ļ��ľ���·��*/
        strBuf.delete(0, strBuf.length());
        strBuf.append(unpackPath).append(fileName);

        /*�����ļ�*/
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

        /*����Ҫ������Ŀ¼�ľ���·��*/
        strBuf.delete(0, strBuf.length());
        strBuf.append(unpackPath).append(zipFolderItem);

        /*����Ŀ¼*/
        file = new File(strBuf.toString());
        ret = file.mkdirs()? 0: 1;

        return ret;
    }
}
