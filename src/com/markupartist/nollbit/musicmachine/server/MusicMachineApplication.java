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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * PLEASE NOTE!
 * <p/>
 * Most of this code comes from the Gateway application from the Jotify distribution.
 * <p/>
 * So I take NO credit for this
 * <p/>
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
        int port = 6170;

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
            }
        }

        if (username == null || password == null) {
            System.err.println("Usage: -u <username> -p <password>");
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
        if (!user.isPremium()) {
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
        server.createContext("/", new ContentHandler());
        server.createContext("/playlist", new PlaylistHandler());
        server.createContext("/status", new StatusHandler());
        server.createContext("/vote", new VoteHandler());
        server.createContext("/control", new ControlHandler());
        server.createContext("/setup/", new SetupHandler());

        /* Set executor for server threads. */
        server.setExecutor(executor);

        /* Start HTTP server. */
        server.start();

        InetAddress addr = InetAddress.getLocalHost();
        System.out.println("Server started on [http://" + addr.getHostName() + ":" + port + "]");

        String computerName = "MusicMachine";
        String[] hostNameParts = addr.getHostName().split("\\.");
        if (hostNameParts.length > 1) {
            computerName = hostNameParts[hostNameParts.length - 2];
        } else if (hostNameParts.length == 1) {
            computerName = hostNameParts[0];
        }

        jmdns.registerService(ServiceInfo.create("_http._tcp.local.", computerName, port, 10, 10, "path=/"));

        playlist.setListener(pbAdapter);

        elector.setListener(new MusicMachineElector.ElectionListener() {
            public void voteAdded(String trackUri, String userId) {
                System.out.println(String.format("Vote added: %s by %s", trackUri, userId));
                // if playlist is empty, draw a winner in 30 seconds
                if (playlist.isEmpty() && elector.getNumVotes() == 1) {
                    System.out.println("Playlist is empty. Playing immediately.");
                    pbAdapter.addWinnersToPlaylist();
                }
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                System.out.println("Shutting down...");
                try {
                    jotify.close();
                    jmdns.close();
                } catch (ConnectionException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
    }

}
