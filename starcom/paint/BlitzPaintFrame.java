package starcom.paint;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.File;
import java.util.HashMap;

import starcom.paint.tools.ITool;
import starcom.paint.tools.ITool.EventType;
import starcom.system.ClipboardTool;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

public class BlitzPaintFrame
{
  @FXML private Pane pane;
  @FXML private ScrollPane scrollPane;
  @FXML private Button settingsButton;
  @FXML private Button arrowTool;
  private Node lastSelectedToolButton;
  private ClipboardTool clipTool = new ClipboardTool();
  HashMap<String,ITool> tools = new HashMap<String,ITool>();
  boolean isDrag = false;
  ITool currentTool;

  @FXML public void initialize()
  {
    pane.setOnDragDetected(createDrag(EventType.DRAG));
    pane.setOnMousePressed(createDrag(EventType.CLICK));
    pane.setOnMouseReleased(createDrag(EventType.RELEASE));
    pane.setOnMouseDragged(createDrag(EventType.MOVE));
    pane.setStyle("-fx-border-color: black");
    loadSaveClip(false);
    selectTool("ArrowTool");
    lastSelectedToolButton = arrowTool;
    changeButtonActive(arrowTool, true);
    clipChildren();
  }
  
  private void clipChildren()
  {
    int arc = 3;
    final Rectangle outputClip = new Rectangle();
    outputClip.setArcWidth(arc);
    outputClip.setArcHeight(arc);
    pane.setClip(outputClip);
    pane.layoutBoundsProperty().addListener((ov, oldValue, newValue) ->
    {
      outputClip.setWidth(newValue.getWidth());
      outputClip.setHeight(newValue.getHeight());
    });
  }
  
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
      changeButtonActive(lastSelectedToolButton, false);
      lastSelectedToolButton = sourceN;
      changeButtonActive(lastSelectedToolButton, true);
      String id = sourceN.getId();
      id = id.substring(0,1).toUpperCase() + id.substring(1);
      currentTool = tools.get(id);
      if (currentTool == null)
      {
        selectTool(id);
      }
      System.out.println("Tool selected: " + sourceN.getId());
    }
    else
    {
      throw new IllegalStateException("Selected from unknown source: " + source);
    }
  }
  
  @FXML void selectAction(ActionEvent event)
  {
    Object source = event.getSource();
    if (source instanceof Node)
    {
      Node sourceN = (Node) source;
      if (sourceN.getId().equals("saveBut"))
      {
        loadSave(sourceN, true);
      }
      else if (sourceN.getId().equals("loadBut"))
      {
        loadSave(sourceN, false);
      }
    }
    else
    {
      throw new IllegalStateException("Selected from unknown source: " + source);
    }
  }
  
  void loadSave(Node sourceN, boolean do_save)
  {
    ContextMenu contextMenu = new ContextMenu();
    if (do_save)
    {
      MenuItem m_clip = new MenuItem("To Clip");
      MenuItem m_file = new MenuItem("To File");
      m_file.setOnAction((event) -> loadSaveFile(true));
      m_clip.setOnAction((event) -> loadSaveClip(true));
      contextMenu.getItems().addAll(m_clip, m_file);
    }
    else
    {
      MenuItem m_clip = new MenuItem("From Clip");
      MenuItem m_file = new MenuItem("From File");
      m_file.setOnAction((event) -> loadSaveFile(false));
      m_clip.setOnAction((event) -> loadSaveClip(false));
      contextMenu.getItems().addAll(m_clip, m_file);
    }
    Point pos = MouseInfo.getPointerInfo().getLocation();
    contextMenu.show(sourceN, pos.x, pos.y);
  }
  
  void loadSaveClip(boolean do_save)
  {
    if (do_save)
    {
      WritableImage image = new WritableImage((int)pane.getWidth(), (int)pane.getHeight());
      pane.snapshot(null, image);
      clipTool.putImageToClipboard(image);
    }
    else
    {
      Image contentPix = clipTool.getImageFromClipboard();
      if (contentPix==null) { contentPix = new WritableImage(400,200); }
      pane.setMaxSize(contentPix.getWidth(), contentPix.getHeight());
      pane.getChildren().add(createImageView(contentPix));
    }
  }
  
  void loadSaveFile(boolean do_save)
  {
    FileChooser fileChooser = new FileChooser();
    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.bmp");
    fileChooser.getExtensionFilters().add(extFilter);
   
    //Show save file dialog
    File file = null;
    if (do_save)
    {
      fileChooser.setTitle("Save to file");
      file = fileChooser.showSaveDialog(null);
    }
    else
    {
      fileChooser.setTitle("Load from file");
      file = fileChooser.showOpenDialog(null);
    }
    if(file != null)
    {
      loadSaveFileDirect(do_save, file.getPath());
    }
  }
  
  void loadSaveFileDirect(boolean do_save, String file)
  {
    if (do_save)
    {
      WritableImage image = new WritableImage((int)pane.getWidth(), (int)pane.getHeight());
      pane.snapshot(null, image);
      starcom.pix.Saver.save(SwingFXUtils.fromFXImage(image,null), file);
    }
    else
    {
      Image contentPix = new Image("file:" + file);
      pane.setMaxSize(contentPix.getWidth(), contentPix.getHeight());
      pane.getChildren().clear();
      pane.getChildren().add(createImageView(contentPix));
    }
  }

  @FXML void selectSet(ActionEvent event)
  {
    System.out.println("Selected settings for: " + currentTool.toString());
    Color color = (Color)settingsButton.getBackground().getFills().get(0).getFill();
    changeButtonActive(settingsButton, color != Color.YELLOWGREEN);
  }
  
  void changeButtonActive(Node button, boolean b_active)
  {
    if (b_active)
    {
      button.setStyle("-fx-background-color: yellowgreen");
    }
    else
    {
      button.setStyle(null);
    }
  }
  
  private void selectTool(String id)
  {
    System.out.println("Selected tool: " + id);
    String toolClass = ITool.class.getPackage().getName() + "." + id;
    try
    {
      currentTool = (ITool) Class.forName(toolClass).newInstance();
    }
    catch (Exception e) { throw new IllegalArgumentException(e); }
    currentTool.init(pane);
    tools.put(id, currentTool);
  }

  private ImageView createImageView(Image image)
  {
    ImageView iv = new ImageView();
    iv.setImage(image);
    return iv;
  }
}
