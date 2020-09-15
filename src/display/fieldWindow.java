package display;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// don't like doing ui
// can't be bothered to made the code cleaner
public class fieldWindow {
    public double robotx = 0;//cm
    public double roboty = 0;//cm
    public double robotr = 1;

    public double leftTop = 0;
    public double leftBottom = 0;
    public double righttTop = 0;
    public double rightBottom = 0;

    public static void main(String[] args) {
        try {
            new fieldWindow().show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void show() throws IOException {
        BufferedImage field = ImageIO.read(new File(
                "D:/IdeaProjects/GameChangersCodeUnits/src/display/download.jpg"));

        JFrame frame = new JFrame();
        frame.setSize(1000, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mothership = new JPanel();

        JPanel panel = new JPanel(){
            public void paintComponent(Graphics g){
                super.paintComponent(g);
                int width = Math.min(getWidth(), getHeight());
                g.drawImage(field, 0, 0, width, width, new Color(0,0,0), null);

                //robot
                Graphics2D g2d = (Graphics2D) g;

                int x = toPixels(toFeet(robotx), width);// + width/2;
                int y = width - toPixels(toFeet(roboty), width);// - width/2;
                g2d.rotate(-robotr, x, y); //odometry uses cc positive rotations, but not affineT

                int botWidth = toPixels(1.5, width);
                g2d.setColor(new Color(40, 255, 40));
                g2d.fillRect(x - botWidth/2, y - botWidth/2, botWidth, botWidth);

                repaint();
            }
        };
        panel.setMinimumSize(new Dimension(400, 400));

        mothership.setLayout(new SpringLayout());
        mothership.add(panel);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JSlider slide = new JSlider(JSlider.HORIZONTAL, -50, 50, 0);
        slide.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            leftTop = source.getValue();
        });
        left.add(slide);

        JSlider slide2 = new JSlider(JSlider.VERTICAL, -50, 50, 0);
        slide2.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            leftBottom = source.getValue();
        });
        left.add(slide2);

        JSlider slide3 = new JSlider(JSlider.HORIZONTAL, -50, 50, 0);
        slide3.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            righttTop = source.getValue();
        });
        right.add(slide3);

        JSlider slide4 = new JSlider(JSlider.VERTICAL, -50, 50, 0);
        slide4.addChangeListener(e -> {
            JSlider source = (JSlider) e.getSource();
            rightBottom = source.getValue();
        });
        right.add(slide4);

        mothership.add(left);
        mothership.add(right);

        SpringUtilities.makeGrid(mothership,
                1, 3, //rows, cols
                5, 5, //initialX, initialY
                5, 5);//xPad, yPad

        frame.add(mothership);
        left.setPreferredSize(new Dimension(100, 500));
        right.setPreferredSize(new Dimension(100, 500));
        frame.repaint();

        frame.setVisible(true);
    }

    int toPixels(double feet, double size){
        return (int) (size/12 * feet);
    }

    double toFeet(double cm){
        return cm * 0.0328084;
    }
}
