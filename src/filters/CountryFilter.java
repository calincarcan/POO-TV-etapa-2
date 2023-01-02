package filters;

import data.Database;
import data.Movie;

import java.util.ArrayList;

public final class CountryFilter {
    /**
     * Method parses the whole movie database and returns a list containing all the movies that
     * can be viewed by a user from a given country
     * @param country country permissions
     * @param db database
     * @return allowedMovieList
     */
    public static ArrayList<Movie> moviePerms(final String country, final Database db) {
        ArrayList<Movie> allMovies = db.getMovies();
        ArrayList<Movie> allowedMovieList = new ArrayList<>();
        for (Movie movie : allMovies) {
            if (!movie.getCountriesBanned().contains(country)) {
                allowedMovieList.add(movie);
            }
        }
        return allowedMovieList;
    }

    private CountryFilter() {

    }
}
