package starcom.paint.tools;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
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
      double posX = event.getX();
      double posY = event.getY();
      curGizmo = null;
      curObj = null;
      PaintObject objOfNode = null;
      for (Node child : pane.getChildren())
      {
        if (child instanceof ImageView) { continue; }
        if (child.intersects(posX, posY, 1, 1))
        {
          System.out.println("Selected: " + child);
          PaintObject obj = PaintObject.findObjectOfGizmo(child);
          if (obj!=null)
          {
            curGizmo = child;
            curObj = obj;
            break;
          }
          obj = PaintObject.findObjectOf(child);
          if (obj!=null)
          {
            objOfNode = obj;
          }
        }
      }
      if (objOfNode != null && curGizmo == null)
      {
        objOfNode.setGizmoActive(pane, true);
      }
    }
    else if (evType == EventType.RELEASE)
    {
      if (curObj!=null)
      {
        curObj.updateGizmoPositions();
      }
    }
  }

}
