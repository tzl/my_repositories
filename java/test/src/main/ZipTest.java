/**
 * @file ZipTest.java
 */

package main;

import java.io.IOException;
import util.zip.*;

public class ZipTest
{
    public static void main(String[] args)
    {
        try {
            Zip.unZip("processing-js-0.9.1-examples.zip", "my_zip_folder");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
