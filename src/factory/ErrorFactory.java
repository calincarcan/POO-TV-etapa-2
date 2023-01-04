package factory;

import data.ErrorMessage;
import data.Movie;
import data.User;

import java.util.ArrayList;
public final class ErrorFactory {
    private static ErrorMessage instance = null;
    /**
     * Method creates and returns a singleton standard error message.
     * @return returns a standard error message
     */
    public static ErrorMessage standardErr() {
        if(instance == null) {
            instance = new ErrorMessage();
            instance.setError("Error");
            instance.setCurrentMoviesList(new ArrayList<>());
            instance.setCurrentUser(null);
        }
        return instance;
    }
    /**
     * Method creates and returns a specific error message.
     * @return returns a specific error message
     */
    public static ErrorMessage createErr(final String error,
                                         final ArrayList<Movie> list, final User currentUser) {
        ErrorMessage err = new ErrorMessage();
        err.setError(error);
        ArrayList<Movie> newList;
        if (list != null) {
            newList = new ArrayList<>(list);
        } else {
            newList = null;
        }
        err.setCurrentMoviesList(newList);
        err.setCurrentUser(currentUser);
        return err;
    }
    private ErrorFactory() {

    }
}
