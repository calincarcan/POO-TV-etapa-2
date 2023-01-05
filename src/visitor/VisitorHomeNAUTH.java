package visitor;

import data.CurrentPage;
import data.Database;
import data.ErrorMessage;
import data.User;
import multiconstructors.UserConstructor;
import filters.CountryFilter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import iofiles.Action;

import java.util.ArrayList;
import java.util.Stack;

public final class VisitorHomeNAUTH implements Visitor {
    /**
     * Method checks if there is a user present in the database
     * with the same credentials as the ones given by the login action
     * @param action action
     * @param db database
     * @return reference to user if found
     */
    private User checkLogin(final Action action, final Database db) {
        String loginName = action.getCredentials().getName();
        String loginPassword = action.getCredentials().getPassword();
        for (User user : db.getUsers()) {
            if (user.getCredentials().getName().equals(loginName)
                    && user.getCredentials().getPassword().equals(loginPassword)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Visitor executes the on page and change page commands specific
     * to the not authenticated home page
     * @param currentPage current page
     * @param action action
     * @param db database
     * @param output output
     */
    public void visit(final CurrentPage currentPage, final Action action,
                      final Database db, final ArrayNode output) {
        String actionType = action.getType();
        switch (actionType) {
            case "change page" -> {
                if (!action.getPage().equals("login")
                        && !action.getPage().equals("register")) {
                    ErrorMessage err = ErrorMessage.getStdError();
                    output.addPOJO(err);
                    currentPage.resetHomeNAUTH();
                    break;
                }
                currentPage.setPageName(action.getPage());
            }
            case "on page" -> {
                if (action.getFeature().equals("login")) {
                    User foundUser = checkLogin(action, db);
                    if (!action.getFeature().equals("login") || foundUser == null) {
                        ErrorMessage err = ErrorMessage.getStdError();
                        output.addPOJO(err);
                        currentPage.resetHomeNAUTH();
                        break;
                    } else {
                        db.setUndoStack(new Stack<>());

                        currentPage.resetHomeAUTH();
                        db.setCurrUser(foundUser);

                        User errUser = UserConstructor.createUser(foundUser);
                        db.setCurrMovies(CountryFilter
                                .moviePerms(foundUser.getCredentials().getCountry(), db));
                        ErrorMessage err = new ErrorMessage.Builder()
                                .error(null)
                                .currentMoviesList(new ArrayList<>())
                                .currentUser(errUser)
                                .build();
                        output.addPOJO(err);
                        break;
                    }
                }
                if (action.getFeature().equals("register")) {
                    if (!action.getFeature().equals("register")) {
                        ErrorMessage err = ErrorMessage.getStdError();
                        output.addPOJO(err);
                        currentPage.resetHomeNAUTH();
                        break;
                    }
                    db.setUndoStack(new Stack<>());

                    User newUser = UserConstructor.createUser(action.getCredentials());
                    db.getUsers().add(newUser);

                    db.setCurrMovies(CountryFilter
                            .moviePerms(newUser.getCredentials().getCountry(), db));
                    currentPage.resetHomeAUTH();
                    db.setCurrUser(newUser);

                    User errUser = UserConstructor.createUser(newUser);
                    ErrorMessage err = new ErrorMessage.Builder()
                            .error(null)
                            .currentMoviesList(new ArrayList<>())
                            .currentUser(errUser)
                            .build();
                    output.addPOJO(err);
                }
            }
            default -> {

            }
        }
    }
}
