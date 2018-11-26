package com.hopen.darts.utils;

import android.text.TextUtils;

import com.github.promeg.pinyinhelper.Pinyin;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Flavin on 2016/7/6.
 */
public class StringFormatUtil {
    /**
     * 手机号格式校验
     *
     * @param phone 带校验的字符串
     * @return 是否为手机号
     */
    public static boolean phoneCheck(String phone) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
         * 总结起来就是第一位必定为1，第二位必定为3、4、5、7、8，其他位置的可以为0-9
         */
        if (TextUtils.isEmpty(phone))
            return false;
        else {
            String telRegex = "^[1][34578]\\d{9}$";// "[1]"代表第1位为数字1，"[34578]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
            return phone.matches(telRegex);
        }

    }

    /**
     * 密码复杂度校验
     *
     * @param password 待校验的字符串
     * @return 是否合格
     */
    public static boolean passCheck(String password) {
        String reg = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$";
        boolean isPass = password != null && password.matches(reg);
        return isPass;
    }

    /**
     * 邮箱格式校验
     *
     * @param email 待校验的字符串
     * @return 是否为邮箱
     */
    public static boolean emailCheck(String email) {
        if (TextUtils.isEmpty(email)) return false;
        String emailRegex = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
        String emailRegex2 = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";

        Pattern r = Pattern.compile(emailRegex);
        Matcher m = r.matcher(email);
        return m.matches();
    }

    /**
     * double去两位小数
     *
     * @param value 待舍入的数
     * @return 舍入后的数
     */
    public static String doubleRounding(double value) {
//        BigDecimal bd = new BigDecimal(value);
////        value = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
////        StringBuffer sbint = new StringBuffer((int) value + "");
////        StringBuffer sbdouble = new StringBuffer(value + "");
////        if (sbint.length() == sbdouble.length() - 2) {
////            sbdouble.append("0");
////        } else if (sbint.length() == sbdouble.length()) {
////            sbdouble.append(".00");
////        }
        return doubleRounding(value, 2);
    }

    /**
     * double去两位小数
     *
     * @param value    待舍入的数
     * @param keep_num 保留小数的位数
     * @return 舍入后的数
     */
    public static String doubleRounding(double value, int keep_num) {
        BigDecimal bd = new BigDecimal(value);
        value = bd.setScale(keep_num, BigDecimal.ROUND_HALF_UP).doubleValue();
        StringBuffer sbint = new StringBuffer((int) value + "");
        StringBuffer sbdouble = new StringBuffer(value + "");
        if (sbint.length() == sbdouble.length()) {
            sbdouble.append(".");
        }
        int length_diff = sbdouble.length() - sbint.length();
        for (int i = 0; i < keep_num - (length_diff == 0 ? 0 : length_diff - 1); i++) {
            sbdouble.append("0");
        }
        return sbdouble.toString();
    }

    /**
     * double去两位小数
     *
     * @param value 待舍入的数
     * @return 舍入后的数
     */
    public static double doubleRoundingDouble(double value) {
        BigDecimal bd = new BigDecimal(value);
        return bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 是否为字母
     *
     * @param str 字符串
     * @return 是否为字母
     */
    public static boolean isLetter(String str) {
        for (int i = 0; i < str.length(); i++) {
            int chr = str.charAt(i);
            if (chr < 65 || (90 < chr && chr < 97) || 122 < chr)
                return false;
        }
        return true;
    }

    /**
     * 是否为数字
     *
     * @param str 字符串
     * @return 是否为数字
     */
    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57)
                return false;
        }
        return true;
    }

    /**
     * 是否是颜色值
     *
     * @param color 待校验的字符串
     * @return 是否是颜色值
     */
    public static boolean isColor(String color) {
        Pattern pattern = Pattern.compile("[0-9 a-f A-F]{1,}");
        Matcher matcher = pattern.matcher(color);
        return (color.length() == 6 || color.length() == 8) && matcher.matches();
    }

    /**
     * 是否包含emoji表情
     *
     * @param string 待校验的字符串
     * @return 是否包含emoji表情
     */
    public static boolean containEmoji(String string) {
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\ud83e\udc00-\ud83e\udfff]|[\ud83f\udc00-\ud83f\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher emojiMatcher = emoji.matcher(string);
        return emojiMatcher.find();
    }

    /**
     * 是否包含emoji表情
     *
     * @param strings 待校验的字符串集合
     * @return 是否包含emoji表情
     */
    public static boolean containEmoji(String... strings) {
        for (String item : strings) {
            if (containEmoji(item)) return true;
        }
        return false;
    }

    /**
     * 搜索时，两个字符串的包含校验
     *
     * @param child      关键词子串
     * @param wait_check 待校验的，是否包含关键词子串的字符串
     * @return 是否包含
     */
    public static boolean containCheck(String child, String wait_check) {
        try {
            if (TextUtils.isEmpty(child) || TextUtils.isEmpty(wait_check))
                return false;
            boolean child_is_chinese = true;
            for (char item : child.toCharArray()) {
                if (!Pinyin.isChinese(item))
                    child_is_chinese = false;
            }
            if (child_is_chinese || isNumeric(wait_check)) {
                return wait_check.contains(child);
            }
            if (wait_check.contains(child))
                return true;
            String child_pinyin = Pinyin.toPinyin(child, "").toLowerCase();
            String wait_check_pinyin = Pinyin.toPinyin(wait_check, "").toLowerCase();
            if (wait_check_pinyin.contains(child_pinyin))
                return true;
            final String separator = "^";
            String[] child_pinyin_array = Pinyin.toPinyin(child, separator).toLowerCase().split("\\" + separator);
            String[] wait_check_pinyin_array = Pinyin.toPinyin(wait_check, separator).toLowerCase().split("\\" + separator);
            int first_position = 0;
            int first_check_success_num = 0;
            for (String itemi : child_pinyin_array) {
                if (first_position >= wait_check_pinyin_array.length)
                    break;
                for (int j = first_position; j < wait_check_pinyin_array.length; j++) {
                    String itemj = wait_check_pinyin_array[j];
                    if (itemj.equals(itemi) || itemj.startsWith(itemi)) {
                        first_position = j + 1;
                        first_check_success_num++;
                        break;
                    }
                }
            }
            return first_check_success_num == child_pinyin_array.length;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取字符串的首字母（包括中文）
     *
     * @param string 字符串
     * @return 字符串的首字母，如果首位不是字母，则返回'#'
     */
    public static char getInitial(String string) {
        try {
            char temp = Pinyin.toPinyin(string, "").toUpperCase().charAt(0);
            if (temp < 65 || 90 < temp) {
                temp = '#';
            }
            return temp;
        } catch (Throwable e) {
            e.printStackTrace();
            return '#';
        }
    }

    /**
     * 格式化时间戳
     *
     * @param date_long 时间戳
     * @param format    指定格式
     * @return 格式化后的时间字符串
     */
    public static String dateFormat(long date_long, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = new Date(date_long);
            String formatDate = sdf.format(date);
            return formatDate;
        } catch (Exception e) {
            return "无";
        }
    }

    /**
     * 格式化时间戳
     *
     * @param date   时间戳
     * @param format 指定格式
     * @return 格式化后的时间字符串
     */
    public static String dateFormat(Date date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            String formatDate = sdf.format(date);
            return formatDate;
        } catch (Exception e) {
            return "无";
        }
    }

    /**
     * 格式化时间戳
     *
     * @param date_string String型的时间戳
     * @param format      指定格式
     * @return 格式化后的时间字符串
     */
    public static String dateFormat(String date_string, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            long dateLong = Long.parseLong(date_string);
            Date date = new Date(dateLong);
            String formatDate = sdf.format(date);
            return formatDate;
        } catch (Exception e) {
            return "无";
        }
    }

    /**
     * 格式化的日期字符串转化为Date对象
     *
     * @param date_string 格式化的日期字符串
     * @param format      日期字符串的格式
     * @return 日期对象
     */
    public static Date formatToDate(String date_string, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = sdf.parse(date_string);
            return date;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 格式化的日期字符串转化为毫秒值
     *
     * @param date_string 格式化的日期字符串
     * @param format      日期字符串的格式
     * @return 毫秒值
     */
    public static long formatToLong(String date_string, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = sdf.parse(date_string);
            return date.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 将时分转化为时间戳
     *
     * @param hour   小时（24小时制）
     * @param minute 分钟
     * @return 格式化后的时间字符串
     */
    public static String timeToDate(String hour, String minute) {
        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.set(year, month, day, Integer.parseInt(hour), Integer.parseInt(minute));
            return calendar.getTimeInMillis() + "";
        } catch (Exception e) {
            return Calendar.getInstance().getTimeInMillis() + "";
        }
    }

    /**
     * 将完整日期转化为时间戳
     *
     * @param year   年
     * @param month  月
     * @param day    日
     * @param hour   时（24小时制）
     * @param minute 分
     * @param second 秒
     * @return 格式化后的时间字符串
     */
    public static long dateToDate(String year, String month, String day, String hour, String minute, String second) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(minute), Integer.parseInt(second));
            return calendar.getTimeInMillis();
        } catch (Exception e) {
            return Calendar.getInstance().getTimeInMillis();
        }
    }
}
