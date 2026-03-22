import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {

    //  Configs
    static final int LARGURA = 600;
    static final int ALTURA = 600;
    static final int TAMANHO = 25;
    static final int UNIDADES = (LARGURA * ALTURA) / (TAMANHO * TAMANHO);
    static final int DELAY = 100;

    //  Cores
    static final Color COR_FUNDO = Color.black;
    static final Color COR_CABECA = Color.green;
    static final Color COR_CORPO = new Color(45, 180, 0);
    static final Color COR_COMIDA = Color.red;
    static final Color COR_GAME_OVER = Color.red;

    // Textos
    static final String TEXTO_GAME_OVER = "Game Over";

    //  Fontes
    static final Font FONTE_GAME_OVER = new Font("Ink Free", Font.BOLD, 40);

    final int x[] = new int[UNIDADES];
    final int y[] = new int[UNIDADES];

    int partes = 6;
    int comidaX;
    int comidaY;
    int direcao = 'R';
    boolean rodando = false;
    boolean mudouDirecao = false;

    Timer timer;
    Random random;

    SnakeGame() {
        random = new Random();
        this.setPreferredSize(new Dimension(LARGURA, ALTURA));
        this.setBackground(COR_FUNDO);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        iniciarJogo();
    }

    public void iniciarJogo() {
        novaComida();
        rodando = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        desenhar(g);
    }

    public void desenhar(Graphics g) {
        if (rodando) {

            // 🍎 comida
            g.setColor(COR_COMIDA);
            g.fillOval(comidaX, comidaY, TAMANHO, TAMANHO);

            // 🐍 cobra
            for (int i = 0; i < partes; i++) {
                if (i == 0) {
                    g.setColor(COR_CABECA);
                } else {
                    g.setColor(COR_CORPO);
                }
                g.fillRect(x[i], y[i], TAMANHO, TAMANHO);
            }

        } else {
            gameOver(g);
        }
    }

    public void novaComida() {
        comidaX = random.nextInt((int)(LARGURA / TAMANHO)) * TAMANHO;
        comidaY = random.nextInt((int)(ALTURA / TAMANHO)) * TAMANHO;
    }

    public void mover() {
        for (int i = partes; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direcao) {
            case 'U' -> y[0] -= TAMANHO;
            case 'D' -> y[0] += TAMANHO;
            case 'L' -> x[0] -= TAMANHO;
            case 'R' -> x[0] += TAMANHO;
        }
    }

    public void verificarComida() {
        if (x[0] == comidaX && y[0] == comidaY) {
            partes++;
            novaComida();
        }
    }

    public void verificarColisoes() {
        for (int i = partes; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                rodando = false;
            }
        }

        if (x[0] < 0 || x[0] >= LARGURA || y[0] < 0 || y[0] >= ALTURA) {
            rodando = false;
        }

        if (!rodando) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        g.setColor(COR_GAME_OVER);
        g.setFont(FONTE_GAME_OVER);

        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString(
                TEXTO_GAME_OVER,
                (LARGURA - metrics.stringWidth(TEXTO_GAME_OVER)) / 2,
                ALTURA / 2
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (rodando) {
            mudouDirecao = false;
            mover();
            verificarComida();
            verificarColisoes();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {

            if (mudouDirecao) return;

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    if (direcao != 'R') {
                        direcao = 'L';
                        mudouDirecao = true;
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direcao != 'L') {
                        direcao = 'R';
                        mudouDirecao = true;
                    }
                }
                case KeyEvent.VK_UP -> {
                    if (direcao != 'D') {
                        direcao = 'U';
                        mudouDirecao = true;
                    }
                }
                case KeyEvent.VK_DOWN -> {
                    if (direcao != 'U') {
                        direcao = 'D';
                        mudouDirecao = true;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        SnakeGame jogo = new SnakeGame();

        frame.add(jogo);
        frame.setTitle("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}