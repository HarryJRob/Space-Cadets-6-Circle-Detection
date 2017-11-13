import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

public abstract class imageFunc {

	
	//			Public Functions
	
	
	//Apply a Gaussian blur to a image
	public static BufferedImage applyGaussianBlur(BufferedImage img) {
		BufferedImage outputImage = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		
		for(int x = 0; x < img.getWidth(); x++) {
			for(int y = 0; y < img.getHeight(); y++) {
				if(x == 0 || y == 0 || x == img.getWidth()-1 || y == img.getHeight() - 1) {
					outputImage.setRGB(x, y, img.getRGB(x, y));
				} else {
					/*
					 *  1 2 1  
						2 4 2  
						1 2 1  
					 */
					
					Color[][] pixelMatrix = new Color[3][3];
	                pixelMatrix[0][0]=new Color(img.getRGB(x-1,y-1));
	                pixelMatrix[0][1]=new Color(img.getRGB(x-1,y));
	                pixelMatrix[0][2]=new Color(img.getRGB(x-1,y+1));
	                
	                pixelMatrix[1][0]=new Color(img.getRGB(x,y-1));
	                pixelMatrix[1][1]=new Color(img.getRGB(x,y));
	                pixelMatrix[1][2]=new Color(img.getRGB(x,y+1));
	                
	                pixelMatrix[2][0]=new Color(img.getRGB(x+1,y-1));
	                pixelMatrix[2][1]=new Color(img.getRGB(x+1,y));
	                pixelMatrix[2][2]=new Color(img.getRGB(x+1,y+1));
	                
	                int red = (pixelMatrix[0][0].getRed()) + (pixelMatrix[0][1].getRed()*2) + (pixelMatrix[0][2].getRed()) +
	                		(pixelMatrix[1][0].getRed()*2) + (pixelMatrix[1][1].getRed()*4) + (pixelMatrix[1][2].getRed()*2) +
	                		(pixelMatrix[2][0].getRed()) + (pixelMatrix[2][1].getRed()*2) + (pixelMatrix[2][2].getRed());
	                
	                int green = (pixelMatrix[0][0].getGreen()) + (pixelMatrix[0][1].getGreen()*2) + (pixelMatrix[0][2].getGreen()) +
	                		(pixelMatrix[1][0].getGreen()*2) + (pixelMatrix[1][1].getGreen()*4) + (pixelMatrix[1][2].getGreen()*2) +
	                		(pixelMatrix[2][0].getGreen()) + (pixelMatrix[2][1].getGreen()*2) + (pixelMatrix[2][2].getGreen());
	                
	                int blue = (pixelMatrix[0][0].getBlue()) + (pixelMatrix[0][1].getBlue()*2) + (pixelMatrix[0][2].getBlue()) +
	                		(pixelMatrix[1][0].getBlue()*2) + (pixelMatrix[1][1].getBlue()*4) + (pixelMatrix[1][2].getBlue()*2) +
	                		(pixelMatrix[2][0].getBlue()) + (pixelMatrix[2][1].getBlue()*2) + (pixelMatrix[2][2].getBlue());
	
	                red /= 16;
	                green /= 16;
	                blue /= 16;
	                
	                outputImage.setRGB(x, y, (red<<16) | (green<<8) | blue);
				}
			}
		}
		
		return outputImage;
	}
	
	//Apply Grayscale to a buffered image
	public static BufferedImage bufferedImagetoGrayScale(BufferedImage img) { 
	    int width = img.getWidth();
	    int height = img.getHeight();

	    BufferedImage outputImage = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
	    
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
	
	//Loads a BufferedImage from a file directory
	public static BufferedImage loadImage(String imgDir) throws IOException {
		BufferedImage returnImg = null;
		returnImg = ImageIO.read(new File(imgDir));
		return returnImg;
	}
	
	//Saves a bufferedImage to a file directory
	public static void saveImage(String imgDir, BufferedImage img) throws IOException {
		ImageIO.write(img, "jpg", new File(imgDir));
	}
	
	//My own implementation of Sobel Operator and Hough Transform
	public static BufferedImage applySobelHough(BufferedImage img, int minRadius, int maxRadius, int numberOfCircles) throws Exception {
		//Get the width and height of the image
		int width = img.getWidth();
		int height = img.getHeight();
		
		//The first z contains the color of the edge and the second contains the angle
		int[][][] sobelOutput = new int[width][height][2];
				
		{
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
                int gTheta = 0;
                
                if(gy != 0) 
                	gTheta = (int) Math.atan(gx/gy);

                
                sobelOutput[x][y][0] = g;
                sobelOutput[x][y][1] = gTheta;
				}
			}
		}
		
