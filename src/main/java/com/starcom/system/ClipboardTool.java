package starcom.system;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.jnlp.ClipboardService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import starcom.debug.LoggingSystem;

public class ClipboardTool
{
  private IClipboardService cs;

  /** Get the ClipboardTool from jnlp or awt. **/
  public ClipboardTool()
  {
    if (starcom.system.ClassPath.isClassAvailable("javax.jnlp.ClipboardService"))
    {
      cs = JnlpClipboardService.getClipboardService();
    }
    if (cs == null)
    {
      cs = new AwtClipboardService();
    }
  }
  
  public boolean putImageToClipboard(Image image)
  {
    if (cs == null) { return false; }
    ImageSelection imageSel = new ImageSelection(image);
    cs.setContents(imageSel);
    return true;
  }
  
  public Image getImageFromClipboard()
  {
    Transferable transferable = cs.getContents();
    if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor))
    {
      try
      {
        Object imageType = transferable.getTransferData(DataFlavor.imageFlavor);
        if (imageType instanceof Image) { return (Image)imageType; }
        if (imageType instanceof BufferedImage) { return javafx.embed.swing.SwingFXUtils.toFXImage((BufferedImage)imageType, null); }
        LoggingSystem.warn(ClipboardTool.class, "Clipboard image type not known.");
        return null; 
      }
      catch (Exception e)
      {
        e.printStackTrace();
        LoggingSystem.severe(ClipboardTool.class, e, "Clipboard error.");
      }
    }
    else
    {
      LoggingSystem.info(ClipboardTool.class, "Clipboard does not contain an image.");
    }
    return null;
  }

  static class ImageSelection implements Transferable
  {
    private java.awt.Image awtImage;

    public ImageSelection(Image image)
    {
      this.awtImage = SwingFXUtils.fromFXImage(image, null);
    }
    
    public ImageSelection(java.awt.Image image)
    {
      this.awtImage = image;
    }
    
    @Override
    public DataFlavor[] getTransferDataFlavors()
    {
      return new DataFlavor[] { DataFlavor.imageFlavor };
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
      return DataFlavor.imageFlavor.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
    {
      if (!DataFlavor.imageFlavor.equals(flavor))
      {
        throw new UnsupportedFlavorException(flavor);
      }
      return awtImage;
    }
  }
  
  static interface IClipboardService
  {
    public Transferable getContents();
    public void setContents(Transferable paramTransferable);
  }
  
  static class JnlpClipboardService implements IClipboardService,ClipboardOwner
  {
    private ClipboardService jnlp_cs;
    
    public JnlpClipboardService(ClipboardService jnlp_cs)
    {
      this.jnlp_cs = jnlp_cs;
    }
    
    /** Get the ClipboardService of JNLP.
     *  @return The ClipboardService, or null if no jnlp-clipboard is available. **/
    public static JnlpClipboardService getClipboardService()
    {
      try
      {
        ClipboardService jnlp_cs;
        jnlp_cs = (ClipboardService)ServiceManager.lookup("javax.jnlp.ClipboardService");
        return new JnlpClipboardService(jnlp_cs);
      }
      catch (UnavailableServiceException e)
      {
        return null;
      }
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents)
    {
    }

    @Override
    public Transferable getContents()
    {
      return jnlp_cs.getContents();
    }

    @Override
    public void setContents(Transferable paramTransferable)
    {
      jnlp_cs.setContents(paramTransferable);
    }
    
  }
  
  static class AwtClipboardService implements IClipboardService,ClipboardOwner
  {
    Clipboard clipboard;
    
    public AwtClipboardService()
    {
      clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    }
    
    @Override
    public Transferable getContents()
    {
      return clipboard.getContents(null);
    }

    @Override
    public void setContents(Transferable paramTransferable)
    {
      clipboard.setContents(paramTransferable, this);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents)
    {
    }
    
  }
}
