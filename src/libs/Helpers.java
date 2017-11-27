package libs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.util.ArrayList;

public class Helpers {
    public static ArrayList<BigInteger> getKeyfromFile(String filename) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(filename);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object o = ois.readObject();
        ArrayList<BigInteger> key = (ArrayList<BigInteger>) o;
        return key;
    }
}
