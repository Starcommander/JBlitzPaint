package com.starcom.paint;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.starcom.paint.tools.ITool;
import com.starcom.paint.tools.ITool.EventType;
import com.starcom.paint.tools.SizeTool;
import com.starcom.system.ClipboardTool;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Popup;

public class BlitzPaintFrame
{
  enum MenuType {Load, Save, Settings};
  enum ToolBarType {Canvas, Paint};
  public static String ARROW_TOOL = "ArrowTool";
  public static String CROP_TOOL = "CropTool";
  public static Color color = Color.RED;
  @FXML private Pane pane;
  @FXML private ScrollPane scrollPane;
  @FXML private Button settingsButton;
  @FXML private Button arrowTool;
  @FXML private Button cropTool;
  @FXML private GridPane toolbar_paint;
  @FXML private GridPane toolbar_canvas;
  private ContextMenu contextMenu;
  private Node lastSelectedToolButton;
  ArrayList<CheckMenuItem> toolBarList;
  private ClipboardTool clipTool = new ClipboardTool();
  HashMap<String,ITool> tools = new HashMap<String,ITool>();
  boolean isDrag = false;
  ITool currentTool;

  @FXML public void initialize()
  {
    toolbar_paint.managedProperty().bind(toolbar_paint.visibleProperty());
    toolbar_canvas.managedProperty().bind(toolbar_canvas.visibleProperty());
    toolbar_canvas.setVisible(false);
//    pane.setOnMouseMoved((ev) -> currentTool.handle(EventType.MOVE, ev)); // Not used yet!
    pane.setOnMousePressed((ev) -> currentTool.handle(EventType.CLICK, ev));
    pane.setOnMouseReleased((ev) -> currentTool.handle(EventType.RELEASE, ev));
    pane.setOnMouseDragged((ev) -> currentTool.handle(EventType.DRAG, ev));
    scrollPane.setOnKeyPressed((ev) -> onKey(ev));
    pane.setStyle("-fx-border-color: black");
  }
  
  public void onShowPre()
  {
    if (BlitzPaint.fullShot==null)
    {
      openEmptyPix(pane, 400, 200);
      selectTool(ARROW_TOOL);
      lastSelectedToolButton = arrowTool;
      changeButtonActive(arrowTool, true);
    }
    else
    {
      Image pix = SizeTool.getScaledInstance(BlitzPaint.fullShot, 400, 0, true);
      openPix(pane, pix);
    }
    clipChildren();
  }
  
  public void onShowPost()
  {
    if (BlitzPaint.fullShot!=null)
    {
      selectTool(CROP_TOOL);
      lastSelectedToolButton = cropTool;
      changeButtonActive(cropTool, true);
    }
  }
  
