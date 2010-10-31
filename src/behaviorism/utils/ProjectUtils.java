package behaviorism.utils;

import com.sun.opengl.impl.InternalBufferUtil;
import java.nio.*;

import javax.media.opengl.*;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

/**
 * ProjectFloat.java
 * <p/>
 * <p/>
 * Created 11-jan-2004
 *
 * @author Erik Duijs
 * @author Kenneth Russell
 */
public class ProjectUtils {
  private static final float[] IDENTITY_MATRIX =
    new float[] {
      1.0f, 0.0f, 0.0f, 0.0f,
      0.0f, 1.0f, 0.0f, 0.0f,
      0.0f, 0.0f, 1.0f, 0.0f,
      0.0f, 0.0f, 0.0f, 1.0f };

  private static final float[] ZERO_MATRIX =
    new float[] {
      0.0f, 0.0f, 0.0f, 0.0f,
      0.0f, 0.0f, 0.0f, 0.0f,
      0.0f, 0.0f, 0.0f, 0.0f,
      0.0f, 0.0f, 0.0f, 0.0f };

  // Note that we have cloned parts of the implementation in order to
  // support incoming Buffers. The reason for this is to avoid loading
  // non-direct buffer subclasses unnecessarily, because doing so can
  // cause performance decreases on direct buffer operations, at least
  // on the current HotSpot JVM. It would be nicer (and make the code
  // simpler) to simply have the array-based entry points delegate to
  // the versions taking Buffers by wrapping the arrays.

  // Array-based implementation
  private final float[] matrix = new float[16];
  private final float[][] tempInvertMatrix = new float[4][4];

  private final float[] in = new float[4];
  private final float[] out = new float[4];

  private final float[] forward = new float[3];
  private final float[] side = new float[3];
  private final float[] up = new float[3];

  // Buffer-based implementation
  private FloatBuffer locbuf;
  private final FloatBuffer matrixBuf;
  private final FloatBuffer tempInvertMatrixBuf;

  private final FloatBuffer inBuf;
  private final FloatBuffer outBuf;

  private final FloatBuffer forwardBuf;
  private final FloatBuffer sideBuf;
  private final FloatBuffer upBuf;

  public ProjectUtils() {
    // Use direct buffers to avoid loading indirect buffer
    // implementations for applications trying to avoid doing so.
    // Slice up one big buffer because some NIO implementations
    // allocate a huge amount of memory to back even the smallest of
    // buffers.
    locbuf = InternalBufferUtil.newFloatBuffer(2*16+2*4+3*3);
    int pos = 0;
    int sz = 16;
    matrixBuf = slice(locbuf, pos, sz);
    pos += sz;
    tempInvertMatrixBuf = slice(locbuf, pos, sz);
    pos += sz;
    sz = 4;
    inBuf = slice(locbuf, pos, sz);
    pos += sz;
    outBuf = slice(locbuf, pos, sz);
    pos += sz;
    sz = 3;
    forwardBuf = slice(locbuf, pos, sz);
    pos += sz;
    sideBuf = slice(locbuf, pos, sz);
    pos += sz;
    upBuf = slice(locbuf, pos, sz);
  }

  public void destroy() {
    if(locbuf!=null) {
        locbuf.clear();
        locbuf=null;
    }
  }

  private static FloatBuffer slice(FloatBuffer buf, int pos, int len) {
    buf.position(pos);
    buf.limit(pos + len);
    return buf.slice();
  }

  /**
   * Make matrix an identity matrix
   */
  public static void gluMakeIdentityf(FloatBuffer m) {
    int oldPos = m.position();
    m.put(IDENTITY_MATRIX);
    m.position(oldPos);
  }

  /**
   * Make matrix an zero matrix
   */
  public static void gluMakeZero(FloatBuffer m) {
    int oldPos = m.position();
    m.put(ZERO_MATRIX);
    m.position(oldPos);
  }

  /**
   * Make matrix an identity matrix
   */
  public static void gluMakeIdentityf(float[] m) {
    for (int i = 0; i < 16; i++) {
      m[i] = IDENTITY_MATRIX[i];
    }
  }

