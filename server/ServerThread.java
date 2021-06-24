package server;

import utils.ConnectionUtil;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

/**
 * A class that handles multiple clients in separate threads
 */

public class ServerThread extends Thread implements Serializable {

    /**
     * variable storing the current connection with the database
     */

    Connection con;

    /**
     *   Variable used to execute the same or similar database statements repeatedly with high efficiency.
     */

    PreparedStatement preparedStatement;

    /**
     * Variable that stores the current query result
     */
    ResultSet resultSet;

    /**
     * Boolean value that stores the current connection status of
     * the server with the SQL database (true/false)
     */
    Boolean SQLStatus;

    /**
     * A String variable that stores a string entered by the client
     */
    String strLogin;

    /**
     * A String variable that stores a string entered by the client
     */
    String strPassword;

    /**
     * A bool type variable that stores the true / false value
     * depending on the login data entered by the client
     */
    Boolean CheckCred;

    /**
     * A bool variable that stores the value sent to the client
     */
    String result;

    /**
     * A bool variable that stores the value that is
     * received from the client
     */
    Boolean Check;

    /**
     * A string variable that stores the flight time
     * received from the client
     */
    String FlyHour2str;

    /**
     * A string variable that stores the flight day
     * received from the client
     */
    String strDay;

    /**
     * A string variable that stores the flight time
     * received from the client
     */


    LocalDate Day;

    /**
     * Variable that stores the day of flight in the form of LocalDate,
     * suitable for date comparison
     */


    Boolean CheckFlyHigh;

    /**
     * A bool type variable that stores the true / false depending
     * on the flight altitude specified by the client
     */

    private final Socket socket;

    /**
     * The bool variable that stores the true / false value depending on the
     * result of the insert query to the database
     */
    Boolean Conf;

    /**
     * A function that takes the current server socket object and connects to the database
     *
     * @param socket Variable that stores the server socket object
     */

    public ServerThread(Socket socket) {
        this.socket = socket;
        con = ConnectionUtil.conDB();
    }


