package misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractTxtZipUrl
{
    private File _htmlFile;
    private final Pattern _pat = Pattern.compile(".*href=\"/download.php\\?tid=([0-9]+)\" target=\"_blank\">TXT</a>.*", Pattern.CASE_INSENSITIVE);
    private final Pattern _numPat = Pattern.compile("[0-9]{6}", Pattern.CASE_INSENSITIVE);

    private ExtractTxtZipUrl() {}
    public ExtractTxtZipUrl(String htmlFileName) throws IOException
    {
        this();
        setHtmlFile(htmlFileName);
    }

    private String deduceUrl(String number)
    {
        Matcher matcher = _numPat.matcher(number);
        StringBuffer strBuf = new StringBuffer();

        if (!matcher.matches()) {
            return null;
        }

        strBuf.append("http://www.tieku.org/txt/");
        strBuf.append(number.substring(0, 3)).append("/");
        strBuf.append(number).append("/").append(number).append(".zip");

        return strBuf.toString();
    }

    public Vector<String> genZipTxtList()
    {
        String line, zipTxtUrl;
        FileReader fReader = null;
        LineNumberReader lineReader = null;
        Matcher matcher;
        Vector<String> list = new Vector<String>();

        try {
            fReader = new FileReader(_htmlFile);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        lineReader = new LineNumberReader(fReader);

        try {
            while (null != (line = lineReader.readLine())) {
                matcher = _pat.matcher(line);
                if (matcher.matches()) {
                    zipTxtUrl = deduceUrl(matcher.group(1));
                    if(null != zipTxtUrl) {
                        list.add(zipTxtUrl);
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (null != fReader) {
                try {
                    fReader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != lineReader) {
                try {
                    lineReader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }

    public void setHtmlFile(String htmlFileName) throws IOException
    {
        File file = new File(htmlFileName);

        if (!file.isFile()) {
            throw new IOException("failed to open html file " + htmlFileName);
        }
        _htmlFile = file;
    }
}
