package com.markupartist.nollbit.musicmachine.server;

import com.markupartist.nollbit.musicmachine.server.handlers.*;
import com.sun.net.httpserver.HttpServer;
import de.felixbruns.jotify.api.Jotify;
import de.felixbruns.jotify.api.JotifyConnection;
import de.felixbruns.jotify.api.exceptions.AuthenticationException;
import de.felixbruns.jotify.api.exceptions.ConnectionException;
import de.felixbruns.jotify.api.media.User;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 *
 * PLEASE NOTE!
 *
 * Most of this code comes from the Gateway application from the Jotify distribution.
 *
 * So I take NO credit for this
 * 
 * Created by IntelliJ IDEA.
 * User: johanm
 * Date: Apr 14, 2010
 * Time: 10:25:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class MusicMachineApplication {
    public static Map<String, MusicMachineSession> sessions;
    public static ExecutorService executor;
    public static Jotify jotify;
    public static MusicMachinePlaylist playlist;
    public static MusicMachineElector elector;
    public static JmDNS jmdns;

    private static MusicMachinePlaybackAdapter pbAdapter;
    private static User user;

    private static Timer kickOffTimer = new Timer();

    /* Statically create session map and executor for sessions. */
    static {
        sessions = new HashMap<String, MusicMachineSession>();
        executor = Executors.newCachedThreadPool();
        jotify = new JotifyConnection();
        playlist = new MusicMachinePlaylist();
        pbAdapter = new MusicMachinePlaybackAdapter();
        elector = new MusicMachineElector();
        try {
            jmdns = JmDNS.create();
        } catch (IOException e) {
            System.err.println("Unable to start JmDNS");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    /* Main thread to listen for client connections. */
    public static void main(String[] args) throws IOException {
        String username = null;
        String password = null;
        int port = 8080;

        int i = 0;
        while (i < args.length && args[i].startsWith("-")) {
            String arg = args[i++];
            if (arg.equals("-u")) {
                if (i < args.length)
                    username = args[i++];
                else
                    System.err.println("-u requires a username");
            } else if (arg.equals("-p")) {
                if (i < args.length)
                    password = args[i++];
                else
                    System.err.println("-p requires a password");
            } else if (arg.equals("-P")) {
                if (i < args.length)
                    try {
                        port = Integer.parseInt(args[i++]);
                    } catch (NumberFormatException e) {
                        System.err.println(String.format(
                                "Port must be numeric, using default port %s instead.", port));
                    }
            }
        }

        if (username == null || password == null) {
            System.err.println("Usage: -u <username> -p <password> [-P <port>]");
            System.exit(0);
        }

        /* Login to spotify */
        try {
            jotify.login(username, password);
            user = jotify.user();
        } catch (ConnectionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (AuthenticationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TimeoutException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if (user == null) {
            System.err.println("Unable to login to spotify, check stacks");
            return;
        }
        /* Check if user is premium. */
        if(!user.isPremium()){
            try {
                jotify.close();
            } catch (ConnectionException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            System.err.println("You need a premium account :) byebye");
            return;
        }

        /* Create a HTTP server that listens for connections on port 8080 or the given port. */
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        /* Set up content handlers. */
        server.createContext("/",               new ContentHandler());
        server.createContext("/playlist",       new PlaylistHandler());
        server.createContext("/status",         new StatusHandler());
        server.createContext("/vote",           new VoteHandler());
        server.createContext("/control",        new ControlHandler());
        server.createContext("/setup/",          new SetupHandler());

        /* Set executor for server threads. */
        server.setExecutor(executor);

        /* Start HTTP server. */
        server.start();
        System.out.println("Server started on port " + port);

        jmdns.registerService(ServiceInfo.create("_http._tcp.local.", "MusicMachine", port, 0, 0, "path=/"));


        playlist.setListener(pbAdapter);

        elector.setListener(new MusicMachineElector.ElectionListener() {
            public void voteAdded(String trackUri, String userId) {
                System.out.println(String.format("Vote added: %s by %s", trackUri, userId));
                // if playlist is empty, draw a winner in 30 seconds
                if (playlist.isEmpty() && elector.getNumVotes() == 1) {
                    System.out.println("Playlist is empty, drawing winner in 30 seconds");
                    kickOffTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            pbAdapter.addWinnersToPlaylist();
                        }
                    }, 30*1000);
                    
                }
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                System.out.println("Shutting down...");
                try {
                    jotify.close();
                } catch (ConnectionException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
    }

}
