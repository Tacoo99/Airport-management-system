package add_flight;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;


/**
 * The main class responsible for displaying the window with the selection of the flight destination city
 */
public class ChooseTownController {

    @FXML
    private Label lbl_close;

    @FXML
    private Label lblTownStatus_1;

    @FXML
    private Label Zagrzeb;

    @FXML
    private Label Moskwa;

    @FXML
    private Label Berlin;

    @FXML
    private Label Tokyo;

    @FXML
    private Label Nowy_Jork;

    @FXML
    private Label Warszawa;

    @FXML
    private Label Oslo;

    @FXML
    private ImageView MoskwaImg;

    @FXML
    private ImageView ZagrzebImg;

    @FXML
    private ImageView BerlinImg;

    @FXML
    private ImageView Nowy_JorkImg;

    @FXML
    private ImageView TokyoImg;

    @FXML
    private ImageView WarszawaImg;

    @FXML
    private ImageView OsloImg;


    @FXML
    private double x, y;

    @FXML
    private String town;


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
     * The function closes the current window and opens a new one, depending on the correctness of the selected city
     * @param event Retrieving the action source, scene and window
     */
    @FXML
    public void SaveExitTown(MouseEvent event){


        if(town == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setContentText("Kliknij OK i wybierz miasto!");
            alert.show();
        }

        else {

            Stage StageTown = (Stage) ((Node) event.getSource()).getScene().getWindow();
            StageTown.close();

            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/add_flight/Add_Flight.fxml"));
                Parent root = loader.load();

                AddFlightController scene2Controller = loader.getController();

                scene2Controller.TransferTown(town);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("SKL - dodaj lot");
                stage.getIcons().add(new Image("/images/airport2_48px.png"));
                stage.initStyle(StageStyle.UNDECORATED);
                stage.show();


            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    private void SetTownImg(ImageView Town_Img){
        Town_Img.setImage(new Image("file:src/images/building_green_30px.png"));
    }

    /**
     * Function that takes the selected city name into a variable,
     * references the TownSelector function with the string Town parameter, and changes the proper city icon to green
     */

    @FXML
    public void SelectMoskwa(){
        town = Moskwa.getText();
        restartTown();
        TownSelector(town);
        SetTownImg(MoskwaImg);
    }

    /**
     * Function that takes the selected city name into a variable,
     * references the TownSelector function with the string Town parameter, and changes the proper city icon to green
     */

    public void SelectZagrzeb() {
        town = Zagrzeb.getText();
        restartTown();
        TownSelector(town);
        SetTownImg(ZagrzebImg);
    }

    /**
     * Function that takes the selected city name into a variable,
     * references the TownSelector function with the string Town parameter, and changes the proper city icon to green
     */

    public void SelectBerlin() {
       town = Berlin.getText();
        restartTown();
        TownSelector(town);
        SetTownImg(BerlinImg);
    }

    /**
     * Function that takes the selected city name into a variable,
     * references the TownSelector function with the string Town parameter, and changes the proper city icon to green
     */

    public void SelectNowy_Jork() {
        town = Nowy_Jork.getText();
        restartTown();
        TownSelector(town);
        SetTownImg(Nowy_JorkImg);
    }

    /**
     * Function that takes the selected city name into a variable,
     * references the TownSelector function with the string Town parameter, and changes the proper city icon to green
     */

    public void SelectTokyo() {
        town = Tokyo.getText();
        restartTown();
        TownSelector(town);
        SetTownImg(TokyoImg);
    }

    /**
     * Function that takes the selected city name into a variable,
     * references the TownSelector function with the string Town parameter, and changes the proper city icon to green
     */

    public void SelectWarszawa() {
        town = Warszawa.getText();
        restartTown();
        TownSelector(town);
        SetTownImg(WarszawaImg);
    }

    /**
     * Function that takes the selected city name into a variable,
     * references the TownSelector function with the string Town parameter, and changes the proper city icon to green
     */

    public void SelectOslo() {
        town = Oslo.getText();
        restartTown();
        TownSelector(town);
        SetTownImg(OsloImg);
    }


    /**
     * The function resets the string town variable, label with the status,
     * changes the icons and inscriptions of cities to red
     */

    public void lblRefresh() {
        town = null;
        lblTownStatus_1.setText("Status:");
        restartTown();
        LblRestart();
    }


    /**
     * Function that sets the color of the Label text to green
     * @param TextTown Variable that holds the Label identifier
     */
    private void SetTextTown(Label TextTown){
        TextTown.setTextFill(Color.GREEN);
    }

    /**
     * The function is responsible for selecting a city, highlighting its name and icon in green
     * @param SelectedTown A variable that stores the name of the city
     */
    private void TownSelector(String SelectedTown) {
        lblTownStatus_1.setTextFill(Color.GREEN);
        lblTownStatus_1.setText("Status: Wybrano miasto - " + SelectedTown);
        LblRestart();

        Map <String, Runnable> codes = Map.of(
                Moskwa.getText(), ()-> SetTextTown(Moskwa),
                Berlin.getText(), ()-> SetTextTown(Berlin),
                Zagrzeb.getText(), ()-> SetTextTown(Zagrzeb),
                Nowy_Jork.getText(), ()-> SetTextTown(Nowy_Jork),
               Tokyo.getText(), ()-> SetTextTown(Tokyo),
                Warszawa.getText(), ()-> SetTextTown(Warszawa),
                Oslo.getText(), ()-> SetTextTown(Oslo)
        );

        codes.get(SelectedTown).run();


    }

    private void restartTown(){

        for (ImageView imageView : Arrays.asList(MoskwaImg, ZagrzebImg, BerlinImg,
                Nowy_JorkImg, TokyoImg, WarszawaImg, OsloImg)) {
            imageView.setImage(new Image("file:src/images/building_30px_red.png"));
        }
    }

    private void LblRestart(){

        for (Label label : Arrays.asList(Moskwa, Zagrzeb, Berlin, Nowy_Jork,
                Tokyo, Warszawa, Oslo)) {
            label.setTextFill(Color.TOMATO);
        }
    }


}

