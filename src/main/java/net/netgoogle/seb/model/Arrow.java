package net.netgoogle.seb.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.awt.Graphics;

@AllArgsConstructor
@Accessors(fluent = true)
public class Arrow {

    @Getter @Setter private int x1;
    @Getter @Setter private int y1;
    @Getter @Setter private int x2;
    @Getter @Setter private int y2;

    public void draw(Graphics g) {
        if ((x1() != x2()) && (y1() != y2())) {
            float arrowWidth = 10.0f;
            float theta = 0.423f;
            int[] xPoints = new int[3];
            int[] yPoints = new int[3];
            float[] vecLine = new float[2];
            float[] vecLeft = new float[2];
            float fLength;
            float th;
            float ta;
            float baseX, baseY;

            xPoints[0] = x2();
            yPoints[0] = y2();

            // Zbuduj wektor liniowy
            vecLine[0] = (float) xPoints[0] - x1();
            vecLine[1] = (float) yPoints[0] - y1();

            // Zbuduj wektor bazowy strzałki - normalny do linii
            vecLeft[0] = -vecLine[1];
            vecLeft[1] = vecLine[0];

            // Ustaw parametry długości
            fLength = (float) Math.sqrt(vecLine[0] * vecLine[0] + vecLine[1] * vecLine[1]);

            th = arrowWidth / (2.0f * fLength);
            ta = arrowWidth / (2.0f * ((float) Math.tan(theta) / 2.0f) * fLength);

            // Znajdź podstawę strzałki
            baseX = ((float) xPoints[0] - ta * vecLine[0]);
            baseY = ((float) yPoints[0] - ta * vecLine[1]);

            // Zbuduj punkty po bokach strzałki
            xPoints[1] = (int) (baseX + th * vecLeft[0]);
            yPoints[1] = (int) (baseY + th * vecLeft[1]);
            xPoints[2] = (int) (baseX - th * vecLeft[0]);
            yPoints[2] = (int) (baseY - th * vecLeft[1]);

            g.drawLine(x1(), y1(), (int) baseX, (int) baseY);
            g.fillPolygon(xPoints, yPoints, 3);
        }
    }

}
