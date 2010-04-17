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
    private int timeUntilAdd;

    public MMStatus(int playtime, int timeUntilAdd) {
        this.playtime = playtime;
        this.timeUntilAdd = timeUntilAdd;
    }

    public int getPlaytime() {
        return playtime;
    }

    public void setPlaytime(int playtime) {
        this.playtime = playtime;
    }

    public int getTimeUntilAdd() {
        return timeUntilAdd;
    }

    public void setTimeUntilAdd(int timeUntilAdd) {
        this.timeUntilAdd = timeUntilAdd;
    }
}
