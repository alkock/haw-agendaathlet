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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import de.haw.agendaathlet.InjectorManager;
import de.haw.agendaathlet.R;
import de.haw.agendaathlet.eventVisual.Event;

public class PopupDetail extends AppCompatActivity {

    private TextView eventName, eventDescription;
    private int i;
    private final EventLogic event;

    public PopupDetail()
    {
        event = InjectorManager.IM.gibEventLogic();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detailview);
        eventName = (TextView)findViewById(R.id.NameTV);
        eventDescription = (TextView)findViewById(R.id.descTV);
        Bundle bundle = getIntent().getExtras();

        if(bundle.getInt("event")!= -1)
        {
            i = (int) bundle.get("event");
            Event ev = event.getEventList().get(i);
            System.out.println(ev.getName());
            eventName.setText(ev.getName());
            eventDescription.setText(ev.getDescription());
        }
    }

    public void closePopup(View view) {
        finish();
    }

    public void deleteevent(View view) {
        Event eventLoesche = event.getEventList().get(i);
        event.getEventList().remove(i);
        InjectorManager.IM.gibDatenverwaltung().loescheEvent(eventLoesche);
        finish();
    }

    public void deletemulevent(View view) {
            List<Event> zuAenderndeEvents = new ArrayList<>();
            for(int j = 0; j < event.getEventList().size(); j++) {
                if(event.getEventList().get(j).getName().equals(event.getEventList().get(i).getName())) zuAenderndeEvents.add(event.getEventList().get(j));
            }
            for(int j = 0; j < zuAenderndeEvents.size(); j++) {
                System.out.println(event.getEventList().indexOf(zuAenderndeEvents.get(j)));
                 InjectorManager.IM.gibDatenverwaltung().loescheEvent(zuAenderndeEvents.get(j));
                event.getEventList().remove(zuAenderndeEvents.get(j));
            }
            finish();
    }

    public void editevent(View view) {
        finish();
        Intent intent = new Intent(this, EventModActivity.class);
        intent.putExtra("event", i);
        intent.putExtra("multi", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void editmulevent(View view) {
        finish();
        Intent intent = new Intent(this, EventModActivity.class);
        intent.putExtra("event", i);
        intent.putExtra("multi", true);
        startActivity(intent);
    }
}