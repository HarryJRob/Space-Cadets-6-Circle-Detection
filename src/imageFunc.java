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
					
					int[][] blurMatrix = new int[][] {{1,2,1},{2,4,2},{1,2,1}};
					
					int[][] redMatrix = new int[][] {
						{img.getRGB(x-1,y-1)>>16&0xff,img.getRGB(x,y-1)>>16&0xff,img.getRGB(x+1,y-1)>>16&0xff},
						{img.getRGB(x-1,y)>>16&0xff,img.getRGB(x,y)>>16&0xff,img.getRGB(x+1,y)>>16&0xff},
						{img.getRGB(x-1,y+1)>>16&0xff,img.getRGB(x,y+1)>>16&0xff,img.getRGB(x+1,y+1)>>16&0xff}};
					
					int[][] greenMatrix = new int[][] {
						{img.getRGB(x-1,y-1)>>8&0xff,img.getRGB(x,y-1)>>8&0xff,img.getRGB(x+1,y-1)>>8&0xff},
						{img.getRGB(x-1,y)>>8&0xff,img.getRGB(x,y)>>8&0xff,img.getRGB(x+1,y)>>8&0xff},
						{img.getRGB(x-1,y+1)>>8&0xff,img.getRGB(x,y+1)>>8&0xff,img.getRGB(x+1,y+1)>>8&0xff}};	
						
					int[][] blueMatrix = new int[][] {
						{img.getRGB(x-1,y-1)&0xff,img.getRGB(x,y-1)&0xff,img.getRGB(x+1,y-1)&0xff},
						{img.getRGB(x-1,y)&0xff,img.getRGB(x,y)&0xff,img.getRGB(x+1,y)&0xff},
						{img.getRGB(x-1,y+1)&0xff,img.getRGB(x,y+1)&0xff,img.getRGB(x+1,y+1)&0xff}};	
						
					int red = matrixConvolve(blurMatrix,redMatrix);
					int green = matrixConvolve(blurMatrix,greenMatrix);
					int blue = matrixConvolve(blurMatrix,blueMatrix);
	
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
	
	//My own implementation of Sobel Operator and Hough Transform
	public static BufferedImage applySobelHough(BufferedImage img, int minRadius, int maxRadius, int numberOfCircles) throws Exception {
		//Get the width and height of the image
		int width = img.getWidth();
		int height = img.getHeight();
		double averageEdge = 0;
		
		//The first z contains the color of the edge and the second contains the angle
		double[][][] sobelOutput = new double[width][height][2];
				
		{
			//For each pixel do
			//This avoids the edge of the image completely.
			//In a megapixel wide image the edge pixels make up a tiny amount of image and so can be safely ignored
			for(int y = 1; y < height-1; y++) {
				for(int x = 1; x < width-1; x++) {
					int[][]	gxMatrix = new int[][] {
						{1,0,-1},
						{2,0,-2},
						{1,0,-1}};
					
					int[][] gyMatrix = new int[][] {
						{1 , 2, 1},
						{0 , 0, 0},
						{-1,-2,-1}};	
						
					int[][] pixelMatrix = new int[][] {
						{img.getRGB(x-1,y-1)&0xff,img.getRGB(x-1,y)&0xff,img.getRGB(x-1,y+1)&0xff},
						{img.getRGB(x,y-1)&0xff,img.getRGB(x,y)&0xff,img.getRGB(x,y+1)&0xff},
						{img.getRGB(x+1,y+1)&0xff,img.getRGB(x+1,y)&0xff,img.getRGB(x+1,y+1)&0xff}};
				
					int gx = matrixConvolve(gxMatrix,pixelMatrix);
                
					int gy = matrixConvolve(gyMatrix,pixelMatrix);

					double g = Math.sqrt(Math.pow(gy,2)+Math.pow(gx,2));
					double gTheta = 0;
                
					if(gy != 0) 
						gTheta = Math.atan(gx/gy);

					averageEdge += g;
					sobelOutput[x][y][0] = g;
					sobelOutput[x][y][1] = gTheta;
				}
			}
		}
		
		//Calculate the average value for a edge (this just clears up the image)
		averageEdge /= width*height;
		
		int[][][] voteMatrix = new int[width][height][maxRadius];
		
		{
			
			//For each possible radius do
			for(int r = minRadius; r < maxRadius; r++ ) {
				//For each pixel
				for(int x = 0; x < width; x++) {
					for(int y = 0; y < height; y++) {
						//If the intensity is above average
						if(sobelOutput[x][y][0] > averageEdge) {
							//Not yet implemented
							//Using the angle calculated by the sobel operator find the points which could be the center of a circle
							int a = (int) (x - r * Math.cos(sobelOutput[x][y][1]));
							int b = (int) (y - r * Math.sin(sobelOutput[x][y][1]));
							int c = (int) (x - r * Math.cos(sobelOutput[x][y][1] + Math.PI));
							int d = (int) (y - r * Math.sin(sobelOutput[x][y][1] + Math.PI));
								
							if((a >= 0 && a < width) && (b >= 0 && b < height)) {
								voteMatrix[a][b][r] += 1;
							}
							
							if((c >= 0 && c < width) && (d >= 0 && d < height)) {
								voteMatrix[c][d][r] += 1;
							}
						}
					}
				}
			}
		}
		
		//Find the largest value in the array until u run out of circles to find 
		//Upon finding a circle half the Vote values of the surrounding values in a square of edge length radius/4
		LinkedList<Circle> circleList =  new LinkedList<Circle>();
		
		{
			for(int r = minRadius; r < maxRadius; r++) {
				//Find highest vote
				int highestVote = -1;
				int highestVoteX = -1;
				int highestVoteY = -1;
				for(int x = 0; x < width; x++) {
					for(int y = 0; y < height; y++) {
						if(voteMatrix[x][y][r] > highestVote) {
							highestVote = voteMatrix[x][y][r];
							highestVoteX = x;
							highestVoteY = y;
						}
					}
				}
				
				circleList.add(new Circle(highestVoteX, highestVoteY, r));
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

	private static int matrixConvolve(int[][] weightMatrix, int[][] matrixToConvolve) {
		
		if(weightMatrix.length == matrixToConvolve.length && weightMatrix[0].length == matrixToConvolve[0].length) {
			int width = weightMatrix[0].length;
			int height = weightMatrix.length;
			int total = 0;
			for(int x = 0; x < width; x++) {
				for(int y = 0;y < height; y++) {
					total += weightMatrix[x][y] * matrixToConvolve[x][y];
				}
			}
			return total;
		}
		
		return 0;
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
}
