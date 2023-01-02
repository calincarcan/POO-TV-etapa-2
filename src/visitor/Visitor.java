package visitor;

import data.CurrentPage;
import data.Database;
import com.fasterxml.jackson.databind.node.ArrayNode;
import iofiles.Action;

public interface Visitor {
    void visit(CurrentPage currentPage, Action action, Database db, ArrayNode output);
}
