package org.nc.VSE;
    import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;

public class SingleSongEngine {
    private final Plugin plugin;
    private final Song song;
    private int currentTick = 0;
    private boolean playing = false;

    public SingleSongEngine(Plugin plugin, Song song,int delay) {
        this.plugin = plugin;
        this.song = song; 
    }

    /**
     * Starts playing the song to the given player immediately (no delay).
     */
    public void start(Player player) {
        start(player, 0L, 1L);
    }

    /**
     * Starts playing the song to the given player after an initial delay (in ticks).
     * @param player the player who should hear the song
     * @param delayTicks how many ticks before the song starts
     */
    public void start(Player player, long delayTicks) {
        start(player, delayTicks, 1L);
    }

    /**
     * Starts playing the song to the given player after an initial delay,
     * then checks and plays notes every period ticks.
     *
     * @param player     The player who should hear the song
     * @param delayTicks How many ticks before the song starts
     * @param period     How many ticks between each check for note playback
     */
    public void start(Player player, long delayTicks, long period) {
        playing = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!playing) {
                    cancel();
                    return;
                }
                // Play any notes matching the current tick
                for (Note note : song.getNotes()) {
                    if (note.getStartTick() == currentTick) {
                        player.playSound(
                            player.getLocation(),
                            note.getInstrument(),
                            note.getVolume(),
                            note.getPitch()
                        );
                    }
                }
                currentTick++;
            }
        }.runTaskTimer(plugin, delayTicks, period);
    }

    public void stop() {
        playing = false;
        currentTick = 0;
    }
}

