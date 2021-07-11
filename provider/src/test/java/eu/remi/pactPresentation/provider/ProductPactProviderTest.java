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

@Provider("ProductService")
@PactFolder("pacts")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductPactProviderTest {

  @LocalServerPort
  int port;

  @MockBean
  private ProductRepository productRepository;

  @BeforeEach
  void setUp(PactVerificationContext context) {
    context.setTarget(new HttpTestTarget("localhost", port));
  }

  @TestTemplate
  @ExtendWith(PactVerificationInvocationContextProvider.class)
  void verifyPact(PactVerificationContext context) {
    context.verifyInteraction();
  }

  /* v @state je to, kar si dal v .given, ko si ustvarjal s @Pact anotacijo na testih consumerja
     in se je zapisalo v "interactions.providerStates.name".
     Iz jsona bo povleklo, kaj je bilo klicano in kaj bi moralo dobiti nazaj. Preverilo se bo,
     da dejansko reagira na ta klic in da vrne nazaj točno to (zraven je pripadajoči matcher)
  */
  @State("products exist")
  void toProductsExistState() {
    when(productRepository.fetchAll()).thenReturn(
        List.of(
            new Product("09", "CREDIT_CARD", "Gem Visa", "v1"),
            new Product("10", "CREDIT_CARD", "28 Degrees", "v1")));
  }

  @State({
      "no products exist",
      "product with ID 11 does not exist"
  })
  void toNoProductsExistState() {
    when(productRepository.fetchAll()).thenReturn(Collections.emptyList());
  }

  @State("product with ID 10 exists")
  void toProductWithIdTenExistsState() {
    when(productRepository.getById("10")).thenReturn(Optional.of(new Product(
        "10",
        "CREDIT_CARD",
        "28 Degrees",
        "v1")));
  }

  @State("product with ID 11 does not exist")
  void toProductWithIdElevenDoesNotExistState() {}

}
