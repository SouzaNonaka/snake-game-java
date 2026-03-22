import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InputHandler extends KeyAdapter {

    public interface GameControls {
        void requestDirection(Snake.Direction dir);
        void startPlaying();
        void togglePause();
        void restart();
        void quit();
        void toggleMute();
        GameState getState();
    }

    private final GameControls controls;

    public InputHandler(GameControls controls) {
        this.controls = controls;
    }


    public MouseAdapter mouseAdapter() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                switch (controls.getState()) {
                    case MENU      -> controls.startPlaying();
                    case GAME_OVER -> controls.restart();
                    default        -> {}
                }
            }
        };
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (controls.getState()) {
            case MENU      -> handleMenuInput(e);
            case PLAYING   -> handleGameInput(e);
            case PAUSED    -> handlePausedInput(e);
            case GAME_OVER -> handleGameOverInput(e);
        }
    }

    private void handleMenuInput(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> controls.startPlaying();
            case KeyEvent.VK_ESCAPE                   -> controls.quit();
            case KeyEvent.VK_M                        -> controls.toggleMute();
        }
    }

    private void handleGameInput(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT,  KeyEvent.VK_A -> controls.requestDirection(Snake.Direction.LEFT);
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> controls.requestDirection(Snake.Direction.RIGHT);
            case KeyEvent.VK_UP,    KeyEvent.VK_W -> controls.requestDirection(Snake.Direction.UP);
            case KeyEvent.VK_DOWN,  KeyEvent.VK_S -> controls.requestDirection(Snake.Direction.DOWN);
            case KeyEvent.VK_P, KeyEvent.VK_ESCAPE -> controls.togglePause();
            case KeyEvent.VK_M                     -> controls.toggleMute();
        }
    }

    private void handlePausedInput(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_P, KeyEvent.VK_ESCAPE -> controls.togglePause();
            case KeyEvent.VK_M                     -> controls.toggleMute();
        }
    }

    private void handleGameOverInput(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_R, KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> controls.restart();
            case KeyEvent.VK_ESCAPE                                   -> controls.quit();
            case KeyEvent.VK_M                                        -> controls.toggleMute();
        }
    }
}
