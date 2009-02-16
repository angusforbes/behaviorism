package behaviors;

import java.io.*;
import java.nio.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.net.*;

/**
 * AccelrationPoint is used by the Behaviors to define the speed of actions
 * at certain points (time/distance ratio) 
 * Used like so:
 * new AccelartionPoint(pointInTime, pointInDistance);
 * to mean : in some amount of time I go some amount of distance...
 * that is : if the behavior is set up to last 5000ms and move
 * along the x-axis 10 units.
 * then accelration points at (.8, .2), (1.0, 1.0)
 * means that after 4000ms I have only gone 2 units,
 * but that over the next 1000ms I go 8 units
 */

public class AccelerationPoint
{
  public float percentage_time;
	public float percentage_dist;

  public AccelerationPoint(float pt, float pd)
	{
		this.percentage_time = pt;
		this.percentage_dist = pd;
	}
}

