/* TextRendererHack.java ~ Oct 21, 2008 */

package behaviorism;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.j2d.TextureRenderer;

/**
 * Call TextRenderHack to fix the TextRender problem on the Mac This class should only be used until
 * Apple puts out their fix.
 * 
 * @author Jeff Addison - Southgate Software Ltd. www.southgatesoftware.com
 */
public class TextRenderHack
{
	/**
	 * Call this function in your drawing code to fix the TextRender rendering problem on the Mac
	 * 
	 * @param tr Text Renderer to fix
	 */
	public static void fixIt(TextRenderer tr)
	{
		// Get the OS Name
		String osName = System.getProperty("os.name");

		// Only fix it if it's broke :)
		if (osName != null && osName.toLowerCase().contains("mac"))
		{
			// Call the TextRenderer's private function getBackingStore to get the backingStore
			TextureRenderer backingStore = (TextureRenderer) invokePrivateMethod(tr, "getBackingStore",
					null);

			// If we have a valid backing store, mark the entire thing dirty.
			if (backingStore != null)
			{
				backingStore.markDirty(0, 0, backingStore.getWidth(), backingStore.getHeight());
			}
		}
	}

	/**
	 * Invokes a private method on and Object.
	 * 
	 * @param o Object to call private method on
	 * @param methodName Name of the method to call
	 * @param params Array of parameters to be passed to the function
	 * @return Object that the method called normally returns (Cast to proper type) NOTE: This function
	 *         was found on the Internet. Lost the link so we are unable to give the author the proper
	 *         credit.
	 */
	public static Object invokePrivateMethod(Object o, String methodName, Object[] params)
	{
		// Go and find the private method...
		final Method methods[] = o.getClass().getDeclaredMethods();
		for (int i = 0; i < methods.length; ++i)
		{
			if (methodName.equals(methods[i].getName()))
			{
				try
				{
					methods[i].setAccessible(true);
					return methods[i].invoke(o, params);
				}
				catch (IllegalAccessException ex)
				{
					System.out.println("IllegalAccessException accessing " + methodName);
				}
				catch (InvocationTargetException ite)
				{
					System.out.println("InvocationTargetException accessing " + methodName);
				}
			}
		}
		return null;
	}

}
