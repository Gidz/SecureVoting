import java.util.ArrayList;
public class BulletinBoard {

    public static ArrayList<Vote> storage = new ArrayList<Vote>();

    public static int[] count()
    {
        int yays=0,nays=0;

        for(int i=0;i<storage.size();i++)
        {
            if(storage.get(i).getVote()==2)
            {
                nays+=1;
            }
            else if(storage.get(i).getVote()==1)
            {
                yays+=1;
            }
            else
            {
                continue;
            }
        }

        int[] tally = {yays,nays};
        return tally;
    }

//    public static void main(String args[])
//    {
//        int[] tally = count();
//        System.out.println("YES : "+tally[0]);
//        System.out.println("NO : "+tally[1]);
//    }

}