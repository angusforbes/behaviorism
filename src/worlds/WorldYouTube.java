/* WorldYouTube.java (created on July 31, 2007, 2:50 PM) */
package worlds;

import handlers.YouTubeHandler;
import be.roam.drest.service.youtube.YouTubeVideo;
import geometry.media.GeomRectVideoFobs;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import utils.Utils;

public class WorldYouTube extends WorldGeom
{

	public WorldYouTube()
	{
	}

	public void setUpWorld()
	{
		setCamera();

		printEnvironmentVariables();
	//testYouTubeAPI();
	//testVideoLoading();
	}

	public void printEnvironmentVariables()
	{
		Map<String, String> variables = System.getenv();

		for (Map.Entry<String, String> entry : variables.entrySet())
		{
			String name = entry.getKey();
			String value = entry.getValue();
			System.out.println(name + "=" + value);
		}

		Utils.sleep(1000);

		System.out.println("JLP  =" + System.getProperty("java.library.path"));
		String libraryName = "/Users/angus/Projects/attribute/libs/nativeMacLibs/libfobs4jmf.jnilib";
		//String libraryName = "fobs4jmf";
		//String libraryName = "jogl"; //"fobs4jmf";

		System.out.println("can we find this library : <" + libraryName + ">");
		try
		{

			//System.loadLibrary(libraryName);
			System.load(libraryName);
		}
		catch (UnsatisfiedLinkError e)
		{
			e.printStackTrace();
		}

	}

	public void testYouTubeAPI()
	{
		String devId = "YicFkZ52898";
		YouTubeHandler service = new YouTubeHandler(devId);
		String user = "hillaryclintondotcom";
		System.out.println("user = " + user);
		try
		{
			List<YouTubeVideo> videoList = service.getVideosByUser(user, 1, 50);
			System.out.println("videoList size = " + videoList.size());

			for (int i = 0; i < videoList.size(); i++)
			{
				String filename = "" + user + "" + i;
				YouTubeVideo video = videoList.get(i);
				video = service.getVideoDetails(video.getId());

				YouTubeHandler.printVideo(video);
			//getVideoPage(video.getId(), filename); //to download video
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void getVideoPage(String vidId, String filename)
	{
		try
		{
			// Create a URL for the desired page
			URL url = new URL("http://youtube.com//watch?v=" + vidId);

			// Read all the text returned by the server
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String str;
			while ((str = in.readLine()) != null)
			{
				String matchStr = "watch_fullscreen";
				if (str.indexOf(matchStr) >= 0)
				{
					String[] split1 = str.split(matchStr);
					String[] split2 = split1[1].split("&fs=1");
					String downloadURL = "http://youtube.com/get_video.php" + split2[0];

					downloadVideo(downloadURL, filename);
				}
			}

			in.close();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void downloadVideo(String downloadURL, String filename) throws IOException
	{
		System.out.println(downloadURL);
		URL u = new URL(downloadURL);
		URLConnection uc = u.openConnection();
		String contentType = uc.getContentType();
		int contentLength = uc.getContentLength();

		InputStream raw = uc.getInputStream();
		InputStream in = new BufferedInputStream(raw);
		byte[] data = new byte[contentLength];
		int bytesRead = 0;
		int offset = 0;
		while (offset < contentLength)
		{
			bytesRead = in.read(data, offset, data.length - offset);
			if (bytesRead == -1)
			{
				break;
			}
			offset += bytesRead;
		//System.out.println("offest = " + offset);
		}
		in.close();

		if (offset != contentLength)
		{
			throw new IOException("Only read " + offset + " bytes; Expected " + contentLength + " bytes");
		}

		System.out.println("filename = " + filename);
		FileOutputStream out = new FileOutputStream("data/youtube/" + filename + ".flv");
		out.write(data);
		out.flush();
		out.close();
	}

	public void testVideoLoading()
	{
		try
		{
			//String url1 = "file:data/DaddyCool.flv";
			String url1 = "file:data/videos/testvid.mov";
			//String url1 = "file:data/cats.flv";
			//String url1 = "file:/Users/angus/Projects/attribute/data/videos/testvid.mov";
			//String url1 = "file:data/videos/testvid.mov";
			//String url1 = "http://www.mat.ucsb.edu/~a.forbes/sheep1.mov";

			//String url1 = "file:data/abc.avi";
			//String url1 = "file://data/youtube/10467.flv";

			//File f = new File("data/cats.flv");
			//File f = new File("data/DaddyCool.flv");
			//File f = new File("data/arctcat.mpg");
			//URL url = f.toURL();
			//System.out.println("url = " + url);
			//GeomRectVideo grv1 = new GeomRectVideo(url);


			GeomRectVideoFobs grv1 = new GeomRectVideoFobs(url1);

			grv1.load();
			addGeom(grv1);

			//Utils.sleep(3000);

			grv1.isActive = true;
		/*
		grv1.rotateAnchor = new GeomPoint(0f, 0f, 0f);
		grv1.attachBehavior(new BehaviorRotate3D(Utils.nowPlusMillis(1000L), 5000L,
		LoopEnum.REVERSE,
		0f, 0f, 360f,
		0f));
		grv1.attachBehavior(new BehaviorScale3D(Utils.nowPlusMillis(1000L), 5000L,
		LoopEnum.REVERSE,
		4f, 4f, 0f,
		0f));
		 */
		}
		catch (Exception e)
		{
		}
	/*
	String url2 = "file://data/sheep1.mov";
	GeomRectVideo grv2 = new GeomRectVideo(url2);
	addGeom(grv2);
	grv2.isActive = true;
	//grv2.load();
	grv2.attachBehavior(new BehaviorLine3D(System.nanoTime(), 1000L, BehaviorLine3D.REVERSE, true, 0f, 0f, 0f, -.3f, 2f, 0f, 0f));
	
	
	String url3 = "file://data/heatdeath1.avi";
	GeomRectVideo grv3 = new GeomRectVideo(url3);
	addGeom(grv3);
	grv3.isActive = true;
	//grv3.load();
	grv3.attachBehavior(new BehaviorLine3D(System.nanoTime(), 1000L, BehaviorLine3D.REVERSE, true, 0f, 0f, 0f, 1f, 1f, 0f, 0f));
	 */
	}
}
