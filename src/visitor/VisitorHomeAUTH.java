package visitor;

import data.*;
import factory.ErrorFactory;
import factory.MovieFactory;
import factory.UserFactory;
import com.fasterxml.jackson.databind.node.ArrayNode;
import filters.CountryFilter;
import iofiles.Action;

import java.util.ArrayList;

public final class VisitorHomeAUTH implements Visitor {
    /**
     * Visitor executes the on page and change page commands specific
     * to the authenticated home page
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
                    ErrorMessage err = ErrorFactory.standardErr();
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

//                    currentPage.resetMovies();
//                    User user = UserFactory.createUser(db.getCurrUser());
//                    ArrayList<Movie> list = new ArrayList<>();
//                    for (Movie movie : db.getCurrMovies()) {
//                        list.add(MovieFactory.createMovie(movie));
//                    }
//                    ErrorMessage err = ErrorFactory
//                            .createErr(null, list, user);
//                    output.addPOJO(err);
//                    break;
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
                if (action.getPage().equals("upgrades")) {
                    db.getUndoStack().push(new ChangePageCommand(currentPage.getPageName(), action));

                    currentPage.resetUpgrades();
                    break;
                }
            }
            case "on page" -> {
                ErrorMessage err = ErrorFactory.standardErr();
                output.addPOJO(err);
            }
            default -> {

            }
        }
    }
}
