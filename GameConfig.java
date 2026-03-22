import java.awt.*;

public final class GameConfig {

    private GameConfig() {}

    public static final int WIDTH  = 600;
    public static final int HEIGHT = 600;
    public static final int TILE   = 25;


    public static final int TIMER_DELAY_MS = 16;


    public static final long MOVE_INTERVAL_START_NS = 120_000_000L;

    public static final long MOVE_INTERVAL_MIN_NS = 45_000_000L;

    public static final long SPEED_STEP_NS = 750_000L;

    public static final int  SPECIAL_FOOD_EVERY  = 5;
    public static final long SPECIAL_FOOD_TTL_NS = 7_000_000_000L;
    public static final int  SPECIAL_FOOD_POINTS = 3;

    // Colors
    public static final Color BG           = new Color(10, 10, 15);
    public static final Color GRID         = new Color(255, 255, 255, 6);
    public static final Color HEAD         = new Color(50, 255, 100);
    public static final Color BODY         = new Color(40, 180, 80);
    public static final Color FOOD         = new Color(255, 50,  50);
    public static final Color FOOD_PULSE   = new Color(255, 120, 120);
    public static final Color SPECIAL_FOOD = new Color(255, 215, 0);
    public static final Color SPECIAL_PULSE= new Color(255, 255, 150);
    public static final Color TEXT         = Color.WHITE;

    // Fonts
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 40);
    public static final Font UI_FONT    = new Font("Arial", Font.PLAIN, 18);
}
