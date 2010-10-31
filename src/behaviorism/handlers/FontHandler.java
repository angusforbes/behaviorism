/* FontHandler.java (created on March 7, 2008, 1:46 AM) */
package behaviorism.handlers;

import behaviorism.utils.Utils;
import com.sun.opengl.util.awt.TextRenderer;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.grlea.log.SimpleLogger;

/**
 * FontHandler determines the available fonts (stored in data/fonts) and handles
 * loading the font family at various LOD (level-of-detail) into JOGL TextRenderer objects 
 * @author angus
 */
public class FontHandler {

  boolean USE_VERTEX_ARRAYS = true; //false;
  boolean USE_SMOOTHING = false; //true;
  boolean USE_MIPMAPS = false;
  boolean USE_FRACTIONAL_METRICS = false;
  float MIN_ALIASED_FONT_SIZE = 10f; //18f;
  private final Set<FontId> fontsToInstall = Collections.synchronizedSet(new HashSet<FontId>());
  public final Set<FontId> fontIds = Collections.synchronizedSet(new HashSet<FontId>());
  public AtomicBoolean isInstallingFonts = new AtomicBoolean(false);
  public final Map<TextRenderer, FontRenderContext> textRendererToFontRendererContext = new HashMap<TextRenderer, FontRenderContext>();

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
  //public List<TextRenderer> textRenderers = new CopyOnWriteArrayList<TextRenderer>();
  //public AtomicBoolean changeFonts = new AtomicBoolean(true);
  //public AtomicBoolean fontsReady = new AtomicBoolean(false);
  /**
   * The name of the default font to use during when the application first starts up. This is set
   * programmatically in Main if the parameter "font.defaultFont" exists in attributes.properties.
   */
  public String defaultFont = "Arial";
  public int defaultStyle = Font.PLAIN;
  //public FontId defaultFontId = new FontId("Georgia", Font.ITALIC);
  /**
   * Singleton instance of FontHandler. The only way to use this class is via the static getInstance() method.
   */
  private static final FontHandler instance = new FontHandler();
  public static final SimpleLogger log = new SimpleLogger(FontHandler.class);

  /**
   * Gets the singleton FontHandler object.
   * @return the singleton FontHandler
   */
  public static FontHandler getInstance() {
    return instance;
  }

  private FontHandler() {
  }

  public FontId getFontId(String fontName, int fontStyle, float fontSize) {

    FontId f = getFontId(fontName, fontStyle);
    if (f != null) {
      if (f.checkSize(fontSize)) {
	return f;
      }
    }

    return null;
  }

  public FontId getFontId(FontId key) {
    return getFontId(key.fontName, key.fontStyle);
  }
  
  public FontId getFontId(String fontName, int fontStyle) {
    System.err.println("in getFontId : " + fontName + ", " + fontStyle);
    for (FontId f : fontIds) {
      System.err.println("in getFontId : checking for " + fontName + " against " + f);
      if (f.fontName.equals(fontName) && f.fontStyle == fontStyle) {
	System.err.println("FOUND!");
	return f;
      }
    }
    System.err.println("not found...");
    return null;
  }

  public List<TextRenderer> getOrCreateTextRenderers(String fontName, int fontStyle) {
    FontId fid = getFontId(fontName, fontStyle);
    if (fid != null)
    {
      return fid.textRenderers;
    } else {
      return createFont(fontName, fontStyle).textRenderers;
    } 
  }

  public TextRenderer getOrCreateTextRenderer(String fontName, int fontStyle, float fontSize) {
    System.err.println("looking for font : " + fontName + ", " + fontStyle + ", " + fontSize);
    
    FontId fid = getFontId(fontName, fontStyle, fontSize);
    if (fid != null)
    {
      return fid.fontSizeMap.get(fontSize);
    } else {
      System.err.println("not found, so creating...");
      FontId newFontId = createFont(fontName, fontStyle, fontSize);
      if (newFontId == null) {
	System.err.println("couldn't create " + fontName + "," + fontStyle + " so return default instead...");
	return getOrCreateTextRenderer(defaultFont, defaultStyle, fontSize);
      } else {
	return newFontId.fontSizeMap.get(fontSize);
      }
    }
  }

  /**
   * @param fontName
   * @param fontStyle
   * @param fontSize
   * @return A TextRenderer representing the specified font.
   */
  public TextRenderer getTextRenderer(String fontName, int fontStyle, float fontSize) {
    FontId fid = getFontId(fontName, fontStyle, fontSize);
    if (fid != null)
    {
      return fid.fontSizeMap.get(fontSize);
    } else {
      return null;
    }
  }

  public List<TextRenderer> getTextRenderers(String fontName, int fontStyle) {
    FontId fid = getFontId(fontName, fontStyle);
    if (fid != null)
    {
      return fid.textRenderers;
    } else {
      return null;
    }
  }

  
  public void setDefaultFont(String font, int fontStyle) {
    this.defaultFont = font;
    this.defaultStyle = fontStyle;
    createFont(new FontId(font, fontStyle));
  }

  public List<TextRenderer> getDefaultTextRenderers() {
    return getTextRenderers(defaultFont, defaultStyle);
  }

  /*
  public TextRenderer getDefaultFont(float fontSize) {
    if (defaultFontFamily == null) {
      //defaultFontFamily = getFontFamily(this.defaultFont, this.defaultStyle);
    }

    for (TextRenderer renderer : defaultFontFamily) {
      if (renderer.getFont().getSize() == fontSize) {
	return renderer;
      }
    }

    return null;
    //return renderer;
  }
   *
   */


