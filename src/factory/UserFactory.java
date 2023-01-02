package factory;

import data.Movie;
import data.User;
import iofiles.Credentials;
import iofiles.Userio;

import java.util.ArrayList;

public final class UserFactory {
    /**
     * Method creates a copy of a User object given a User object
     * @param user user to copy
     * @return copyUser
     */
    public static User createUser(final User user) {
        User copyUser = new User();
        copyUser.setCredentials(CredentialsFactory.createCred(user.getCredentials()));
        copyUser.setTokensCount(user.getTokensCount());
        copyUser.setNumFreePremiumMovies(user.getNumFreePremiumMovies());
        ArrayList<Movie> auxList = new ArrayList<>();
        for (Movie movie : user.getPurchasedMovies()) {
            auxList.add(MovieFactory.createMovie(movie));
        }
        copyUser.setPurchasedMovies(auxList);
        auxList = new ArrayList<>();
        for (Movie movie : user.getWatchedMovies()) {
            auxList.add(MovieFactory.createMovie(movie));
        }
        copyUser.setWatchedMovies(auxList);
        auxList = new ArrayList<>();
        for (Movie movie : user.getLikedMovies()) {
            auxList.add(MovieFactory.createMovie(movie));
        }
        copyUser.setLikedMovies(auxList);
        auxList = new ArrayList<>();
        for (Movie movie : user.getRatedMovies()) {
            auxList.add(MovieFactory.createMovie(movie));
        }
        copyUser.setRatedMovies(auxList);
        return copyUser;
    }

    /**
     * Method creates a User object given a Userio object
     * @param userio user to read
     * @return newUser
     */
    public static User createUser(final Userio userio) {
        User newUser = new User();
        newUser.setCredentials(userio.getCredentials());
        newUser.setTokensCount(0);
        newUser.setNumFreePremiumMovies(15);
        newUser.setPurchasedMovies(new ArrayList<>());
        newUser.setWatchedMovies(new ArrayList<>());
        newUser.setLikedMovies(new ArrayList<>());
        newUser.setRatedMovies(new ArrayList<>());
        return newUser;
    }

    /**
     * Method creates a User object with default values for given credentials
     * @param credentials credentials to read
     * @return newUser
     */
    public static User createUser(final Credentials credentials) {
        User newUser = new User();
        newUser.setCredentials(credentials);
        newUser.setTokensCount(0);
        newUser.setNumFreePremiumMovies(15);
        newUser.setPurchasedMovies(new ArrayList<>());
        newUser.setWatchedMovies(new ArrayList<>());
        newUser.setLikedMovies(new ArrayList<>());
        newUser.setRatedMovies(new ArrayList<>());
        return newUser;
    }
    private UserFactory() {

    }
}
