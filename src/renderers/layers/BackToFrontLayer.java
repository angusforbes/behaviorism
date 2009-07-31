/* BackToFrontLayer.java ~ Aug 24, 2008 */

package renderers.layers;

/**
 * Sorts the geoms attached to this layer from back to front based on the z-buffer taken at the anchor point
 * of each geom.
 * @author angus
 */
public class BackToFrontLayer extends RendererLayer
{
  @Override
  public void sortGeomsInLayer()
  {
    {
      sortGeomsByAnchorDepth(attachedGeoms, true);
    }
  }
}
