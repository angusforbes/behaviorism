/* Attraction.java ~ Apr 8, 2009 */

package physics;

import javax.vecmath.Vector3d;

public class Attraction
{

    public Attraction(Particle a, Particle b, float k, float distanceMin)
    {
        this.a = a;
        this.b = b;
        this.k = k;
        on = true;
        this.distanceMin = distanceMin;
    }

    public final float getMinimumDistance()
    {
        return distanceMin;
    }

    public final void setMinimumDistance(float d)
    {
        distanceMin = d;
    }

    public final void turnOff()
    {
        on = false;
    }

    public final void turnOn()
    {
        on = true;
    }

    public final void setStrength(float k)
    {
        this.k = k;
    }

    public final Particle getOneEnd()
    {
        return a;
    }

    public final Particle getTheOtherEnd()
    {
        return b;
    }

    public final void apply()
    {
        if(on)
        {
            float a2bX = (float)(a.position().x - b.position().x);
            float a2bY = (float)(a.position().y - b.position().y);
            float a2bZ = (float)(a.position().z - b.position().z);
            float oneOvera2bDistance = ParticleSystem.fastInverseSqrt(a2bX * a2bX + a2bY * a2bY + a2bZ * a2bZ);
            float a2bDistance = 1.0F / oneOvera2bDistance;
            if(a2bDistance == 0.0F)
                return;
            a2bX *= oneOvera2bDistance;
            a2bY *= oneOvera2bDistance;
            a2bZ *= oneOvera2bDistance;
            float force = k * a.mass * b.mass;
            if(a2bDistance < distanceMin)
                force /= distanceMin * distanceMin;
            else
                force *= oneOvera2bDistance * oneOvera2bDistance;
            a2bX *= force;
            a2bY *= force;
            a2bZ *= force;
            a.force().add(new Vector3d(-a2bX, -a2bY, -a2bZ));
            b.force().add(new Vector3d(a2bX, a2bY, a2bZ));
        }
    }

    public final float getStrength()
    {
        return k;
    }

    public final boolean isOn()
    {
        return on;
    }

    public final boolean isOff()
    {
        return !on;
    }

    public final boolean hasDead()
    {
        return a.isDead() || b.isDead();
    }

    Particle a;
    Particle b;
    float k;
    boolean on;
    float distanceMin;
}
