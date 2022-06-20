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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IcsUrlFinder implements ICSCrawler {

    private final String ICS_REGEX = "href=\"(\\S+\\.ics)\"";
    public final List<URL> urlList = new ArrayList<>();
    public final List<String> nameList = new ArrayList<>();

    public final String[] SOURCE_URLS = {
            "https://userdoc.informatik.haw-hamburg.de/doku.php?id=stundenplan:ics_public",
            "https://www.haw-hamburg.de/en/study/degree-courses-a-z/study-courses-in-detail/course/courses/show/information-engineering/Studierende/",
            "https://www.haw-hamburg.de/hochschule/technik-und-informatik/departments/informations-und-elektrotechnik/studium/studienorganisation/studienplaene/",
    };

    public void getAll(String extraUrl) throws IOException {
        for (String url : SOURCE_URLS) {
            urlList.addAll(getFromUrl(url));
        }
        if (!extraUrl.equals("keine")) {
            urlList.addAll(getFromUrl(extraUrl));
        }
    }

    private List<URL> getFromUrl(String baseUrl) throws IOException {
        URL url = new URL(baseUrl);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        List<URL> urls = new ArrayList<>();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            Pattern pattern = Pattern.compile(ICS_REGEX);
            Matcher matcher = pattern.matcher(inputLine);
            if (matcher.find()) {
                String modiURL = baseUrl.replace("/doku.php?id=stundenplan:ics_public", "");
                modiURL = modiURL.replace("https://www.haw-hamburg.de/en/study/degree-courses-a-z/study-courses-in-detail/course/courses/show/information-engineering/Studierende/", "https://www.haw-hamburg.de/fileadmin/TI-IE/PDF/Studium/Studienorganisation/Studienpl%C3%A4ne/Kalenderdateien/");
                modiURL = modiURL.replace("https://www.haw-hamburg.de/hochschule/technik-und-informatik/departments/informations-und-elektrotechnik/studium/studienorganisation/studienplaene/", "https://www.haw-hamburg.de/fileadmin/TI-IE/PDF/Studium/Studienorganisation/Studienpl%C3%A4ne/Kalenderdateien/");
                URL newUrl = new URL(modiURL + matcher.group(1));
                urls.add(newUrl);
                Pattern pattern2 = Pattern.compile("([^\\/]*).ics(?=\\s*$|\\s*[?&amp;])");
                Matcher matcher2 = pattern2.matcher(newUrl.toString());
                while (matcher2.find()) {
                    nameList.add(matcher2.group(1).replace("fetch.php?media=stundenplan:", ""));
                }
            }
        }
        in.close();
        return urls;
    }

    public List<URL> getUrlList() {
        return urlList;
    }

    public List<String> getNameList() {
        return nameList;
    }
}