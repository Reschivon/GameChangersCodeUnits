package movement;

import theLib.DcMotor;
import theLib.DcMotorEx;
import utility.point;

public class mecanum {
    DcMotorEx frontLeft;
    DcMotorEx frontRight;
    DcMotorEx backLeft;
    DcMotorEx backRight;

    private static final double maxTicksPerSec = 1024;

    //top l, top r, bottom l, bottom r
    public mecanum(DcMotorEx... motors){
        frontLeft = motors[0];
        frontRight = motors[1];
        backLeft = motors[2];
        backRight = motors[3];

        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);
    }

    public void drive(double xSpeed, double ySpeed, double turnSpeed){

        point move = new point(xSpeed, ySpeed);
        double angle = move.angle();
        double magnitude = move.magnitude();

        double[] wheelSpeeds = new double[4];
        wheelSpeeds[0] = magnitude * Math.sin(angle + Math.PI / 4) + turnSpeed;
        wheelSpeeds[1] = magnitude * Math.sin(angle - Math.PI / 4) - turnSpeed;
        wheelSpeeds[2] = magnitude * Math.sin(angle - Math.PI / 4) + turnSpeed;
        wheelSpeeds[3] = magnitude * Math.sin(angle + Math.PI / 4) - turnSpeed;

        //normalize
        double max = 0;
        for(double d:wheelSpeeds){
            if(Math.abs(max) < d) max = d;
        }for(int i = 0;i<wheelSpeeds.length;i++){
            wheelSpeeds[i] /= max;
        }

        frontLeft.setVelocity((int)(maxTicksPerSec * wheelSpeeds[0]));
        frontRight.setVelocity((int)(maxTicksPerSec * wheelSpeeds[1]));
        backLeft.setVelocity((int)(maxTicksPerSec * wheelSpeeds[2]));
        backRight.setVelocity((int)(maxTicksPerSec * wheelSpeeds[3]));
    }
}
