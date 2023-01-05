package visitor;

import data.Movie;
import data.ErrorMessage;
import data.User;
import data.Database;
import data.ChangePageCommand;
import data.CurrentPage;

import multiconstructors.MovieConstructor;
import multiconstructors.UserConstructor;
import filters.CountryFilter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import iofiles.Action;

import java.util.ArrayList;
import java.util.Map;

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
                        && !action.getPage().equals("upgrades")
                        && !action.getPage().equals("logout")) {
                    ErrorMessage err = ErrorMessage.getStdError();
                    output.addPOJO(err);
                    break;
                }
                if (action.getPage().equals("logout")) {
                    db.setCurrUser(null);
                    db.setCurrMovies(new ArrayList<>());
                    currentPage.resetHomeNAUTH();
                    break;
                }
                if (action.getPage().equals("movies")) {
                    db.getUndoStack().push(new ChangePageCommand(currentPage.getPageName(), action));

                    db.setCurrMovies(CountryFilter
                            .moviePerms(db.getCurrUser().getCredentials().getCountry(), db));
                    currentPage.resetMovies();
                    User user = UserConstructor.createUser(db.getCurrUser());
                    ArrayList<Movie> list = new ArrayList<>();
                    for (Movie movie : db.getCurrMovies()) {
                        list.add(MovieConstructor.createMovie(movie));
                    }
                    ErrorMessage err = new ErrorMessage.Builder()
                            .error(null)
                            .currentMoviesList(list)
                            .currentUser(user)
                            .build();

                    output.addPOJO(err);
                }
                if (action.getPage().equals("upgrades")) {
                    db.getUndoStack().push(new ChangePageCommand(currentPage.getPageName(), action));

                    currentPage.resetUpgrades();
                    break;
                }
            }
            case "on page" -> {
                final int minRating = 1;
                final int maxRating = 5;
                final int movieCost = 2;
                if (!action.getFeature().equals("purchase")
                        && !action.getFeature().equals("watch")
                        && !action.getFeature().equals("like")
                        && !action.getFeature().equals("subscribe")
                        && !action.getFeature().equals("rate")) {
                    ErrorMessage err = ErrorMessage.getStdError();
                    output.addPOJO(err);
                    break;
                }
                if (action.getFeature().equals("subscribe")) {
                    ArrayList<String> currMovieGenres = db.getCurrMovies().get(0).getGenres();
                    ArrayList<String> currUserGenres = db.getCurrUser().getSubscribedGenres();
                    if (!currMovieGenres.contains(action.getSubscribedGenre())
                            || currUserGenres.contains(action.getSubscribedGenre())) {
                        ErrorMessage err = ErrorMessage.getStdError();
                        output.addPOJO(err);
                        break;
                    }
                    db.getCurrUser().getSubscribedGenres().add(action.getSubscribedGenre());
                    break;
                }
                if (action.getFeature().equals("purchase")) {
                    action.setMovie(db.getCurrMovies().get(0).getName());
                    Movie movie = db.getCurrMovies().get(0);
                    if (!action.getMovie().equals(movie.getName())) {
                        ErrorMessage err = ErrorMessage.getStdError();
                        output.addPOJO(err);
                        break;
                    }
                    User user = db.getCurrUser();

                    if (user.getPurchasedMovies().contains(movie)) {
                        ErrorMessage err = ErrorMessage.getStdError();
                        output.addPOJO(err);
                        break;
                    }

                    if (user.getCredentials().getAccountType().equals("premium")) {
                        int aux = user.getNumFreePremiumMovies();
                        if (aux > 0) {
                            aux--;
                            user.setNumFreePremiumMovies(aux);
                            user.getPurchasedMovies().add(movie);
                        } else {
                            if (user.getTokensCount() < movieCost) {
                                ErrorMessage err = ErrorMessage.getStdError();
                                output.addPOJO(err);
                                break;
                            }
                            user.setTokensCount(user.getTokensCount() - movieCost);
                            user.getPurchasedMovies().add(movie);
                        }
                    } else {
                        if (user.getTokensCount() < movieCost) {
                            ErrorMessage err = ErrorMessage.getStdError();
                            output.addPOJO(err);
                            break;
                        }
                        user.setTokensCount(user.getTokensCount() - movieCost);
                        user.getPurchasedMovies().add(movie);
                    }
                    user = UserConstructor.createUser(db.getCurrUser());
                    ArrayList<Movie> list = new ArrayList<>();
                    list.add(MovieConstructor.createMovie(movie));
                    ErrorMessage err = new ErrorMessage.Builder()
                            .error(null)
                            .currentMoviesList(list)
                            .currentUser(user)
                            .build();
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
                        ErrorMessage err = ErrorMessage.getStdError();
                        output.addPOJO(err);
                        break;
                    }

                    if (!user.getWatchedMovies().contains(watchedMovie)) {
                        user.getWatchedMovies().add(watchedMovie);
                    }

                    user = UserConstructor.createUser(db.getCurrUser());
                    ArrayList<Movie> list = new ArrayList<>();
                    list.add(MovieConstructor.createMovie(watchedMovie));
                    ErrorMessage err = new ErrorMessage.Builder()
                            .error(null)
                            .currentMoviesList(list)
                            .currentUser(user)
                            .build();
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
                        ErrorMessage err = ErrorMessage.getStdError();
                        output.addPOJO(err);
                        break;
                    }

                    if (!user.getLikedMovies().contains(likedMovie)) {
                        user.getLikedMovies().add(likedMovie);
                        for (String genre : likedMovie.getGenres()) {
                            if (user.getLikeMap().containsKey(genre)) {
                                user.getLikeMap().put(genre, user.getLikeMap().get(genre) + 1);
                            } else {
                                user.getLikeMap().put(genre, 1);
                            }
                        }
                        likedMovie.setNumLikes(likedMovie.getNumLikes() + 1);
                    }

                    user = UserConstructor.createUser(db.getCurrUser());
                    ArrayList<Movie> list = new ArrayList<>();
                    list.add(MovieConstructor.createMovie(likedMovie));
                    ErrorMessage err = new ErrorMessage.Builder()
                            .error(null)
                            .currentMoviesList(list)
                            .currentUser(user)
                            .build();
                    output.addPOJO(err);
                    break;
                }
                if (action.getFeature().equals("rate")) {
                    action.setMovie(db.getCurrMovies().get(0).getName());

                    if (action.getRate() < minRating || action.getRate() > maxRating) {
                        ErrorMessage err = ErrorMessage.getStdError();
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
                        ErrorMessage err = ErrorMessage.getStdError();
                        output.addPOJO(err);
                        break;
                    }

                    ratedMovie.getRatingMap().put(user, action.getRate());
                    double sum = 0;
                    for (Map.Entry<User, Integer> entry : ratedMovie.getRatingMap().entrySet()) {
                        sum +=entry.getValue();
                    }
                    ratedMovie.setRating(sum/ratedMovie.getRatingMap().size());
                    ratedMovie.setNumRatings(ratedMovie.getRatingMap().size());

                    if (!user.getRatedMovies().contains(ratedMovie)) {
                        user.getRatedMovies().add(ratedMovie);
                    }

                    user = UserConstructor.createUser(db.getCurrUser());
                    ArrayList<Movie> list = new ArrayList<>();
                    list.add(MovieConstructor.createMovie(ratedMovie));
                    ErrorMessage err = new ErrorMessage.Builder()
                            .error(null)
                            .currentMoviesList(list)
                            .currentUser(user)
                            .build();
                    output.addPOJO(err);
                }
            }
            default -> {

            }
        }
    }
}
