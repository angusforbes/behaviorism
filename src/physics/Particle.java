/* Particle.java ~ Apr 8, 2009 */

package physics;

import javax.vecmath.Vector3d;

/**
 *
 * @author angus
 */

public class Particle
{
    public Vector3d position;
    public Vector3d velocity;
    public Vector3d force;
    public float mass;
    public float age;
    public boolean dead;
    public boolean fixed;

    public Particle(float m, Vector3d p)
    {
        position = p;
        velocity = new Vector3d();
        force = new Vector3d();
        mass = m;
        fixed = false;
        age = 0.0F;
        dead = false;
    }

    public final void moveTo(float x, float y, float z)
    {
        position.set(x, y, z);
    }

    public final void moveBy(float x, float y, float z)
    {
        position.x += x;
        position.y += y;
        position.z += z;
    }

    public final void addVelocity(float x, float y, float z)
    {
        velocity.x += x;
        velocity.y += y;
        velocity.z += z;
    }

    public final void setForce(float x, float y, float z)
    {
        force.set(x, y, z);
    }

    public final void fix()
    {
        fixed = true;
        velocity.set(0, 0, 0);
    }

    public final boolean isFixed()
    {
        return fixed;
    }

    public final boolean isFree()
    {
        return !fixed;
    }

    public final void unfix()
    {
        fixed = false;
    }

    public final Vector3d position()
    {
        return position;
    }

    public final void setVelocity(float x, float y, float z)
    {
        velocity.set(x, y, z);
    }

    public final Vector3d velocity()
    {
        return velocity;
    }

    public final float mass()
    {
        return mass;
    }

    public final void setMass(float m)
    {
        mass = m;
    }

    public final Vector3d force()
    {
        return force;
    }

    public final float age()
    {
        return age;
    }

    public final void kill()
    {
        dead = true;
    }

    public final boolean isDead()
    {
        return dead;
    }



}
