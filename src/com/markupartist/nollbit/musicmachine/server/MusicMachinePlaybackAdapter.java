package com.markupartist.nollbit.musicmachine.server;

import de.felixbruns.jotify.api.media.Track;
import de.felixbruns.jotify.api.player.PlaybackAdapter;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by IntelliJ IDEA.
 * User: johanm
 * Date: Apr 16, 2010
 * Time: 11:43:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class MusicMachinePlaybackAdapter extends PlaybackAdapter {
        @Override
        public void playbackFinished(Track track) {
            // move to next song
            MusicMachineApplication.playlist.removeTrack(track);
            if (MusicMachineApplication.playlist.isEmpty()) {
                System.out.println("No more tracks to play, waiting for new ones");
            }
            else
            {
                try {
                    System.out.println("Playing next track");
                    MusicMachineApplication.jotify.play(MusicMachineApplication.playlist.popTrack().getJotifyTrack(), 0 , this);
                } catch (TimeoutException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (LineUnavailableException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }

    @Override
    public void playbackPosition(Track track, int playtime) {
        MusicMachineApplication.playlist.setPlayingTrackPlaytime(playtime);
    }
}
