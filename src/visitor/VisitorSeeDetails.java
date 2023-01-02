package visitor;

import data.ErrorMessage;
import data.User;
import data.Movie;
import data.CurrentPage;
import data.Database;
import factory.ErrorFactory;
import factory.MovieFactory;
import factory.UserFactory;
import filters.CountryFilter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import iofiles.Action;

import java.util.ArrayList;

public final class VisitorSeeDetails implements Visitor {
    /**
     * Visitor executes the on page and change page commands specific
     * to the see details page
     * @param currentPage
     * @param action
     * @param db
     * @param output
     */
    public void visit(final CurrentPage currentPage, final Action action,
                      final Database db, final ArrayNode output) {
        String actionType = action.getType();
        switch (actionType) {
            case "change page" -> {
                if (!action.getPage().equals("movies")
                        && !action.getPage().equals("home")
                        && !action.getPage().equals("upgrades")
                        && !action.getPage().equals("logout")) {
                    ErrorMessage err = ErrorFactory.standardErr();
                    output.addPOJO(err);
                    break;
                }
                if (action.getPage().equals("home")) {
                    db.setCurrMovies(CountryFilter
                            .moviePerms(db.getCurrUser().getCredentials().getCountry(), db));
                    currentPage.resetHomeAUTH();
                    break;
                }
                if (action.getPage().equals("logout")) {
                    db.setCurrUser(null);
                    db.setCurrMovies(new ArrayList<>());
                    currentPage.resetHomeNAUTH();
                    break;
                }
                if (action.getPage().equals("movies")) {
                    db.setCurrMovies(CountryFilter
                            .moviePerms(db.getCurrUser().getCredentials().getCountry(), db));
                    currentPage.resetMovies();
                    User user = UserFactory.createUser(db.getCurrUser());
                    ArrayList<Movie> list = new ArrayList<>();
                    for (Movie movie : db.getCurrMovies()) {
                        list.add(MovieFactory.createMovie(movie));
                    }
                    ErrorMessage err = ErrorFactory
                            .createErr(null, list, user);
                    output.addPOJO(err);
                }

            }
            case "on page" -> {
                final int MIN_RATING = 1;
                final int MAX_RATING = 5;
                final int MOVIE_COST = 2;
                if (!action.getFeature().equals("purchase")
                        && !action.getFeature().equals("watch")
                        && !action.getFeature().equals("like")
                        && !action.getFeature().equals("rate")) {
                    ErrorMessage err = ErrorFactory.standardErr();
                    output.addPOJO(err);
                    break;
                }
                if (action.getFeature().equals("purchase")) {
                    action.setMovie(db.getCurrMovies().get(0).getName());
                    Movie movie = db.getCurrMovies().get(0);
                    if (!action.getMovie().equals(movie.getName())) {
                        ErrorMessage err = ErrorFactory.standardErr();
                        output.addPOJO(err);
                        break;
                    }
                    User user = db.getCurrUser();
                    if (user.getCredentials().getAccountType().equals("premium")) {
                        int aux = user.getNumFreePremiumMovies();
                        if (aux > 0) {
                            aux--;
                            user.setNumFreePremiumMovies(aux);
                            user.getPurchasedMovies().add(movie);
                        } else {
                            if (user.getTokensCount() < MOVIE_COST) {
                                ErrorMessage err = ErrorFactory.standardErr();
                                output.addPOJO(err);
                                break;
                            }
                            user.setTokensCount(user.getTokensCount() - MOVIE_COST);
                            user.getPurchasedMovies().add(movie);
                        }
                    } else {
                        if (user.getTokensCount() < MOVIE_COST) {
                            ErrorMessage err = ErrorFactory.standardErr();
                            output.addPOJO(err);
                            break;
                        }
                        user.setTokensCount(user.getTokensCount() - MOVIE_COST);
                        user.getPurchasedMovies().add(movie);
                    }
                    user = UserFactory.createUser(db.getCurrUser());
                    ArrayList<Movie> list = new ArrayList<>();
                    list.add(MovieFactory.createMovie(movie));
                    ErrorMessage err = ErrorFactory.createErr(null, list, user);
                    output.addPOJO(err);
                    break;
                }
                if (action.getFeature().equals("watch")) {
                    action.setMovie(db.getCurrMovies().get(0).getName());
                    User user = db.getCurrUser();
                    String movie = action.getMovie();
                    Movie watchedMovie = null;
                    boolean found = false;
                    for (Movie movies : user.getPurchasedMovies()) {
                        if (movies.getName().equals(movie)) {
                            watchedMovie = movies;
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        ErrorMessage err = ErrorFactory.standardErr();
                        output.addPOJO(err);
                        break;
                    }
                    user.getWatchedMovies().add(watchedMovie);
                    user = UserFactory.createUser(db.getCurrUser());
                    ArrayList<Movie> list = new ArrayList<>();
                    list.add(MovieFactory.createMovie(watchedMovie));
                    ErrorMessage err = ErrorFactory.createErr(null, list, user);
                    output.addPOJO(err);
                    break;
                }
                if (action.getFeature().equals("like")) {
                    action.setMovie(db.getCurrMovies().get(0).getName());
                    User user = db.getCurrUser();
                    String movie = action.getMovie();
                    Movie likedMovie = null;
                    boolean found = false;
                    for (Movie movies : user.getWatchedMovies()) {
                        if (movies.getName().equals(movie)) {
                            likedMovie = movies;
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        ErrorMessage err = ErrorFactory.standardErr();
                        output.addPOJO(err);
                        break;
                    }
                    user.getLikedMovies().add(likedMovie);
                    likedMovie.setNumLikes(likedMovie.getNumLikes() + 1);
                    user = UserFactory.createUser(db.getCurrUser());
                    ArrayList<Movie> list = new ArrayList<>();
                    list.add(MovieFactory.createMovie(likedMovie));
                    ErrorMessage err = ErrorFactory.createErr(null, list, user);
                    output.addPOJO(err);
                    break;
                }
                if (action.getFeature().equals("rate")) {
                    action.setMovie(db.getCurrMovies().get(0).getName());
                    if (action.getRate() < MIN_RATING || action.getRate() > MAX_RATING) {
                        ErrorMessage err = ErrorFactory.standardErr();
                        output.addPOJO(err);
                        break;
                    }
                    User user = db.getCurrUser();
                    String movie = action.getMovie();
                    Movie ratedMovie = null;
                    boolean found = false;
                    for (Movie movies : user.getWatchedMovies()) {
                        if (movies.getName().equals(movie)) {
                            ratedMovie = movies;
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        ErrorMessage err = ErrorFactory.standardErr();
                        output.addPOJO(err);
                        break;
                    }
                    user.getRatedMovies().add(ratedMovie);
                    ratedMovie.setSumRatings(ratedMovie.getSumRatings() + action.getRate());
                    ratedMovie.setNumRatings(ratedMovie.getNumRatings() + 1);
                    ratedMovie.setRating(ratedMovie.getSumRatings() / ratedMovie.getNumRatings());
                    user = UserFactory.createUser(db.getCurrUser());
                    ArrayList<Movie> list = new ArrayList<>();
                    list.add(MovieFactory.createMovie(ratedMovie));
                    ErrorMessage err = ErrorFactory.createErr(null, list, user);
                    output.addPOJO(err);
                }
            }
            default -> {

            }
        }
    }
}
