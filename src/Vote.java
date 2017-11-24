import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;

public class Vote implements Serializable{

    ArrayList<BigInteger> vote;

    public Vote(ArrayList<BigInteger> vote)
    {
        this.vote = vote;
    }

    public ArrayList<BigInteger> getVote()
    {
        return this.vote;
    }
}
