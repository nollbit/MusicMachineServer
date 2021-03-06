# Music Machine

Music Machine is a Java server that plays music using a [Spotify](http://spotify.com) playlist, which contains a maximum of 5 tracks. A [web frontend](MusicMachineServer/raw/master/screenhots/music-machine-web.png) is provided which shows currently playing track and upcoming playlist.

__Music Machine requires a Spotify Premium account to function.__

During the playback of a song, anyone can use either a [Android](http://github.com/johannilsson/musicmachineandroid) or [iPhone](http://github.com/jbripley/mmiphone) client to vote for the next track to be added to the playlist. When the current song ends, the track with the most votes is added to the playlist. If the current playlist is less than 5 songs, the highest voted tracks are added in order to fill up the 5 slots.

## Installation

Music Machine requires Java 6 JRE to be on the machine used for playback. A pre-built Java [jar file](http://github.com/downloads/nollbit/MusicMachineServer/musicmachine-1_0.jar) is provided, so you don't need to build it yourself.

1. Download Java [jar file](http://github.com/downloads/nollbit/MusicMachineServer/musicmachine-1_0.jar)
2. Open up a commandline in your OS of choice (cmd on Windows, Terminal.app on Mac, or xterm etc. on Linux)
3. Go to the directory where you downloaded the musicmachine.jar file
4. Type in the following, replacing [user] and [password] with your Spotify account username and password.

        java -jar musicmachine-1_0.jar -u [user] -p [password]
    
5. It should start up and print out something like

        Connected to 'A2.spotify.com./78.31.12.10:4070'
        Server started on [Music Machine URL]
    
6. Go to [Music Machine URL] in a web browser, ex. `http://kite.local:6170`
7. You should see something like the screenshot shown below
8. All done

## Build

1. Install [ANT](http://ant.apache.org/)
2. Open up a commandline in your OS of choice (cmd on Windows, Terminal.app on Mac, or xterm etc. on Linux)
3. Go to the directory where you downloaded the source code
4. Type in the following

        ant jar
        
5. All done

## Screenshot
![Alt text](https://raw.githubusercontent.com/nollbit/MusicMachineServer/master/screenhots/music-machine-web.png "Web")
