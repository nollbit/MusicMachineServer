package com.markupartist.nollbit.musicmachine.server;

import de.felixbruns.jotify.api.media.Track;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: johanm
 * Date: Apr 19, 2010
 * Time: 7:22:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class MusicMachineElector {

    private Map<String, Integer> votes = new ConcurrentHashMap<String, Integer>();
    private List<String> voters = new CopyOnWriteArrayList<String>();

    private ElectionListener listener;

    public void addVote(String trackUri, String userId) throws UserHasAlreadyVotedException {
        if (voters.contains(userId)) {
            throw new UserHasAlreadyVotedException();
        }
        voters.add(userId);
        if (!votes.containsKey(trackUri)) {
            votes.put(trackUri, 0);
        }
        votes.put(trackUri, votes.get(trackUri) + 1);

        if (listener != null) {
            listener.voteAdded(trackUri, userId);
        }
    }

    public List<String> electWinners(int numWinners) {
        List winners = new ArrayList<Track>();
        List list = new LinkedList(votes.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            if (winners.size() < numWinners) {
                winners.add(entry.getKey());
            }
        }
        votes.clear();
        voters.clear();
        return winners;
    }

    public synchronized int getNumVotes() {
        return voters.size();
    }

    public synchronized boolean hasVoted(String userId) {
        return voters.contains(userId);
    }

    public ElectionListener getListener() {
        return listener;
    }

    public void setListener(ElectionListener listener) {
        this.listener = listener;
    }

    public class UserHasAlreadyVotedException extends Exception {
    }

    public interface ElectionListener {
        public void voteAdded(String trackUri, String userId);
    }
}
