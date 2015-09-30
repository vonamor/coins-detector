package com.vonamor.detector;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class Util {

    public static int DEFAULT_SHOW_IMAGE_HEIGHT = 500;

    public static String getFullFilename(String filename) {
        return "res/out/" + filename;
    }

    public static void loadOpenCVLibrary() {
        System.load("E://opencv/build/java/x64/opencv_java248.dll");
//        System.load("D://opencv/build/java/x86/opencv_java248.dll");
    }

    public static void writeImage(Mat mat, String filename) {
        System.out.println(String.format("Write image %s", filename));
        Highgui.imwrite(filename, mat);
    }

    public static Mat readImage(String filename) {
        return Highgui.imread(filename);
    }

    public static void showResult(Mat img) {
        Mat tmp = new Mat();
        float prop = img.width() * 1.0f / img.height();
        int width = Math.round(prop * DEFAULT_SHOW_IMAGE_HEIGHT);

        Imgproc.resize(img, tmp, new Size(width, DEFAULT_SHOW_IMAGE_HEIGHT));

        MatOfByte matOfByte = new MatOfByte();
        Highgui.imencode(".jpg", tmp, matOfByte);
        byte[] byteArray = matOfByte.toArray();

        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            BufferedImage bufImage = ImageIO.read(in);
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
