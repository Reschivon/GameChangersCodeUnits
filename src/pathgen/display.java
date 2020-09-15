package pathgen;

import utility.point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class display {
    boolean exited = false;
    boolean subdivideReq = false;
    boolean addControlReq = false;
    boolean tightTurnsOnlyC = false;
    // don't like doing ui
    // can't be bothered to made the code cleaner
    public void show(Path points){
        JPanel panel = new JPanel(){
            public void paintComponent(Graphics g){
                super.paintComponent(g);

                for(PathPoint p : points){
                    // point body
                    g.setColor(p.isControl?Color.red:Color.black);
                    g.fillOval((int)(p.x - 5), (int)(getHeight() - (p.y - 5)) - 10, 10, 10);

                    // point direction
                    ((Graphics2D)g).setStroke(new BasicStroke(2.3f));
                    point r = new point(p.speed * 15 + 5, 0).rotate(p.dir);
                    r.translate(p.x, p.y);
                    g.drawLine((int)p.x, getHeight() - (int)p.y, (int)r.x, getHeight() - (int)r.y);

                    // point text
                    g.setColor(Color.BLACK);
                    //g.drawString(String.format("%.2f", p.speed), (int)p.x + 5, getHeight() - (int)p.y);
                }

                g.setColor(new Color(128, 255, 200));
                g.fillOval((int)(points.start.x - 5), (int)(getHeight() - (points.start.y - 5)) - 10, 10, 10);
                g.fillOval((int)(points.end.x - 5), (int)(getHeight() - (points.end.y - 5)) - 10, 10, 10);

                repaint();
            }
        };

        MouseAdapter listen = new MouseAdapter() {
            PathPoint selected = null;

            @Override
            public void mousePressed(MouseEvent e) {
                //look for which one
                for(PathPoint p:points){
                    if (20 > Math.hypot(e.getX() - p.x, e.getY() - (panel.getHeight() - p.y))){
                        if(p.isControl){
                            selected = p;
                            break;
                        }
                    }
                }
                if(selected != null) return;
                for(PathPoint p:points){
                    if (20 > Math.hypot(e.getX() - p.x, e.getY() - (panel.getHeight() - p.y))){
                        selected = p;
                        break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                selected = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if(selected == null) return;

                selected.x = e.getX();
                selected.y = panel.getHeight() - e.getY();
            }
        };
        panel.setBackground(new Color(150, 150, 150));
        panel.addMouseListener(listen);
        panel.addMouseMotionListener(listen);

        JPanel controlPanel = new JPanel();

        JButton exit = new JButton("Exit");
        exit.addActionListener(e -> exited = true);
        controlPanel.add(exit);

        JCheckBox tightTurnsOnly = new JCheckBox("tightTurnsOnly");
        tightTurnsOnly.addActionListener(e -> tightTurnsOnlyC = tightTurnsOnly.isSelected());
        controlPanel.add(tightTurnsOnly);

        JButton subdivide = new JButton("Subdivide");
        subdivide.addActionListener(e -> subdivideReq = true);
        controlPanel.add(subdivide);

        JButton addControl = new JButton("Add Control Point");
        addControl.addActionListener(e -> addControlReq = true);
        controlPanel.add(addControl);

        JFrame frame = new JFrame("Path Optimization. Export to " + new File("").getAbsolutePath());

        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        panel.repaint();
        frame.setSize(500, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
