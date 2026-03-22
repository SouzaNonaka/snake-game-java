import java.util.Random;


public class Food {

    private final Random random = new Random();

    private int   x, y;
    private long  spawnTimeNs   = 0L;

    private boolean specialActive  = false;
    private int     specialX, specialY;
    private long    specialSpawnNs = 0L;
    private int     eatCount       = 0;

    private static final long PULSE_DURATION_NS = 400_000_000L;

    public Food() {}   

    public void spawn(Snake snake, long nowNs) {
        int cols = GameConfig.WIDTH  / GameConfig.TILE;
        int rows = GameConfig.HEIGHT / GameConfig.TILE;
        do {
            x = random.nextInt(cols) * GameConfig.TILE;
            y = random.nextInt(rows) * GameConfig.TILE;
        } while (snake.occupies(x, y) || (specialActive && x == specialX && y == specialY));

        spawnTimeNs = nowNs;
    }


    public float getPulseScale(long nowNs) {
        long elapsed = nowNs - spawnTimeNs;
        if (elapsed >= PULSE_DURATION_NS) return 1f;
        float t = (float) elapsed / PULSE_DURATION_NS;
        return 1f + 0.5f * (1f - t) * (1f - t);
    }

    public int  getX() { return x; }
    public int  getY() { return y; }

    public void onEat(Snake snake, long nowNs) {
        eatCount++;
        if (!specialActive && eatCount % GameConfig.SPECIAL_FOOD_EVERY == 0) {
            spawnSpecial(snake, nowNs);
        }
    }

    private void spawnSpecial(Snake snake, long nowNs) {
        int cols = GameConfig.WIDTH  / GameConfig.TILE;
        int rows = GameConfig.HEIGHT / GameConfig.TILE;
        do {
            specialX = random.nextInt(cols) * GameConfig.TILE;
            specialY = random.nextInt(rows) * GameConfig.TILE;
        } while (snake.occupies(specialX, specialY) || (specialX == x && specialY == y));

        specialActive  = true;
        specialSpawnNs = nowNs;
    }

    public void updateSpecial(long nowNs) {
        if (specialActive && (nowNs - specialSpawnNs) >= GameConfig.SPECIAL_FOOD_TTL_NS) {
            specialActive = false;
        }
    }

    /** @return) */
    public float getSpecialTimeLeft(long nowNs) {
        if (!specialActive) return 0f;
        return 1f - Math.min(1f, (float)(nowNs - specialSpawnNs) / GameConfig.SPECIAL_FOOD_TTL_NS);
    }

    public float getSpecialPulseScale(long nowNs) {
        if (!specialActive) return 1f;
        long elapsed = nowNs - specialSpawnNs;
        if (elapsed >= PULSE_DURATION_NS) return 1f;
        float t = (float) elapsed / PULSE_DURATION_NS;
        return 1f + 0.5f * (1f - t) * (1f - t);
    }

    public boolean isSpecialActive()           { return specialActive; }
    public int     getSpecialX()               { return specialX;      }
    public int     getSpecialY()               { return specialY;      }
    public void    clearSpecial()              { specialActive = false; }
    public void    resetEatCount()             { eatCount = 0;         }
}
