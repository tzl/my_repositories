package wav;

import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class WaveHeader
{
    private static final String RIFF = "RIFF";
    private static final String WAVE = "WAVE";
    private static final String FORMAT = "fmt ";
    private static final String DATA = "data";

    private final ByteBuffer _head = ByteBuffer.allocate(44);

    private int mFileLength;
    private int mDataOffset;
    public short mChannels;
    public int mSampleRate;
    public short mBitDepth;
    public int mPcmLength;

    private WaveHeader(){}

    public static WaveHeader readWaveInfo(InputStream is)
    {
        WaveHeader hdr = new WaveHeader();

        try {
            hdr._head.order(ByteOrder.LITTLE_ENDIAN);
            is.read(hdr._head.array());
            hdr.mFileLength = hdr._head.getInt(4);
            hdr.mChannels = hdr._head.getShort(22);
            hdr.mSampleRate = hdr._head.getInt(24);
            hdr.mBitDepth = hdr._head.getShort(34);
            hdr.mDataOffset = hdr.locateData(is);
            if (-1 != hdr.mDataOffset) {
                hdr.mPcmLength = hdr._head.getInt(40);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            hdr = null;
        }

        return hdr;
    }

    private int locateData(InputStream is)
    {
        byte[] buf = new byte[4];
        int pos = 36;

        try {
            do {
                _head.position(36);
                _head.get(buf);

                int chunkSize = _head.getInt(40);
                if (Arrays.equals(DATA.getBytes(), buf)) {
                    pos += 8;
                    break;
                }

                is.skip(chunkSize);
                pos += chunkSize + 8/*(int)chunk-name + (int)chunk-size*/;
                if (8 != is.read(_head.array(), 36, 8)) {
                    pos = -1;
                    break;
                }
            } while (true);
        }
        catch (IOException e) {
            e.printStackTrace();
            pos = -1;
        }

        return pos;
    }

    /**
     * |偏移地址|大小字节 |数据块类型 | 内容
     * |00H~03H | 4       | 4字符     |  资源交换文件标志 ("RIFF")
     * |04H~07H | 4       | 长整数    |  从下个地址开始到文件尾的总字节数
     * |08H~0BH | 4       | 4字符     |  WAV文件标志 ("WAVE")
     * |0CH~0FH | 4       | 4字符     |  波形格式标志 ("fmt "),最后一位空格.
     * |10H~13H | 4       | 整数      |  过滤字节(一般为00000010H)
     * |14H~15H | 2       | 整数      |  格式种类(值为1时,表示数据为线性PCM编码)
     * |16H~17H | 2       | 整数      |  通道数,单声道为1,双声道为2
     * |18H~1BH | 4       | 长整数    |  采样频率
     * |1CH~1FH | 4       | 长整数    |  波形数据传输速率(每秒平均字节数)
     * |20H~21H | 2       | 整数      |  DATA数据块长度,字节.
     * |22H~23H | 2       | 整数      |  PCM位宽
     * |24H~27H | 4       | 4字符     |  chunk 的名字(4个字符), "data", "fact" 等
     * |28H~2BH | 4       | 长整数    |  chunk 的长度
     */
    public static WaveHeader generate(int channels,
                                      int bitDepth,
                                      int sampleRate,
                                      int pcmLength)
    {
        WaveHeader hdr = new WaveHeader();
        hdr._head.order(ByteOrder.LITTLE_ENDIAN);

        hdr._head.put(RIFF.getBytes());
        //长度字段 = 内容的大小（PCMSize) + 头部字段的大小(不包括前面4字节的标识符RIFF以及fileLength本身的4字节)
        hdr.mFileLength = pcmLength + 44 - 8;
        hdr._head.putInt(hdr.mFileLength);
        hdr._head.put(WAVE.getBytes());
        hdr._head.put(FORMAT.getBytes());
        hdr._head.putInt(16); //FmtHdrLeth = 16;
        hdr._head.putShort((short)1);//FormatTag = 1;
        hdr.mChannels = (short)channels;
        hdr._head.putShort(hdr.mChannels);
        hdr.mSampleRate = sampleRate;
        hdr._head.putInt(sampleRate);
        short blockAlign = (short)(channels * bitDepth / 8);
        int avgBytesPerSec = blockAlign * sampleRate;
        hdr._head.putInt(avgBytesPerSec);
        hdr._head.putShort(blockAlign);
        hdr.mBitDepth = (short)bitDepth;
        hdr._head.putShort(hdr.mBitDepth);
        hdr._head.put(DATA.getBytes());
        hdr.mDataOffset = 44;
        hdr.mPcmLength = pcmLength;
        hdr._head.putInt(hdr.mPcmLength);

        return hdr;
    }

    public void writeWaveInfo(OutputStream os)
    {
        try {
            os.write(_head.array());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeWaveInfo(DataOutput os)
    {
        try {
            os.write(_head.array());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dump()
    {
        StringBuilder sb = new StringBuilder("--Wave Header--");

        sb.append("\nfile length:").append(mFileLength);
        sb.append("\nchannels:").append(mChannels);
        sb.append("\nsample rate:").append(mSampleRate);
        sb.append("\nbit depth:").append(mBitDepth);
        sb.append("\npcm length:").append(mPcmLength);
        sb.append("\ndata offset:").append(mDataOffset);
        sb.append("\n");

        System.out.println(sb.toString());
    }
}
