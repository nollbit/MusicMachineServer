package com.markupartist.nollbit.musicmachine.server.handlers;

import com.markupartist.nollbit.musicmachine.server.MusicMachineApplication;
import com.markupartist.nollbit.musicmachine.server.MusicMachineHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: johanm
 * Date: Apr 30, 2010
 * Time: 6:05:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ControlHandler extends MusicMachineHandler {
    @Override
    public String handlePost(Map<String, String> params) throws BadRequestException, InternalServerErrorException, ConflictException {
        if (!params.containsKey("action")) {
            throw new BadRequestException("Missing param 'action'");
        }

        String action = params.get("action");
        if (action.equalsIgnoreCase("next")) {
            if (!MusicMachineApplication.playlist.isEmpty())
            {
                int almostAtEnd = MusicMachineApplication.playlist.popTrack().getLength() - 10000;
                try {
                    MusicMachineApplication.jotify.seek(almostAtEnd);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        else
        {
            throw new BadRequestException("Action not supported");
        }

        return "ok";
    }
}
