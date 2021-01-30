package net.netgoogle.seb.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.netgoogle.seb.Constants;

import java.awt.Color;
import java.awt.Graphics2D;

@Accessors(fluent = true)
public class Ball implements Comparable<Ball> {

    @Getter @Setter private Vector2d velocity;
    @Getter @Setter private Vector2d position;
    @Getter @Setter private float mass;
    @Getter @Setter private float radius;

    public Ball(float x, float y, float radius, float mass) {
        this.velocity = new Vector2d(0, 0);
        this.position = new Vector2d(x, y);
        this.setMass(mass);
        this.setRadius(radius);
    }

    public Color getBallColor(float magnitude) {
        float maxMagnitude = 1000; // Dostosuj to, aby uzyskać odpowiedni zakres kolorów

        magnitude = Math.min(magnitude, maxMagnitude);

        float H = (magnitude / maxMagnitude) * 0.38f;  // 0.4f = zielony
        float S = 0.98f;
        float B = 0.95f;

        return Color.getHSBColor(H, S, B);
    }


    public void draw(Graphics2D g2) {
        g2.setColor(getBallColor(velocity.getLength()));
		g2.fillOval((int) (position.x() - getRadius()), (int) (position.y() - getRadius()), (int) (2 * getRadius()), (int) (2 * getRadius()));
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }

    public void resolveCollision(Ball ball) {
        Vector2d delta = (position.subtract(ball.position));
        float r = getRadius() + ball.getRadius();
        float dist2 = delta.dot(delta);

        if (dist2 > r * r) return; // Nie kolidują

        float d = delta.getLength();

        Vector2d mtd;
		if (d == 0.0f) {
			d = ball.getRadius() + getRadius() - 1.0f;
			delta = new Vector2d(ball.getRadius() + getRadius(), 0.0f);
		}
		mtd = delta.multiply(((getRadius() + ball.getRadius()) - d) / d); // Minimalna odległość potrzebna do rozsunięcia piłek

        // Rozwiązać skrzyżowanie
        float im1 = 1 / getMass(); // odwrotność mas
        float im2 = 1 / ball.getMass();

        // Rozłączenie
        position = position.add(mtd.multiply(im1 / (im1 + im2)));
        ball.position = ball.position.subtract(mtd.multiply(im2 / (im1 + im2)));

        // Prędkość uderzenia
        Vector2d v = (this.velocity.subtract(ball.velocity));
        float vn = v.dot(mtd.normalize());

        // Kula przecinająca się, ale oddalająca się już od siebie
        if (vn > 0.0f) return;

        // impuls zderzeniowy
        float i = (-(1.0f + Constants.RESTITUTION) * vn) / (im1 + im2);
        Vector2d impulse = mtd.multiply(i);

        // zmiana pędu
        this.velocity = this.velocity.add(impulse.multiply(im1));
        ball.velocity = ball.velocity.subtract(impulse.multiply(im2));
    }

    private void setMass(float mass) {
        this.mass = mass;
    }

    private float getMass() {
        return mass;
    }

    public int compareTo(Ball ball) {
		return Float.compare(this.position.x() - this.getRadius(), ball.position.x() - ball.getRadius());
    }

}
