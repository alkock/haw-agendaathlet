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

import de.haw.agendaathlet.crawler.Essensplan;
import de.haw.agendaathlet.crawler.EssensplanImpl;
import de.haw.agendaathlet.crawler.ICSCrawler;
import de.haw.agendaathlet.crawler.IcsUrlFinder;
import de.haw.agendaathlet.datamanagement.Datenverwaltung;
import de.haw.agendaathlet.eventManager.EventLogic;
import de.haw.agendaathlet.eventManager.EventLogicImpl;

public class InjectorManager {

    public static  InjectorManager IM;
    private ICSCrawler icscrawler;
    private Datenverwaltung datenverwaltung;
    private EventLogic event;
    private Essensplan essensplanImpl;
    private MainActivity mainActivity;

    public InjectorManager()
    {
        IM = this;
    }

    public ICSCrawler gibICSCrawler()
    {
        if(icscrawler == null) icscrawler = new IcsUrlFinder();
        return icscrawler;
    }

    public Datenverwaltung gibDatenverwaltung()
    {
        return datenverwaltung;
    }

    public void setDatenverwaltung(Datenverwaltung datenverwaltung1)
    {
        datenverwaltung = datenverwaltung1;
    }

    public EventLogic gibEventLogic() {
        if(event == null) event = new EventLogicImpl();
        return event;
    }

    public MainActivity gibMainActivity()
    {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    public Essensplan gibEssensplan() {
        if(essensplanImpl == null) essensplanImpl = new EssensplanImpl();
        return essensplanImpl;
    }
}
