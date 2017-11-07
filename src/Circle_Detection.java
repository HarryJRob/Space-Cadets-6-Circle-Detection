import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;


public class Circle_Detection extends Application {
	
	private static String imgDir = "";
	
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			ImageView iView1 = new ImageView();
			ImageView iView2 = new ImageView();
			ImageView iView3 = new ImageView();
			ImageView iView4 = new ImageView();
			BufferedImage unfilteredImage = loadImage();
			iView1.setImage(bufferedImgToImg(unfilteredImage));
			root.setTop(iView1);
			root.setLeft(iView2);
			root.setRight(iView3);
			root.setBottom(iView4);
			Scene scene = new Scene(root,400,400);
			
			BufferedImage grayImage = bufferedImagetoGrayScale(unfilteredImage);
			
			iView2.setImage(bufferedImgToImg(grayImage));
			
			BufferedImage sobelImage = applySobel(grayImage);
			
			iView3.setImage(bufferedImgToImg(sobelImage));
			
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if (args.length == 1) {
			imgDir = args[0];
			launch(args);
		} else {
			System.out.println("Usage: Circle_Detection <Image Directory>");
		}
	}
	
	//Load the image as a BufferedImage file
	private BufferedImage loadImage() {
		BufferedImage returnImg = null;

		try {
		    returnImg = ImageIO.read(new File(imgDir));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return returnImg;
	}
	
	//Convert a bufferedImage to a Img for viewing
	private Image bufferedImgToImg(BufferedImage img) {
		return SwingFXUtils.toFXImage(img, null);
	}
	
	//Grayscale a buffered image
	private BufferedImage bufferedImagetoGrayScale(BufferedImage img) { 
	    int width = img.getWidth();
	    int height = img.getHeight();

	    BufferedImage outputImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
	    
	    //for each pixel of the image do 
	    for(int y = 0; y < height; y++){
	      for(int x = 0; x < width; x++){
	    	//get the RBG value of the picture
	        int p = img.getRGB(x,y);

	        //split the RGB value into the components: Alpha, Red, Green, Blue
	        //0xAARRGGBB
	        int r = (p>>16)&0xff;
	        int g = (p>>8)&0xff;
	        int b = p&0xff;

	        //Average the RBG components
	        int avg = (r+g+b)/3;

	        //Set the RBG to the average
	        outputImage.setRGB(x, y, (avg<<16) | (avg<<8) | avg);
	      }
	    }
	    
	    return outputImage;
	}
	
	private BufferedImage applySobel(BufferedImage img) {
		
		int width = img.getWidth();
		int height = img.getHeight();
		
		BufferedImage outputImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		
		for(int y = 1; y < height-1; y++) {
			for(int x = 1; x < width-1; x++) {
				int[][] pixelMatrix = new int[3][3];
                pixelMatrix[0][0]=new Color(img.getRGB(x-1,y-1)).getRed();
                pixelMatrix[0][1]=new Color(img.getRGB(x-1,y)).getRed();
                pixelMatrix[0][2]=new Color(img.getRGB(x-1,y+1)).getRed();
                pixelMatrix[1][0]=new Color(img.getRGB(x,y-1)).getRed();
                pixelMatrix[1][2]=new Color(img.getRGB(x,y+1)).getRed();
                pixelMatrix[2][0]=new Color(img.getRGB(x+1,y-1)).getRed();
                pixelMatrix[2][1]=new Color(img.getRGB(x+1,y)).getRed();
                pixelMatrix[2][2]=new Color(img.getRGB(x+1,y+1)).getRed();
				
        		int gy=(pixelMatrix[0][0]*-1)+(pixelMatrix[0][1]*-2)+(pixelMatrix[0][2]*-1)+
        				(pixelMatrix[2][0])+(pixelMatrix[2][1]*2)+(pixelMatrix[2][2]*1);
        		int gx=(pixelMatrix[0][0])+(pixelMatrix[0][2]*-1)+(pixelMatrix[1][0]*2)+
        				(pixelMatrix[1][2]*-2)+(pixelMatrix[2][0])+(pixelMatrix[2][2]*-1);
                
                int convoResult = (int) Math.sqrt(Math.pow(gy,2)+Math.pow(gx,2));
                
                outputImage.setRGB(x,y,(convoResult<<16|convoResult<<8|convoResult));
			}
		}
		
		
		return outputImage;
	}
	
}
