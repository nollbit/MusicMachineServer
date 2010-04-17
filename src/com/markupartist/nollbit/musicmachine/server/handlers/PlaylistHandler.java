package com.markupartist.nollbit.musicmachine.server.handlers;

import com.google.gson.Gson;
import com.markupartist.nollbit.musicmachine.server.MusicMachineApplication;
import com.markupartist.nollbit.musicmachine.server.MusicMachineHandler;
import com.markupartist.nollbit.musicmachine.server.MusicMachinePlaylist;
import com.markupartist.nollbit.musicmachine.server.model.MMTrack;
import de.felixbruns.jotify.api.media.Track;

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
        List<MMTrack> tracks = MusicMachineApplication.playlist.getPlaylist();
        return gson.toJson(tracks);
    }

    @Override
    public String handlePost(Map<String, String> params) throws BadRequestException, InternalServerErrorException, ConflictException{
        if (!params.containsKey("id"))
            throw new BadRequestException("Missing param 'id'");

        String trackId = params.get("id");

        try {
            Track track = MusicMachineApplication.jotify.browseTrack(trackId);
            MusicMachineApplication.playlist.addTrack(track);
        } catch (TimeoutException e) {
            System.err.println("Timeout occurred while getting track information");
            throw new InternalServerErrorException("Timeout occurred while getting track information");
        } catch (MusicMachinePlaylist.PlaylistFullException e) {
            throw new ConflictException("Playlist full");
        }

        return "ok";
    }
}
