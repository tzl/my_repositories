/**
 * @file Main3.java
 */

package main;

import util.HexaUtil;

public class Main3
{
    public static void main(String[] args)
    {
        byte[] array;
        StringBuffer strBuf = new StringBuffer();

        strBuf.append("function\n========");
        strBuf.append("\n\tɾ��zip�ļ��е�svn��cvs��ص�Ŀ¼���ļ�.");
        strBuf.append("\nusage\n========");
        strBuf.append("\n\tjava main.Main2 zipFile");

        System.out.println(strBuf.toString());

        array = strBuf.toString().getBytes();
        HexaUtil.printArray(array, 0, array.length, "printArray");
    }
}
