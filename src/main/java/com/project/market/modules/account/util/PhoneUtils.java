package com.project.market.modules.account.util;

public class PhoneUtils {

    static public String trim(String phone) {
        char[] chars = phone.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    static public String format(String phone) {
        if (phone.length() == 11) {
            return phone.substring(0, 3) + "-" + phone.substring(3, 7) + "-" + phone.substring(7, 11);
        } else {
            return phone.substring(0, 3) + "-" + phone.substring(3, 6) + "-" + phone.substring(6, 10);
        }
    }
}
