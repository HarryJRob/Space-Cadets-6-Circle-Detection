import java.awt.image.BufferedImage;

public abstract class imageFunc {

	//Grayscale a buffered image
	public static BufferedImage bufferedImagetoGrayScale(BufferedImage img) { 
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
	public static BufferedImage applySobelOperator(BufferedImage img) {
		
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
	
	public static int[][][] applyHoughTransform(double[][][] sobelOutput) {
		
		
		
		return null;
	}
	
}
