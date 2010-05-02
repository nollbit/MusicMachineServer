package com.markupartist.nollbit.musicmachine.server.handlers;

import com.google.gson.Gson;
import com.markupartist.nollbit.musicmachine.server.MusicMachineApplication;
import com.markupartist.nollbit.musicmachine.server.MusicMachineHandler;
import com.markupartist.nollbit.musicmachine.server.model.MMStatus;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: johan
 * Date: May 2, 2010
 * Time: 2:23:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class SetupHandler extends MusicMachineHandler {
    Gson gson = new Gson();
    @Override
    public String handleGet(Map<String, String> params) throws BadRequestException, InternalServerErrorException, ConflictException {
        InetSocketAddress address = getHttpExchange().getLocalAddress();

        String hostAddress = address.getAddress().getHostAddress();
        int port = address.getPort();

        String serverUrl = String.format("http://%s:%s", hostAddress, port);
        return gson.toJson(serverUrl);
    }
}
