package data;

import filters.CountryFilter;
import iofiles.Action;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.security.PrivilegedAction;
import java.util.*;
import java.util.function.Consumer;

@Setter
@Getter
public class Database {
    private ArrayList<User> users;
    private ArrayList<Movie> movies;
    private ArrayList<Action> actions;
    private User currUser;
    private ArrayList<Movie> currMovies;
    private Stack<ChangePageCommand> undoStack;

    public String getRecommendation() {
        ArrayList<String> genres = new ArrayList<>();
        currUser.getLikeMap().entrySet().stream().sorted(Map.Entry.comparingByValue())
                .forEach(stringIntegerEntry -> genres.add(stringIntegerEntry.getKey()));
        Collections.reverse(genres);
        currMovies = CountryFilter.moviePerms(currUser.getCredentials().getCountry(), this);
        currMovies.sort((o1, o2) -> o2.getNumLikes() - o1.getNumLikes());

        for (String genre : genres) {
            for (Movie movie : currMovies) {
                if (movie.getGenres().contains(genre) && !currUser.getWatchedMovies().contains(movie))
                    return movie.getName();
            }
        }
        return null;
    }

    /**
     * Method adds a notifications to all the users subscribed to the genres of
     * the new added movie.
     *
     * @param movie movie added
     */
    public void notifyUsersADD(final Movie movie) {
        for (User user : users) {
            if (movie.getCountriesBanned().contains(user.getCredentials().getCountry())) {
                continue;
            }
            for (String genre : movie.getGenres()) {
                if (user.getSubscribedGenres().contains(genre)) {
                    Notification notification = new Notification(movie.getName(), "ADD");
                    user.getNotifications().add(notification);
                    break;
                }
            }
        }
    }

    /**
     * Method adds a notification to all users who purchased the movie and refunds them
     * according to their account type
     *
     * @param movie deleted movie
     */
    public void notifyUsersDELETE(final Movie movie) {
        for (User user : users) {
            if (user.getPurchasedMovies().contains(movie)) {
                Notification notification = new Notification(movie.getName(), "DELETE");
                user.getNotifications().add(notification);
                if (user.getCredentials().getAccountType().equals("premium")) {
                    user.setNumFreePremiumMovies(user.getNumFreePremiumMovies() + 1);
                } else {
                    user.setTokensCount(user.getTokensCount() + 2);
                }
            }
        }
    }

    /**
     * Method deletes the movie from all lists of all users
     * then deletes the movie from the database
     *
     * @param movie movie to be deleted
     */
    public void deleteMovie(final Movie movie) {
        for (User user : users) {
            user.getPurchasedMovies().remove(movie);
            user.getWatchedMovies().remove(movie);
            user.getLikedMovies().remove(movie);
            user.getRatedMovies().remove(movie);
        }
        movies.remove(movie);
    }

    /**
     * Method returns a reference to the movie with the specified name
     *
     * @param name name of the movie
     * @return reference to the movie or null
     */
    public Movie findMovie(String name) {
        for (Movie movie : movies) {
            if (movie.getName().equals(name)) {
                return movie;
            }
        }
        return null;
    }

    /**
     * Constructor initializez the database and sets the current user to null
     */
    public Database() {
        users = new ArrayList<>();
        movies = new ArrayList<>();
        actions = new ArrayList<>();
        currMovies = new ArrayList<>();
        undoStack = new Stack<>();
        currUser = null;
    }
}
