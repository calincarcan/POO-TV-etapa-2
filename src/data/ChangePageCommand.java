package data;

import iofiles.Action;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangePageCommand {
    String page;
    Action action;

    public ChangePageCommand(final String page, final Action action) {
        this.page = page;
        this.action = action;
    }
}
