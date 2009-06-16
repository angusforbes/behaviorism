/* RungeKuttaIntegrator.java ~ Apr 8, 2009 */

package physics;

import java.util.ArrayList;
import javax.vecmath.Vector3d;

public class RungeKuttaIntegrator
{
    ArrayList originalPositions;
    ArrayList originalVelocities;
    ArrayList k1Forces;
    ArrayList k1Velocities;
    ArrayList k2Forces;
    ArrayList k2Velocities;
    ArrayList k3Forces;
    ArrayList k3Velocities;
    ArrayList k4Forces;
    ArrayList k4Velocities;
    ParticleSystem s;

    public RungeKuttaIntegrator(ParticleSystem s)
    {
        this.s = s;
        originalPositions = new ArrayList();
        originalVelocities = new ArrayList();

        k1Forces = new ArrayList();
        k1Velocities = new ArrayList();

        k2Forces = new ArrayList();
        k2Velocities = new ArrayList();

        k3Forces = new ArrayList();
        k3Velocities = new ArrayList();

        k4Forces = new ArrayList();
        k4Velocities = new ArrayList();
    }

    public final void allocateParticles()
    {
        for(; s.particles.size() > originalPositions.size(); k4Velocities.add(new Vector3d()))
        {
            originalPositions.add(new Vector3d());
            originalVelocities.add(new Vector3d());
            k1Forces.add(new Vector3d());
            k1Velocities.add(new Vector3d());
            k2Forces.add(new Vector3d());
            k2Velocities.add(new Vector3d());
            k3Forces.add(new Vector3d());
            k3Velocities.add(new Vector3d());
            k4Forces.add(new Vector3d());
        }
    }

