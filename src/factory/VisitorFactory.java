package factory;
import visitor.*;

public final class VisitorFactory {
    public static Visitor createVisitor(String type)
    {
        if (type == null || type.isEmpty())
            return null;
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
