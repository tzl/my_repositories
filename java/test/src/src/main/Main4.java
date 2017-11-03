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

public class Main4
{
    private static void usage()
    {
        StringBuffer strBuf = new StringBuffer();

        strBuf.append("function\n========");
        strBuf.append("\n\t1:解压目录中的所有的zip文件到指定目录.");
        strBuf.append("\n\t2:删除目录中的所有目录(这些目录有同名的zip文件对应).");
        strBuf.append("\nusage\n========");
        strBuf.append("\n\tjava main.Main4 path_container {del|expd}");

        System.out.println(strBuf.toString());
    }

    public static void main(String[] args)
    {
        File dir;
        File[] files = null;
        String path, action;
        int i, ret;

        if (2 > args.length) {
            usage();
            return;
        }

        path = args[0];
        action = args[1];

        dir = new File(path);
        boolean[] mask = new boolean[4];
        mask[0] = dir.isDirectory();
        mask[1] = dir.exists();
        mask[2] = action.equalsIgnoreCase("del");
        mask[3] = action.equalsIgnoreCase("expd");
        if (!mask[0] || !mask[1] || !mask[2] && !mask[3]) {
            for (boolean t: mask) {
                System.out.println("mask-" + t);
            }
            usage();
            return;
        }

        files = dir.listFiles(ZipFileFilter.getInstance());
        if (null == files || 0 == files.length) {
            return;
        }

        try {
            if (action.equalsIgnoreCase("expd")) {
                for (i = 0; i < files.length; ++i) {
                    if (files[i].isFile()) {
                        System.out.println("zip file------- " + files[i].getName());
                        Zip.unZip(files[i].getAbsolutePath(), path);
                    }
                }
            }
            else {
                for (i = 0; i < files.length; ++i) {
                    if (files[i].isFile()) {
                        String folder = files[i].getAbsolutePath();
                        folder = folder.substring(0, folder.length() - 4);
                        System.out.println("to delete folder:" + folder);
                        deleteFolder(folder);
                    }
                }
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static void deleteFolder(String dir) throws IOException
    {
        if (null == dir || dir.isEmpty()) {
            return;
        }

        File folder;
        File[] files;
        int i, len;

        folder = new File(dir);
        files = folder.listFiles();
        if (null != files) {
            len = files.length;
            for (i = 0; i < len; ++i) {
                if (files[i].isFile()) {
                    System.out.println("delete file: " + files[i].getAbsolutePath());
                    files[i].delete();
                }
                else {
                    deleteFolder(files[i].getAbsolutePath());
                }
            }
        }

        System.out.println("delete folder: " + folder.getAbsolutePath());
        folder.delete();
    }
}

class ZipFileFilter implements FileFilter
{
    private static ZipFileFilter _instance = null;
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

    public static ZipFileFilter getInstance()
    {
        if (null == _instance) {
            _instance = new ZipFileFilter();
        }

        return _instance;
    }
}
