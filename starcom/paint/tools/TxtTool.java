package starcom.paint.tools;

import java.util.Optional;

import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TxtTool implements ITool
{
  static int line_thick = 4;
  static double opacity = 0.3;
  Pane pane;
  Rectangle r;
  Text t;
  String s_txt;
  
  void makeRect()
  {
    r = new Rectangle();
    r.setX(10);
    r.setY(10);
    r.setWidth(20);
    r.setHeight(20);
    r.setArcWidth(5);
    r.setArcHeight(5);
    r.setOpacity(opacity);
    r.setStrokeWidth(line_thick);
    r.setFill(Color.GRAY);
    r.setStroke(Color.BLACK);
    pane.getChildren().add(r);
  }
  
  void askText()
  {
    TextInputDialog dialog = new TextInputDialog("This is a default text");
    dialog.setTitle("Text");
    dialog.setHeaderText("Enter some Text!");

    Optional<String> result = dialog.showAndWait();
    if (result.isPresent())
    {
      s_txt = result.get();
    }
  }
  
  void makeText()
  {
    DropShadow ds = new DropShadow();
    ds.setOffsetY(3.0f);
    ds.setColor(Color.GRAY);
    t = new Text();
    t.setX(10);
    t.setY(10);
    t.setText(s_txt);
    t.setFont(Font.font ("Verdana", 20));
    t.setEffect(ds);
    t.setFill(Color.RED);
    pane.getChildren().add(t);
    s_txt = null;
  }
  
  void makeShape()
  {
    makeRect();
    makeText();
  }

  @Override
  public void init(Pane pane)
  {
    this.pane = pane;
  }

  @Override
  public void handle(EventType evType, MouseEvent event)
  {
    EventHandle:
    if (evType == EventType.MOVE)
    {
      if (r==null) { break EventHandle; }
      double posX = event.getX();
      double posY = event.getY();
      update(r.getX(),r.getY(),posX,posY);
    }
    else if (evType == EventType.CLICK)
    {
      if (s_txt == null)
      {
        askText();
        break EventHandle;
      }
      makeShape();
      double posX = event.getX();
      double posY = event.getY();
      update(posX,posY,posX,posY);
    }
    else if (evType == EventType.RELEASE)
    {
      r = null;
      t = null;
    }
  }

  private void update(double x1, double y1, double x2, double y2)
  {
    updateRect(x1, y1, x2, y2);
    updateText(x1, y1, x2, y2);
  }

  private void updateRect(double x1, double y1, double x2, double y2)
  {
    r.setX(x1);
    r.setY(y1);
    double w = x2-x1;
    if (w<20) { w = 20; }
    r.setWidth(w);
    double h = y2-y1;
    if (h<20) { h = 20; }
    r.setHeight(h);
  }
  
  private void updateText(double x1, double y1, double x2, double y2)
  {
    t.setX(x1 + 5);
    t.setY(y1 + 20);
    double w = (x2-x1) - 10;
    if (w<20) { w = 20; }
    t.setWrappingWidth(w);
  }
}
