package factory;

import visitor.Visitor;
import visitor.VisitorHomeAUTH;
import visitor.VisitorHomeNAUTH;
import visitor.VisitorMovies;
import visitor.VisitorSeeDetails;
import visitor.VisitorUpgrades;


public final class VisitorFactory {
    /**
     * Method creates visitor object according to the type given
     *
     * @param type type of the visitor to be returned
     * @return specified type visitor
     */
    public static Visitor createVisitor(final String type) {
        if (type == null || type.isEmpty()) {
            return null;
        }
        return switch (type) {
            case "HomeAUTH" -> new VisitorHomeAUTH();
            case "HomeNAUTH" -> new VisitorHomeNAUTH();
            case "movies" -> new VisitorMovies();
            case "seeDetails" -> new VisitorSeeDetails();
            case "upgrades" -> new VisitorUpgrades();
            default -> throw new IllegalArgumentException("Unknown type " + type);
        };
    }

    private VisitorFactory() {
    }
}
