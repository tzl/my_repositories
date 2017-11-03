/**
 * @file IEntryHandler.java
 */

package util.zip;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * EntryHandler��������zip�ļ��е�һ���ļ������ļ���.
 * �����ڶ�ȡzip�ļ��Ĺ����з�����������ӿ�����������zip�ļ�.
 */
public abstract class IEntryHandler
{
    /**
     * @brief
     *  ����zip�ļ��е��ļ���Ŀ���ļ�����Ŀ.
     *
     * @param zis
     *  zip�ļ���.
     * @param entry
     *  zip�е�һ��ѹ����(�ļ���Ŀ¼).
     * @return
     *  <ul>
     *  <li>0   - ��ʾ������Ŀ��������.����zip�ļ���������������Ŀ���ŵ���
     *  process�����������������Ŀ.</li>
     *  <li>���� - ��ʾ������Ŀ����,���Ҳ�ϣ�����������Ŀ,����ֱ�ӽ���zip�ļ��Ĵ���.</li>
     *  <li>���� - ��ʾ������Ŀ����,�����˳�����zip�ļ��Ĵ���.</li>
     *  </ul>
     */
    public abstract int process(ZipInputStream zis, ZipEntry entry);

    /**
     * @brief
     *  ������zip�ļ�֮�����β����.
     * @param zis
     *  zip�ļ���.��ʱ(���ñ�����ǰ)�ļ����Ѿ�����zip�ļ���β��.
     * @return
     *  <ul>
     *  <li>0   - ����β������������.</li>
     *  <li>���� - ����β��������,ϣ���ػ������н�һ���Ĵ���.</li>
     *  <li>���� - ����β��������,����ϣ�����Դ���.</li>
     *  </ul>
     */
    public int postProcess(ZipInputStream zis)
    {
        return 0;
    };
}
