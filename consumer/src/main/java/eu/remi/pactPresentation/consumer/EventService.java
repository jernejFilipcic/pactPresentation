package eu.remi.pactPresentation.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class EventService {

  private final RestTemplate restTemplate;

  @Autowired
  public EventService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<Event> getAllEvents() {
    return restTemplate.exchange(
        "/events",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<Event>>() {}).getBody();
  }

  public Event getEvent(String id) {
    //TODO here errors are to happen (call events instead of event)
    return restTemplate.getForEntity("/events/{id}", Event.class, id).getBody();
  }
}