  private void onKey(KeyEvent ev)
  {
    if (ev.getCode() == KeyCode.DELETE)
    {
      PaintObject.clearFocusObject(pane);
    }
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
  
  @FXML void selectTool(ActionEvent event)
  {
    Object source = event.getSource();
    if (source instanceof Node)
    {
      PaintObject.clearGizmos(pane);
      Node sourceN = (Node) source;
      changeButtonActive(lastSelectedToolButton, false);
      lastSelectedToolButton = sourceN;
      changeButtonActive(lastSelectedToolButton, true);
      String id = sourceN.getId();
      id = id.substring(0,1).toUpperCase() + id.substring(1);
      selectTool(id);
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
      PaintObject.clearGizmos(pane);
      Node sourceN = (Node) source;
      if (sourceN.getId().equals("saveBut"))
      {
        showMenu(sourceN, MenuType.Save);
      }
      else if (sourceN.getId().equals("loadBut"))
      {
        showMenu(sourceN, MenuType.Load);
      }
      else // Settings
      {
        showMenu(sourceN, MenuType.Settings);
      }
    }
    else
    {
      throw new IllegalStateException("Selected from unknown source: " + source);
    }
  }
  
  void showMenu(Node sourceN, MenuType type)
  {
    if (contextMenu != null && contextMenu.isShowing())
    {
      contextMenu.hide();
    }
    contextMenu = new ContextMenu();
    if (type == MenuType.Save)
    {
      MenuItem m_clip = new MenuItem("To Clip");
      MenuItem m_file = new MenuItem("To File");
      m_file.setOnAction((event) -> loadSaveFile(true));
      m_clip.setOnAction((event) -> loadSaveClip(true));
      contextMenu.getItems().addAll(m_clip, m_file);
    }
    else if (type == MenuType.Load)
    {
      MenuItem m_clip = new MenuItem("From Clip");
      MenuItem m_file = new MenuItem("From File");
      MenuItem m_empty = new MenuItem("Empty");
      m_file.setOnAction((event) -> loadSaveFile(false));
      m_clip.setOnAction((event) -> loadSaveClip(false));
      m_empty.setOnAction((event) -> openEmptyPix(pane, 400, 200));
      contextMenu.getItems().addAll(m_clip, m_file, m_empty);
    }
    else // Settings
    {
      initToolbarList();
      SeparatorMenuItem sep1 = new SeparatorMenuItem();
      SeparatorMenuItem sep2 = new SeparatorMenuItem();
      MenuItem m_color = new MenuItem("Set color");
      CheckMenuItem toolBar_paint = toolBarList.get(0);
      CheckMenuItem toolBar_canvas =  toolBarList.get(1);
      MenuItem m_about = new MenuItem("About");
      m_color.setOnAction((event) -> selectColor(sourceN));
      m_about.setOnAction((event) -> showAbout(sourceN));
      contextMenu.getItems().addAll(m_color, sep1, toolBar_paint, toolBar_canvas, sep2, m_about);
    }
    Point pos = MouseInfo.getPointerInfo().getLocation();
    contextMenu.show(sourceN, pos.x, pos.y);
  }

  private void initToolbarList()
  {
    if (toolBarList == null)
    {
      toolBarList = new ArrayList<CheckMenuItem>();
      CheckMenuItem toolBar_paint = new CheckMenuItem("ToolBar: Paint");
      toolBar_paint.setSelected(true);
      toolBar_paint.setOnAction((event) -> selectToolbar(toolBar_paint, toolBarList, ToolBarType.Paint));
      CheckMenuItem toolBar_canvas = new CheckMenuItem("ToolBar: Canvas");
      toolBar_canvas.setOnAction((event) -> selectToolbar(toolBar_canvas, toolBarList, ToolBarType.Canvas));
      toolBarList.add(toolBar_paint);
      toolBarList.add(toolBar_canvas);
    }
  }

  private void selectToolbar(CheckMenuItem toolBar, ArrayList<CheckMenuItem> toolBarList, ToolBarType type)
  {
    for (CheckMenuItem curItem : toolBarList) { curItem.setSelected(false); }
    toolBar.setSelected(true);
    if (type == ToolBarType.Paint)
    {
      toolbar_paint.setVisible(true);
      toolbar_canvas.setVisible(false);
    }
    else
    {
      toolbar_paint.setVisible(false);
      toolbar_canvas.setVisible(true);
    }
  }

  private void showAbout(Node sourceN)
  {
    Alert alert = new Alert(AlertType.INFORMATION);
    StringBuilder sb = new StringBuilder();
    sb.append("This software is released under the GPLv3.\n\n");
    sb.append("Author: Paul Kashofer Austria\n");
    sb.append("Web: https://github.com/Starcommander/JBlitzPaint");
    alert.setContentText(sb.toString());
    alert.show();
  }

  private void selectColor(Node sourceN)
  {
    Popup win = new Popup();
    win.setAutoHide(true);
    ColorPicker colorPicker = new ColorPicker(color);
    colorPicker.setOnAction((event) -> color = colorPicker.getValue() );
    win.getContent().add(colorPicker);
    Point pos = MouseInfo.getPointerInfo().getLocation();
    win.show(sourceN, pos.x, pos.y);
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
      PaintObject.clearAllObjects(pane);
      pane.setCursor(Cursor.WAIT);
      Image contentPix = null;
      contentPix = clipTool.getImageFromClipboard();
      if (contentPix==null) { contentPix = new WritableImage(400,200); }
      pane.setCursor(Cursor.DEFAULT);
      openPix(pane, contentPix);
    }
  }
  
  public static void openPix(Pane pane, Image pix)
  {
    pane.getChildren().clear();
    pane.setMaxSize(pix.getWidth(), pix.getHeight());
    pane.getChildren().add(createImageView(pix));
  }
  
  public static void openEmptyPix(Pane pane, int sizeX, int sizeY)
  {
    PaintObject.clearAllObjects(pane);
    WritableImage contentPix = new WritableImage(sizeX, sizeY);
    openPix(pane, contentPix);
  }
  
  void loadSaveFile(boolean do_save)
  {
    FileChooser fileChooser = new FileChooser();
    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files",
        "*.png", "*.jpg", "*.bmp",
        "*.PNG", "*.JPG", "*.BMP");
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
      com.starcom.pix.Saver.save(SwingFXUtils.fromFXImage(image,null), file);
    }
    else
    {
      PaintObject.clearAllObjects(pane);
      Image contentPix = new Image("file:" + file);
      openPix(pane, contentPix);
    }
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
    ITool lastTool = currentTool;
    currentTool = tools.get(id);
    if (lastTool != null) { lastTool.onDeselected(); }
    if (currentTool != null)
    {
      currentTool.onSelected();
      return;
    }
    String toolClass = ITool.class.getPackage().getName() + "." + id;
    try
    {
      currentTool = (ITool) Class.forName(toolClass).newInstance();
    }
    catch (Exception e) { throw new IllegalArgumentException(e); }
    currentTool.init(pane);
    currentTool.onSelected();
    tools.put(id, currentTool);
  }

  public static ImageView createImageView(Image image)
  {
    ImageView iv = new ImageView();
    iv.setImage(image);
    return iv;
  }
}
