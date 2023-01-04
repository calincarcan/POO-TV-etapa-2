package data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import iofiles.Credentials;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

@Setter
@Getter
public class User {
    private Credentials credentials;
    private int tokensCount;
    private int numFreePremiumMovies;
    private ArrayList<Movie> purchasedMovies;
    private ArrayList<Movie> watchedMovies;
    private ArrayList<Movie> likedMovies;
    private ArrayList<Movie> ratedMovies;
    private ArrayList<Notification> notifications;
    @JsonIgnore
    private ArrayList<String> subscribedGenres;
    @JsonIgnore
    private HashMap<String, Integer> likeMap = new HashMap<>();

}
