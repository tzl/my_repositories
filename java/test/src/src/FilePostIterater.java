
import java.io.File;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Arrays;

public class FilePostIterater
{
    private volatile boolean mRunning = false, mAvailable = true, mWidthFirst;
    private String mRootDir;
    private Handler mFileHandler;
    private Object mParam;

    public static void main(String args[])
    {
        Handler handler = new Handler() {
                @Override
                public boolean callback(File file, boolean isDirectory, Object param) {
                    try {
                        System.out.println("handle -- (" + isDirectory + ") " + file.getAbsolutePath());
                        if (!file.delete()) {
                            System.out.println("delete file error: (" + isDirectory + ") " + file.getAbsolutePath());
                            return false;
                        }
                    }
                    catch (Exception e) {
                        System.out.println("delete file error:" + e.getLocalizedMessage());
                    }
                    return true;
                }
            };

        System.out.println("program start");
        FilePostIterater fp = new FilePostIterater();
        fp.mFileHandler = handler;
        fp.mRootDir = "/Users/tianzhenglu/workspace/java/ddd";
        fp.postIterate();
        System.out.println("program end");


        byte[] ddt = {0};
        System.out.println("---" + ddt.getClass().toString());
    }

    public static interface Handler {
        /**
         * @param param the customer data set to {@link FileIterator}.
         * @return false: stop iterating.
         * true: go on iterating.
         */
        boolean callback(File file, boolean isDirectory, Object param);
    }

    private synchronized boolean waitForStop() {
        while (mRunning) {
            mRunning = false;

            try {
                wait();
            }
            catch (InterruptedException e) {
                return false;
            }
        }

        mRunning = true;

        return true;
    }

    private synchronized void notityFree() {
        mRunning = false;
        mAvailable = true;
        notifyAll();
    }

    /**
     * 先处理文件,再处理目录;处理文件的先后顺序是先处理当前目录再处理子目录中的文件, 处理
     * 目录的顺序和处理文件的顺序相反,是先处理子目录中的目录再处理当前目录.
     */
    public int postIterate() {
        if (!waitForStop() || null == mRootDir || null == mFileHandler) {
            notityFree();
            return 0;
        }

        File f = new File(mRootDir);
        if (!f.exists()) {
            notityFree();
            return 0;
        }

        int ret = 1;
        boolean isDirectory = f.isDirectory();

        if (isDirectory) {
            ArrayDeque<File> dirs = new ArrayDeque<>();
            // 子节点是否被list过
            HashMap<File, Boolean> childrenListed = new HashMap<>();

            dirs.add(f);
            childrenListed.put(f, false);

            outer: do {
                Boolean visited = childrenListed.get(f);
                while (visited) {
                    if (!mRunning || !mFileHandler.callback(f, true, mParam)) {
                        System.err.println("postIterate: failed to handle folder(" + f + ")");
                        break outer;
                    }
                    dirs.pollLast();
                    childrenListed.remove(f);

                    f = dirs.peekLast();
                    if (null == f) {
                        break outer;
                    }
                    visited = childrenListed.get(f);
                }

                childrenListed.put(f, true);

                File[] fs = f.listFiles();
                if (null != fs && 0 < fs.length) {
                    ret += fs.length;
                    for (File e : fs) {
                        if (e.isDirectory()) {
                            dirs.add(e);
                            childrenListed.put(e, false);
                        }
                        else {
                            if (!mRunning || !mFileHandler.callback(e, false, mParam)) {
                                System.err.println("postIterate: failed to handle file(" + f + ")");
                                break outer;
                            }
                        }
                    }
                }

                f = dirs.peekLast();
            } while (null != f);
        }
        else {
            mFileHandler.callback(f, false, mParam);
        }

        notityFree();

        return ret;
    }
}
