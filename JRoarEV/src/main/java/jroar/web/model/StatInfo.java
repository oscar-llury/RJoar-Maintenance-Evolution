package jroar.web.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class StatInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    String date;

    int visits;

    public StatInfo(String date, int visits) {
        this.date = date;
        this.visits = visits;
    }

    public StatInfo() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }
}