/**
 * @file RegexTest.java
 */

package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest
{
    final static Pattern sUppercasePat = Pattern.compile("((\\d|[A-F]){32}).*");
    final static Pattern sLowercasePat = Pattern.compile("((\\d|[a-f]){32}).*");
    final static Pattern sCuidUppercasePat = Pattern.compile("((\\d|[A-F]){32}).*(\\|.*)");
    final static Pattern sCuidLowercasePat = Pattern.compile("((\\d|[a-f]){32}).*(\\|.*)");

    public static void main(String[] args)
    {
        if (1 > args.length) {
            System.out.println("usage: java RegexTest String");
            return;
        }

        System.out.println("input:\n\t" + args[0]);

        Matcher matcher = sUppercasePat.matcher(args[0]);
        String validStr = matcher.matches()? matcher.group(1): null;
        if (null == validStr) {
            matcher = sLowercasePat.matcher(args[0]);
            validStr = matcher.matches()? matcher.group(1): "no match things";
        }

        matcher = sCuidUppercasePat.matcher(args[0]);
        int count = matcher.groupCount();
        if (matcher.matches()) {
            System.out.println("count=" + count);
            for (int i = 0; i <= count; ++i) {
                System.out.println("group[" + i + "]=" + matcher.group(i));
            }
        }

        String imei = matcher.matches()? matcher.group(3): null;
        if (null == imei) {
            matcher = sCuidLowercasePat.matcher(args[0]);
            imei = matcher.matches()? matcher.group(3): "no match things";
        }
        System.out.println("deviceid:\n\t" + validStr + "\nimei:\n\t" + imei);
    }
}
