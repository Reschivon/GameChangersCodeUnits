package cv;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class RingPipeline {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    static Mat inRange = new Mat();
    static Mat edge = new Mat();
    static Mat binarizedEdges = new Mat();
    static Mat divided = new Mat();
    static Mat showy = new Mat();
    static Mat yuv = new Mat();

    static Mat elem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8, 1), new Point(4, 0));
    static Mat none = new Mat();

    //testing dirty fix
    static String currName = "";

    public static void main(String[] args) {
        //cv.Controls c = new cv.Controls();

        var files = new String[]{"1.jpg", "2.jpg", "3.jpg", "4.jpg", "5.jpg", "6.jpg"//5
        , "e1.png", "e2.png", "e3.png", "e4.png", "n1.jpg", "n2.jpg", "n3.jpg",//12
        "q1.jpg","q2.jpg","q3.jpg","q4.jpg","q5.jpg", "q6.jpg","q7.jpg","q8.jpg",//20
        "q9.jpg","q10.jpg", "q11.jpg","q12.jpg","q13.jpg","q14.jpg","q15.jpg",//28
        "q16.jpg"};


        // 25
        for(int i = 0; i < 29; i++) {
            String name = files[i];
            String url = "D:/OneDrive/Desktop/cv/" + name ;
            System.out.println(url);
            Mat in = Imgcodecs.imread(url);

            long start = System.currentTimeMillis();
            for(var r:process(in)){
                //drawRotatedRect(r, in, 4);
            }

            Imgproc.putText(in, String.valueOf(System.currentTimeMillis() - start), new Point(20, 20),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.6, new Scalar(255, 0,255), 2);

            HighGui.waitKey();
            in.release();
        }
        System.exit(0);

        /*
        VideoCapture capture = new VideoCapture();
        capture.open(0);
        while (capture.isOpened()){
            Mat in = new Mat();
            capture.read(in);

            process(in);

            HighGui.waitKey(1);
            in.release();
        }
        capture.release();*/

    }
    public static List<RotatedRect> process(Mat in){

        Imgproc.resize(in, in, new Size(640, 480), 0, 0, Imgproc.INTER_LINEAR);
        /*int sizex = 640 * 3;
        int sizey = 480 * 3;

        sizex = Math.min(sizex, in.cols());
        sizey = Math.min(sizey, in.rows());

        in = new Mat(in, new Rect(in.cols()/2 - sizex/2, in.rows()/2 - sizey/2, sizex, sizey));
        Imgproc.resize(in, in, new Size(640, 480), 0, 0, Imgproc.INTER_LINEAR);*/



        //happens once per image size
        if(showy.cols() != in.cols() || showy.rows() != in.rows())
            showy = new Mat(in.rows(), in.cols(),CvType.CV_8UC3);

        CLAHE c = Imgproc.createCLAHE(15, new Size(2, 2));
        var channelsff = new ArrayList<Mat>();
        Core.split(in, channelsff);
        c.apply(channelsff.get(0), channelsff.get(0));
        c.apply(channelsff.get(1), channelsff.get(1));
        c.apply(channelsff.get(2), channelsff.get(2));
        Core.merge(channelsff, in);


        Imgproc.cvtColor(in, yuv, Imgproc.COLOR_BGR2Lab);

        //get Y
        channelsff = new ArrayList<Mat>();
        Core.split(yuv, channelsff);
        Mat Y = channelsff.get(0);

        //find medians
        Scalar medians = median(yuv)    ;
        System.out.println("medians " + medians);

            //HighGui.imshow("YYYY", Y);

        //get differences to median
        var diffs = new Mat();
        Core.absdiff(yuv, medians, diffs);
        var channels = new ArrayList<Mat>();
        Core.split(diffs, channels);

        //HighGui.imshow("lightness", channels.get(0));
        //HighGui.imshow("blue", channels.get(1));
        //HighGui.imshow("red", channels.get(2));

        Mat value = channels.get(0).clone();

        //color channel
        Mat color = new Mat();
        Core.add(channels.get(1), channels.get(2), color);

            //HighGui.imshow("color", color);

        //yellow range
        Core.inRange(color, new Scalar(15), new Scalar(255), inRange);
        Core.bitwise_and(Y, inRange, value);

            //HighGui.imshow("value", value);

        //edges
        sobel(value, edge);

            //HighGui.imshow("thin edges", edge.clone());

        Imgproc.erode(edge, edge,
                Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2)));
        Imgproc.dilate(edge, edge,
                Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,1), new Point(2, 0)));

            //HighGui.imshow("Binarized edge", edge.clone());

        //split region of interest by edges
        Core.multiply(edge, new Scalar(1.5), edge);

            //HighGui.imshow("Binarized edge2", edge.clone());

        Core.subtract(value, edge, divided);

        //shave pesky edges
        Imgproc.erode(divided, divided,
                Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5), new Point(2,2)));

            //HighGui.imshow("divided ", divided.clone());

        Core.inRange(divided, new Scalar(20), new Scalar(255), divided);

            HighGui.imshow("divided threshed", divided);

        //find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(divided, contours, none, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        //filter contours
        List<RotatedRect> bb = filterContours(contours, 30);

        //draw contours
        Imgproc.rectangle(showy, new Point(0,0), new Point(showy.cols(), showy.rows()), new Scalar(0,0,0), -1);
        drawContours(contours, in);

        for(var r:bb){
            drawRotatedRect(r, in, 2);
        }

            HighGui.imshow("showy", in);
            //Imgcodecs.imwrite("Output " + currName, showy);

        return bb;
    }

    //sort assuming it's of CV_8U type
    public static Scalar median (Mat in){
        double[] medians = new double[3];

        //resize
        Mat clone = new Mat();
        Imgproc.resize(in.clone(), clone, new Size(32, 24));

        //get data
        clone.convertTo(clone, CvType.CV_16UC3); // New line added.
        List<Mat> channels = new ArrayList<>();
        Core.split(clone, channels);

        int i = 0;
        for(Mat m : channels) {
            short[] buff = new short[(int) (m.total() * m.channels())];
            m.get(0, 0, buff);
            medians[i] = QuickSelect.median(buff);
            ++i;
        }

        return new Scalar(medians);
    }

    public static List<RotatedRect> filterContours(List<MatOfPoint> inp, double size){
        var boundingRects = new ArrayList<RotatedRect>();
        var contours = new ArrayList<MatOfPoint>();

        for (MatOfPoint contour : inp) {
            if (Imgproc.contourArea(contour) < size) {
                continue;
            }

            //bounding box
            RotatedRect bound = Imgproc.minAreaRect(new MatOfPoint2f(contour.toArray()));

            int toUpright = (int) (bound.angle - -45);
            int turnsToRight = toUpright / 90 + toUpright < 0 ? 1 : 0;
            boolean isUpright = turnsToRight % 2 == 0;

            if (isUpright) {
                if (bound.size.width > bound.size.height * 1) {
                    boundingRects.add(bound);
                    contours.add(contour);
                }
            } else {
                if (bound.size.height > bound.size.width * 1) {
                    boundingRects.add(bound);
                    contours.add(contour);
                }
            }

            //System.out.format("rect %.4f %.4f %.4f %s %n", bound.size.width, bound.size.height, bound.angle, isUpright?"upright":"fell down");

        }
        inp.clear();
        inp.addAll(contours);
        return boundingRects;
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
    public static void drawRotatedRect(RotatedRect in, Mat canvas){
        drawRotatedRect(in, canvas, 1);
    }
    public static void drawRotatedRect(RotatedRect in, Mat canvas, int thick){
        Point[] points = new Point[4];
        in.points(points);
        for(int i=0; i<4; ++i){
            Imgproc.line(canvas, points[i], points[(i+1)%4], new Scalar(255,0,255), thick);
        }
    }

    //Not memory safe
    public static void distance(Mat inp, double thresh){
        //distance transform

        Mat out = new Mat(inp.rows(), inp.cols(), CvType.CV_8UC1);
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

    public static void sobel(Mat inp, Mat edge){
        Mat edgeY = new Mat();
        Imgproc.Sobel(inp, edgeY, -1, 0, 1, Imgproc.FILTER_SCHARR, 1);

        Mat edgeX = new Mat();
        Imgproc.Sobel(inp, edgeX, -1, 0, 1, Imgproc.FILTER_SCHARR, 1);

        Core.addWeighted(edgeX, 0.3, edgeY, 0.3, 0, edge);

        edgeY.release();
        edgeX.release();
    }

    public static void hough(Mat gray){
        // Changing the color of the canny
        Mat canvas = new Mat();
        Imgproc.cvtColor(gray, canvas, Imgproc.COLOR_GRAY2BGR);

        //Detecting the hough lines from (canny)
        Mat lines = new Mat();
        Imgproc.HoughLines(gray, lines, 1, Math.PI/180, 70);

        //draw lines
        for (int i = 0; i < lines.rows(); i++) {
            double[] data = lines.get(i, 0);
            double rho = data[0];
            double theta = data[1];
            double a = Math.cos(theta);
            double b = Math.sin(theta);
            double x0 = a*rho;
            double y0 = b*rho;
            //Drawing lines on the image
            Point pt1 = new Point();
            Point pt2 = new Point();
            pt1.x = Math.round(x0 + 1000*(-b));
            pt1.y = Math.round(y0 + 1000*(a));
            pt2.x = Math.round(x0 - 1000*(-b));
            pt2.y = Math.round(y0 - 1000 *(a));
            Imgproc.line(canvas, pt1, pt2, new Scalar(0, 0, 255), 1);
        }

        HighGui.imshow("res", canvas);
    }


    /**
     * Compute and show the histogram for the given {@link Mat} image
     *
     * @param frame
     *            the {@link Mat} image for which compute the histogram
     * @param gray
     *            is a grayscale image?
     */
    static void showHistogram(Mat frame, boolean gray)
    {
        // split the frames in multiple images
        List<Mat> images = new ArrayList<>();
        Core.split(frame, images);

        // set the number of bins at 256
        MatOfInt histSize = new MatOfInt(256);
        // only one channel
        MatOfInt channels = new MatOfInt(0);
        // set the ranges
        MatOfFloat histRange = new MatOfFloat(0, 256);

        // compute the histograms for the B, G and R components
        Mat hist_b = new Mat();
        Mat hist_g = new Mat();
        Mat hist_r = new Mat();

        // B component or gray image
        Imgproc.calcHist(images.subList(0, 1), channels, new Mat(), hist_b, histSize, histRange, false);

        // G and R components (if the image is not in gray scale)
        if (!gray)
        {
            Imgproc.calcHist(images.subList(1, 2), channels, new Mat(), hist_g, histSize, histRange, false);
            Imgproc.calcHist(images.subList(2, 3), channels, new Mat(), hist_r, histSize, histRange, false);
        }

        // draw the histogram
        int hist_w = 150; // width of the histogram image
        int hist_h = 150; // height of the histogram image
        int bin_w = (int) Math.round(hist_w / histSize.get(0, 0)[0]);

        Mat histImage = new Mat(hist_h, hist_w, CvType.CV_8UC3, new Scalar(0, 0, 0));
        // normalize the result to [0, histImage.rows()]
        Core.normalize(hist_b, hist_b, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());

        // for G and R components
        if (!gray)
        {
            Core.normalize(hist_g, hist_g, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
            Core.normalize(hist_r, hist_r, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat());
        }

        // effectively draw the histogram(s)
        for (int i = 1; i < histSize.get(0, 0)[0]; i++)
        {
            // B component or gray image
            Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_b.get(i - 1, 0)[0])),
                    new Point(bin_w * (i), hist_h - Math.round(hist_b.get(i, 0)[0])), new Scalar(255, 0, 0), 2, 8, 0);
            // G and R components (if the image is not in gray scale)
            if (!gray)
            {
                Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_g.get(i - 1, 0)[0])),
                        new Point(bin_w * (i), hist_h - Math.round(hist_g.get(i, 0)[0])), new Scalar(0, 255, 0), 2, 8,
                        0);
                Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(hist_r.get(i - 1, 0)[0])),
                        new Point(bin_w * (i), hist_h - Math.round(hist_r.get(i, 0)[0])), new Scalar(0, 0, 255), 2, 8,
                        0);
            }
        }

        // display the histogram...
        HighGui.imshow("histogram", histImage);

    }

}
