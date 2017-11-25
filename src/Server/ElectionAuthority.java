package Server;

import Crypto.ElGamalScheme;
import libs.Vote;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;

public class ElectionAuthority {

    static ArrayList<Vote> votes;
    private static ArrayList<BigInteger> sk;
    private static ArrayList<BigInteger> pk;

    private static ArrayList<BigInteger> getKeyfromFile(String filename) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(filename);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object o = ois.readObject();
        ArrayList<BigInteger> key = (ArrayList<BigInteger>) o;
        return key;
    }

    private static ArrayList<Vote> getVotes() throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream("votes.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
        ArrayList<Vote> votes = (ArrayList<Vote>) ois.readObject();
        return votes;
    }

    private static BigInteger calculateResult() throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
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

        return tally;
    }

    public static void main(String args[]) throws IOException, ClassNotFoundException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidKeyException {

        //Get public and private keys
        sk = getKeyfromFile("pri.key");
        pk= getKeyfromFile("pub.key");

        //Load the votes from file
        votes = getVotes();

        //Calculate the result
        System.out.println("Calculating votes.");
        int tally = calculateResult().intValue() ;
        System.out.println("YES : "+tally);
        System.out.println("NO : "+(votes.size() - tally));

    }
}
