import odometry.SimulatedOdometryWheel;
import org.junit.jupiter.api.Test;
import utility.pose;

class OdometryWheelTest {
    @Test
    void inverseTest(){
        SimulatedOdometryWheel w = new SimulatedOdometryWheel(new pose(1, 0, Math.PI/2), 500);
        System.out.println(
                w.robotAngleToOdoDelta(w.odoDeltaToBotAngle(1.123, 10, 0), 10, 0));
    }
}