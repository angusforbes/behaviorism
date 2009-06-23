/* FontHandler.java (created on March 7, 2008, 1:46 AM) */
package handlers;

import com.sun.opengl.util.j2d.TextRenderer;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import utils.FileUtils;

/**
 * FontHandler determines the available fonts (stored in data/fonts) and handles
 * loading the font family at various LOD (level-of-detail) into JOGL TextRenderer objects 
 * @author angus
 */
public class FontHandler
{

  boolean USE_VERTEX_ARRAYS = false;
  boolean USE_SMOOTHING = true;
  boolean USE_MIPMAPS = false;
  boolean USE_FRACTIONAL_METRICS = false;
  public Map<String, List<TextRenderer>> fontFamilyMap = new ConcurrentHashMap<String, List<TextRenderer>>();
  public List<TextRenderer> defaultFontFamily = null;
  //public List<String> fontNames = new ArrayList<String>();
  //public int fontIndex = 0;
  /** 
  textRenderers is a List of currently active TextRenderers, each one assigned
  to a particular size within a specific family. That is,
  it contains ONE family at various size. When the font family is changed,
  This list is cleared and re-filled. In the future we will probably want
  to do something more intelligent so that we can have more than one font
  loaded and used at the same time! 
   */
  public List<TextRenderer> textRenderers = new CopyOnWriteArrayList<TextRenderer>();
  public AtomicBoolean changeFonts = new AtomicBoolean(true);
  public AtomicBoolean fontsReady = new AtomicBoolean(false);
  /**
   * The name of the default font to use during when the application first starts up. This is set
   * programmatically in Main if the parameter "font.defaultFont" exists in attributes.properties.
   */
  public String defaultFont = "Arial";
  public int defaultStyle = Font.PLAIN;

  /**
   * Singleton instance of FontHandler. The only way to use this class is via the static getInstance() method.
   */
  private static FontHandler instance = null;

  /**
   * Gets the singleton FontHandler object.
   * @return the singleton FontHandler
   */
  public static FontHandler getInstance()
  {
    if (instance != null)
    {
      return instance;
    }

    instance = new FontHandler();

    return instance;
  }


  private FontHandler()
  {}

  /**
   * Determine which fonts are available and place them in an unmodifiable list.
   */
  /*
  public void determineFonts()
  {
    List<String> names = new ArrayList<String>();
    File dir = new File("data" + File.separator + "fonts" + File.separator);

    FilenameFilter filter = new FilenameFilter()
    {

      @Override
      public boolean accept(File dir, String name)
      {
        if ((!name.endsWith("ttf") && !name.endsWith("TTF")) || name.startsWith("."))
        {
          return false;
        }
        return true;
      }
    };
    String children[] = dir.list(filter);

    if (children != null)
    {
      Arrays.sort(children);

      for (int i = 0; i < children.length; i++)
      {
        names.add(children[i]);
      //System.out.println("font : " + children[i]);
      }
    }
    else
    {
      System.out.println("error can't get fonts!");
    //error! couldn't get files from directory
    }

    fontNames = Collections.unmodifiableList(names);


    //set default font if specified
    int idx = fontNames.indexOf(defaultFont);
    if (idx >= 0)
    {
      System.out.println("FontHandler : using default font \"" + defaultFont + "\"");
      fontIndex = idx;
    }
    else
    {
      System.out.println("FontHandler : default font \"" + defaultFont + "\" not available");
      fontIndex = 0;
    }
  }
  */

  public synchronized TextRenderer getLargestTextRenderer()
  {
    return textRenderers.get(textRenderers.size() - 1);
  }

