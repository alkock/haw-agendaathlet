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
package de.haw.agendaathlet.datamanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import de.haw.agendaathlet.essen.Essen;
import de.haw.agendaathlet.eventManager.CalendarUtils;
import de.haw.agendaathlet.eventVisual.Event;

public class DatenverwaltungImpl extends SQLiteOpenHelper implements Datenverwaltung {
    private final Context context;
    public static final String DATABASE_NAME = "Eventsundessen.db";
    public static final int DATABASE_VERSION = 6;


    SharedPreferences firebaseSynchActivePreference;
    SharedPreferences.Editor editor;

    //TABLE EVENT
    private static final String TABLE_EVENTS = "events";
    private static final String EVENTS_ID = "_id";
    private static final String EVENTS_MODULE_NAME = "event_module_name";
    private static final String EVENTS_DESCRIPTION = "event_description";
    private static final String EVENTS_START_DATE = "event_start_date";
    private static final String EVENTS_START_TIME = "event_start_time";
    private static final String EVENTS_END_TIME = "event_end_time";

    //TABLE ESSEN
    private static final String TABLE_ESSEN = "essen";
    private static final String ESSEN_ID = "_id";
    private static final String ESSEN_NAME = "essen_name";
    private static final String ESSEN_PREIS = "essen_preis";
    private static final String ESSEN_DATUM = "essen_datum";

    private SQLiteDatabase IODatabase;

    public DatenverwaltungImpl(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Log.v("EventDatenbank:", " Instanz erstellt!");
//        Log.d("firebaseSynch", "anfang" + firebaseSynchActivePreference.getBoolean("firebaseSynchActive", false));

    }

    public void getDatabase() {

        if (IODatabase == null) IODatabase = this.getWritableDatabase();
    }

    public void speichereEvents(List<Event> o) {
        getDatabase();
        for (Event e : o) {
            /**
             *     private String name;
             *     private LocalDate date;
             *     private LocalTime startTime;
             *     private LocalTime endTime;
             *     private String description;
             */

            String name = e.getName();
            String startdate = CalendarUtils.DateToString(e.getDate());
            String starttime = CalendarUtils.formattedTime(e.getstarTime());
            String endtime = CalendarUtils.formattedTime(e.getendTime());
            String description = e.getDescription();

            IODatabase.execSQL("INSERT INTO " + TABLE_EVENTS + "(" + EVENTS_MODULE_NAME + " , " + EVENTS_START_DATE + " , " + EVENTS_START_TIME + " , " + EVENTS_END_TIME + " , " + EVENTS_DESCRIPTION + ") " + " VALUES ('" + name + "' , '" + startdate + "' , '" + starttime + "' , '" + endtime + "' , '" + description + "');");
            Cursor cursor = IODatabase.rawQuery("SELECT * FROM events", null);
            cursor.moveToLast();
            int id = cursor.getInt(0);
            cursor.close();
            e.setID(id);



        }
    }


    @Override
    public void speichereEssen(List<String> localname, List<String> localpreis, List<LocalDate> localdate) {
        getDatabase();

        for (LocalDate d : localdate) {
            IODatabase.execSQL("DELETE FROM " + TABLE_ESSEN + " WHERE " + ESSEN_DATUM + " = '" + CalendarUtils.DateToString(d) + "';");
        }

        for (int i = 0; i < localname.size(); i++) {
            String name = localname.get(i);
            String preis = localpreis.get(i);
            String datum = CalendarUtils.DateToString(localdate.get(i));
            IODatabase.execSQL("INSERT INTO " + TABLE_ESSEN + "(" + ESSEN_NAME + " , " + ESSEN_PREIS + " , " + ESSEN_DATUM + ")" + " VALUES ('" + name + "' , '" + preis + "' , '" + datum + "');");
        }
        IODatabase.execSQL("DELETE FROM " + TABLE_ESSEN + " WHERE " + ESSEN_DATUM + " <= '" + CalendarUtils.DateToString(LocalDate.now().minusDays(120)) + "';");

    }

    @Override
    public ArrayList<Essen> ladeEssen() {
        getDatabase();
        ArrayList<Essen> result = new ArrayList<Essen>();
        Cursor cursor = IODatabase.rawQuery("SELECT * FROM " + TABLE_ESSEN, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(1);
                LocalDate datum = CalendarUtils.DateFromString(cursor.getString(2));
                String preis = cursor.getString(3);
                result.add(new Essen(name, preis, datum));
            }
            while (cursor.moveToNext());
        }

