package com.markupartist.nollbit.musicmachine.server;

import com.markupartist.nollbit.musicmachine.server.model.MMTrack;
import de.felixbruns.jotify.api.media.File;
import de.felixbruns.jotify.api.media.Track;
import de.felixbruns.jotify.api.player.PlaybackAdapter;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by IntelliJ IDEA.
 * User: johanm
 * Date: Apr 16, 2010
 * Time: 11:43:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class MusicMachinePlaybackAdapter extends PlaybackAdapter implements MusicMachinePlaylist.PlaylistPlayableListener {
    public void trackAddedToEmptyPlaylist(MusicMachinePlaylist playlist) {
        try {
            this.playTrack(playlist.popTrack());
        } catch (MusicMachinePlaylist.PlaylistEmptyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void playbackFinished(Track track) {
        addWinnersToPlaylist();
        // move to next song
        try {
            MusicMachineApplication.playlist.removeTrack(track);
            this.playTrack(MusicMachineApplication.playlist.popTrack());

        } catch (MusicMachinePlaylist.PlaylistEmptyException e) {
            System.out.println("No more tracks to play, waiting for new ones");
        }
    }

    @Override
    public void playbackPosition(Track track, int playtime) {
        MusicMachineApplication.playlist.setPlayingTrackPlaytime(playtime);
    }

    private void playTrack(MMTrack track) {
        System.out.println("Playing track: " + track.toString());
        try {
            MusicMachineApplication.jotify.play(track.getJotifyTrack(), File.BITRATE_160, this);
        } catch (TimeoutException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (LineUnavailableException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    // sorry about this :)
    public void addWinnersToPlaylist() {
        // this should really be an event instead
        int numAvailableSpots = MusicMachineApplication.playlist.countAvailableSpots();
        if (numAvailableSpots > 0) {
            // see if the elector has some tracks for us
            List<String> trackUris = MusicMachineApplication.elector.electWinners(numAvailableSpots);
            Track votedTrack;
            for (String uri : trackUris) {
                try {
                    votedTrack = MusicMachineApplication.jotify.browseTrack(uri);
                    MusicMachineApplication.playlist.addTrack(votedTrack);
                } catch (TimeoutException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        
    }

}
