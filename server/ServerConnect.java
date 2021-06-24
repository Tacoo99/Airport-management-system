package server;


import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

/**
 * A class that handles the connection between the client and the server socket
 */

public class ServerConnect {

    /**
     * A static Boolean variable that stores the value sent to the LoginController
     * class depending on the state of the server socket connection
     */
    static Boolean error;

    /**
     * Function that returns the Socket object of the connection to the Server Socket, changing the variable error to true / false depending on the connection status
     * @return Returns the server connection object or null
     */
    public Socket ServerConn() {
        try {
            Socket s = new Socket("localhost", 6666);
            error = true;
            return s;
        } catch (ConnectException e) {
            error = false;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Sending the value of the variable error to the LoginController class
     * @return Returning the value of the error variable
     */

    static public Boolean SendError(){
        return error;
    }
}
