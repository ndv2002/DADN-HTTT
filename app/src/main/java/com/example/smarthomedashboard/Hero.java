package com.example.smarthomedashboard;



import java.io.Serializable;

/**
 * Created by Belal on 9/9/2017.
 */

class Hero {
    private int id;
    private Float temp, humid;

    public Hero(Float temp, Float humid) {
        this.temp=temp;
        this.humid=humid;
    }

    public int getId() {
        return id;
    }

    /*public String getName() {
        return name;
    }

    public String getRealname() {
        return realname;
    }

    public int getRating() {
        return rating;
    }

    public String getTeamaffiliation() {
        return teamaffiliation;
    }*/
}
