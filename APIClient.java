package Task2_APIClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APIClient {

    private static final String API_URL =
            "https://api.open-meteo.com/v1/forecast?latitude=22.57&longitude=88.36&current_weather=true";

    public static void main(String[] args) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                String json = response.toString();

                Matcher tempMatcher = Pattern.compile("\"temperature\":(-?\\d+(\\.\\d+)?)").matcher(json);
                Matcher codeMatcher = Pattern.compile("\"weathercode\":(\\d+)").matcher(json);
                Matcher timeMatcher = Pattern.compile("\"time\":\"([^\"]+)\"").matcher(json);

                double temperature = tempMatcher.find() ? Double.parseDouble(tempMatcher.group(1)) : Double.NaN;
                int weatherCode = codeMatcher.find() ? Integer.parseInt(codeMatcher.group(1)) : -1;
                String timeRaw = timeMatcher.find() ? timeMatcher.group(1) : "N/A";

                String weatherDescription = getWeatherDescription(weatherCode);
                String formattedTime = formatTime(timeRaw);

                System.out.println("Weather Data for Kolkata:");
                System.out.println("Temperature: " + temperature + " °C");
                System.out.println("Condition: " + weatherDescription + " (code " + weatherCode + ")");
                System.out.println("Time: " + formattedTime);

            } else {
                System.out.println("Error: HTTP code " + responseCode);
            }

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private static String getWeatherDescription(int code) {
        switch (code) {
            case 0: return "Clear sky";
            case 1: case 2: return "Mainly clear";
            case 3: return "Partly cloudy";
            case 45: case 48: return "Fog";
            case 51: case 53: case 55: return "Drizzle";
            case 61: case 63: case 65: return "Rain";
            case 71: case 73: case 75: return "Snow";
            case 95: return "Thunderstorm";
            default: return "Unknown";
        }
    }

    private static String formatTime(String isoTime) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(isoTime);
            return dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
        } catch (Exception e) {
            return isoTime;
        }
    }
}
