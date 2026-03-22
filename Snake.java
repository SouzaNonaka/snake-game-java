import java.util.ArrayList;
import java.util.List;

public class Snake {

    public enum Direction {
        UP, DOWN, LEFT, RIGHT;

        public boolean isOpposite(Direction other) {
            return (this == UP && other == DOWN)
                || (this == DOWN && other == UP)
                || (this == LEFT && other == RIGHT)
                || (this == RIGHT && other == LEFT);
        }
    }

    private static final int INITIAL_SIZE = 6;
    private static final int TILE = GameConfig.TILE;

    private final List<SnakeSegment> body = new ArrayList<>();
    private Direction direction = Direction.RIGHT;
    private Direction pendingDirection = Direction.RIGHT;
    private boolean directionChanged = false;

    public Snake() {
        reset();
    }

    public void reset() {
        body.clear();
        direction = Direction.RIGHT;
        pendingDirection = Direction.RIGHT;
        directionChanged = false;

        for (int i = 0; i < INITIAL_SIZE; i++) {
            body.add(new SnakeSegment(0, 0, 0, 0));
        }
    }

  
    public void requestDirection(Direction newDir) {
        if (!directionChanged && !newDir.isOpposite(direction)) {
            pendingDirection = newDir;
            directionChanged = true;
        }
    }


    public void move() {
        direction = pendingDirection;
        directionChanged = false;

        int headX = body.get(0).x();
        int headY = body.get(0).y();

        int newX = headX + switch (direction) {
            case LEFT  -> -TILE;
            case RIGHT -> +TILE;
            default    -> 0;
        };

        int newY = headY + switch (direction) {
            case UP   -> -TILE;
            case DOWN -> +TILE;
            default   -> 0;
        };

        for (int i = body.size() - 1; i > 0; i--) {
            SnakeSegment prev = body.get(i - 1);
            body.set(i, body.get(i).moveTo(prev.x(), prev.y()));
        }

        body.set(0, body.get(0).moveTo(newX, newY));
    }


    public void grow() {
        SnakeSegment tail = body.get(body.size() - 1);
        body.add(new SnakeSegment(tail.x(), tail.y(), tail.prevX(), tail.prevY()));
    }

    public boolean collidesWithSelf() {
        SnakeSegment head = body.get(0);
        for (int i = 1; i < body.size(); i++) {
            if (head.x() == body.get(i).x() && head.y() == body.get(i).y()) return true;
        }
        return false;
    }

    public boolean isOutOfBounds() {
        int hx = body.get(0).x();
        int hy = body.get(0).y();
        return hx < 0 || hx >= GameConfig.WIDTH || hy < 0 || hy >= GameConfig.HEIGHT;
    }

    public boolean headAt(int x, int y) {
        return body.get(0).x() == x && body.get(0).y() == y;
    }

    public boolean occupies(int x, int y) {
        for (SnakeSegment seg : body) {
            if (seg.x() == x && seg.y() == y) return true;
        }
        return false;
    }

    public List<SnakeSegment> getBody() {
        return body;
    }

    public int size() {
        return body.size();
    }
}
