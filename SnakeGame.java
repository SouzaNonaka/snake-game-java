import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SnakeGame extends JPanel implements ActionListener, InputHandler.GameControls {


    private final Snake        snake        = new Snake();
    private final Food         food         = new Food();
    private final ScoreManager scoreManager = new ScoreManager();
    private final Renderer     renderer     = new Renderer();
    private final SoundManager sound        = new SoundManager();


    private GameState state     = GameState.MENU;
    private boolean   newRecord = false;


    private float alpha        = 0f;
    private long  accumNs      = 0L;
    private long  lastFrameNs  = 0L;

    private long moveIntervalNs = GameConfig.MOVE_INTERVAL_START_NS;
    private int  level          = 1;

    private Timer swingTimer;


    public SnakeGame() {
        setPreferredSize(new Dimension(GameConfig.WIDTH, GameConfig.HEIGHT));
        setBackground(GameConfig.BG);
        setFocusable(true);
        InputHandler input = new InputHandler(this);
        addKeyListener(input);
        addMouseListener(input.mouseAdapter());

        swingTimer = new Timer(16, this);
        swingTimer.start();
    }


    @Override
    public void startPlaying() {
        snake.reset();
        scoreManager.reset();
        long nowNs = System.nanoTime();
        food.spawn(snake, nowNs);
        food.resetEatCount();
        food.clearSpecial();
        newRecord      = false;
        alpha          = 0f;
        accumNs        = 0L;
        moveIntervalNs = GameConfig.MOVE_INTERVAL_START_NS;
        level          = 1;
        lastFrameNs    = nowNs;
        state          = GameState.PLAYING;
        sound.playStart();
    }

    @Override
    public void restart() {
        startPlaying();
    }

    @Override
    public void togglePause() {
        if (state == GameState.PLAYING) {
            state       = GameState.PAUSED;
            lastFrameNs = 0L;
            sound.playPause();
        } else if (state == GameState.PAUSED) {
            state       = GameState.PLAYING;
            lastFrameNs = System.nanoTime();
            sound.playPause();
        }
    }

    @Override
    public void toggleMute() {
        sound.toggleMute();
    }

    @Override
    public void quit() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) window.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (state == GameState.PLAYING) {
            long now = System.nanoTime();

            if (lastFrameNs == 0L) {
                lastFrameNs = now;
            }

            long delta  = Math.min(now - lastFrameNs, 100_000_000L);
            lastFrameNs = now;
            accumNs    += delta;

            if (accumNs >= moveIntervalNs) {
                accumNs -= moveIntervalNs;
                tick(now);
            }

            alpha = Math.min((float) accumNs / moveIntervalNs, 1f);
        }

        repaint();
    }

    private void tick(long nowNs) {
        snake.move();
        checkFood(nowNs);
        food.updateSpecial(nowNs);
        checkCollisions();
    }

    private void checkFood(long nowNs) {
        if (snake.headAt(food.getX(), food.getY())) {
            snake.grow();
            scoreManager.recordEat(1);
            food.onEat(snake, nowNs);
            food.spawn(snake, nowNs);
            updateSpeed();
            sound.playEat();
        }

        if (food.isSpecialActive() && snake.headAt(food.getSpecialX(), food.getSpecialY())) {
            snake.grow();
            scoreManager.recordEat(GameConfig.SPECIAL_FOOD_POINTS);
            food.clearSpecial();
            updateSpeed();
            sound.playSpecialEat();
        }
    }

    private void updateSpeed() {
        int score      = scoreManager.getScore();
        level          = score + 1;
        moveIntervalNs = Math.max(
                GameConfig.MOVE_INTERVAL_MIN_NS,
                GameConfig.MOVE_INTERVAL_START_NS - (long) score * GameConfig.SPEED_STEP_NS
        );
    }

    private void checkCollisions() {
        if (snake.collidesWithSelf() || snake.isOutOfBounds()) {
            newRecord = scoreManager.submitScore();
            state     = GameState.GAME_OVER;
            sound.playDeath();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        long nowNs = System.nanoTime();

        switch (state) {
            case MENU      -> renderer.drawMenu(g2, scoreManager, nowNs);
            case PLAYING   -> renderer.drawGame(g2, snake, food, scoreManager, alpha, nowNs, level);
            case PAUSED    -> {
                renderer.drawGame(g2, snake, food, scoreManager, alpha, nowNs, level);
                renderer.drawPause(g2);
            }
            case GAME_OVER -> renderer.drawGameOver(g2, scoreManager, newRecord);
        }

        if (sound.isMuted()) {
            g2.setFont(new Font("Arial", Font.PLAIN, 13));
            g2.setColor(new Color(180, 180, 180, 160));
            g2.drawString("🔇", GameConfig.WIDTH - 22, GameConfig.HEIGHT - 10);
        }
    }


    @Override public void requestDirection(Snake.Direction dir) { snake.requestDirection(dir); }
    @Override public GameState getState()                        { return state; }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game Pro");
            frame.add(new SnakeGame());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

