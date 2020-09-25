package cv;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RingPipeline {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    static Mat hsv = new Mat();
    static Mat inRange = new Mat();
    static Mat region = new Mat();
    static Mat val = new Mat();
    static Mat edge = new Mat();
    static Mat divided = new Mat();
    static Mat showy = new Mat();

    static Mat elem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(6, 1), new Point(5, 0));
    static Mat none = new Mat();

    public static void main(String[] args) {
        //cv.Controls c = new cv.Controls();

        // 1 - 6
        for(int i=1;i<6;i++) {
            String url = "D:/Onedrive/Desktop/cv/" + (int)(i + 1) + ".jpg";
            System.out.println(url);
            Mat in = Imgcodecs.imread(url);
            process(in);
            HighGui.waitKey();
            HighGui.waitKey();
            in.release();
        }

        //System.exit(0);

    }
    public static List<RotatedRect> process(Mat in){

        Imgproc.resize(in, in, new Size(640, 480), 0, 0, Imgproc.INTER_LINEAR);
        HighGui.imshow("jds", in);

        //happens once per image size
        if(showy.cols() != in.cols() || showy.rows() != in.rows())
            showy = new Mat(in.rows(), in.cols(),CvType.CV_8UC3);

        System.out.println(showy.size());

        //to hsv
        Imgproc.cvtColor(in, hsv, Imgproc.COLOR_BGR2HSV);

            //HighGui.imshow("HSV", hsv);

        //yellow range
        Core.inRange(hsv, new Scalar(6, 62, 89), new Scalar(15, 255, 255), inRange);
        Imgproc.cvtColor(inRange, inRange, Imgproc.COLOR_GRAY2BGR);

            //HighGui.imshow("inRange", inRange);

        Core.bitwise_and(inRange, hsv, region);

        //edges
        sobel(region, edge);

        val = getChannel(edge, 2);

            //.imshow("val", val.clone());

        //dilate val
        Imgproc.dilate(val, val, elem);
        expand(val, val);

            //HighGui.imshow("val", val.clone());

        //split region of interest
        Core.subtract(region, val, divided);

        Imgproc.cvtColor(divided, divided, Imgproc.COLOR_BGR2GRAY);

        //take advantage of the gradient in the real image
        Imgproc.threshold(divided, divided, 50, 255, Imgproc.THRESH_BINARY);

        //find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(divided, contours, none, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        filterContours(contours, 40);

        Imgproc.rectangle(showy, new Point(0,0), new Point(showy.cols(), showy.rows()), new Scalar(0,0,0), -1);

        drawContours(contours, showy);

            HighGui.imshow("showy", showy);



        List<RotatedRect> bb = new ArrayList<>();
        for(MatOfPoint c:contours){
            //to matofpoint2f
            bb.add(Imgproc.minAreaRect(new MatOfPoint2f(c.toArray())));
        }

        return bb;
    }

    public static void filterContours(List<MatOfPoint> inp, double size){
        for(int i=0;i<inp.size();i++){
            if(Imgproc.contourArea(inp.get(i)) < size){
                inp.remove(i);
                i--;
            }
        }
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

    //Not memory safe
    public static void distance (Mat inp, double thresh, Mat out){
        //distance transform

        out = new Mat(inp.rows(), inp.cols(), CvType.CV_8UC1);
        Imgproc.distanceTransform(inp, out, Imgproc.DIST_C, 3);
        // [70, 255]
        Imgproc.threshold(out, out, thresh, 255, Imgproc.THRESH_BINARY);
        out.convertTo(out, CvType.CV_8UC1);
    }

    public static void sum(Mat inp, Mat out){
        List<Mat> channels = new ArrayList<>();
        Core.split(inp, channels);
        Mat sum = new Mat();

        Core.add(channels.get(0), channels.get(1), sum);
        Core.add(channels.get(2), sum, sum);

        Core.multiply(sum, new Scalar(1.0/3), out);

        sum.release();
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

        edgeY.release();
        edgeX.release();
    }
}
