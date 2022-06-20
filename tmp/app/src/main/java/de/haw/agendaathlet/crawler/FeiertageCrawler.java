package de.haw.agendaathlet.crawler;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import de.haw.agendaathlet.InjectorManager;
import de.haw.agendaathlet.eventVisual.Event;

public class FeiertageCrawler {

    private static String url1 = "https://feiertage-api.de/api/?jahr=";
    private static String url2 = "&nur_land=HH";
    private int jahr;
    private SharedPreferences prefs;

    public FeiertageCrawler() {

        jahr = Calendar.getInstance().get(Calendar.YEAR);
    }

    public void gibFeiertage(int X) {
        List<Event> event = new ArrayList<>();


        try {
            URL url = new URL(url1 + X + url2);

            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder responce = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                responce.append(inputLine);
            }
            in.close();
            System.out.println(responce.toString());
            JSONObject feiertage = new JSONObject(responce.toString());
            Iterator<String> keys = feiertage.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                System.out.println(feiertage.get(key));
                event.add(new Event(key, LocalDate.parse((feiertage.getJSONObject(key).get("datum")).toString()),
                        LocalTime.MIN, LocalTime.MAX, "Gesetzlicher Feiertag in HH"));
            }

            InjectorManager.IM.gibDatenverwaltung().speichereEvents(event);
            Context context = InjectorManager.IM.gibMainActivity().getApplicationContext();
            PackageManager packageManager = context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
            ComponentName componentName = intent.getComponent();
            Intent mainIntent = Intent.makeRestartActivityTask(componentName);
            context.startActivity(mainIntent);
            Runtime.getRuntime().exit(0);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}


