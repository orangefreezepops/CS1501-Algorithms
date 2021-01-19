import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

// NOTE: BigInteger is used only for convenience in printing and converting byte[]'s
import java.math.BigInteger;

public class TwoHundredDigitTest {
    private static boolean QUIET = false;

    public static void main(String[] args) {
        String one, two;
        //200 digit integer
        one = "274987372983923728292749873729839237282927498737298392372829274987372983923728292749873729839237282927498737298392372829" +
        "27498737298392372829274987372983923728292749873729839237282927498737298392372829";

        //200 digit integer
        two = "215646543784951248152156465437849512481521564654378495124815215646543784951248152156465437849512481521564654378495124815" +
        "21564654378495124815215646543784951248152156465437849512481521564654378495124815";

        System.out.println(one.length());
        System.out.println(two.length());

        largeIntMultiply(args, one, two);
        extendedEuclideanAlgo(args, one, two);
    }

    public static void largeIntMultiply(String[] args, String one, String two){
        checkIfQuiet(args);
        
        HeftyInteger hiOne = new HeftyInteger(new BigInteger(one).toByteArray());
        HeftyInteger hiTwo = new HeftyInteger(new BigInteger(two).toByteArray());
        //time this 
        long start = System.nanoTime();
        HeftyInteger result = hiOne.multiply(hiTwo);
        long end = System.nanoTime();

        printIfLoud("Result:");
        printHeftyInteger(result);

        long timelapse = ((end - start) / 1_000_000_000)/60; 
        System.out.println("multiplication took: " + timelapse + " minutes");
    }

    public static void extendedEuclideanAlgo(String[] args, String one, String two){
        checkIfQuiet(args);
        HeftyInteger hiOne = new HeftyInteger(new BigInteger(one).toByteArray());
        HeftyInteger hiTwo = new HeftyInteger(new BigInteger(two).toByteArray());

        long start = System.nanoTime();
        HeftyInteger[] result = hiOne.XGCD(hiTwo);
        long end = System.nanoTime();

        printIfLoud("Result, where a*x + b*y = GCD(x,y):");
        System.out.print("GCD(a, b) = ");
        printHeftyInteger(result[0]);
        System.out.print("x = ");
        printHeftyInteger(result[1]);
        System.out.print("y = ");
        printHeftyInteger(result[2]);
        
        long timelapse = ((end - start) / 1_000_000_000)/60; 
        System.out.println("XGCD took: " + timelapse + " minutes");
    }

    private static void printIfLoud(String s) {
        if (!QUIET) System.out.println(s);
    }

    private static void checkIfQuiet(String[] args) {
        if (args.length >= 1 && args[0].equals("-q")) QUIET = true;
    }

    public static void printHeftyInteger(HeftyInteger hi) {
        System.out.println(new BigInteger(hi.getVal()).toString());
    }
}