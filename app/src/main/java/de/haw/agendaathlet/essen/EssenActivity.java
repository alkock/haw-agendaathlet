package de.haw.agendaathlet.essen;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import de.haw.agendaathlet.InjectorManager;
import de.haw.agendaathlet.R;
import de.haw.agendaathlet.crawler.Essensplan;
import de.haw.agendaathlet.datamanagement.Datenverwaltung;
import de.haw.agendaathlet.eventManager.CalendarUtils;
import de.haw.agendaathlet.eventVisual.OnSwipeTouchListener;

public class EssenActivity extends AppCompatActivity {

    private ListView essensListe;
    ArrayList<Essen> essens;
    private final Essensplan essensplanImpl;
    private TextView daytext;
    private LocalDate selectedEssenDate;
    private final Datenverwaltung datenverwaltung;

    public EssenActivity()
    {
        essensplanImpl = InjectorManager.IM.gibEssensplan();
        datenverwaltung = InjectorManager.IM.gibDatenverwaltung();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_essen);
        essensListe = findViewById(R.id.essensListe);
        daytext = findViewById(R.id.dayText);
        selectedEssenDate = LocalDate.now();
        setDayView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setDayView();
    }

    private void setDayView()
    {
        String date = CalendarUtils.monthDayFromDate2(selectedEssenDate);
        String dayOfWeek = selectedEssenDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        daytext.setText(dayOfWeek + ", "+ date);
        essens = new ArrayList<>();

        for(int i = essensplanImpl.gibmenuliste().size()-1; i>=0; --i)
        {
            if(essensplanImpl.gibdateliste().get(i).equals(selectedEssenDate))essens.add(new Essen(essensplanImpl.gibmenuliste().get(i), essensplanImpl.gibpriceliste().get(i).replace("&mdash;", "Preis folgt"), essensplanImpl.gibdateliste().get(i)));
        }

        Collections.sort(essens, new Comparator<Essen>() {
            @Override
            public int compare(Essen o1, Essen o2) {
                int result = 1;
                try{
                    result = (int) ((Double.valueOf(o2.getPreis().replace("€", "").replace(",", ".").replace(" pro 100g", ""))*100)-(Double.valueOf(o1.getPreis().replace("€", "").replace(",", ".").replace(" pro 100g", ""))*100));
                }catch (Exception e){}
                return  result;  }
        });

        if(essens.size() == 0)  {
            if(!(datenverwaltung.ladeEssen(selectedEssenDate).size() == 0)) {
                essens.addAll(datenverwaltung.ladeEssen(selectedEssenDate));
            }
            else{
                essens.add(new Essen("Hunger", "0,00€", selectedEssenDate));
            }
        }
        EssAdapter essensAdapter = new EssAdapter(getApplicationContext(), essens);
        essensListe.setAdapter(essensAdapter);

        essensListe.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeRight() {
                ArrayList<Essen> es = datenverwaltung.ladeEssen();
                ArrayList<LocalDate> da = new ArrayList<LocalDate>(datenverwaltung.ladeEssen().size());
                for(Essen e : es)
                {
                    da.add(e.getDatum());
                }
                LocalDate minDate = Collections.min(da);
                if(!selectedEssenDate.equals(minDate))
                {
                    selectedEssenDate = selectedEssenDate.minusDays(1);
                }
                setDayView();
            }

            public void onSwipeLeft() {
                ArrayList<Essen> es = datenverwaltung.ladeEssen();
                ArrayList<LocalDate> da = new ArrayList<LocalDate>(datenverwaltung.ladeEssen().size());
                for(Essen e : es)
                {
                    da.add(e.getDatum());
                }
                LocalDate maxDate = Collections.max(da);
                if(!selectedEssenDate.equals(maxDate))
                {
                    selectedEssenDate = selectedEssenDate.plusDays(1);
                }
                setDayView();
            }
        });
    }

    public void tagZurueck(View view)
    {
        ArrayList<Essen> es = datenverwaltung.ladeEssen();
        ArrayList<LocalDate> da = new ArrayList<LocalDate>(datenverwaltung.ladeEssen().size());
        for(Essen e : es)
        {
            da.add(e.getDatum());
        }
        LocalDate minDate = Collections.min(da);
        if(!selectedEssenDate.equals(minDate))
        {
            selectedEssenDate = selectedEssenDate.minusDays(1);
        }
        setDayView();
    }

    public void heute(View view)
    {
        selectedEssenDate = LocalDate.now();
        setDayView();
    }

    public void tagVor(View view)
    {
        ArrayList<Essen> es = datenverwaltung.ladeEssen();
        ArrayList<LocalDate> da = new ArrayList<LocalDate>(datenverwaltung.ladeEssen().size());
        for(Essen e : es)
        {
            da.add(e.getDatum());
        }
        LocalDate maxDate = Collections.max(da);
        if(!selectedEssenDate.equals(maxDate))
        {
            selectedEssenDate = selectedEssenDate.plusDays(1);
        }
        setDayView();
    }
}