  /**
   * Gets or creates the specified font. First looks to see if the font family is loaded. If not,
   * the font family is loaded. Then looks to see if our specified size is available. If not,
   * create it and add it to the fontFamily. If for some reason we can't load the font family, then
   * use the default font family.
   * @param fontName
   * @param fontStyle
   * @param fontSize
   * @return A TextRenderer representing the specfied font.
   */
  public synchronized TextRenderer getFont(String fontName, int fontStyle, float fontSize)
  {
    List<TextRenderer> renderers = getFontFamily(fontName, fontStyle);


    /*
    if (renderers == null)
    {
      System.out.println("in getFont() : couldn't find font <" + fontName + "," + fontStyle + ">, using default font");
      renderers = textRenderers;
    }
    */

    for (TextRenderer renderer : renderers)
    {
      if (renderer.getFont().getSize() == fontSize)
      {
        return renderer;
      }
    }

   System.out.println("in getFont() : couldn't find size <" + fontSize + ">, creating it...");

    String familyId = fontName + "," + fontStyle;
    List<TextRenderer> test = fontFamilyMap.get(familyId);
    TextRenderer renderer;

    if (test == null)
    {
      System.out.println("in getFont() : we weren't able to load this font. using default.");
      renderer = getDefaultFont(fontSize);
    }
    else
    {
      Font derive = new Font(fontName, fontStyle, 800);
      System.out.println("in getFont() : derive font = " + derive);
      renderer = createTextRenderer(derive, fontStyle, fontSize);
    }

    renderers.add(renderer);
    return renderer;

  }

  public void setDefaultFont(String font, int fontStyle)
  {
    this.defaultFont = font;
    this.defaultStyle = fontStyle;

    defaultFontFamily = getFontFamily(this.defaultFont, this.defaultStyle);
  }

  public TextRenderer getDefaultFont(float fontSize)
  {
    if (defaultFontFamily == null)
    {
      defaultFontFamily = getFontFamily(this.defaultFont, this.defaultStyle);
    }

    for (TextRenderer renderer : defaultFontFamily)
    {
      if (renderer.getFont().getSize() == fontSize)
      {
        return renderer;
      }
    }

    System.out.println("couldn't find size <" + fontSize + ">, creating it...");
    Font derive = new Font(defaultFont, defaultStyle, 800);

    TextRenderer renderer = createTextRenderer(derive, defaultStyle, fontSize);
    defaultFontFamily.add(renderer);

    return renderer;
  }

  public List<TextRenderer> getDefaultFontFamily()
  {
    if (defaultFontFamily == null)
    {
      defaultFontFamily = getFontFamily(this.defaultFont, this.defaultStyle);
    }

    return defaultFontFamily;
  }


  /**
   * Creates a single TextRenderer for a specified font and style with a particular size.
   * @param font
   * @param fontStyle
   * @param fontSize
   * @return A new TextRenderer for the specified font with a specified style at a specified size.
   */
  private synchronized TextRenderer createTextRenderer(Font font, int fontStyle, float fontSize)
  {
    boolean useAntialias = true;

    if (fontSize < 18f) //then don't anti-alias!
    {
      useAntialias = false;
    }

    TextRenderer renderer = new TextRenderer(
      font.deriveFont(fontStyle, fontSize),
      useAntialias, USE_FRACTIONAL_METRICS, null, USE_MIPMAPS);
    renderer.setSmoothing(USE_SMOOTHING);
    renderer.setUseVertexArrays(USE_VERTEX_ARRAYS);

    return renderer;
  }

