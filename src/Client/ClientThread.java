package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import Server.*;
import Crypto.*;
import libs.Vote;

public class ClientThread extends Thread {
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
    public List<BigInteger> pk;

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
        System.out.println("Not so fast. Enter username and password.");
        Scanner sc = new Scanner(System.in);
        System.out.print("Username : ");
        String username = sc.nextLine();
        System.out.print("Password : ");
        String password = sc.nextLine();

        if(!Checker.authenticate(username,password))
        {
            System.out.println("Sorry, you are not recognized as an eligible voter.");
            System.exit(0);
        }

        try {
            String toSend,question;

            //Send a join request to the server
            oos.writeObject(new String("JOIN"));
            oos.writeObject(new String(username));

            //Get the public key from the server
            ArrayList<BigInteger> pk = (ArrayList<BigInteger>) ois.readObject();
            question = (String) ois.readObject();

            //See if the voter has already voted
            String status = (String) ois.readObject();

            if(status.equals("VOTED"))
            {
                System.out.println("You have already voted.");
                System.exit(0);
            }
            //Client only receives objects of type String.
            //No need to check for the type of object.

            System.out.println("Congratulations, you've joined the voting server");
            System.out.println("------------------------------------------------");
            System.out.println("Question: "+ question);
            System.out.println("Please Vote your opinion. Choose one of the options : \n 1. YES \n 2. NO");
            System.out.print("> ");

            //Get the input from user
            toSend = sc.nextLine();

            // Resolve options to 0 or 1
            int i = -1;
            if(Integer.parseInt(toSend) == 1)
                i=1;
            else if(Integer.parseInt(toSend)== 2)
                i=0;
            else
            {
                System.out.println("Please choose a proper option");
                System.exit(0);
            }

            //Encrypt the vote
            ArrayList<BigInteger> v = ElGamalScheme.Encrypt_Homomorph(pk,BigInteger.valueOf(i));

            //Make a Vote object
            Vote vote = new Vote(v);

            //Send the vote across the network
            oos.writeObject(vote);

            //Get the confirmation message
            System.out.println((String) ois.readObject());

        } catch (SocketTimeoutException e) {
            //In case of networking failures or when server takes too long to respond
          System.err.println("Socket timed out.. Oops!");
        } catch(IOException e) {
            //In case the server hasn't registerd the vote sent
            System.out.println("Your vote hasn't been registered. Please try again.");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally
        {
            // Always close it:
            try {
                socket.close();
            } catch(IOException e) {}
            threadcount--;
        }
    }
}

