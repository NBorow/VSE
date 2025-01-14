package org.nc.VSE;

import org.bukkit.entity.Player;

import java.util.*;

/**
 * A channel has:
 *   - a name (e.g. "RouletteWheel" or "BettingTable")
 *   - a set of ActiveSong instances
 *   - a set of players
 * 
 * The channel's tick() updates all songs, playing notes to its players.
 */
public class Channel {

    private final String channelName;
    private final Set<Player> players;           // All players in this channel
    private final Map<String, ActiveSong> activeSongs;  // songId -> ActiveSong

    public Channel(String channelName) {
        this.channelName = channelName;
        this.players = new HashSet<>();
        this.activeSongs = new HashMap<>();
    }

    public String getChannelName() {
        return channelName;
    }

    /**
     * Add a player to this channel so they hear all active songs.
     */
    public void addPlayer(Player p) {
        players.add(p);
    }

    /**
     * Remove a player, so they no longer hear songs in this channel.
     */
    public void removePlayer(Player p) {
        players.remove(p);
    }

    public Set<Player> getPlayers() {
        return players;
    }

    /**
     * Start playing a new song in this channel. 
     * @param song       the Song definition (list of notes)
     * @param looping    true if we want it to repeat
     * @param songId     optional ID. If blank, auto-generate one.
     */
    public String playSong(Song song, boolean looping, String songId) {
        if (songId == null || songId.isEmpty()) {
            songId = UUID.randomUUID().toString();
        }
        // If there's already a song with that ID, we stop and remove it
        if (activeSongs.containsKey(songId)) {
            activeSongs.get(songId).stop();
            activeSongs.remove(songId);
        }

        ActiveSong as = new ActiveSong(songId, song, looping);
        activeSongs.put(songId, as);
        return songId;
    }

    /**
     * Stop a specific song by ID if it's in this channel.
     */
    public void stopSong(String songId) {
        ActiveSong as = activeSongs.get(songId);
        if (as != null) {
            as.stop();
        }
    }

    /**
     * Called each tick by MultiSongEngine to update this channel’s songs.
     */
    public void tick() {
        List<String> toRemove = new ArrayList<>();
        // Update each ActiveSong
        for (ActiveSong as : activeSongs.values()) {
            if (!as.isStopped()) {
                // tick the underlying note list
                boolean stillPlaying = as.tick(as.getSong().getNotes(), new ArrayList<>(players));
                if (!stillPlaying) {
                    // means song ended or was stopped
                    toRemove.add(as.getSongId());
                }
            } else {
                toRemove.add(as.getSongId());
            }
        }
        // remove finished songs
        for (String id : toRemove) {
            activeSongs.remove(id);
        }
    }
}
