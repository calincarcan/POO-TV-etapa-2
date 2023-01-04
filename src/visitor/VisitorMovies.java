package visitor;

import data.*;
import factory.ErrorFactory;
import factory.MovieFactory;
import factory.UserFactory;
import com.fasterxml.jackson.databind.node.ArrayNode;
import iofiles.Action;
import iofiles.Filters;
import filters.CountryFilter;

import java.util.ArrayList;
import java.util.stream.Collectors;

public final class VisitorMovies implements Visitor {
    /**
     * Method filters the movies after Rating
     *
     * @param filters the filter parameters
     * @param movies  the movies
     * @return the filtered list
     */
    private ArrayList<Movie> filterNoDuration(final Filters filters, ArrayList<Movie> movies) {
        movies = (ArrayList<Movie>) movies.stream()
                .sorted((o1, o2) -> {
                    if (filters.getSort().getRating().equals("decreasing")) {
                        double aux = o2.getRating() - o1.getRating();
                        if (aux > 0) {
                            return 1;
                        }
                        if (aux == 0) {
                            return 0;
                        }
                        return -1;
                    } else {
                        double aux = o1.getRating() - o2.getRating();
                        if (aux > 0) {
                            return 1;
                        }
                        if (aux == 0) {
                            return 0;
                        }
                        return -1;
                    }
                })
                .collect(Collectors.toList());
        return movies;
    }

    /**
     * Method filters the movies after Rating and Duration
     *
     * @param filters the filter parameters
     * @param movies  the movies
     * @return the filtered list
     */
    private ArrayList<Movie> filter(final Filters filters, final ArrayList<Movie> movies) {
        return new ArrayList<>(movies.stream()
                .sorted((o1, o2) -> {
                    if (filters.getSort() != null) {
                        if (filters.getSort().getDuration().equals("decreasing")) {
                            if (filters.getSort().getRating().equals("decreasing")) {
                                if (o2.getDuration() - o1.getDuration() == 0) {
                                    return (int) (o2.getRating() - o1.getRating());
                                } else {
                                    return o2.getDuration() - o1.getDuration();
                                }
                            } else {
                                if (o2.getDuration() - o1.getDuration() == 0) {
                                    return (int) (o1.getRating() - o2.getRating());
                                } else {
                                    return (o2.getDuration() - o1.getDuration());
                                }
                            }
                        } else {
                            if (filters.getSort().getRating().equals("decreasing")) {
                                if (o1.getDuration() - o2.getDuration() == 0) {
                                    return (int) (o2.getRating() - o1.getRating());
                                } else {
                                    return o1.getDuration() - o2.getDuration();
                                }
                            } else {
                                if (o1.getDuration() - o2.getDuration() == 0) {
                                    return (int) (o1.getRating() - o2.getRating());
                                } else {
                                    return (o1.getDuration() - o2.getDuration());
                                }
                            }
                        }
                    }
                    return 0;
                }).toList());
    }

    /**
     * Visitor executes the on page and change page commands specific
     * to the movies page
     *
     * @param currentPage current page
     * @param action      action
     * @param db          database
     * @param output      output
     */
    public void visit(final CurrentPage currentPage, final Action action,
                      final Database db, final ArrayNode output) {
        String actionType = action.getType();
        String pageName = action.getPage();
        switch (actionType) {
            case "change page" -> {
                if (!pageName.equals("see details")
                        && !pageName.equals("logout")
                        && !pageName.equals("movies")) {
                    ErrorMessage err = ErrorFactory.standardErr();
                    output.addPOJO(err);
                    break;
                }
                if (pageName.equals("logout")) {
                    currentPage.resetHomeNAUTH();
                    db.setCurrUser(null);
                    db.setCurrMovies(new ArrayList<>());
                    break;
                }
                Movie details = null;
                for (Movie movie : db.getCurrMovies()) {
                    if (movie.getName().equals(action.getMovie())) {
                        details = movie;
                        break;
                    }
                }
                if (pageName.equals("see details")) {
                    if (details == null) {
                        ErrorMessage err = ErrorFactory.standardErr();
                        output.addPOJO(err);
                        currentPage.resetMovies();
                        break;
                    } else {
                        db.getUndoStack().push(new ChangePageCommand(currentPage.getPageName(), action));

                        currentPage.resetSeeDetails();
                        ArrayList<Movie> errMovie = new ArrayList<>();
                        errMovie.add(MovieFactory.createMovie(details));
                        User user = UserFactory.createUser(db.getCurrUser());
                        db.setCurrMovies(new ArrayList<>());
                        db.getCurrMovies().add(details);
                        ErrorMessage err = ErrorFactory.createErr(null, errMovie, user);
                        output.addPOJO(err);
                        break;
                    }
                }
                if (pageName.equals("movies")) {
                    db.getUndoStack().push(new ChangePageCommand(currentPage.getPageName(), action));

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
                    break;
                }
            }
            case "on page" -> {
                if (!action.getFeature().equals("search")
                        && !action.getFeature().equals("filter")) {
                    ErrorMessage err = ErrorFactory.standardErr();
                    output.addPOJO(err);
                    break;
                }
                if (action.getFeature().equals("search")) {
                    ArrayList<Movie> list = new ArrayList<>();
                    for (Movie movie : db.getCurrMovies()) {
                        if (movie.getName().indexOf(action.getStartsWith()) == 0)
                            list.add(MovieFactory.createMovie(movie));
                    }
                    User errUser = UserFactory.createUser(db.getCurrUser());
                    ErrorMessage err = ErrorFactory.createErr(null, list, errUser);
                    output.addPOJO(err);
                    break;
                }
                if (action.getFeature().equals("filter")) {
                    ArrayList<Movie> list = new ArrayList<>();
                    db.setCurrMovies(CountryFilter
                            .moviePerms(db.getCurrUser().getCredentials().getCountry(), db));
                    for (Movie movie : db.getCurrMovies()) {
                        list.add(MovieFactory.createMovie(movie));
                    }
                    if (action.getFilters().getContains() != null) {
                        // Filter by genre
                        if (action.getFilters().getContains().getGenre() != null) {
                            for (String genre : action.getFilters().getContains().getGenre()) {
                                list.removeIf(movie -> !(movie.getGenres().contains(genre)));
                            }
                        }
                        // Filter by actor
                        if (action.getFilters().getContains().getActors() != null) {
                            for (String actor : action.getFilters().getContains().getActors()) {
                                list.removeIf(movie -> !(movie.getActors().contains(actor)));
                            }
                        }
                    }
                    // Sort by duration and rating
                    if (action.getFilters().getSort() != null) {
                        if (action.getFilters().getSort().getDuration() != null) {
                            list = filter(action.getFilters(), list);
                        } else {
                            list = filterNoDuration(action.getFilters(), list);
                        }
                    }
                    ArrayList<Movie> errList = new ArrayList<>();
                    for (Movie movie : list) {
                        errList.add(MovieFactory.createMovie(movie));
                    }
                    db.setCurrMovies(list);
                    User user = UserFactory.createUser(db.getCurrUser());
                    ErrorMessage err = ErrorFactory.createErr(null, errList, user);
                    output.addPOJO(err);
                }
            }
            default -> {

            }
        }
    }
}
