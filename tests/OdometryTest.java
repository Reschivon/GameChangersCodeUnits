import display.fieldWindow;
import odometry.Odometry;
import odometry.OdometryWheel;
import odometry.SimulatedOdometryWheel;
import org.junit.jupiter.api.Test;
import utility.Timing;
import utility.pose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class myOdometry extends Odometry{
    public myOdometry(List<OdometryWheel> wheels) {
        super(wheels);
    }

    pose mycurvedTrajectoryTranslation (pose in){
        return super.curvedTrajectoryTranslation(in);
    }
}

class OdometryTest {
    // constants
    final static double facingForward = Math.PI/2;
    final static double facingRight = 0;

    @Test
    void fullSim() {
        // real robot setup
        List<OdometryWheel> wheels = Arrays.asList(
                new SimulatedOdometryWheel(new pose(-22.8, 0.0,facingRight))
                ,new SimulatedOdometryWheel(new pose(-22.8, 0, facingForward))
                ,new SimulatedOdometryWheel(new pose(22.8, 0.0,facingRight))
                ,new SimulatedOdometryWheel(new pose(22.8, 0,  facingForward)));

        Odometry odo = new Odometry(new pose(182, 182, facingForward), wheels);

        fieldWindow f = new fieldWindow();

        try {f.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        odo.start();
        while (true) {
            pose position = //*
            odo.getPosition();/*/odo.curvedTrajectoryTranslation(odo.getDeltaPose());//*/
            System.out.println("delta " + position);
            f.robotr = position.r;
            f.robotx = position.x;
            f.roboty = position.y;
            Timing.delay(50);

            //System.out.printf("sliders %f %f %f %f%n", f.leftTop, f.leftBottom, f.righttTop, f.rightBottom);
            ((SimulatedOdometryWheel)wheels.get(0)).setSpeed(f.leftTop/200.0);
            ((SimulatedOdometryWheel)wheels.get(1)).setSpeed(f.leftBottom/200.0);
            ((SimulatedOdometryWheel)wheels.get(2)).setSpeed(f.righttTop/200.0);
            ((SimulatedOdometryWheel)wheels.get(3)).setSpeed(f.rightBottom/200.0);
        }
    }

    @Test
    void getDeltaPose() {
        // real robot setup
        Odometry odo = new Odometry(Arrays.asList(
                new SimulatedOdometryWheel(new pose(-3, 0.1,facingRight), 0)
                ,new SimulatedOdometryWheel(new pose(-3, 0, facingForward), Math.PI * 12)
                ,new SimulatedOdometryWheel(new pose(3, 0.1,facingRight), 0)
                ,new SimulatedOdometryWheel(new pose(3, 0,  facingForward), 0)
        ));
        System.out.println(odo.getDeltaPose());
    }

    @Test
    void curvedTrajectoryTranslation() {
        myOdometry odo = new myOdometry(new ArrayList<>());
        System.out.println(odo.mycurvedTrajectoryTranslation(new pose(1, 1, Math.PI/4)));
    }
}