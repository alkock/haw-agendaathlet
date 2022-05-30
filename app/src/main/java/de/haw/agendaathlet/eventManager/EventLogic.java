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
package de.haw.agendaathlet.eventManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;

import de.haw.agendaathlet.eventVisual.Event;

public interface EventLogic {
    /**
     * Gibt die Event-Liste zurück.
     * @return ArrayList<Event>
     */
    ArrayList<Event> getEventList();

    /**
     * Gibt die aktuelle Event-Liste zurück.
     * @return ArrayList<Event>
     */
    ArrayList<Event> getCurrentEventList();

    /**
     * Gibt die Events für ein bestimmtes Datum zurück.
     * @param date
     * @return ArrayList<Event>
     */
    ArrayList<Event> eventsForDate(LocalDate date);

    /**
     * Gibt die Events für ein bestimmtes Datum und eine bestimmte Uhrzeit zurück.
     * @param date
     * @param time
     * @return ArrayList<Event>
     */
    ArrayList<Event> eventsForDateAndTime(LocalDate date, LocalTime time);

    /**
     * Fügt Events aus einer ICS-Datei hinzu.
     * @param x ICS-Datei
     */
    void addicstoEvents(String x);
}
