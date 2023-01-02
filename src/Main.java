import data.CurrentPage;
import data.Database;
import factory.VisitorFactory;

import factory.MovieFactory;
import factory.UserFactory;
import visitor.Visitor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import iofiles.Action;
import iofiles.Input;
import iofiles.Movieio;
import iofiles.Userio;

import java.io.File;
import java.io.IOException;

public final class Main {
    public static void main(String[] args) throws IOException {
        String inPath = args[0];
        String outPath = args[1];
        ObjectMapper objectMapper = new ObjectMapper();
        // Input data received
        Input inputData = objectMapper.readValue(new File(inPath), Input.class);
        ArrayNode output = objectMapper.createArrayNode();
        // Actual code starts here
        CurrentPage currentPage = new CurrentPage();
        // Visitors initialized
        currentPage.getVisitorColl().put("HomeNAUTH", VisitorFactory.createVisitor("HomeNAUTH"));
        currentPage.getVisitorColl().put("HomeAUTH", VisitorFactory.createVisitor("HomeAUTH"));
        currentPage.getVisitorColl().put("movies", VisitorFactory.createVisitor("movies"));
        currentPage.getVisitorColl().put("seeDetails", VisitorFactory.createVisitor("seeDetails"));
        currentPage.getVisitorColl().put("upgrades", VisitorFactory.createVisitor("upgrades"));
        // Database populated
        Database database = new Database();
        for (Userio user: inputData.getUsers()) {
            database.getUsers().add(UserFactory.createUser(user));
        }
        for (Movieio movie: inputData.getMovies()) {
            database.getMovies().add(MovieFactory.createMovie(movie));
        }
        database.setActions(inputData.getActions());
        for (Action action : database.getActions()) {
            Visitor visitor = currentPage.getVisitorColl().get(currentPage.getCurrentVisitor());
            currentPage.accept(visitor, action, database, output);
        }
        // Output data finished
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(outPath), output);
    }
}
