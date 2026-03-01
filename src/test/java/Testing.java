import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

//documentation to reference: 
// https://openjdk.org/groups/net/httpclient/intro.html - basic java http setup, similar to C# 
// https://www.baeldung.com/java-httpclient-map-json-response - uses GSON, for an idea
// https://www.geeksforgeeks.org/java/how-to-setup-jackson-in-java-application/ - Jackson library for java would probably handle the JSON better

public class Testing {
    @Test
    void countryTotal() {
        Set<String> countries = new HashSet<>(); // chose a hashset because it doesn't have duplicates, I also don't
                                                 // have a value to track with it where a hashmap wuld be more useful
        String url = "https://restcountries.com/v3.1/all/?fields=name"; // all by itself does not work because of the 10
                                                                        // limit but if I filter by just name I can work
                                                                        // off of that
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString()); 
            String body = response.body(); 

            JsonArray jsonArray = JsonParser.parseString(body).getAsJsonArray(); 
            //take the official name for each country and add to hashset
            for (JsonElement element : jsonArray) {
                String name = element.getAsJsonObject().getAsJsonObject("name").get("official").getAsString(); 
                countries.add(name);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(250, countries.size()); // I am assuming max countries of 250 and all "statuses" count in my
                                             // assumptions. COuld filter this by status as well or have a range that passes
    }

    @Test
    void saslOfficial() {
        // very similar approach as #1 the main difference is the languages are stored
        // as a key value pair so accessing it was slightly easier
        Set<String> languages = new HashSet<>(); // storing each languages
        String url = "https://restcountries.com/v3.1/name/South%20Africa?fields=languages"; // just need SA languages
                                                                                            // for this test
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString()); 
            String body = response.body(); 

            JsonArray jsonArray = JsonParser.parseString(body).getAsJsonArray();
            // uses 0 to get the only element and then access languages
            JsonObject languagesObject = jsonArray.get(0).getAsJsonObject().get("languages").getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : languagesObject.entrySet()) {
                languages.add(entry.getKey()); // add each language to the set. I am assuming it is SASL for testing
                                               // but if this can vary could do getValue and as String to eliminate that
                                               // risk
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if (languages.contains("sasl")) { // lower case to match API
            System.out.println("South African Sign Language is an official language");
        } else {
            System.out.println("South African Sign Language is not an official language at this moment");
        }
        assertFalse(languages.contains("sasl"), "SASL Found"); // this should fail since it is not updated in the API
    }

    @Test
    void fieldCheck() {
        // going to just check the status as 400 or 200. Could also return a count of
        // fields but the documentation also supports the 10 limit
        String overURL = "https://restcountries.com/v3.1/all?fields=languages,name,capital,currencies,borders,area,coatOfArms,continents,independent,fifa,flag";
        String underURL = "https://restcountries.com/v3.1/all?fields=languages,name,capital,currencies,borders,area,coatOfArms,continents,independent,fifa";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest overRequest = HttpRequest.newBuilder().uri(URI.create(overURL)).GET().build();
        HttpRequest underRequest = HttpRequest.newBuilder().uri(URI.create(underURL)).GET().build();
        HttpResponse<String> overResponse = null; // had to set initially null here because the assert
        HttpResponse<String> underResponse = null; // wasn't able to reach the variable in the try catch
        try {
            overResponse = client.send(overRequest, BodyHandlers.ofString()); // store the response for 11 fields
            underResponse = client.send(underRequest, BodyHandlers.ofString()); // store the response for 10 fields
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(200, underResponse.statusCode()); // test for passing at 10 fields
        assertEquals(400, overResponse.statusCode()); // test for giving 400 code once 11 fields are entered
    }

    @Test
    void antarctica() {
        // has no languages to match with
        App.loader();
        Map<String, Country> results = App.findCentre("Antarctica", 1000);
        assertTrue(results.isEmpty(), "Antarctica should never have a return");
    }

    @Test
    void midnight() {
        // testing if time zones in he next day are good
        App.loader();
        Map<String, Country> results = App.findCentre("Republic of Ghana", 2200);
        assertTrue(results.containsKey("New Zealand"), "New Zealand will be 10 AM the next day");
    }

    @Test
    void nine() {
        // testing if 9 AM exactly fails
        App.loader();
        Map<String, Country> results = App.findCentre("Hashemite Kingdom of Jordan", 900);
        assertTrue(results.containsKey("Kingdom of Saudi Arabia"), "Saudi Arabia is exactly 900");
    }

    @Test
    void five() {
        //testing if 5 o'clock exactly works as well
        App.loader();
        Map<String, Country> results = App.findCentre("Hashemite Kingdom of Jordan", 1700);
        assertTrue(results.containsKey("Kingdom of Saudi Arabia"),"Saudi Arabia is exactly 1700");
    }

    @Test
    void ownCountry() {
        //testing if 5 o'clock exactly works as well
        App.loader();
        Map<String, Country> results = App.findCentre("Hashemite Kingdom of Jordan", 1200);
        assertTrue(results.containsKey("Hashemite Kingdom of Jordan"),"Own country is within business hours");
    }
}
