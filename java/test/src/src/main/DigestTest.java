/**
 * @file DigestTest.java
 */

package main;

import java.io.File;
import java.io.FileFilter;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import misc.CollectFileDigest;

public class DigestTest
{
    private static int _dupCount = 0;
    public static void main(String[] args)
    {
        String container;
        CollectFileDigest digestCollector = null;
        TreeMap<byte[], Vector<String>> biTree;
        Iterator<Map.Entry<byte[], Vector<String>>> itr;
        Map.Entry<byte[], Vector<String>> entry;

        if (1 > args.length) {
            System.out.println("usage: java DigestTest src_dir");
            return;
        }

        container = args[0];
        try {
            digestCollector = new CollectFileDigest(container, ImageFileFilter.getInstance(), null);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        biTree = digestCollector.getEntries();

        synchronized (biTree) {
            int total, dup, temp;

            total = dup = 0;
            itr = biTree.entrySet().iterator();
            while (itr.hasNext()) {
                entry = itr.next();
                temp = printEntry(entry);
                total += temp;
                dup += (temp - 1);
            }

            System.out.println("total:" + total + ", dup:" + dup);
        }
    }

    private static void renameFile(String src, String dstDir)
    {
        File fileSrc = new File(src);
        File fileDst = new File(dstDir + src.substring(src.lastIndexOf("/")));
        fileSrc.renameTo(fileDst);
    }

    private static final String _dstDir = "/Users/tzl/Pictures/2013-06-01";
    public static int printEntry(Map.Entry<byte[], Vector<String>> entry)
    {
        byte[] digest;
        Vector<String> fileNames;
        String fileName;
        int i = 0;
        Iterator<String> itr;

        digest = entry.getKey();
        fileNames = entry.getValue();

        if (1 < fileNames.size()) {
            System.out.println("--------entry--------" + ++_dupCount);

            util.HexaUtil.printArray(digest, 0, digest.length, null);

            itr = fileNames.iterator();
            if (itr.hasNext()) {
                fileName = itr.next();
                System.out.println("\t" + ++i + ": " + fileName);
                // renameFile(fileName, _dstDir);
            }

            while (itr.hasNext()) {
                fileName = itr.next();
                System.out.println("\t" + ++i + ": " + fileName);
                new File(fileName).delete();
            }
        }

        return i;
    }
}

class ImageFileFilter implements FileFilter
{
    private static ImageFileFilter _instance = null;
    private static Pattern norm_pat = Pattern.compile(".*\\.(jpg|png|jpeg)$",
        Pattern.CASE_INSENSITIVE);
    private static Pattern spec_pat = Pattern.compile("^(.|_)(svn|cvs)$",
        Pattern.CASE_INSENSITIVE);

    @Override public boolean accept(File file)
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

    public static ImageFileFilter getInstance()
    {
        if (null == _instance) {
            _instance = new ImageFileFilter();
        }

        return _instance;
    }
}
