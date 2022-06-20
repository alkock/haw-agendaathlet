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
package de.haw.agendaathlet;

import static de.haw.agendaathlet.eventManager.CalendarUtils.selectedDate;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.haw.agendaathlet.crawler.Essensplan;
import de.haw.agendaathlet.crawler.FeiertageCrawler;
import de.haw.agendaathlet.datamanagement.Datenverwaltung;
import de.haw.agendaathlet.datamanagement.DatenverwaltungImpl;
import de.haw.agendaathlet.essen.EssenActivity;
import de.haw.agendaathlet.eventManager.CalendarUtils;
import de.haw.agendaathlet.eventManager.DashboardActivity;
import de.haw.agendaathlet.eventManager.EventLogic;
import de.haw.agendaathlet.eventVisual.Event;
import de.haw.agendaathlet.eventVisual.EventAnzeigeAdapter;
import de.haw.agendaathlet.eventVisual.EventSearchActivity;
import de.haw.agendaathlet.eventVisual.EventZeitComparator;
import de.haw.agendaathlet.eventVisual.OnSwipeTouchListener;
import de.haw.agendaathlet.impressum.ImpressumActivity;

public class MainActivity extends AppCompatActivity {
    private TextView monthDayText;
    private TextView dayOfWeekTV;
    private ListView eventAnzeigeListView;
    private ImageView essenView;
    public static Datenverwaltung datenverwaltung;
    private EventLogic eventLogic;
    private Essensplan essensplan;
    private boolean essenGeladen;
    private BottomNavigationView bottom;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InjectorManager j = new InjectorManager();
        InjectorManager.IM.setMainActivity(this);
        InjectorManager.IM.setDatenverwaltung(new DatenverwaltungImpl(this));
        datenverwaltung = j.gibDatenverwaltung();
        eventLogic = j.gibEventLogic();
        essensplan = InjectorManager.IM.gibEssensplan();
        CalendarUtils.selectedDate = LocalDate.now();
        setContentView(R.layout.activity_main);
        initWidgets();
        essenGeladen = false;
        //essenView.setColorFilter(getResources().getColor(R.color.lightGray));
        prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        ladeEssen();
        erstelleWecker();
        ladeFeiertage();
        TextView dp = findViewById(R.id.monthDayText);


