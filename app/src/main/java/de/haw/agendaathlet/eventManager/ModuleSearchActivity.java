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

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import de.haw.agendaathlet.InjectorManager;
import de.haw.agendaathlet.R;

public class ModuleSearchActivity extends AppCompatActivity {

    private ListView listeNamen;
    private ArrayAdapter<String> adapter;
    private SearchView suchView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_selection);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initWidgets();
    }

    private void initWidgets()
    {
        listeNamen = findViewById(R.id.listeNamen);
        adapter = new ModuleSearchAdapter(this, android.R.layout.simple_list_item_multiple_choice, InjectorManager.IM.gibICSCrawler().getNameList());
        suchView = findViewById(R.id.SearchNamen);
        listeNamen.setAdapter(adapter);
        initSuchListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        if(id == R.id.item_done) {

            setContentView(R.layout.activity_module_selection);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            initWidgets();

            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 0; i < listeNamen.getAdapter().getCount(); i++) {
                            if (((ModuleSearchAdapter) listeNamen.getAdapter()).isChecked(i)) {

                                try {
                                    URL url = InjectorManager.IM.gibICSCrawler().getUrlList().get(i);
                                    URLConnection con = url.openConnection();
                                    InputStream in = con.getInputStream();
                                    String encoding = con.getContentEncoding();
                                    encoding = encoding == null ? "UTF-8" : encoding;
                                    Reader r = new InputStreamReader(in, encoding);
                                    StringBuilder buf = new StringBuilder();
                                    while (true) {
                                        int ch = r.read();
                                        if (ch < 0)
                                            break;
                                        buf.append((char) ch);
                                    }
                                    String str = buf.toString();
                                    System.out.println(url);
                                    InjectorManager.IM.gibEventLogic().addicstoEvents(str);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }}
                        for (int i = 0; i < listeNamen.getAdapter().getCount(); i++) {
                            ModuleSearchAdapter.checkBoxState2[i] = false;
                        }
                    }
                }).start();
            } catch (Exception e) {
                Toast.makeText(ModuleSearchActivity.this, "Laden der Pläne lief nicht so dolle", Toast.LENGTH_LONG).show();
            }
        }

        Toast.makeText(ModuleSearchActivity.this, "Erfolgreich importiert ✅", Toast.LENGTH_LONG).show();
        finish();
        return true;
    }

    private void initSuchListener()
    {
        suchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) { return false;}

            @Override
            public boolean onQueryTextChange(String s)
            {
                ArrayList<String> filteredList = new ArrayList<String>();

                for(String string: InjectorManager.IM.gibICSCrawler().getNameList())
                {
                    if(string.toLowerCase().contains(s.toLowerCase())) filteredList.add(string);
                }
                ModuleSearchAdapter adapter2 = new ModuleSearchAdapter(getApplicationContext(), 0, filteredList);
                listeNamen.setAdapter(adapter2);
                return false;
            }
        });
    }
}