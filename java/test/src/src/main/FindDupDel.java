package main;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindDupDel
{
    public static void main(String[] args)
    {
        // String folder = 0 == args.length? null: args[0];

        // if (null == folder) {
        //     System.out.println("usage:\n\tFindDupDel folder.");
        //     return;
        // }

        // File dir = new File(folder);
        // File[] dirs = dir.listFiles(new MyFileFilter());
        // for (int i = dirs.length - 1; 0 <= i; --i) {
        //     String fileName = dirs[i].getPath();
        //     String orgName = fileName.replace(" \\{1\\}2\\.", ".");
        //     System.out.println("src:" + fileName + "  org:" + orgName);
        //     File org = new File(dirs[i].getPath());
        //     System.out.println("-- " + dirs[i].getPath());
        // }

        String a = "a 2 .JPGStest 2.JPG";
        String b = a.replaceAll("(.*\\s2+).", ".");
        System.out.println("--->" + b + "<---");
    }
}

class MyFileFilter implements FilenameFilter
{
    Pattern mPat = Pattern.compile(".* 2.JPG", Pattern.CASE_INSENSITIVE);;

    public boolean accept(File dir, String name)
    {
        Matcher matcher = mPat.matcher(name);
        return matcher.matches();
    }
}
