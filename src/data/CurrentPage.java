package data;

import visitor.Visitor;
import com.fasterxml.jackson.databind.node.ArrayNode;
import iofiles.Action;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Setter
@Getter
public final class CurrentPage {
    private String pageName;
    private String currentVisitor;
    private HashMap<String, Visitor> visitorColl;

    /**
     * Constructor initializes the current page and the visitor to the not authenticated home page
     */
    public CurrentPage() {
        setPageName("HomeNAUTH");
        setCurrentVisitor("HomeNAUTH");
        setVisitorColl(new HashMap<>());
    }
    /**
     * Method sets the current page and the visitor to the upgrade page
     */
    public void resetUpgrades() {
        this.setPageName("upgrades");
        this.setCurrentVisitor("upgrades");
    }
    /**
     * Method sets the current page and the visitor to the see details page
     */
    public void resetSeeDetails() {
        this.setPageName("seeDetails");
        this.setCurrentVisitor("seeDetails");
    }
    /**
     * Method sets the current page and the visitor to the movies page
     */
    public void resetMovies() {
        this.setPageName("movies");
        this.setCurrentVisitor("movies");
    }
    /**
     * Method sets the current page and the visitor to the not authenticated home page
     */
    public void resetHomeNAUTH() {
        this.setPageName("HomeNAUTH");
        this.setCurrentVisitor("HomeNAUTH");
    }
    /**
     * Method sets the current page and the visitor to the authenticated home page
     */
    public void resetHomeAUTH() {
        this.setPageName("HomeAUTH");
        this.setCurrentVisitor("HomeAUTH");
    }
    /**
     * Method accepts and executes the code inside the given visitor
     */
    public void accept(final Visitor visitor, final Action action,
                       final Database db, final ArrayNode output) {
        visitor.visit(this, action, db, output);
    }
}