  /**
   * Method __gluMultMatrixVecf
   *
   * @param matrix
   * @param in
   * @param out
   */
  private void __gluMultMatrixVecf(float[] matrix, int matrix_offset, float[] in, float[] out) {
    for (int i = 0; i < 4; i++) {
      out[i] =
        in[0] * matrix[0*4+i+matrix_offset] +
        in[1] * matrix[1*4+i+matrix_offset] +
        in[2] * matrix[2*4+i+matrix_offset] +
        in[3] * matrix[3*4+i+matrix_offset];
    }
  }

  /**
   * Method __gluMultMatrixVecf
   *
   * @param matrix
   * @param in
   * @param out
   */
  private void __gluMultMatrixVecf(FloatBuffer matrix, FloatBuffer in, FloatBuffer out) {
    int inPos = in.position();
    int outPos = out.position();
    int matrixPos = matrix.position();
    for (int i = 0; i < 4; i++) {
      out.put(i + outPos,
              in.get(0+inPos) * matrix.get(0*4+i+matrixPos) +
              in.get(1+inPos) * matrix.get(1*4+i+matrixPos) +
              in.get(2+inPos) * matrix.get(2*4+i+matrixPos) +
              in.get(3+inPos) * matrix.get(3*4+i+matrixPos));
    }
  }

  /**
   * @param src
   * @param inverse
   *
   * @return
   */
  public boolean gluInvertMatrixf(float[] src, float[] inverse) {
    int i, j, k, swap;
    float t;
    float[][] temp = tempInvertMatrix;

    for (i = 0; i < 4; i++) {
      for (j = 0; j < 4; j++) {
        temp[i][j] = src[i*4+j];
      }
    }
    gluMakeIdentityf(inverse);

    for (i = 0; i < 4; i++) {
      //
      // Look for largest element in column
      //
      swap = i;
      for (j = i + 1; j < 4; j++) {
        if (Math.abs(temp[j][i]) > Math.abs(temp[i][i])) {
          swap = j;
        }
      }

      if (swap != i) {
        //
        // Swap rows.
        //
        for (k = 0; k < 4; k++) {
          t = temp[i][k];
          temp[i][k] = temp[swap][k];
          temp[swap][k] = t;

          t = inverse[i*4+k];
          inverse[i*4+k] = inverse[swap*4+k];
          inverse[swap*4+k] = t;
        }
      }

      if (temp[i][i] == 0) {
        //
        // No non-zero pivot. The matrix is singular, which shouldn't
        // happen. This means the user gave us a bad matrix.
        //
        return false;
      }

      t = temp[i][i];
      for (k = 0; k < 4; k++) {
        temp[i][k] /= t;
        inverse[i*4+k] /= t;
      }
      for (j = 0; j < 4; j++) {
        if (j != i) {
          t = temp[j][i];
          for (k = 0; k < 4; k++) {
            temp[j][k] -= temp[i][k] * t;
            inverse[j*4+k] -= inverse[i*4+k]*t;
          }
        }
      }
    }
    return true;
  }

