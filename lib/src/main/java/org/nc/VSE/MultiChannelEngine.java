package org.nc.VSE;

import org.bukkit.Bukkit;
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
    public void removeChannel(String channelName) {
        Channel ch = channels.remove(channelName);
        if (ch != null) {
            // Not strictly necessary: the next tick loop won't call it
            // but if you want to forcibly stop everything:
            for (Player p : ch.getPlayers()) {
                // No direct "stop" needed for players. 
                // But we can clear the songs if we want.
            }
        }
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
        for (Channel channel : channels.values()) {
            if (channel.getPlayers().contains(player)) {
                channel.removePlayer(player);
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
