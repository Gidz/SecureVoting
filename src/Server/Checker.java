package Server;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Checker {

    private static Map<String,String> users = new HashMap<String,String>();

    private static boolean loadUsers(String filename)
    {
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);
            String line=null;
            while ((line = br.readLine()) != null)
            {
                StringTokenizer st = new StringTokenizer(line,",");
                //Store in a map
                users.put(st.nextToken(),st.nextToken());
            }

        } catch (FileNotFoundException e) {
            System.out.println("Sorry. The file doesn't exist.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean authenticate(String username, String password)
    {
        loadUsers("users");
        String passwd = (String) users.get((String) username);
        if(passwd.equals(password))
        {
            return true;
        }
        //Pessimistic approach
        return false;
    }
}
