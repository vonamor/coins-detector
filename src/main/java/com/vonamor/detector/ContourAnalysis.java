package com.vonamor.detector;


import java.util.Arrays;

public class ContourAnalysis {

    public static void main(String[] args) {

        Util.loadOpenCVLibrary();

        String srcImageFilename = "res/coin/mungu.jpg";
//        String srcImageFilename = "res/coin/roubl_avers.jpg";

        processImage(srcImageFilename);

    }

    public static void processImage(String filename) {
        Mat src = Util.readImage(filename);
        Util.showResult(src);

//        Mat gray = new Mat();
//        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.medianBlur(gray, gray, 9);
//        Imgproc.erode(gray, gray, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(3, 3)));

//        Util.showResult(gray);

        hsvOtsu(src);

//        canny(gray);

//        featureDetection(gray);

//        Imgproc.GaussianBlur(gray, gray, new Size(9, 9), 1.0);
//        Imgproc.medianBlur(gray, gray,9);
        Mat binary = new Mat();
//        Scalar mean = Core.mean(gray);
//        Imgproc.Canny(gray, binary, mean.val[0], 255);
//        Imgproc.threshold(gray, binary, mean.val[0], 255, Imgproc.THRESH_OTSU);
//        findContours(binary, src);

//        Imgproc.GaussianBlur(gray, gray, new Size(9, 9), 1.0);


//        analyzeHSV(src);
    }

    public static void canny(Mat gray) {
        Scalar mean = Core.mean(gray);
        System.out.println(Arrays.toString(mean.val));
        Mat canny = new Mat();
        Imgproc.Canny(gray, canny, mean.val[0], 255);
        Util.showResult(canny);
    }

    public static void featureDetection(Mat gray) {
        //        FeatureDetector fd = FeatureDetector.create(FeatureDetector.SIFT);
        FeatureDetector fd = FeatureDetector.create(FeatureDetector.SURF);
        MatOfKeyPoint mokp = new MatOfKeyPoint();
        fd.detect(gray, mokp);

        DescriptorExtractor de = DescriptorExtractor.create(DescriptorExtractor.SIFT);
        Mat descriptors = new Mat();
        de.compute(gray, mokp, descriptors);

        Mat out = new Mat();
        Features2d.drawKeypoints(gray, mokp, out);
        Util.showResult(out);
    }

    public static void findContours(Mat binary, Mat src) {
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat heirarchy = new Mat();
        Imgproc.findContours(binary, contours, heirarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Mat drawing = new Mat(binary.size(), CvType.CV_8UC3);
        for( int i = 0; i< contours.size(); i++ ) {
            Imgproc.drawContours(drawing, contours, i, new Scalar(30.0,255.0,30.0), 1, Imgproc.CV_WARP_FILL_OUTLIERS, heirarchy, 0, new Point());
        }
        Util.showResult(drawing);
    }

    public static void hsvOtsu(Mat src) {
        Mat hsv = new Mat();


        Imgproc.cvtColor(src, hsv, Imgproc.COLOR_BGR2HSV_FULL);

        List<Mat> channelsHSV = new ArrayList<Mat>();

        Core.split(hsv, channelsHSV);
        Mat hue = channelsHSV.get(0);
        Mat sat = channelsHSV.get(1);
        Mat value = channelsHSV.get(2);

        Util.showResult(hue);
        Mat binary = new Mat();
//        Imgproc.medianBlur(hue, hue, 9);
        Imgproc.erode(hue, binary, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(3, 3)));
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
