package starcom.paint;

import java.util.HashMap;

import starcom.paint.tools.ITool;
import starcom.paint.tools.ITool.EventType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class BlitzPaintFrame
{
  @FXML private Pane pane;
  @FXML private ScrollPane scrollPane;
  @FXML private Button settingsButton;
  private Background settingsButtonBG;
  HashMap<String,ITool> tools = new HashMap<String,ITool>();
  boolean isDrag = false;
  ITool currentTool;

  @FXML public void initialize()
  {
//    Blend blend = new Blend();
//    blend.setMode(BlendMode.COLOR_BURN);
    pane.setOnDragDetected(createDrag(EventType.DRAG));
    pane.setOnMousePressed(createDrag(EventType.CLICK));
    pane.setOnMouseReleased(createDrag(EventType.RELEASE));
    pane.setOnMouseDragged(createDrag(EventType.MOVE));
    pane.setStyle("-fx-border-color: black");
    pane.getChildren().add(createImageView("starcom/paint/BlitzPaintArrow.png"));
    selectTool("ArrowTool");
  }
  
//  void initializeTmp()
//  {
//    Blend blend = new Blend();
//    blend.setMode(BlendMode.COLOR_BURN);
//    ImageView iv = createImageView("starcom/paint/BlitzPaintBG.png");
//    iv.setOnDragDetected(createDrag(EventType.DRAG));
//    iv.setOnMousePressed(createDrag(EventType.CLICK));
//    iv.setOnMouseReleased(createDrag(EventType.RELEASE));
//    iv.setOnMouseDragged(createDrag(EventType.MOVE));
//    iv.setStyle("-fx-border-color: black");
//    pane.getChildren().add(iv);
//    selectTool("ArrowTool");
//  }
  
  private EventHandler<? super MouseEvent> createDrag(final EventType evType)
  {
    EventHandler<MouseEvent> ev = new EventHandler<MouseEvent>()
    {
      @Override
      public void handle(MouseEvent event)
      {
        currentTool.handle(evType, event);
      }
    };
    return ev;
  }
  
  @FXML void selectTool(ActionEvent event)
  {
    Object source = event.getSource();
    if (source instanceof Node)
    {
      Node sourceN = (Node) source;
      currentTool = tools.get(sourceN.getId());
      if (currentTool == null)
      {
        selectTool(sourceN.getId());
      }
      System.out.println("Tool selected: " + sourceN.getId());
    }
    else
    {
      throw new IllegalStateException("Selected from unknown source: " + source);
    }
  }

  @FXML void selectSet(ActionEvent event)
  {
    System.out.println("Select settings now: " + currentTool.toString());
   // settingsButton.setBackground(Background.)
    
    Color color = (Color)settingsButton.getBackground().getFills().get(0).getFill();
    if (color != Color.YELLOWGREEN)
    {
      settingsButtonBG = settingsButton.getBackground();
      settingsButton.setBackground(new Background(new BackgroundFill(
                Color.YELLOWGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
    }
    else
    {
      settingsButton.setBackground(settingsButtonBG);
    }
  }
  
  private void selectTool(String id)
  {
    String toolClass = ITool.class.getPackage().getName() + "." + id;
    try
    {
      currentTool = (ITool) Class.forName(toolClass).newInstance();
    }
    catch (Exception e) { throw new IllegalArgumentException(e); }
    currentTool.init(pane);
    tools.put(id, currentTool);
  }

  private ImageView createImageView(String file)
  {
    Image image = new Image(file);
    ImageView iv = new ImageView();
    iv.setImage(image);
    return iv;
  }
}
