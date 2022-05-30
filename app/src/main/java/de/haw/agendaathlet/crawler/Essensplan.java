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
package de.haw.agendaathlet.crawler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface Essensplan {

    /**
     * Lädt den Essensplan von der Website der Mensa
     * @throws Exception
     */
    void ladeEssen(String url, int preis) throws Exception;

    /**
     * Gibt die Menüliste zurück
     * @return ArrayList mit den Menüs
     */
    ArrayList<String> gibmenuliste();

    /**
     * Gibt die Preisliste zurück
     * @return ArrayList mit den Preisen im Format EE,CC
     */
    ArrayList<String> gibpriceliste();

    /**
     * Gibt die Dateliste zurück
     * @return ArrayList mit den Dates
     */
    ArrayList<LocalDate> gibdateliste();
}