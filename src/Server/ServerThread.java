package Server;

import Client.BulletinBoard;
import Crypto.ElGamalScheme;
import libs.Vote;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static Server.Server.question;

class ServerThread extends Thread {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    public ServerThread(Socket s)
            throws IOException {
        socket = s;
        ois = new ObjectInputStream(socket.getInputStream());
        oos = new ObjectOutputStream(socket.getOutputStream());


        // If any of the above calls throw an 
        // exception, the caller is responsible for
        // closing the socket. Otherwise the thread
        // will close it.
        start(); // Calls run()
    }
    public void run() {
        try {
            while (true) {
                //Receive Object
                Object obj = ois.readObject();

                //Check the type of object
                if(obj instanceof String)
                {
                    String str;
                    str = (String) obj;
                    if(str.equals("JOIN"))
                    {
                        //Send the public key of the server
                        System.out.println("Shared the public key with the voter.");
                        oos.writeObject(ElGamalScheme.pk);
                        oos.writeObject(question);
                        continue;
                    }
                    else
                    {
                        String username = (String) obj;
                        //Check if the voter has voted before
                        boolean voted = true;
                        if(Server.voters.isEmpty())
                            voted =  false;

                        if(Server.voters.contains((String) username))
                            voted = Server.voters.contains((String) username);
                        else
                            voted = false;

                        if(voted)
                        {
                            oos.writeObject("VOTED");
                            //Kill the thread
                            socket.close();
                        }
                        else
                        {
                            Server.markVoter((String)obj);
                            oos.writeObject("NOT VOTED");
                        }
                    }
                }
                else if(obj instanceof Vote)
                {
                    Vote vote = (Vote) obj;
                    if(BulletinBoard.addToBulletinBoard(vote)) {
                        ArrayList<BigInteger> a = vote.getVote();
                        oos.writeObject("Thank you. Your vote has been registered successfully");
                    }
                    else
                    {
                        oos.writeObject("Sorry. Something went wrong. Please try again.");
                    }
                }
                else
                {
                    System.err.println("Sorry, this type of object is unknown to the system.");
                }
            }
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch(IOException e) {}
        }
    }
}

