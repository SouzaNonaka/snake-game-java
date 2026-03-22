
public record SnakeSegment(int x, int y, int prevX, int prevY) {

    public SnakeSegment moveTo(int newX, int newY) {
        return new SnakeSegment(newX, newY, x, y);
    }

    public SnakeSegment withPrev(int px, int py) {
        return new SnakeSegment(x, y, px, py);
    }
    public float interpX(float alpha) {
        return prevX + (x - prevX) * alpha;
    }
    public float interpY(float alpha) {
        return prevY + (y - prevY) * alpha;
    }
}
