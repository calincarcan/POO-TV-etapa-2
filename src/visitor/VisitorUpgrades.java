package visitor;

import data.*;
import multiconstructors.MovieConstructor;
import multiconstructors.UserConstructor;
import filters.CountryFilter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import iofiles.Action;

import java.util.ArrayList;

public final class VisitorUpgrades implements Visitor {
    /**
     * Visitor executes the on page and change page commands specific
     * to the upgrades page
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
                if (!action.getPage().equals("movies")
                        && !action.getPage().equals("logout")) {
                    ErrorMessage err = ErrorMessage.getStdError();
                    output.addPOJO(err);
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
                    break;
                }
                currentPage.resetHomeNAUTH();
                db.setCurrUser(null);
                db.setCurrMovies(new ArrayList<>());
            }
            case "on page" -> {
                final int premiumCost = 10;
                if (!action.getFeature().equals("buy tokens")
                        && !action.getFeature().equals("buy premium account")) {
                    ErrorMessage err = ErrorMessage.getStdError();
                    output.addPOJO(err);
                    break;
                }
                if (action.getFeature().equals("buy tokens")) {
                    User user = db.getCurrUser();
                    int balance = Integer.parseInt(user.getCredentials().getBalance());
                    int count = Integer.parseInt(action.getCount());
                    if (balance < count) {
                        ErrorMessage err = ErrorMessage.getStdError();
                        output.addPOJO(err);
                        break;
                    }
                    balance -= count;
                    user.getCredentials().setBalance(Integer.toString(balance));
                    user.setTokensCount(user.getTokensCount() + count);
                    break;
                }
                User user = db.getCurrUser();
                int tokens = user.getTokensCount();
                if (tokens < premiumCost) {
                    ErrorMessage err = ErrorMessage.getStdError();
                    output.addPOJO(err);
                    break;
                }
                user.setTokensCount(user.getTokensCount() - premiumCost);
                user.getCredentials().setAccountType("premium");
            }
            default -> {

            }
        }
    }
}
