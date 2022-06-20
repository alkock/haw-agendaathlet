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

import java.io.IOException;
import java.net.URL;
import java.util.List;

public interface ICSCrawler {
    /**
     * Diese Methode filtert entsprechende URLs, um ICS-Links zu identifizieren
     * und dann zu sichern.
     */
    void getAll(String extraUrl) throws IOException;

    /**
     * Diese Methode gibt eine Liste von URLs zurück, die die ICS-Links enthalten.
     *
     * @return Liste von URLs als HTTPS Link
     */
    List<URL> getUrlList();

    /**
     * Diese Methode gibt eine Liste von Namen zurück, die die Namen der Ics Files enthalten.
     *
     * @return Liste von Namen der Module
     */
    List<String> getNameList();
}
