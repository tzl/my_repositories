package wav;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Observable;
import java.util.Observer;

public class AudioFileWriter implements Observer {
    private AudioRecorder mRecorder;
    private FileOutputStream mAudioFile;
    private File mFile;
    private WaveHeader mWavHeader;
    private int mPcmLength;

    public AudioFileWriter(AudioRecorder recorder) {
        mRecorder = recorder;
        mFile = new File("/sdcard/cu.wav");
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable != mRecorder) {
            return;
        }

        if (data instanceof AudioRecorder.State) {
            AudioRecorder.State state = (AudioRecorder.State) data;
            if (AudioRecorder.State.OPEN == state) {
                mPcmLength = 0;
                try {
                    mAudioFile = new FileOutputStream(mFile);
                    FileInputStream ios = new FileInputStream(mFile);
                    WaveHeader.readWaveInfo(ios).dump();
                    ios.close();
                    mWavHeader = WaveHeader.generate(1, 16, 8000, 0);
                    mWavHeader.writeWaveInfo(mAudioFile);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (AudioRecorder.State.STOP == state) {
                try {
                    mAudioFile.close();
                    mWavHeader = WaveHeader.generate(1, 16, 8000, mPcmLength);
                    RandomAccessFile raf = new RandomAccessFile(mFile, "rw");
                    mWavHeader.writeWaveInfo(raf);
                    mWavHeader.dump();
                    raf.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                mAudioFile = null;
            }
        }
        else if (data instanceof Buffer) {
            if (null != mAudioFile) {
                ByteBuffer bb = (ByteBuffer) data;
                try {
                    mPcmLength += bb.remaining();
                    mAudioFile.write(bb.array(), bb.position(), bb.remaining());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
