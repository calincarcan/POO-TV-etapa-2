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
     * Method adds a notifications to all the users subscribed to the genres of
     * the new added movie.
     * @param movie movie added
     */
    public void notifyUsersADD(Movie movie) {
        for (User user : users) {
            if (movie.getCountriesBanned().contains(user.getCredentials().getCountry())) {
                continue;
            }
            for (String genre : movie.getGenres()) {
                if (user.getSubscribedGenres().contains(genre)) {
                    Notification notification = new Notification(movie.getName(), "ADD");
                    user.getNotifications().add(notification);
                }
            }
        }
    }

    public void notifyUsersDELETE(Movie movie) {
        for (User user : users) {

        }
    }

    /**
     * Method checks if a movie with the same name as the given one already exists
     * in the database
     * @param name name of the movie to be added
     * @return true if found else false
     */
    public boolean findMovie(String name) {
        for (Movie movie : movies) {
            if (movie.getName().equals(name))
                return true;
        }
        return false;
    }

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
