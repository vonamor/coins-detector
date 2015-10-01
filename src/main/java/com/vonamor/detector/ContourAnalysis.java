package com.vonamor.detector;


import org.opencv.core.*;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContourAnalysis {

    public static void main(String[] args) {

        Util.loadOpenCVLibrary();

//        String srcImageFilename = "C:\\dev\\projects\\coins-detector\\src\\main\\resources\\img\\mungu.jpg";
        String srcImageFilename = "C:\\dev\\projects\\coins-detector\\src\\main\\resources\\img\\mark_revers.jpg";
//        String srcImageFilename = "C:\\dev\\projects\\coins-detector\\src\\main\\resources\\img\\kopek.jpg";
//        String srcImageFilename = "C:\\dev\\projects\\coins-detector\\src\\main\\resources\\img\\quarter_revers.jpg";

        processImage(srcImageFilename);

    }

    public static void processImage(String filename) {
        Mat src = Util.readImage(filename);

        Util.showResult(src);

        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.medianBlur(gray, gray, 3);
        /*Imgproc.erode(gray, gray, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(5, 5)));
        Imgproc.dilate(gray, gray, Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(5, 5)));*/
//        Imgproc.threshold(gray, gray, 0, 255, Imgproc.THRESH_OTSU);
//        Util.showResult(gray);

//        findContours(gray, null);

//        hsvOtsu(src);

        HSV hsv = new HSV(src);

        int size = 3;

//        cannyWithMorphology(hsv.getHue(), size);
//        invert(hsv.getSat());

        cannyWithMorphology(hsv.getSat(), size);
        cannyWithMorphology(hsv.getValue(), size);
        cannyWithMorphology(gray, size);

        /*canny(hsv.getHue());
        canny(hsv.getSat());
        canny(hsv.getValue());
        canny(gray);*/

//        findFeatureDescriptors(src, FeatureDetector.ORB, DescriptorExtractor.ORB);
//        findFeatureDescriptors(src, FeatureDetector.SIFT, DescriptorExtractor.SIFT);
//        findFeatureDescriptors(src, FeatureDetector.SURF, DescriptorExtractor.SURF);

//        Imgproc.GaussianBlur(gray, gray, new Size(9, 9), 1.0);
//        Imgproc.medianBlur(gray, gray,9);
//        Mat binary = new Mat();
//        Scalar mean = Core.mean(gray);
//        Imgproc.Canny(gray, binary, mean.val[0], 255);
//        Imgproc.threshold(gray, binary, mean.val[0], 255, Imgproc.THRESH_OTSU);
//        findContours(binary, src);

//        Imgproc.GaussianBlur(gray, gray, new Size(9, 9), 1.0);


//        analyzeHSV(src);
    }


    public static void invert(Mat src) {
        Scalar mean = Core.mean(src);
        Mat inverted = new Mat();
        Imgproc.threshold(src, inverted, mean.val[0], 255, Imgproc.THRESH_BINARY_INV);
        Util.showResult(inverted);
    }

    public static void cannyWithMorphology(Mat gray, int size) {
        Imgproc.medianBlur(gray, gray, size);
        /*Imgproc.erode(gray, gray, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(size, size)));
        Imgproc.dilate(gray, gray, Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(size, size)));*/
        canny(gray);
    }

    public static void canny(Mat gray) {
        Scalar mean = Core.mean(gray);
        Mat canny = new Mat();
        Imgproc.Canny(gray, canny, mean.val[0], 255);
        Util.showResult(canny);
    }

    // findFeatureDescriptors(src, FeatureDetector.ORB, DescriptorExtractor.ORB);
    public static Mat findFeatureDescriptors(Mat src, int featureDetector, int descriptorExtractor) {
        FeatureDetector fd = FeatureDetector.create(featureDetector);
        MatOfKeyPoint mokp = new MatOfKeyPoint();
        fd.detect(src, mokp);

        DescriptorExtractor de = DescriptorExtractor.create(descriptorExtractor);
        Mat descriptors = new Mat();
        de.compute(src, mokp, descriptors);

        Mat out = new Mat();
        Features2d.drawKeypoints(src, mokp, out);
        Util.showResult(out);
        return descriptors;
    }

    public static void findContours(Mat binary, Mat src) {
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat heirarchy = new Mat();
        Imgproc.findContours(binary, contours, heirarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Mat drawing = new Mat(binary.size(), CvType.CV_8UC3);
        for( int i = 0; i< contours.size(); i++ ) {
            Imgproc.boundingRect(contours.get(i));
            Imgproc.drawContours(drawing, contours, i, new Scalar(30.0,255.0,30.0), 1, Imgproc.CV_WARP_FILL_OUTLIERS, heirarchy, 0, new Point());
        }
        Util.showResult(drawing);
    }

    public static class HSV {

        private final Mat hue;
        private final Mat sat;
        private final Mat value;

        public HSV(Mat rgb) {
            Mat hsv = new Mat();

            Imgproc.cvtColor(rgb, hsv, Imgproc.COLOR_BGR2HSV_FULL);

            List<Mat> channelsHSV = new ArrayList<Mat>();

            Core.split(hsv, channelsHSV);

            this.hue = channelsHSV.get(0);
            this.sat = channelsHSV.get(1);
            this.value = channelsHSV.get(2);
        }

        public Mat getHue() {
            return hue;
        }

        public Mat getSat() {
            return sat;
        }

        public Mat getValue() {
            return value;
        }
    }

    public static void hsvOtsu(Mat src) {
        HSV hsv = new HSV(src);

//        Util.showResult(hue);
        Util.showResult(hsv.getSat());
//        Util.showResult(value);

        Mat binary = new Mat();
//        Imgproc.medianBlur(hue, hue, 9);
        Imgproc.erode(hsv.getSat(), binary, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(3, 3)));
        Imgproc.dilate(binary, binary, Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(3, 3)));

        Imgproc.threshold(binary, binary, 0, 360, Imgproc.THRESH_OTSU);
        Util.showResult(binary);
    }


    public static void analyzeHSV(Mat src) {
        Mat hsv = new Mat();

        Imgproc.cvtColor(src, hsv, Imgproc.COLOR_BGR2HSV_FULL);

        List<Mat> channelsHSV = new ArrayList<Mat>();

        Core.split(hsv, channelsHSV);
        Mat hue = channelsHSV.get(0);
        Mat sat = channelsHSV.get(1);
        Mat value = channelsHSV.get(2);

        double mHue = Core.mean(hue).val[0];
        Core.MinMaxLocResult resHue = Core.minMaxLoc(hue);
        double mSat = Core.mean(sat).val[0];
        Core.MinMaxLocResult resSat = Core.minMaxLoc(sat);
        double mValue = Core.mean(value).val[0];
        Core.MinMaxLocResult resValue = Core.minMaxLoc(value);

        Mat hist = new Mat();
        int mHistSize = 25;
        MatOfFloat mRanges = new MatOfFloat(0f, 255f);
        Imgproc.calcHist(channelsHSV, new MatOfInt(2), new Mat(), hist, new MatOfInt(mHistSize), mRanges);
        Core.normalize(hist, hist, 255, 0, Core.NORM_INF);
        float[] buff = new float[mHistSize];
        hist.get(0, 0, buff);

        for (int i = 0; i < mHistSize; i++) {
            System.out.print("[" + i + "|" + (int) buff[i] + "]");
        }

        System.out.printf("H [%d, %d] Mean = %d\n", Math.round(resHue.minVal), Math.round(resHue.maxVal), Math.round(mHue));
        System.out.printf("S [%d, %d] Mean = %d\n", Math.round(resSat.minVal), Math.round(resSat.maxVal), Math.round(mSat));
        System.out.printf("V [%d, %d] Mean = %d\n", Math.round(resValue.minVal), Math.round(resValue.maxVal), Math.round(mValue));

        /*Imgproc.threshold(hue, hue, mHue, 255, Imgproc.THRESH_BINARY);
        Imgproc.threshold(sat, sat, mSat, 255, Imgproc.THRESH_BINARY);
        Imgproc.threshold(value, value, mValue, 255, Imgproc.THRESH_BINARY);*/

        Core.inRange(hue, new Scalar(15), new Scalar(40), hue);
        Core.inRange(sat, new Scalar(10), new Scalar(50), sat);
        Core.inRange(value, new Scalar(10), new Scalar(255), value);

        /*Core.inRange(hue, new Scalar(50), new Scalar(150), hue);
        Imgproc.threshold(sat, sat, 10, 255, Imgproc.THRESH_BINARY);
        Imgproc.threshold(value, value, 50, 255, Imgproc.THRESH_BINARY);*/

        Core.bitwise_and(hue, sat, hsv);
        Core.bitwise_and(hsv, value, hsv);

        Imgproc.dilate(hsv, hsv, Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(3, 3)));

        Imgproc.Canny(hsv, hsv, 0, 255);

        Util.showResult(hsv);
    }


}
