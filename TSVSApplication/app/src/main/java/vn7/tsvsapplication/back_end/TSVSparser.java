package vn7.tsvsapplication.back_end;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import org.json.simple.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TSVSparser {
    private static String number = "";
    private static String password = "";
    private static String cookie = "";
    private static final String COOKIES_HEADER = "Set-Cookie";
    private static final String login_fail = "STU=Erroring";
    private static boolean loginStatus = false;
    public static final boolean sucess = true, fail = false;
    private static final int timeout = 10000;
    private static final String i_Stu_Session_Timeout_title = "【 新北市立淡水商工　學生選單()　V2.2】";
    // private String getSession_url =

    // "http://csv.tsvs.ntpc.edu.tw/csn/stu.asp?CHOICE=OK";
    // public final String i_Stu_url = "http://210.71.68.6/csn/i_Stu.asp";

    // check session
    public final static String i_Stu_url = "http://csv.tsvs.ntpc.edu.tw/csn/i_Stu.asp";
    // "Big5"
    public static final String login_url = "http://csv.tsvs.ntpc.edu.tw/csn/Reg_Stu.ASP";// 登入網址
    public static final String stu_info_url = "http://csv.tsvs.ntpc.edu.tw/csn/stu_0.ASP";// 學生資訊
    public static final String stu_score_url = "http://csv.tsvs.ntpc.edu.tw/csn/stscore.asp";// 期中成績
    public static final String stu_pastYearScore_url = "http://csv.tsvs.ntpc.edu.tw/csn/stusn.asp";// 歷年成績
    public static final String stu_absence_url = "http://csv.tsvs.ntpc.edu.tw/csn/work.asp";// 出缺席紀錄
    public static final String stu_rewards_url = "http://csv.tsvs.ntpc.edu.tw/csn/ds.asp";// 獎懲記錄
    // "UTF-8"
    public static final String calendar_url = "http://www.tsvs.ntpc.edu.tw/calendar/pagecalendar.asp?id={16D0AC9B-D5CD-495F-8822-EDAEF6A82867}";//行事曆
    public static final String onlineRepair_url = "https://docs.google.com/forms/d/e/1FAIpQLSdq-7aH6EauPo7TBn_GKFwul4rgsomVvWdYX7awDIat-5Rq5Q/viewform";//線上報修
    public static final String onlineRepairFile_url = "https://goo.gl/9TQaQD";//報修結果下載
    public static final String kao_bei_TSVS = "https://www.crush.ninja/zh-tw/pages/139786429524934/";//靠北淡商靠北網頁
    public static final String FB_kao_bei_TSVS = " https://www.facebook.com/CowBeiTSVS/";//靠北淡商fb
    //----------------------------------------------
    final String[] UUrl = {"http://csv.tsvs.ntpc.edu.tw/csn/stu_0.ASP", "http://csv.tsvs.ntpc.edu.tw/csn/stu_info.asp",
            "http://csv.tsvs.ntpc.edu.tw/csn/stue_up.asp", "http://csv.tsvs.ntpc.edu.tw/csn/stusn.asp",
            "http://csv.tsvs.ntpc.edu.tw/csn/stus.asp", "http://csv.tsvs.ntpc.edu.tw/csn/stscore.asp",
            "http://csv.tsvs.ntpc.edu.tw/csn/kscore.asp", "http://csv.tsvs.ntpc.edu.tw/csn/work.asp",
            "http://csv.tsvs.ntpc.edu.tw/csn/ds.asp", "http://csv.tsvs.ntpc.edu.tw/csn/WSTSTU.asp"};

    public TSVSparser() {

    }

    public static boolean getLoginStatus() {
        return loginStatus;
    }

    public static void init() {
        number = "";
        password = "";
        cookie = "";
        loginStatus = fail;
    }

    public static boolean checkSession() {
        try {
            Document doc = Jsoup.parse(getUrl(i_Stu_url));
            //if session did not timeout
            if (doc.title().length() > i_Stu_Session_Timeout_title.length()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //if session did timeout
        return false;
    }

    public static boolean login(String number, String password) {
        if (loginStatus == true) {
            init();
        }
        String cookies;
        try {
            setNumber(number);
            setPassword(password);
            cookies = getSession();

            if (cookies.contains(TSVSparser.login_fail)) {
                logout();
                return false;
            } else {
                loginStatus = sucess;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("LoginErroring", "");
            logout();
            return false;
        }

        return true;
    }

    public static void logout() {
        init();

    }

    public static void setNumber(String number) {
        TSVSparser.number = number;
    }

    public static void setPassword(String password) {
        TSVSparser.password = password;
    }

    private static void setHeaderField(HttpURLConnection httpConn) {
        httpConn.setRequestProperty("Cookie", TSVSparser.cookie);
        httpConn.setInstanceFollowRedirects(false);
        try {
            httpConn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        httpConn.setRequestProperty("Accept",
                "image/gif, image/jpeg, image/pjpeg, application/x-ms-application, application/xaml+xml, application/x-ms-xbap,");
        httpConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
        httpConn.setRequestProperty("Accept-Language", "zh-TW");
        httpConn.setRequestProperty("Connection", " Keep-Alive");
        httpConn.setRequestProperty("Host", " csv.tsvs.ntpc.edu.tw");
        httpConn.setRequestProperty("Referer", "http://csv.tsvs.ntpc.edu.tw/csn/stu.asp?CHOICE=OK");
        httpConn.setRequestProperty("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 10.0; WOW64; Trident/7.0; .NET4.0C; .NET4.0E; .NET CLR 2.0.50727; .NET CLR 3.0.30729; .NET CLR 3.5.30729; rctw; InfoPath.3; Creative AutoUpdate v1.41.09)");
        httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpConn.setRequestProperty("Cache-Control", "no-cache");
        httpConn.setRequestProperty("Connection", "Keep-Alive");
    }

    public static String getUrl(String URL, String charsetName) throws IOException {
        URL url = new URL(URL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoInput(true);
        httpConn.setDoOutput(true);
        httpConn.setUseCaches(false);
        httpConn.setConnectTimeout(timeout);
        httpConn.setRequestProperty("Cookie", TSVSparser.cookie);
        httpConn.connect();
        // read html
        String content = "";
        try {
            InputStreamReader in = new InputStreamReader(httpConn.getInputStream(), charsetName);
            BufferedReader br = new BufferedReader(in);
            String s;
            while ((s = br.readLine()) != null) {
                content = content + s;
            }
            in.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public static String getUrl(String URL) throws IOException {
        return getUrl(URL, "Big5");
    }

    public static String getSession() throws IOException {

        // -------------------------------------

        URL url;
        URLConnection conn;
        HttpURLConnection httpConn;

        // ------------------------------
        url = new URL(login_url);
        conn = url.openConnection();
        httpConn = (HttpURLConnection) conn;
        setHeaderField(httpConn);

        httpConn.setDoInput(true);
        httpConn.setDoOutput(true);
        httpConn.setUseCaches(false); // Post can't use caches
        // write number&password to server for get session
        httpConn.disconnect();
        DataOutputStream out = new DataOutputStream(httpConn.getOutputStream());
        String content = "txtS_NO=" + number + "&txtPerno=" + password;
        out.writeBytes(content);
        out.flush();

        out.close();


        Map<String, List<String>> headerFields = httpConn.getHeaderFields();
        List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

        for (int i = 0; i < cookiesHeader.size(); i++) {
            TSVSparser.cookie = TSVSparser.cookie + cookiesHeader.get(i) + ";";
        }
        /*
         * for (Map.Entry<String, List<String>> entry : headerFields.entrySet())
		 * { System.out.println("Key : " + entry.getKey() + " ,Value : " +
		 * entry.getValue()); }
		 */
        return TSVSparser.cookie;
    }

    public static JSONObject getStu_Info() throws MalformedURLException, IOException {
        String content = getUrl(stu_info_url);
        // parser html
        JSONObject Stu_Info = new JSONObject();
        try {
            Document doc = Jsoup.parse(content);
            Elements link = doc.select("b");
            String text = doc.body().text();
            text = text.substring(0, text.length() - 2);
            Stu_Info.put("class", link.get(0).text().substring(link.get(0).text().indexOf("：") + 1));
            Stu_Info.put("class_number", link.get(1).text().substring(link.get(0).text().indexOf("：") + 1));
            Stu_Info.put("stu_number", link.get(2).text().substring(link.get(0).text().indexOf("：") + 1));
            Stu_Info.put("name", link.get(3).text().substring(link.get(0).text().indexOf("：") + 1));
        } catch (Exception e) {
            e.printStackTrace();
            Stu_Info.clear();
        }
        return Stu_Info;
    }

    public static JSONObject getSchool_Announcement() {
        JSONObject Announcement = new JSONObject();

        return Announcement;
    }

    public static JSONObject getSchool_Calendar(String year, String month) throws Exception {
        JSONObject calendar = new JSONObject();
        JSONArray jarray = new JSONArray();
        try {
            //init data
            int currentPage = 1;
            String currentDate = "", schedule = "", department = "";
            Document doc = Jsoup.parse(getUrl(calendar_url + "&mode=view&y1=" + year + "&m1=" + month + "&PageNo=" + currentPage, "UTF-8"));
            int allPage = doc.getElementById("pagesellabel").childNodeSize();
            int departmentCount = 0;

            //parse html to json

            for (currentPage = 1; currentPage <= allPage; currentPage++) {
                doc = Jsoup.parse(getUrl(calendar_url + "&mode=view&y1=" + year + "&m1=" + month + "&PageNo=" + currentPage, "UTF-8"));
                Elements links = doc.select("td.C-tableA2,td.C-tableA3");
                for (int i = 0; i < links.size(); i++) {
                    Element element = links.get(i);
                    if ((!element.attr("rowspan").equals("")) && (element.text().endsWith(")"))) {
                        currentDate = element.text();
                    }
                    if (!element.select("a").attr("href").equals("")) {
                        if (i != links.size()) {
                            schedule = element.text();
                        }
                        if (links.get(i + 1).select("a").attr("href").equals("") || ((links.get(i + 1).attr("rowspan").equals("")) && (links.get(i + 1).text().endsWith(")")))) {
                            department = links.get(i + 1).text();
                            if (department.hashCode() == 160) {
                                department = "";
                            }
                        }
                /*        if (department.hashCode() == 160) {
                            department = "";
                        }*/

                        //set json element
                        JSONObject jobject = new JSONObject();
                        jobject.put("date", currentDate);
                        jobject.put("schedule", schedule);
                        jobject.put("department", department);
                        //put element in jarray
                        jarray.add(jobject);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        calendar.put("calendar", jarray);
        return calendar;
    }

    public static JSONObject getStu_Crriculum() {
        JSONObject crriculum = new JSONObject();

        return crriculum;
    }

    public static JSONObject getStu_PastYearScore(String grade) throws Exception {
        JSONObject stu_score = new JSONObject();
        // select grade-------------
        URL url = new URL(stu_pastYearScore_url);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setDoInput(true);
        httpConn.setDoOutput(true);
        httpConn.setUseCaches(false);
        httpConn.setConnectTimeout(timeout);
        httpConn.setRequestProperty("Cookie", TSVSparser.cookie);
        httpConn.connect();
        OutputStreamWriter out = new OutputStreamWriter(httpConn.getOutputStream());
        out.write("GRA=" + grade);
        out.flush();
        // read html
        String content = "";
        try {
            InputStreamReader in = new InputStreamReader(httpConn.getInputStream(), "Big5");
            BufferedReader br = new BufferedReader(in);
            String s;
            while ((s = br.readLine()) != null) {
                content = content + s;
            }
            in.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // parser html
        try {
            JSONArray score_Array = new JSONArray();
            String title;
            Document doc = Jsoup.parse(content);
            Elements tr = doc.select("tr");
            title = tr.first().text();
            if (tr.size() > 4) {
                for (int i = 3; i >= 0; i--) {
                    tr.remove(i);
                }
                for (int i = 0; i < tr.size(); i++) {
                    Elements td = tr.get(i).select("td");
                    JSONArray jarray = new JSONArray();
                    for (int j = 0; j < td.size(); j++) {
                        jarray.add(td.get(j).text());
                    }
                    score_Array.add(jarray);
                }
                stu_score.put("title", title);
                stu_score.put("score_Array", score_Array);
                stu_score.put("status", true);
            } else {
                stu_score.put("status", false);
            }
            System.out.println(stu_score);
        } catch (Exception e) {
            e.printStackTrace();
            stu_score.put("status", false);
        }
        return stu_score;

    }

    public static JSONObject getStu_Score() throws Exception {
        String content = getUrl(stu_score_url);
        String title;
        // parser html
        JSONObject Stu_Score = new JSONObject();
        JSONArray score_Array = new JSONArray();
        try {
            Document doc = Jsoup.parse(content);
            Elements tr = doc.select("tr");
            //get title
            title = tr.first().text();
            tr.remove(0);
            for (int i = 0; i < tr.size(); i++) {
                Elements td = tr.get(i).select("td");
                JSONArray jarray = new JSONArray();
                for (int j = 0; j < td.size(); j++) {
                    jarray.add(td.get(j).text());
                }
                score_Array.add(jarray);
            }
            Stu_Score.put("title", title);
            Stu_Score.put("score_Array", score_Array);
            Stu_Score.put("status", true);
            System.out.println(Stu_Score);
        } catch (Exception e) {
            e.printStackTrace();
            Stu_Score.put("status", false);
        }
        return Stu_Score;
    }

    public static JSONObject getStu_Absence_Record() throws Exception {
        JSONObject absence = new JSONObject();
        String content = getUrl(stu_absence_url);
        String title;
        // parser html
        JSONArray score_Array = new JSONArray();
        try {
            Document doc = Jsoup.parse(content);
            Elements tr = doc.select("tr");
            // get title
            title = tr.first().text();
            tr.remove(0);
            for (int i = 0; i < tr.size(); i++) {
                Elements td = tr.get(i).select("td");
                JSONArray jarray = new JSONArray();
                for (int j = 0; j < td.size(); j++) {
                    jarray.add(td.get(j).text());
                }
                score_Array.add(jarray);
            }
            absence.put("title", title);
            absence.put("score_Array", score_Array);
            absence.put("status", true);
            System.out.println(absence);
        } catch (Exception e) {
            e.printStackTrace();
            absence.put("status", false);
        }
        return absence;
    }

    public static JSONObject getStu_Rewards_Record() throws Exception {
        JSONObject rewards = new JSONObject();
        String content = getUrl(stu_rewards_url);
        String title;
        // parser html
        JSONArray score_Array = new JSONArray();
        try {
            Document doc = Jsoup.parse(content);
            Elements tr = doc.select("tr");
            // get title
            title = tr.first().text();
            tr.remove(0);
            for (int i = 0; i < tr.size(); i++) {
                Elements td = tr.get(i).select("td");
                JSONArray jarray = new JSONArray();
                for (int j = 0; j < td.size(); j++) {
                    jarray.add(td.get(j).text());
                }
                score_Array.add(jarray);
            }
            rewards.put("title", title);
            rewards.put("score_Array", score_Array);
            rewards.put("status", true);
            System.out.println(rewards);
        } catch (Exception e) {
            e.printStackTrace();
            rewards.put("status", false);
        }
        return rewards;
    }
}
