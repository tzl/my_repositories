/**
 * @file TestIni.java
 */

package main;

import java.io.IOException;

import util.ini.IniFile;

public class TestIni
{
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        IniFile iniFile;

        try {
            String section, key, val;
            StringBuffer buf = new StringBuffer();

            iniFile = new IniFile("myIni.ini");

            section = "host";
            key = "ip";

            iniFile.setKeyValue("host", "ip", "BJ-Tom");
            iniFile.setKeyValue("sectiontest", "name", "192.168.80.110");
            iniFile.setKeyValue("host", "name", "");
            val = iniFile.getKeyValue(section, key);
            buf.delete(0, buf.length());
            buf.append("section[").append(section).append("] key:").append(key);
            buf.append("---").append(val);
            System.out.println(buf.toString());

            iniFile.close();
        }
        catch (IOException e) {
            /* TODO Auto-generated catch block*/
            e.printStackTrace();
        }

    }

}
