import data.*;
import factory.VisitorFactory;

import multiconstructors.MovieConstructor;
import multiconstructors.UserConstructor;
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
import java.util.Objects;

public final class Main {
    static int nrTest = 1;

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
        for (Userio user : inputData.getUsers()) {
            database.getUsers().add(UserConstructor.createUser(user));
        }
        for (Movieio movie : inputData.getMovies()) {
            database.getMovies().add(MovieConstructor.createMovie(movie));
        }
        database.setActions(inputData.getActions());
        for (Action action : database.getActions()) {
            if (!action.getType().equals("database") && !action.getType().equals("back")) {
                Visitor visitor = currentPage.getVisitorColl().get(currentPage.getCurrentVisitor());
                currentPage.accept(visitor, action, database, output);
            } else {
                if (action.getType().equals("back")) {

                    if (database.getCurrUser() == null || database.getUndoStack().isEmpty()) {
                        ErrorMessage err = ErrorMessage.getStdError();
                        output.addPOJO(err);
                        continue;
                    }

                    if (database.getUndoStack().size() == 1) {
                        currentPage.resetHomeAUTH();
                        database.getUndoStack().pop();
                        continue;
                    }

                    database.getUndoStack().pop();
                    ChangePageCommand command = database.getUndoStack().pop();
                    String visitorName = command.getPage();
                    Visitor visitorAux = currentPage.getVisitorColl().get(visitorName);
                    Action actionAux = command.getAction();

                    currentPage.accept(visitorAux, actionAux, database, output);

                    continue;
                }
                switch (action.getFeature()) {
                    case "add" -> {
                        if (database.findMovie(action.getAddedMovie().getName()) != null) {
                            ErrorMessage err = ErrorMessage.getStdError();
                            output.addPOJO(err);
                        } else {
                            Movie movie = MovieConstructor.createMovie(action.getAddedMovie());
                            database.getMovies().add(movie);
                            database.notifyUsersADD(movie);
                        }
                    }
                    case "delete" -> {
                        Movie movie = database.findMovie(action.getDeletedMovie());
                        if (movie == null) {
                            ErrorMessage err = ErrorMessage.getStdError();
                            output.addPOJO(err);
                        } else {
                            database.notifyUsersDELETE(movie);
                            database.deleteMovie(movie);
                        }
                    }
                    default -> {
                        ErrorMessage err = ErrorMessage.getStdError();
                        output.addPOJO(err);
                    }
                }
            }
        }

        if (database.getCurrUser() != null) {
            if (database.getCurrUser().getCredentials().getAccountType().equals("premium")) {
                String recMovieName = database.getRecommendation();
                Notification recommendation;
                recommendation = new Notification(Objects
                        .requireNonNullElse(recMovieName, "No recommendation"), "Recommendation");
                database.getCurrUser().getNotifications().add(recommendation);
                ErrorMessage err = new ErrorMessage.Builder()
                        .error(null)
                        .currentMoviesList(null)
                        .currentUser(database.getCurrUser())
                        .build();
                output.addPOJO(err);
            }
        }

        // Output data finished
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(outPath), output);
        objectWriter.writeValue(new File("checker/resources/out/out_" + nrTest++ + ".json"), output);
    }
}
