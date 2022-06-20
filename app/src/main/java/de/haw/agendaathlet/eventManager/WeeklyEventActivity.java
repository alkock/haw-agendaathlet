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

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.haw.agendaathlet.InjectorManager;
import de.haw.agendaathlet.R;
import de.haw.agendaathlet.eventVisual.Event;

public class WeeklyEventActivity extends AppCompatActivity {
    private EditText editEventName, editWeekdays, editWeeks, editeventDescrption;
    private Button startTimeButton;
    private Button endTimeButton;
    private LocalTime startTime;
    private LocalTime endTime;
    private int startZeitMinute;
    private int startZeitStunde;
    private int endZeitMinute;
    private int endZeitStunde;

    public WeeklyEventActivity() {
        startZeitStunde = 0;
        startZeitMinute = 0;
        endZeitStunde = 0;
        endZeitMinute = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_weekly);
        initWidgets();
        startTime = LocalTime.now();
        endTime = LocalTime.now();

        editEventName.setText("Beste VL Ever");
        startTimeButton.setText("Startzeit");
        endTimeButton.setText("Endzeit");
        editWeekdays.setText("1");
        editWeeks.setText("13,14,15");
        editeventDescrption.setText("Tolle VL");
    }

    private void initWidgets() {
        editEventName = findViewById(R.id.eventNameET);
        startTimeButton = findViewById(R.id.editTextstartTime);
        endTimeButton = findViewById(R.id.editTextendTime);
        editWeekdays = findViewById(R.id.editTextweekday);
        editWeeks = findViewById(R.id.editTextweeks);
        editeventDescrption = findViewById(R.id.editeventDescription);
    }

    public void saveEventAction(View view) {

        String eventName = editEventName.getText().toString();
        String start = startTimeButton.getText().toString();
        String end = endTimeButton.getText().toString();
        String weekdays = editWeekdays.getText().toString();
        String weeks = editWeeks.getText().toString();
        String desc = editeventDescrption.getText().toString();

        if (start.equals("Startzeit")) start = "00:00";
        if (end.equals("Endzeit")) end = "00:00";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        startTime = LocalTime.parse(start, formatter);
        endTime = LocalTime.parse(end, formatter);

        try {

            String[] days = weekdays.split(",");
            String[] weeksArr = weeks.split(",");
            List<String> weeksList = new ArrayList<>();
              for (String week : weeksArr) {
                 weeksList.add(week);
                }
              List<String> toRemove = new ArrayList<String>();
              List<String> toAdd = new ArrayList<String>();
            for(String week : weeksList) {
                if(week.contains("-"))
                {
                    String[] weekRange = week.split("-");
                    int startWeek = Integer.parseInt(weekRange[0]); //52 //14
                    int endWeek = Integer.parseInt(weekRange[1]); // 2 //16
                    for(int i = startWeek; (endWeek < startWeek || i <= endWeek) && i <= 52; i = i+1) {
                        System.out.println(i);
                        toAdd.add(String.valueOf(i));
                    }
                    if(endWeek < startWeek)
                    {
                        for(int i = 1; i <= endWeek; i = i+1)
                        {
                            System.out.println(i);
                            toAdd.add(String.valueOf(i));
                        }
                    }
                toRemove.add(week);
                }
            }
            weeksList.removeAll(toRemove);
            weeksList.addAll(toAdd);


            for (String week : weeksList) {
                for (String day : days) {
                    if(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) > Integer.parseInt(week))
                    {
                        LocalDate date = LocalDate.of(Calendar.getInstance().get(Calendar.YEAR)+1, 1, 1).with(WeekFields.of(Locale.GERMANY).dayOfWeek(), Integer.parseInt(day)).plusWeeks(Integer.parseInt(week));
                        Event newEvent = new Event(eventName, date, startTime, endTime, desc);
                        InjectorManager.IM.gibEventLogic().getEventList().add(newEvent);
                        List e = new ArrayList();
                        e.add(newEvent);
                        System.out.println(newEvent.getDate());
                        InjectorManager.IM.gibDatenverwaltung().speichereEvents(e);
                    }
                    else
                    {
                        LocalDate date = LocalDate.of(Calendar.getInstance().get(Calendar.YEAR), 1, 1).with(WeekFields.of(Locale.GERMANY).dayOfWeek(), Integer.parseInt(day)).plusWeeks(Integer.parseInt(week));
                        Event newEvent = new Event(eventName, date, startTime, endTime, desc);
                        InjectorManager.IM.gibEventLogic().getEventList().add(newEvent);
                        List e = new ArrayList();
                        e.add(newEvent);
                        System.out.println(newEvent.getDate());
                        InjectorManager.IM.gibDatenverwaltung().speichereEvents(e);
                    }
                }
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Fehler bei Tage oder Wochenangabe", Toast.LENGTH_SHORT).show();
        }

    }

    public void popUpTimerStart(View view) {
        TimePickerDialog.OnTimeSetListener listner = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                startZeitStunde = i;
                startZeitMinute = i1;
                startTimeButton.setText(String.format(Locale.getDefault(), "%02d:%02d", startZeitStunde, startZeitMinute));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog((this), R.style.Datepicker1, listner, startZeitStunde, startZeitMinute, true);
        //timePickerDialog.setTitle("Zeit auswählen");
        timePickerDialog.show();
    }

    public void popUpTimerEnd(View view) {
        TimePickerDialog.OnTimeSetListener listner = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                endZeitStunde = i;
                endZeitMinute = i1;
                endTimeButton.setText(String.format(Locale.getDefault(), "%02d:%02d", endZeitStunde, endZeitMinute));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog((this), R.style.Datepicker1, listner, endZeitStunde, endZeitMinute, true);
        //timePickerDialog.setTitle("Zeit auswählen");
        timePickerDialog.show();
    }
}
