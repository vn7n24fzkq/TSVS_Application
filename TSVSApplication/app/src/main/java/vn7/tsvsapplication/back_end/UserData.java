package vn7.tsvsapplication.back_end;


public class UserData {
    public static String account_number = "";
    public static String name = "";
    public static String classs_number = "";
    public static String classs = "";

    public static void clear() {
        account_number = "";
        name = "";
        classs_number = "";
        classs = "";
    }

    public static int getGrade() {
        int gra = 1;
        if (!classs.equals("")) {
            try {
                char grade = classs.charAt(2);
                switch (grade) {
                    case '一':
                        gra = 1;
                        break;
                    case '二':
                        gra = 2;
                        break;
                    case '三':
                        gra = 3;
                        break;
                    default:
                        gra = 1;
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                gra = 1;
            }
        } else {
            gra = 1;
        }
        return gra;
    }

}
