package add_flight;

import java.io.IOException;
import java.sql.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTimePicker;
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
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import login.LoginController;
import utils.ConnectionUtil;

/**
 * Class displaying a window for adding a flight and previewing planned/ongoing flights
 */

public class AddFlightController implements Initializable {


    @FXML
    private JFXTimePicker txtHour;

    @FXML
    private TextField ArrivalTime;

    @FXML
    private Label lbl_close;

    @FXML
    private Label lblRefresh;

    @FXML
    private TextField txtFlyHigh;

    @FXML
    private TextField txtDirection;

    @FXML
    private ComboBox<String> txtPlaneName;

    @FXML
    private TextField txtTown;

    @FXML
    private TextField txtTime;

    @FXML
    private JFXDatePicker txtDate;


    @FXML
    private ComboBox<String> txtStartLine;

    @FXML
    private TextField txtPlaneID;

    @FXML
    private Label lblStatus;

    @FXML
    private TableView tblData;


    private double x,y;

    /**
     * A variable that stores the value true/false depending on the correctness of the entered flight altitude
     */
    Boolean CheckFlyHigh;


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
     * Function that sends the selected city to another scene
     * @param Town A String variable that stores the name of the city
     */
    public void TransferTown(String Town) {
        txtTown.setText(Town);
    }

    /**
     * New LoginController class object
     */
    LoginController obj = new LoginController();

    /**
     * A function that takes the name of the plane and city into a variable, sends it to the server and receives ready results
     */

    @FXML
    private void CalcTown() {

        if (!txtPlaneName.getSelectionModel().isEmpty() || (!(txtHour.getValue() == null)) ) {

            //Zapisuję nazwę samolotu oraz miasta do zmiennych typu String
            String PlaneName = txtPlaneName.getValue();
            String TownName = txtTown.getText();


            //Wysyłam kod do serwera
            obj.ServerDout("AddFlight");


            //Wysyłam nazwę samolotu do serwera
            obj.ServerDout(PlaneName);

            //Wysyłam nazwę miasta do serwera
            obj.ServerDout(TownName);

            if (!(txtHour.getValue() == null)){

                //Wysyłam wartość true do serwera - wybrano godzinę lotu
                obj.ServerDoutBoolean(true);


                //Wysyłam godzinę lotu do serwera
                obj.ServerDout(String.valueOf(txtHour.getValue()));


                //Odbieram czas lotu od serwera
                txtTime.setText(obj.ServerDinString());


                //Odbieram czas przylotu od serwera
                ArrivalTime.setText(obj.ServerDinString());
            }



            else{

                obj.ServerDoutBoolean(false);

            }

        }
    }

    /**
     * Opening a new window - selecting the destination city
     * @param event Fetching the current scene and window
     */

