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
package de.haw.agendaathlet.eventVisual;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import de.haw.agendaathlet.InjectorManager;
import de.haw.agendaathlet.R;
import de.haw.agendaathlet.eventManager.EventLogic;

public class EventSearchActivity extends AppCompatActivity {

    private ListView listeNamen;
    private EventSearchAdapter adapter;
    private SearchView suchView;
    private final EventLogic event;

    public EventSearchActivity() {
        event = InjectorManager.IM.gibEventLogic();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_search);
        initWidgets();
    }

    private void initWidgets() {
        listeNamen = findViewById(R.id.listeEvents);
        adapter = new EventSearchAdapter(getApplicationContext(), event.getmegaCurrentEventList());
        suchView = findViewById(R.id.Suche);
        listeNamen.setAdapter(adapter);
        initSuchListener();
    }

    private void initSuchListener() {
        suchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<Event> filteredList = new ArrayList<Event>();

                for (Event ev : event.getmegaCurrentEventList()) {
                    String string = ev.getName();
                    if (string.toLowerCase().contains(s.toLowerCase())) filteredList.add(ev);
                }

                EventSearchAdapter adapter2 = new EventSearchAdapter(getApplicationContext(), filteredList);
                listeNamen.setAdapter(adapter2);
                return false;
            }
        });
    }
}