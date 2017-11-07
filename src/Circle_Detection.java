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
			ImageView iView = new ImageView();
			BufferedImage unfilteredImage = loadImage();
			iView.setImage(bufferedImgToImg(unfilteredImage));
			root.setCenter(iView);
			Scene scene = new Scene(root,400,400);
			
			BufferedImage grayImage = bufferedImagetoGrayScale(unfilteredImage);
			
			iView.setImage(bufferedImgToImg(grayImage));
			
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
	
	private BufferedImage loadImage() {
		BufferedImage returnImg = null;

		try {
		    returnImg = ImageIO.read(new File(imgDir));
		} catch (IOException e) {
			
		}
		
		return returnImg;
	}
	
	private Image bufferedImgToImg(BufferedImage img) {
		return SwingFXUtils.toFXImage(img, null);
	}
	
	private BufferedImage bufferedImagetoGrayScale(BufferedImage img) { 
		return new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_BYTE_BINARY);
	}
}
