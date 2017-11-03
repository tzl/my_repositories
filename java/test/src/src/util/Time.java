/**
 * @file Time.java
 */

package util;

public class Time
{
    public int year, month, day, hour, minute, second;
    private int _timeZone;

    public Time(int timeZone)
    {
        _timeZone = timeZone;
    }

    public Time()
    {
        _timeZone = 8;
    }

    public String getCurrentTime()
    {
        extractEl(System.currentTimeMillis(), _timeZone);

        StringBuffer buf = new StringBuffer("[");

        buf.append(year).append("-");
        if (month < 10) {
            buf.append("0");
        }
        buf.append(month).append("-");
        if (day < 10) {
            buf.append("0");
        }
        buf.append(day).append(" ");

        if (hour < 10) {
            buf.append("0");
        }
        buf.append(hour).append(":");
        if (minute < 10) {
            buf.append("0");
        }
        buf.append(minute).append(":");
        if (second < 10) {
            buf.append("0");
        }
        buf.append(second).append("] ");

        return buf.toString();
    }

    private void extractEl(long ms, int timeZone)
    {
        long time_l = ms / 1000L;
        byte Days[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        long n32_Pass4year, n32_hpery;

        /*ȡ��ʱ��*/
        second = (int)(time_l % 60);
        time_l /= 60;

        /*ȡ����ʱ��*/
        minute = (int)(time_l % 60);
        time_l /= 60;

        /*ȡ��ȥ���ٸ����꣬ÿ������ 1461L * 24L = 35064L Сʱ*/
        n32_Pass4year= (time_l / 35064L);

        /*�������*/
        year = (int)(n32_Pass4year << 2) + 1970;

        /*������ʣ�µ�Сʱ�� 1461L * 24L = 35064L*/
        time_l %= 35064L;

        /*У������Ӱ�����ݣ�����һ����ʣ�µ�Сʱ��*/
        for (;;) {
            /*һ���Сʱ�� 365 * 24 = 8760L;*/
            n32_hpery = 8760L;

            /*�ж�����*/
            if (0 == (year % 400) || (0 == (year % 4) && 0 != (year & 3))) {
                /*�����꣬һ�����24Сʱ����һ��*/
                n32_hpery += 24;
            }

            if (time_l < n32_hpery) {
                break;
            }

            year++;
            time_l -= n32_hpery;
        }

        /*Сʱ��*/
        hour = (int)(time_l % 24);
        hour += timeZone; /*ʱ������*/

        /*һ����ʣ�µ�����*/
        time_l /= 24;

        /*�ٶ�Ϊ����*/
        time_l++;

        /*У��������������·ݣ�����*/
        if (0 == (year % 400) || (0 == (year % 4) && 0 != (year & 3))) {
            if (time_l > 60) {
                time_l--;
            }
            else if (time_l == 60) {
                month = 1;
                day = 29;
                return ;
            }
        }

        /*��������*/
        for (month = 0; Days[month] < time_l; ++month) {
            time_l -= Days[month];
        }
        month += 1;

        day = (int)(time_l);
    }
}