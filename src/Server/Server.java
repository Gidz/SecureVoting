package Server;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Scanner;

import Client.BulletinBoard;
import Crypto.ElGamalScheme;

public class Server {
    public static final int PORT = 8080;
    static final long endTime = 60;
    public static String question;
    static final String exitMessage = "The Elections have ended. No further votes will be accepted";
    static ArrayList<BigInteger> sk,pk;
    static long start = System.currentTimeMillis();

    public static void main(String[] args)
            throws IOException, NoSuchProviderException, NoSuchAlgorithmException {
        ServerSocket s = new ServerSocket(PORT);
        System.out.println("Server Started");

        //Generate public and private keys
        ElGamalScheme.KeyGen(200);
        pk = ElGamalScheme.getPublicKey();
        sk = ElGamalScheme.getPrivateKey();

        try {

            /*Store the public and private key pair to their respective files.
            * Server is run on a separate machine than the clients, so there is
            * no chance of voters accessing these keys.
            * */
            writeKeytoFile("pub.key",pk);
            writeKeytoFile("pri.key",sk);

            //Set the question
            System.out.print("Set the question : ");
            Scanner sc = new Scanner(System.in);
            question = sc.nextLine();


            PrintWriter writer = new PrintWriter("question.txt", "UTF-8");
            writer.println(question);
            writer.close();

            /*A thread which keeps track of time. Once enough time has elapsed, this
            * thread stops the elections, stores all the votes to a file and exits.
            * */
            Thread t = new Thread() {
                public void run() {
                    long end = start + (endTime * 1000); // 60 seconds * 1000 ms/sec
                    int i=0;
                    while (System.currentTimeMillis() < end)
                    {
                        //Busy wait
                    }
                    System.out.println(exitMessage);
                    try {
                        //Store the votes in a file
                        BulletinBoard.storeVotes();
                        System.out.println("The votes are stored safely to the storage device.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                }
            };
            t.start();

            while(true) {
                // Blocks until a connection occurs:
                Socket socket = s.accept();
                try {
                        new ServerThread(socket);
                } catch(IOException e) {
                    // If it fails, close the socket,
                    // otherwise the thread will close it:
                    socket.close();
                }
            }
        } finally {
            System.out.println("The server is shutting down");
            s.close();
        }
    }
    private static void writeKeytoFile(String fileName,ArrayList<BigInteger> key) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(key);
        fos.close();
        oos.close();
    }
}