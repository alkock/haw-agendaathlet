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
package de.haw.agendaathlet.crawler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.haw.agendaathlet.InjectorManager;
import de.haw.agendaathlet.datamanagement.Datenverwaltung;
import de.haw.agendaathlet.eventManager.CalendarUtils;

public class EssensplanImpl implements Essensplan {

    private final ArrayList<String> listmenu;
    private final ArrayList<String> listprice;
    private final ArrayList<LocalDate> listdate;
    private final Datenverwaltung datenverwaltung;

    public EssensplanImpl() {
        listmenu = new ArrayList<>();
        listprice = new ArrayList<>();
        listdate = new ArrayList<>();
        datenverwaltung = InjectorManager.IM.gibDatenverwaltung();
    }

    public void ladeEssen(String urlDesEssens, int preis) throws Exception {
        URL url = new URL(urlDesEssens);

        URLConnection connection = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        List<String> lines = new ArrayList<String>();

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        lines.add(inputLine);
        in.close();

        String s = response.toString();

        int i1 = s.indexOf("<h5 class=\"mensainfo__title\">Mensa Berliner Tor</h5>");
        int i2 = s.indexOf("<h5 class=\"mensainfo__title\">", i1 + 1);
        s = s.substring(i1, i2);
        s = s.substring(s.indexOf("<span class=\"menulist__headline--bold\">") + 39);
        String[] st = s.split(" <span class=\"menulist__headline--bold\">");

        for (String a : st) {


            ArrayList<String> lmenu = new ArrayList<>();
            ArrayList<String> lprice = new ArrayList<>();
            int ind = a.indexOf("</span>");
            String date = a.substring(ind + 10, ind + 70).replace(" ", "");

            LocalDate ld = CalendarUtils.DateFromString(date);
            //System.out.println("x" + ld + "x    " + a);

            Pattern pattern = Pattern.compile("(<h5 class=\"singlemeal__headline singlemeal__headline--(.*?)\">(.*?)</h5>)");
            Matcher matcher = pattern.matcher(a);

            while (matcher.find()) {
                lmenu.add(matcher.group(1).replace("  ", "").replaceAll("\\<.*?\\>", "").replaceAll("\\(.*?\\)", "").replace(" ,", ",").replace("&quot;", "“"));
            }

            pattern = Pattern.compile("<span class=\"singlemeal__info--semibold\">(.*?)</span>");
            matcher = pattern.matcher(a);

            while (matcher.find()) {
                String x = matcher.group(1).replace("  ", "").replaceAll(" &#8364;", "€");
                if (x.contains("0,")) x += " pro 100g";
                lprice.add(x);
            }

            for (int i = lmenu.size() - 1; i >= 0; --i) {
                listmenu.add(lmenu.get(i));
                listdate.add(ld);
            }

            for (int i = (lprice.size() / 3) - 1; i >= 0; --i) {
                listprice.add(lprice.get((i * 3) + preis));
            }

            datenverwaltung.speichereEssen(listmenu, listprice, listdate);
        }
    }

    public ArrayList<String> gibmenuliste() {
        return listmenu;
    }

    public ArrayList<String> gibpriceliste() {
        return listprice;
    }

    public ArrayList<LocalDate> gibdateliste() {
        return listdate;
    }
}