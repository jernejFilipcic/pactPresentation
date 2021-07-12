package eu.remi.pactPresentation.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

@Component
public class ConsoleInterface implements CommandLineRunner {

  private final EventService eventService;

  private List<Event> events;

  @Autowired
  ConsoleInterface(EventService eventService) {
    this.eventService = eventService;
  }

  @Override
  public void run(String... args) {
    Scanner scanner = new Scanner(System.in);
    while (true) {
      printAllEvents();
      Integer choice = getUserChoice(scanner);
      if (choice == null || choice <= 0 || choice > events.size()) {
        System.out.println("Exiting...");
        break;
      }
      printEvent(choice);
    }
  }

  private void printAllEvents() {
    events = eventService.getAllEvents();
    System.out.println("\n\nEvents\n--------");
    IntStream.range(0, events.size())
        .forEach(index -> System.out.println(String.format(
            "%d) %s",
            index + 1,
            events.get(index).getName())));
  }

  private Integer getUserChoice(Scanner scanner) {
    System.out.print("Select item to view details: ");
    String choice = scanner.nextLine();
    return parseChoice(choice);
  }

  private void printEvent(int index) {
    String id = events.get(index - 1).getId();
    Event event = eventService.getEvent(id);

    System.out.println("Event Details\n---------------");
    System.out.println(event);
  }

  private Integer parseChoice(String choice) {
    try {
      return Integer.parseInt(choice);
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