  /**
   * @param src
   * @param inverse
   *
   * @return
   */
  public boolean gluInvertMatrixf(FloatBuffer src, FloatBuffer inverse) {
    int i, j, k, swap;
    float t;

    int srcPos = src.position();
    int invPos = inverse.position();

    FloatBuffer temp = tempInvertMatrixBuf;

    for (i = 0; i < 4; i++) {
      for (j = 0; j < 4; j++) {
        temp.put(i*4+j, src.get(i*4+j + srcPos));
      }
    }
    gluMakeIdentityf(inverse);

    for (i = 0; i < 4; i++) {
      //
      // Look for largest element in column
      //
      swap = i;
      for (j = i + 1; j < 4; j++) {
        if (Math.abs(temp.get(j*4+i)) > Math.abs(temp.get(i*4+i))) {
          swap = j;
        }
      }

      if (swap != i) {
        //
        // Swap rows.
        //
        for (k = 0; k < 4; k++) {
          t = temp.get(i*4+k);
          temp.put(i*4+k, temp.get(swap*4+k));
          temp.put(swap*4+k, t);

          t = inverse.get(i*4+k + invPos);
          inverse.put(i*4+k + invPos, inverse.get(swap*4+k + invPos));
          inverse.put(swap*4+k + invPos, t);
        }
      }

      if (temp.get(i*4+i) == 0) {
        //
        // No non-zero pivot. The matrix is singular, which shouldn't
        // happen. This means the user gave us a bad matrix.
        //
        return false;
      }

      t = temp.get(i*4+i);
      for (k = 0; k < 4; k++) {
        temp.put(i*4+k, temp.get(i*4+k) / t);
        inverse.put(i*4+k + invPos, inverse.get(i*4+k + invPos) / t);
      }
      for (j = 0; j < 4; j++) {
        if (j != i) {
          t = temp.get(j*4+i);
          for (k = 0; k < 4; k++) {
            temp.put(j*4+k, temp.get(j*4+k) - temp.get(i*4+k) * t);
            inverse.put(j*4+k + invPos, inverse.get(j*4+k + invPos) - inverse.get(i*4+k + invPos) * t);
          }
        }
      }
    }
    return true;
  }


