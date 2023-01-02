package data;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Notification {
    private String movieName;
    private String message;

    public Notification(String movieName, String message) {
        this.movieName = movieName;
        this.message = message;
    }
}
