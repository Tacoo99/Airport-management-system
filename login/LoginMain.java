package login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.image.Image;

import java.util.Objects;

/**
 * Main function to open a new window from an .xml file
 */

public class LoginMain extends Application {

    /**
     * Function that opens a new window with custom stage icon and title
     * @param stage New stage variable
     * @throws Exception Exception handling
     */

    @Override
    public void start(Stage stage) throws Exception{

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Login.fxml")));
        Scene scene = new Scene(root);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.getIcons().add(new Image("/images/airport2_48px.png"));
        stage.setTitle("SKL - zaloguj się");
        stage.setScene(scene);
        stage.show();


    }

    /**
     *  The main () method is ignored in correctly deployed JavaFX application.
     *  main () serves only as fallback in case the application can not be
     *  launched through deployment artifacts, e.g., in IDEs with limited FX
     *  support. IntellJ ignores main().
     *  @param args the command line arguments
     */

    public static void main(String[] args) {
        launch(args);
    }
}
