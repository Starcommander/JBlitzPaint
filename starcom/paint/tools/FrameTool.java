package starcom.paint.tools;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class FrameTool implements ITool
{
  static int tail_len = 30;
  static int line_thick = 5;
  static double opacity = 0.8;
  Pane pane;
  Line line[] = new Line[4];
  
  void makeShapeLine(int nr)
  {
    line[nr] = new Line();
    line[nr].setStroke(Color.RED);
    line[nr].setStrokeWidth(line_thick);
    line[nr].setStrokeLineCap(StrokeLineCap.BUTT);
    line[nr].setOpacity(opacity);
    pane.getChildren().add(line[nr]);
  }
  
  void makeShape()
  {
    makeShapeLine(0);
    makeShapeLine(1);
    makeShapeLine(2);
    makeShapeLine(3);
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
      if (line[3]==null) { break EventHandle; }
      double posX = event.getX();
      double posY = event.getY();
      update(line[0].getStartX(),line[0].getStartY(),posX,posY);
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
      line[0] = null;
      line[1] = null;
      line[2] = null;
      line[3] = null;
    }
  }

  private void update(double x1, double y1, double x2, double y2)
  {
    updateLine(0, x1, y1, x2, y1);
    updateLine(1, x2, y2, x1, y2);
    updateLine(2, x1, y1, x1, y2);
    updateLine(3, x2, y2, x2, y1);
  }
  private void updateLine(int nr, double x1, double y1, double x2, double y2)
  {
    /* Line start end. */
    line[nr].setStartX(x1);
    line[nr].setStartY(y1);
    line[nr].setEndX(x2);
    line[nr].setEndY(y2);
  }
}
