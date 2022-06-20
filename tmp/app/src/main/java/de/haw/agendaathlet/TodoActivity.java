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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class TodoActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private EditText edittodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        sharedPreferences = getSharedPreferences("Todo", Context.MODE_PRIVATE);
        edittodo = findViewById(R.id.edittodo);
        edittodo.setText(sharedPreferences.getString("Todo", " •   "));

        edittodo.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //Add • to the beginning of each line after the user tipes enter
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    if (s.charAt(start) == '\n') {
                        edittodo.setText(edittodo.getText().toString() + " •   ");
                        edittodo.setSelection(edittodo.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Todo", s.toString());
                editor.apply();
            }
        });

    }

    public void close(View view) {
        finish();
    }

    public void save(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Todo", edittodo.getText().toString());
        editor.commit();
        finish();
    }
}
