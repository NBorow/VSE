package org.nc.VSE;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Manages multiple channels. Each channel can have multiple songs and a set of players.
 * We run one repeating task that calls channel.tick() for each channel every tick.
 */
public class MultiChannelEngine {

    private final Plugin plugin;
    private final Map<String, Channel> channels;
    private BukkitTask updateTask;

    public MultiChannelEngine(Plugin plugin) {
        this.plugin = plugin;
        this.channels = new HashMap<>();
        startUpdateTask();
    }

    private void startUpdateTask() {
        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Channel ch : channels.values()) {
                ch.tick();
            }
        }, 0L, 1L);
    }

    /**
     * Create or get an existing channel by name.
     */
    public Channel getOrCreateChannel(String channelName) {
        Channel ch = channels.get(channelName);
        if (ch == null) {
            ch = new Channel(channelName);
            channels.put(channelName, ch);
        }
        return ch;
    }

    /**
     * Remove a channel entirely, stopping all songs in it.
     */
    private void checkAndStopUpdateTask() {
        if (channels.isEmpty() && updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
    }
    
    public void removeChannel(String channelName) {
        Channel ch = channels.remove(channelName);
        if (ch != null) {
            for (Player p : ch.getPlayers()) {
                ch.removePlayer(p);
            }
        }
        checkAndStopUpdateTask();
    }
    


    public void addPlayerToChannel(String channelName, Player p) {
        Channel ch = getOrCreateChannel(channelName);
        ch.addPlayer(p);
    }

    public void removePlayerFromChannel(String channelName, Player p) {
        Channel ch = channels.get(channelName);
        if (ch != null) {
            ch.removePlayer(p);
        }
    }

    public void removePlayerFromAllChannels(Player player) {
        Iterator<Map.Entry<String, Channel>> it = channels.entrySet().iterator();
        while (it.hasNext()) {
            Channel channel = it.next().getValue();
            if (channel.getPlayers().contains(player)) {
                  for (Sound sound : channel.getAllPlayedSounds()) {
                    if(sound!=null){
                    player.stopSound(sound, SoundCategory.MASTER);}
                  }    
                channel.removePlayer(player);
            }
            // Remove the channel if it has no players left
            if (channel.getPlayers().isEmpty()) {
                it.remove();
            }
        }
    }
    

    /**
     * Start a new song or loop in the given channel.
     * Returns the final songId used.
     */
    public String playSong(String channelName, Song song, boolean looping, String songId) {
        Channel ch = getOrCreateChannel(channelName);
        return ch.playSong(song, looping, songId);
    }

    /**
     * Stop a specific song in a channel, if it exists.
     */
    public void stopSong(String channelName, String songId) {
        Channel ch = channels.get(channelName);
        if (ch != null) {
            ch.stopSong(songId);
        }
    }

    /**
     * Cleanly shut down this entire engine if needed, 
     * e.g., on plugin disable. Cancels the updateTask.
     */
    public void shutdown() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        channels.clear();
    }
}
