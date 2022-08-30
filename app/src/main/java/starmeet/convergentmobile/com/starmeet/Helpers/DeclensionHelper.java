package starmeet.convergentmobile.com.starmeet.Helpers;

public class DeclensionHelper {
    //Склонение
    public static String getDeclensionWord(int number, String word1, String word2, String word3) {

        String str;
        int i = 0;
        number = number % 100;
        if (number >= 11 && number <= 19) {
            str = word2;
        } else {
            i = number % 10;
            switch (i) {
                case (1):
                    str = word1;
                    break;
                case (2):
                case (3):
                case (4):
                    str = word2;
                    break;
                default:
                    str = word3;
            }
        }

        return str;
    }
}
