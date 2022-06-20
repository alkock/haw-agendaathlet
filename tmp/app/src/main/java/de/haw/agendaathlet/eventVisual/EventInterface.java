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

import java.time.LocalDate;
import java.time.LocalTime;

public interface EventInterface {

    /**
     * Gibt der Namen Events zurück
     */
    String getName();

    /**
     * Ändert der Namen Events
     *
     * @param name
     */
    void setName(String name);

    /**
     * Gibt das Datum Events zurück
     */
    LocalDate getDate();

    /**
     * Ändert das Datum Events
     *
     * @param date
     */
    void setDate(LocalDate date);

    /**
     * Gibt die startZeit Events zurück
     *
     * @return
     */
    LocalTime getstarTime();

    /**
     * Ändert die Zeit Events
     *
     * @param time
     */
    void setstartTime(LocalTime time);

    /**
     * Gibt die EndZeit Event zurück
     *
     * @return
     */
    LocalTime getendTime();

    /**
     * Ändert die Endzeit Events
     *
     * @param time
     */
    void setendTime(LocalTime time);

    /**
     * Gibt die Event beschreibung zurück
     *
     * @return
     */
    String getDescription();

    /**
     * Ändert die Event beschreibung
     *
     * @param description
     */
    void setDescription(String description);
}