    @FXML
    private void ChooseTown(MouseEvent event) {

            Stage StageTown = (Stage) ((Node) event.getSource()).getScene().getWindow();
            StageTown.close();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/add_flight/ChooseTown.fxml"));

                Parent root = loader.load();

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
     * variable storing the current connection with the database
     */
    Connection connection;

    @Override
    public void initialize(URL url, ResourceBundle rb) {


        connection = ConnectionUtil.conDB();

        txtPlaneName.getItems().addAll("Boeing", "Concord", "AirBus");

        txtStartLine.getItems().addAll("1", "2", "3", "4");

        fetColumnList();
        fetRowList();

    }

    /**
     * A function that downloads entered flight data, sends it to the server and receives ready results
     */

    @FXML
    private void HandleEvents() {


        if (txtPlaneName.getSelectionModel().isEmpty() || txtPlaneID.getText().isEmpty() || txtStartLine.getValue() == null || txtDirection.getText().isEmpty() ||
                txtTown.getText().isEmpty() || txtFlyHigh.getText().isEmpty() || txtHour.getValue() == null || txtDate.getValue() == null ){
            lblStatus.setTextFill(Color.TOMATO);

            lblStatus.setText("Błąd! : Podaj wszystkie dane");
        }

        else {


            //Wysyłam do serwera prośbę o sprawdzenie wprowadzonych parametrów lotu
            obj.ServerDout("CheckFlight");

            //Wysyłam do serwera aktualną datę
            obj.ServerDout(txtDate.getValue().toString());

            //Wysyłam do serwera wybraną przez klienta godzinę lotu
            obj.ServerDout(String.valueOf(txtHour.getValue()));

            //Przyjmuję od serwera true/false w zależności od poprawności wprowadzonej daty

            boolean day = obj.ServerDinBoolean();

            if (!day) {
                lblStatus.setTextFill(Color.TOMATO);
                lblStatus.setText("Błąd! : Podaj poprawną datę!");
                return;
            }


            boolean day2 = obj.ServerDinBoolean();

            if(day2){

                boolean hour = obj.ServerDinBoolean();

                if(!hour){

                    lblStatus.setTextFill(Color.TOMATO);
                    lblStatus.setText("Błąd! : Podaj poprawną godzinę!");
                    return;

                }
            }

            //Wysyłam do serwera wysokość lotu
            obj.ServerDout(txtFlyHigh.getText());


            CheckFlyHigh = obj.ServerDinBoolean();

            if(!CheckFlyHigh){

                lblStatus.setTextFill(Color.TOMATO);
                lblStatus.setText("Błąd! Podaj większą/poprawną wysokość przelotową!");
                return;

            }


            tblData.getItems().clear();
            saveData();
            fetRowList();

        }
    }


    /**
     * Clear fields after adding a new flight
     */

    private void clearFields() {
        txtPlaneID.clear();
        txtDirection.clear();
        txtFlyHigh.clear();
    }


    /**
     * Function that sends parameters of flights to the server and receives the value true / false depending on the insert result to the database
     */

    private void saveData() {

        obj.ServerDout("InsertFlight");

        //Wysyłam parametry lotu do serwera
        for (String s : Arrays.asList(txtPlaneID.getText(), txtPlaneName.getValue(), txtStartLine.getValue(), txtDirection.getText(), txtTown.getText(),
                txtFlyHigh.getText(), txtHour.getValue().toString(), txtDate.getValue().toString(), txtTime.getText(), ArrivalTime.getText())) {
            obj.ServerDout(s);

        }

        //Odbieram status zapytania insert do bazy SQL

        boolean Conf = obj.ServerDinBoolean();
        if (Conf) {
            SetlblStatus(Color.GREEN, "Lot został dodany");
            clearFields();

        }
        if (!Conf) {
            SetlblStatus(Color.TOMATO, "Błąd dodawania lotu!");
        }
    }


    /**
     * A list that stores the results of an SQL query
     */
    private ObservableList<ObservableList> data;

    /**
     * Query stored in the variable String displaying all flights
     */

    String SQL = "SELECT * FROM flights";

    /**
     * Retrieving a list of columns
     */

    private void fetColumnList() {

        try {
            ResultSet rs = connection.createStatement().executeQuery(SQL);

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1).toUpperCase());
                col.setCellValueFactory((Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));

                tblData.getColumns().removeAll(col);
                tblData.getColumns().addAll(col);



            }

        } catch (Exception e) {
            System.out.println("Błąd " + e.getMessage());

        }
    }

    /**
     * Retrieving data from a table
     */

    //fetches rows and data from the list
    private void fetRowList() {
        data = FXCollections.observableArrayList();
        ResultSet rs;
        try {
            rs = connection.createStatement().executeQuery(SQL);

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

    /**
     * A function that takes a color parameter and a text to be set in a Label field named lblStatus
     * @param color Color name
     * @param message A value of type String
     */

    private void SetlblStatus(Color color, String message){
        lblStatus.setTextFill(color);
        lblStatus.setText(message);
    }

}