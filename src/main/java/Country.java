import java.util.HashSet;
import java.util.Set;

public class Country {
    String name;
    Set<String> languages = new HashSet<>();
    Set<Integer> timeZones = new HashSet<>();

    public Country(String name, Set<String> languages, Set<Integer> timeZones) {
        this.name = name;
        this.languages = languages;
        this.timeZones = timeZones;
    }
}
