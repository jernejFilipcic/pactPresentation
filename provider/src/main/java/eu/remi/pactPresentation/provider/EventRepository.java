package eu.remi.pactPresentation.provider;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class EventRepository {

    private final Map<String, Event> EVENTS = Map.of(
            "3333", new Event("3333", "sr:sport:1", "Abitanti vs Pregara"),
            "1111", new Event("1111", "sr:sport:1", "Kostabona vs Puce"),
            "2222", new Event("2222", "sr:sport:1", "Lopar vs Babici")
    );

    public List<Event> fetchAll() {
        return List.copyOf(EVENTS.values());
    }

    public Optional<Event> getById(String id) {
        return Optional.ofNullable(EVENTS.get(id));
    }

}
