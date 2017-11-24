//: MultiJabberServer.java
// A server that uses multithreading to handle
// any number of clients.

import java.io.*;
import java.net.Socket;

class ServerThread extends Thread {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    public static String question = "Should pineapple be allowed on pizza ?";
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
                    String str = null;
                    str = (String) obj;
                    if (str.equals("END")) break;

                    if(str.equals("JOIN"))
                    {
                        //Send the public key of the server
                        System.out.println(ElGamalScheme.pk);
                        oos.writeObject(ElGamalScheme.pk);

                        oos.writeObject(question);
                        continue;
                    }
                    System.out.println("Received: " + str);
                }
                else if(obj instanceof Vote)
                {
                    Vote vote = (Vote) obj;
                    if(BulletinBoard.addToBulletinBoard(vote)) {
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

