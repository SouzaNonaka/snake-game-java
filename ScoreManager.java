import java.util.prefs.Preferences;

public class ScoreManager {

    private static final String PREF_KEY = "highscore";
    private final Preferences prefs = Preferences.userNodeForPackage(ScoreManager.class);

    private int score     = 0;
    private int highScore;

    public ScoreManager() {
        highScore = prefs.getInt(PREF_KEY, 0);
    }

    public void recordEat(int points) {
        score += points;
    }

    public void reset() {
        score = 0;
    }

    /** @return*/
    public boolean submitScore() {
        if (score > highScore) {
            highScore = score;
            prefs.putInt(PREF_KEY, highScore);
            return true;
        }
        return false;
    }

    public int getScore()     { return score;     }
    public int getHighScore() { return highScore;  }
}
