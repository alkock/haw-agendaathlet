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

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import de.haw.agendaathlet.InjectorManager;
import de.haw.agendaathlet.R;
import de.haw.agendaathlet.eventManager.EventLogic;
import de.haw.agendaathlet.eventVisual.Event;

public class ExportActivity extends AppCompatActivity {

    private final EventLogic eventLogic;
    private Button exportGoogle, exportICS;
    private String icsText;
    final Calendar myCalendar = Calendar.getInstance();
    private Button startDatum;
    private Button endDatum;
    private LocalDate startDatumDate;
    private LocalDate endDatumDate;
    private final int callbackId;

    public ExportActivity() {

        eventLogic = InjectorManager.IM.gibEventLogic();
        startDatumDate = LocalDate.MIN;
        endDatumDate = LocalDate.MAX;
        callbackId = 42;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_export);
        initWidgets();
        exportICS.setText("Exportiere als Datei");
        exportGoogle.setText("Exportiere zu Google Calander");


        startDatum = (Button) findViewById(R.id.editExportStartDate);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                //Keine Ahnung, warum da Plus eins hin muss
                startDatumDate = LocalDate.of(year, month + 1, day);
                updateLabelStart();
            }
        };
        startDatum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ExportActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endDatum = (Button) findViewById(R.id.editExportEndDate);
        DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                //Keine Ahnung, warum da Plus eins hin muss
                endDatumDate = LocalDate.of(year, month + 1, day);
                updateLabelEnd();

            }
        };
        endDatum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ExportActivity.this, date2, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabelStart() {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        startDatum.setText(dateFormat.format(myCalendar.getTime()));

    }

    private void updateLabelEnd() {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        endDatum.setText(dateFormat.format(myCalendar.getTime()));

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 2 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            OutputStream outputStream;
            try {
                outputStream = getContentResolver().openOutputStream(uri);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
                bw.write(icsText);
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }

    }


    private void initWidgets() {
        exportGoogle = findViewById(R.id.ExportIcsButton);
        exportICS = findViewById(R.id.ExportGoogleButton);
    }

    public void icsErstellenNeu(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ExportActivity.this);
        builder.setCancelable(false);


        if (startDatumDate.isBefore(LocalDate.of(1950, 1, 1)) ||
                endDatumDate.isAfter(LocalDate.of(2300, 1, 1))) {
            builder.setTitle("Sind sie sicher? ");
            builder.setMessage("ES WERDEN ALLE ELEMENTE AUS DEM GESAMTEN KALENDER EXPORTIERT!!" +
                    "Wenn sie das nicht wollen, wählen sie oben das Zeitfenster aus, aus dem Termine exportiert werden sollen.");
        } else {
            builder.setTitle("Sind sie sicher? ");
            builder.setMessage("Es werden alle Elemente vom " + startDatumDate.toString() + " bis zum " + endDatumDate.toString() + " exportiert");
        }

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.setPositiveButton("Export", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                icsSchicken(eventZeitraum(eventLogic.getEventList()));
            }
        });
        builder.show();


    }

    public void icsSchicken(List<Event> eventList) {

        ICSBuilder icsBuilder = new ICSBuilder();

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/calendar");
        intent.putExtra(Intent.EXTRA_TITLE, "Agendaathlet Termine");
        icsText = icsBuilder.buildGesammtICS(eventList);
        startActivityForResult(intent, 2);

    }

    public void googleCalenderHinzufuegenNeu(View view) {

        addEventsToGoogle(eventZeitraum(eventLogic.getEventList()));
    }


    public void addEventsToGoogle(List<Event> eventList) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ExportActivity.this);
        builder.setCancelable(false);


        if (startDatumDate.isBefore(LocalDate.of(1950, 1, 1)) ||
                endDatumDate.isAfter(LocalDate.of(2300, 1, 1))) {
            builder.setTitle("Sind sie sicher? ");
            builder.setMessage("ES WERDEN ALLE ELEMENTE AUS DEM GESAMTEN KALENDER EXPORTIERT!!" +
                    "Wenn sie das nicht wollen, wählen sie oben das Zeitfenster aus, aus dem Termine exportiert werden sollen.");
        } else {
            builder.setTitle("Sind sie sicher? ");
            builder.setMessage("Es werden alle Elemente vom " + startDatumDate.toString() + " bis zum " + endDatumDate.toString() + " exportiert");
        }

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.setPositiveButton("Export", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (Event event : eventList) {
//            addEventToGoogle2(event);
                    addEventToGoogle(event);
                }
            }
        });
        builder.show();
    }

