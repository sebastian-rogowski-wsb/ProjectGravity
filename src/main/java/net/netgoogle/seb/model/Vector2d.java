package net.netgoogle.seb.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Accessors(fluent = true)
public class Vector2d {

    @Getter @Setter private float x;
    @Getter @Setter private float y;

    public Vector2d() {
        this.x(0);
        this.y(0);
    }

    public void set(float x, float y) {
        this.x(x);
        this.y(y);
    }

    public float dot(Vector2d v2) {
		return this.x() * v2.x() + this.y() * v2.y();
    }

    public float getLength() {
        return (float) Math.sqrt(x() * x() + y() * y());
    }

    public Vector2d add(Vector2d v2) {
        Vector2d result = new Vector2d();

        result.x(x() + v2.x());
        result.y(y() + v2.y());

        return result;
    }

    public Vector2d subtract(Vector2d v2) {
        Vector2d result = new Vector2d();

        result.x(this.x() - v2.x());
        result.y(this.y() - v2.y());

        return result;
    }

    public Vector2d multiply(float scaleFactor) {
        Vector2d result = new Vector2d();

        result.x(this.x() * scaleFactor);
        result.y(this.y() * scaleFactor);

        return result;
    }

    public Vector2d normalize() {
        float len = getLength();

        if (len != 0.0f) {
            this.x(this.x() / len);
            this.y(this.y() / len);
        } else {
            this.x(0.0f);
            this.y(0.0f);
        }

        return this;
    }

}
