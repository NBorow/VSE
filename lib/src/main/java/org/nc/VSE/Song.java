package org.nc.VSE;

import java.util.ArrayList;
import java.util.List;

public class Song {
    private final String title;
    private final int tempo;
    private final List<Note> notes;

    public Song(String title, int tempo) {
        this.title = title;
        this.tempo = tempo;
        this.notes = new ArrayList<>();
    }

    public void addNote(Note note) {
        notes.add(note);
    }

    public String getTitle() {
        return title;
    }

    public int getTempo() {
        return tempo;
    }

    public List<Note> getNotes() {
        return notes;
    }
}