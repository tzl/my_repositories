package misc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import util.threadpool.ITask;

public class HttpDownloader implements ITask
{
    private String _url, _toDir;
    private int _index, _total;
    public static boolean _debug = true;

    private HttpDownloader() {}
    public HttpDownloader(String url, String toDir, int index, int total)
    {
        this();

        _url = url;
        _toDir = toDir;
        if (null == _toDir) {
            _toDir = "";
        }
        _index = index;
        _total = total;
    }

    public void run()
    {
        String fileName;
        URL url;
        URLConnection hc = null;
        InputStream is = null;
        FileOutputStream fos = null;
        byte[] buf = new byte[1024];
        StringBuffer strBuf = new StringBuffer();

        if (_debug) {
            strBuf.append("downloading ").append(_index).append("/").append(_total);
            strBuf.append("\n\t").append(_url);
            System.out.println(strBuf.toString());
        }

        fileName = genImageName(_url);

        if (new File(fileName).exists()) {
            System.out.println(_url + "------alread downloaded");
            return;
        }

        try {
            int ret;

            url = new URL(_url);
            hc = url.openConnection();
/*            hc.setRequestProperty("Referer", _url);*/
            hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; zh-CN; rv:1.9.2.3) Gecko/20100401 Firefox/3.6.3 GTB7.0 ( .NET CLR 3.5.30729)");
            hc.setConnectTimeout(5000);
            is = hc.getInputStream();

            fos = new FileOutputStream(fileName);
            do {
                if (0 < (ret = is.read(buf))) {
                    fos.write(buf, 0, ret);
                }
            } while (0 < ret);

            fos.close();
            is.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            url = null;
            fos = null;
            is = null;
        }
    }

    private String genImageName(String imageUrl)
    {
        StringBuffer strBuf = new StringBuffer(_toDir);
        String fileName = null;

        if (!_toDir.endsWith(File.separator)) {
            strBuf.append(File.separator);
        }

        fileName = imageUrl.replaceAll("/", "_");
        fileName = fileName.substring(fileName.indexOf("_", 30) + 1);
        strBuf.append(fileName);

        fileName = strBuf.toString();

        return fileName;
    }
}
