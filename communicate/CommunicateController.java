package communicate;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import login.LoginController;

import java.net.URL;

import java.util.ResourceBundle;

/**
 * A class that displays a window enabling basic communication with the currently running flights
 */

public class CommunicateController implements Initializable {

    @FXML
    private Label lbl_close;

    @FXML
    private ComboBox FlyID;

    @FXML
    private TextField FlyMessage;

    private double x,y;



    /**
     * The function responsible for moving the program's dialog window
     * @param event Getting the x, y coordinates from the place where the function is called
     */
    @FXML
    public void dragged(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);

    }

    /**
     * A function that gets the x, y coordinates of the current scene in the program
     * @param event Get the x, y coordinates of the scene
     */
    @FXML
    public void pressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    /**
     * A function that minimizes the current program window
     * @param event Extracting the source, scene and action window
     */

    @FXML
    public void minimize(MouseEvent event){
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    /**
     * The function closes the current program window
     */
    @FXML
    public void handleCloseWindow() {
        Stage stage = (Stage) lbl_close.getScene().getWindow();
        stage.close();
    }

    /**
     * The function starts with the opening of the window, sending the code to the server, receiving the Object [] flight list and entering it into the ComboBox named FlyID
     */

    public void initialize(URL url, ResourceBundle rb) {

        //Nowy obiekt klasy LoginController
        LoginController obj = new LoginController();

        //Wysyłam kod do serwera
        obj.ServerDout("Communicate");

        Object[] arraylist = (Object[]) obj.ServerDinObject();

        for (Object o : arraylist) {

            FlyID.getItems().add(o);
        }

    }

    /**
     * The function closes the communication window by displaying an appropriate alert depending on the fulfilled condition
     * @param event Retrieve the current scene and the window for closing
     */

    public void SaveExitCommunicate(MouseEvent event)
    {

        if(FlyID.getSelectionModel().isEmpty() || FlyMessage.getText().isEmpty()){

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Uzupełnij wszystkie pola");
            alert.show();

        }

        else {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setTitle("Sukces");
            alert.setHeaderText("Twoja wiadomość została wysłana");
            alert.show();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        }
    }


}
