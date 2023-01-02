package visitor;

import data.Database;
import data.User;
import data.CurrentPage;
import data.Movie;
import data.ErrorMessage;
import factory.ErrorFactory;
import factory.MovieFactory;
import factory.UserFactory;
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
                        && !action.getPage().equals("home")
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
                    break;
                }
                currentPage.resetHomeNAUTH();
                db.setCurrUser(null);
                db.setCurrMovies(new ArrayList<>());
            }
            case "on page" -> {
                final int PREMIUM_COST = 10;
                if (!action.getFeature().equals("buy tokens")
                        && !action.getFeature().equals("buy premium account")) {
                    ErrorMessage err = ErrorFactory.standardErr();
                    output.addPOJO(err);
                    break;
                }
                if (action.getFeature().equals("buy tokens")) {
                    User user = db.getCurrUser();
                    int balance = Integer.parseInt(user.getCredentials().getBalance());
                    int count = Integer.parseInt(action.getCount());
                    if (balance < count) {
                        ErrorMessage err = ErrorFactory.standardErr();
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
                if (tokens < PREMIUM_COST) {
                    ErrorMessage err = ErrorFactory.standardErr();
                    output.addPOJO(err);
                    break;
                }
                user.setTokensCount(user.getTokensCount() - PREMIUM_COST);
                user.getCredentials().setAccountType("premium");
            }
            default -> {

            }
        }
    }
}
