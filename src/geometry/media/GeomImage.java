/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geometry.media;

import geometry.*;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Point3f;
import renderers.RendererJogl;
import utils.FileUtils;

public class GeomImage extends GeomRect {

    float maxSize = 1f;

    /** 
     * create a GeomImage from a URL using default coordinates.
     * @param url
     */
    public GeomImage(URL url) {
        super(0f, 0f, 0f, 1f, 1f);
        generateTexture(url, true);
    }

    public GeomImage(URL url, Point3f p3f) {
        super(p3f, 1f, 1f);
        generateTexture(url, true);
    }

    public GeomImage(String urlString) {
        super(0f, 0f, 0f, 1f, 1f);

        try {
            URL url = new URL(urlString);
            generateTexture(url, true);
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        }
    }

    /**
     * create a GeomImage from a URL using specified coordinates. 
     * @param url
     * @param x
     * @param y
     * @param z
     * @param w
     * @param h
     */
    public GeomImage(URL url, float x, float y, float z, float w, float h) {
        super(x, y, z, w, h);
        generateTexture(url, true);
    }

    /**
     * create a GeomImage from a URL using specified coordinates. 
     * @param url
     * @param x
     * @param y
     * @param z
     * @param w
     * @param h
     */
    public GeomImage(String urlString, float x, float y, float z, float w, float h) {
        super(x, y, z, w, h);

        try {
            URL url = new URL(urlString);
            generateTexture(url, true);
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        }
    }

    /**
     * create a GeomImage from a URL using specified coordinates.
     * @param url
     * @param p3f
     * @param w
     * @param h
     */
    public GeomImage(URL url, Point3f p3f, float w, float h) {
        super(p3f, w, h);
        generateTexture(url, true);
    }

    /**
     * Create a new GeomImage with an existing TextureData
     * (eg, taken from a GeomImage that's already been created).
     * 
     * @param textureData
     * @param x
     * @param y
     * @param z
     * @param w
     * @param h
     */
    public GeomImage(TextureData textureData,
            float x, float y, float z,
            float w, float h) {
        super(x, y, z, w, h);
        this.textureData = textureData;
    }

    public GeomImage(TextureData textureData,
            Point3f p3f,
            float w, float h) {
        super(p3f, w, h);
        this.textureData = textureData;
        this.maxSize = w;
        normalizeSize(maxSize);
    }

    /**
     * Create a new GeomImage with an existing BufferedImage
     * 
     * @param image
     * @param x
     * @param y
     * @param z
     * @param w
     * @param h
     */
    public GeomImage(BufferedImage bi,
            float x, float y, float z,
            float w, float h) {
        super(x, y, z, w, h);
        maxSize = w;
        generateTexture(bi, true);
    }

    /**
     * Create a new GeomImage with an existing BufferedImage
     * 
     * @param bi
     * @param p3f
     * @param w
     * @param h
     */
    public GeomImage(BufferedImage bi,
            Point3f p3f,
            float w, float h) {
        super(p3f, w, h);
        maxSize = w;
        generateTexture(bi, true);
    }

    /**
     * Create a new GeomImage from an image file.
     * @param file
     * @param p3f
     * @param w
     * @param h
     */
     public GeomImage(File file,
            Point3f p3f,
            float w, float h) {
        super(p3f, w, h);
        maxSize = w;
        BufferedImage bi = FileUtils.loadBufferedImageFromFile(file);
        generateTexture(bi, true);
    }

    public void generateTexture(URL url, boolean useMipMaps) {
        setColor(1f, 1f, 1f, 1f);
        String imageType = TextureIO.JPG;
        try {
            this.textureData = TextureIO.newTextureData(url, useMipMaps, imageType);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    //	normalizeSize(this.w);
    //normalizeSize(maxSize);
    }

    //no mipmaps by default...
    public static TextureData generateTexture(BufferedImage bi) {
        return TextureIO.newTextureData(bi, false);
    }

    public void generateTexture(BufferedImage bi, boolean useMipMaps) {
        setColor(1f, 1f, 1f, 1f);

        this.textureData = TextureIO.newTextureData(bi, useMipMaps);
        normalizeSize(maxSize);
    }

    @Override
    public void draw(GL gl, GLU glu, float offset)
    {
        if (this.textureData == null) {
            //System.out.println("A NO TEXTURE DATA!!!");
            //not ready yet
            return;
        } else if (this.texture == null) {
            //System.out.println("B THERE WAS NO TEXTURE OBJ. MAKING IT NOW");
            this.texture = TextureIO.newTexture(this.textureData);
        //this.texture.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        //this.texture.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

        } else {
            //System.out.println("C WE ALREADY HAVE A TEXTURE...");
        }

        //System.out.println("drawing... " + System.currentTimeMillis());
        boolean depthTest = RendererJogl.getBoolean(gl, GL.GL_DEPTH_TEST);

        if (depthTest == false && isSelectable == true) {
            //then we need to render it invisibly with DEPTH_TEST on so that we can pick it
            gl.glColor4f(0f, 0f, 0f, 0f);

            gl.glEnable(GL.GL_DEPTH_TEST);

            gl.glBegin(gl.GL_QUADS);

            gl.glVertex3f(0f, 0f, offset);
            gl.glVertex3f(w, 0f, offset);
            gl.glVertex3f(w, h, offset);
            gl.glVertex3f(0f, h, offset);
            gl.glEnd();

            gl.glDisable(GL.GL_DEPTH_TEST);

        }

        gl.glColor4f(r, g, b, a);

        //this.texture.enable();
        this.texture.bind();

        /*
        float fliph = this.h;
        if (this.texture.getMustFlipVertically())
        {
        //fliph *= -1.0f;
        // don't seem to need this if explicitly grabbing texture coords...
        /
         */

        gl.glEnable(GL.GL_TEXTURE_2D);

        //might need this for macs???
        //gl.glEnable(GL.GL_TEXTURE_RECTANGLE_ARB);

        //might need this for macs???
        //gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);

        gl.glBegin(gl.GL_QUADS);

        TextureCoords tc = texture.getImageTexCoords();
        //System.out.printf("getImageTexCoords %f/%f/%f/%f\n", tc.bottom(),
        // sds									tc.top(), tc.left(), tc.right());


        gl.glTexCoord2f(tc.left(), tc.bottom());
        gl.glVertex3f(0f, 0f, offset);
        gl.glTexCoord2f(tc.right(), tc.bottom());
        gl.glVertex3f(w, 0f, offset);
        gl.glTexCoord2f(tc.right(), tc.top());
        gl.glVertex3f(w, h, offset);
        gl.glTexCoord2f(tc.left(), tc.top());
        gl.glVertex3f(0f, h, offset);

        gl.glEnd();

        gl.glDisable(GL.GL_TEXTURE_2D);

    //gl.glEnable(gl.GL_BLEND);


    //this.texture.disable();
    //temp!!!

    //debugPackingAlgorithm(gl);

    //end temp

    }

    public void normalizeSize(float size) {
        if (textureData == null) {
            System.out.println("ERROR, no textureData!");
            return;
        }
        int tw = textureData.getWidth();
        int th = textureData.getHeight();

        //System.out.println("tw / th = " + tw + "/" + th);
        if (tw >= th) {
            normalizeSizeByWidth(tw, th, size);
        } else {
            normalizeSizeByHeight(tw, th, size);
        }
    }

}