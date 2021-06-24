package server;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The controller class of the graphics server version
 */

public class GUIController implements Initializable{

    @FXML
    private TextArea serverLog;

    @FXML
    private Button btnStart;

    @FXML
    private Button btnStop;

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
     * The function responsible for closing the window
     */

    @FXML
    public void handleExitAction() {
        System.exit(0);
    }

    /**
     * Function called after clicking the btnStart button,
     * starts the server by calling the RunServer() function,
     * deactivates the btnStart button and activates btnStop
     */
    @FXML
    void startButton() {

        RunServer();
        btnStart.setDisable(true);
        btnStop.setDisable(false);

    }

    /**
     * Function called after clicking the btnStop button,
     * activates the btnStart button and deactivates btnStop,
     * closing current Server Socket
     */

    @FXML
    void stopButton() {

        btnStop.setDisable(true);
        btnStart.setDisable(false);

        try{
            serverSocket.close();
            serverLog.appendText("\nZatrzymywanie serwera");

        } catch (IOException e) {
            e.getMessage();
        }


    }


    /**
     * The int variable that stores the server port number
     */

    int port = 6666;

    /**
     * Server socket declaration
     */
    ServerSocket serverSocket;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btnStop.setDisable(true);

    }

    /**
     * Function that starts the server socket and accepts incoming connections
     * and creates a separate thread for each client
     */

    private void RunServer() {


        Thread thread = new Thread(() -> {

            try {

                serverSocket = new ServerSocket(6666);
                serverLog.appendText("Otworzono socket serwera na porcie: " + port);
                serverLog.appendText("\nOczekuję na klienta");

                while (!serverSocket.isClosed()) {
                    Socket socket = serverSocket.accept();
                    serverSocket.getInetAddress();
                    serverLog.appendText("\nNowy klient podłączony: " + InetAddress.getLocalHost());
                    new ServerThread(socket).start();
                }


            } catch (IOException e) {
                serverLog.appendText("\n" + e.getMessage());
            }
        });

        thread.start();
    }

}
