import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashSet;
import java.util.Set;

//documentation to reference: https://openjdk.org/groups/net/httpclient/intro.html

public class Testing {
    @Test
    void countryTotal() {
        Set<String> countries = new HashSet<>();
        String url = "https://restcountries.com/v3.1/all/?fields=name"; // all by itself does not work because of the 10
                                                                        // limit but if I filter by just name I can work
                                                                        // off of that
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        client.sendAsync(request, BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(System.out::println).join(); //will need to change this bit to handle the JSON being returned and add the names to the set
    }
}
