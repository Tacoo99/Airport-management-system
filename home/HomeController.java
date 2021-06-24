package home;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import login.LoginController;
import utils.ConnectionUtil;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The main function of the Home window displaying a table with current flights and a selection menu
 */

public class HomeController implements Initializable, Serializable {

    /**
     * A table declaration of the TableView type with the name tblData
     */
    @FXML
    private TableView tblData;

    @FXML
    private Label lblRefresh;

    /**
     * variable storing the current connection with the database
     */
    Connection con;


    /**
     * Variables of the double type used to store the coordinates of the window
     */
    double x,y;

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
     *  Function to refresh the contents of the table
     */



    @FXML
    void tblRefresh() {
        tblData.getItems().clear();
        fetRowList();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        lblRefresh.setTextFill(Color.GREEN);
        lblRefresh.setText("Odświeżono tablę: "+ dtf.format(now));
    }

    /**
     * A function that terminates the program
     */

    public void handleExitAction() {

        System.exit(0);
    }

    /**
     * Function that opens a window for selecting the destination city of the flight
     */

    public void handleAddFlightsWindow() {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/add_flight/ChooseTown.fxml"));

                Parent root = loader.load();

                loader.getController();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.getIcons().add(new Image("/images/airport2_48px.png"));
                stage.setTitle("SKL - wybierz zmiasto");
                stage.initStyle(StageStyle.UNDECORATED);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    /**
     * A function that opens a window for communication with currently ongoing flights
     */

    public void handleAComunicateWindow(){

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/communicate/Communicate.fxml"));

                Parent root = loader.load();

                loader.getController();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.getIcons().add(new Image("/images/airport2_48px.png"));
                stage.setTitle("SKL - komunikacja");
                stage.initStyle(StageStyle.UNDECORATED);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        LoginController obj = new LoginController();
        obj.ServerDout("FinishedFlight");
        con = ConnectionUtil.conDB();
        fetColumnList();
        fetRowList();

        
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                tblRefresh();
                obj.ServerDout("FinishedFlight");
            }
        }, 0, 2000);

        timer.cancel();


    }

    /**
     * ObservableList that stores data retrieved from the table
     */
    private ObservableList<ObservableList> data;

    /**
     * Query stored in the variable String displaying all flights
     */

    String SQL = "SELECT * FROM flights";

    /**
     * Fetches column list from table
     */

    private void fetColumnList() {

        try {
            ResultSet rs = con.createStatement().executeQuery(SQL);

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1).toUpperCase());
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));

                tblData.getColumns().removeAll(col);
                tblData.getColumns().addAll(col);

            }

        } catch (Exception e) {
            System.out.println("Błąd " + e.getMessage());

        }
    }

    /**
     * Fetches data/row's from table
     */
    private void fetRowList() {
        data = FXCollections.observableArrayList();
        ResultSet rs;
        try {
            rs = con.createStatement().executeQuery(SQL);

            while (rs.next()) {
                //Iterate Row
                ObservableList row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rs.getString(i));
                }

                data.add(row);

            }

            tblData.setItems(data);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
}

