import java.awt.*;
import java.util.List;


public class Renderer {

    private static final int TILE      = GameConfig.TILE;
    private static final int HALF_TILE = TILE / 2;

    public void drawGame(Graphics2D g, Snake snake, Food food,
                         ScoreManager score, float alpha, long nowNs, int level) {
        drawGrid(g);
        drawFood(g, food, nowNs);
        drawSpecialFood(g, food, nowNs);
        drawSnake(g, snake, alpha);
        drawHUD(g, score, level);
    }
    
    public void drawMenu(Graphics2D g, ScoreManager score, long nowNs) {
        double pulse = 0.75 + 0.25 * Math.sin(nowNs / 400_000_000.0);

        g.setFont(new Font("Arial", Font.BOLD, 52));
        FontMetrics fm = g.getFontMetrics();
        String title = "SNAKE";

        g.setColor(new Color(0, 80, 30, 120));
        g.drawString(title, centerX(fm, title) + 3, 160 + 3);

        g.setColor(new Color(50, 255, 100, (int)(200 * pulse)));
        g.drawString(title, centerX(fm, title), 160);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        fm = g.getFontMetrics();
        g.setColor(new Color(100, 180, 120, 180));
        String sub = "P R O";
        g.drawString(sub, centerX(fm, sub), 182);

        g.setFont(GameConfig.UI_FONT);
        fm = g.getFontMetrics();
        g.setColor(new Color(255, 215, 0, 200));
        String best = "Recorde: " + score.getHighScore();
        g.drawString(best, centerX(fm, best), 260);

        drawMenuButton(g, "▶  JOGAR", GameConfig.HEIGHT / 2 + 20, true, nowNs);

        g.setFont(new Font("Arial", Font.PLAIN, 13));
        fm = g.getFontMetrics();
        g.setColor(new Color(150, 150, 160));
        String controls = "Setas ou WASD para mover  •  P para pausar  •  M para mudo";
        g.drawString(controls, centerX(fm, controls), GameConfig.HEIGHT - 30);
    }

