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
        strBuf.append("\n\t删除zip文件中的svn或cvs相关的目录和文件.");
        strBuf.append("\nusage\n========");
        strBuf.append("\n\tjava main.Main2 zipFile");

        System.out.println(strBuf.toString());

        array = strBuf.toString().getBytes();
        HexaUtil.printArray(array, 0, array.length, "printArray");
    }
}
