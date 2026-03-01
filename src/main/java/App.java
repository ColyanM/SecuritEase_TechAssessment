import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Scanner;

public class App {

    public static Map<String, Country> allCountries = new HashMap<>(); // used to store countries without duplicating

    public static void main(String[] args) throws Exception {
        loader();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your country");
        String country = scanner.nextLine();
        System.out.println("Enter current time with 24H format");
        int time = scanner.nextInt();
        findCentre(country, time);
    }

    public static void loader() {
        String url = "https://restcountries.com/v3.1/all?fields=languages,name,timezones";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString()); // JSON response storage
            String body = response.body(); // JSON into string format

            JsonArray jsonArray = JsonParser.parseString(body).getAsJsonArray(); // parse into array
            for (JsonElement element : jsonArray) { // using this loop to construct country objects and add to master
                                                    // hashmap
                String countryName = element.getAsJsonObject().getAsJsonObject("name").get("official").getAsString(); // pulls
                                                                                                                      // country
                                                                                                                      // name
                                                                                                                      // out

                Set<String> languages = new HashSet<>();
                JsonObject languagesObject = element.getAsJsonObject().get("languages").getAsJsonObject(); // takes each
                                                                                                           // countries
                                                                                                           // languages
                                                                                                           // and adds
                                                                                                           // to hashset
                                                                                                           // for
                                                                                                           // uniqueness
                for (Map.Entry<String, JsonElement> entry : languagesObject.entrySet()) {
                    languages.add(entry.getValue().getAsString());
                }

                Set<Integer> timeZones = new HashSet<>();
                JsonArray timeZonesList = element.getAsJsonObject().get("timezones").getAsJsonArray(); // same approach
                                                                                                       // with time
                                                                                                       // zones as
                                                                                                       // languages but
                                                                                                       // need to remove
                                                                                                       // UTC and spaces
                                                                                                       // for int format

                for (JsonElement timeZone : timeZonesList) {
                    String rawTimeZone = timeZone.getAsString();
                    if (rawTimeZone.equals("UTC")) { // handles no number for UTC
                        timeZones.add(0);
                    } else {
                        String[] splitter = rawTimeZone.replace("UTC", "").split(":"); // splits the numbers into hours
                                                                                       // and min
                        int timeZoneHours = Integer.parseInt(splitter[0]);
                        int timeZoneMinutes = Integer.parseInt(splitter[1]);
                        int totalMinutes = 0;
                        if (timeZoneHours < 0) {
                            totalMinutes = (timeZoneHours * 60) - timeZoneMinutes; // Negative UTC handling
                        } else {
                            totalMinutes = (timeZoneHours * 60) + timeZoneMinutes;
                        }
                        timeZones.add(totalMinutes);
                    }
                }

                Country newCountry = new Country(countryName, languages, timeZones);
                allCountries.put(countryName, newCountry);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    // change to return a map so I can write tests
    public static Map<String, Country> findCentre(String homeCountry, int currentTime) {
        int hours = currentTime / 100; // coverts to 2 digit hours
        int minutes = currentTime % 100; // converts to remaining minutes
        if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) { // checks the time is valid 24H time
            System.out.println("Please enter a valid time");
            return new HashMap<>();
        }
        Country homeCountryObject = null;
        for (Map.Entry<String, Country> findCountry : allCountries.entrySet()) { // loop over all countries
            if (findCountry.getKey().equalsIgnoreCase(homeCountry)) { // to match with entered string
                homeCountryObject = findCountry.getValue();
            }
        }
        if (homeCountryObject == null) { // handling no country found
            System.out.println("Please enter a valid country");
            return new HashMap<>();
        }

        int homeCountryMins = homeCountryObject.timeZones.iterator().next(); // will assume the first time zone for the
                                                                             // country
        int utc = (hours * 60 + minutes) - homeCountryMins; // calculates the current UTC time

        Map<String, Country> eligibleCountries = new HashMap<>(); // holds countries that match requirements later
        Map<String, Integer> localTimes = new HashMap<>(); // going to use to print and test the local time

        for (Map.Entry<String, Country> findEligibleCountries : allCountries.entrySet()) { // go over each country
            for (String lang : findEligibleCountries.getValue().languages) { // loop each countries languages
                for (int timeZoneMath : findEligibleCountries.getValue().timeZones) { // and their time zones
                    int countryLocalTime = utc + timeZoneMath; // find their local time for business hours checking
                    countryLocalTime = ((countryLocalTime % 1440) + 1440) % 1440; // if next day is needed
                    int countryLocalTime24h = (countryLocalTime / 60) * 100 + (countryLocalTime % 60); // convert time
                                                                                                       // to 24H
                    if (homeCountryObject.languages.contains(lang) && countryLocalTime24h >= 900 // match the language
                                                                                                 // and time zone
                            && countryLocalTime24h <= 1700) {
                        eligibleCountries.put(findEligibleCountries.getKey(), findEligibleCountries.getValue());
                        localTimes.put(findEligibleCountries.getKey(), countryLocalTime24h);
                        break; // added so a country isn't duplicated from multiple time zones
                    }
                }
            }
        }

        if (eligibleCountries.isEmpty()) {
            System.out.println("No eligible countries to take call");
        } else {
            for (Map.Entry<String, Country> entry : eligibleCountries.entrySet()) { // need to format this to print
                                                                                    // current time correctly for that
                                                                                    // country and time zone
                int localTime = localTimes.get(entry.getKey());
                System.out.println("Name: " + entry.getValue().name);
                System.out.println("Languages: " + entry.getValue().languages);
                System.out.println("Time: " + localTime);
                System.out.println("-------------------------------------------------------------");
            }
        }
        return eligibleCountries;
    }

}
