package models;

import io.ebean.Model;
import io.ebean.Finder;


import javax.persistence.*;


@Entity
@Table(name = "events")
public class Event extends Model {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int totalTickets;

    @Column(nullable = false)
    private int availableTickets;

    public Event(String name, int totalTickets, int availableTickets) {
        this.name = name;
        this.totalTickets = totalTickets;
        this.availableTickets = availableTickets;
    }

    public static Finder<Long, Event> find =
            new Finder<>(Event.class);

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public int getAvailableTickets() {
        return availableTickets;
    }

    public void setAvailableTickets(int availableTickets) {
        this.availableTickets = availableTickets;
    }
}
