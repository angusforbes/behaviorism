/* MyImageIcon.java ~ Oct 24, 2008 */

package utils;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 *
 * @author angus
 */
class MyImageIcon extends ImageIcon 
  {
    protected final MediaTracker tracker = null;
    protected final Component component = null;
    public MyImageIcon(URL url)
    {
      super(url);
    }
    public MyImageIcon(Image img)
    {
      super(img);
    }
  }
  