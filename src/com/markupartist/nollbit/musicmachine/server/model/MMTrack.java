package com.markupartist.nollbit.musicmachine.server.model;

import de.felixbruns.jotify.api.media.Track;

/**
 * Created by IntelliJ IDEA.
 * User: johanm
 * Date: Apr 16, 2010
 * Time: 7:25:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class MMTrack {
    private String id;
    private String artist;
    private String title;
    private String uri;
    private int length;

    transient private Track jotifyTrack;

    public MMTrack(Track track) {
        id = track.getId();
        artist = track.getArtist().getName();
        title = track.getTitle();
        length = track.getLength();
        uri = track.getLink().asString();
        jotifyTrack = track;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Track getJotifyTrack() {
        return jotifyTrack;
    }

    public void setJotifyTrack(Track jotifyTrack) {
        this.jotifyTrack = jotifyTrack;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String toString() {
        return String.format("%s - %s (%s)", this.jotifyTrack.getArtist().getName(), this.jotifyTrack.getTitle(), this.jotifyTrack.getAlbum().getName());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MMTrack && (((MMTrack) obj).getId().equals(this.id));
    }
}
