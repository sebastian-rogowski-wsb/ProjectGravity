package net.netgoogle.seb.panel;

import net.netgoogle.seb.model.Arrow;
import net.netgoogle.seb.model.Ball;
import net.netgoogle.seb.Constants;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.util.Random;

public class BallPanel extends Canvas {

    // Obiekty renderujące / buforujące
    private BufferStrategy strategy;
    private Graphics2D g2;

    // Piłki
    private final Ball[] balls = new Ball[2000];
    private Ball currentBall;
    private int ballCount;

    public static float gravity = 2000f;

    // Strzałka mocy
    private Arrow powerArrow;

    public BallPanel() {
        setPreferredSize(new Dimension(800, 600));
        setIgnoreRepaint(true);

        // Połącz wydarzenia
        MouseHandler mouseHandler = new MouseHandler();
        addMouseMotionListener(mouseHandler);
        addMouseListener(mouseHandler);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void startMainLoop() {
        long previousTime = System.currentTimeMillis();
        long currentTime;
        long elapsedTime;
        long totalElapsedTime = 0;

        while (true) {
            currentTime = System.currentTimeMillis();
            elapsedTime = (currentTime - previousTime); // upływający czas w sekundach
            totalElapsedTime += elapsedTime;

            if (totalElapsedTime > 1000) {
                totalElapsedTime = 0;
            }

            updateGame(elapsedTime / 1000f);
            render();

            try {
                Thread.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
            }

            previousTime = currentTime;
        }
    }

    public void clearBalls() {
        ballCount = 0;
    }

    public void setGravity(float pixelsPerSecond) {
        gravity = pixelsPerSecond;
    }

    public void generateBalls(int numBalls) {
        Random rand = new Random();
        for (int i = 0; i < numBalls; i++) {
            Ball tempBall = new Ball(rand.nextInt(10) + (getWidth() >> 1), rand.nextInt(10) + (getHeight() >> 1), 10, .1f);
            balls[ballCount] = tempBall;
            ballCount++;
        }
    }

    public void scatterBalls() {
        Random rand = new Random();
        for (int i = 0; i < this.ballCount; i++) {
            balls[i].velocity().set((rand.nextFloat() * 3000) - 1500, (rand.nextFloat() * 3000) - 1500);
        }
    }

    public void render() {
        if (strategy == null || strategy.contentsLost()) {
            // Utwórz BufferStrategy dla rysowania
            createBufferStrategy(2);

            strategy = getBufferStrategy();

            Graphics g = strategy.getDrawGraphics();
            this.g2 = (Graphics2D) g;
        }

        // Włącz anti-aliasing
        this.g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Rysuj tło
        this.g2.setColor(Color.BLACK);
        this.g2.fillRect(0, 0, getWidth(), getHeight());

        // Rysuj piłki
        for (int i = 0; i < ballCount; i++) {
            balls[i].draw(this.g2);
        }

        Ball tempBall = currentBall;
        if (tempBall != null) tempBall.draw(this.g2);

        // Rysuj pierwszy plan
        // Narysuj strzałę mocy, jeśli wystrzeliwujemy piłkę
        Arrow tempArrow = powerArrow;
        if (tempArrow != null) {
            tempArrow.draw(this.g2);

            this.g2.setColor(Color.WHITE);
            this.g2.drawString("", (tempArrow.x2() + tempArrow.x1()) / 2, (tempArrow.y1() + tempArrow.y2()) / 2);
        }

        // Jeśli nie ma piłek, wyświetl tekst pomocy na środku
        if (ballCount == 0 && currentBall == null) {
            String helpString = "Wciśnij oraz przeciągnij muszkę, żeby puścić piłkę w odpowiednią stronę.";

            this.g2.setColor(Color.WHITE);
            this.g2.drawString(helpString, getWidth() / 2 - (this.g2.getFontMetrics().stringWidth(helpString) / 2), getHeight() / 2);
        }

        // Narysuj liczbę piłek
        this.g2.setColor(Color.WHITE);
        this.g2.drawString("Ilość piłek: " + ballCount, 15, 15);

        if (!strategy.contentsLost()) strategy.show();
    }

    public void updateGame(float elapsedSeconds) {
        // Oblicz krok pozycji ruchomych obiektów na podstawie ich prędkości, grawitacji i czasu, który upłynął
        for (int i = 0; i < ballCount; i++) {
            balls[i].velocity().y(balls[i].velocity().y() + (gravity * (elapsedSeconds)));

            balls[i].position().x(balls[i].position().x() + (balls[i].velocity().x() * (elapsedSeconds)));
            balls[i].position().y(balls[i].position().y() + (balls[i].velocity().y() * (elapsedSeconds)));

            if (Math.abs(balls[i].velocity().x()) < Constants.EPSILON) balls[i].velocity().x(0);
            if (Math.abs(balls[i].velocity().y()) < Constants.EPSILON) balls[i].velocity().y(0);
        }
        checkCollisions();
    }

    // Sortowanie przez wstawianie
    public void insertionSort(Comparable[] a) {
        for (int p = 1; p < ballCount; p++) {
            Comparable tmp = a[p];
            int j = p;

            for (; j > 0 && tmp.compareTo(a[j - 1]) < 0; j--)
                a[j] = a[j - 1];

            a[j] = tmp;
        }
    }

    public void checkCollisions() {
        insertionSort(balls);

        // Sprawdź, czy nie ma kolizji ze ścianami
        for (int i = 0; i < ballCount; i++) {
            if (balls[i].position().x() - balls[i].getRadius() < 0) {
                balls[i].position().x(balls[i].getRadius()); 								 // Umieść piłke przeciwko krawędzi
                balls[i].velocity().x(-(balls[i].velocity().x() * Constants.RESTITUTION));   // Odwróć kierunek wektora i uwzględnij tarcie
                balls[i].velocity().y(balls[i].velocity().y() * Constants.RESTITUTION);
            } else if (balls[i].position().x() + balls[i].getRadius() > getWidth()) { 		 // Prawa ściana
            	balls[i].position().x(getWidth() - balls[i].getRadius());        			 // Umieść piłke przeciwko krawędzi
                balls[i].velocity().x(-(balls[i].velocity().x() * Constants.RESTITUTION));   // Odwróć kierunek wektora i uwzględnij tarcie
                balls[i].velocity().y((balls[i].velocity().y() * Constants.RESTITUTION));
            }

            if (balls[i].position().y() - balls[i].getRadius() < 0) {               	   // Górna ściana
            	balls[i].position().y(balls[i].getRadius());                			   // Umieść piłke przeciwko krawędzi
                balls[i].velocity().y(-(balls[i].velocity().y() * Constants.RESTITUTION)); // Odwróć kierunek wektora i uwzględnij tarcie
                balls[i].velocity().x((balls[i].velocity().x() * Constants.RESTITUTION));
            } else if (balls[i].position().y() + balls[i].getRadius() > getHeight()) {	   // Dolna ściana
            	balls[i].position().y(getHeight() - balls[i].getRadius());                 // Umieść piłke przeciwko krawędzi
                balls[i].velocity().y(-(balls[i].velocity().y() * Constants.RESTITUTION)); // Odwróć kierunek wektora i uwzględnij tarcie
                balls[i].velocity().x((balls[i].velocity().x() * Constants.RESTITUTION));
            }

            // Zderzenie piłki z piłką
            for (int j = i + 1; j < ballCount; j++) {
                if ((balls[i].position().x() + balls[i].getRadius()) < (balls[j].position().x() - balls[j].getRadius()))
                    break;

                if ((balls[i].position().y() + balls[i].getRadius()) < (balls[j].position().y() - balls[j].getRadius()) ||
                        (balls[j].position().y() + balls[j].getRadius()) < (balls[i].position().y() - balls[i].getRadius()))
                    continue;

                balls[i].resolveCollision(balls[j]);
            }
        }
    }

    private class MouseHandler extends MouseAdapter implements MouseMotionListener {
        public void mousePressed(MouseEvent e) {
            currentBall = new Ball(e.getX(), e.getY(), 15, 15);
            powerArrow = new Arrow(e.getX(), e.getY(), e.getX(), e.getY());
        }

        public void mouseReleased(MouseEvent e) {
            // Zmiana x/y na sekundę
            float xVector = (powerArrow.x2() - powerArrow.x1()) * 5;
            float yVector = (powerArrow.y2() - powerArrow.y1()) * 5;

            currentBall.velocity().set(xVector, yVector);
            balls[ballCount] = currentBall;
            ballCount++;

            currentBall = null;
            powerArrow = null;
        }

        public void mouseDragged(MouseEvent e) {
            int x1 = powerArrow.x1();
            int y1 = powerArrow.y1();
            int x2 = e.getX();
            int y2 = e.getY();
            int dx = Math.abs(x2 - x1);
            int dy = Math.abs(y2 - y1);

            if ((x2 - x1) < 0) {
                powerArrow.x2(x1 + dx);
            } else {
                powerArrow.x2(x1 - dx);
            }

            if ((y2 - y1) < 0) {
                powerArrow.y2(y1 + dy);
            } else {
                powerArrow.y2(y1 - dy);
            }
        }

    }

}
