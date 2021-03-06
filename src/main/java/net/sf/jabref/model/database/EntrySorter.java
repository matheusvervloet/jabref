/*  Copyright (C) 2003-2016 JabRef contributors.
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/
package net.sf.jabref.model.database;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.jabref.event.EntryAddedEvent;
import net.sf.jabref.event.EntryChangedEvent;
import net.sf.jabref.event.EntryRemovedEvent;
import net.sf.jabref.model.entry.BibEntry;

import com.google.common.eventbus.Subscribe;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EntrySorter {

    private static final Log LOGGER = LogFactory.getLog(EntrySorter.class);

    private final List<BibEntry> set;
    private final Comparator<BibEntry> comp;
    private BibEntry[] entryArray;
    private boolean changed;


    public EntrySorter(List<BibEntry> entries, Comparator<BibEntry> comp) {
        set = entries;
        this.comp = comp;
        changed = true;
        index();
    }

    private void index() {

        synchronized (set) {

            // Resort if necessary:
            if (changed) {
                Collections.sort(set, comp);
                changed = false;
            }

            // Create an array of IDs for quick access, since getIdAt() is called by
            // getValueAt() in EntryTableModel, which *has* to be efficient.

            int count = set.size();


            entryArray = new BibEntry[count];
            int piv = 0;
            for (BibEntry entry : set) {
                entryArray[piv] = entry;
                piv++;
            }
        }
    }

    public BibEntry getEntryAt(int pos) {
        synchronized (set) {
            return entryArray[pos];
        }
    }

    public int getEntryCount() {
        synchronized (set) {
            if (entryArray != null) {
                return entryArray.length;
            } else {
                return 0;
            }
        }
    }

    @Subscribe
    public void listen(EntryAddedEvent EntryAddedEvent) {
        synchronized (set) {
            int pos = -Collections.binarySearch(set, EntryAddedEvent.getBibEntry(), comp) - 1;
            LOGGER.debug("Insert position = " + pos);
            if (pos >= 0) {
                set.add(pos, EntryAddedEvent.getBibEntry());
            } else {
                set.add(0, EntryAddedEvent.getBibEntry());
            }
        }
    }

    @Subscribe
    public void listen(EntryRemovedEvent entryRemovedEvent) {
        synchronized (set) {
            set.remove(entryRemovedEvent.getBibEntry());
            changed = true;
        }
    }

    @Subscribe
    public void listen(EntryChangedEvent entryChangedEvent) {
        synchronized (set) {
            int pos = Collections.binarySearch(set, entryChangedEvent.getBibEntry(), comp);
            int posOld = set.indexOf(entryChangedEvent.getBibEntry());
            if (pos < 0) {
                set.remove(posOld);
                set.add(-posOld - 1, entryChangedEvent.getBibEntry());
            }
        }
    }

}
