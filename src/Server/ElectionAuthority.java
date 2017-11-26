package Server;
import Crypto.ElGamalScheme;
import libs.Vote;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import static libs.Helpers.getKeyfromFile;

public class ElectionAuthority {

    static ArrayList<Vote> votes;
    private static ArrayList<BigInteger> sk;
    private static ArrayList<BigInteger> pk;

    private static ArrayList<Vote> getVotes() throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream("votes.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
        ArrayList<Vote> votes = (ArrayList<Vote>) ois.readObject();
        return votes;
    }

    public static ArrayList<Integer> calculateResult() throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidKeyException, IOException, ClassNotFoundException {
        //Load the votes from file
        votes = getVotes();

        //Get public and private keys
        sk = getKeyfromFile("pri.key");
        pk= getKeyfromFile("pub.key");

        BigInteger temp1 = new BigInteger("0");
        BigInteger temp2 = new BigInteger("0");
        BigInteger tally = new BigInteger("0");

        for(int i=0;i<votes.size();i++)
        {
            //If it is the last vote
            if((i+1) >= votes.size())
            {
                tally = tally.add(ElGamalScheme.Decrypt_homomorphe(sk,pk.get(1),votes.get(i).getVote().get(0),votes.get(i).getVote().get(1)));
            }
            else
            {
                temp1 = votes.get(i).getVote().get(0).multiply(votes.get(i+1).getVote().get(0));
                temp2 = votes.get(i).getVote().get(1).multiply(votes.get(i+1).getVote().get(1));
                tally = tally.add(ElGamalScheme.Decrypt_homomorphe(sk,pk.get(1),temp1,temp2));
                i++;
            }
        }
        ArrayList<Integer> result = new ArrayList<Integer>();
        result.add(tally.intValue());
        result.add(votes.size() - tally.intValue());
        return result;
    }

    public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        //Calculate the result
        System.out.println("Calculating votes.");
        ArrayList<Integer> tally = calculateResult();
        System.out.println("YES : "+tally.get(0));
        System.out.println("NO : "+tally.get(1));

        //Save the result to file
        FileOutputStream fos = new FileOutputStream("result.obj");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(tally);
    }
}
