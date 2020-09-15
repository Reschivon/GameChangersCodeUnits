package cv;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class Controls {
    int q,w,e;
    int x,y;

    public Controls(){
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());

        frame.add(new slider(){
            public void update(int val){
                q = val;
            }
        });
        frame.add(new slider(){
            public void update(int val){
                w = val;
            }
        });

        frame.add(new slider(){
            public void update(int val){
                e = val;
            }
        });

        frame.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                x=e.getX();
                y=e.getY();
            }
        });

        frame.pack();
        frame.setLocation(10, 700);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}

class slider extends JSlider implements ChangeListener {
    public slider(){
        super(JSlider.VERTICAL, 0, 255, 0);
        addChangeListener(this);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        update(((JSlider)e.getSource()).getValue());
    }

    public void update(int val){

    }
}
