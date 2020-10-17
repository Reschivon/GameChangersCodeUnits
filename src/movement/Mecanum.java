package movement;

import theLib.*;
import utility.*;

public class Mecanum {
    DcMotorEx frontLeft;
    DcMotorEx frontRight;
    DcMotorEx backLeft;
    DcMotorEx backRight;

    private static final double maxTicksPerSec = 1024;
    private static final double wheelToCenter = 13;

    public Mecanum(DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx backLeft, DcMotorEx backRight){
        this.frontLeft   = frontLeft;
        this.frontRight  = frontRight;
        this.backLeft    = backLeft;
        this.backRight   = backRight;

        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);
    }

    //turn speed be in robot angle
    public void drive(double xSpeed, double ySpeed, double turnSpeed){
        // radians = circumference / radius
        turnSpeed /= wheelToCenter;

        point move = new point(xSpeed, ySpeed);
        move.normalize();

        double[] normalizedSpeeds = maximum.squishIntoRange(1.0,
                move.y + move.x - turnSpeed,  //FL
                move.y - move.x + turnSpeed,        //FR
                move.y - move.x - turnSpeed,        //BL
                move.y + move.x + turnSpeed);       //BR

        frontLeft.setVelocity(  (int)(maxTicksPerSec * normalizedSpeeds[0]));
        frontRight.setVelocity( (int)(maxTicksPerSec * normalizedSpeeds[1]));
        backLeft.setVelocity(   (int)(maxTicksPerSec * normalizedSpeeds[2]));
        backRight.setVelocity(  (int)(maxTicksPerSec * normalizedSpeeds[3]));
    }
}
