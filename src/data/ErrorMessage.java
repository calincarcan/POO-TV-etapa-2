package data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Setter
@Getter
public class ErrorMessage {
    private String error;
    private ArrayList<Movie> currentMoviesList;
    private User currentUser;
}
