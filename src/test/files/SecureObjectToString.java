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


    public static void main(String[] args) throws Exception {

        padssDataObject secureObject ;
        padssDataObject secureObject2;
        padssDataObject secureObject3;
        padssDataObject secureObject4;
        padssDataObject secureObject5;// Compliant

        secureObject = new padssDataObject();

        secureObject.sendRequest();
        System.out.println("dd");
        secureObject.discard();

        secureObject2 = new padssDataObject();

        String s = secureObject2.toString();// Noncompliant {{}}

        secureObject3 = new padssDataObject();
        String s2 = secureObject3.toString();// Noncompliant {{}}

        s2 = secureObject2.toString(); // Noncompliant {{}}

        System.out.println(s2+s);


    }

    void discard(){

    }

    void sendRequest(){

    }




}
