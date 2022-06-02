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



import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import de.haw.agendaathlet.R;

public class DashboardActivity extends AppCompatActivity {

    private static final int PICKFILE_RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    public void onClick(View view){
        if(view.getId() == R.id.modul) {
            startActivity(new Intent(DashboardActivity.this, ModuleSearchActivity.class));
        }

        if(view.getId() == R.id.einevent) {
            Intent intent = new Intent(DashboardActivity.this, NewEventActivity.class);
            startActivity(intent);
        }

        if(view.getId() == R.id.eventmehrere) {
            startActivity(new Intent(DashboardActivity.this, WeeklyEventActivity.class));
        }

        if(view.getId() == R.id.ics) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/calendar");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, PICKFILE_RESULT_CODE);
        }
        finish();
    }

}
