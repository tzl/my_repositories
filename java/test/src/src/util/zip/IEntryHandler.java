/**
 * @file IEntryHandler.java
 */

package util.zip;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * EntryHandler用来处理zip文件中的一个文件或者文件夹.
 * 用来在读取zip文件的过程中反复调用这个接口来处理整个zip文件.
 */
public abstract class IEntryHandler
{
    /**
     * @brief
     *  处理zip文件中的文件条目或文件夹条目.
     *
     * @param zis
     *  zip文件流.
     * @param entry
     *  zip中的一个压缩项(文件或目录).
     * @return
     *  <ul>
     *  <li>0   - 表示处理本条目正常结束.这样zip文件处理会遍历后续条目接着调用
     *  process来处理遍历出来的条目.</li>
     *  <li>负数 - 表示处理本条目出错,并且不希望处理后续条目,而是直接结束zip文件的处理.</li>
     *  <li>正数 - 表示处理本条目正常,有意退出后续zip文件的处理.</li>
     *  </ul>
     */
    public abstract int process(ZipInputStream zis, ZipEntry entry);

    /**
     * @brief
     *  处理完zip文件之后的收尾工作.
     * @param zis
     *  zip文件流.这时(调用本函数前)文件流已经到了zip文件的尾部.
     * @return
     *  <ul>
     *  <li>0   - 本收尾工作正常结束.</li>
     *  <li>负数 - 本收尾工作出错,希望截获错误进行进一步的处理.</li>
     *  <li>正数 - 本收尾工作出错,但是希望忽略错误.</li>
     *  </ul>
     */
    public int postProcess(ZipInputStream zis)
    {
        return 0;
    };
}
