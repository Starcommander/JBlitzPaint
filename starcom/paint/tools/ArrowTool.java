package starcom.paint.tools;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;

public class ArrowTool implements ITool
{
  static int tail_len = 30;
  static int line_thick = 10;
  static double opacity = 0.8;
  Pane pane;
  Line line;
  Polygon polygon;
  
  void makeShape()
  {
    line = new Line();
    line.setStroke(Color.RED);
    line.setStrokeWidth(line_thick);
    line.setStrokeLineCap(StrokeLineCap.BUTT);
    line.setOpacity(opacity);
    pane.getChildren().add(line);
    
    polygon = new Polygon();
    polygon.getPoints().addAll(new Double[]{
        0.0, 0.0,
        0.0 - line_thick, 0.0 - tail_len,
        0.0 + line_thick, 0.0 - tail_len });
    polygon.setStroke(Color.RED.darker());
    polygon.setFill(Color.RED);
    polygon.setOpacity(opacity);
    pane.getChildren().add(polygon);
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
      if (line==null) { break EventHandle; }
      double posX = event.getX();
      double posY = event.getY();
      update(line.getStartX(),line.getStartY(),posX,posY);
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
      line = null;
      polygon = null;
    }
  }

  private void update(double x1, double y1, double x2, double y2)
  {
    /* Line start end. */
    line.setStartX(x1);
    line.setStartY(y1);
    line.setEndX(x2);
    line.setEndY(y2);
    
    /* Shape rotate move. */
    double a = line.getEndX() - line.getStartX();
    double b = line.getEndY() - line.getStartY();
    if (a==0) { a = 1; }
    if (b==0) { b = 1; }
    double alpha = starcom.math.Winkel.getAlpha(b, a) + 90.0;
    if (a>0) { alpha += 180.0; }
    polygon.getTransforms().clear();
    polygon.getTransforms().add(new Rotate(alpha, 0.0, 0.0, 0.0, Rotate.Z_AXIS));
    polygon.setTranslateX(line.getEndX());
    polygon.setTranslateY(line.getEndY());

    /* Line reduce end. */
    double c = starcom.math.Winkel.getC(a, b);
    if (c<1) { c=1; }
    double mult = tail_len/c;
    a = a * mult;
    b = b * mult;
    line.setEndX(line.getEndX() - a);
    line.setEndY(line.getEndY() - b);
  }
}