  /**
   * Creates a single TextRenderer for a specified font and style with a particular size.
   * @param font
   * @param fontStyle
   * @param fontSize
   * @return A new TextRenderer for the specified font with a specified style at a specified size.
   */
  private synchronized TextRenderer createTextRenderer(Font font, int fontStyle, float fontSize) {
    log.entry("in createTextRenderer()");
    log.debug("font = " + font + ", style = " + fontStyle + ", fontSize = " + fontSize);

    boolean useAntialias = true;

    if (fontSize < MIN_ALIASED_FONT_SIZE) {
      useAntialias = false;
    }

    TextRenderer renderer = new TextRenderer(
	    font.deriveFont(fontStyle, fontSize),
	    useAntialias, USE_FRACTIONAL_METRICS, null, USE_MIPMAPS);
    //renderer.setSmoothing(USE_SMOOTHING);
    renderer.setUseVertexArrays(USE_VERTEX_ARRAYS);

    textRendererToFontRendererContext.put(renderer, renderer.getFontRenderContext()); //do i need this?
    log.exit("out createTextRenderer()");

    return renderer;
  }

  //Install the fonts represented by a FontId, called by installFonts via the OpenGL display loop
  private void installFont(FontId f) {

    Float[] fontSizesToAdd = f.fontSize;

    //System.err.println("in install font, installing " + f);
    Font derive = new Font(f.fontName, f.fontStyle, 800);
    if (!derive.getFamily().equals(f.fontName)) {
      String fontPath = "data" + File.separator + "fonts" + File.separator + f.fontName + ".ttf";
      //System.err.println("in installFont: trying to load font \"" + fontPath + "\"");

      try {
	FileInputStream fontStream = new FileInputStream(fontPath);
	derive = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, fontStream);
      } catch (Exception e) {
	//System.err.println("in installFont : we failed to load the font from a data file :" + f);
	derive = null;
      }
    }

    if (derive != null) {
      FontId alreadyPresent = getFontId(f.fontName, f.fontStyle);
      if (alreadyPresent != null) {
	//System.err.println("in installFont : A font by this name/style (" + f.fontName + "," + f.fontStyle + ") already exists ");
	//System.err.println("and the fontId is : " + alreadyPresent);
	f = alreadyPresent;
      }
      
      for (float fontSize : fontSizesToAdd) {
	if (f.fontSizeMap.get(fontSize) == null) {
	  f.fontSizeMap.put(fontSize, createTextRenderer(derive, f.fontStyle, fontSize));
	} else { 
	  //System.err.println("we've already installed this size " + fontSize);
	}
      }

      //sync up the textRenderers list, the fontSize array, and the fontSize->textRenderer map.
      f.textRenderers = new ArrayList<TextRenderer>(f.fontSizeMap.values());
      FontHandler.sortTextRenderersBySize(f.textRenderers);

      int numSizes = f.fontSizeMap.size();
      f.fontSize = (Float[])f.fontSizeMap.keySet().toArray(new Float[numSizes]);
      Arrays.sort(f.fontSize);

      //if we have never installed any font sizes for this family, add to the global fontIds map
      if (alreadyPresent == null) {
	fontIds.add(f);
      }

    } else {
      System.err.println("in installFont : couldn't install this font " + f + "!");
    }
    
  }
  /**
   * This should only be called from within the Render loop.
   * Using a synchronizedList instead of a CopyOnWriteArrayList because
   * the latter does not seem to work with my removal strategy.
   * May need to rethink if the synchronization causes slowness
   * (after all this gets called every single frame!). But it may not be an issue at all.
   */
 
  public void installFonts() {
    log.entry("in installFonts() : fontsToInstall.size = " + fontsToInstall.size());
    synchronized (fontsToInstall) {
      if (fontsToInstall.size() > 0) {
	Iterator<FontId> i = fontsToInstall.iterator();

	while (i.hasNext()) {
	  FontId f = i.next();
	  installFont(f);
	  i.remove();
	}

	isInstallingFonts.set(false);
      }
    }
    log.exit("out installFonts() : fontsToInstall.size = " + fontsToInstall.size());
  }

  public FontId createFont(String f) {
    return createFont(new FontId(f, Font.PLAIN));
  }

  public FontId createFont(String f, int s) {
    return createFont(new FontId(f, s));
  }

  public FontId createFont(String f, int s, Float ... fs) {
    return createFont(new FontId(f, s, fs));
  }

  public FontId createFont(Font f) {
    return createFont(new FontId(f.getName(), f.getStyle(), (float)f.getSize()));
  }

  public FontId createFont(FontId f) {
    System.err.println("in createFont() : adding fontId " + f);

    synchronized (fontsToInstall) {
      if (fontsToInstall.contains(f)) {
	return getFontId(f);
      }

      isInstallingFonts.set(true);
      this.fontsToInstall.add(f);
    }

    while (isInstallingFonts.get() == true) {
      Utils.sleep(10);
    }

    return getFontId(f);
  }

  public static void sortTextRenderersBySize(List<TextRenderer> list) {
    Collections.sort(list, new Comparator<TextRenderer>() {

      public int compare(TextRenderer a, TextRenderer b) {
	TextRenderer tr1 = a;
	TextRenderer tr2 = b;
	if (tr1.getFont().getSize() > tr2.getFont().getSize()) {
	  return +1;
	} else if (tr1.getFont().getSize() < tr2.getFont().getSize()) {
	  return -1;
	} else {
	  return 0;
	}
      }
    });

//    for (TextRenderer tr : list) {
//      System.out.println("text renderer : " + tr.getFont());
//    }
  }
}
