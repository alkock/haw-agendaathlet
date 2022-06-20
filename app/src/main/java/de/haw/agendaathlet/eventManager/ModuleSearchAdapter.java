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


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import de.haw.agendaathlet.InjectorManager;
import de.haw.agendaathlet.R;

public class ModuleSearchAdapter extends ArrayAdapter<String> {

    public static boolean[] checkBoxState;
    public static boolean[] checkBoxState2 = new boolean[InjectorManager.IM.gibICSCrawler().getNameList().size()];

    public ModuleSearchAdapter(Context context, int resource, List<String> stringList) {
        super(context, resource, stringList);
        checkBoxState = new boolean[stringList.size()];

        for (int i = 0; i < stringList.size(); i++) {
            checkBoxState[i] = false;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String string = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_modul_suche, parent, false);
        }

        TextView fachName = convertView.findViewById(R.id.fachName);
        CheckBox check = convertView.findViewById(R.id.checkBox1);
        check.setTag(position);
        check.setChecked(checkBoxState[position]);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int getPosition = (Integer) v.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                checkBoxState[getPosition] = ((CheckBox) v).isChecked(); // Set the value of checkbox to maintain its state.
                checkBoxState2[InjectorManager.IM.gibICSCrawler().getNameList().indexOf(fachName.getText().toString())] = ((CheckBox) v).isChecked();
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox check = v.findViewById(R.id.checkBox1);
                check.setChecked(!check.isChecked());
                checkBoxState[position] = check.isChecked(); // Set the value of checkbox to maintain its state.
                checkBoxState2[InjectorManager.IM.gibICSCrawler().getNameList().indexOf(fachName.getText().toString())] = check.isChecked();
            }
        });

        fachName.setText(string);
        return convertView;


    }

    public boolean isChecked(int index) {
        return checkBoxState2[index];
    }

}