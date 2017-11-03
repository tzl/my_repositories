package misc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.zip.*;

public class ImgZipEntryHandler implements IEntryHandler
{
    public int processFile(String fileName, InputStream fileStream)
    {
        System.out.println("file:-->"+zipFolderItem+"<--")
        return 0;
    }

    public int processFolder(String zipFolderItem)
    {
        System.out.println("folder:-->"+zipFolderItem+"<--")
        return 0;
    }
}
