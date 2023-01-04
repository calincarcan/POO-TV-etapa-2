package factory;

import data.Movie;
import iofiles.Movieio;

import java.util.HashMap;

public final class MovieFactory {
    /**
     * Method creates a copy of a Movie object given a Movie object
     * @param movie movie to copy
     * @return movieCopy
     */
    public static Movie createMovie(final Movie movie) {
        Movie movieCopy = new Movie();
        if (movie != null) {
            movieCopy.setName(movie.getName());
            movieCopy.setYear(movie.getYear());
            movieCopy.setDuration(movie.getDuration());
            movieCopy.setGenres(movie.getGenres());
            movieCopy.setActors(movie.getActors());
            movieCopy.setCountriesBanned(movie.getCountriesBanned());
            movieCopy.setGenres(movie.getGenres());
            movieCopy.setNumLikes(movie.getNumLikes());
            movieCopy.setRating(movie.getRating());
            movieCopy.setNumRatings(movie.getNumRatings());
            return movieCopy;
        }
        return null;
    }

    /**
     * Method creates a Movie object given a Movieio object
     * @param movieio movie to read
     * @return newMovie
     */
    public static Movie createMovie(final Movieio movieio) {
        Movie newMovie = new Movie();
        newMovie.setName(movieio.getName());
        newMovie.setYear(movieio.getYear());
        newMovie.setDuration(movieio.getDuration());
        newMovie.setGenres(movieio.getGenres());
        newMovie.setActors(movieio.getActors());
        newMovie.setCountriesBanned(movieio.getCountriesBanned());
        newMovie.setGenres(movieio.getGenres());
        newMovie.setNumLikes(0);
        newMovie.setRating(0);
        newMovie.setNumRatings(0);
        newMovie.setRatingMap(new HashMap<>());
        return newMovie;
    }
    private MovieFactory() {

    }
}
