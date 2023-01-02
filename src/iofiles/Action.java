package iofiles;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Action {
    private String type;
    private String page;
    private String movie;
    private String feature;
    private Credentials credentials;
    private String startsWith;
    private String count;
    private int rate;
    private Filters filters;
}
