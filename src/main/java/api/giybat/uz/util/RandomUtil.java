package api.giybat.uz.util;

import java.util.Random;

public class RandomUtil {

    public  static  final Random rand = new Random();


    public static String getRandomSmsCode(){
        return String.valueOf(rand.nextInt(10000 , 99999));
    }
}
