/**
 * @file Main2.java
 */

package main;

import java.io.IOException;

import util.zip.SvnItemDeleter;
import util.zip.Zip;

public class Main2
{
    public static void main(String[] args)
    {
        String name;
        SvnItemDeleter deleter = null;

        if (1 > args.length) {
            StringBuffer strBuf = new StringBuffer();

            strBuf.append("function\n========");
            strBuf.append("\n\tɾ��zip�ļ��е�svn��cvs��ص�Ŀ¼���ļ�.");
            strBuf.append("\nusage\n========");
            strBuf.append("\n\tjava main.Main2 zipFile");

            System.out.println(strBuf.toString());
            return;
        }

        name = args[0];

        try {
            java.io.File oldFile, newFile;

            deleter = new SvnItemDeleter(name);
            Zip.processEntries(name, deleter);

            oldFile = new java.io.File(name);
            newFile = new java.io.File(name + ".bak");
            oldFile.delete();
            newFile.renameTo(oldFile);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
