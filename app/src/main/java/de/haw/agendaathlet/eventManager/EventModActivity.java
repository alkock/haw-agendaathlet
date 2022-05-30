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

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.haw.agendaathlet.InjectorManager;
import de.haw.agendaathlet.R;
import de.haw.agendaathlet.datamanagement.Datenverwaltung;
import de.haw.agendaathlet.eventVisual.Event;

public class EventModActivity extends AppCompatActivity
{
    private EditText eventName, eventDescription;
    private TextView eventDateTV;
    private Button timeButtonStart;
    private Button timeButtonEnd;
    private Button modEventDateDate;
    private int einfach;
    private boolean multi;
    private final EventLogic event;
    private final Datenverwaltung dv;
    LocalDate eventDate;
    private int startZeitStunde;
    private int startZeitMinute;
    private int endZeitStunde;
    private int endZeitMinute;

    public EventModActivity()
    {
        event = InjectorManager.IM.gibEventLogic();
        dv = InjectorManager.IM.gibDatenverwaltung();
        startZeitMinute = 0;
        startZeitStunde = 0;
        endZeitMinute = 0;
        endZeitStunde = 0;
        eventDate = null;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_modify);
        eventName = (EditText) findViewById(R.id.modeventName);
        timeButtonStart = findViewById(R.id.modTextstartTime);
        timeButtonEnd =  findViewById(R.id.modTextendTime);
        eventDescription = (EditText) findViewById(R.id.editmodDescription);
        modEventDateDate = findViewById(R.id.modEventDateDate);
        eventDateTV = (TextView) findViewById(R.id.modeventDateTV);


        Bundle bundle = getIntent().getExtras();
            einfach = (int) bundle.get("event");
            multi = (boolean) bundle.get("multi");
            Event ev = event.getEventList().get(einfach);
            eventDate = ev.getDate();
            eventName.setText(ev.getName());
            timeButtonStart.setText(CalendarUtils.TimeToString(ev.getstarTime()));
            timeButtonEnd.setText(CalendarUtils.TimeToString(ev.getendTime()));
            eventDescription.setText(ev.getDescription());
            eventDateTV.setText("Datum:");
            if(!multi) modEventDateDate.setText(CalendarUtils.DateToString(ev.getDate()));
            else modEventDateDate.setText("Datum: Mehrere Daten ausgewählt");
    }

    public void close(View view) {
        finish();
    }

    public void save(View view) {

        List<Event> zuAenderndeEvents = new ArrayList<>();

        if(!multi) zuAenderndeEvents.add(event.getEventList().get(einfach));
        else {
            for(int j = 0; j < event.getEventList().size(); j++) {
                if(event.getEventList().get(j).getName().equals(event.getEventList().get(einfach).getName())) zuAenderndeEvents.add(event.getEventList().get(j));
            }
        }

        for(int j = 0; j < zuAenderndeEvents.size(); j++) {
            zuAenderndeEvents.get(j).setName(eventName.getText().toString());
            zuAenderndeEvents.get(j).setDate(eventDate);
            zuAenderndeEvents.get(j).setstartTime(CalendarUtils.TimeFromString2(timeButtonStart.getText().toString()));
            zuAenderndeEvents.get(j).setendTime(CalendarUtils.TimeFromString2(timeButtonEnd.getText().toString()));
            zuAenderndeEvents.get(j).setDescription(eventDescription.getText().toString());
            dv.updateEvent(zuAenderndeEvents.get(j));
        }

        finish();
    }

    public void popUpListnerStart(View view) {
        TimePickerDialog.OnTimeSetListener listner = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                startZeitStunde = i;
                startZeitMinute = i1;
                timeButtonStart.setText(String.format(Locale.getDefault(), "%02d:%02d", startZeitStunde, startZeitMinute));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog((this), listner, startZeitStunde, startZeitMinute, true);
        timePickerDialog.setTitle("Zeit auswählen");
        timePickerDialog.show();
    }

    public void popUpListnerEnd(View view) {
        TimePickerDialog.OnTimeSetListener listner = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                endZeitStunde = i;
                endZeitMinute = i1;
                timeButtonEnd.setText(String.format(Locale.getDefault(), "%02d:%02d", endZeitStunde, endZeitMinute));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog((this), listner, endZeitStunde, endZeitMinute, true);
        timePickerDialog.setTitle("Zeit auswählen");
        timePickerDialog.show();
    }

    public void popUpListnerDate(View view) {
        DatePickerDialog.OnDateSetListener listner = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                LocalDate date = LocalDate.of(year, monthOfYear, dayOfMonth);
                eventDate = date;
                modEventDateDate.setText(CalendarUtils.DateToString(eventDate));
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog((this), listner, eventDate.getYear(), eventDate.getMonthValue(), eventDate.getDayOfMonth());
        datePickerDialog.setTitle("Datum auswählen");
        datePickerDialog.show();
    }
}
