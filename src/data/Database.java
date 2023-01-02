package data;

import iofiles.Action;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class Database {
    private ArrayList<User> users;
    private ArrayList<Movie> movies;
    private ArrayList<Action> actions;
    private User currUser;
    private ArrayList<Movie> currMovies;

    /**
     * Constructor initializez the database and sets the current user to null
     */
    public Database() {
        users = new ArrayList<>();
        movies = new ArrayList<>();
        actions = new ArrayList<>();
        currMovies = new ArrayList<>();
        currUser = null;
    }
}
