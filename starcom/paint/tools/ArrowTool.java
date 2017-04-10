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
  
//  void makeLine()
//  {
//    line = new Line();
//    line.setStroke(Color.RED);
//    line.setStrokeWidth(line_thick);
//    line.setStrokeLineCap(StrokeLineCap.BUTT);
//    pane.getChildren().add(line);
//  }
  
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
  
//  void finishLine()
//  {
//  polygon = new Polygon();
//  polygon.getPoints().addAll(new Double[]{
//      0.0, 0.0,
//      0.0 - line_thick, 0.0 - tail_len,
//      0.0 + line_thick, 0.0 - tail_len });
//  polygon.setStroke(Color.RED);
//  
//  double a = line.getEndX() - line.getStartX();
//  double b = line.getEndY() - line.getStartY();
//  if (a==0) { a = 1; }
//  if (b==0) { b = 1; }
//  double alpha = starcom.math.Winkel.getAlpha(b, a) + 90.0;
//  if (a>0) { alpha += 180.0; }
//  System.out.println("W: " + alpha);
//  
//  
//  
//
//  
//  
////polygon.rotateProperty().set(alpha);
////polygon.setRotate(alpha);
//polygon.getTransforms().add(new Rotate(alpha, 0.0, 0.0, 0.0, Rotate.Z_AXIS));
//polygon.setTranslateX(line.getEndX());
//polygon.setTranslateY(line.getEndY());
//
//
//double c = starcom.math.Winkel.getC(a, b);
//if (c<1) { c=1; }
//double mult = tail_len/c;
//a = a * mult;
//b = b * mult;
//line.setEndX(line.getEndX() - a);
//line.setEndY(line.getEndY() - b);
//
//
////pane.getChildren().add(polygon);
//line = null;
//polygon = null;
//
//
////    Line line1 = new Line();
////    line1.setEndX(line.getEndX());
////    line1.setEndY(line.getEndY());
////    
////    double a = line.getEndX() - line.getStartX();
////    double b = line.getEndY() - line.getStartY();
////    double c = starcom.math.Winkel.getC(a, b);
////    if (c<1) { c=1; }
////    double mult = TAIL_LEN/c;
////    a = a * mult;
////    b = b * mult;
////    line1.setStartX(line.getEndX() - (a + b*0.5 ));
////    line1.setStartY(line.getEndY() - (b + a*0.5 ));
////    
////
////    pane.getChildren().add(line1);
////    line = null;
//  }
  
//  void finishLineOld()
//  {
//    Line line1 = new Line();
//    line1.setEndX(line.getEndX());
//    line1.setEndY(line.getEndY());
//    double dirX = line.getStartX() - line.getEndX();
//    double dirY = line.getStartY() - line.getEndY();
//    // TO DO: DIV/0 vermeiden, zwei mal!
//    //double ratio = dirX/dirY;
////    dirX = line.getEndX() + (30.0 * ratio) + (5.0 * (1.0/ratio));
////    dirY = line.getEndY() + (30.0 * (1.0/ratio)) + (5.0 * ratio);
//    
////    dirX = line.getEndX() - (30.0 * (1.0/ratio));
////    dirY = line.getEndY() - (30.0 * ratio);
//    
//    double len = dirX + dirY;
//    if (len==0) { len = 10; }
//    dirX = dirX / len;
//    dirY = dirY / len;
//    dirX = line.getEndX() + dirX;
//    dirY = line.getEndY() + dirY;
//    
//    double a = line.getEndX() - line.getStartX();
//    double b = line.getEndY() - line.getStartY();
//    double c = starcom.math.Winkel.getC(a, b);
//    
//    line1.setStartX(dirX);
//    line1.setStartY(dirY);
//    pane.getChildren().add(line1);
//    //TO DO: Pfeil
//    line = null;
//  }

  @Override
  public void init(Pane pane)
  {
    this.pane = pane;
  }

  @Override
  public void handle(EventType evType, MouseEvent event)
  {
//    System.out.println("Event: " + evType);
//    System.out.println("-----> " + event.getSource());
    EventHandle:
    if (evType == EventType.MOVE)
    {
      if (line==null) { break EventHandle; }
      double posX = event.getX();
      double posY = event.getY();
//      line.setEndX(posX);
//      line.setEndY(posY);
      update(line.getStartX(),line.getStartY(),posX,posY);
    }
    else if (evType == EventType.CLICK)
    {
      makeShape();
      double posX = event.getX();
      double posY = event.getY();
      update(posX,posY,posX,posY);
//      line.setStartX(posX);
//      line.setStartY(posY);
//      line.setEndX(posX);
//      line.setEndY(posY);
    }
    else if (evType == EventType.RELEASE)
    {
      //finishLine();
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
