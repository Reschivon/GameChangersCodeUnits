package cv;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ringPipeline {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //cv.Controls c = new cv.Controls();

        Mat m = Imgcodecs.imread("D:/Onedrive/Desktop/field.jpg");
        HighGui.imshow("jds", m);

        //to hsv
        Mat hsv = new Mat();
        Imgproc.cvtColor(m, hsv, Imgproc.COLOR_BGR2HSV);

        //HighGui.imshow("HSV", hsv);

        Mat inRange = new Mat();

        //yellow range
        Core.inRange(hsv, new Scalar(6, 62, 89), new Scalar(15, 255, 255), inRange);
        Imgproc.cvtColor(inRange, inRange, Imgproc.COLOR_GRAY2BGR);
        //HighGui.imshow("inRange", inRange);

        Mat region = inRange.clone();
        Core.bitwise_and(inRange, hsv, region);

        //edges
        Mat edge = new Mat();
        sobel(region, edge);
        //dilate val
        Mat dilateElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2));
        Imgproc.dilate(edge, edge, dilateElem);

        Mat val = getChannel(edge, 2);

        //HighGui.imshow("val", val);

        //dilate val
        Mat Elem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 2));
        Imgproc.dilate(val, val, Elem);
        expand(val, val);

        //split region of interest
        Mat divided = new Mat();
        Core.subtract(region, val, divided);

        Imgproc.threshold(divided, divided, 50, 255, Imgproc.THRESH_BINARY);

        HighGui.imshow("divided", divided);

        Imgproc.cvtColor(divided, divided, Imgproc.COLOR_BGR2GRAY);

        //distance transform
        //Mat distance = new Mat();
        //distance(divided, 1, distance);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(divided, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        Mat showy = new Mat(m.rows(), m.cols(),CvType.CV_8UC3);
        drawContours(contours, showy);
        HighGui.imshow("showy", showy);

        HighGui.waitKey(0);
        System.exit(0);
    }

    public static void drawContours(List<MatOfPoint> inp, Mat showy){
        System.out.printf("there are %d contours%n",inp.size());

        for(int i = 0;i<inp.size();i++) {
            double rand = Math.random() * 255;
            double rand2 = Math.random() * 255;
            double rand3 = Math.random() * 255;
            Imgproc.drawContours(showy, inp, i, new Scalar(rand, rand2, rand3>128?128:rand3), -1);
        }
    }

    public static void distance (Mat inp, double thresh, Mat out){
        //distance transform

        Mat distance = new Mat(inp.rows(), inp.cols(), CvType.CV_8UC1);
        Imgproc.distanceTransform(inp, distance, Imgproc.DIST_C, 3);
        // [70, 255]
        Imgproc.threshold(distance, distance, thresh, 255, Imgproc.THRESH_BINARY);
        distance.convertTo(out, CvType.CV_8UC1);
    }

    public static void sum(Mat inp, Mat out){
        List<Mat> channels = new ArrayList<>();
        Core.split(inp, channels);
        Mat sum = new Mat();

        Core.add(channels.get(0), channels.get(1), sum);
        Core.add(channels.get(2), sum, sum);

        Core.multiply(sum, new Scalar(1.0/3), out);
    }
    public static Mat getChannel(Mat inp, int channel){
        List<Mat> channels = new ArrayList<>();
        Core.split(inp, channels);
        return channels.get(channel);
    }
    //1chan to 3chan
    public static void expand(Mat inp, Mat dest){
        Core.merge(Arrays.asList(inp, inp, inp), dest);
    }

    public static void sobel(Mat inp, Mat edge){
        Mat edgeY = new Mat();
        Imgproc.Sobel(inp, edgeY, -1, 0, 1, 3, 1);

        Mat edgeX = new Mat();
        Imgproc.Sobel(inp, edgeX, -1, 0, 1, 3, 1);

        Core.addWeighted(edgeX, 0.5, edgeY, 0.5, 0, edge);
    }
}
