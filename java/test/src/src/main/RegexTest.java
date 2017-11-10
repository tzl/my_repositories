/**
 * @file RegexTest.java
 */

package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest
{
    private static Pattern sNonCashBindPat = Pattern.compile("(3|1(1|2))");

    public static void main(String[] args)
    {
        if (1 > args.length) {
            System.out.println("usage: java RegexTest String");
            return;
        }

        System.out.println("input:\n\t" + args[0]);

        Matcher matcher = sNonCashBindPat.matcher(args[0]);
        String validStr = matcher.matches()? matcher.group(): null;

        System.out.println("match data:\n\t" + validStr);
    }
}
