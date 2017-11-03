/**
 * @file ZangKuImages.java
 */

package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import misc.ExtractTxtZipUrl;
import misc.HttpDownloader;
import util.threadpool.TaskManager;

public class ZangKuImages
{
    public static void main(String[] args)
    {
        File toDir, htmlFile;
        HttpDownloader downloader;
        FileOutputStream fos = null;
        File[] htmlFiles;
        Vector<String> urlList;
        ExtractTxtZipUrl extractor;
        Iterator<String> itr;
        String url, lineSep = new String("\r\n");
        int i, k, total;

        if (1 > args.length) {
            System.out.println("usage:\n\tZangKuImages toDir");
            return;
        }

        toDir = new File(args[0]);
        if (!toDir.isDirectory() && !toDir.mkdirs()) {
            System.out.println("failed to create destination directory.");
            return;
        }

        //œ¬‘ÿhtml index “≥√Ê(74“≥)
        donwloadHtml(toDir.getAbsolutePath());

        try {
            fos = new FileOutputStream(toDir.getAbsolutePath() + File.separator + "index.txt");
        }
        catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return;
        }
        htmlFiles = toDir.listFiles();

        TaskManager taskMgr = TaskManager.create(20);

        try {
            for (i = 0; i < htmlFiles.length; ++i) {
                htmlFile = htmlFiles[i];
                extractor = new ExtractTxtZipUrl(htmlFile.getAbsolutePath());

                urlList = extractor.genZipTxtList();
                total = urlList.size();
                k = 1;

                itr = urlList.iterator();
                while (itr.hasNext()) {
                    url = itr.next();

                    System.out.println("-- " + url);
                    fos.write(url.getBytes(), 0, url.length());
                    fos.write(lineSep.getBytes(), 0, lineSep.length());

                    downloader = new HttpDownloader(url, toDir.getAbsolutePath(), k++, total);
                    taskMgr.performTask(downloader);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (null != fos) {
                try {
                    fos.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        taskMgr.destroy();
    }

    private static void donwloadHtml(String toDir)
    {
        int i;
        String htmlUrl;
        HttpDownloader downloader;

        TaskManager taskMgr = TaskManager.create(20);

        for (i = 1; i <= 74; ++i) {
            htmlUrl = genIndexUrl(i);
            downloader = new HttpDownloader(htmlUrl, toDir, i, 74);
            taskMgr.performTask(downloader);
        }

        taskMgr.destroy();
    }

    private static String genIndexUrl(int index)
    {
        StringBuffer strBuf = new StringBuffer();

        strBuf.append("http://www.tieku.org/index_1000_");
        strBuf.append(index).append("_1.html");

        return strBuf.toString();
    }
}
