package com.sohu.wap.util;
import java.io.*;  
import java.awt.image.*;  
import java.awt.geom.AffineTransform;  
import java.awt.color.ColorSpace;  
import java.awt.image.ConvolveOp;  
import java.awt.image.Kernel;  
import java.awt.image.BufferedImage;  
import javax.imageio.ImageIO;  
//import javax.media.jai.JAI;
//import javax.media.jai.RenderedOp;
//
//import com.sun.media.jai.codec.BMPEncodeParam;
//import com.sun.media.jai.codec.ImageCodec;
//import com.sun.media.jai.codec.ImageEncoder;
//import com.sun.media.jai.codec.TIFFEncodeParam;

import java.awt.Toolkit;  
import java.awt.Image;  
  
/** 
 * <p>Title: Image Filter</p> 
 * 
 * <p>Description: image processing by filters </p> 
 * <p>Copyright: Copyright (c) 2010</p> 
 * 
 * @author gl74gs48@163.com 
 * @since jdk1.5.0 
 * @version 1.0 
 */  
public class MyImgFilter {  
    BufferedImage image;  
    private int iw, ih;  
    private int[] pixels;  
  
  
    public MyImgFilter(BufferedImage image) {  
        this.image = image;  
        iw = image.getWidth();  
        ih = image.getHeight();  
        pixels = new int[iw * ih];  
  
    }  
  
    /** 图像二值化 */  
    public BufferedImage changeGrey() {  
  
  
        PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, iw, ih, pixels,0, iw);  
        try {  
            pg.grabPixels();  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
        // 设定二值化的域值，默认值为100  
        int grey = 200;  
        // 对图像进行二值化处理，Alpha值保持不变  
        ColorModel cm = ColorModel.getRGBdefault();  
        for (int i = 0; i < iw * ih; i++) {  
            int red, green, blue;  
            int alpha = cm.getAlpha(pixels[i]);  
            if (cm.getRed(pixels[i]) > grey) {  
                red = 255;  
            } else {  
                red = 0;  
            }  
            if (cm.getGreen(pixels[i]) > grey) {  
                green = 255;  
            } else {  
                green = 0;  
            }  
            if (cm.getBlue(pixels[i]) > grey) {  
                blue = 255;  
            } else {  
                blue = 0;  
            }  
            pixels[i] = alpha << 24 | red << 16 | green << 8 | blue; //通过移位重新构成某一点像素的RGB值  
        }  
        // 将数组中的象素产生一个图像  
        Image tempImg=Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(iw,ih, pixels, 0, iw));  
        image = new BufferedImage(tempImg.getWidth(null),tempImg.getHeight(null), BufferedImage.TYPE_INT_BGR );  
        image.createGraphics().drawImage(tempImg, 0, 0, null); 
        
