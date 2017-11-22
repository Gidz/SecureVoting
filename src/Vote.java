import java.io.Serializable;

public class Vote implements Serializable{
    int vote;
    public Vote(int vote)
    {
        this.vote = vote;
    }
    public int getVote()
    {
        return this.vote;
    }
}
