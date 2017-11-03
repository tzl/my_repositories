package wav;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args)
    {
        File pcmFile = new File(args[0]);
        long pcmLength = pcmFile.length();
        String wavFileName = "a.wav";

        WaveHeader mWavHeader = WaveHeader.generate(1, 16, 8000, (int)pcmLength);
        FileOutputStream wavFile = null;
        FileInputStream pcmInputStream = null;

        try {
            wavFile = new FileOutputStream(wavFileName);
            mWavHeader.writeWaveInfo(wavFile);
            pcmInputStream = new FileInputStream(pcmFile);
            byte[] buf = new byte[1000];

            do {
                int ret = pcmInputStream.read(buf);
                if (~0 != ret) {
                    wavFile.write(buf, 0, ret);
                }
                else {
                    break;
                }
            } while (true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (null != wavFile) {
                try {
                    wavFile.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != pcmInputStream) {
                try {
                    pcmInputStream.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
