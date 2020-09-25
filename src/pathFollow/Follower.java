package pathFollow;

import movement.Mecanum;
import odometry.Odometry;
import pathgen.ImportPath;
import pathgen.Path;
import utility.RotationUtil;
import utility.point;
import utility.pose;

public class Follower {
    double closeEnoughDistance = 10;

    /**
     * Follows the pathFile found in local directory
     * @param drivetrain a mecanum drivetrain
     * @param odometry an activated, running odometry to be used for positioning
     */
    public Follower(Mecanum drivetrain, Odometry odometry){
        this(drivetrain, odometry, "paths.txt");
    }
    /**
     * Follows a path specified by the fileName
     * @param drivetrain a mecanum drivetrain
     * @param odometry an activated, running odometry to be used for positioning
     */
    public Follower(Mecanum drivetrain, Odometry odometry, String pathFile){
        Path path = ImportPath.getPath(pathFile);

        //index of current target point
        int i = 0;
        while(i < path.size()){
            pose position = odometry.getPosition();
            //if close enough advance
            for(int j = i; j < path.size(); j++) {
                if (path.get(j).distTo(new point(position.x, position.y)) < closeEnoughDistance) {
                    i = j;
                    break;
                }
            }

            //move robot towards i
            double rotDiff = RotationUtil.turnLeftOrRight(position.r, path.get(i).dir, Math.PI * 2);
            point transDiff = new point(path.get(i).x - position.x, path.get(i).y - position.y);
            point transDiffIntrinsic = transDiff.rotate(-position.r);
            drivetrain.drive(transDiffIntrinsic.x, transDiffIntrinsic.y, rotDiff);
        }
    }

}
