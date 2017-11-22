//: MultiJabberClient.java
// Client that tests the MultiJabberServer
// by starting up multiple clients.
import java.net.*;
import java.io.*;
import java.util.Scanner;

class ClientThread extends Thread {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private static int counter = 0;
    private int id = counter++;
    private static int threadcount = 0;
    public static int threadCount() {
        return threadcount;
    }
    public ClientThread(InetAddress addr) {
//        System.out.println("Making client " + id);
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
        try {
            String toSend,question;
            oos.writeObject(new String("JOIN"));

            //Client only receives objects of type String.
            //No need to check for the type of object.

            question = (String) ois.readObject();
            System.out.println("Congratulations, you've joined the voting server");
            System.out.println("------------------------------------------------");
            System.out.println("Question: "+ question);
            System.out.println("Please Vote your opinion. Choose one of the options : \n 1. YES \n 2. NO");
            System.out.print("> ");

            //Get the input from user
            Scanner sc = new Scanner(System.in);
            toSend = sc.nextLine();

            //Make a Vote object
            Vote vote = new Vote(Integer.parseInt(toSend));
            oos.writeObject(vote);
            System.out.println((String) ois.readObject());
        }
        catch(IOException e) {
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // Always close it:
            try {
                socket.close();
            } catch(IOException e) {}
            threadcount--; // Ending this thread
        }
    }
}

