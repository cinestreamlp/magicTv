package com.allreplay.magicTv.replayService.http;

import com.allreplay.magicTv.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deblock on 27/02/2016.
 */
public class Categorie {
    private String name;

    private List<Movie> movies = new ArrayList<>();

    public Categorie(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addMovie(Movie movie) {
        this.movies.add(movie);
    }

    public List<Movie> getMovies() {
        return movies;
    }
}
