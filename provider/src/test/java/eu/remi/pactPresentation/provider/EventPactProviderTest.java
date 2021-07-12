package eu.remi.pactPresentation.provider;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@Provider("EventService")
@PactFolder("pacts")
//@PactBroker(
//    host = "localhost",
//    port = "8000",
//    authentication = @PactBrokerAuth(username = "remi", password = "remi")
//)

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventPactProviderTest {

  @LocalServerPort
  int port;

  @MockBean
  private EventRepository eventRepository;

  @BeforeEach
  void setUp(PactVerificationContext context) {
    context.setTarget(new HttpTestTarget("localhost", port));
  }

  @TestTemplate
  @ExtendWith(PactVerificationInvocationContextProvider.class)
  void verifyPact(PactVerificationContext context) {
    context.verifyInteraction();
  }

  @State("events exist")
  void toEventsExistState() {
    when(eventRepository.fetchAll()).thenReturn(
        List.of(new Event("1111", "sr:sport:1", "Kostabona vs Puce"),
            new Event("2222", "sr:sport:1", "Lopar vs Babici")));
  }

  @State({
      "no events exist",
      "event with ID 2222 does not exist"
  })
  void toNoEventsExistState() {
    when(eventRepository.fetchAll()).thenReturn(Collections.emptyList());
  }

  @State("event with ID 1111 exists")
  void toEventWithId1111ExistsState() {
    when(eventRepository.getById("1111")).thenReturn(Optional.of(new Event("1111", "sr:sport:1", "Kostabona vs Puce")));
  }

  @State("event with ID vv2222 does not exist")
  void toEventWithId2222DoesNotExistState() {}

}
