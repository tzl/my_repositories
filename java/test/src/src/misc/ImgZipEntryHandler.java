package misc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import util.RingList;

import util.zip.*;

public class ImgZipEntryHandler implements IEntryHandler
{
    private LineNumberReader lineReader = null;
    private final Pattern pat = Pattern.compile(".*(http://.*\\.jpe?g).*", Pattern.CASE_INSENSITIVE);
    private RingList imgList = new RingList(2);

    public int processFile(String fileName, InputStream fileStream)
    {
        String line;
        Matcher match;

        if (!fileName.endsWith(".txt")) {
            return 0;
        }

        lineReader = new LineNumberReader(new InputStreamReader(fileStream));
        try {
            while (null != (line = lineReader.readLine())) {
                match = pat.matcher(line);
                if (match.matches()) {
                    imgList.addEl(match.group(1));
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int processFolder(String zipFolderItem)
    {
        return 0;
    }

    public RingList getImageList()
    {
        return imgList;
    }
}