    /**
     * A function that listens for the codes sent to the server and refers to specific functions
     */
    public void run() {

        try {
            DataInputStream disCode = new DataInputStream(socket.getInputStream());
            String code = disCode.readUTF();


            Map<String, Runnable> codes = Map.of(
                    "SQL", this::CodeSQL,
                    "Login", this::CodeLogin,
                    "Communicate", this::CodeCommunicate,
                    "AddFlight", this::CodeAddFlight,
                    "CheckFlight", this::CodeCheckFlight,
                    "FinishedFlight", this::CodeFinishedFlights,
                    "InsertFlight", this::CodeInsertFlight
            );


            try {
                codes.get(code).run();
            } catch (NullPointerException ex) {
                run();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The function checks the current connection status with the server and sends the Boolean information to the client
     */

    private void CodeSQL() {

        SQLStatus = con != null;

        try {
            DataOutputStream doutSQL = new DataOutputStream(socket.getOutputStream());
            doutSQL.writeBoolean(SQLStatus);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Powrót do nasłuchiwania kolejnych kodów
        run();
    }

    /**
     * The function retrieves the entered login and password from the client, checks the correctness with the database and sends information to the client
     */

    private void CodeLogin() {


        strLogin = ToServer();
        strPassword = ToServer();


        String sql = "SELECT * FROM admins Where login = ? and password = ?";

        try {
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, strLogin);
            preparedStatement.setString(2, strPassword);
            resultSet = preparedStatement.executeQuery();

            CheckCred = resultSet.next();

            DataOutputStream doutCred = new DataOutputStream(socket.getOutputStream());
            doutCred.writeBoolean(CheckCred);

        } catch (SQLException ex) {
            ex.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Powrót do nasłuchiwania kolejnych kodów
        run();
    }

    /**
     * Function that takes the flight id from the table and sends it to the client
     */

    private void CodeCommunicate() {

        String sql = "SELECT id_lotu FROM flights";

        try {
            preparedStatement = con.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            ArrayList arrayList = new ArrayList();

            while (resultSet.next()) {
                arrayList.add(resultSet.getInt("id_lotu"));
            }

            Object[] objArray = arrayList.toArray();

            FromServerObject(objArray);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        run();

    }

    /**
     * A function that retrieves the name of the plane and city from the client,
     * retrieves the distance from the city and the speed of the plane from the maps,
     */

    private void CodeAddFlight() {

        String Plane = ToServer();
        String Town = ToServer();


        Map<String, Integer> towns = Map.of(
                "Moskwa", 600,
                "Berlin", 300,
                "Zagrzeb", 400,
                "Nowy Jork", 2000,
                "Tokyo", 6000,
                "Warszawa", 400,
                "Oslo", 3000
        );

        double s = towns.get(Town);

        Map<String, Integer> planes = Map.of(
                "Concord", 1200,
                "Boeing", 1000,
                "AirBus", 800
        );

        double v = planes.get(Plane);


        FlyTime(v, s);


    }

    /**
     * * calculates the flight time and sends this information to the client
     * @param v Airplane speed
     * @param s Distance from the city
     */

    private void FlyTime(double v, double s) {

        boolean checkHour = ToServerBoolean();
        LocalTime ArrivalHour;

        if (checkHour) {

            //Godzina lotu do serwera
            FlyHour2str = ToServer();
            LocalTime FlyHour2 = LocalTime.parse(FlyHour2str);


            double t = s / v;
            if (t > 1) {

                String sTh = String.valueOf((int) t);
                int tInt = (int) t;

                //Przesyłam String z czasem lotu do klienta
                FromServerString(sTh + " h ");

                ArrivalHour = FlyHour2.plusHours(tInt);

                //Przesyłam czas przylotu do serwera
                FromServerString(ArrivalHour.toString());


            } else {

                int ts = (int) (t * 60);
                String sTm = String.valueOf(ts);

                //Przesyłam String z czasem lotu do klienta
                FromServerString(sTm + " min ");

                ArrivalHour = FlyHour2.plusMinutes(ts);

                //Przesyłam czas przylotu do serwera
                FromServerString(ArrivalHour.toString());


            }

        }


        run();

    }

    /**
     * A function that checks the flight data provided
     */

    private void CodeCheckFlight() {

        //Aktualna data
        LocalDate DayNow = LocalDate.now();

        //Pobieram dzień lotu od klienta
        strDay = ToServer();


        //Zamieniam dzień lotu na typ LocalDate
        Day = LocalDate.parse(strDay);
        System.out.println("Odebrałem datę lotu: " + Day);

        //Pobieram od klienta wybraną przez niego godzinę lotu
        String Hourstr = ToServer();


        if (Day.compareTo(DayNow) < 0) {

            //Wysyłam do klienta informację że wprowadził złą datę
            FromServerBoolean(false);
            run();
        } else {

            FromServerBoolean(true);
        }

        if (Day.compareTo(DayNow) == 0) {

            //Zamieniam na typ zmiennej LocalTime
            LocalTime Hour = LocalTime.parse(Hourstr);

            //Zapisuję do zmiennej aktualną godzinę serwerową
            LocalTime HourNow = LocalTime.now();

            FromServerBoolean(true);

            if (Hour.compareTo(HourNow) < 0) {

                FromServerBoolean(false);

                run();

            } else {

                FromServerBoolean(true);

            }

        } else {

            FromServerBoolean(false);

        }


        int FlyHigh = Integer.parseInt(ToServer());

        CheckFlyHigh = FlyHigh >= 300;

        FromServerBoolean(CheckFlyHigh);

        run();
    }

    /**
     * Function to delete flights that have already ended
     */

    private void CodeFinishedFlights() {

        String SQL = "SELECT Data,Czas_przylotu FROM flights";

        try {
            ResultSet rs = con.createStatement().executeQuery(SQL);

            while (rs.next()) {

                Date FetchedDate = rs.getDate("Data");
                LocalDate ConvertedDate = FetchedDate.toLocalDate();

                String FetchedTime = rs.getString("Czas_Przylotu");
                LocalTime ConvertedTime = LocalTime.parse(FetchedTime);

                if (ConvertedDate.compareTo(LocalDate.now()) < 0) {
                    String st2 = "delete from flights where Data = ? ";
                    preparedStatement = con.prepareStatement(st2);
                    preparedStatement.setString(1, ConvertedDate.toString());
                    preparedStatement.executeUpdate();
                }

                if (ConvertedDate.compareTo(LocalDate.now()) == 0) {

                    if (ConvertedTime.compareTo(LocalTime.now()) < 0) {
                        String st3 = "delete from flights where Czas_przylotu = ? ";
                        preparedStatement = con.prepareStatement(st3);
                        preparedStatement.setString(1, ConvertedTime.toString());
                        preparedStatement.executeUpdate();
                    }

                }

            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        run();

    }


    /**
     * A function that adds a flight to the base
     */

    private void CodeInsertFlight() {

        String id_lotu, samolot, pas_startowy, kierunek;
        String miasto, wysokosc, godzina, data, czas_lotu, czas_przylotu;

        id_lotu = ToServer();
        samolot = ToServer();
        pas_startowy = ToServer();
        kierunek = ToServer();
        miasto = ToServer();
        wysokosc = ToServer();
        godzina = ToServer();
        data = ToServer();
        czas_lotu = ToServer();
        czas_przylotu = ToServer();

        try {
            String st = "INSERT INTO flights ( id_lotu, Samolot, Pas_startowy, Kierunek, Miasto, Wysokość, Godzina, Data, Czas_lotu, Czas_przylotu) VALUES (?,?,?,?,?,?,?,?,?,?)";
            preparedStatement = con.prepareStatement(st);
            preparedStatement.setString(1, id_lotu);
            preparedStatement.setString(2, samolot);
            preparedStatement.setString(3, pas_startowy);
            preparedStatement.setString(4, kierunek);
            preparedStatement.setString(5, miasto);
            preparedStatement.setString(6, wysokosc);
            preparedStatement.setString(7, godzina);
            preparedStatement.setString(8, data);
            preparedStatement.setString(9, czas_lotu);
            preparedStatement.setString(10, czas_przylotu);

            preparedStatement.executeUpdate();
            Conf = true;


        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            Conf = false;
        }

        FromServerBoolean(Conf);
        run();

    }

    /**
     * A function that sends a String value to the server
     * @return The returned String value
     */

    private String ToServer() {

        try {
            DataInputStream disData = new DataInputStream(socket.getInputStream());
            result = disData.readUTF();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }

    /**
     * A function that sends a Boolean value to the server
     * @return The returned Boolean value
     */

    private boolean ToServerBoolean() {

        try {
            DataInputStream disData = new DataInputStream(socket.getInputStream());
            Check = disData.readBoolean();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Check;
    }

    /**
     * A function that sends a value of type String to the client
     * @param data Value transfered to the customer
     */

    private void FromServerString(String data) {

        try {
            DataOutputStream doutData = new DataOutputStream(socket.getOutputStream());
            doutData.writeUTF(data);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A function that sends a value of type Boolean to the client
     * @param data Value transfered to the customer
     */

    private void FromServerBoolean(Boolean data) {


        try {
            DataOutputStream doutData = new DataOutputStream(socket.getOutputStream());
            doutData.writeBoolean(data);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A function that sends Object to the client
     * @param object Variable that stores the Object to send
     */

    private void FromServerObject(Object object) {

        try {
            ObjectOutputStream obj = new ObjectOutputStream(socket.getOutputStream());
            obj.writeObject(object);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}







