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
package de.haw.agendaathlet.eventManager.ICSExport;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import de.haw.agendaathlet.eventVisual.Event;
//import com.google.api.services.calendar.Calendar;

/*
Diese Klasse
 */
public class ICSBuilder {

    //diese Klasse bietet eine statische Methode an, welche aus einer Liste an Events einen String
    //einer ICS Datei baut, welcher dann in Kalander Apps importiert werden kann.
    //https://icalendar.org/validator.html#results zum testen und validieren


    public ICSBuilder() {
    }

    //getestet mit Google calander und hat funktioniert
    public String buildGesammtICS(List<Event> eventList) {
        String ausgabe = "BEGIN:VCALENDAR\n";
        ausgabe += "VERSION:2.0\n";
        ausgabe += "PRODID:AgendaAthlet\n";

        for (Event e : eventList) {
            ausgabe += einzelEventICS(e);
        }

        ausgabe += "END:VCALENDAR\n";
//        Log.i("ICS", ausgabe);

        return ausgabe;
    }

    private String einzelEventICS(Event event) {
        String ausgabeEvent = "BEGIN:VEVENT\n";

        ausgabeEvent += "UID:" + event.getName() + "\n";
        ausgabeEvent += "DTSTAMP:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")) + "\n";


        ausgabeEvent += "DTSTART;TZID=Europe/Berlin:" + event.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "T" + event.getstarTime().format(DateTimeFormatter.ofPattern("HHmmss")) + "\n";
        ausgabeEvent += "DTEND;TZID=Europe/Berlin:" + event.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "T" + event.getendTime().format(DateTimeFormatter.ofPattern("HHmmss")) + "\n";
//        //Potenziell problematisch
//        ausgabeEvent += "DTSTAMP:" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "T" + event.getendTime() + "\n";
        ausgabeEvent += "SUMMARY:" + event.getName() + "\n";
//        Log.d("ICSBuilder", event.getDescription().replaceAll("||", " "));
        ausgabeEvent += "DESCRIPTION:" + descriptionAnpassen(event.getDescription()) + "\n";
        //Location auf "", damit beim wieder Einfügen kein null Wert entsteht
        ausgabeEvent += "LOCATION:" + "" + "\n";
//        Log.d("ICSBuilder", event.getDescription());
        ausgabeEvent += "END:VEVENT" + "\n";
//        Log.d("ICSBuilder", ausgabeEvent);
        return ausgabeEvent;
    }

    //
    private String descriptionAnpassen(String s)
    {
//        return " Dies ist ein Test || was alles: nicht g-eht 23423";
        String ausgabe = s.replaceAll("[^A-Za-z0-9 |:-]","");
        return ausgabe;
//        return s.replaceAll(" ", "");
    }
    public Uri icsDateiErstellen(List<Event> eventList, Context context)  {
        String icsEingabe = buildGesammtICS(eventList);
        File path = context.getFilesDir();
        try {
            FileOutputStream writer = new FileOutputStream(new File(path, "agendaathlet.ics"));
            writer.write(icsEingabe.getBytes());
            writer.close();
            Toast.makeText(context, "Wrote to file", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
//        String icsEingabe = buildGesammtICS(eventList);
//        File file = new File(Environment.getExternalStorageDirectory() + "/" + File.separator + "test.txt");
//
//
//        try {
//            if(file.createNewFile())
//            {
//                Log.d("ICS", "Datei wurde erstellt");
//            }
//            else
//            {
//                Log.d("ICS", "Datei war bereits vorhanden");
//            }
//        } catch (IOException e) {
//            Log.d("ICS", "Fehler bei der Dateierstellung");
//            e.printStackTrace();
//        }
//
//
//        //in die Datei schreiben
//        if (file.exists()) {
//            try {
//                OutputStream fo = new FileOutputStream(file);
//                fo.write(icsEingabe.getBytes());
//
//                fo.flush();
//                fo.close();
//                Log.d("ICS", "Datei erstellt");
//            } catch (IOException i) {
//                Log.d("ICS", "IO Exception");
//            }
//
//
//        }
//
//        Uri myUri = Uri.parse(file.getAbsolutePath());
//
//
//        Log.d("ICS", myUri.toString());
//        return myUri;
//    }

//        catch (Exception e)
//        {
//
//            Log.d("ICS","fehler ICS Datei erstellung");
//            Log.d("ICS", e.getClass().toString());
//            return null;
//        }

////deleting the file
//        file.delete();
//        System.out.println("file deleted");

    }
}

