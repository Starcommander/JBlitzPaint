package starcom.paint;

import java.io.File;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BlitzPaint extends Application
{

  public static void main(String[] args)
  {
    Application.launch(BlitzPaint.class, args);
//    javax.swing.JOptionPane.showConfirmDialog(null, "This is a test msg");
//    System.exit(0);
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
    handleArgs(getParameters(), frame);
    stage.setTitle("### JBlitzPaint ###");
    stage.setScene(new Scene(root, 500, 400));
    stage.show();
  }

  private void handleArgs(Parameters parameters, BlitzPaintFrame frame)
  {
    List<String> args = parameters.getUnnamed();
    if (args.size()==0) { return; }
    if (!new File(args.get(0)).isFile()) { return; }
    if (args.get(0).endsWith(".png")) { frame.loadSaveDirect(false, args.get(0)); }
    if (args.get(0).endsWith(".jpg")) { frame.loadSaveDirect(false, args.get(0)); }
    if (args.get(0).endsWith(".bmp")) { frame.loadSaveDirect(false, args.get(0)); }
  }

}