        return image;  
  
    }  
      
    /** 中值滤波 */  
    public BufferedImage getMedian() {  
        PixelGrabber pg = new PixelGrabber(image.getSource(), 0, 0, iw, ih,  
                                           pixels,  
                                           0, iw);  
        try {  
            pg.grabPixels();  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
        // 对图像进行中值滤波，Alpha值保持不变  
        ColorModel cm = ColorModel.getRGBdefault();  
        for (int i = 1; i < ih - 1; i++) {  
            for (int j = 1; j < iw - 1; j++) {  
                int red, green, blue;  
                int alpha = cm.getAlpha(pixels[i * iw + j]);  
  
                // int red2 = cm.getRed(pixels[(i - 1) * iw + j]);  
                int red4 = cm.getRed(pixels[i * iw + j - 1]);  
                int red5 = cm.getRed(pixels[i * iw + j]);  
                int red6 = cm.getRed(pixels[i * iw + j + 1]);  
                // int red8 = cm.getRed(pixels[(i + 1) * iw + j]);  
  
                // 水平方向进行中值滤波  
                if (red4 >= red5) {  
                    if (red5 >= red6) {  
                        red = red5;  
                    } else {  
                        if (red4 >= red6) {  
                            red = red6;  
                        } else {  
                            red = red4;  
                        }  
                    }  
                } else {  
                    if (red4 > red6) {  
                        red = red4;  
                    } else {  
                        if (red5 > red6) {  
                            red = red6;  
                        } else {  
                            red = red5;  
                        }  
                    }  
                }  
  
                int green4 = cm.getGreen(pixels[i * iw + j - 1]);  
                int green5 = cm.getGreen(pixels[i * iw + j]);  
                int green6 = cm.getGreen(pixels[i * iw + j + 1]);  
  
                // 水平方向进行中值滤波  
                if (green4 >= green5) {  
                    if (green5 >= green6) {  
                        green = green5;  
                    } else {  
                        if (green4 >= green6) {  
                            green = green6;  
                        } else {  
                            green = green4;  
                        }  
                    }  
                } else {  
                    if (green4 > green6) {  
                        green = green4;  
                    } else {  
                        if (green5 > green6) {  
                            green = green6;  
                        } else {  
                            green = green5;  
                        }  
                    }  
                }  
  
                // int blue2 = cm.getBlue(pixels[(i - 1) * iw + j]);  
                int blue4 = cm.getBlue(pixels[i * iw + j - 1]);  
                int blue5 = cm.getBlue(pixels[i * iw + j]);  
                int blue6 = cm.getBlue(pixels[i * iw + j + 1]);  
                // int blue8 = cm.getBlue(pixels[(i + 1) * iw + j]);  
  
                // 水平方向进行中值滤波  
                if (blue4 >= blue5) {  
                    if (blue5 >= blue6) {  
                        blue = blue5;  
                    } else {  
                        if (blue4 >= blue6) {  
                            blue = blue6;  
                        } else {  
                            blue = blue4;  
                        }  
                    }  
                } else {  
                    if (blue4 > blue6) {  
                        blue = blue4;  
                    } else {  
                        if (blue5 > blue6) {  
                            blue = blue6;  
                        } else {  
                            blue = blue5;  
                        }  
                    }  
                }  
                pixels[i * iw +  
                        j] = alpha << 24 | red << 16 | green << 8 | blue;  
            }  
        }  
  
        // 将数组中的象素产生一个图像  
        Image tempImg=Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(iw,ih, pixels, 0, iw));  
        image = new BufferedImage(tempImg.getWidth(null),tempImg.getHeight(null), BufferedImage.TYPE_INT_BGR );  
        image.createGraphics().drawImage(tempImg, 0, 0, null);  
        return image;  
  
    }  
  
  
  
  
    public BufferedImage getGrey() {  
        ColorConvertOp ccp=new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);  
        return image=ccp.filter(image,null);  
    }  
    
    public BufferedImage getBinary() {  
    	
    	 BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);  
    	    for(int i= 0 ; i < image.getWidth() ; i++){  
    	        for(int j = 0 ; j < image.getHeight(); j++){  
    	        int rgb = image.getRGB(i, j);  
    	        grayImage.setRGB(i, j, rgb);  
    	        }  
    	    }  
       
        return image=grayImage;  
    }  
  
    //Brighten using a linear formula that increases all color values  
    public BufferedImage getBrighten() {  
        RescaleOp rop=new RescaleOp(1.25f, 0, null);  
        return image=rop.filter(image,null);  
    }  
    //Blur by "convolving" the image with a matrix  
    public BufferedImage getBlur() {  
        float[] data = {  
                       .1111f, .1111f, .1111f,  
                       .1111f, .1111f, .1111f,  
                       .1111f, .1111f, .1111f, };  
        ConvolveOp cop = new ConvolveOp(new Kernel(3, 3, data));  
        return image=cop.filter(image,null);  
  
    }  
  
    // Sharpen by using a different matrix  
    public BufferedImage getSharpen() {  
        float[] data = {  
                       0.0f, -0.75f, 0.0f,  
                       -0.75f, 4.0f, -0.75f,  
                       0.0f, -0.75f, 0.0f};  
        ConvolveOp cop = new ConvolveOp(new Kernel(3, 3, data));  
        return image=cop.filter(image,null);  
    }  
    // 11) Rotate the image 180 degrees about its center point  
    public BufferedImage getRotate() {  
        AffineTransformOp atop=new AffineTransformOp(AffineTransform.getRotateInstance(Math.PI,image.getWidth()/2,image.getHeight()/2),  
                AffineTransformOp.TYPE_NEAREST_NEIGHBOR);  
        return image=atop.filter(image,null);  
    }  
      
    public BufferedImage getProcessedImg()  
    {  
        return image;  
    }  
  
    /**
     * 
     *对图片作为处理便于识别 
     * 
     */
    public static void transformImg(String oriImg, String destImg) throws IOException{
    	 FileInputStream fin=new FileInputStream(oriImg);  
         BufferedImage bi=ImageIO.read(fin);  
         MyImgFilter flt=new MyImgFilter(bi);  
         flt.changeGrey();  
         flt.getGrey();
         flt.getBinary();
//         flt.getBinary();
//         flt.getMedian();
//         flt.getBrighten(); //解决64位bug 
         bi=flt.getProcessedImg();  
         File file = new File(destImg);  
         
         OutputStream os = new FileOutputStream(file);
//         toTiff(bi,os);
         ImageIO.write(bi, "jpg", file); 
    }
    
//    public static void toTiff(RenderedImage src, OutputStream  os) throws IOException{
//        /*tif转换到bmp格式*/  
//        TIFFEncodeParam param = new TIFFEncodeParam();  
//        ImageEncoder enc = ImageCodec.createImageEncoder("TIFF", os,param);  
//        enc.encode(src);  
//        os.close();//关闭流 
//    }
    
    
    
    public static void main(String[] args) throws IOException {  
    	MyImgFilter.transformImg("/home/wjj/tmp/a.gif", "/home/wjj/tmp/b.jpg");
//        FileInputStream fin=new FileInputStream(args[0]);  
//        BufferedImage bi=ImageIO.read(fin);  
//        MyImgFilter flt=new MyImgFilter(bi);  
//        flt.changeGrey();  
//        flt.getGrey();  
//       flt.getBrighten();  
//        bi=flt.getProcessedImg();  
//        flt.changeGrey();  
//        bi=flt.getProcessedImg();  
//        String pname=args[0].substring(0,args[0].lastIndexOf("."));  
//        File file = new File(pname +".jpg");  
//        ImageIO.write(bi, "jpg", file);  
    }  
  
}  