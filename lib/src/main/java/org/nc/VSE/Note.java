package org.nc.VSE;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;

public class Note {
    private final Sound instrument;
    private final SoundCategory category;
    private final int startTick;
    private final int endTick;
    private final float pitch;
    private final float volume;

    public Note(Sound instrument, SoundCategory category, int startTick, int endTick, float pitch, float volume) {
        this.instrument = instrument;
        this.category = category;
        this.startTick = startTick;
        this.endTick = endTick;
        this.pitch = pitch;
        this.volume = volume;
    }

    public Note(Sound instrument, int startTick, int endTick, float pitch, float volume) {
        this(instrument, SoundCategory.MASTER, startTick, endTick, pitch, volume);
    }

    public Sound getInstrument() {
        return instrument;
    }

    public SoundCategory getCategory() {
         return category; 
    }

    public int getStartTick() {
        return startTick;
    }

    public int getEndTick() {
        return endTick;
    }

    public float getPitch() {
        return pitch;
    }

    public float getVolume() {
        return volume;
    }
}