  /**
   * Gets the specified font family. This method tries to find 
   * the font family in the following manner. First, by searching the fontFamilyMap
   * to see if this font has already been loaded. If not, it looks to see if this font family
   * is available natively on the system and loads it. If not, it looks in the
   * resources folder within behaviorism.jar and loads it (TO DO!). If not, it looks in the data/fonts
   * directory and loads it. If all of these fail, then we are unable to create the
   * font family and instead return the default font family (TO DO!).
   * @param fontName
   * @param fontStyle
   * @return A List of TextRenderers representing the specified font family at various sizes.
   */
  public synchronized List<TextRenderer> getFontFamily(String fontName, int fontStyle)
  {
    String familyId = fontName + "," + fontStyle;

    List<TextRenderer> familyTextRenderers = fontFamilyMap.get(familyId);

    if (familyTextRenderers != null) //we found it in out hash...
    {
      //System.out.println("in findFontFamily : we found the font family :" + familyId);
      return familyTextRenderers;
    }
    else // we haven't seen this family yet...
    {
      System.out.println("in getFontFamily : we have not yet loaded this font family...");
      //try to load in native font from the system...

      Font derive = new Font(fontName, fontStyle, 800);
      if (derive.getFamily().equals(fontName))
      {
        familyTextRenderers = createTextRenderersForFamily(derive, fontStyle);
        fontFamilyMap.put(familyId, familyTextRenderers);
        System.out.println("in findFontFamily : we loaded the font family from the system :" + familyId);
        return familyTextRenderers;
      }
      else
      {
        System.out.println("in findFontFamily : we failed to load the font family from the system :" + familyId);

        //try to load from data file...
        if (familyTextRenderers == null) //if unsuccesful...
        {
          String fontPath = "data" + File.separator + "fonts" + File.separator + fontName + ".ttf";
          System.out.println("trying to load font \"" + fontPath + "\"");

          try
          {
            FileInputStream fontStream = new FileInputStream(fontPath);
            derive = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, fontStream);

            familyTextRenderers = createTextRenderersForFamily(derive, fontStyle);
            System.out.println("in findFontFamily : we loaded the font family from a data file :" + familyId);
            System.out.println("familyTextRenderers size = " + familyTextRenderers.size());
            fontFamilyMap.put(familyId, familyTextRenderers);
            return familyTextRenderers;
          }
          catch (IOException e)
          {
            System.out.println("in findFontFamily : we failed to load the font family from a data file :" + familyId);
          }
          catch (FontFormatException e)
          {
            System.out.println("in findFontFamily : we failed to load the font family from a data file :" + familyId);
          }
        }

        if (defaultFontFamily != null)
        {
          System.out.println("in findFontFamily : We couldn't load your font, so returning default!");
          return defaultFontFamily;
        }
        else
        {
          System.out.println("in findFontFamily : We couldn't load your font, and your default" +
            "is broken, so creating a backup default and returning that!");
          return loadBackupFontFamily();
        }
      }
    }

  }

  /**
   * If the user supplied default font does not load, then revert to the Java default font.
   * @return
   */
  public List<TextRenderer> loadBackupFontFamily()
  {
    Font derive = new Font("default", Font.PLAIN, 800);
    List<TextRenderer> backupTextRenderers = createTextRenderersForFamily(derive, Font.PLAIN);
    String familyId = "default,0";
    fontFamilyMap.put(familyId, backupTextRenderers);
    defaultFontFamily = backupTextRenderers;
    defaultFont = "default";
    return backupTextRenderers;
  }

  public List<TextRenderer> createTextRenderersForFamily(Font font, int fontStyle)
  {
    List<TextRenderer> familyTextRenderers = new CopyOnWriteArrayList();

    float fontSizes[] =
    {
      18f, 20f, 32f, 48f, 64f, 72f, 100f, 200f, 300f, 400f, 500f, 600f, 700f, 800f
    //18f, 48f, 100f, 300f, 600f
    };

    for (float fontSize : fontSizes)
    {
      familyTextRenderers.add(createTextRenderer(font, fontStyle, fontSize));
    }

    return familyTextRenderers;
  }

  /** We have removed teh notion of a global font. If we need to recreate this behavior, it should be
   * within a Sequence, or a behavior if for a particular set of Geoms...
   * @param fontIndex
   * @deprecated
   */
  /*
  @Deprecated
  public synchronized void nextFont(int fontIndex)
  {
    //fontIndex = 0;
    //System.out.println("in nextFont()");
    textRenderers.clear();
    this.fontIndex = fontIndex;

    float fontSizes[] =
    {
      18f, 20f, 32f, 48f, 64f, 72f, 100f, 200f, 300f, 400f, 500f, 600f, 700f, 800f
    };

    //float fontSizes[] = {10f, 12f, 14f, 16f, 18f, 20f, 32f, 48f, 64f, 72f, 100f, 200f, 300f, 400f, 500f, 600f, 700f, 800f};
    //float fontSizes[] = {18f, 20f, 32f, 48f, 64f, 72f, 100f, 150f, 200f, 250f, 300f, 350f, 400f, 450f, 500f, 600f, 700f, 800f};
    //float fontSizes[] = {400f,500f};

    Font derive = null;
    if (fontNames.size() == 0) //no fonts in data directory!
    {
      derive = new Font("default", Font.PLAIN, 100);
    }
    else //load font from file
    {
      String fontPath = FileUtils.toCrossPlatformFilename("data/fonts/" + fontNames.get(fontIndex));
      System.out.println("try to load font : " + fontPath);

      try
      {
        FileInputStream fontStream = new FileInputStream(fontPath);
        derive = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, fontStream);
        System.out.println("successfully loaded font " + fontNames.get(fontIndex));
      }
      catch (IOException e)
      {
        System.out.println("error! couldn't load font " + fontNames.get(fontIndex) + "! using default one...");
      }
      catch (FontFormatException e)
      {
        System.out.println("error! couldn't load font " + fontNames.get(fontIndex) + "! using default one...");
      }
    }

    for (float fontSize : fontSizes)
    {
      textRenderers.add(createTextRenderer(derive, Font.PLAIN, fontSize));
    }



    fontsReady.set(true);
    changeFonts.set(false);

    //System.out.println("out nextFont()");
  }
   */
  /*
  for (FontInfo finfo : Main.fonts)
  {
  finfo.textRenderer = new TextRenderer(finfo.font, true, false, null, false);
  //finfo.textRenderer = new TextRenderer(finfo.font, true, false, 
  new CustomRenderDelegate(52, 10, Color.BLUE, Color.CYAN), false);
  //using MipMaps (the last argument) is really slow!
  }
   */
  /*
  private static final Color DROP_SHADOW_COLOR = new Color(0, 0, 0, 0.5f);
  class CustomRenderDelegate implements TextRenderer.RenderDelegate {
  private float gradientSize;
  private int dropShadowDepth;
  private Color color1;
  private Color color2;
  
  
  CustomRenderDelegate(float gradientSize, int dropShadowDepth, Color color1, Color color2) {
  this.gradientSize = gradientSize;
  this.dropShadowDepth = dropShadowDepth;
  this.color1 = color1;
  this.color2 = color2;
  }
  
  public boolean intensityOnly() {
  return false;
  }
  
  public Rectangle2D getBounds(String str,
  Font font,
  FontRenderContext frc) {
  GlyphVector gv = font.createGlyphVector(frc, str);
  Rectangle2D stringBounds = gv.getPixelBounds(frc, 0, 0);
  return new Rectangle2D.Double(stringBounds.getX(),
  stringBounds.getY(),
  //stringBounds.getWidth() + dropShadowDepth,
  //stringBounds.getHeight() + dropShadowDepth);
  stringBounds.getWidth(),
  stringBounds.getHeight());
  }
  
  
  public void draw(Graphics2D graphics, String str, int x, int y) {
  graphics.setColor(new Color(1f, 1f, 1f, 1f));
  graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_ATOP));
  
  //graphics.drawString(str, x + dropShadowDepth, y + dropShadowDepth);
  //graphics.setColor(Color.WHITE);
  Font f = graphics.getFont();       
  Rectangle2D r2d = getBounds(str, f, graphics.getFontMetrics().getFontRenderContext()); 
  System.out.println("r2d = " + r2d);
  graphics.fill(r2d);
  
  graphics.setPaint(new GradientPaint(x, y, color1,
  x, y + gradientSize / 2, color2,
  true));
  
  graphics.drawString(str, x, y);
  }
  }
   */
}
