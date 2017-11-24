//: MultiJabberClient.java
// Client that tests the MultiJabberServer
// by starting up multiple clients.
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Base64;
import java.util.Scanner;

class ClientThread extends Thread {
    static boolean debug = true;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private static int socketTimeout = 100;

    private static int counter = 0;
    private int id = counter++;
    private static int threadcount = 0;
    public static int threadCount() {
        return threadcount;
    }
    public ClientThread(InetAddress addr) {
        threadcount++;
        try {
            socket =
                    new Socket(addr, Server.PORT);
        } catch(IOException e) {
            // If the creation of the socket fails,
            // nothing needs to be cleaned up.
        }
        try {
            socket.setSoTimeout(socketTimeout);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            start();
        } catch(IOException e) {
            // The socket should be closed on any
            // failures other than the socket
            // constructor:
            try {
                socket.close();
            } catch(IOException e2) {}
        }
        // Otherwise the socket will be closed by
        // the run() method of the thread.
    }
    public void run() {
        try {
            String toSend,question;

            //Send a join request to the server
            oos.writeObject(new String("JOIN"));

            //Client only receives objects of type String.
            //No need to check for the type of object.

            //Get the public key from the server
            Key pubKey = (Key) ois.readObject();
            if(debug) System.out.println(ElGamal.encodeKey(pubKey));
            question = (String) ois.readObject();

            System.out.println("Congratulations, you've joined the voting server");
            System.out.println("------------------------------------------------");
            System.out.println("Question: "+ question);
            System.out.println("Please Vote your opinion. Choose one of the options : \n 1. YES \n 2. NO");
            System.out.print("> ");

            //Get the input from user
            Scanner sc = new Scanner(System.in);
            toSend = sc.nextLine();

            //Encrypt the vote
            byte[] v = ElGamal.encryptVote(toSend,pubKey);

            //Decrypt the vote
//            System.out.println(ElGamal.encodeKey(Server.privKey));

            //Make a Vote object
            Vote vote = new Vote(v);
            oos.writeObject(vote);

            //Get the confirmation message
            System.out.println((String) ois.readObject());
        } catch (SocketTimeoutException e) {
          System.err.println("Socket timed out.. oops!");
        } catch(IOException e) {
            System.out.println("Your vote hasn't been registered. Please try again.");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } finally
        {
            // Always close it:
            try {
                socket.close();
            } catch(IOException e) {}
            threadcount--; // Ending this thread
        }
    }
}

