package connect;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class SQLConnection {

    private static final String url = "jdbc:postgresql://enterprise.cw9r6djgzdex.us-east-2.rds.amazonaws.com:5432/postgres?CurrentSchema=projorm";
    private static final String username = "postgres";
    private static final String password =  "master123";                                                    //do you have that portion in your url?
    //jbank is my schema


    public Connection instance;

    public SQLConnection(){}



    public Connection getInstance(){
        if(instance == null){
            try {
                Class.forName("org.postgresql.Driver");
                instance = DriverManager.getConnection(url, username, password);
                System.out.println("connected success");

            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }




}
