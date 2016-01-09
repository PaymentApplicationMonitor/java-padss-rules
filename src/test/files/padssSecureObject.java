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
        ResultSet rs = stmt.executeQuery("SELECT Lname FROM Customers WHERE Snum = 2001");
        rs = stmt.executeQuery("SELECT Lname FROM Customers WHERE Snum = "+param);
        String query = "SELECT Lname FROM Customers WHERE Snum = "+param;
        rs = stmt.executeQuery(query);
    }

    void sendRequest(){
        Connection conn = DriverManager.getConnection("url", "user1", "password");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT Lname FROM Customers WHERE Snum = 2001");
        rs = stmt.executeQuery("SELECT Lname FROM Customers WHERE Snum = "+param);
    }
    String getFinalString(){
        return  "effqf";
    }
    String getDetails(){

        return s; // Noncompliant {{}}
    }

    int getNum(){
        return aField; // Noncompliant {{}}
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
        return s+"dss"; // Noncompliant {{}}
    }

    private String getDATA(){
        return s;
    }



}
