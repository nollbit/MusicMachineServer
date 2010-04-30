package com.markupartist.nollbit.musicmachine.server.model;

/**
 * Created by IntelliJ IDEA.
 * User: johanm
 * Date: Apr 17, 2010
 * Time: 12:21:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class MMStatus {
    private int playtime;
    private int timeUntilVote;
    private int numVotes;

    public MMStatus(int playtime, int timeUntilVote, int numVotes) {
        this.playtime = playtime;
        this.timeUntilVote = timeUntilVote;
        this.numVotes = numVotes;
    }

    public int getPlaytime() {
        return playtime;
    }

    public void setPlaytime(int playtime) {
        this.playtime = playtime;
    }

    public int getTimeUntilVote() {
        return timeUntilVote;
    }

    public void setTimeUntilVote(int timeUntilVote) {
        this.timeUntilVote = timeUntilVote;
    }

    public int getNumVotes() {
        return numVotes;
    }

    public void setNumVotes(int numVotes) {
        this.numVotes = numVotes;
    }
}
