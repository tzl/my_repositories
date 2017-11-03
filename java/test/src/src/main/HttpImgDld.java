/**
 * @file HttpImgDld.java
 */

package main;

import java.io.File;

import misc.HttpDownloader;
import misc.ImgZipEntryHandler;
import util.RingList;
import util.threadpool.TaskManager;
import util.zip.Zip;

public class HttpImgDld
{
    public static void main(String[] args)
    {
        RingList imgList;
        String imgUrl, zipFile, imgDir;
        File dir;
        TaskManager taskMgr;
        HttpDownloader downloader;
        int index, total;

        if (1 > args.length) {
            System.out.println("usage: java HttpImgDld src_dir [img_dir]");
            return;
        }

        imgDir = 2 <= args.length? args[1]: "images";
        zipFile = args[0];

        dir = new File(imgDir);
        if (!dir.isDirectory()) {
            if (!dir.mkdirs()) {
                System.out.println("Can not create dir " + imgDir);
                return;
            }
        }

        ImgZipEntryHandler handler = new ImgZipEntryHandler();
        Zip.processEntries(zipFile, handler);
        imgList = handler.getImageList();

        taskMgr = TaskManager.create(20);

        total = imgList.getSize();
        index = 1;
        do {
            imgUrl = (String)imgList.getEl(null);
            if (null != imgUrl) {
                downloader = new HttpDownloader(imgUrl, imgDir, index++, total);
                taskMgr.performTask(downloader);
            }
            else {
                break;
            }
        } while (true);


        taskMgr.destroy();
//        try {
//            Zip.unZip(processing-js-0.9.1-examples.zip", "my_zip_folder");
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
