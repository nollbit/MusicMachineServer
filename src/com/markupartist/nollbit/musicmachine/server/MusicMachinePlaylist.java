package com.markupartist.nollbit.musicmachine.server;

import com.markupartist.nollbit.musicmachine.server.model.MMStatus;
import com.markupartist.nollbit.musicmachine.server.model.MMTrack;
import de.felixbruns.jotify.api.media.Track;

import java.util.ArrayList;
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

    List<MMTrack> playedTracks = new ArrayList<MMTrack>();

    private int playingTrackPlaytime;

    public List<MMTrack> getPlaylist() {
        return tracks;
    }

    public MMTrack addTrack(Track track) throws PlaylistFullException, TrackAlreadyAddedException {
        MMTrack mmTrack = new MMTrack(track);
        addTrack(mmTrack);
        return mmTrack;
    }

    public boolean addTrack(MMTrack track) throws PlaylistFullException, TrackAlreadyAddedException {
        if (tracks.size() >= MAX_TRACKS) {
            throw new PlaylistFullException();
        }

        if (playedTracks.contains(track) || tracks.contains(track)) {
            throw new TrackAlreadyAddedException();
        }

        tracks.add(track);

        if (tracks.size() == 1 && listener != null) {
            listener.trackAddedToEmptyPlaylist(this);
        }

        return true;
    }

    public void removeTrack(Track track) {
        for (MMTrack t : tracks) {
            if (t.getId() == track.getId()) {
                playedTracks.add(t);
                tracks.remove(t);
            }
        }
    }

    public boolean isEmpty() {
        return tracks.isEmpty();
    }

    public MMTrack popTrack() throws PlaylistEmptyException {
        try {
            return tracks.get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new PlaylistEmptyException();
        }
    }

    public int countAvailableSpots() {
        return MAX_TRACKS - tracks.size();
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
    }

    public int getTotalPlayingTime() {
        int playingTime = 0;
        for (MMTrack t : tracks) {
            playingTime += t.getLength();
        }
        return playingTime;
    }

    public MMStatus getStatus() {
        int timeUntilVote = 30000;
        if (!tracks.isEmpty()) {
            timeUntilVote = tracks.get(0).getLength() - this.playingTrackPlaytime;
        }
        return new MMStatus(this.playingTrackPlaytime, timeUntilVote, 0);
    }

    public List<MMTrack> getPreviousTracks() {
        return playedTracks;
    }

    public class PlaylistFullException extends RuntimeException {
    }

    public class PlaylistEmptyException extends RuntimeException {
    }

    public class TrackAlreadyAddedException extends RuntimeException {
    }


    public interface PlaylistPlayableListener {
        public void trackAddedToEmptyPlaylist(MusicMachinePlaylist playlist);
    }

    public class PlaylistPlayableAdapter implements PlaylistPlayableListener {
        public void trackAddedToEmptyPlaylist(MusicMachinePlaylist playlist) {
        }
    }

}
