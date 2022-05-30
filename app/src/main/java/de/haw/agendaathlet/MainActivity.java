
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;
import static de.haw.agendaathlet.eventManager.CalendarUtils.selectedDate;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import de.haw.agendaathlet.crawler.Essensplan;
import de.haw.agendaathlet.datamanagement.Datenverwaltung;
import de.haw.agendaathlet.datamanagement.DatenverwaltungImpl;
import de.haw.agendaathlet.essen.EssenActivity;
import de.haw.agendaathlet.eventManager.CalendarUtils;
import de.haw.agendaathlet.eventManager.DashboardActivity;
import de.haw.agendaathlet.eventManager.EventLogic;
import de.haw.agendaathlet.eventManager.NewEventActivity;
import de.haw.agendaathlet.eventVisual.Event;
import de.haw.agendaathlet.eventVisual.EventAnzeigeAdapter;
import de.haw.agendaathlet.eventVisual.EventSearchActivity;
import de.haw.agendaathlet.eventVisual.EventZeitComparator;
import de.haw.agendaathlet.eventVisual.OnSwipeTouchListener;
import de.haw.agendaathlet.impressum.ImpressumActivity;

public class MainActivity extends AppCompatActivity
{
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
    protected void onCreate(Bundle savedInstanceState)
    {
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
    }

    private void erstelleWecker() {
        bottom = (BottomNavigationView) findViewById(R.id.navi);
        bottom.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override



            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.nav_toodoo){
                    startActivity(new Intent(MainActivity.this, TodoActivity.class));
                }
                if (item.getItemId() == R.id.nav_essen){
                    startActivity(new Intent(MainActivity.this, EssenActivity.class));
                }
                if (item.getItemId() == R.id.nav_alarm){
                    LocalTime t = null;
                    try {
                        LocalTime first = eventLogic.eventsForDate(selectedDate).get(0).getstarTime();
                        for (Event e : eventLogic.eventsForDate(selectedDate)) if (e.getstarTime().compareTo(first) < 0) first = e.getstarTime();
                        t = first;
                    } catch (Exception e) { }

                    if (t != null) {
                        for (int i = 5; i > 0; i--) {
                            Intent intent2 = new Intent(AlarmClock.ACTION_DISMISS_ALARM);
                            intent2.putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_LABEL);
                            intent2.putExtra(AlarmClock.EXTRA_MESSAGE, "HAW Agenda Athlet");
                            startActivity(intent2);
                        }
                        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
                        t = t.minusMinutes(Long.parseLong(prefs.getString("wecker", "90")));
                        System.out.println(t);
                        int day = selectedDate.getDayOfMonth();
                        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                        intent.putExtra(AlarmClock.EXTRA_HOUR, t.getHour());
                        intent.putExtra(AlarmClock.EXTRA_MINUTES, t.getMinute());
                        intent.putExtra(AlarmClock.EXTRA_ALARM_SNOOZE_DURATION, 5);
                        intent.putExtra(AlarmClock.EXTRA_MESSAGE, "HAW Agenda Athlet");
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Kein Wecker notwendig ;-) Ausschlafen \uD83E\uDD73", Toast.LENGTH_LONG).show();
                    }
                }
                if(item.getItemId() == R.id.nav_plus) {
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
                        e.printStackTrace();
                    }
                }}).start();
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
                        e.printStackTrace();
                    }
                }}).start();
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
                        e.printStackTrace();
                    }
                }}).start();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Essensplan laden lief so semi gut", Toast.LENGTH_LONG).show();
        }
    }

    private void initWidgets()
    {
        monthDayText = findViewById(R.id.monthDayText);
        dayOfWeekTV = findViewById(R.id.dayOfWeekTV);
        eventAnzeigeListView = findViewById(R.id.EventsListe);
        essenView = findViewById(R.id.imageview1);
        eventAnzeigeListView = findViewById(R.id.EventsListe);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setDayView();
    }

    private void setDayView()
    {
        monthDayText.setText(CalendarUtils.monthDayFromDate(selectedDate));
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        dayOfWeekTV.setText(dayOfWeek);

        ArrayList<Event> gesamtListe = eventLogic.getEventList();
        ArrayList<Event> tagesListe = new ArrayList<>();

        for(Event e: gesamtListe) if(e.getDate().equals(CalendarUtils.selectedDate)) tagesListe.add(e);

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

    public void previousDayAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusDays(1);
        setDayView();
    }

    public void today(View view) {
        CalendarUtils.selectedDate = LocalDate.now();
        setDayView();
    }

    public void nextDayAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusDays(1);
        setDayView();
    }
}








