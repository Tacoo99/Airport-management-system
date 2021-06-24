package login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import server.ServerConnect;


import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Main class responsible for, among other things, connecting to the server and sending messages to it
 */

public class LoginController implements Initializable, Serializable {

    @FXML
    private Label lblErrors1;

    @FXML
    private Label lblErrors;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtPassword;

    @FXML
    private Button btnSignin;

    /*
      @author Wojciech Kozioł
     */

    /**
     * An object variable used to transfer objects between the server and client
     */
    Object obj2;

    /**
     * Variables of the double type used to store the coordinates of the window
     */
    double x, y;

    /**
     * Variable declaration that currently stores the socket connection of the server
     */
    static Socket s;

    /**
     * A Boolean variable that stores the true / false value depending on the correctness of the entered login data
     */
    boolean CheckCred;

    /**
     * A variable that stores true / false for transferring information between the server and client
     */
    boolean BoolFromServer;

    /**
     * A variable that stores a Boolean true or false value sent to the client
     */
    String StringFromServer;


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
     * The function responsible for closing the window
     */

    @FXML
    public void handleExitAction() {
        System.exit(0);
    }

    /**
     * The function is performed after the customer has correctly entered the login and password, closing the current window and opening another
     * @param event Get the current scene of the window to close it
     */

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if (event.getSource() == btnSignin) {
            if (logIn()) {

                try {
                    int secondsToSleep = 3;
                    Thread.sleep(secondsToSleep * 1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }


                try {

                    Node node = (Node) event.getSource();
                    Stage stage = (Stage) node.getScene().getWindow();
                    stage.close();


                    Scene scene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/home/Home.fxml"))));
                    stage.getIcons().add(new Image("/images/airport2_48px.png"));
                    stage.setTitle("SKL - strona główna");
                    stage.setScene(scene);
                    stage.show();

                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {


        //Laczenie z serwerem

            ServerConnect obj2 = new ServerConnect();
            s = obj2.ServerConn();

            //Pobierz status serwera z klasy ServerConnect
            Boolean serverstatus = ServerConnect.SendError();

            if(serverstatus){
                setLblError1(Color.GREEN,"Połączono z serwerem");
            }
            else{
                setLblError1(Color.TOMATO,"Błąd łączenia z serwerem");
            }

        //Wysyłam kod SQL do serwera - sprawdzenie stanu połączenia z bazą
        ServerDout("SQL");

        //Odbieram wartość true/false od serwera
        boolean SQLStatus = ServerDinBoolean();


        if (SQLStatus) {
            setLblError(Color.GREEN, "Połączono z bazą danych");
        } else {
            setLblError(Color.TOMATO, "Błąd łączenia z bazą danych");
            btnSignin.setDisable(true);

        }


    }


    /**
     * The function retrieving the entered login and password, sending the code to the server and receiving the true / false value depending on the correctness of the entered data
     * @return Returning a boolean value
     */

    private Boolean logIn() {

        boolean status;
        String login = txtUsername.getText();
        String password = txtPassword.getText();

        if (login.isEmpty() || password.isEmpty()) {
            setLblError(Color.TOMATO, "Puste pola");
            status = false;
        } else {

            //Wysyłam kod do serwera
            ServerDout("Login");

            //Wysyłam wpisane przez klienta login oraz hasło
            ServerDout(login);
            ServerDout(password);

            //Odbieram wartość true/false z serwera - dobre/złe hasło/login
            CheckCred = ServerDinBoolean();

            if (CheckCred) {
                setLblError(Color.GREEN, "Zalogowano pomyślnie");
                status = true;
            } else {
                setLblError(Color.TOMATO, "Błędny login lub hasło!");
                status = false;
            }


        }
        return status;
    }

    /**
     * Function for setting the color and text of the label lblErrors
     * @param color Color name
     * @param text String with text
     */

    private void setLblError(Color color, String text) {
        lblErrors.setTextFill(color);
        lblErrors.setText(text);
    }

    /**
     * Function for setting the color and text of the label lblErrors1
     * @param color Color name
     * @param text String with text
     */

    public void setLblError1(Color color, String text) {
        lblErrors1.setTextFill(color);
        lblErrors1.setText(text);
    }

    /**
     * A function that transfers a string from the server to the client
     * @param data String variable containing the string to be sent
     */


    public void ServerDout(String data){

        try {
            DataOutputStream doutData = new DataOutputStream(s.getOutputStream());
            doutData.writeUTF(data);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * A function that transfers a Boolean variable from the server to the client
     * @param data String variable containing the Boolean variable to be sent
     */

    public void ServerDoutBoolean(boolean data){

        try{

            DataOutputStream doutData = new DataOutputStream(s.getOutputStream());
            doutData.writeBoolean(data);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //DATAINPUTSTREAMS

    /**
     * A function that receives a stream that comes from a client with a Boolean value
     * @return Return a retrieved Boolean value from a data stream
     */

    public boolean ServerDinBoolean() {

                
        try {
            DataInputStream dinData = new DataInputStream(s.getInputStream());
            BoolFromServer = dinData.readBoolean();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return BoolFromServer;
    }

    /**
     * A function that receives a stream that comes from a client with a String value
     * @return Return a retrieved String value from a data stream
     */

    public String ServerDinString(){

        try{
            DataInputStream dinDataa = new DataInputStream(s.getInputStream());
            StringFromServer = dinDataa.readUTF();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return StringFromServer;
    }

    /**
     * A function that sends a data stream of type Object to the client
     * @return Return a retrieved Object from a data stream
     */

    public Object ServerDinObject(){
        try {
            ObjectInputStream dinObj = new ObjectInputStream(s.getInputStream());
            obj2 = dinObj.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj2;
    }

    }