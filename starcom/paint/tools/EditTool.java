package starcom.paint.tools;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import starcom.paint.PaintObject;

public class EditTool implements ITool
{
  static int line_thick = 5;
  static double opacity = 0.8;
  Pane pane;
  double lastPosX = 0.0;
  double lastPosY = 0.0;
  Node curGizmo;
  PaintObject curObj;
  
  @Override
  public void init(Pane pane)
  {
    this.pane = pane;
  }

  @Override
  public void handle(EventType evType, MouseEvent event)
  {
    if (evType == EventType.MOVE)
    {
      if (curGizmo == null) { return; }
      if (curObj == null) { return; }
      double posX = event.getX() - lastPosX;
      double posY = event.getY() - lastPosY;
      curObj.moveGizmo(curGizmo, posX, posY);
    }
    else if (evType == EventType.CLICK)
    {
      PaintObject oldObj = curObj;
      double posX = event.getX();
      double posY = event.getY();
      findCursorIntersection(posX, posY);
      if (curObj != null && curGizmo == null)
      {
        curObj.setGizmoActive(pane, true);
      }
      else if (curObj != null)
      {
      }
      else if (oldObj != null)
      {
        oldObj.setGizmoActive(pane, false);
      }
    }
    else if (evType == EventType.RELEASE)
    {
      if (curGizmo!=null)
      {
        curObj.updateGizmoPositions();
      }
    }
  }
  
  /** Set curObj on intersection, and curGizmo on intersectionGizmo. **/
  private void findCursorIntersection(double posX, double posY)
  {
    curGizmo = null;
    curObj = null;
    Circle mousePoint = new Circle(posX, posY, 1);
    pane.getChildren().add(mousePoint);
    for (Node child : pane.getChildren())
    {
      if (!(child instanceof Shape)) { continue; }
      Shape s = Shape.intersect((Shape)child, mousePoint);
      if (s.getBoundsInLocal().getWidth() != -1)
      {
        onIntersection(child);
        if (curGizmo!=null) { break; }
      }
    }
    pane.getChildren().remove(mousePoint);
    if (curObj==null)
    {
      for (Node child : pane.getChildren())
      {
        if (!(child instanceof Shape)) { continue; }
        if (child.intersects(posX, posY, 1, 1))
        {
          onIntersection(child);
          if (curObj!=null) { break; }
        }
      }
    }
  }

  private void onIntersection(Node child)
  {
    System.out.println("Selected: " + child);
    PaintObject obj = PaintObject.findObjectOfGizmo(child);
    if (obj!=null)
    {
      curGizmo = child;
      curObj = obj;
    }
    obj = PaintObject.findObjectOf(child);
    if (obj!=null)
    {
      curObj = obj;
    }
  }

  @Override
  public void onSelected()
  {
  }

}
