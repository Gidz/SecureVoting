//: MultiJabberClient.java
// Client that tests the MultiJabberServer
// by starting up multiple clients.
import java.net.*;
import java.io.*;
import java.util.Scanner;

class ClientThread extends Thread {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
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
            in = new DataInputStream(socket.getInputStream());
            // Enable auto-flush:
            out = new DataOutputStream(socket.getOutputStream());
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

            out.writeUTF("JOIN");
            question = in.readUTF();

            System.out.println("Congratulations, you've joined the voting server");
            System.out.println("------------------------------------------------");
            System.out.println("Question: "+ question);
            System.out.println("Please Vote your opinion. Choose one of the options : \n 1. YES \n 2. NO");
            System.out.print("> ");
            Scanner sc = new Scanner(System.in);
            toSend = sc.nextLine();
            out.writeUTF(toSend);
        }
        catch(IOException e) {
        }
        finally {
            // Always close it:
            try {
                socket.close();
            } catch(IOException e) {}
            threadcount--; // Ending this thread
        }
    }
}