		//Calculate the average value for a edge (this just clears up the image)
		int averageEdge = 0;
		
		{
			for(int x = 0; x < width; x++) {
				for(int y = 0; y < height; y++) {
					averageEdge += sobelOutput[x][y][0];
				}
			}
			
			averageEdge /= width*height;
		}
		
		int[][] centerVoteMatrix = new int[width][height];
		int[][][] radiusVoteMatrix = new int[width][height][maxRadius];
		
		{
			
			//For each possible radius do
			for(int r = minRadius; r < maxRadius; r++ ) {
				//For each pixel
				for(int x = 0; x < width; x++) {
					for(int y = 0; y < height; y++) {
						//If the intensity is above average
						if(sobelOutput[x][y][0] > averageEdge) {
							//Using the angle calculated by the sobel operator find the points which could be the centre of a circle
							for(int theta = 0; theta < 360; theta++) {
								//Calculate the middle coordinates of the circle and add them to the accumulator
								int a = (int) (x - r * Math.cos(theta * Math.PI/180));
								int b = (int) (y - r * Math.sin(theta * Math.PI/180));
									
								if((a >= 0 && a < width) && (b >= 0 && b < height)) {
									centerVoteMatrix[a][b] += 1;
									radiusVoteMatrix[a][b][r] +=1;
								}
								
							}
						
						}
					}
				}
			}
		}
		
		//Find the largest value in the array until u run out of circles to find 
		//Upon finding a circle half the Vote values of the surrounding values in a square of edge length radius/4
		LinkedList<Circle> circleList = new LinkedList<Circle>();
		
		{
			for(int circleNum = 0; circleNum < numberOfCircles; circleNum++) {
				//Find highest vote
				int curHighestVote = -1;
				int curHighestX = -1;
				int curHighestY = -1;
				for(int x = 0; x < width; x++) {
					for(int y = 0; y < height; y++) {
						if(centerVoteMatrix[x][y] > curHighestVote) {
							curHighestVote = centerVoteMatrix[x][y];
							curHighestX = x;
							curHighestY = y;
						}
					}
				}
				
				//Find highest radius vote for the highest vote
				int highestRadius = -1;
				int radiusVote = -1;
				for(int r = minRadius; r < maxRadius; r++) {
					if(radiusVoteMatrix[curHighestX][curHighestY][r] > radiusVote) {
						highestRadius = r;
						radiusVote = radiusVoteMatrix[curHighestX][curHighestY][r]; 
					}
				}
				System.out.println("Found most likely circle: (" + curHighestX + " , " + curHighestY + ") of radius " + highestRadius);
				
				//Half the nearby vote values so that it isn't detected again unless it was incredibly popular for a different radius
				for(int x = 0; x < width; x++) {
					for(int y = 0; y < height; y++) {
						if(x > curHighestX - highestRadius/2 && x < curHighestX + highestRadius/2 && y > curHighestY - highestRadius/2 && y < curHighestY + highestRadius/2) {
							centerVoteMatrix[x][y] /= 2;
							radiusVoteMatrix[x][y][highestRadius] /= 2;
						}
					}
				}
				
				//Now store this circle for drawing later
				circleList.add(new Circle(curHighestX,curHighestY,highestRadius));
			}
		}

		//Now to draw stuff
		BufferedImage outputImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		
		{
			//For each circle you found draw them
			for(Circle curCircle : circleList) {
				for(int theta = 0; theta < 360; theta++) {
					int x = (int) (curCircle.getX() + curCircle.getRadius() * Math.cos(theta * Math.PI/180));
					int y = (int) (curCircle.getY() + curCircle.getRadius() * Math.sin(theta * Math.PI/180));
					if(x > 0 && x < width && y > 0 && y < height)
						outputImage.setRGB(x, y, 0xffffffff);
				}
			}
		}
		
		//Finally return the image (that took a while)
		return outputImage;
	}
}