    private void drawMenuButton(Graphics2D g, String label, int centerY, boolean highlight, long nowNs) {
        int bw = 180, bh = 44;
        int bx = (GameConfig.WIDTH - bw) / 2;
        int by = centerY - bh / 2;

        double pulse = highlight ? 0.85 + 0.15 * Math.sin(nowNs / 300_000_000.0) : 1.0;
        int alpha    = highlight ? (int)(180 * pulse) : 100;

        g.setColor(new Color(40, 160, 70, alpha));
        g.fillRoundRect(bx, by, bw, bh, 12, 12);

        g.setColor(new Color(80, 255, 120, alpha));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(bx, by, bw, bh, 12, 12);

        g.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.WHITE);
        g.drawString(label, centerX(fm, label), by + bh / 2 + fm.getAscent() / 2 - 1);
    }


    public void drawPause(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        int pw = 280, ph = 130;
        int px = (GameConfig.WIDTH  - pw) / 2;
        int py = (GameConfig.HEIGHT - ph) / 2;

        g.setColor(new Color(20, 25, 30, 230));
        g.fillRoundRect(px, py, pw, ph, 16, 16);

        g.setColor(new Color(80, 255, 120, 140));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(px, py, pw, ph, 16, 16);

        g.setFont(new Font("Arial", Font.BOLD, 28));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(GameConfig.TEXT);
        String paused = "PAUSADO";
        g.drawString(paused, centerX(fm, paused), py + 52);

        g.setFont(new Font("Arial", Font.PLAIN, 14));
        fm = g.getFontMetrics();
        g.setColor(new Color(180, 180, 190));
        String hint = "Pressione P para continuar";
        g.drawString(hint, centerX(fm, hint), py + 85);

        String esc = "ESC para sair";
        g.drawString(esc, centerX(fm, esc), py + 108);
    }


    public void drawGameOver(Graphics2D g, ScoreManager score, boolean newRecord) {
        g.setColor(GameConfig.FOOD);
        g.setFont(GameConfig.TITLE_FONT);
        FontMetrics fm = g.getFontMetrics();
        String title = "Game Over";
        g.drawString(title, centerX(fm, title), GameConfig.HEIGHT / 2 - 20);

        g.setFont(GameConfig.UI_FONT);
        g.setColor(GameConfig.TEXT);
        fm = g.getFontMetrics();

        String scoreLine = "Score: " + score.getScore();
        String highLine  = newRecord
                ? "🏆 New Record: " + score.getHighScore() + "!"
                : "High Score: " + score.getHighScore();

        g.drawString(scoreLine, centerX(fm, scoreLine), GameConfig.HEIGHT / 2 + 30);

        if (newRecord) g.setColor(new Color(255, 215, 0));
        g.drawString(highLine, centerX(fm, highLine), GameConfig.HEIGHT / 2 + 55);

        g.setColor(GameConfig.TEXT);
        String restart = "Pressione R para jogar novamente";
        String quit    = "Pressione ESC para sair";
        g.drawString(restart, centerX(fm, restart), GameConfig.HEIGHT / 2 + 95);
        g.drawString(quit,    centerX(fm, quit),    GameConfig.HEIGHT / 2 + 115);
    }


    private void drawGrid(Graphics2D g) {
        g.setColor(GameConfig.GRID);
        g.setStroke(new BasicStroke(1f));
        for (int x = 0; x <= GameConfig.WIDTH; x += TILE) {
            g.drawLine(x, 0, x, GameConfig.HEIGHT);
        }
        for (int y = 0; y <= GameConfig.HEIGHT; y += TILE) {
            g.drawLine(0, y, GameConfig.WIDTH, y);
        }
    }

    private void drawFood(Graphics2D g, Food food, long nowNs) {
        float scale = food.getPulseScale(nowNs);
        drawPulsedCircle(g, food.getX(), food.getY(), GameConfig.FOOD, GameConfig.FOOD_PULSE, scale);
    }

    private void drawSpecialFood(Graphics2D g, Food food, long nowNs) {
        if (!food.isSpecialActive()) return;

        float scale    = food.getSpecialPulseScale(nowNs);
        float timeLeft = food.getSpecialTimeLeft(nowNs);

        int cx = food.getSpecialX() + HALF_TILE;
        int cy = food.getSpecialY() + HALF_TILE;
        int ringRadius = (int)(TILE * 0.85f);

        g.setStroke(new BasicStroke(2.5f));
        g.setColor(new Color(
                GameConfig.SPECIAL_FOOD.getRed(),
                GameConfig.SPECIAL_FOOD.getGreen(),
                GameConfig.SPECIAL_FOOD.getBlue(),
                (int)(80 + 80 * timeLeft)
        ));
        g.drawOval(cx - ringRadius, cy - ringRadius, ringRadius * 2, ringRadius * 2);

        drawPulsedCircle(g, food.getSpecialX(), food.getSpecialY(),
                GameConfig.SPECIAL_FOOD, GameConfig.SPECIAL_PULSE, scale);

        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.setColor(Color.BLACK);
        g.drawString("x3", food.getSpecialX() + 7, food.getSpecialY() + 16);
    }

    private void drawPulsedCircle(Graphics2D g, int tileX, int tileY,
                                   Color main, Color glow, float scale) {
        int size   = (int)(TILE * scale);
        int offset = (TILE - size) / 2;

        g.setColor(new Color(glow.getRed(), glow.getGreen(), glow.getBlue(), 60));
        int glowSize = size + 6;
        int glowOff  = (TILE - glowSize) / 2;
        g.fillOval(tileX + glowOff, tileY + glowOff, glowSize, glowSize);

        g.setColor(main);
        g.fillOval(tileX + offset, tileY + offset, size, size);
    }

    private void drawSnake(Graphics2D g, Snake snake, float alpha) {
        List<SnakeSegment> body = snake.getBody();
        if (body.size() < 2) return;

        g.setColor(GameConfig.BODY);
        g.setStroke(new BasicStroke(TILE, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (int i = 1; i < body.size(); i++) {
            SnakeSegment a = body.get(i - 1);
            SnakeSegment b = body.get(i);

            int x1 = (int) a.interpX(alpha) + HALF_TILE;
            int y1 = (int) a.interpY(alpha) + HALF_TILE;
            int x2 = (int) b.interpX(alpha) + HALF_TILE;
            int y2 = (int) b.interpY(alpha) + HALF_TILE;

            g.drawLine(x1, y1, x2, y2);
        }

        SnakeSegment head = body.get(0);
        int hx = (int) head.interpX(alpha);
        int hy = (int) head.interpY(alpha);

        g.setColor(GameConfig.HEAD);
        g.fillOval(hx, hy, TILE, TILE);
    }


    private void drawHUD(Graphics2D g, ScoreManager score, int level) {
        g.setStroke(new BasicStroke(1));
        g.setFont(GameConfig.UI_FONT);

        g.setColor(GameConfig.TEXT);
        g.drawString("Score: " + score.getScore(),     GameConfig.WIDTH - 125, 25);
        g.drawString("Best:  " + score.getHighScore(), GameConfig.WIDTH - 125, 45);

        g.setColor(levelColor(level));
        g.drawString("Lv " + level, 10, 25);

        drawSpeedBar(g, level);
    }

    private void drawSpeedBar(Graphics2D g, int level) {
        int maxLevel = 50;
        float pct    = Math.min(1f, (float) level / maxLevel);
        int barW     = (int)(GameConfig.WIDTH * pct);

        g.setColor(new Color(30, 30, 40));
        g.fillRect(0, GameConfig.HEIGHT - 3, GameConfig.WIDTH, 3);

        g.setColor(levelColor(level));
        g.fillRect(0, GameConfig.HEIGHT - 3, barW, 3);
    }

    private Color levelColor(int level) {
        float t = Math.min(1f, level / 40f);
        int r   = (int)(50  + 205 * t);
        int gr  = (int)(255 - 155 * t);
        return new Color(r, gr, 50);
    }

    private int centerX(FontMetrics fm, String text) {
        return (GameConfig.WIDTH - fm.stringWidth(text)) / 2;
    }
}

