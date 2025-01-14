# Vanilla Sound Engine (VSE) - README

The Vanilla Sound Engine (VSE) is a versatile and efficient system for managing audio playback in Minecraft plugins. It ensures precise synchronization of sounds with game events, making it ideal for interactive experiences like mini-games and role-playing environments. Since it is built for vanilla minecraft, works for bedrock edition console players

---

## Core Components


### 1. Note

A `Note` represents an individual sound with specific playback attributes.

#### Responsibilities:
- Defines the instrument, pitch, volume, and duration of a sound.

#### Methods:
- `getInstrument`: Returns the instrument to be played, can be any bukkit sound.
- `getStartTick`: Retrieves the tick at which the note starts.
- `getEndTick`: Retrieves the tick at which the note ends. (does not yet end the note, only used to set the end of a song to the max endTick)
- `getPitch` and `getVolume`: Define the note's characteristics.

#### Example:
```java
Note note = new Note(Sound.BLOCK_NOTE_BLOCK_HARP, 0, 5, 1.2f, 0.8f);
```

---

### 2. Song

A `Song` is a collection of `Note` objects, played at a specified tempo.

#### Responsibilities:
- Manages the title and tempo of a song. (tempo currently does nothing)
- Stores a list of notes to be played.

#### Methods:
- `addNote`: Adds a note to the song.
- `getNotes`: Retrieves the list of notes.

#### Example:
```java
Song song = new Song("MySong", 120);
song.addNote(new Note(Sound.BLOCK_NOTE_BLOCK_PLING, 0, 5, 1.0f, 0.5f));
song.addNote(new Note(Sound.BLOCK_NOTE_BLOCK_BASS, 6, 10, 0.8f, 0.6f));
```

---

### 3. ActiveSong

Tracks the state of a currently playing song.

#### Responsibilities:
- Manages playback progress, looping, and stopping conditions.
- Plays notes to all players in a channel at the appropriate ticks.

#### Methods:
- `tick`: Updates the song’s playback, playing notes for the current tick.
- `stop`: Marks the song as stopped.
- `getMaxEndTick`: Determines the maximum tick for the song.

---

### 4. Channel

A channel is a logical grouping of players and active songs.

#### Responsibilities:
- Adds/removes players who hear the audio.
- Manages the lifecycle of `ActiveSong` instances.
- Updates all songs in the channel on each tick.

#### Methods:
- `addPlayer(Player p)`: Adds a player to the channel.
- `removePlayer(Player p)`: Removes a player from the channel.
- `playSong(Song song, boolean looping, String songId)`: Starts playing a new song in the channel.
- `tick()`: Updates all active songs, playing notes to players.

---

### 5. MultiChannelEngine

Manages multiple channels and synchronizes their updates.

#### Responsibilities:
- Creates and removes channels.
- Updates all channels on a regular interval.

#### Methods:
- `getOrCreateChannel(String channelName)`: Retrieves or creates a new channel.
- `playSong(String channelName, Song song, boolean looping, String songId)`: Plays a song in a specific channel.
- `shutdown()`: Stops all updates and clears channels.
#### Example:
```java
private final MultiChannelEngine mce;
//...
this.mce = new MultiChannelEngine(plugin); 
//...
Song song = new Song("MySong", 120);
song.addNote(new Note(Sound.BLOCK_NOTE_BLOCK_PLING, 0, 5, 1.0f, 0.5f));
song.addNote(new Note(Sound.BLOCK_NOTE_BLOCK_BASS, 6, 10, 0.8f, 0.6f));
mce.addPlayerToChannel("Master", player);
mce.playSong("Master", song , false, "Demo");

```
---

## Benefits of VSE

- **Flexibility**: Supports multiple channels and simultaneous playback of different songs.
- **Precision**: Ensures tight synchronization of audio with game events.
- **Scalability**: Handles many players and songs without performance issues.

---

## Known Limitations

- Playback precision depends on server tick rate (20 ticks/second). Any lag on the server can affect timing.
- Any sounds can be played. Some sounds pick from a random set of sounds.

---

## Future Improvements

- Implement a GUI or in game block interface for composing songs.

---

By following this guide, you can effectively utilize the Virtual Sound Engine (VSE) in your Minecraft plugins for advanced audio control and synchronization.

