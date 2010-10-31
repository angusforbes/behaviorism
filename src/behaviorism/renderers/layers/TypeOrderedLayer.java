/* TypeOrderedLayer.java ~ Aug 24, 2008 */

package behaviorism. renderers.layers;

/**
 * TypeOrderedLayer sorts the Geoms attached to the layer based on their specific class.
 * For instance if we have a List containing a GeomCircle, a GeomImage, and another GeomCricle,
 * then the following code will ensure that both circles are rendered bfore the images:
 * <p>
 * RendererLayer typeOrderedLayer = new TypeOrderedLayer("GeomCricle", "GeomImage");
 * myWorld.addLayer(myOrderedLayerNum, typeOrderedLayer);
 * addGeomToLayer(circle1, myOrderedLayerNum);   
 * addGeomToLayer(image1, myOrderedLayerNum);   
 * addGeomToLayer(circle2, myOrderedLayerNum);
 * <p>
 * whereas:
 * <p>    
 * RendererLayer typeOrderedLayer = new TypeOrderedLayer("GeomImage", "GeomCircle");
 * would render the image before the circles.
 * <p>
 * Note that the classes are specified by Strings that indicate the "simple" name of the Geom.
 * That is, if you have a class in a different package behaviorism.but with the same name 
 * --For example, custompackage behaviorism..GeomImage and geometry.GeomImage-- they both need to be described by
 * their simple (package behaviorism.s-less) name-- eg, "GeomImage"-- and thus will be treated as the same 
 * class for purposes of ordering within the attachedGeom list.
 * <p>
 * If a Geom is attached to this layer which is *not* one of the types associated with this layer,
 * then it will always be sorted at the front of the list, and thus be rendered first, before any of the 
 * Geoms of an associated type.
 * 
 * @author angus
 */
public class TypeOrderedLayer extends RendererLayer
{
  String[] types;
  public TypeOrderedLayer(String ... types)
  {
    this.types = types;
  }

  @Override
  public void sortGeomsInLayer()
  {
    sortGeomsByType(attachedGeoms, types);
  }
}
