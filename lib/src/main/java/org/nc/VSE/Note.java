package org.nc.VSE;

import org.bukkit.Sound;

public class Note {
    private final Sound instrument;
    private final int startTick;
    private final int endTick;
    private final float pitch;
    private final float volume;

    public Note(Sound instrument, int startTick, int endTick, float pitch, float volume) {
        this.instrument = instrument;
        this.startTick = startTick;
        this.endTick = endTick;
        this.pitch = pitch;
        this.volume = volume;
    }

    public Sound getInstrument() {
        return instrument;
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
