package data;

import multiconstructors.MovieConstructor;
import multiconstructors.UserConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public final class ErrorMessage {
    private static ErrorMessage stdError = null;
    private String error;
    private ArrayList<Movie> currentMoviesList;
    private User currentUser;

    /**
     * Method returns a reference to a lazy singleton implementation of the standard error
     *
     * @return standard error
     */
    public static ErrorMessage getStdError() {
        if (stdError == null) {
            stdError = new Builder()
                    .error("Error")
                    .currentMoviesList(new ArrayList<>())
                    .currentUser(null)
                    .build();
        }
        return stdError;
    }

    private ErrorMessage(final Builder builder) {
        this.error = builder.error;
        this.currentMoviesList = builder.currentMoviesList;
        this.currentUser = builder.currentUser;
    }

    public static final class Builder {
        private String error;
        private ArrayList<Movie> currentMoviesList;
        private User currentUser;

        /**
         * Method builds the error message
         *
         * @param err error message
         * @return builder with error message
         */
        public Builder error(final String err) {
            this.error = err;
            return this;
        }

        /**
         * Method builds the movie list
         *
         * @param list movie list
         * @return builder with movie list
         */
        public Builder currentMoviesList(final ArrayList<Movie> list) {
            if (list != null) {
                this.currentMoviesList = new ArrayList<>();
                for (Movie movie : list) {
                    this.currentMoviesList.add(MovieConstructor.createMovie(movie));
                }
            } else {
                this.currentMoviesList = null;
            }
            return this;
        }

        /**
         * Method builds the user
         *
         * @param user user
         * @return builder with user
         */
        public Builder currentUser(final User user) {
            if (user != null) {
                this.currentUser = UserConstructor.createUser(user);
            } else {
                this.currentUser = null;
            }
            return this;
        }

        /**
         * Method builds the error message
         *
         * @return ErrorMessage
         */
        public ErrorMessage build() {
            return new ErrorMessage(this);
        }
    }
}
