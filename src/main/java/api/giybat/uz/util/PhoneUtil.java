package api.giybat.uz.util;

import java.util.regex.Pattern;

public class PhoneUtil {
    public static Boolean isPhone(String phone) {
        String PhoneRegex = "^998\\d{9}$"    ;
        return Pattern.matches(PhoneRegex, phone);
    }
}
