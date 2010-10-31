/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package behaviorism.handlers;

import com.sun.opengl.util.awt.TextRenderer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author angus
 */
public class FontId {
  String fontName;
  int fontStyle;
  Float[] fontSize;
  Map<Float, TextRenderer> fontSizeMap = new HashMap<Float, TextRenderer>();
  List<TextRenderer> textRenderers = new ArrayList<TextRenderer>();

  public FontId(String fontName, int fontStyle)
  {
    this.fontName = fontName;
    this.fontStyle = fontStyle;
    this.fontSize = new Float[]{18f, 20f, 32f, 48f, 64f, 72f, 100f, 200f, 300f, 400f, 500f, 600f, 700f, 800f};
  }
  public FontId(String fontName, int fontStyle, Float ... fontSize)
  {
    this.fontName = fontName;
    this.fontStyle = fontStyle;
    this.fontSize = fontSize;
  }

  public boolean checkSize(float size) {
    for (float fs : fontSize) {
     if (fs == size) {
       return true;
     }
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 19 * hash + (this.fontName != null ? this.fontName.hashCode() : 0);
    hash = 19 * hash + this.fontStyle;
    return hash;
  }

  @Override
  public boolean equals(Object o)
  {
    if (o instanceof FontId) {
      FontId other = (FontId) o;
      if (other.fontName.equals(this.fontName) && other.fontStyle == this.fontStyle) {
	return true;
      }
    }
    return false;
  }

  @Override
  public String toString()
  {
    return fontName + "," + fontStyle + "," + Arrays.toString(fontSize);
  }
}
