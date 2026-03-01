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

public class App {

    public static Map<String, Country> allCountries = new HashMap<>(); // used to store countries without duplicating

    public static void main(String[] args) throws Exception {
        loader();
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
                        String[] splitter = rawTimeZone.replace("UTC", "").split(":");
                        int timeZoneHours = Integer.parseInt(splitter[0]);
                        timeZones.add(timeZoneHours);
                    }
                }

                Country newCountry = new Country(countryName, languages, timeZones);
                allCountries.put(countryName, newCountry);
            }
        } catch (IOException | InterruptedException e) {

        }

    }

    public static void findCenter(Country homeCountry, int currentTime){ //will assume normal business hours are 8 AM to 5 PM
        int hours = currentTime/100; //coverts to 2 digit hours
        int minutes = currentTime%100; //converts to remaining minutes
    }

}
