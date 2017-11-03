import java.awt.Button;
import java.io.IOException;
import java.util.Vector;

/**
 * Entering main()
 * Calling m1()
 * Entering m1()
 * Calling m2()
 * Enterning m2()
 * In finally for m2()
 * Exiting m2()
 * Returning from call to m2()
 * Calling m3()
 * Entering m3()
 * In finally for m3()
 * Caught IOException in m1() ... rethrowing
 * In finally for m1()
 * Caught IOException in main()
 * Exiting main()
 */

public class ExceptionTest
{
    public static void main(String args[])
    {
        System.out.println("Entering main()");
        ExceptionTest et = new ExceptionTest();

        try {
            System.out.println("Calling m1()");
            et.m1();
            System.out.println("Returning from call to m1()"); //1
        }
        catch (Exception e) {
            System.out.println("Caught IOException in main()");
        }

        System.out.println("Exiting main()");
    }

    public void m1() throws IOException
    {
        System.out.println("Entering m1()");
        Button b1 = new Button();

        try {
            System.out.println("Calling m2()");
            m2();

            System.out.println("Returning from call to m2()");
            System.out.println("Calling m3()");
            m3(true);
            System.out.println("Returning from call to m3()"); //2
        }
        catch (IOException e) {
            System.out.println("Caught IOException in " +
                               "m1() ... rethrowing");
            throw e; //3
        }
        finally {
            System.out.println("In finally for m1()");
        }

        System.out.println("Exiting m1()"); //4
    }

    public void m2()
    {
        System.out.println("Enterning m2()");

        try {
            Vector v = new Vector(5);
        }
        catch (IllegalArgumentException iae) {
            System.out.println("Caught IllegalArgumentException in m2()"); //5
        }
        finally {
            System.out.println("In finally for m2()");
        }

        System.out.println("Exiting m2()");
    }

    public void m3(boolean genExc) throws IOException
    {
        System.out.println("Entering m3()");

        try {
            Button b3 = new Button();
            if (genExc) {
                throw new IOException();
            }
        }
        finally {
            System.out.println("In finally for m3()");
        }

        System.out.println("Exiting m3()"); //6
    }
}
