package com.markupartist.nollbit.musicmachine.server.handlers;

import com.google.gson.Gson;
import com.markupartist.nollbit.musicmachine.server.MusicMachineApplication;
import com.markupartist.nollbit.musicmachine.server.MusicMachineHandler;
import com.markupartist.nollbit.musicmachine.server.MusicMachinePlaylist;
import com.markupartist.nollbit.musicmachine.server.model.MMTrack;
import de.felixbruns.jotify.api.media.Track;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by IntelliJ IDEA.
 * User: johanm
 * Date: Apr 15, 2010
 * Time: 1:54:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlaylistHandler extends MusicMachineHandler {
    Gson gson = new Gson();

    @Override
    public String handleGet(Map<String, String> params) {
        List<MMTrack> tracks;
        int limit = 10;
        if (params.containsKey("p") && params.get("p").equalsIgnoreCase("played"))
        {
            tracks = MusicMachineApplication.playlist.getPreviousTracks();
            Collections.reverse(tracks);
        }
        else
        {
            tracks = MusicMachineApplication.playlist.getPlaylist();
        }

        if (limit > tracks.size())
        {
            limit = tracks.size() + 1;
        }
        tracks = tracks.subList(0, limit - 1);

        return gson.toJson(tracks);
    }

    @Override
    public String handlePost(Map<String, String> params) throws BadRequestException, InternalServerErrorException, ConflictException{
        if (!params.containsKey("track"))
            throw new BadRequestException("Missing param 'track'");

        String trackId = params.get("track");

        MMTrack addedTrack;
        try {
            Track track = MusicMachineApplication.jotify.browseTrack(trackId);
            addedTrack = MusicMachineApplication.playlist.addTrack(track);
        } catch (TimeoutException e) {
            System.err.println("Timeout occurred while getting track information");
            throw new InternalServerErrorException("Timeout occurred while getting track information");
        } catch (MusicMachinePlaylist.PlaylistFullException e) {
            throw new ConflictException("Playlist full");
        } catch (MusicMachinePlaylist.TrackAlreadyAddedException e) {
            throw new ConflictException("Track has already been added");
        } catch (IllegalArgumentException e) {
            return e.toString();
        }

        return addedTrack.getId();
    }
}
