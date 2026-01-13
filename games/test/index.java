import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class index extends JPanel {
    private final int GRID_SIZE = 20;
    private final int TILE_SIZE = 30;
    private ArrayList<Point> snake;
    private Point food;
    private Direction direction;
    private Direction nextDirection;
    private boolean gameOver;
    private int score;
    private Random random;

    enum Direction {
        UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);

        public final int dx, dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    public index() {
        setPreferredSize(new Dimension(GRID_SIZE * TILE_SIZE, GRID_SIZE * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        random = new Random();
        initGame();

        Timer timer = new Timer(100, e -> update());
        timer.start();
    }

    private void initGame() {
        snake = new ArrayList<>();
        snake.add(new Point(GRID_SIZE / 2, GRID_SIZE / 2));
        direction = Direction.RIGHT;
        nextDirection = Direction.RIGHT;
        gameOver = false;
        score = 0;
        spawnFood();
    }

    private void spawnFood() {
        Point newFood;
        do {
            newFood = new Point(random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE));
        } while (snake.contains(newFood));
        food = newFood;
    }

    private void handleKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (direction != Direction.DOWN) nextDirection = Direction.UP;
                break;
            case KeyEvent.VK_DOWN:
                if (direction != Direction.UP) nextDirection = Direction.DOWN;
                break;
            case KeyEvent.VK_LEFT:
                if (direction != Direction.RIGHT) nextDirection = Direction.LEFT;
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != Direction.LEFT) nextDirection = Direction.RIGHT;
                break;
            case KeyEvent.VK_SPACE:
                if (gameOver) initGame();
                repaint();
                break;
        }
    }

    private void update() {
        if (gameOver) {
            repaint();
            return;
        }

        direction = nextDirection;
        Point head = snake.get(0);
        Point newHead = new Point(
            (head.x + direction.dx + GRID_SIZE) % GRID_SIZE,
            (head.y + direction.dy + GRID_SIZE) % GRID_SIZE
        );

        // Check self collision
        if (snake.contains(newHead)) {
            gameOver = true;
            repaint();
            return;
        }

        snake.add(0, newHead);

        // Check food collision
        if (newHead.equals(food)) {
            score += 10;
            spawnFood();
        } else {
            snake.remove(snake.size() - 1);
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw grid
        g2.setColor(new Color(30, 30, 30));
        for (int i = 0; i <= GRID_SIZE; i++) {
            g2.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, GRID_SIZE * TILE_SIZE);
            g2.drawLine(0, i * TILE_SIZE, GRID_SIZE * TILE_SIZE, i * TILE_SIZE);
        }

        // Draw snake
        g2.setColor(Color.GREEN);
        for (int i = 0; i < snake.size(); i++) {
            Point p = snake.get(i);
            if (i == 0) {
                g2.setColor(new Color(0, 200, 0)); // Head
            } else {
                g2.setColor(Color.GREEN); // Body
            }
            g2.fillRect(p.x * TILE_SIZE + 2, p.y * TILE_SIZE + 2,
                    TILE_SIZE - 4, TILE_SIZE - 4);
        }

        // Draw food
        g2.setColor(Color.RED);
        g2.fillRect(food.x * TILE_SIZE + 2, food.y * TILE_SIZE + 2,
                TILE_SIZE - 4, TILE_SIZE - 4);

        // Draw score
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Score: " + score, 10, GRID_SIZE * TILE_SIZE + 25);

        // Draw game over message
        if (gameOver) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, GRID_SIZE * TILE_SIZE, GRID_SIZE * TILE_SIZE);
            g2.setColor(Color.RED);
            g2.setFont(new Font("Arial", Font.BOLD, 40));
            String text = "GAME OVER";
            FontMetrics fm = g2.getFontMetrics();
            int x = (GRID_SIZE * TILE_SIZE - fm.stringWidth(text)) / 2;
            int y = (GRID_SIZE * TILE_SIZE - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, x, y);

            g2.setFont(new Font("Arial", Font.PLAIN, 20));
            String restart = "Press SPACE to restart";
            fm = g2.getFontMetrics();
            x = (GRID_SIZE * TILE_SIZE - fm.stringWidth(restart)) / 2;
            g2.drawString(restart, x, y + 40);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new index());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}