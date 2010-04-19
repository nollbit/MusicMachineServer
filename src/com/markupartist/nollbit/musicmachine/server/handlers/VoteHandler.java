package com.markupartist.nollbit.musicmachine.server.handlers;

import com.markupartist.nollbit.musicmachine.server.MusicMachineApplication;
import com.markupartist.nollbit.musicmachine.server.MusicMachineElector;
import com.markupartist.nollbit.musicmachine.server.MusicMachineHandler;
import de.felixbruns.jotify.api.media.Track;

import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by IntelliJ IDEA.
 * User: johanm
 * Date: Apr 19, 2010
 * Time: 10:14:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class VoteHandler extends MusicMachineHandler {
    @Override
    public String handlePost(Map<String, String> params) throws BadRequestException, InternalServerErrorException, ConflictException {
        if (!params.containsKey("track") || !params.containsKey("user")) {
            throw new BadRequestException("Missing param 'track' or 'user'");
        }

        try {
            MusicMachineApplication.elector.addVote(params.get("track"), params.get("user"));
        } catch (MusicMachineElector.UserHasAlreadyVotedException e) {
            throw new ConflictException("Bah!");
        }
        return "ok";
    }
}
