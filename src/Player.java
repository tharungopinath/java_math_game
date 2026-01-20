//holds name and score and game summary
public class Player {
    public String name;
    public int score;
    public String summary; //stores total info for questions/answers for the player

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.summary = "";
    }
}