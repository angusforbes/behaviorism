/* Spring.java ~ Apr 8, 2009 */

package physics;

import javax.vecmath.Vector3d;

public class Spring
{
    float springConstant;
    float damping;
    float restLength;
    public Particle a;
    public Particle b;
    boolean on;

    public Spring(Particle A, Particle B, float ks, float d, float r)
    {
        springConstant = ks;
        damping = d;
        restLength = r;
        a = A;
        b = B;
        on = true;
    }

    public final void turnOff()
    {
        on = false;
    }

    public final void turnOn()
    {
        on = true;
    }

    public final boolean isOn()
    {
        return on;
    }

    public final boolean isOff()
    {
        return !on;
    }

    public final Particle getOneEnd()
    {
        return a;
    }

    public final Particle getTheOtherEnd()
    {
        return b;
    }

    public final float currentLength()
    {
        double dx = a.position.x - b.position.x;
        double dy = a.position.y - b.position.y;
        double dz = a.position.z - b.position.z;
        return (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public final float restLength()
    {
        return restLength;
    }

    public final float strength()
    {
        return springConstant;
    }

    public final void setStrength(float ks)
    {
        springConstant = ks;
    }

    public final float damping()
    {
        return damping;
    }

    public final void setDamping(float d)
    {
        damping = d;
    }

    public final void setRestLength(float l)
    {
        restLength = l;
    }


    public final void apply()
    {
        if(on)
        {
            float a2bX = (float)(a.position.x - b.position.x);
            float a2bY = (float)(a.position.y - b.position.y);
            float a2bZ = (float)(a.position().z - b.position().z);
            float oneOvera2bDistance = ParticleSystem.fastInverseSqrt(a2bX * a2bX + a2bY * a2bY + a2bZ * a2bZ);
            float a2bDistance = 1.0F / oneOvera2bDistance;
            if(a2bDistance == 0.0F)
            {
                a2bX = 0.0F;
                a2bY = 0.0F;
                a2bZ = 0.0F;
            } else
            {
                a2bX *= oneOvera2bDistance;
                a2bY *= oneOvera2bDistance;
                a2bZ *= oneOvera2bDistance;
            }
            float springForce = -(a2bDistance - restLength) * springConstant;
            float Va2bX = (float)(a.velocity.x - b.velocity.x);
            float Va2bY = (float)(a.velocity.y - b.velocity.y);
            float Va2bZ = (float)(a.velocity.z - b.velocity.z);
            float dampingForce = -damping * (a2bX * Va2bX + a2bY * Va2bY + a2bZ * Va2bZ);
            float r = springForce + dampingForce;
            a2bX *= r;
            a2bY *= r;
            a2bZ *= r;
            if(a.isFree())
                a.force().add(new Vector3d(a2bX, a2bY, a2bZ));
            if(b.isFree())
                b.force().add(new Vector3d(-a2bX, -a2bY, -a2bZ));
        }
    }

    public final boolean hasDead()
    {
        return a.isDead() || b.isDead();
    }


}