//    public void addEventToGoogle2(Event event) {
//
//        OffsetDateTime odt = OffsetDateTime.now(ZoneId.systemDefault());
//        ZoneOffset zoneOffset = odt.getOffset();
//
//        LocalDateTime dateStartTime = event.getstarTime().atDate(event.getDate());
//        GregorianCalendar gcStart = GregorianCalendar.from(ZonedDateTime.of(dateStartTime, ZoneId.systemDefault()));
//        long startZeit = gcStart.getTimeInMillis();
//
//        LocalDateTime dateEndTime = event.getendTime().atDate(event.getDate());
//        GregorianCalendar gcEnd = GregorianCalendar.from(ZonedDateTime.of(dateEndTime, ZoneId.systemDefault()));
//        long endZeit = gcEnd.getTimeInMillis();
//
//
//        Intent intent = new Intent(Intent.ACTION_INSERT);
//
//        intent.setType("vnd.android.cursor.item/event");
//        intent.setData(CalendarContract.Events.CONTENT_URI);
//        intent.putExtra(CalendarContract.Events.TITLE, event.getName() + "");
//        intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription() + "");
//
//
//        GregorianCalendar calDate = new GregorianCalendar(2022, 4, 20, 8, 20);
//        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
//                startZeit);
//        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
//                endZeit);
//
//
//        intent.putExtra(CalendarContract.Events.LAST_SYNCED, LocalDate.now().toString());
//
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        } else {
//            Toast.makeText(this, "There is no compatible Calendar App available", Toast.LENGTH_SHORT).show();
//        }
//
//    }


    private void addEventToGoogle(Event event) {

        checkPermission(callbackId, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);

        LocalDateTime dateStartTime = event.getstarTime().atDate(event.getDate());
        GregorianCalendar gcStart = GregorianCalendar.from(ZonedDateTime.of(dateStartTime, ZoneId.systemDefault()));
        long startZeit = gcStart.getTimeInMillis();

        LocalDateTime dateEndTime = event.getendTime().atDate(event.getDate());
        GregorianCalendar gcEnd = GregorianCalendar.from(ZonedDateTime.of(dateEndTime, ZoneId.systemDefault()));
        long endZeit = gcEnd.getTimeInMillis();

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startZeit);
        values.put(CalendarContract.Events.DTEND, endZeit);
        values.put(CalendarContract.Events.TITLE, event.getName());
        values.put(CalendarContract.Events.DESCRIPTION, event.getDescription());

        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Berlin");
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        Log.d("AddToGoogleCalendar", event.getName());
        Log.d("AddToGoogleCalendar", "hoffentlich erfolgreich");

    }


    private void checkPermission(int callbackId, String... permissionsId) {
        boolean permissions = true;
        for (String p : permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(this, p) == PERMISSION_GRANTED;
        }

        if (!permissions)
            ActivityCompat.requestPermissions(this, permissionsId, callbackId);
    }


    private List<Event> eventZeitraum(List<Event> eventListe) {
        try {
            List<Event> ausgabeListe = new LinkedList<Event>();
            for (int i = 0; i < eventListe.size(); ++i) {
                Event temp = eventListe.get(i);
                if (temp.getDate().isAfter(startDatumDate) && temp.getDate().isBefore(endDatumDate)
                        || temp.getDate().isEqual(startDatumDate) || temp.getDate().isEqual(endDatumDate)) {
                    ausgabeListe.add(temp);
//                Log.d("ExportTermin", temp.getName());
//                Log.d("ExportTermin", Integer.toString(temp.getDate().getYear()));
//                Log.d("ExportTermin", Integer.toString(temp.getDate().getMonthValue()));
//                Log.d("ExportTermin", Integer.toString(temp.getDate().getDayOfMonth()));
                }
            }
//        Log.d("Export", startDatumDate.toString());
//        Log.d("Export", endDatumDate.toString());
//        Log.d("Export", ausgabeListe.size() + "");

            return ausgabeListe;
        } catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(),
                            "Vermutlich ist kein Zeitraum ausgewählt",
                            Toast.LENGTH_LONG)
                    .show();
            return new LinkedList<Event>();
        }
    }

}
