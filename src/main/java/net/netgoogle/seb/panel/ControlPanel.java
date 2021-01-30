package net.netgoogle.seb.panel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel {

    private final BallPanel mainPanel;
    private final JButton resetButton;
    private final JButton generateButton;
    private final JButton scatterButton;
    private final JSlider gravitySlider;

	public ControlPanel(BallPanel mainPanel) {
        this.setPreferredSize(new Dimension(200, 60));
        this.setMaximumSize(new Dimension(5000, 100));
        this.mainPanel = mainPanel;

        // Utwórz instancji kontrolek
        resetButton = new JButton("Wyczyść");
        generateButton = new JButton("Generuj piłki");
        scatterButton = new JButton("Wykonaj randomowe ruchy piłek");

        gravitySlider = new JSlider(JSlider.HORIZONTAL, 0, 3000, 2000);
        gravitySlider.setBorder(BorderFactory.createTitledBorder("Grawitacja - " + gravitySlider.getValue() + "pixeli/sekundęa"));
        gravitySlider.setMajorTickSpacing(200);
        gravitySlider.setMinorTickSpacing(100);
        gravitySlider.setPaintTicks(true);

        // Dodaj kontrolery do panelu głównego
        this.add(gravitySlider);
        this.add(scatterButton);
        this.add(generateButton);
        this.add(resetButton);

        // wire up gui events
        ButtonHandler buttonHandler = new ButtonHandler();
        resetButton.addActionListener(buttonHandler);
        scatterButton.addActionListener(buttonHandler);
        generateButton.addActionListener(buttonHandler);

        SliderHandler sliderHandler = new SliderHandler();
        gravitySlider.addChangeListener(sliderHandler);

    }

    private class ButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton) e.getSource();

            if (source == resetButton) {
                mainPanel.clearBalls();
            }

            if (source == generateButton) {
                mainPanel.generateBalls(100);
            }

            if (source == scatterButton) {
                mainPanel.scatterBalls();
            }
        }
    }

    private class SliderHandler implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();

            if (source == gravitySlider) {
                source.setBorder(BorderFactory.createTitledBorder("Grawitacja - " + source.getValue() + "pixeli/sekundę"));
                mainPanel.setGravity(source.getValue());
            }
        }
    }

}
