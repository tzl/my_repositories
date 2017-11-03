package main;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindPlugin
{
    public static void main(String[] args)
    {
        String plugInSeg = args[0];
        int i;

        if (null == plugInSeg) {
            System.out.println("usage:\n\tFindPlugin plugin_name_segment.");
            return;
        }

        File dir = new File("D:\\eclipse-rcp-galileo-SR2-win32\\ÎÄµµ");
        File[] dirs = dir.listFiles(new MyFileFilter(plugInSeg));
        for (i = dirs.length - 1; 0 <= i; --i) {
            System.out.println("-- " + dirs[i].getName());
        }
    }
}

class MyFileFilter implements FilenameFilter
{
    Pattern pat = null;

    public MyFileFilter(String pattern)
    {
        if (null != pattern) {
            pattern = ".*" + pattern + ".*";
            pat = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        }
    }

    public boolean accept(File dir, String name)
    {
        if (null != pat) {
            Matcher matcher = pat.matcher(name);
            return matcher.matches();
        }

        return true;
    }
}
