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

        /*取秒时间*/
        second = (int)(time_l % 60);
        time_l /= 60;

        /*取分钟时间*/
        minute = (int)(time_l % 60);
        time_l /= 60;

        /*取过去多少个四年，每四年有 1461L * 24L = 35064L 小时*/
        n32_Pass4year= (time_l / 35064L);

        /*计算年份*/
        year = (int)(n32_Pass4year << 2) + 1970;

        /*四年中剩下的小时数 1461L * 24L = 35064L*/
        time_l %= 35064L;

        /*校正闰年影响的年份，计算一年中剩下的小时数*/
        for (;;) {
            /*一年的小时数 365 * 24 = 8760L;*/
            n32_hpery = 8760L;

            /*判断闰年*/
            if (0 == (year % 400) || (0 == (year % 4) && 0 != (year & 3))) {
                /*是闰年，一年则多24小时，即一天*/
                n32_hpery += 24;
            }

            if (time_l < n32_hpery) {
                break;
            }

            year++;
            time_l -= n32_hpery;
        }

        /*小时数*/
        hour = (int)(time_l % 24);
        hour += timeZone; /*时区调整*/

        /*一年中剩下的天数*/
        time_l /= 24;

        /*假定为闰年*/
        time_l++;

        /*校正润年的误差，计算月份，日期*/
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

        /*计算月日*/
        for (month = 0; Days[month] < time_l; ++month) {
            time_l -= Days[month];
        }
        month += 1;

        day = (int)(time_l);
    }
}