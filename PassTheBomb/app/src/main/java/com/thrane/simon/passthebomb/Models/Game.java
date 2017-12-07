package com.thrane.simon.passthebomb.Models;

import java.util.List;

/**
 * Created by Søren on 26-11-2017.
 */
public class Game {

    public Game() {
        // Default constructor required for calls to DataSnapshot.getValue(Game.class)

    }

    public Game( List<User> users, User host, Boolean isPublic, String category, String name, String difficulty, String password) {
        this.users = users;
        this.host = host;
        this.isPublic = isPublic;
        this.category = category;
        this.name = name;
        this.difficulty = difficulty;
        this.password = password;
    }

    public List<User> users;
    public User host;
    public Boolean isPublic;
    public Category category;
    public String name;
    public String difficulty;
    public String password;
    public Boolean gameStarted = false;


}
