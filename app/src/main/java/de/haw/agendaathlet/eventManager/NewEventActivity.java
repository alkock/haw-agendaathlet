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

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.haw.agendaathlet.InjectorManager;
import de.haw.agendaathlet.R;
import de.haw.agendaathlet.eventVisual.Event;

public class NewEventActivity extends AppCompatActivity {
    private EditText eventNameET, editeventDescrption;
    private TextView eventDateTV, eventstartTimeTV, eventendTimeTV;
    private LocalTime startTime;
    private static final int PICKFILE_RESULT_CODE = 1;
    Button timeButtonStart;
    Button editEventDate;
    Button timeButtonEnd;
    private final EventLogic eventLogic;
    LocalDate eventDate;
    int startZeitStunde, startZeitMinute;
    int endZeitStunde, endZeitMinute;
    Button button30minutes;
    Button button45minutes;
    Button button60minutes;
    Button button90minutes;
    Button button120minutes;
    Button button180minutes;
    Button button195minutes;

    public NewEventActivity() {
        eventLogic = InjectorManager.IM.gibEventLogic();
        startZeitStunde = 0;
        startZeitMinute = 0;
        endZeitStunde = 0;
        endZeitMinute = 0;
        eventDate = CalendarUtils.selectedDate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        initWidgets();
        startTime = LocalTime.now();
        fillFieldsWithText();
    }

    private void initWidgets() {
        eventNameET = findViewById(R.id.eventNameET);
        eventDateTV = findViewById(R.id.eventDateTV);
        eventstartTimeTV = findViewById(R.id.eventstartTimeTV);
        eventendTimeTV = findViewById(R.id.eventendTimeTV);
        editEventDate = findViewById(R.id.editEventDate);
        timeButtonStart = findViewById(R.id.editTextstartTime);
        timeButtonEnd = findViewById(R.id.editTextendTime);
        editeventDescrption = findViewById(R.id.editeventDescription);
        button30minutes = findViewById(R.id.button30minutes);
        button45minutes = findViewById(R.id.button45minutes);
        button60minutes = findViewById(R.id.button60minutes);
        button90minutes = findViewById(R.id.button90minutes);
        button120minutes = findViewById(R.id.button120minutes);
        button180minutes = findViewById(R.id.button180minutes);
        button195minutes = findViewById(R.id.button195minutes);
    }

    private void fillFieldsWithText() {
        editEventDate.setText(CalendarUtils.DateToString(eventDate));
        eventDateTV.setText("Datum: ");
        eventstartTimeTV.setText("Startzeit: ");
        eventendTimeTV.setText("Endzeit: ");
        eventNameET.setText("Beste VL Ever");
        timeButtonStart.setText("Zeit auswählen");
        timeButtonEnd.setText("Zeit auswählen");
        editeventDescrption.setText("Diese Vorlesung ist wirklich beeindruckend.");
    }

    public void saveEventAction(View view) {
        String eventName = eventNameET.getText().toString();
        String start = timeButtonStart.getText().toString();
        String end = timeButtonEnd.getText().toString();
        String desc = editeventDescrption.getText().toString();

        if (start.equals("Zeit auswählen")) start = "00:00";
        if (end.equals("Zeit auswählen")) end = "00:00";

        Event newEvent = new Event(eventName, eventDate, LocalTime.parse(start, DateTimeFormatter.ofPattern("HH:mm")), LocalTime.parse(end, DateTimeFormatter.ofPattern("HH:mm")), desc);
        eventLogic.getEventList().add(newEvent);
        List list = new ArrayList<Event>();
        list.add(newEvent);
        InjectorManager.IM.gibDatenverwaltung().speichereEvents(list);
        finish();
    }

    public void ics(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/calendar");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICKFILE_RESULT_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line).append('\n');
                }

                String x = total.toString();
                eventLogic.addicstoEvents(x);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void suchen(View view) {

        startActivity(new Intent(this, ModuleSearchActivity.class));
        finish();
    }

    public void woechentlich(View view) {
        startActivity(new Intent(this, WeeklyEventActivity.class));
        finish();
    }

