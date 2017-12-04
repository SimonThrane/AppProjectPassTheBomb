package com.thrane.simon.passthebomb.Models;

import java.util.List;

/**
 * Created by Søren on 26-11-2017.
 */
public class Game {
    public List<User> users;
    public User host;
    public Boolean isPublic;
    public Category category;
    public String name;
    public String difficulty;
    public String password;

    public Game() {
    }
}