        return result;
    }

    @Override
    public ArrayList<Essen> ladeEssen(LocalDate date) {
        getDatabase();
        ArrayList<Essen> result = new ArrayList<Essen>();
        Cursor cursor = IODatabase.rawQuery("SELECT * FROM " + TABLE_ESSEN + " WHERE " + ESSEN_DATUM + " = '" + CalendarUtils.DateToString(date) + "'", null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(1);
                LocalDate datum = CalendarUtils.DateFromString(cursor.getString(2));
                String preis = cursor.getString(3);
                result.add(new Essen(name, preis, datum));
            }
            while (cursor.moveToNext());
        }

        return result;
    }

    @Override
    public void loescheEvent(Event event) {
        int id = event.getID();
        IODatabase.execSQL("DELETE FROM " + TABLE_EVENTS + " WHERE " + EVENTS_ID + " = " + id + ";");

    }

    @Override
    public void updateEvent(Event event) {
        int id = event.getID();
        String name = event.getName();
        String startdate = CalendarUtils.DateToString(event.getDate());
        String starttime = CalendarUtils.formattedTime(event.getstarTime());
        String endtime = CalendarUtils.formattedTime(event.getendTime());
        String description = event.getDescription();

        IODatabase.execSQL("UPDATE " + TABLE_EVENTS + " SET " + EVENTS_MODULE_NAME + " = '" + name + "' , " + EVENTS_START_DATE + " = '" + startdate + "' , " + EVENTS_START_TIME + " = '" + starttime + "' , " + EVENTS_END_TIME + " = '" + endtime + "' , " + EVENTS_DESCRIPTION + " = '" + description + "' WHERE " + EVENTS_ID + " = " + id + ";");

    }

    @Override
    public void loescheAlles() {
        IODatabase.execSQL("DELETE FROM " + TABLE_EVENTS + ";");

    }

    @Override
    public ArrayList<Event> ladeEvents() {
        getDatabase();
        ArrayList<Event> events = new ArrayList<Event>();
        /**
         *     private String name;
         *     private LocalDate date;
         *     private LocalTime startTime;
         *     private LocalTime endTime;
         *     private String description;
         */
        Cursor cursor = IODatabase.rawQuery("SELECT * FROM " + TABLE_EVENTS, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                LocalDate date = CalendarUtils.DateFromString(cursor.getString(2));
                LocalTime startTime = CalendarUtils.TimeFromString(cursor.getString(3));
                LocalTime endTime = CalendarUtils.TimeFromString(cursor.getString(4));
                String description = cursor.getString(5);

                Event event = new Event(id, name, date, startTime, endTime, description);
                events.add(event);
            }
            while (cursor.moveToNext());
        }
        return events;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         *     private String name;
         *     private LocalDate date;
         *     private LocalTime startTime;
         *     private LocalTime endTime;
         *     private String description;
         */

        Log.v("EventDatenbank: ", "Erstelle Datenbank..");
        String query = "CREATE TABLE " + TABLE_EVENTS + " (" + EVENTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EVENTS_MODULE_NAME + " TEXT, " +
                EVENTS_START_DATE + " TEXT, " +
                EVENTS_START_TIME + " TEXT, " +
                EVENTS_END_TIME + " TEXT, " +
                EVENTS_DESCRIPTION + " TEXT" +
                ");";
        db.execSQL(query);
        query = "CREATE TABLE " + TABLE_ESSEN + " (" + ESSEN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ESSEN_NAME + " TEXT, " +
                ESSEN_DATUM + " TEXT, " +
                ESSEN_PREIS + " TEXT " +
                ");";

        db.execSQL(query);
        Log.v("EventDatenbank:", " Datenbank erstellt!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ESSEN);
        onCreate(db);
    }

    public boolean isFirebaseSynchActive() {
        boolean firebaseSynchActive = firebaseSynchActivePreference.getBoolean("firebaseSynchActive", false);
        return firebaseSynchActive;
    }

    public void setFirebaseSynchActive(boolean firebaseSynchActive) {
        editor.putBoolean("firebaseSynchActive", firebaseSynchActive);
        editor.commit();
        Log.d("firebaseSynch", "Synch=" + firebaseSynchActivePreference.getBoolean("firebaseSynchActive", false));
    }
}
