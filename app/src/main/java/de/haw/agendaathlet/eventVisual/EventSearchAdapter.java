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

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.haw.agendaathlet.InjectorManager;
import de.haw.agendaathlet.R;
import de.haw.agendaathlet.eventManager.CalendarUtils;
import de.haw.agendaathlet.eventManager.PopupDetailActivity;

public class EventSearchAdapter extends ArrayAdapter<Event> {

    public EventSearchAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Event event = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_event_suche, parent, false);
        }

        TextView eventName = convertView.findViewById(R.id.eventName);
        TextView eventDatum = convertView.findViewById(R.id.eventDatum);
        TextView eventZeit = convertView.findViewById(R.id.eventZeit);
        eventName.setText(event.getName());
        eventDatum.setText(CalendarUtils.DayMonthFromDate(event.getDate()));
        eventZeit.setText(CalendarUtils.formattedShortTime(event.getstarTime()) + " - " + CalendarUtils.formattedShortTime(event.getendTime()));

        eventName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PopupDetailActivity.class);

                int i = InjectorManager.IM.gibEventLogic().getEventList().indexOf(event);
                intent.putExtra("event", i);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
        eventDatum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PopupDetailActivity.class);

                int i = InjectorManager.IM.gibEventLogic().getEventList().indexOf(event);
                intent.putExtra("event", i);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
        eventZeit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PopupDetailActivity.class);

                int i = InjectorManager.IM.gibEventLogic().getEventList().indexOf(event);
                intent.putExtra("event", i);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
        return convertView;
    }

}