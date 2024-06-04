import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WeatherAPI {
    public static void main(String[] args) {
        try {
            Scanner scan = new Scanner(System.in);
            String city = "";
            //loop
            while(!city.equalsIgnoreCase("no")) {
                System.out.println("===============================================");
                System.out.println("Enter the city name 0r To Quit Enter \"No\" : ");
                //user input
                city = scan.next();

                //loop breaks if the input is NO
                if(city.equalsIgnoreCase("no")) break;

                JSONObject cityLocationData = getLocationData(city);

                if (cityLocationData != null) {
                    double latitude = (double) cityLocationData.get("latitude");
                    double longitude = (double) cityLocationData.get("longitude");
                    getCityWeatherData(latitude, longitude);
                }
                else {
                    System.out.println("City location data not found.");
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }



    //method to get Weather of the given city using location
    private static void getCityWeatherData(double latitude, double longitude) {
       try {
           //url to get our weather parameters from open - meteo website using location
           String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude +
                   "&current=temperature_2m,relative_humidity_2m,is_day,rain,snowfall,wind_speed_10m,wind_direction_10m&hourly=temperature_2m";

           HttpURLConnection apiConnection = fetchApiResponse(url);

           if (apiConnection.getResponseCode() != 200) {
               System.out.println("ERROR: could not get API");
           }

           String jsonResponse = readApiResponse(apiConnection);

           JSONParser parser = new JSONParser();
           JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
           JSONObject currentWeatherJson = (JSONObject) jsonObject.get("current");

           //result parameters
           String time = (String) currentWeatherJson.get("time");
           System.out.println("Current Time : " + time);

           double temperature = (double) currentWeatherJson.get("temperature_2m");
           System.out.println("Current Temperature (C) : " + temperature);

           long relativeHumidity = (long) currentWeatherJson.get("relative_humidity_2m");
           System.out.println("Relative Humidity  : " + relativeHumidity);

           long isDay = (long)currentWeatherJson.get("is_day");
           System.out.println("Is Day ? : " + isDay);

           double windSpeed = (double) currentWeatherJson.get("wind_speed_10m");
           System.out.println("Wind Speed : " + windSpeed);

           long windDirection = (long) currentWeatherJson.get("wind_direction_10m");
           System.out.println("Wind Direction : " + windDirection);

           double rain = (double) currentWeatherJson.get("rain");
           System.out.println("Rain : "+rain);

           double snowfall = (double) currentWeatherJson.get("snowfall");
           System.out.println("Snowfall : "+snowfall);

       }
       catch(Exception e){
           e.printStackTrace();
       }

    }




    //method to get the location of the given city
    private static JSONObject getLocationData(String city) {
        city = city.replace(" ","+");
        //url to get location of the city by its name from open - meteo website
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name="+city+"&count=1&language=en&format=json";

        try {
            HttpURLConnection apiConnection = fetchApiResponse(urlString);

            if (apiConnection.getResponseCode() != 200) {
                System.out.println("ERROR : Could not connect to API");
                return null;
            }

            String jsonResponse = readApiResponse(apiConnection);

            JSONParser parser = new JSONParser();
            JSONObject resultJSONObj = (JSONObject) parser.parse(jsonResponse);
            JSONArray locationData = (JSONArray) resultJSONObj.get("results");
            return (JSONObject) locationData.get(0);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;

    }



    //method to fetch api response and response code from the url
    private static HttpURLConnection fetchApiResponse(String urlString){
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            return conn;
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }



    //method to get the result json and convert it as string
    private static String readApiResponse(HttpURLConnection apiConnection){
        try {
            StringBuilder resultJson = new StringBuilder();
            Scanner scan = new Scanner(apiConnection.getInputStream());
            while (scan.hasNext()) {
                resultJson.append(scan.nextLine());
            }

            scan.close();

            return resultJson.toString();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
}