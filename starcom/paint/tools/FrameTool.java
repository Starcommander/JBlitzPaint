package starcom.paint.tools;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class FrameTool implements ITool
{
  static int line_thick = 5;
  static double opacity = 0.8;
  Pane pane;
  Rectangle r;

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
    r.setFill(null);
    r.setStroke(Color.RED);
    pane.getChildren().add(r);
  }
  
  void makeShape()
  {
    makeRect();
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
      makeShape();
      double posX = event.getX();
      double posY = event.getY();
      update(posX,posY,posX,posY);
    }
    else if (evType == EventType.RELEASE)
    {
      r = null;
    }
  }

  private void update(double x1, double y1, double x2, double y2)
  {
    updateRect(x1, y1, x2, y2);
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
}
