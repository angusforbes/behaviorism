/* ParticleSystem.java ~ Apr 8, 2009 */

package physics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.vecmath.Vector3d;

//this shouldn't be part of the library!!! we should just import it as needed directly from the traer jar
//for specific projects. Or make an add-on lib to include other numerical methods/integrators if needed...
//
public class ParticleSystem
{
    public List<Particle> particles = new ArrayList<Particle>();
    public List<Spring> springs = new ArrayList<Spring>();
    public List<Attraction> attractions = new ArrayList<Attraction>();

    RungeKuttaIntegrator integrator;
    Vector3d gravity;
    float drag;

    public final void setGravity(float x, float y, float z)
    {
        gravity.set(x, y, z);
    }

    public final void setGravity(float g)
    {
        gravity.set(0.0F, g, 0.0F);
    }

    public final void setDrag(float d)
    {
        drag = d;
    }

    public final void advanceTime(float time)
    {
        cleanUp();
        integrator.step(time);
    }

    public final void tick()
    {
        cleanUp();
        integrator.step(1.0F);
    }

    public final void tick(float t)
    {
        cleanUp();
        integrator.step(t);
    }

    public final Particle makeParticle(float mass, float x, float y, float z)
    {
        Particle p = new Particle(mass, new Vector3d(x, y, z));
        particles.add(p);
        integrator.allocateParticles();
        return p;
    }

    public final Particle makeParticle()
    {
        return makeParticle(1.0F, 0.0F, 0.0F, 0.0F);
    }

    public final Spring makeSpring(Particle a, Particle b, float ks, float d, float r)
    {
        Spring s = new Spring(a, b, ks, d, r);
        springs.add(s);
        return s;
    }

    public final Attraction makeAttraction(Particle a, Particle b, float k, float minDistance)
    {
        Attraction m = new Attraction(a, b, k, minDistance);
        attractions.add(m);
        return m;
    }

    public final void clear()
    {
        particles.clear();
        springs.clear();
        attractions.clear();
    }

    public ParticleSystem(float g, float somedrag)
    {
        integrator = new RungeKuttaIntegrator(this);
        particles = new ArrayList();
        springs = new ArrayList();
        attractions = new ArrayList();
        gravity = new Vector3d(0.0F, g, 0.0F);
        drag = somedrag;
    }

    public ParticleSystem(float gx, float gy, float gz, float somedrag)
    {
        integrator = new RungeKuttaIntegrator(this);
        particles = new ArrayList();
        springs = new ArrayList();
        attractions = new ArrayList();
        gravity = new Vector3d(gx, gy, gz);
        drag = somedrag;
    }

    protected final void applyForces()
    {
        for(int i = 0; i < particles.size(); i++)
        {
            Particle p = (Particle)particles.get(i);
            p.force.add(gravity);
            p.force.add(new Vector3d(p.velocity.x * -drag, p.velocity.y * -drag, p.velocity.z * -drag));
        }

        for(int i = 0; i < springs.size(); i++)
        {
            Spring f = (Spring)springs.get(i);
            f.apply();
        }

        for(int i = 0; i < attractions.size(); i++)
        {
            Attraction f = (Attraction)attractions.get(i);
            f.apply();
        }

    }

    protected final void clearForces()
    {
        Particle p;
        for(Iterator i = particles.iterator(); i.hasNext(); p.force.set(0,0,0))
            p = (Particle)i.next();

    }

    private final void cleanUp()
    {
        for(int i = particles.size() - 1; i >= 0; i--)
        {
            Particle p = (Particle)particles.get(i);
            if(p.isDead())
                particles.remove(i);
        }

        for(int i = springs.size() - 1; i >= 0; i--)
        {
            Spring f = (Spring)springs.get(i);
            if(f.hasDead())
                springs.remove(i);
        }

        for(int i = attractions.size() - 1; i >= 0; i--)
        {
            Attraction f = (Attraction)attractions.get(i);
            if(f.hasDead())
                attractions.remove(i);
        }

    }

    public final int numberOfParticles()
    {
        return particles.size();
    }

    public final int numberOfSprings()
    {
        return springs.size();
    }

    public final int numberOfAttractions()
    {
        return attractions.size();
    }

    public final Particle getParticle(int i)
    {
        return (Particle)particles.get(i);
    }

    public final Spring getSpring(int i)
    {
        return (Spring)springs.get(i);
    }

    public final Attraction getAttraction(int i)
    {
        return (Attraction)attractions.get(i);
    }



    public static float fastInverseSqrt(float x)
    {
        float half = 0.5F * x;
        int i = Float.floatToIntBits(x);
        i = 0x5f375a86 - (i >> 1);
        x = Float.intBitsToFloat(i);
        return x * (1.5F - half * x * x);
    }
}

