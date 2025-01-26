// Example ActiveSong
package org.nc.VSE;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ActiveSong {
    private final String songId;
    private final Song song;
    private final boolean looping;

    private int currentTick;
    private boolean stopped;

    /**
     * For each Note in the Song, we keep track of:
     *  - the original Note
     *  - whether it is currently "active" (we played it but haven't ended it yet)
     *  - which SoundCategory we actually used to play it
     */
    private static class NotePlayback {
        final Note note;
        boolean isPlaying;
        SoundCategory categoryUsed; // <- what category we ended up using

        NotePlayback(Note n) {
            this.note = n;
            this.isPlaying = false;
            this.categoryUsed = n.getCategory(); // default to note's own category
        }
    }

    private final List<NotePlayback> notePlaybacks;

    public ActiveSong(String songId, Song song, boolean looping) {
        this.songId = songId;
        this.song = song;
        this.looping = looping;
        this.currentTick = 0;
        this.stopped = false;

        this.notePlaybacks = new ArrayList<>();
        for (Note n : song.getNotes()) {
            notePlaybacks.add(new NotePlayback(n));
        }
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

    public void stop() {
        this.stopped = true;
    }

    public void stopAllActiveNotesForSinglePlayer(Player player) {
        // We do NOT mark np.isPlaying = false,
        // since the note should keep playing for other players.
        for (NotePlayback np : notePlaybacks) {
            if (np.isPlaying) {
                     if(np.note.getInstr()!=null){
                        //player.stopSound(np.note.getInstr(), np.categoryUsed);
                        player.playSound(player.getLocation(), np.note.getInstr(), 0, np.note.getPitch());

                    }
                else{
                    player.stopSound(np.note.getInstrument(), np.categoryUsed);

            }
        }
    }
    
    }
    /**
     * Called each tick by the Channel.
     *  1) If currentTick == startTick => play the note
     *  2) If isPlaying && currentTick == endTick => stop the note
     *  3) If forced stop => stop them all immediately.
     */
    public boolean tick(List<Player> channelPlayers) {
        if (stopped) {
            // We were forcibly stopped => stop all active notes right away
            stopAllActiveNotes(channelPlayers);
            return false;
        }

        for (NotePlayback np : notePlaybacks) {
            Note note = np.note;

            // (1) Start the note
            if (!np.isPlaying && currentTick == note.getStartTick()) {
                // Decide which category to use
                SoundCategory catToUse = pickCategoryFor(note.getInstrument(), note.getCategory());

                // Actually playSound in that category
                for (Player p : channelPlayers) {
                    if(note.getInstr()!=null){    
                // Sound temp=getSoundSafely(note.getInstr());
                   //  if(temp!=null){
                     p.playSound(p.getLocation(), note.getInstr(), note.getVolume(), note.getPitch());
                    }
                    else{
                   p.playSound(p.getLocation(), note.getInstrument(), catToUse, note.getVolume(), note.getPitch());}
              

                // Mark active if it’s a multi-tick note
                if (note.getEndTick() > note.getStartTick()) {
                    np.isPlaying = true;
                    np.categoryUsed = catToUse; // store which category we actually used
                }
            }
            }

            // (2) End the note
            if (np.isPlaying && currentTick >= note.getEndTick()&&note.getEndTick()!=note.getStartTick()) {
                // Stop it in the exact category we used
                for (Player p : channelPlayers) {
                    if(note.getInstr()!=null){
                        p.playSound(p.getLocation(), note.getInstr(), 0, note.getPitch());

                        //p.stopSound(note.getInstr(), np.categoryUsed);
                }else{
                    p.stopSound(note.getInstrument(), np.categoryUsed);

                }
                }
                np.isPlaying = false;
            }
        }

        // (3) increment tick
        currentTick++;

        // (4) check if we’re past the final note
        int maxTick = getMaxEndTick();
        if (currentTick > maxTick) {
            if (looping) {
                // reset to start
                currentTick = 0;
                resetAllNotes(channelPlayers);
                return true;  
            } else {
                // done with the song
                stopAllActiveNotes(channelPlayers);
                stopped = true;
                return false;
            }
        }
        return true;
    }
    
    /**
     * If you want "mostly MASTER" but to override the category if something
     * with the same instrument is already playing in MASTER, we do that logic here.
     */
    private SoundCategory pickCategoryFor(org.bukkit.Sound instr, SoundCategory defaultCat) {
        // 1) If defaultCat is not MASTER, just respect that
        if (defaultCat != SoundCategory.MASTER) {
            return defaultCat;
        }

        // 2) Otherwise, check if we have the same instrument *currently playing* in MASTER
        if (isInstrumentActiveInMaster(instr)) {
            // If yes, pick some fallback category. Let's pick MUSIC, for example.
            return SoundCategory.MUSIC;
        }

        // 3) If not active, we stay in MASTER
        return SoundCategory.MASTER;
    }

    /**
     * Checks whether we have a note still "playing" (not ended) in MASTER with the same Sound
     */
    private boolean isInstrumentActiveInMaster(org.bukkit.Sound instr) {
        for (NotePlayback np : notePlaybacks) {
            if (np.isPlaying
            && np.note.getInstrument()==null){
                return false;
            }
            if (np.isPlaying
                && np.categoryUsed == SoundCategory.MASTER
                && np.note.getInstrument() == instr) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the highest endTick in the song so we know when it’s done
     */
    private int getMaxEndTick() {
        int max = 0;
        for (NotePlayback np : notePlaybacks) {
            max = Math.max(max, np.note.getEndTick());
        }
        return max;
    }

    /**
     * Called if the song forcibly stops or finishes. 
     * Stop every note that is currently playing.
     */
    private void stopAllActiveNotes(List<Player> channelPlayers) {
        for (NotePlayback np : notePlaybacks) {
            if (np.isPlaying) {
                for (Player p : channelPlayers) {
                    if(np.note.getInstr()!=null){
                        p.playSound(p.getLocation(), np.note.getInstr(), 0, np.note.getPitch());
                        //p.stopSound(np.note.getInstr(), np.categoryUsed);
                    }
                    p.stopSound(np.note.getInstrument(), np.categoryUsed);
                }
                np.isPlaying = false;
            }
        }
    }

    /**
     * If we loop, we want to re-activate from the start, 
     * so we also stop any note that might still be playing.
     */
    private void resetAllNotes(List<Player> channelPlayers) {
        stopAllActiveNotes(channelPlayers);
        // currentTick reset done in the main logic
    }
}
