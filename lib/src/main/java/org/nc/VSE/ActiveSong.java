package org.nc.VSE;

import java.util.List;

/**
 * Represents a currently playing instance of a Song.
 * It tracks:
 *  - a unique songId (e.g. "rouletteSpinLoop")
 *  - whether it loops
 *  - the current tick
 *  - whether it’s stopped
 * 
 * It does NOT store players, because that’s handled by the channel.
 */
public class ActiveSong {

    private final String songId;   // e.g. "rouletteSpinLoop"
    private final Song song;
    private final boolean looping;

    private int currentTick;       // where we are in the timeline
    private boolean stopped;       // if manually stopped or completed (for non-loop)

    public ActiveSong(String songId, Song song, boolean looping) {
        this.songId = songId;
        this.song = song;
        this.looping = looping;
        this.currentTick = 0;
        this.stopped = false;
    }

    public String getSongId() {
        return songId;
    }

    public Song getSong() {
        return song;
    }

    public boolean isLooping() {
        return looping;
    }

    public boolean isStopped() {
        return stopped;
    }

    /**
     * Mark as stopped externally, so channel knows to remove it next tick.
     */
    public void stop() {
        this.stopped = true;
    }

    /**
     * Called each tick by the Channel. We pass in the list of players in that channel,
     * so we can play notes to them.
     *
     * @return true if we’re still playing, false if we just finished (for one-shots).
     */
    public boolean tick(List<Note> notes, List<org.bukkit.entity.Player> channelPlayers) {
        if (stopped) {
            return false;
        }

        // 1) Play any notes matching currentTick for the channel’s players
        for (Note note : notes) {
            if (note.getStartTick() == currentTick) {
                for (org.bukkit.entity.Player p : channelPlayers) {
                    p.playSound(
                        p.getLocation(),
                        note.getInstrument(),
                        note.getVolume(),
                        note.getPitch()
                    );
                }
            }
        }

        // 2) Increment
        currentTick++;

        // 3) Check if we are past the final note’s end
        int maxTick = getMaxEndTick(notes);
        if (currentTick > maxTick) {
            if (looping) {
                currentTick = 0; // wrap around
                return true;     // keep playing
            } else {
                stopped = true;
                return false;    // done playing
            }
        }
        return true; // still going
    }

    private int getMaxEndTick(List<Note> notes) {
        int max = 0;
        for (Note note : notes) {
            max = Math.max(max, note.getEndTick());
        }
        return max;
    }
}
