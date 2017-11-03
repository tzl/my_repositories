package misc;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.Vector;

public class CollectFileDigest
{
    private TreeMap<byte[], Vector<String>> _digestMap;
    private File _container;
    private FileFilter _filter;
    private static MessageDigest _msgDigest = null;
    public static boolean _debug = false;

    static {
        try {
            _msgDigest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private CollectFileDigest() {}
    public CollectFileDigest(String folder,
                             FileFilter filter,
                             TreeMap<byte[], Vector<String>> digestEntries)
        throws IllegalArgumentException, NoSuchAlgorithmException
    {
        this();
        _container = new File(folder);

        if (!_container.isDirectory()) {
            throw new IllegalArgumentException("folder does NOT exist.");
        }

        _filter = filter;

        if (null == digestEntries) {
            Comparator<byte[]> cmp = TreeElComparator.getInstance();
            _digestMap = new TreeMap<byte[], Vector<String>>(cmp);
        }
        else {
            _digestMap = digestEntries;
        }

        if (null == _msgDigest) {
            _msgDigest = MessageDigest.getInstance("MD5");
        }
    }

    public TreeMap<byte[], Vector<String>> getEntries()
    {
        updateEntries();

        return _digestMap;
    }

    private void updateEntries()
    {
        Vector<String> fileNames;
        String fileName;
        File[] list;
        byte[] digest;
        int i;

        if (null == _filter) {
            list = _container.listFiles();
        }
        else {
            list = _container.listFiles(_filter);
        }

        for (i = list.length - 1; i >= 0; --i) {
            /*递归*/
            if (list[i].isDirectory()) {
                CollectFileDigest nestActor = null;

                if (_debug) {
                    System.out.println("***** folder: " + list[i].getAbsolutePath());
                }

                try {
                    nestActor = new CollectFileDigest(list[i].getAbsolutePath(), _filter, _digestMap);
                }
                catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                if (null != nestActor) {
                    nestActor.updateEntries();
                }

                continue;
            }

            if (_debug) {
                System.out.println("\t" + list[i].getAbsolutePath());
            }

            digest = getDigest(list[i]);
            if (null == digest) {
                continue;
            }
            fileName = list[i].getAbsolutePath();

            synchronized (_digestMap) {
                fileNames = (Vector<String>)_digestMap.get(digest);
                if (null == fileNames) {
                    fileNames = new Vector<String>();
                    fileNames.add(fileName);
                    _digestMap.put(digest, fileNames);
                }
                else if (!fileNames.contains(fileName)) {
                    fileNames.add(fileName);
                }
            }
        }
    }

    private byte[] getDigest(File file)
    {
        FileInputStream fis = null;
        DigestInputStream dis;
        byte[] buf = new byte[1024];
        Exception error = null;
        int ret;

        try {
            fis = new FileInputStream(file);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            error = e;
        }

        if (null != error) {
            return null;
        }

        dis = new DigestInputStream(fis, _msgDigest);
        dis.on(true);

        try {
            do {
                ret = dis.read(buf);
            } while (0 < ret);
        }
        catch (IOException e) {
            e.printStackTrace();
            error = e;
        }
        finally {
            if (null != fis) {
                try {
                    fis.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    error = e;
                }
            }
        }

        if (null != error) {
            return null;
        }

        return _msgDigest.digest();
    }

    public static class TreeElComparator implements Comparator<byte[]>
    {
        private static TreeElComparator _instance = null;

        private TreeElComparator() {}

        public int compare(byte[] digest1, byte[] digest2)
        {
            int length, i, delt;

            if (null == digest1) {
                return null == digest2? 0: -1;
            }
            else if (null == digest2) {
                return 1;
            }

            delt = digest1.length - digest2.length;
            if (0 != delt) {
                return delt;
            }

            length = Math.min(digest1.length, digest1.length);

            for (i = 0; i < length; ++i) {
                delt = digest1[i] - digest2[i];
                if (0 != delt) {
                    return delt;
                }
            }

            return digest1.length - digest2.length;
        }

        public static TreeElComparator getInstance()
        {
            if (null == _instance) {
                _instance = new TreeElComparator();
            }

            return _instance;
        }
    }
}
