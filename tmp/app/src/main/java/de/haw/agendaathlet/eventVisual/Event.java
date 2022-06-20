/**
 * HAWAgendaAthlet Android client application
 *
 * @author Tobias Kröger
 * @author Leon Schardin
 * @author Kaleb Pohl
 * @author Erfan Akhondi
 * @author Taalaibek Mateev
 * @author Ansgar Leonard Kock
 * Copyright (C) 2022.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3,
 * as published by the Free Software Foundation.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.haw.agendaathlet.eventVisual;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Event implements Serializable, EventInterface {

    private String name;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String description;
    /**
     * ID AUS DER DB NICHT AUS DER LISTE!!!
     ****/
    private int id;

    /**
     * KONSTRUKTOR NUR FÜR DATENBANK !!!!
     */
    public Event(int id, String name, LocalDate date, LocalTime startTime, LocalTime endTime, String description) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

    /**
     * KONSTRUKTOR FÜR ALLE
     **/
    public Event(String name, LocalDate date, LocalTime startTime, LocalTime endTime, String description) {
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getstarTime() {
        return startTime;
    }

    public void setstartTime(LocalTime time) {
        this.startTime = time;
    }

    public LocalTime getendTime() {
        return endTime;
    }

    public void setendTime(LocalTime time) {
        this.endTime = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * DIESE METHODE IST NUR FÜR DIE DB!
     **/
    public void setID(int id) {
        this.id = id;
    }

    /**
     * DIESE METHODE IST NUR FÜR DIE DB!
     **/
    public int getID() {
        return id;
    }
}
