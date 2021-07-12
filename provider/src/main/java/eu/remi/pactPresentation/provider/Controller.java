package eu.remi.pactPresentation.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class Controller {

    private final EventRepository eventRepository;

    @Autowired
    Controller(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping("findEvents")
    public List<Event> getAllEvents() {
        return eventRepository.fetchAll();
    }

    @GetMapping("findEvents/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable("id") String id) {
        Optional<Event> event = eventRepository.getById(id);

        return ResponseEntity.of(event);
    }
}
