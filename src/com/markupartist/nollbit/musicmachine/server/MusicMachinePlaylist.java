package com.markupartist.nollbit.musicmachine.server;

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
            listener.playlistItemAdded(this);
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

    public MMTrack popTrack() {
        if (this.isEmpty())
            return null;

        return tracks.get(0);
    }
    
    public PlaylistPlayableListener getListener() {
        return listener;
    }

    public void setListener(PlaylistPlayableListener listener) {
        this.listener = listener;
    }

    public class PlaylistFullException extends Exception {}

    public interface PlaylistPlayableListener {
        public void playlistItemAdded(MusicMachinePlaylist playlist);
    }
    public class PlaylistPlayableAdapter implements PlaylistPlayableListener{
        public void playlistItemAdded(MusicMachinePlaylist playlist) {}
    }

}
