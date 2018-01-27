package vn7.tsvsapplication.back_end.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WeatherSample {
    public static String key;

    public ArrayList<WeekWeather> getWeekWeather(String location) throws IOException {
        String city = "", town = "";
        String locationName = "&locationName" + URLEncoder.encode(location);
        ArrayList<WeekWeather> weatherList = new ArrayList<WeekWeather>();

        URL url = new URL(
                "http://opendata.cwb.gov.tw/api/v1/rest/datastore/F-D0047-071?sort=time&elementName=PoP,MaxT,Wx,,UVI,WeatherDescription,MinT"
                        + locationName);
        URLConnection conn = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) conn;
        httpConn.setRequestMethod("GET");
        // api key
        httpConn.setRequestProperty("Authorization", key);
        httpConn.connect();
        httpConn.getResponseCode();
        Reader in = new InputStreamReader(httpConn.getInputStream(), "UTF-8");
        BufferedReader br = new BufferedReader(in);
        String content = "", s;
        while ((s = br.readLine()) != null) {
            content = content + s;
        }

        try {
            JSONObject jobject = (JSONObject) new JSONParser().parse(content);
            JSONArray jrecords = (JSONArray) ((JSONObject) jobject.get("records")).get("locations");
            JSONObject records = (JSONObject) jrecords.get(0);
            // get city name
            city = (String) records.get("locationsName");
            JSONArray locations = (JSONArray) records.get("location");
            // get town name
            town = (String) ((JSONObject) locations.get(0)).get("locationName");
            // get weather data
            JSONArray weatherElement = (JSONArray) ((JSONObject) locations.get(0)).get("weatherElement");
            JSONArray UVI = null, WeatherDescription = null, Wx = null, PoP = null, MaxT = null, MinT = null;
            for (int i = 0; i < weatherElement.size(); i++) {
                JSONObject elements = (JSONObject) weatherElement.get(i);
                if (elements.get("elementName").equals("Wx")) {
                    Wx = (JSONArray) elements.get("time");
                } else if (elements.get("elementName").equals("UVI")) {
                    UVI = (JSONArray) elements.get("time");
                } else if (elements.get("elementName").equals("WeatherDescription")) {
                    WeatherDescription = (JSONArray) elements.get("time");
                } else if (elements.get("elementName").equals("PoP")) {
                    PoP = (JSONArray) elements.get("time");
                } else if (elements.get("elementName").equals("MaxT")) {
                    MaxT = (JSONArray) elements.get("time");
                } else if (elements.get("elementName").equals("MinT")) {
                    MinT = (JSONArray) elements.get("time");
                }
            }
            // 天氣描述
            for (int i = 0; i < WeatherDescription.size(); i++) {
                WeekWeather ww;
                try {
                    ww = weatherList.get(i);
                } catch (IndexOutOfBoundsException e) {
                    ww = new WeekWeather();
                    weatherList.add(ww);
                }
                ww.start_time = (String) ((JSONObject) WeatherDescription.get(i)).get("startTime");
                ww.end_time = (String) ((JSONObject) WeatherDescription.get(i)).get("endTime");
                ww.WeatherDescription = (String) ((JSONObject) WeatherDescription.get(i)).get("elementValue");
            }
            // 紫外線強度
            for (int i = 0; i < UVI.size(); i++) {
                for (int j = 0; j < weatherList.size(); j++) {
                    try {
                        if (weatherList.get(j).start_time.equals((String) ((JSONObject) UVI.get(i)).get("startTime"))) {

                            WeekWeather ww = weatherList.get(j);
                            ww.UVI = (String) ((JSONObject) ((JSONArray) ((JSONObject) UVI.get(i)).get("parameter"))
                                    .get(1)).get("parameterName");
                            ww.UVILevel = (String) ((JSONObject) ((JSONArray) ((JSONObject) UVI.get(i))
                                    .get("parameter")).get(1)).get("parameterValue");
                            ww.Exposure = (String) ((JSONObject) ((JSONArray) ((JSONObject) UVI.get(i))
                                    .get("parameter")).get(0)).get("parameterName");
                            ww.ExposureDescription = (String) ((JSONObject) ((JSONArray) ((JSONObject) UVI.get(i))
                                    .get("parameter")).get(0)).get("parameterValue");
                            try {
                                Integer.parseInt(ww.UVILevel);
                            } catch (Exception e) {
                                String temp = ww.ExposureDescription;
                                ww.ExposureDescription = ww.UVILevel;
                                ww.UVILevel = temp;
                                temp = ww.Exposure;
                                ww.Exposure = ww.UVI;
                                ww.UVI = temp;
                            }
                            // weatherList.add(ww);
                            break;
                        }

                    } catch (IndexOutOfBoundsException e) {

                    }
                }

            }
            // 天氣種類圖示
            for (int i = 0; i < Wx.size(); i++) {
                WeekWeather ww;
                try {
                    ww = weatherList.get(i);
                } catch (IndexOutOfBoundsException e) {
                    ww = new WeekWeather();
                    weatherList.add(ww);
                }
                if (ww.start_time == null || ww.start_time.equals((String) ((JSONObject) Wx.get(i)).get("startTime"))) {
                    ww.Wx = (String) ((JSONObject) ((JSONArray) ((JSONObject) Wx.get(i)).get("parameter")).get(0))
                            .get("parameterValue");
                    ww.WxDescription = (String) ((JSONObject) Wx.get(i)).get("elementValue");
                }
            }
            for (int i = 0; i < PoP.size(); i++) {
                WeekWeather ww;
                try {
                    ww = weatherList.get(i);
                } catch (IndexOutOfBoundsException e) {
                    ww = new WeekWeather();
                    weatherList.add(ww);
                }
                if (ww.start_time == null
                        || ww.start_time.equals((String) ((JSONObject) PoP.get(i)).get("startTime"))) {
                    ww.PoP = (String) ((JSONObject) PoP.get(i)).get("elementValue");
                }
            }
            for (int i = 0; i < MaxT.size(); i++) {
                WeekWeather ww;
                try {
                    ww = weatherList.get(i);
                } catch (IndexOutOfBoundsException e) {
                    ww = new WeekWeather();
                    weatherList.add(ww);
                }
                if (ww.start_time == null
                        || ww.start_time.equals((String) ((JSONObject) MaxT.get(i)).get("startTime"))) {
                    ww.MaxAT = (String) ((JSONObject) MaxT.get(i)).get("elementValue");
                }
            }
            for (int i = 0; i < MinT.size(); i++) {
                WeekWeather ww;
                try {
                    ww = weatherList.get(i);
                } catch (IndexOutOfBoundsException e) {
                    ww = new WeekWeather();
                    weatherList.add(ww);
                }
                if (ww.start_time == null
                        || ww.start_time.equals((String) ((JSONObject) MinT.get(i)).get("startTime"))) {
                    ww.MinAT = (String) ((JSONObject) MinT.get(i)).get("elementValue");
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return weatherList;
    }
}

