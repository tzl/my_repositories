package com.my.util;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.*;
import android.net.Uri;
import android.os.*;
import android.text.TextUtils;

import java.io.File;
import java.util.regex.*;

public class Downloader {
    // private static final String sTag = "Downloader";
    private DownloadManager mDownloadMgr;
    private final Pattern mFileNamePat = Pattern.compile(".*\\/(.*)");
    private final Pattern sAcceptUrlPat = Pattern.compile("^(https?)://.*");
    private Handler mHandler;
    private ContentResolver mContentResolver;
    private static final int MSG_DOWNLOAD = 1;
    private final String mDownloadDir;

    private static final class SingletonHolder {
        private static Downloader sInstance = new Downloader();
    }

    private Downloader() {
        mDownloadDir = Environment.getExternalStorageDirectory() + File.separator +
            Environment.DIRECTORY_DOWNLOADS + File.separator;
    }

    public static Downloader getInstance() {
        return SingletonHolder.sInstance;
    }

    public void init(Context context) {
        if (null == mDownloadMgr && null != context) {
            mDownloadMgr = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
            mContentResolver = context.getContentResolver();

            HandlerThread downloader = new HandlerThread("downloader");
            downloader.start();
            mHandler = new Handler(downloader.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (MSG_DOWNLOAD == msg.what) {
                        MsgArgument arg = (MsgArgument)msg.obj;
                        download0(arg.url, arg.callback, arg.destFileName);
                    }
                }
            };
        }
    }

    public void download(String url, String filePath, DownloaderListener observer) {
        if (null == mDownloadMgr) {
            actionFailed(observer, url + " [error: call init() first]");
            return;
        }

        boolean isUrlValid = false;
        if (null != url) {
            Matcher matcher = sAcceptUrlPat.matcher(url);
            isUrlValid = matcher.matches();
        }

        if (isUrlValid) {
            MsgArgument arg = new MsgArgument();
            arg.url = url;
            arg.callback = observer;
            arg.destFileName = filePath;
            mHandler.obtainMessage(MSG_DOWNLOAD, arg).sendToTarget();
        }
        else {
            actionFailed(observer, url);
        }
    }

    private void download0(String url, DownloaderListener lsnr, String destFileName) {
        String fileName = TextUtils.isEmpty(destFileName)? getFileName(url): destFileName;
        if (null == fileName) {
            actionFailed(lsnr, url);
            return;
        }

        fileName = makeSurePathExist(fileName);
        if (null == fileName) {
            actionFailed(lsnr, url);
            return;
        }

        try {
            Uri uri = Uri.parse(url);
            DownloadManager.Request req = new DownloadManager.Request(uri);
            req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            long downloadId = mDownloadMgr.enqueue(req);

            DownloadObserver observer = new DownloadObserver(mHandler, url, downloadId, lsnr);
            uri = Uri.parse("content://downloads/my_downloads/" + downloadId);
            mContentResolver.registerContentObserver(uri, true, observer);
        }
        catch (Exception e) {
            actionFailed(lsnr, url);
            e.printStackTrace();
        }
    }

    private void actionFailed(DownloaderListener observer, String url) {
        if (null != observer) {
            observer.onProgress(url, DownloadManager.STATUS_FAILED, 0, 0);
        }
    }

    private String getFileName(String url) {
        Matcher matcher = mFileNamePat.matcher(url);
        return matcher.matches()? matcher.group(1): null;
    }

    /**
     * 返回相对于“/Download/“的相对路径文件名字
     */
    private String makeSurePathExist(String path) {
        String root = path.startsWith(mDownloadDir)? null: mDownloadDir;
        File file = new File(root, path);
        File dir = file.getParentFile();

        dir.mkdirs();
        if (!dir.isDirectory()) {
            return null;
        }
        else {
            return null == root? path.substring(mDownloadDir.length()): path;
        }
    }

    private final class DownloadObserver extends ContentObserver {
        private long mId;
        private DownloaderListener mListener;
        private String mUrl;
        private final int mDoneFlag = DownloadManager.STATUS_SUCCESSFUL | DownloadManager.STATUS_FAILED;

        DownloadObserver(final Handler handler, String url, long downloadId, DownloaderListener lsnr) {
            super(handler);
            mUrl = url;
            mId = downloadId;
            mListener = lsnr;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            probeStatus(mId);
        }

        private void probeStatus(long id) {
            Cursor cursor = null;
            try {
                cursor = mDownloadMgr.query(new DownloadManager.Query().setFilterById(id));
                if (null == cursor || cursor.getCount() == 0 || !cursor.moveToFirst()) {
                    // Log.w(sTag, "error2");
                    return;
                }

                final int totalBytesColumnIndex = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                final int currentBytesIndex = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                final int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);

                final long total = cursor.getLong(totalBytesColumnIndex);
                final long current = cursor.getLong(currentBytesIndex);
                final int status = cursor.getInt(statusIndex);

                //通知调用者
                if (null != mListener) {
                    mListener.onProgress(mUrl, status, total, current);
                }

                //解除注册
                if (0 != (mDoneFlag & status)) {
                    // String log = String.format("[%d] end->(%d)", id, status);
                    // Log.i(sTag, log);
                    mListener = null;
                    mContentResolver.unregisterContentObserver(this);
                }
            }
            catch (Exception e) {
                // Log.w(sTag, "error3");
                //解除注册
                mListener = null;
                mContentResolver.unregisterContentObserver(this);
            }
            finally {
                if (null != cursor) {
                    cursor.close();
                }
            }
        }
    }

    public interface DownloaderListener {
        void onProgress(String url, int status, long total, long current);
    }

    private class MsgArgument {
        String url; //download uri
        DownloaderListener callback;
        String destFileName; //Destination path(file) name relative to "Environment.DIRECTORY_DOWNLOADS"
    }
}
