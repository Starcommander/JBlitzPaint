package starcom.paint;

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
  }

  @Override
  /** This method is called when initialization is complete **/
  public void init() {}

  @Override
  public void start(Stage stage) throws Exception
  {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BlitzPaint.fxml"));
    Parent root = (Parent)fxmlLoader.load();
    stage.setTitle("### JBlitzPaint ###");
    stage.setScene(new Scene(root, 500, 400));
    stage.show();
  }

}
