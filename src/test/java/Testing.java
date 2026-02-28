import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

//documentation to reference: 
// https://openjdk.org/groups/net/httpclient/intro.html - basic java http setup, similar to C# 
// https://www.baeldung.com/java-httpclient-map-json-response - uses GSON, for an idea
// https://www.geeksforgeeks.org/java/how-to-setup-jackson-in-java-application/ - Jackson library for java would probably handle the JSON better

public class Testing {
    @Test
    void countryTotal() {
        ObjectMapper objectMapper = new ObjectMapper();
        Gson gson = new GsonBuilder().create();
        Set<String> countries = new HashSet<>(); // chose a hashset because it doesn't have duplicates, I also don't
                                                 // have a value to track with it where a hashmap wuld be more useful
        String url = "https://restcountries.com/v3.1/all/?fields=name"; // all by itself does not work because of the 10
                                                                        // limit but if I filter by just name I can work
                                                                        // off of that
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString()); //store the response 
            String body = response.body(); //take the raw JSON into a string

            JsonArray jsonArray = JsonParser.parseString(body).getAsJsonArray(); //parse into array
            for (JsonElement element : jsonArray) {
                String commonName = element.getAsJsonObject().getAsJsonObject("name").get("official").getAsString(); //take the official name and add to hashset
                countries.add(commonName);
            }
        } catch (IOException | InterruptedException e) {

        }

        assertEquals(250, countries.size()); // I am assuming max countries of 250 and all "statuses" count in my
                                             // assumptions
    }
}
