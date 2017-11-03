/**
 * @file SvnItemDeleter.java
 */

package util.zip;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class SvnItemDeleter extends IEntryHandler
{
    private static Pattern _pat = Pattern.compile(".*(.|_)(svn|cvs)/?.*");
    private ZipOutputStream _zos = null;

    public SvnItemDeleter(String zipFileName) throws IOException
    {
        FileOutputStream fos = null;
        String tempFileName;

        if (null == zipFileName) {
            throw new IOException("invalid zip file name");
        }
        tempFileName = zipFileName + ".bak";

        fos = new FileOutputStream(tempFileName);
        _zos = new ZipOutputStream(fos);
    }

    public int process(ZipInputStream zis, ZipEntry entry)
    {
        String itemName = entry.getName();
        Matcher matcher = _pat.matcher(itemName);
        byte[] buf = new byte[1000];

        if (matcher.matches()) {
            System.out.println(itemName);
            return 0;
        }

        try {
            int ret;

            _zos.putNextEntry(entry);

            ret = zis.read(buf);
            while (0 < ret) {
                _zos.write(buf, 0, ret);
                ret = zis.read(buf);
            }
            _zos.closeEntry();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return 0;
    }

    public int postProcess(ZipInputStream fileStream)
    {
        if (null != _zos) {
            try {
                _zos.close();
            }
            catch (IOException ioe) {
            }
        }

        return 0;
    }
}
