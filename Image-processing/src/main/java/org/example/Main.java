package org.example;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static final String SOURCE_FILE = "src/main/resources/image.jpg";
    public static final String DESTINATION = "./target/image.jpg";
    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File(SOURCE_FILE));
        BufferedImage resultImage = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_BGR);
        long startTime = System.currentTimeMillis();
        //recolorSingleThreaded(image,resultImage);
        recolorMultiThreaded(image,resultImage,6);
        long endTime = System.currentTimeMillis();
        long duration = endTime-startTime;
        File outputFile = new File(DESTINATION);
        ImageIO.write(resultImage,"jpg",outputFile);
        System.out.println(String.valueOf(duration));
    }

    public static void recolorSingleThreaded(BufferedImage or,BufferedImage res){
        recolorImage(or,res,0,0,or.getWidth(),or.getHeight());

    }
    public static void recolorMultiThreaded(BufferedImage or,BufferedImage res,int nulThreads){
        List<Thread> threads = new ArrayList<>();
        int width =  or.getWidth();
        int height = or.getHeight()/nulThreads;

        for (int i = 0; i < nulThreads; i++) {
            final int threadMul = i;
            Thread thread = new Thread(() -> {
                int leftCorner = 0;
                int topCorner = height*threadMul;
                recolorImage(or,res,leftCorner,topCorner,width,height);
            });
            threads.add(thread);

        }
        for(Thread thread: threads){
            thread.start();
        }
        for (Thread thread: threads){
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static void recolorImage(BufferedImage original,BufferedImage result, int leftCorner,int topCorner,
                                    int width,int height){
        for(int x =leftCorner; x <leftCorner+ width &&  x < original.getWidth() ; x++){
            for(int y = topCorner ; y < topCorner + height  &&  y < original.getHeight();y++){
                 recolorPixel( original,result,x,y);
            }
        }
    }

    public static void recolorPixel(BufferedImage original , BufferedImage res , int x,int y){
        int rgb = original.getRGB(x,y);
        int red = getRed(rgb);
        int blue = getBlue(rgb);
        int green = getGreen(rgb);
        int newRed,newGreen,newBlue;
        if(isShadeofGrey(blue,red,green)){
            newRed = Math.min(255,red + 10);
            newGreen = Math.max(0,green -80);
            newBlue = Math.max(0,blue - 20);
        }else {
            newBlue = blue;
            newRed = red;
            newGreen = green;
        }
        int newRgb = createRGBfromColors(newBlue,newRed,newGreen);

    }

    public static void setRGB(BufferedImage image,int x , int y , int rgb){
        image.getRaster().setDataElements(x,y,image.getColorModel().getDataElements(rgb,null));
    }
    public static boolean isShadeofGrey(int blue,int red,int green){
        return Math.abs(red-blue)<30 && Math.abs(red-green)<30 &&Math.abs(green-blue)<30 ;
    }

    public static int createRGBfromColors(int blue,int red,int green){
        int rgb = 0;

        rgb |= blue;
        rgb |= red << 16;
        rgb |= green << 8;

        rgb |= 0xFF000000;
        return rgb;
    }

    public static int getBlue(int rgb){
        return rgb & 0x000000FF;
    }
    public static int getGreen(int rgb){
        return (rgb & 0x0000FF00) >> 8;
    }
    public static int getRed(int rgb){
        return (rgb & 0x00FF0000) >> 16;
    }
}