        dp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DatePickerDialog.OnDateSetListener listner = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        LocalDate date = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                        selectedDate = date;
                        setDayView();
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, R.style.Datepicker1, listner, selectedDate.getYear(), selectedDate.getMonthValue() - 1, selectedDate.getDayOfMonth());
                //datePickerDialog.setTitle("Datum auswählen");
                datePickerDialog.show();
                return true;
            }

        });


    }

    private void ladeFeiertage() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        FeiertageCrawler fc = InjectorManager.IM.gibFeiertageCrawler();
                        Datenverwaltung db = InjectorManager.IM.gibDatenverwaltung();
                        boolean vorhanden = false;
                        List<Event> events = db.ladeEvents();
                        for (Event event : events) {
                            if (event.getName().equals("Pfingstmontag") && event.getDate().getYear() == LocalDateTime.now().getYear())
                                vorhanden = true;

                        }
                        if (!vorhanden) {
                            fc.gibFeiertage(LocalDateTime.now().getYear());

                        }
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void erstelleWecker() {
        bottom = (BottomNavigationView) findViewById(R.id.navi);
        bottom.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.nav_toodoo) {
                    startActivity(new Intent(MainActivity.this, TodoActivity.class));
                }
                if (item.getItemId() == R.id.nav_essen) {
                    startActivity(new Intent(MainActivity.this, EssenActivity.class));
                }
                if (item.getItemId() == R.id.nav_alarm) {


                    final LocalTime[] t = {null};
                    try {
                        LocalTime first = eventLogic.eventsForDate(selectedDate).get(0).getstarTime();
                        for (Event e : eventLogic.eventsForDate(selectedDate))
                            if (e.getstarTime().compareTo(first) < 0) first = e.getstarTime();
                        t[0] = first;
                    } catch (Exception e) {
                    }


                    if (t[0] != null) {

                        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
                        t[0] = t[0].minusMinutes(Long.parseLong(prefs.getString("wecker", "90")));
                        TimePickerDialog.OnTimeSetListener listner = new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int startZeitStunde, int startZeitMinute) {
                                t[0] = LocalTime.of(startZeitStunde, startZeitMinute);
                                System.out.println(t[0]);
                                Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                                intent.putExtra(AlarmClock.EXTRA_HOUR, t[0].getHour());
                                intent.putExtra(AlarmClock.EXTRA_MINUTES, t[0].getMinute());
                                intent.putExtra(AlarmClock.EXTRA_ALARM_SNOOZE_DURATION, 5);
                                intent.putExtra(AlarmClock.EXTRA_MESSAGE, "HAW Agenda Athlet");
                                intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                                startActivity(intent);
                                //Toast.makeText(MainActivity.this, "Wecker gestellt auf: " + t[0].getHour() + ":" + t[0].getMinute() + " Uhr", Toast.LENGTH_LONG).show();
                            }
                        };

                        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, R.style.MyDialogTheme, listner, t[0].getHour(), t[0].getMinute(), true);
                        timePickerDialog.show();

                    } else {
                        Toast.makeText(MainActivity.this, "Kein Wecker notwendig ;-) Ausschlafen \uD83E\uDD73", Toast.LENGTH_LONG).show();
                    }
                }
                if (item.getItemId() == R.id.nav_plus) {
                    startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                }
                return true;
            }
        });
    }

    private void ladeEssen() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        essensplan.ladeEssen("https://www.stwhh.de/speiseplan?t=this_week", Integer.parseInt(prefs.getString("essen", "0")));
                        //essenView.setColorFilter(getResources().getColor(R.color.blue));
                        essenGeladen = true;
                    } catch (Exception e) {
                        System.out.println("Fehler beim Laden des Essensplans");
                    }
                }
            }).start();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Essensplan laden lief so semi gut", Toast.LENGTH_LONG).show();
        }

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        essensplan.ladeEssen("https://www.stwhh.de/speiseplan?t=next_week", Integer.parseInt(prefs.getString("essen", "0")));
                        //essenView.setColorFilter(getResources().getColor(R.color.blue));
                        essenGeladen = true;
                    } catch (Exception e) {
                        System.out.println("Fehler beim Laden des Essensplans");
                    }
                }
            }).start();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Essensplan laden lief so semi gut", Toast.LENGTH_LONG).show();
        }

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InjectorManager.IM.gibICSCrawler().getAll(prefs.getString("url", "keine"));
                    } catch (Exception e) {
                        System.out.println("Fehler beim Laden der Kurse");
                    }
                }
            }).start();
        } catch (Exception e) {
        }
    }

    private void initWidgets() {
        monthDayText = findViewById(R.id.monthDayText);
        dayOfWeekTV = findViewById(R.id.dayOfWeekTV);
        eventAnzeigeListView = findViewById(R.id.EventsListe);
        essenView = findViewById(R.id.imageview1);
        eventAnzeigeListView = findViewById(R.id.EventsListe);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDayView();
    }

    public void setDayView() {
        monthDayText.setText(CalendarUtils.monthDayFromDate(selectedDate));
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        dayOfWeekTV.setText(dayOfWeek);

        ArrayList<Event> gesamtListe = eventLogic.getEventList();
        ArrayList<Event> tagesListe = new ArrayList<>();

        for (Event e : gesamtListe)
            if (e.getDate().equals(CalendarUtils.selectedDate)) tagesListe.add(e);

        tagesListe.sort(new EventZeitComparator());
        EventAnzeigeAdapter eventAnzeigeAdapter = new EventAnzeigeAdapter(getApplicationContext(), tagesListe);
        eventAnzeigeListView.setAdapter(eventAnzeigeAdapter);

        eventAnzeigeListView.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeRight() {
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusDays(1);
                setDayView();
            }

            public void onSwipeLeft() {
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusDays(1);
                setDayView();
            }
        });
    }

    public void essen(View view) {
        startActivity(new Intent(this, EssenActivity.class));
    }

    public void impressum(View view) {
        startActivity(new Intent(this, ImpressumActivity.class));
    }

    public void eventSuchen(View view) {
        startActivity(new Intent(this, EventSearchActivity.class));
    }

    public void settings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void previousDayAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusDays(1);
        setDayView();
    }

    public void today(View view) {
        CalendarUtils.selectedDate = LocalDate.now();
        setDayView();
    }

    public void nextDayAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusDays(1);
        setDayView();
    }
}








