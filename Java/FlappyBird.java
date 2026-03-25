package lab1pt2;
/**
 *
 * @author Niel
 */
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    // Board settings
    private static final int BOARD_WIDTH = 360;
    private static final int BOARD_HEIGHT = 640;

    // Pipe settings
    private static final int PIPE_WIDTH = 64;
    private static final int PIPE_HEIGHT = 512;
    private static final int BASE_PIPE_GAP = BOARD_HEIGHT / 4;
    private static final int GAP_DECREASE_AMOUNT = 15;
    private static final int MIN_PIPE_GAP = 100;
    private static final int PIPE_SPAWN_DISTANCE = 220;

    // Bird settings
    private static final int BIRD_WIDTH = 48;
    private static final int BIRD_HEIGHT = 40;
    private static final int DRAW_BIRD_WIDTH = BIRD_WIDTH;
    private static final int DRAW_BIRD_HEIGHT = BIRD_HEIGHT;

    // Physics
    private static final int GRAVITY = 1;
    private static final int JUMP_VELOCITY = -9;

    // Pipe speed settings
    private static final int BASE_PIPE_VELOCITY = -4;
    private static final int SPEED_INCREASE_AMOUNT = 1;
    private static final int MAX_PIPE_SPEED = -10;

    private Image backgroundImg, birdImg, topPipeImg, bottomPipeImg;

    private Bird bird;
    private ArrayList<Pipe> pipes = new ArrayList<>();
    private Timer gameLoop;

    private int velocityY = 0;
    private boolean gameOver = false;
    private boolean gameStarted = false;
    private int score = 0;

    private class Bird {
        int x = BOARD_WIDTH / 8;
        int y = BOARD_HEIGHT / 2;
        Image img;

        Bird(Image img) {
            this.img = img;
        }

        void update() {
            velocityY += GRAVITY;
            y += velocityY;
            y = Math.max(y, 0);
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, DRAW_BIRD_WIDTH, DRAW_BIRD_HEIGHT);
        }
    }

    private class Pipe {
        int x = BOARD_WIDTH;
        int y;
        boolean passed = false;
        Image img;

        Pipe(int y, Image img) {
            this.y = y;
            this.img = img;
        }

        void update() {
            x += getCurrentPipeVelocity();
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, PIPE_WIDTH, PIPE_HEIGHT);
        }

        boolean isOffScreen() {
            return x + PIPE_WIDTH < 0;
        }
    }

    public FlappyBird() {
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        // Load images
        backgroundImg = loadImage("/flappybirdbg.png");
        birdImg = loadImage("/flappybird.png");
        topPipeImg = loadImage("/toppipe.png");
        bottomPipeImg = loadImage("/bottompipe.png");

        bird = new Bird(birdImg);

        // Game loop
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    private Image loadImage(String path) {
        java.net.URL imageURL = getClass().getResource(path);

        if (imageURL == null) {
            System.out.println("Could not find image: " + path);
            return null;
        }

        return new ImageIcon(imageURL).getImage();
    }

    private int getCurrentPipeGap() {
        int level = score / 10;
        int newGap = BASE_PIPE_GAP - (level * GAP_DECREASE_AMOUNT);
        return Math.max(newGap, MIN_PIPE_GAP);
    }

    private int getCurrentPipeVelocity() {
        int level = score / 10;
        int newSpeed = BASE_PIPE_VELOCITY - (level * SPEED_INCREASE_AMOUNT);
        return Math.max(newSpeed, MAX_PIPE_SPEED);
    }

    private void placePipes() {
        int currentGap = getCurrentPipeGap();
        int randomY = -PIPE_HEIGHT / 4 - (int) (Math.random() * (PIPE_HEIGHT / 2));

        pipes.add(new Pipe(randomY, topPipeImg));
        pipes.add(new Pipe(randomY + PIPE_HEIGHT + currentGap, bottomPipeImg));
    }

    private boolean shouldSpawnPipe() {
        // Find the most recent top pipe
        for (int i = pipes.size() - 1; i >= 0; i--) {
            Pipe pipe = pipes.get(i);

            if (pipe.img == topPipeImg) {
                return pipe.x <= BOARD_WIDTH - PIPE_SPAWN_DISTANCE;
            }
        }

        // No pipes yet, so spawn the first pair
        return true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        // Background
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);
        } else {
            g.setColor(new Color(135, 206, 235));
            g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        }

        // Bird
        if (bird.img != null) {
            g.drawImage(bird.img, bird.x, bird.y, DRAW_BIRD_WIDTH, DRAW_BIRD_HEIGHT, null);
        } else {
            g.setColor(Color.YELLOW);
            g.fillOval(bird.x, bird.y, DRAW_BIRD_WIDTH, DRAW_BIRD_HEIGHT);
        }

        // Pipes
        for (Pipe pipe : pipes) {
            if (pipe.img != null) {
                g.drawImage(pipe.img, pipe.x, pipe.y, PIPE_WIDTH, PIPE_HEIGHT, null);
            } else {
                g.setColor(Color.GREEN);
                g.fillRect(pipe.x, pipe.y, PIPE_WIDTH, PIPE_HEIGHT);
            }
        }

        // Score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.drawString("Score: " + score, 10, 35);

        if (!gameStarted && !gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Press SPACE to Start", 55, BOARD_HEIGHT / 2);
        }

        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString("Game Over", 110, BOARD_HEIGHT / 2 - 20);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press SPACE to Restart", 70, BOARD_HEIGHT / 2 + 20);
        }
    }

    private void update() {
        if (!gameStarted || gameOver) {
            return;
        }

        if (shouldSpawnPipe()) {
            placePipes();
        }

        bird.update();

        Iterator<Pipe> iterator = pipes.iterator();
        while (iterator.hasNext()) {
            Pipe pipe = iterator.next();
            pipe.update();

            // Only score once per pipe pair using the top pipe
            if (!pipe.passed && pipe.img == topPipeImg && bird.x > pipe.x + PIPE_WIDTH) {
                score++;
                pipe.passed = true;
            }

            if (checkCollision(bird, pipe)) {
                gameOver = true;
                stopGame();
            }

            if (pipe.isOffScreen()) {
                iterator.remove();
            }
        }

        if (bird.y + DRAW_BIRD_HEIGHT >= BOARD_HEIGHT) {
            bird.y = BOARD_HEIGHT - DRAW_BIRD_HEIGHT;
            gameOver = true;
            stopGame();
        }
    }

    private boolean checkCollision(Bird bird, Pipe pipe) {
        return bird.getBounds().intersects(pipe.getBounds());
    }

    private void stopGame() {
        gameLoop.stop();
    }

    private void restartGame() {
        bird.y = BOARD_HEIGHT / 2;
        velocityY = 0;
        pipes.clear();
        gameOver = false;
        gameStarted = false;
        score = 0;
        gameLoop.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {

            if (gameOver) {
                restartGame();
                gameStarted = true;
                velocityY = JUMP_VELOCITY;
                return;
            }

            if (!gameStarted) {
                gameStarted = true;
            }

            velocityY = JUMP_VELOCITY;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        flappyBird.requestFocusInWindow();
    }
}