package me.botsko.prism.utils;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {

    /**
     * Translates a String to a timestamp showing ms since 0.
     * @return Long
     */
    public static Long translateTimeStringToDate(String argValue) {

        long dateFrom = 0L;

        final Pattern p = Pattern.compile("([0-9]+)([s秒m分h时d天w周])");
        final Calendar cal = Calendar.getInstance();

        final String[] matches = TypeUtils.preg_match_all(p, argValue);
        if (matches.length > 0) {
            for (final String match : matches) {

                final Matcher m = p.matcher(match);
                if (m.matches()) {

                    if (m.groupCount() == 2) {

                        final int tfValue = Integer.parseInt(m.group(1));
                        final String tfFormat = m.group(2);

                        switch (tfFormat) {
                            case "w":
                            case "周":
                                cal.add(Calendar.WEEK_OF_YEAR, -1 * tfValue);
                                break;
                            case "d":
                            case "天":
                                cal.add(Calendar.DAY_OF_MONTH, -1 * tfValue);
                                break;
                            case "h":
                            case "时":
                                cal.add(Calendar.HOUR, -1 * tfValue);
                                break;
                            case "m":
                            case "分":
                                cal.add(Calendar.MINUTE, -1 * tfValue);
                                break;
                            case "s":
                            case "秒":
                                cal.add(Calendar.SECOND, -1 * tfValue);
                                break;
                            default:
                                return null;
                        }
                    }
                }
            }
            dateFrom = cal.getTime().getTime();
        }

        return dateFrom;

    }
}
