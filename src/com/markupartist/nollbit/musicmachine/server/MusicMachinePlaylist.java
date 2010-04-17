package com.markupartist.nollbit.musicmachine.server;

import com.markupartist.nollbit.musicmachine.server.model.MMStatus;
import com.markupartist.nollbit.musicmachine.server.model.MMTrack;
import de.felixbruns.jotify.api.media.Track;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: johanm
 * Date: Apr 15, 2010
 * Time: 12:22:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class MusicMachinePlaylist {
    private static int MAX_TRACKS = 5;

    private PlaylistPlayableListener listener;

    CopyOnWriteArrayList<MMTrack> tracks = new CopyOnWriteArrayList<MMTrack>();

    private int playingTrackPlaytime;
    private int timeUntilAdd;

    public List<MMTrack> getPlaylist() {
        return tracks;
    }

    public boolean addTrack(Track track) throws PlaylistFullException {
        MMTrack mmTrack = new MMTrack(track);
        addTrack(mmTrack);
        return true;
    }

    public boolean addTrack(MMTrack track) throws PlaylistFullException {
        if (tracks.size() >= MAX_TRACKS) {
            throw new PlaylistFullException();
        }
        tracks.add(track);

        if (tracks.size() == 1 && listener != null) {
            listener.trackAddedToEmptyPlaylist(track);
        }

        return true;
    }

    public void removeTrack(Track track) {
        for (MMTrack t : tracks) {
            if (t.getId() == track.getId()) {
                tracks.remove(t);
            }
        }
    }

    public boolean isEmpty() {
        return tracks.isEmpty();
    }

    public MMTrack popTrack() throws PlaylistEmptyException {
        try {
            return tracks.remove(0);
        } catch (IndexOutOfBoundsException e) {
            throw new PlaylistEmptyException();
        }
    }

    public PlaylistPlayableListener getListener() {
        return listener;
    }

    public void setListener(PlaylistPlayableListener listener) {
        this.listener = listener;
    }

    public int getPlayingTrackPlaytime() {
        return playingTrackPlaytime;
    }

    public void setPlayingTrackPlaytime(int playingTrackPlaytime) {
        this.playingTrackPlaytime = playingTrackPlaytime;

        // calculate time until we allow adding to the playlist
        this.timeUntilAdd = this.getTotalPlayingTime() - playingTrackPlaytime;
    }

    public int getTotalPlayingTime() {
        int playingTime = 0;
        for (MMTrack t : tracks) {
            playingTime += t.getLength();
        }
        return playingTime;
    }

    public MMStatus getStatus() {
        return new MMStatus(this.playingTrackPlaytime, this.timeUntilAdd);
    }

    public class PlaylistFullException extends RuntimeException {
    }

    public class PlaylistEmptyException extends RuntimeException {
    }

    public interface PlaylistPlayableListener {
        public void trackAddedToEmptyPlaylist(MMTrack track);
    }

    public class PlaylistPlayableAdapter implements PlaylistPlayableListener {
        public void trackAddedToEmptyPlaylist(MMTrack track) {
        }
    }

}
