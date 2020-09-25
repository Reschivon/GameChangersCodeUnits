package movement;

import theLib.DcMotor;
import theLib.DcMotorEx;
import utility.point;

import java.util.Arrays;

public class Mecanum {
    DcMotorEx frontLeft;
    DcMotorEx frontRight;
    DcMotorEx backLeft;
    DcMotorEx backRight;

    int frontLeftI = 0;
    int frontRightI = 1;
    int backLeftI = 2;
    int backRightI = 3;

    private static final double maxTicksPerSec = 1024;

    //top l, top r, bottom l, bottom r
    public Mecanum(DcMotorEx... motors){
        frontLeft   = motors[0];
        frontRight  = motors[1];
        backLeft    = motors[2];
        backRight   = motors[3];

        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);
    }

    public void drive(double xSpeed, double ySpeed, double turnSpeed){

        point move = new point(xSpeed, ySpeed);
        move.normalize();

        double[] wheelSpeeds = new double[4];
        wheelSpeeds[frontLeftI]     = move.y + move.x - turnSpeed;
        wheelSpeeds[frontRightI]    = move.y - move.x + turnSpeed;
        wheelSpeeds[backLeftI]      = move.y - move.x - turnSpeed;
        wheelSpeeds[backRightI]     = move.y + move.x + turnSpeed;

        //normalize
        double max = Arrays.stream(wheelSpeeds).max().getAsDouble();
        for(int i = 0;i<wheelSpeeds.length;i++){
            wheelSpeeds[i] /= max;
        }

        frontLeft.setVelocity(  (int)(maxTicksPerSec * wheelSpeeds[frontLeftI]));
        frontRight.setVelocity( (int)(maxTicksPerSec * wheelSpeeds[frontRightI]));
        backLeft.setVelocity(   (int)(maxTicksPerSec * wheelSpeeds[backLeftI]));
        backRight.setVelocity(  (int)(maxTicksPerSec * wheelSpeeds[backRightI]));
    }
}
