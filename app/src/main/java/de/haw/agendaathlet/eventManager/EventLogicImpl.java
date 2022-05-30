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

import android.util.Log;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.haw.agendaathlet.InjectorManager;
import de.haw.agendaathlet.datamanagement.Datenverwaltung;
import de.haw.agendaathlet.eventVisual.Event;

public class EventLogicImpl implements EventLogic {

    Datenverwaltung datenverwaltung;
    private final ArrayList<Event> eventsList;

    public EventLogicImpl()
    {
        datenverwaltung = InjectorManager.IM.gibDatenverwaltung();
        eventsList = datenverwaltung.ladeEvents();
    }

    public ArrayList<Event> getEventList() {
        return eventsList;
    }

    public ArrayList<Event> getCurrentEventList() {
        ArrayList<Event> result = new ArrayList<Event>();

        for(Event ev: eventsList) if(ev.getDate().compareTo(LocalDate.now()) >= 0) result.add(ev);

        return result;
    }

    public ArrayList<Event> eventsForDate(LocalDate date)
    {
        ArrayList<Event> events = new ArrayList<>();

        for(Event event : eventsList)
        {
            if(event.getDate().equals(date)) events.add(event);
        }
        return events;
    }

    public ArrayList<Event> eventsForDateAndTime(LocalDate date, LocalTime time)
    {
        ArrayList<Event> events = new ArrayList<>();

        for(Event event : eventsList)
        {
            if(event.getDate().equals(date) &&  event.getstarTime().compareTo(time) <= 0 && event.getendTime().compareTo(time) > 0)
            {
                events.add(event);
            }
        }
        return events;
    }

    public void addicstoEvents(String x){

        String[] input = x.split("BEGIN:VEVENT");

        System.out.println(input[1]);

//                String input = "BEGIN: VEVENT\n" +
//                        "SUMMARY: BWI4-RBP/01\n" +
//                        "LOCATION: Stand 15-03-2022\n" +
//                        "DESCRIPTION: HBN/ [Slz]\n" +
//                        "UID: 220315.106936@etech.haw-hamburg.de\n" +
//                        "DTSTART;TZID=Europe/Berlin: 20220512T081500\n" +
//                        "DTEND;TZID=Europe/Berlin: 20220512T113000\n" +
//                        "END : VEVENT\n";

        for(int i = 1; i< input.length; ++i) {

            String regex = "(?:(?<summary>SUMMARY:)(?<summaryValue>[^\\n]*))|(?:(?<description>DESCRIPTION:)(?<descriptionValue>[^\\n]*))|(?:(?<location>LOCATION:)(?<locationValue>[^\\n]*))|(?:(?<startTime>DTSTART;TZID=Europe/Berlin:\\s*)(?<startTimeValue>[^\\n]*))|(?:(?<endTime>DTEND;TZID=Europe/Berlin:\\s*)(?<endTimeValue>[^\\n]*))";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input[i]);

            String summary = null;
            String description = null;
            String location = null;
            LocalDateTime startTime = null;
            LocalDateTime endTime = null;

            while (matcher.find()) {

                if (matcher.group("summary") != null) {
                    summary = matcher.group("summaryValue");
                } else if (matcher.group("description") != null) {
                    description = matcher.group("descriptionValue");
                } else if (matcher.group("location") != null) {
                    location = matcher.group("locationValue");
                } else if (matcher.group("startTime") != null) {
                    String startTimeString = matcher.group("startTimeValue").substring(0,13);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm");
                    startTime = LocalDateTime.parse(startTimeString, formatter);
                } else if (matcher.group("endTime") != null) {
                    String endTimeString = matcher.group("endTimeValue").substring(0,13);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm");
                    endTime = LocalDateTime.parse(endTimeString, formatter);
                }
            }

            Event ev = new Event(summary, startTime.toLocalDate(), startTime.toLocalTime(), endTime.toLocalTime(), description + " || Ort: " + location);
            List e = new ArrayList<Event>();
            e.add(ev);
            InjectorManager.IM.gibDatenverwaltung().speichereEvents(e);
            eventsList.add(ev);
            Log.e("Eventslist Size:" , eventsList.size() + "");
        }
    }
}