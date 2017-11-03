package misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import util.zip.IEntryHandler;

public class EclipseDocHanlder extends IEntryHandler
{
    private static final Pattern pat = Pattern.compile(".*\\.(htm(|l)|png|css)$", Pattern.CASE_INSENSITIVE);
    private String _toFolder;

    private EclipseDocHanlder()
    {
    }

    public EclipseDocHanlder(String toFolder) throws IOException
    {
        this();
        File file = new File(toFolder);
        if (!file.isDirectory()) {
            if (false == file.mkdirs()) {
                StringBuffer strBuf = new StringBuffer("Directory ");
                strBuf.append(toFolder).append(" does not exist and we failed to create it.");
                throw new IOException(strBuf.toString());
            }
        }

        if (!toFolder.endsWith(File.separator)) {
            _toFolder = toFolder + File.separator;
        }
        else {
            _toFolder = toFolder;
        }
    }

    public int processFile(String fileName, InputStream fileStream)
    {
        Matcher match;
        byte[] buf = new byte[1024];
        FileOutputStream fos = null;
        int ret;

        match = pat.matcher(fileName);
        if (match.matches()) {
            System.out.println("--  " + fileName);

            try {
                fileName = _toFolder + fileName;
                if (0 > makeParentPath(fileName)) {
                    System.out.println("failed to create parent folder.");
                }
                fos = new FileOutputStream(fileName);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                while (0 < (ret = fileStream.read(buf))) {
                    fos.write(buf, 0, ret);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    private int makeParentPath(String fileName)
    {
        String path;
        int pos;

        if (null == fileName || fileName.isEmpty()) {
            return -1;
        }

        pos = fileName.lastIndexOf("/");
        if (-1 == pos) {
            pos = fileName.lastIndexOf("\\");
            if (-1 == pos) {
                return 0;
            }
        }

        path = fileName.substring(0, pos);
        File dir = new File(path);
        if (dir.isDirectory()) {
            return 0;
        }
        else if (!dir.mkdirs()) {
            return -1;
        }
        else {
            return 1;
        }
    }

    public int processFolder(String zipFolderItem)
    {
        //    	File dir = new File(_toFolder + zipFolderItem);
        //    	System.out.println("new folder-- " + _toFolder + zipFolderItem);
        //    	dir.mkdirs();
        return 0;
    }

    public void setExtractFolder(String toFolder)
    {
        if (null == toFolder) {
            toFolder = "." + File.separator;
        }

        if (!toFolder.endsWith(File.separator)) {
            toFolder = toFolder.concat(File.separator);
        }

        _toFolder = toFolder;
    }

    @Override public int process(ZipInputStream zis, ZipEntry entry)
    {
        // TODO Auto-generated method stub
        return 0;
    }
}
