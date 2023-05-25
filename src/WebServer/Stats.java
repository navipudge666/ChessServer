package WebServer;

public class Stats
{
    public int wins;
    public int loses;

    public Stats(int wins, int loses)
    {
        this.wins = wins;
        this.loses = loses;
    }

    public int getWins()
    {
        return wins;
    }

    public void incWins()
    {
        this.wins++;
    }

    public int getLoses()
    {
        return loses;
    }

    public void incLoses()
    {
        this.loses++;
    }
}
