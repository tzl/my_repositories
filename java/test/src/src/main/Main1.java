/**
 * @file Main1.java
 */

package main;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.zip.Zip;
import util.zip.ZipFinder;

public class Main1
{
    public static void main(String[] args)
    {
        File dir;
        File[] files = null;
        String path, fileToFind;
        ZipFinder finder;
        RandomAccessFile fRecord = null;
        int i, ret;

        if (2 > args.length) {
            StringBuffer strBuf = new StringBuffer();

            strBuf.append("function\n========");
            strBuf.append("\n\t在目录和目录中的zip文件中查找指定名字的文件.");
            strBuf.append("\nusage\n========");
            strBuf.append("\n\tjava main.Main1 path_container file_to_find");

            System.out.println(strBuf.toString());
            return;
        }

        path = args[0];
        fileToFind = args[1];

        dir = new File(path);
        files = dir.listFiles(JavaFileFilter.getInstance());
        if (null == files || 0 == files.length) {
            return;
        }

        try {
            fRecord = new RandomAccessFile(path + "/folder.cnt", "rw");
            finder = new ZipFinder(fileToFind, fRecord);

            fRecord.setLength(0);
            fRecord.writeBytes("==========root===========\n" + path + "\n");
            fRecord.writeBytes("=========================\n");

            for (i = 0; i < files.length; ++i) {
                if (files[i].isFile()) {
                    System.out.println("zip file------- " + files[i].getName());
                    fRecord.writeBytes("zip ----  " + files[i].getName() + "\n");
                    ret = Zip.processEntries(files[i].getAbsolutePath(), finder);
                    if (0 < ret) {
                        break;
                    }
                }
                else {
                    System.out.println("--------------folder " + files[i].getName());
                    fRecord.write(12);
                    fRecord.writeBytes("subdirectory ----  " + files[i].getName() + "\n");
                }
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            if (null != fRecord) {
                try {
                    fRecord.close();
                }
                catch (IOException ioe) {
                }
            }
        }
    }
}

class JavaFileFilter implements FileFilter
{
    private static JavaFileFilter _instance = null;
    private static Pattern norm_pat = Pattern.compile(".*\\.(jar|zip)$",
            Pattern.CASE_INSENSITIVE);
    private static Pattern spec_pat = Pattern.compile("^(.|_)(svn|cvs)$",
            Pattern.CASE_INSENSITIVE);
    Matcher matcher = null;

    public boolean accept(File file)
    {
        Matcher matcher;
        boolean ret;

        if (file.isFile()) {
            matcher = norm_pat.matcher(file.getName());
            ret = matcher.matches();
        }
        else {
            matcher = spec_pat.matcher(file.getName());
            ret = !matcher.matches();
        }

        return ret;
    }

    public static JavaFileFilter getInstance()
    {
        if (null == _instance) {
            _instance = new JavaFileFilter();
        }

        return _instance;
    }
}
