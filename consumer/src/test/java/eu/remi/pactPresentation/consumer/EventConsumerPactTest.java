package eu.remi.pactPresentation.consumer;


import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonArrayMinLike;
import static io.pactfoundation.consumer.dsl.LambdaDsl.newJsonBody;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(PactConsumerTestExt.class)
public class EventConsumerPactTest {

  @Pact(consumer = "FrontendApplication", provider = "EventService")
  RequestResponsePact getAllEvents(PactDslWithProvider builder) { //ime metode je to, kar daš v @PactTestFor anotacijo na testu
    return builder.given("events exist") //to se zapiše v json kot "interactions.providerStates.name"
        .uponReceiving("get all events") //to se zapiše v json kot "interactions.description"
        .method("GET")
        .path("/events")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "application/json; charset=utf-8"))
        .body(newJsonArrayMinLike(2, array ->
            array.object(object -> {
              object.stringType("id", "sr:match:1234");
              object.stringType("sportId", "sr:sport:1");
              object.stringType("name", "Kostabona vs Puce");
            })
        ).build())
        .toPact();
  }

  @Pact(consumer = "FrontendApplication", provider = "EventService")
  RequestResponsePact noEventsExist(PactDslWithProvider builder) {
    return builder.given("no events exist")
        .uponReceiving("get all events")
        .method("GET")
        .path("/events")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "application/json; charset=utf-8"))
        .body("[]")
        .toPact();
  }

  @Pact(consumer = "FrontendApplication", provider = "EventService")
  RequestResponsePact getOneEvent(PactDslWithProvider builder) {
    return builder.given("event with ID 1111 exists")
        .uponReceiving("get event with ID 1111")
        .method("GET")
        .path("/event/1111")
        .willRespondWith()
        .status(200)
        .headers(Map.of("Content-Type", "application/json; charset=utf-8"))
        .body(newJsonBody(object -> {
          object.stringType("id", "sr:match:1234");
          object.stringType("sportId", "sr:sport:1");
          object.stringType("name", "Kostabona vs Puce");
        }).build())
        .toPact();
  }

  @Pact(consumer = "FrontendApplication", provider = "EventService")
  RequestResponsePact eventDoesNotExist(PactDslWithProvider builder) {
    return builder.given("event with ID 2222 does not exist")
        .uponReceiving("get event with ID 2222")
        .method("GET")
        .path("/event/11")
        .willRespondWith()
        .status(404)
        .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "getAllEvents")  //ime metode s @Pact anotacijo.... ustvaril bo primeren MockServer
  void getAllEvents_whenEventsExist(MockServer mockServer) {
    Event event = new Event();
    event.setId("sr:match:1234");
    event.setSportId("sr:sport:1");
    event.setName("Kostabona vs Puce");
    List<Event> expected = List.of(event, event);

    RestTemplate restTemplate = new RestTemplateBuilder()
        .rootUri(mockServer.getUrl())
        .build();
    List<Event> events = new EventService(restTemplate).getAllEvents();

    assertEquals(expected, events);
  }

  @Test
  @PactTestFor(pactMethod = "noEventsExist")
  void getAllEvents_whenNoEventsExist(MockServer mockServer) {
    RestTemplate restTemplate = new RestTemplateBuilder()
        .rootUri(mockServer.getUrl())
        .build();
    List<Event> events = new EventService(restTemplate).getAllEvents();

    assertEquals(Collections.emptyList(), events);
  }

  @Test
  @PactTestFor(pactMethod = "getOneEvent")
  void getEventById_whenEventWithId1111Exists(MockServer mockServer) {
    Event expected = new Event();
    expected.setId("sr:match:1234");
    expected.setSportId("sr:sport:1");
    expected.setName("Kostabona vs Puce");

    RestTemplate restTemplate = new RestTemplateBuilder()
        .rootUri(mockServer.getUrl())
        .build();
    Event event = new EventService(restTemplate).getEvent("1111");

    assertEquals(expected, event);
  }

  @Test
  @PactTestFor(pactMethod = "eventDoesNotExist")
  void getEventById_whenEventWithId11DoesNotExist(MockServer mockServer) {
    RestTemplate restTemplate = new RestTemplateBuilder()
        .rootUri(mockServer.getUrl())
        .build();

    HttpClientErrorException e = assertThrows(
        HttpClientErrorException.class,
        () -> new EventService(restTemplate).getEvent("11"));
    assertEquals(404, e.getRawStatusCode());
  }
}