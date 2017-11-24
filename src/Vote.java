import java.io.Serializable;

public class Vote implements Serializable{
    byte[] vote;
    public Vote(byte[] vote)
    {
        this.vote = vote;
    }
    public byte[] getVote()
    {
        return this.vote;
    }
}
