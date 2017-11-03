/**
 * @file HttpDemo.java
 */

package main;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class HttpDemo
{
    public static void main(String[] args)
    {
        URL url = null;
        URLConnection hc = null;
        InputStream is = null;

        try {
            int ret;

            url = new URL("http://img13.tianya.cn/photo/2010/3/21/19189722_28998817.jpg");
            hc = url.openConnection();
            hc.setRequestProperty("Referer", "http://img13.tianya.cn/photo/2010/3/21/19189722_28998817.jpg");
            is = hc.getInputStream();

            FileOutputStream fos = new FileOutputStream("httpTest4.jpg");
            byte[] buf = new byte[500];

            System.out.println("\n---http content---");
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
    }
}
