package visitor;

import data.*;
import multiconstructors.MovieConstructor;
import multiconstructors.UserConstructor;
import com.fasterxml.jackson.databind.node.ArrayNode;
import filters.CountryFilter;
import iofiles.Action;

import java.util.ArrayList;

public final class VisitorHomeAUTH implements Visitor {
    /**
     * Visitor executes the on page and change page commands specific
     * to the authenticated home page
     *
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
                        && !action.getPage().equals("logout")
                        && !action.getPage().equals("upgrades")) {
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
                    db.getUndoStack()
                            .push(new ChangePageCommand(currentPage.getPageName(), action));
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
                    break;
                }
                if (action.getPage().equals("upgrades")) {
                    db.getUndoStack()
                            .push(new ChangePageCommand(currentPage.getPageName(), action));

                    currentPage.resetUpgrades();
                }
            }
            case "on page" -> {
                ErrorMessage err = ErrorMessage.getStdError();
                output.addPOJO(err);
            }
            default -> {

            }
        }
    }
}
