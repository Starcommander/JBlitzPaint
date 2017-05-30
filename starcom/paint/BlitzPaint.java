package starcom.paint;

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

public class BlitzPaint extends Application
{
  public static Image fullShot;

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
  
  private void takeShot(Stage stage, BlitzPaintFrame frame)
  {
    Task<Void> t = new Task<Void>()
    {
      @Override
      protected Void call() throws Exception
      {
        java.awt.Robot robot = new java.awt.Robot();
        java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
        java.awt.Rectangle rect = new java.awt.Rectangle(tk.getScreenSize());
        java.awt.image.BufferedImage im = robot.createScreenCapture(rect);
        fullShot = SwingFXUtils.toFXImage(im,null);
        return null;
      }
    };
    t.setOnSucceeded((ev) -> { frame.onShowPre(); stage.show(); frame.onShowPost(); });
    new Thread(t).start();
  }

  private boolean handleArgs(Parameters parameters, BlitzPaintFrame frame)
  {
    List<String> args = parameters.getUnnamed();
    if (args.size()==0) { return false; }
    if (args.get(0).equals("-screenshot")) { return true; }
    if (!new File(args.get(0)).isFile()) { return false; }
    if (args.get(0).endsWith(".png")) { frame.loadSaveFileDirect(false, args.get(0)); }
    if (args.get(0).endsWith(".jpg")) { frame.loadSaveFileDirect(false, args.get(0)); }
    if (args.get(0).endsWith(".bmp")) { frame.loadSaveFileDirect(false, args.get(0)); }
    return false;
  }

}
