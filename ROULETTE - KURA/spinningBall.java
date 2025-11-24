//Thank you to Lewis, Youtube https://www.youtube.com/watch?v=4PfDdJ8GFHI, and Google AI for teaching me how to multithread and apply trig to my code

//imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

//making a runnable public class with JPanel for the frame
public class spinningBall extends JPanel implements Runnable {
    //instance variables - volatility allows change to occur when called+multithreaded vs constant that doesnt change in final
    private volatile double angle = 0;
    //radii for the gold circle and segmented circle
    private final int outerRadius = 140;
    private final int innerRadius = 30;
    //making the ballsize
    private final int ballSize = 10;
    //tell if spinning
    private volatile boolean isSpinning = false;
    private int currentDisplayNumber = 0; // Displayed in the center
    private final int totalNumbers = 16;

    //main colors
    private final Color RED_COLOR = new Color(200, 0, 0);
    private final Color BLACK_COLOR = new Color(50, 48, 48);
    private final Color BROWN_COLOR = new Color(186, 123, 8, 255);
    private final Object lock = new Object();
    private double stopThreshold = 0;

    //making a new random device
    private Random rand = new Random();

    //spin on canvas
    public void startSpinning() {
        if (!isSpinning) {
            //randomize the start angle every time a spin begins
            this.angle = rand.nextDouble() * 360.0;

            //randomize the stop point thru duration of spin
            this.stopThreshold = 0.5 + (2.0 - 0.5) * rand.nextDouble();
            this.currentDisplayNumber = 0;
            this.isSpinning = true;
            new Thread(this).start();
        }
    }

    //getting the final resting angle to compute for win number
    public double getFinalAngle() {
        return angle;
    }

    //monitor block for spin to allow multithreading, but waiting for the spin to finish
    public void waitForSpinToFinish() throws InterruptedException {
        synchronized (lock) {
            while (isSpinning) {
                lock.wait();
            }
        }
    }

    //when the spin is done ping
    public void notifySpinFinished() {
        synchronized (lock) {
            isSpinning = false;
            lock.notifyAll();
        }
    }

    //running the event dispatch thread to update the canvas with each number
    public void updateDisplayNumber(int number) {
        SwingUtilities.invokeLater(() -> {
            this.currentDisplayNumber = number;
            repaint();
        });
    }

    //override allows it to have higher importance and priority over other methods in multithreading - i think
    @Override
    //making the background - graphics class for drawing
    public void paintComponent(Graphics g) {
        //making the brush
        super.paintComponent(g);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        //this is needed for the spin to be smooth
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        //making the background
        g2d.setColor(new Color(0, 73, 0));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        //big brown circle
        g2d.setColor(new Color(76, 38, 7));
        g2d.fillOval(centerX-150,centerY-150, 300, 300);

        //segmentation
        double angleStep = 360.0 / totalNumbers;

        //the loop that allows all the numbers to be generated in a circle
        for (int i = 0; i < totalNumbers; i++) {
            //creating each segment - youtube vid
            Color segmentColor = (i%2==0) ? RED_COLOR : BLACK_COLOR;
            g2d.setColor(segmentColor);
            int startAngle = (int) (i * angleStep);
            g2d.fillArc(centerX - outerRadius, centerY - outerRadius, outerRadius * 2, outerRadius * 2,
                    startAngle, (int) angleStep + 1);

            //creating each number
            g.setColor(Color.WHITE);
            double textAngle = Math.toRadians(startAngle + angleStep / 2.0);
            int textX = (int) (centerX + (outerRadius * 0.75) * Math.cos(textAngle) - g.getFontMetrics().stringWidth(String.valueOf(i+1))/2);
            int textY = (int) (centerY - (outerRadius * 0.75) * Math.sin(textAngle) + g.getFontMetrics().getHeight()/4);
            g.drawString(String.valueOf(i + 1), textX, textY);
        }

        //inner big gold dot
        g.setColor(BROWN_COLOR);
        g2d.fillOval(centerX - innerRadius, centerY - innerRadius, innerRadius * 2, innerRadius * 2);

        //making the ball
        g.setColor(Color.LIGHT_GRAY);
        int ballTrackRadius = outerRadius -15;
        double rads = Math.toRadians(angle);
        int ballX = (int) (centerX + ballTrackRadius * Math.cos(rads) - ballSize / 2);
        int ballY = (int) (centerY - ballTrackRadius * Math.sin(rads) - ballSize / 2);
        g2d.fillOval(ballX, ballY, ballSize, ballSize);

        //puts the wining number in the middle of the board
        if ((!isSpinning) && (currentDisplayNumber != 0)) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            String numStr = String.valueOf(currentDisplayNumber);
            int textWidth = g.getFontMetrics().stringWidth(numStr);
            int textHeight = g.getFontMetrics().getHeight();
            g.drawString(numStr, centerX - textWidth / 2, centerY + textHeight / 4);
        }
    }

    //runs the spin
    @Override
    public void run() {
        //physics of the ball
        double currentSpeed = 30.0;
        double decelerationRate = 0.98;

        //what happens when the ball is still rolling
        while (isSpinning) {
            // angle increments (counter-clockwise movement)
            angle = (angle + currentSpeed) % 360;
            currentSpeed *= decelerationRate;

            //condition: if speed drops to the randomly determined threshold then the ball stops rolling
            if (currentSpeed < stopThreshold) {
                notifySpinFinished();
            }

            repaint();

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //final sleep to ensure the last frame is rendered
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        repaint();
    }
}
