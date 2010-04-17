package com.markupartist.nollbit.musicmachine.server;

import com.markupartist.nollbit.musicmachine.server.handlers.ContentHandler;
import com.markupartist.nollbit.musicmachine.server.handlers.PlaylistHandler;
import com.markupartist.nollbit.musicmachine.server.handlers.StatusHandler;
import com.sun.net.httpserver.HttpServer;
import de.felixbruns.jotify.api.Jotify;
import de.felixbruns.jotify.api.JotifyConnection;
import de.felixbruns.jotify.api.exceptions.AuthenticationException;
import de.felixbruns.jotify.api.exceptions.ConnectionException;
import de.felixbruns.jotify.api.media.Track;
import de.felixbruns.jotify.api.media.User;
import de.felixbruns.jotify.api.player.PlaybackAdapter;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
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

    private static MusicMachinePlaybackAdapter pbAdapter;
    private static User user;

    /* Statically create session map and executor for sessions. */
    static {
        sessions = new HashMap<String, MusicMachineSession>();
        executor = Executors.newCachedThreadPool();
        jotify = new JotifyConnection();
        playlist = new MusicMachinePlaylist();
        pbAdapter = new MusicMachinePlaybackAdapter();
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
            } else if (arg.equals("-p")) {
                if (i < args.length)
                    port = Integer.parseInt(args[i++]);
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

        /* Create a HTTP server that listens for connections on port 8080 or the given port. */
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        /* Set up content handlers. */
        server.createContext("/",               new ContentHandler());
        server.createContext("/playlist",       new PlaylistHandler());
        server.createContext("/status",         new StatusHandler());

        /* Set executor for server threads. */
        server.setExecutor(executor);

        /* Start HTTP server. */
        server.start();

        playlist.setListener(new MusicMachinePlaylist.PlaylistPlayableListener() {

            public void playlistItemAdded(MusicMachinePlaylist playlist) {
                try {
                    System.out.println("Playing track");
                    jotify.play(playlist.popTrack().getJotifyTrack(), 0, pbAdapter);
                } catch (TimeoutException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (LineUnavailableException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