  /**
   * @param a
   * @param b
   * @param r
   */
  private void gluMultMatricesf(float[] a, int a_offset, float[] b, int b_offset, float[] r) {
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        r[i*4+j] =
          a[i*4+0+a_offset]*b[0*4+j+b_offset] +
          a[i*4+1+a_offset]*b[1*4+j+b_offset] +
          a[i*4+2+a_offset]*b[2*4+j+b_offset] +
          a[i*4+3+a_offset]*b[3*4+j+b_offset];
      }
    }
  }


  /**
   * @param a
   * @param b
   * @param r
   */
  public static void gluMultMatricesf(FloatBuffer a, FloatBuffer b, FloatBuffer r) {
    int aPos = a.position();
    int bPos = b.position();
    int rPos = r.position();

    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        r.put(i*4+j + rPos,
          a.get(i*4+0+aPos)*b.get(0*4+j+bPos) +
          a.get(i*4+1+aPos)*b.get(1*4+j+bPos) +
          a.get(i*4+2+aPos)*b.get(2*4+j+bPos) +
          a.get(i*4+3+aPos)*b.get(3*4+j+bPos));
      }
    }
  }

  /**
   * Normalize vector
   *
   * @param v
   */
  public static void normalize(float[] v) {
    float r;

    r = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    if ( r == 0.0 || r == 1.0)
      return;

    r = 1.0f / r;

    v[0] *= r;
    v[1] *= r;
    v[2] *= r;

    return;
  }

  /**
   * Normalize vector
   *
   * @param v
   */
  public static void normalize(FloatBuffer v) {
    float r;

    int vPos = v.position();

    r = (float) Math.sqrt(v.get(0+vPos) * v.get(0+vPos) +
                          v.get(1+vPos) * v.get(1+vPos) +
                          v.get(2+vPos) * v.get(2+vPos));
    if ( r == 0.0 || r == 1.0)
      return;

    r = 1.0f / r;

    v.put(0+vPos, v.get(0+vPos) * r);
    v.put(1+vPos, v.get(1+vPos) * r);
    v.put(2+vPos, v.get(2+vPos) * r);

    return;
  }


  /**
   * Calculate cross-product
   *
   * @param v1
   * @param v2
   * @param result
   */
  private static void cross(float[] v1, float[] v2, float[] result) {
    result[0] = v1[1] * v2[2] - v1[2] * v2[1];
    result[1] = v1[2] * v2[0] - v1[0] * v2[2];
    result[2] = v1[0] * v2[1] - v1[1] * v2[0];
  }

  /**
   * Calculate cross-product
   *
   * @param v1
   * @param v2
   * @param result
   */
  private static void cross(FloatBuffer v1, FloatBuffer v2, FloatBuffer result) {
    int v1Pos = v1.position();
    int v2Pos = v2.position();
    int rPos  = result.position();

    result.put(0+rPos, v1.get(1+v1Pos) * v2.get(2+v2Pos) - v1.get(2+v1Pos) * v2.get(1+v2Pos));
    result.put(1+rPos, v1.get(2+v1Pos) * v2.get(0+v2Pos) - v1.get(0+v1Pos) * v2.get(2+v2Pos));
    result.put(2+rPos, v1.get(0+v1Pos) * v2.get(1+v2Pos) - v1.get(1+v1Pos) * v2.get(0+v2Pos));
  }

  /**
   * Method gluOrtho2D.
   *
   * @param left
   * @param right
   * @param bottom
   * @param top
   */
  public void gluOrtho2D(GLMatrixFunc gl, float left, float right, float bottom, float top) {
    gl.glOrthof(left, right, bottom, top, -1, 1);
  }

  /**
   * Method gluPerspective.
   *
   * @param fovy
   * @param aspect
   * @param zNear
   * @param zFar
   */
  public void gluPerspective(GLMatrixFunc gl, float fovy, float aspect, float zNear, float zFar) {
    float sine, cotangent, deltaZ;
    float radians = fovy / 2 * (float) Math.PI / 180;

    deltaZ = zFar - zNear;
    sine = (float) Math.sin(radians);

    if ((deltaZ == 0) || (sine == 0) || (aspect == 0)) {
      return;
    }

    cotangent = (float) Math.cos(radians) / sine;

    gluMakeIdentityf(matrixBuf);

    matrixBuf.put(0 * 4 + 0, cotangent / aspect);
    matrixBuf.put(1 * 4 + 1, cotangent);
    matrixBuf.put(2 * 4 + 2, - (zFar + zNear) / deltaZ);
    matrixBuf.put(2 * 4 + 3, -1);
    matrixBuf.put(3 * 4 + 2, -2 * zNear * zFar / deltaZ);
    matrixBuf.put(3 * 4 + 3, 0);

    gl.glMultMatrixf(matrixBuf);
  }

  /**
   * Method gluLookAt
   *
   * @param eyex
   * @param eyey
   * @param eyez
   * @param centerx
   * @param centery
   * @param centerz
   * @param upx
   * @param upy
   * @param upz
   */
  public void gluLookAt(
    GL2 gl,
    //GLMatrixFunc gl,
                        float eyex,
                        float eyey,
                        float eyez,
                        float centerx,
                        float centery,
                        float centerz,
                        float upx,
                        float upy,
                        float upz) {
    FloatBuffer forward = this.forwardBuf;
    FloatBuffer side = this.sideBuf;
    FloatBuffer up = this.upBuf;

    forward.put(0, centerx - eyex);
    forward.put(1, centery - eyey);
    forward.put(2, centerz - eyez);

    up.put(0, upx);
    up.put(1, upy);
    up.put(2, upz);

    normalize(forward);

    /* Side = forward x up */
    cross(forward, up, side);
    normalize(side);

    /* Recompute up as: up = side x forward */
    cross(side, forward, up);

    gluMakeIdentityf(matrixBuf);
    matrixBuf.put(0 * 4 + 0, side.get(0));
    matrixBuf.put(1 * 4 + 0, side.get(1));
    matrixBuf.put(2 * 4 + 0, side.get(2));

    matrixBuf.put(0 * 4 + 1, up.get(0));
    matrixBuf.put(1 * 4 + 1, up.get(1));
    matrixBuf.put(2 * 4 + 1, up.get(2));

    matrixBuf.put(0 * 4 + 2, -forward.get(0));
    matrixBuf.put(1 * 4 + 2, -forward.get(1));
    matrixBuf.put(2 * 4 + 2, -forward.get(2));

    gl.glMultMatrixf(matrixBuf);
    gl.glTranslatef(-eyex, -eyey, -eyez);
  }

  /**
   * Method gluProject
   *
   * @param objx
   * @param objy
   * @param objz
   * @param modelMatrix
   * @param projMatrix
   * @param viewport
   * @param win_pos
   *
   * @return
   */
  public boolean gluProject(float objx,
                            float objy,
                            float objz,
                            float[] modelMatrix,
                            int modelMatrix_offset,
                            float[] projMatrix,
                            int projMatrix_offset,
                            int[] viewport,
                            int viewport_offset,
                            float[] win_pos,
                            int win_pos_offset ) {

    float[] in = this.in;
    float[] out = this.out;

    in[0] = objx;
    in[1] = objy;
    in[2] = objz;
    in[3] = 1.0f;

    __gluMultMatrixVecf(modelMatrix, modelMatrix_offset, in, out);
    __gluMultMatrixVecf(projMatrix, projMatrix_offset, out, in);

    if (in[3] == 0.0f)
      return false;

    in[3] = (1.0f / in[3]) * 0.5f;

    // Map x, y and z to range 0-1
    in[0] = in[0] * in[3] + 0.5f;
    in[1] = in[1] * in[3] + 0.5f;
    in[2] = in[2] * in[3] + 0.5f;

    // Map x,y to viewport
    win_pos[0+win_pos_offset] = in[0] * viewport[2+viewport_offset] + viewport[0+viewport_offset];
    win_pos[1+win_pos_offset] = in[1] * viewport[3+viewport_offset] + viewport[1+viewport_offset];
    win_pos[2+win_pos_offset] = in[2];

    return true;
  }

  /**
   * Method gluProject
   *
   * @param objx
   * @param objy
   * @param objz
   * @param modelMatrix
   * @param projMatrix
   * @param viewport
   * @param win_pos
   *
   * @return
   */
  public boolean gluProject(float objx,
                            float objy,
                            float objz,
                            FloatBuffer modelMatrix,
                            FloatBuffer projMatrix,
                            IntBuffer viewport,
                            FloatBuffer win_pos) {

    FloatBuffer in = this.inBuf;
    FloatBuffer out = this.outBuf;

    in.put(0, objx);
    in.put(1, objy);
    in.put(2, objz);
    in.put(3, 1.0f);

    __gluMultMatrixVecf(modelMatrix, in, out);
    __gluMultMatrixVecf(projMatrix, out, in);

    if (in.get(3) == 0.0f)
      return false;

    in.put(3, (1.0f / in.get(3)) * 0.5f);

    // Map x, y and z to range 0-1
    in.put(0, in.get(0) * in.get(3) + 0.5f);
    in.put(1, in.get(1) * in.get(3) + 0.5f);
    in.put(2, in.get(2) * in.get(3) + 0.5f);

    // Map x,y to viewport
    int vPos = viewport.position();
    int wPos = win_pos.position();
    win_pos.put(0+wPos, in.get(0) * viewport.get(2+vPos) + viewport.get(0+vPos));
    win_pos.put(1+wPos, in.get(1) * viewport.get(3+vPos) + viewport.get(1+vPos));
    win_pos.put(2+wPos, in.get(2));

    return true;
  }


  /**
   * Method gluUnproject
   *
   * @param winx
   * @param winy
   * @param winz
   * @param modelMatrix
   * @param projMatrix
   * @param viewport
   * @param obj_pos
   *
   * @return
   */
  public boolean gluUnProject(float winx,
                              float winy,
                              float winz,
                              float[] modelMatrix,
                              int modelMatrix_offset,
                              float[] projMatrix,
                              int projMatrix_offset,
                              int[] viewport,
                              int viewport_offset,
                              float[] obj_pos,
                              int obj_pos_offset) {
    float[] in = this.in;
    float[] out = this.out;

    gluMultMatricesf(modelMatrix, modelMatrix_offset, projMatrix, projMatrix_offset, matrix);

    if (!gluInvertMatrixf(matrix, matrix))
      return false;

    in[0] = winx;
    in[1] = winy;
    in[2] = winz;
    in[3] = 1.0f;

    // Map x and y from window coordinates
    in[0] = (in[0] - viewport[0+viewport_offset]) / viewport[2+viewport_offset];
    in[1] = (in[1] - viewport[1+viewport_offset]) / viewport[3+viewport_offset];

    // Map to range -1 to 1
    in[0] = in[0] * 2 - 1;
    in[1] = in[1] * 2 - 1;
    in[2] = in[2] * 2 - 1;

    __gluMultMatrixVecf(matrix, 0, in, out);

    if (out[3] == 0.0)
      return false;

    out[3] = 1.0f / out[3];

    obj_pos[0+obj_pos_offset] = out[0] * out[3];
    obj_pos[1+obj_pos_offset] = out[1] * out[3];
    obj_pos[2+obj_pos_offset] = out[2] * out[3];

    return true;
  }


  /**
   * Method gluUnproject
   *
   * @param winx
   * @param winy
   * @param winz
   * @param modelMatrix
   * @param projMatrix
   * @param viewport
   * @param obj_pos
   *
   * @return
   */
  public boolean gluUnProject(float winx,
                              float winy,
                              float winz,
                              FloatBuffer modelMatrix,
                              FloatBuffer projMatrix,
                              IntBuffer viewport,
                              FloatBuffer obj_pos) {
    FloatBuffer in = this.inBuf;
    FloatBuffer out = this.outBuf;

    gluMultMatricesf(modelMatrix, projMatrix, matrixBuf);

    if (!gluInvertMatrixf(matrixBuf, matrixBuf))
      return false;

    in.put(0, winx);
    in.put(1, winy);
    in.put(2, winz);
    in.put(3, 1.0f);

    // Map x and y from window coordinates
    int vPos = viewport.position();
    int oPos = obj_pos.position();
    in.put(0, (in.get(0) - viewport.get(0+vPos)) / viewport.get(2+vPos));
    in.put(1, (in.get(1) - viewport.get(1+vPos)) / viewport.get(3+vPos));

    // Map to range -1 to 1
    in.put(0, in.get(0) * 2 - 1);
    in.put(1, in.get(1) * 2 - 1);
    in.put(2, in.get(2) * 2 - 1);

    __gluMultMatrixVecf(matrixBuf, in, out);

    if (out.get(3) == 0.0f)
      return false;

    out.put(3, 1.0f / out.get(3));

    obj_pos.put(0+oPos, out.get(0) * out.get(3));
    obj_pos.put(1+oPos, out.get(1) * out.get(3));
    obj_pos.put(2+oPos, out.get(2) * out.get(3));

    return true;
  }


  /**
   * Method gluUnproject4
   *
   * @param winx
   * @param winy
   * @param winz
   * @param clipw
   * @param modelMatrix
   * @param projMatrix
   * @param viewport
   * @param near
   * @param far
   * @param obj_pos
   *
   * @return
   */
  public boolean gluUnProject4(float winx,
                               float winy,
                               float winz,
                               float clipw,
                               float[] modelMatrix,
                               int modelMatrix_offset,
                               float[] projMatrix,
                               int projMatrix_offset,
                               int[] viewport,
                               int viewport_offset,
                               float near,
                               float far,
                               float[] obj_pos,
                               int obj_pos_offset ) {
    float[] in = this.in;
    float[] out = this.out;

    gluMultMatricesf(modelMatrix, modelMatrix_offset, projMatrix, projMatrix_offset, matrix);

    if (!gluInvertMatrixf(matrix, matrix))
      return false;

    in[0] = winx;
    in[1] = winy;
    in[2] = winz;
    in[3] = clipw;

    // Map x and y from window coordinates
    in[0] = (in[0] - viewport[0+viewport_offset]) / viewport[2+viewport_offset];
    in[1] = (in[1] - viewport[1+viewport_offset]) / viewport[3+viewport_offset];
    in[2] = (in[2] - near) / (far - near);

    // Map to range -1 to 1
    in[0] = in[0] * 2 - 1;
    in[1] = in[1] * 2 - 1;
    in[2] = in[2] * 2 - 1;

    __gluMultMatrixVecf(matrix, 0, in, out);

    if (out[3] == 0.0f)
      return false;

    obj_pos[0+obj_pos_offset] = out[0];
    obj_pos[1+obj_pos_offset] = out[1];
    obj_pos[2+obj_pos_offset] = out[2];
    obj_pos[3+obj_pos_offset] = out[3];
    return true;
  }

  /**
   * Method gluUnproject4
   *
   * @param winx
   * @param winy
   * @param winz
   * @param clipw
   * @param modelMatrix
   * @param projMatrix
   * @param viewport
   * @param near
   * @param far
   * @param obj_pos
   *
   * @return
   */
  public boolean gluUnProject4(float winx,
                               float winy,
                               float winz,
                               float clipw,
                               FloatBuffer modelMatrix,
                               FloatBuffer projMatrix,
                               IntBuffer viewport,
                               float near,
                               float far,
                               FloatBuffer obj_pos) {
    FloatBuffer in = this.inBuf;
    FloatBuffer out = this.outBuf;

    gluMultMatricesf(modelMatrix, projMatrix, matrixBuf);

    if (!gluInvertMatrixf(matrixBuf, matrixBuf))
      return false;

    in.put(0, winx);
    in.put(1, winy);
    in.put(2, winz);
    in.put(3, clipw);

    // Map x and y from window coordinates
    int vPos = viewport.position();
    in.put(0, (in.get(0) - viewport.get(0+vPos)) / viewport.get(2+vPos));
    in.put(1, (in.get(1) - viewport.get(1+vPos)) / viewport.get(3+vPos));
    in.put(2, (in.get(2) - near) / (far - near));

    // Map to range -1 to 1
    in.put(0, in.get(0) * 2 - 1);
    in.put(1, in.get(1) * 2 - 1);
    in.put(2, in.get(2) * 2 - 1);

    __gluMultMatrixVecf(matrixBuf, in, out);

    if (out.get(3) == 0.0f)
      return false;

    int oPos = obj_pos.position();
    obj_pos.put(0+oPos, out.get(0));
    obj_pos.put(1+oPos, out.get(1));
    obj_pos.put(2+oPos, out.get(2));
    obj_pos.put(3+oPos, out.get(3));
    return true;
  }


  /**
   * Method gluPickMatrix
   *
   * @param x
   * @param y
   * @param deltaX
   * @param deltaY
   * @param viewport
   */
  public void gluPickMatrix(GLMatrixFunc gl,
                            float x,
                            float y,
                            float deltaX,
                            float deltaY,
                            IntBuffer viewport) {
    if (deltaX <= 0 || deltaY <= 0) {
      return;
    }

    /* Translate and scale the picked region to the entire window */
    int vPos = viewport.position();
    gl.glTranslatef((viewport.get(2+vPos) - 2 * (x - viewport.get(0+vPos))) / deltaX,
                    (viewport.get(3+vPos) - 2 * (y - viewport.get(1+vPos))) / deltaY,
                    0);
    gl.glScalef(viewport.get(2) / deltaX, viewport.get(3) / deltaY, 1.0f);
  }

  /**
   * Method gluPickMatrix
   *
   * @param x
   * @param y
   * @param deltaX
   * @param deltaY
   * @param viewport
   * @param viewport_offset
   */
  public void gluPickMatrix(GLMatrixFunc gl,
                            float x,
                            float y,
                            float deltaX,
                            float deltaY,
                            int[] viewport,
                            int viewport_offset) {
    if (deltaX <= 0 || deltaY <= 0) {
      return;
    }

    /* Translate and scale the picked region to the entire window */
    gl.glTranslatef((viewport[2+viewport_offset] - 2 * (x - viewport[0+viewport_offset])) / deltaX,
                    (viewport[3+viewport_offset] - 2 * (y - viewport[1+viewport_offset])) / deltaY,
                    0);
    gl.glScalef(viewport[2+viewport_offset] / deltaX, viewport[3+viewport_offset] / deltaY, 1.0f);
  }
}
