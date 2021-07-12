package eu.remi.pactPresentation.consumer;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EventServiceTest {
  private WireMockServer wireMockServer;
  private EventService eventService;

  @BeforeEach
  void setUp() {
    wireMockServer = new WireMockServer(options().dynamicPort());

    wireMockServer.start();

    RestTemplate restTemplate = new RestTemplateBuilder()
        .rootUri(wireMockServer.baseUrl())
        .build();

    eventService = new EventService(restTemplate);
  }

  @AfterEach
  void tearDown() {
    wireMockServer.stop();
  }

  @Test
  void getAllEvents() {
    wireMockServer.stubFor(get(urlPathEqualTo("/events"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("[" +
                "{\"id\":\"1111\",\"sportId\":\"sr:sport:1\",\"name\":\"Kostabona vs Puce\"},"+
                "{\"id\":\"2222\",\"sportId\":\"sr:sport:1\",\"name\":\"Lopar vs Babici\"}"+
                "]")));

    List<Event> expected = List.of(new Event("1111", "sr:sport:1", "Kostabona vs Puce"),
        new Event("2222", "sr:sport:1", "Lopar vs Babici"));

    List<Event> events = eventService.getAllEvents();

    assertEquals(expected, events);
  }

  @Test
  void getEventById() {
    wireMockServer.stubFor(get(urlPathEqualTo("/events/1111"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"id\":\"1111\",\"sportId\":\"sr:sport:1\",\"name\":\"Kostabona vs Puce\"}")));

    Event expected = new Event("1111", "sr:sport:1", "Kostabona vs Puce");

    Event event = eventService.getEvent("1111");

    assertEquals(expected, event);
  }
}