    public void popTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener listner = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int startZeitStunde, int startZeitMinute) {
                NewEventActivity.this.startZeitStunde = startZeitStunde;
                NewEventActivity.this.startZeitMinute = startZeitMinute;
                timeButtonStart.setText(String.format(Locale.getDefault(), "%02d:%02d", NewEventActivity.this.startZeitStunde, NewEventActivity.this.startZeitMinute));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog((this), listner, startZeitStunde, startZeitMinute, true);
        timePickerDialog.setTitle("Zeit auswählen");
        timePickerDialog.show();
    }

    public void onClick(View view) {
        startTime = LocalTime.of(startZeitStunde, startZeitMinute);
        switch (view.getId()) {

            case R.id.button30minutes:
                LocalTime b30 = startTime.plusMinutes(30);
                endZeitStunde = b30.getHour();
                endZeitMinute = b30.getMinute();
                timeButtonEnd.setText(String.format(Locale.getDefault(), "%02d:%02d", endZeitStunde, endZeitMinute));
                break;
            case R.id.button45minutes:
                LocalTime b45 = startTime.plusMinutes(45);
                endZeitStunde = b45.getHour();
                endZeitMinute = b45.getMinute();
                timeButtonEnd.setText(String.format(Locale.getDefault(), "%02d:%02d", endZeitStunde, endZeitMinute));
                break;
            case R.id.button60minutes:
                LocalTime b60 = startTime.plusMinutes(60);
                endZeitStunde = b60.getHour();
                endZeitMinute = b60.getMinute();
                timeButtonEnd.setText(String.format(Locale.getDefault(), "%02d:%02d", endZeitStunde, endZeitMinute));
                break;
            case R.id.button90minutes:
                LocalTime b90 = startTime.plusMinutes(90);
                endZeitStunde = b90.getHour();
                endZeitMinute = b90.getMinute();
                timeButtonEnd.setText(String.format(Locale.getDefault(), "%02d:%02d", endZeitStunde, endZeitMinute));
                break;
            case R.id.button120minutes:
                LocalTime b120 = startTime.plusMinutes(120);
                endZeitStunde = b120.getHour();
                endZeitMinute = b120.getMinute();
                timeButtonEnd.setText(String.format(Locale.getDefault(), "%02d:%02d", endZeitStunde, endZeitMinute));
                break;
            case R.id.button180minutes:
                LocalTime b180 = startTime.plusMinutes(180);
                endZeitStunde = b180.getHour();
                endZeitMinute = b180.getMinute();
                timeButtonEnd.setText(String.format(Locale.getDefault(), "%02d:%02d", endZeitStunde, endZeitMinute));
                break;
            case R.id.button195minutes:
                LocalTime b195 = startTime.plusMinutes(195);
                endZeitStunde = b195.getHour();
                endZeitMinute = b195.getMinute();
                timeButtonEnd.setText(String.format(Locale.getDefault(), "%02d:%02d", endZeitStunde, endZeitMinute));
                break;

        }
    }

    public void popTimePickerEnd(View view) {
        TimePickerDialog.OnTimeSetListener listner = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int endZeitStunde, int endZeitMinute) {
                NewEventActivity.this.endZeitStunde = endZeitStunde;
                NewEventActivity.this.endZeitMinute = endZeitMinute;
                timeButtonEnd.setText(String.format(Locale.getDefault(), "%02d:%02d", NewEventActivity.this.endZeitStunde, NewEventActivity.this.endZeitMinute));
            }
        };

        TimePickerDialog timePickerDialog2 = new TimePickerDialog((this), listner, endZeitStunde, endZeitMinute, true);
        timePickerDialog2.setTitle("Zeit auswählen");
        timePickerDialog2.show();
    }

    public void popDatePicker(View view) {
        DatePickerDialog.OnDateSetListener listner = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                LocalDate date = LocalDate.of(year, monthOfYear, dayOfMonth);
                eventDate = date;
                editEventDate.setText(CalendarUtils.DateToString(eventDate));
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog((this), listner, eventDate.getYear(), eventDate.getMonthValue(), eventDate.getDayOfMonth());
        datePickerDialog.setTitle("Datum auswählen");
        datePickerDialog.show();
    }
}
