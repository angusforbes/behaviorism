/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package behaviors.geom;

import geometry.Geom;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author angus
 */
public interface GeomUpdater 
{
  public List<Geom> attachedGeoms = new CopyOnWriteArrayList<Geom>();

  public void updateGeom(Geom g);
}
