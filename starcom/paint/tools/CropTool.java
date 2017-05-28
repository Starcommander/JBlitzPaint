package starcom.paint.tools;

import java.util.ArrayList;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import starcom.paint.BlitzPaintFrame;
import starcom.paint.PaintObject;

public class CropTool extends CropToolAbstract
{
  @Override
  void onActivateGizmo(PaintObject headlessPaintObject)
  {
    headlessPaintObject.setGizmoActive(pane, true);
  }

  @Override
  void onUpdateGizmoInitPositions(ArrayList<Node> gizmoList)
  {
    double width4 = pane.getWidth() * 0.25;
    double height4 = pane.getHeight() * 0.25;
    
    Rectangle r = (Rectangle)gizmoList.get(POS_BORDER_T);
    updateRect(r, 0.0, 0.0, pane.getWidth(), height4);
    r = (Rectangle)gizmoList.get(POS_BORDER_B);
    updateRect(r, 0.0, height4 * 3.0, pane.getWidth(), height4);
    r = (Rectangle)gizmoList.get(POS_BORDER_L);
    updateRect(r, 0.0, height4, width4, height4 * 2.0);
    r = (Rectangle)gizmoList.get(POS_BORDER_R);
    updateRect(r, width4 * 3.0, height4, width4, height4 * 2.0);
    
    Circle c = (Circle)gizmoList.get(POS_GIZMO_NW);
    c.setCenterX(width4);
    c.setCenterY(height4);
    c = (Circle)gizmoList.get(POS_GIZMO_C);
    c.setCenterX(pane.getWidth() * 0.5);
    c.setCenterY(pane.getHeight() * 0.5);
    c = (Circle)gizmoList.get(POS_GIZMO_SO);
    c.setCenterX(width4 * 3.0);
    c.setCenterY(height4 * 3.0);
  }

  @Override
  void updateFinish(double x, double y, double w, double h)
  {
    headlessPaintObject.setGizmoActive(pane, false);
    SnapshotParameters p = new SnapshotParameters();
    p.setViewport(new Rectangle2D(x + pane.getLayoutX(), y + pane.getLayoutY(), w, h));
    WritableImage contentPix = new WritableImage((int)w, (int)h);
    pane.snapshot(p, contentPix);
    pane.setMaxSize(contentPix.getWidth(), contentPix.getHeight());
    pane.getChildren().clear();
    pane.getChildren().add(BlitzPaintFrame.createImageView(contentPix));
  }

  @Override
  void moveGizmos(ArrayList<Node> tmpGizmoList2, double x, double y, double w, double h)
  {
    moveCircle(((Circle)tmpGizmoList.get(POS_GIZMO_NW)), x, y);
    moveCircle(((Circle)tmpGizmoList.get(POS_GIZMO_C)), x + (w * 0.5), y + (h * 0.5));
    moveCircle(((Circle)tmpGizmoList.get(POS_GIZMO_SO)), x + w, y + h);
  }

  @Override
  void appendGizmoCircles(ArrayList<Node> gizmoList)
  {
    gizmoList.add(PaintObject.createGizmoCircle(GIZMO_C));
    gizmoList.add(PaintObject.createGizmoCircle(GIZMO_NW));
    gizmoList.add(PaintObject.createGizmoCircle(GIZMO_SO));
  }
  
}
