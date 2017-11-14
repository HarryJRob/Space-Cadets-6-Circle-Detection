import java.awt.image.BufferedImage;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class Circle_Detection extends Application {
	
	private static String imgDir = "";
	private static int minRadius = 0;
	private static int maxRadius = 0;
	private static int NoCircles;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			ImageView iView1 = new ImageView();
			ImageView iView4 = new ImageView();
			
			BufferedImage unfilteredImage = imageFunc.loadImage(imgDir);
			
			iView1.setImage(bufferedImgToImg(unfilteredImage));
			
			BufferedImage grayImage = imageFunc.bufferedImagetoGrayScale(imageFunc.applyGaussianBlur(imageFunc.applyGaussianBlur(imageFunc.applyGaussianBlur(unfilteredImage))));
			
			BufferedImage houghImage = imageFunc.applySobelHough(grayImage, minRadius, maxRadius, NoCircles);
			
			iView4.setImage(bufferedImgToImg(houghImage));
			
			iView4.setBlendMode(BlendMode.DIFFERENCE);

			Group blend = new Group(
			        iView1,
			        iView4
			);
			
	        HBox layout = new HBox(10);
	        layout.getChildren().addAll(
	                new ImageView(bufferedImgToImg(unfilteredImage)),
	                blend,
	                new ImageView(bufferedImgToImg(houghImage))
	        );
			
			primaryStage.setScene(new Scene(layout));
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if (args.length == 4) {
			imgDir = args[0];
			minRadius = Integer.parseInt(args[1]);
			maxRadius = Integer.parseInt(args[2]);
			NoCircles = Integer.parseInt(args[3]);
			launch(args);
		} else {
			System.out.println("Usage: Circle_Detection <Image Directory> <Minimum Radius> <Maximum Radius> <No. of Circles>");
			System.exit(0);
		}
	}
	
	//Convert a bufferedImage to a Img for viewing
	private Image bufferedImgToImg(BufferedImage img) {
		return SwingFXUtils.toFXImage(img, null);
	}
	
}
