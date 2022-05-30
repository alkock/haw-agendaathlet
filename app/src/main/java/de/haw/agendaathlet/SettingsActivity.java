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

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import de.haw.agendaathlet.InjectorManager;
import de.haw.agendaathlet.R;
import de.haw.agendaathlet.eventManager.EventLogic;
import de.haw.agendaathlet.eventManager.EventModActivity;
import de.haw.agendaathlet.eventManager.ICSExport.ExportActivity;
import de.haw.agendaathlet.eventVisual.Event;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText editwecker, editurl;
    private final EventLogic eventLogic;
    SharedPreferences sharedPreferences;
    private int mensapreisSpeicher;
    private Spinner spinner;
    private static final String[] paths = {"Student", "Mitarbeiter", "Gast"};

    public SettingsActivity() {
        eventLogic = InjectorManager.IM.gibEventLogic();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        editwecker = findViewById(R.id.editwecker);
        editurl = findViewById(R.id.editurl);

        editwecker.setText(sharedPreferences.getString("wecker", "90"));
        editurl.setText(sharedPreferences.getString("url", "Keine"));

        spinner = (Spinner) findViewById(R.id.editessen);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SettingsActivity.this,
                android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(Integer.valueOf(sharedPreferences.getString("essen", "0")));
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                mensapreisSpeicher = 0;
                break;
            case 1:
                mensapreisSpeicher = 1;
                break;
            case 2:
                mensapreisSpeicher = 2;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        mensapreisSpeicher = 0;
    }

    public void export(View view) {
        startActivity(new Intent(this, ExportActivity.class));
    }

    public void trash(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setCancelable(false);

        builder.setTitle("Sind sie sicher? ");
        builder.setMessage("ES WERDEN ALLE TERMINE UNWIEDERUFLICH GELÖSCHT!!");


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                eventLogic.getEventList().clear();
                InjectorManager.IM.gibDatenverwaltung().loescheAlles();
                finish();
            }
        });
        builder.show();

    }

    public void close(View view) {
        finish();
    }

    public void save(View view) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("wecker", editwecker.getText().toString());
        editor.putString("essen", Integer.toString(mensapreisSpeicher));
        editor.putString("url", editurl.getText().toString());
        editor.commit();
        finish();

        Context context = getApplicationContext();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }
}
