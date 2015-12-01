/**
 *This file is the sample code to test variable discard.

 **/
import java.io.PrintStream;
import java.io.PrintWriter;
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

        secureObject2.sendRequest();// Noncompliant {{}}

        secureObject3 = new padssDataObject();// Noncompliant {{}}

        secureObject4 = secureObject2; // Noncompliant {{}}


    }

    void discard(){

    }

    void sendRequest(){

    }




}
