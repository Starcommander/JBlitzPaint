package com.starcom.app;

import java.io.File;
import java.util.List;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.robot.Robot;

import com.starcom.paint.Frame;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.WritableImage;
import javafx.stage.Screen;

public class BlitzPaint extends Application
{
  public static void main(String[] args)
  {
    Application.launch(BlitzPaint.class, args);
  }

  @Override
  /** This method is called when initialization is complete **/
  public void init() {}

  @Override
  public void start(Stage stage) throws Exception
  {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BlitzPaint.fxml"));
    Parent root = (Parent)fxmlLoader.load();
    BlitzPaintFrame frame = fxmlLoader.getController();
    stage.setTitle("### JBlitzPaint ###");
    stage.setScene(new Scene(root, 500, 400));
    stage.getIcons().add(new Image(getClass().getResourceAsStream("icons/video_display.png")));
    boolean b_shot = handleArgs(getParameters(), frame);
    if (b_shot) { takeShot(stage, frame); }
    else { frame.onShowPre(); stage.show(); frame.onShowPost(); }
  }
  
  private static void takeShot(Stage stage, BlitzPaintFrame frame)
  {
        Robot robot = new Robot();
        var screenRect = Screen.getPrimary().getBounds();
        WritableImage wi = new WritableImage((int)screenRect.getWidth(),(int)screenRect.getHeight());
        Frame.fullShot = robot.getScreenCapture(wi, screenRect);
        robot.mouseMove(30,30);
        frame.onShowPre();
        stage.show();
        frame.onShowPost();
  }

  private static boolean handleArgs(Parameters parameters, BlitzPaintFrame frame)
  {
    List<String> args = parameters.getUnnamed();
    if (args.size()==0) { return false; }
    if (args.get(0).equals("-screenshot")) { return true; }
    if (!new File(args.get(0)).isFile()) { return false; }
    boolean isImage = com.starcom.Stg.endsWithIgnoreCase(args.get(0), 4, ".png", ".jpg", ".bmp");
    if (isImage) { Frame.fullShot = new Image("file:" + args.get(0)); }
    return false;
  }

}
