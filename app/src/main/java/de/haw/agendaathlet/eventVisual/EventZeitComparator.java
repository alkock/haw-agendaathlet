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

import java.util.Comparator;

public class EventZeitComparator implements Comparator<Event> {
    @Override
    //Event has date, hour and minute
    public int compare(Event event1, Event event2) {
        if (event1.getDate().compareTo(event2.getDate()) == 0) {
            if (event1.getstarTime().getHour() == event2.getstarTime().getHour()) {
                return event1.getstarTime().getMinute() - event2.getstarTime().getMinute();
            } else {
                return event1.getstarTime().getHour() - event2.getstarTime().getHour();
            }
        } else {
            return event1.getDate().compareTo(event2.getDate());
        }
    }
}
