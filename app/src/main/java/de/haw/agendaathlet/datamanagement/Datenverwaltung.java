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
package de.haw.agendaathlet.datamanagement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import de.haw.agendaathlet.essen.Essen;
import de.haw.agendaathlet.eventVisual.Event;

/**
 * Schnittstelle für eine Datenverwaltung
 *
 * @author Ansgar
 */
public interface Datenverwaltung {
    /**
     * Speichert eine Liste von Events.
     * Redundate Speicherungen werden überschrieben.
     *
     * @param o Liste von Events
     */
    void speichereEvents(List<Event> o);

    /**
     * Lädt eine Liste von Events die zuvor in der DB gespeichert wurden.
     *
     * @return Liste von Events
     */
    ArrayList<Event> ladeEvents();

    /**
     * Speichert eine Liste von Essen.
     * Redundante Speicherungen werden überschrieben.
     *
     * @param localname Liste von Essen.
     */
    void speichereEssen(List<String> localname, List<String> localpreis, List<LocalDate> localdate);

    /**
     * Lädt eine Liste von Essen die zuvor in der DB gespeichert wurden.
     *
     * @return Eine Liste mit drei Listen: List<String> localname, List<String> localpreis, List<LocalDate> localdate
     */
    ArrayList<Essen> ladeEssen();

    ArrayList<Essen> ladeEssen(LocalDate date);

    /*
    Löscht ein Event nach ID in der Liste Events.
     */
    void loescheEvent(Event event);

    /*
    Diese Methode updatet ein Event, welches bereits in der Datenbank gespeichert wurde.
    @require: Event muss in der Datenbank bereits gespeichert worden sein.
    @require: Event != null
    @ensure: Event wird in der Datenbank aktualisiert nach ihrer EventID.
     */
    void updateEvent(Event event);

    /*
    Löscht alle Events.
     */
    void loescheAlles();

    /*
      Dieser Boolean gibt aus, ob die Synchronisation mit der Cloud aktiv ist.
     */
    boolean isFirebaseSynchActive();

    /*
        Diese Methode deaktiviert oder aktiviert die Synchronisation mittels der Cloud.
        true -> aktivieren
        false -> deaktivieren
     */
    void setFirebaseSynchActive(boolean firebaseSynchActive);
}
