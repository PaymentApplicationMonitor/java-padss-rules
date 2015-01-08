/**
 *This file is the sample code to test variable discard.

 **/
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.String;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import org.hibernate.Session;
import javax.persistence.EntityManager;
import javax.persistence.Query;


class padssDataObject {

    int aField;
    String s;

    public padssDataObject(String pin,int n){
        aField=n;
        s=pin;
    }


    void discard(){
        Connection conn = DriverManager.getConnection("url", "user1", "password");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT Lname FROM Customers WHERE Snum = 2001");// Noncompliant {{}}

        String query = "SELECT Lname FROM Customers WHERE Snum = "+param;
        rs = stmt.executeQuery(query);// Noncompliant {{}}
    }

    void sendRequest(){
        Connection conn = DriverManager.getConnection("url", "user1", "password");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT Lname FROM Customers WHERE Snum = 2001");// Noncompliant {{}}

    }
    String getFinalString(){
        return  "effqf";
    }
    String getDetails(){

        return s;
    }

    int getNum(){
        return aField;
    }

    String getStatus(){
        String temp="sdfdfds";

        return temp;
    }

    String getOtherDetails(){
        String se= "hsdshd";
        return se+"weewfe";
    }

    boolean gettrdrd(){
        boolean temp=true;
        return true;
    }

    String getDataWithOther(){
        return s+"dss";
    }



}
