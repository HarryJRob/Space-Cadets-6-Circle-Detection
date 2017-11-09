import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
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
			
			Rectangle2D psb = Screen.getPrimary().getVisualBounds();
			Scene scene = new Scene(root,psb.getWidth(),psb.getHeight());
			
			BufferedImage grayImage = bufferedImagetoGrayScale(unfilteredImage);
			
			iView2.setImage(bufferedImgToImg(grayImage));
			
			BufferedImage sobelImage = applySobel(grayImage);
			
			iView3.setImage(bufferedImgToImg(sobelImage));
			
			BufferedImage houghImage = applyHoughTransform(sobelImage);
			
			iView4.setImage(bufferedImgToImg(houghImage));
			
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
			System.exit(0);
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
	
	//Apply sobel to a BufferedImage
	private BufferedImage applySobel(BufferedImage img) {
		
		//Get the width and height of the image
		int width = img.getWidth();
		int height = img.getHeight();
		
		//Create a image to output to
		BufferedImage outputImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		
		//For each pixel do
		//This avoids the edge of the image completely.
		//In a megapixel wide image the edge pixels make up a tiny amount of image and so can be safely ignored
		for(int y = 1; y < height-1; y++) {
			for(int x = 1; x < width-1; x++) {
				int[][] pixelMatrix = new int[3][3];
                pixelMatrix[0][0]=img.getRGB(x-1,y-1)&0xff;
                pixelMatrix[0][1]=img.getRGB(x-1,y)&0xff;
                pixelMatrix[0][2]=img.getRGB(x-1,y+1)&0xff;
                
                pixelMatrix[1][0]=img.getRGB(x,y-1)&0xff;
                pixelMatrix[1][2]=img.getRGB(x,y+1)&0xff;
                
                pixelMatrix[2][0]=img.getRGB(x+1,y-1)&0xff;
                pixelMatrix[2][1]=img.getRGB(x+1,y)&0xff;
                pixelMatrix[2][2]=img.getRGB(x+1,y+1)&0xff;
				
    				int gx=(pixelMatrix[0][0]*1) + (pixelMatrix[0][1]*2) + (pixelMatrix[0][2]*1)+
    					(pixelMatrix[2][0]*-1) + (pixelMatrix[2][1]*-2) + (pixelMatrix[2][2]*-1);
                
        			int gy=(pixelMatrix[0][0]*1) + (pixelMatrix[1][0]*2) + (pixelMatrix[2][0]*1)+
        				(pixelMatrix[0][2]*-1) + (pixelMatrix[1][2]*-2) + (pixelMatrix[2][2]*-1);

                int g = (int) Math.sqrt(Math.pow(gy,2)+Math.pow(gx,2));
                
                outputImage.setRGB(x,y,(g<<16|g<<8|g));
			}
		}
		return outputImage;
	}
	
	//Apply circular Hough transform to the image
	private BufferedImage applyHoughTransform(BufferedImage img) {
	
		int width = img.getWidth();
		int height = img.getHeight();
		int maxRadius = 0;
		final int minRadius = 77;
		final int incrementRadius = 1;
		final int circleNum = 9;
		
		//Define the maximum size of a circle in an image
		if(height/2 >= width/2) {
			maxRadius = height/2;
		} else {
			maxRadius = width/2;
		}
		
		//The threshold at which a colour will be recognised as an edge
		int colorThresh = 0;
		int colorAvg = 0;
		
		for(int x = 0; x < width;x++) {
			for(int y = 0; y < height;y++) {
				colorAvg += img.getRGB(x, y)&0x000000FF;
			}
		}
		
		colorThresh = (int) colorAvg / (width*height);
		
		BufferedImage outputImg = new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);
		int[][][] accumulator = new int[width][height][maxRadius];
		
		//For each pixel
		for(int x = 0; x < width; x++ ) {
			for(int y = 0; y < height; y++) {
				//For each pixel where it is an edge
			    if((img.getRGB(x,y)&0x000000FF) >= (0.3f * colorThresh)) {
			    	//For each radius
					for(int radius = minRadius; radius < maxRadius; radius++) {
						//For each angle
						for(int theta = 0; theta < 360; theta +=incrementRadius) {
							//Calculate the middle coordinates of the circle;
							int a = (int) (x - radius * Math.cos(theta * Math.PI/180));
							int b = (int) (y - radius * Math.sin(theta * Math.PI/180));
							
							if((a >= 0 && a < width) && (b >= 0 && b < height)) {
								accumulator[a][b][radius] += 1;
							}
						}
					}
			    }
			}
		}

		//Find the average vote across all pixels and radii
		int votingAvg = 0;
		
		for(int x=0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				for(int r = minRadius; r < maxRadius; r++) {
					votingAvg += accumulator[x][y][r];
				}
			}
		}
		
		votingAvg /= width*height*(maxRadius - minRadius);
		
		LinkedList<Circle> circleList =  new LinkedList<Circle>();
		
		//Where the vote is greater than average do stuff
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				for(int r = minRadius; r < maxRadius; r++) {
					
					int curVote = accumulator[x][y][r];
					
					//If the current vote is higher than average
					if(curVote > votingAvg) {
						//If the circle list is not full
						if(circleList.size() < circleNum) {
							circleList.add(new Circle(x,y,r,curVote));
							
						//Else find the lowest priority circle i.e lowest votes
						} else {
							int curLowestPriority = curVote;
							int lowestCircle = -1;
							for(int i = 0; i < circleList.size(); i++) {
								if(circleList.get(i).getPriority() < curLowestPriority) {
									lowestCircle = i;
									curLowestPriority = circleList.get(i).getPriority();
								}
							}
							
							if(lowestCircle != -1) {
								circleList.set(lowestCircle, new Circle(x,y,r,curVote));
							}
						}
					}
				}
			}
		}
		
		
		//Now plot the circles on the image
		for(Circle curCircle : circleList) {
			int x = curCircle.getX();
			int y = curCircle.getY();
			int radius = curCircle.getRadius();
			System.out.println(curCircle.getPriority());
			for(int theta = 0; theta < 360; theta++) {
				int a = (int) (x - radius * Math.cos(theta * Math.PI/180));
				int b = (int) (y - radius * Math.sin(theta * Math.PI/180));
				if((a >= 0 && a < width) && (b >= 0 && b < width))
					outputImg.setRGB(a, b, 0xffffff);
			}
		}
		
		return outputImg;
		
	}
	
}
