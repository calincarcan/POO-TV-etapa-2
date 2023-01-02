package iofiles;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter
@Setter
public final class Input {
    private ArrayList<Userio> users;
    private ArrayList<Movieio> movies;
    private ArrayList<Action> actions;
}
