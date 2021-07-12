package eu.remi.pactPresentation.provider;

import java.util.Objects;

public class Event {

    private String id;
    private String sportId;
    private String name;

    public Event() {
    }

    public Event(
        String id,
        String sportId,
        String name
    ) {
        this.id = id;
        this.sportId = sportId;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSportId() {
        return sportId;
    }

    public void setSportId(String sportId) {
        this.sportId = sportId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id) &&
            Objects.equals(sportId, event.sportId) &&
            Objects.equals(name, event.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sportId, name);
    }

    @Override
    public String toString() {
        return "Event{" +
            "id='" + id + '\'' +
            ", sportId='" + sportId + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