    /*
 public final void step(float deltaT)
  {
    //store current position and velocity of particle
    for (int i = 0; i < s.particles.size(); i++)
    {
      Particle p = (Particle) s.particles.get(i);
      if (p.isFree())
      {
        ((Vector3d) originalPositions.get(i)).set(p.position);
        ((Vector3d) originalVelocities.get(i)).set(p.velocity);
      }
      p.force.set(0, 0, 0);
    }

    s.applyForces();
    for (int i = 0; i < s.particles.size(); i++)
    {
      Particle p = (Particle) s.particles.get(i);
      if (p.isFree())
      {
        ((Vector3d) k1Forces.get(i)).set(p.force);
        ((Vector3d) k1Velocities.get(i)).set(p.velocity);
      }
      p.force.set(0, 0, 0);
    }


    for (int i = 0; i < s.particles.size(); i++)
    {
      Particle p = (Particle) s.particles.get(i);
      if (p.isFree())
      {
        Vector3d originalPosition = (Vector3d) originalPositions.get(i);
        Vector3d k1Velocity = (Vector3d) k1Velocities.get(i);


        p.position.x = originalPosition.x + (k1Velocity.x / p.mass) * (0.5F * deltaT);
        p.position.y = originalPosition.y + (k1Velocity.y / p.mass) * (0.5F * deltaT);
        p.position.z = originalPosition.z + (k1Velocity.z / p.mass) * (0.5F * deltaT);

        Vector3d originalVelocity = (Vector3d) originalVelocities.get(i);
        Vector3d k1Force = (Vector3d) k1Forces.get(i);
        p.velocity.x = originalVelocity.x + (k1Force.x + k1Velocity.x * deltaT);
        p.velocity.y = originalVelocity.y + (k1Force.y + k1Velocity.y * deltaT);
        p.velocity.z = originalVelocity.z + (k1Force.z + k1Velocity.z * deltaT);



        s.applyForces();

         //vel = vel + dq2[0] + (dq2[1] / ode.mass) * ds / 2;

        p.velocity.x += originalVelocity.x + p.velocity.x + (p.force.x / p.mass) * (0.5F * deltaT);
        p.velocity.y += originalVelocity.y + p.velocity.y + (p.force.y / p.mass) * (0.5F * deltaT);
        p.velocity.z += originalVelocity.z + p.velocity.z + (p.force.z / p.mass) * (0.5F * deltaT);

      }
    }

    s.applyForces();

  }
  */
    public final void step(float deltaT)
    {
      //store current position and velocity of particle
        for(int i = 0; i < s.particles.size(); i++)
        {
            Particle p = (Particle)s.particles.get(i);
            if(p.isFree())
            {
                ((Vector3d)originalPositions.get(i)).set(p.position);
                ((Vector3d)originalVelocities.get(i)).set(p.velocity);
            }
            p.force.set(0,0,0);
        }

        s.applyForces();
        for(int i = 0; i < s.particles.size(); i++)
        {
            Particle p = (Particle)s.particles.get(i);
            if(p.isFree())
            {
                ((Vector3d)k1Forces.get(i)).set(p.force);
                ((Vector3d)k1Velocities.get(i)).set(p.velocity);
            }
            p.force.set(0,0,0);
        }

        for(int i = 0; i < s.particles.size(); i++)
        {
            Particle p = (Particle)s.particles.get(i);
            if(p.isFree())
            {
                Vector3d originalPosition = (Vector3d)originalPositions.get(i);
                Vector3d k1Velocity = (Vector3d)k1Velocities.get(i);
                p.position.x = originalPosition.x + k1Velocity.x * 0.5F * deltaT;
                p.position.y = originalPosition.y + k1Velocity.y * 0.5F * deltaT;
                p.position.z = originalPosition.z + k1Velocity.z * 0.5F * deltaT;
                Vector3d originalVelocity = (Vector3d)originalVelocities.get(i);
                Vector3d k1Force = (Vector3d)k1Forces.get(i);
                p.velocity.x = originalVelocity.x + (k1Force.x * 0.5F * deltaT) / p.mass;
                p.velocity.y = originalVelocity.y + (k1Force.y * 0.5F * deltaT) / p.mass;
                p.velocity.z = originalVelocity.z + (k1Force.z * 0.5F * deltaT) / p.mass;
            }
        }

        s.applyForces();
        for(int i = 0; i < s.particles.size(); i++)
        {
            Particle p = (Particle)s.particles.get(i);
            if(p.isFree())
            {
                ((Vector3d)k2Forces.get(i)).set(p.force);
                ((Vector3d)k2Velocities.get(i)).set(p.velocity);
            }
            p.force.set(0,0,0);
        }

        for(int i = 0; i < s.particles.size(); i++)
        {
            Particle p = (Particle)s.particles.get(i);
            if(p.isFree())
            {
                Vector3d originalPosition = (Vector3d)originalPositions.get(i);
                Vector3d k2Velocity = (Vector3d)k2Velocities.get(i);
                p.position.x = originalPosition.x + k2Velocity.x * 0.5F * deltaT;
                p.position.y = originalPosition.y + k2Velocity.y * 0.5F * deltaT;
                p.position.z = originalPosition.z + k2Velocity.z * 0.5F * deltaT;
                Vector3d originalVelocity = (Vector3d)originalVelocities.get(i);
                Vector3d k2Force = (Vector3d)k2Forces.get(i);
                p.velocity.x = originalVelocity.x + (k2Force.x * 0.5F * deltaT) / p.mass;
                p.velocity.y = originalVelocity.y + (k2Force.y * 0.5F * deltaT) / p.mass;
                p.velocity.z = originalVelocity.z + (k2Force.z * 0.5F * deltaT) / p.mass;
            }
        }

        s.applyForces();
        for(int i = 0; i < s.particles.size(); i++)
        {
            Particle p = (Particle)s.particles.get(i);
            if(p.isFree())
            {
                ((Vector3d)k3Forces.get(i)).set(p.force);
                ((Vector3d)k3Velocities.get(i)).set(p.velocity);
            }
            p.force.set(0,0,0);
        }

        for(int i = 0; i < s.particles.size(); i++)
        {
            Particle p = (Particle)s.particles.get(i);
            if(p.isFree())
            {
                Vector3d originalPosition = (Vector3d)originalPositions.get(i);
                Vector3d k3Velocity = (Vector3d)k3Velocities.get(i);
                p.position.x = originalPosition.x + k3Velocity.x * deltaT;
                p.position.y = originalPosition.y + k3Velocity.y * deltaT;
                p.position.z = originalPosition.z + k3Velocity.z * deltaT;
                Vector3d originalVelocity = (Vector3d)originalVelocities.get(i);
                Vector3d k3Force = (Vector3d)k3Forces.get(i);
                p.velocity.x = originalVelocity.x + (k3Force.x * deltaT) / p.mass;
                p.velocity.y = originalVelocity.y + (k3Force.y * deltaT) / p.mass;
                p.velocity.z = originalVelocity.z + (k3Force.z * deltaT) / p.mass;
            }
        }

        s.applyForces();
        for(int i = 0; i < s.particles.size(); i++)
        {
            Particle p = (Particle)s.particles.get(i);
            if(p.isFree())
            {
                ((Vector3d)k4Forces.get(i)).set(p.force);
                ((Vector3d)k4Velocities.get(i)).set(p.velocity);
            }
        }

        for(int i = 0; i < s.particles.size(); i++)
        {
            Particle p = (Particle)s.particles.get(i);
            p.age += deltaT;
            if(p.isFree())
            {
                Vector3d originalPosition = (Vector3d)originalPositions.get(i);
                Vector3d k1Velocity = (Vector3d)k1Velocities.get(i);
                Vector3d k2Velocity = (Vector3d)k2Velocities.get(i);
                Vector3d k3Velocity = (Vector3d)k3Velocities.get(i);
                Vector3d k4Velocity = (Vector3d)k4Velocities.get(i);
                p.position.x = originalPosition.x + (deltaT / 6F) * (k1Velocity.x + 2.0F * k2Velocity.x + 2.0F * k3Velocity.x + k4Velocity.x);
                p.position.y = originalPosition.y + (deltaT / 6F) * (k1Velocity.y + 2.0F * k2Velocity.y + 2.0F * k3Velocity.y + k4Velocity.y);
                p.position.z = originalPosition.z + (deltaT / 6F) * (k1Velocity.z + 2.0F * k2Velocity.z + 2.0F * k3Velocity.z + k4Velocity.z);
                Vector3d originalVelocity = (Vector3d)originalVelocities.get(i);
                Vector3d k1Force = (Vector3d)k1Forces.get(i);
                Vector3d k2Force = (Vector3d)k2Forces.get(i);
                Vector3d k3Force = (Vector3d)k3Forces.get(i);
                Vector3d k4Force = (Vector3d)k4Forces.get(i);
                p.velocity.x = originalVelocity.x + (deltaT / (6F * p.mass)) * (k1Force.x + 2.0F * k2Force.x + 2.0F * k3Force.x + k4Force.x);
                p.velocity.y = originalVelocity.y + (deltaT / (6F * p.mass)) * (k1Force.y + 2.0F * k2Force.y + 2.0F * k3Force.y + k4Force.y);
                p.velocity.z = originalVelocity.z + (deltaT / (6F * p.mass)) * (k1Force.z + 2.0F * k2Force.z + 2.0F * k3Force.z + k4Force.z);
            }
        }

    }


}
