/* FrontToBackLayer.java ~ Aug 24, 2008 */

package renderers.layers;

/**
 *
 * @author angus
 */
public class FrontToBackLayer extends RendererLayer
{
  @Override
  public void sortGeomsInLayer()
  {
    sortGeomsByAnchorDepth(attachedGeoms, false);
  }

}
