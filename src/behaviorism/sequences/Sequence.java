package behaviorism. sequences;

import behaviorism.utils.RenderUtils;
import behaviorism.worlds.World;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import static behaviorism.utils.Utils.*;

/**
 *
 * @author angus
 */
public class Sequence
{
  public List<Sequence> sequences = new CopyOnWriteArrayList<Sequence>();
  long executeTime = 0;

	public World world = null;
	public long baseNano = -1;
	public long pauseNano = -1;

  public Sequence()
  {
    this.baseNano = now();
  }

	public Sequence(World world, long baseNano)
	{
		this.world = world;
		this.baseNano = baseNano;
	}

	//public abstract List<Sequence> execute();
	public void execute(final long currentNano)
  {

  }


  public void executeSequences(
    //final List<Sequence> sequences,
					final long currentNano)
	{
    for (Sequence sequence : sequences)
    {
      sequence.executeSequences(currentNano);
    }

    if (currentNano >= executeTime)
    {
      execute(currentNano);
    }
  }

	public static void executeSequencesOld(final List<Sequence> sequences,
					final long currentNano)
	{
		/*
		Thread t = new Thread()
		{
			@Override
			public void run()
			{
		 */
				//System.out.println("in executeSequences");

				//make placeholder for new sequences that may be created via the execution
				//List<Sequence> toBeScheduled = new ArrayList<Sequence>();

				//handle pauses correctly - prob a more efficient way to do this!
				//synchronized (sequences)
				{
					for (int i = sequences.size() - 1; i >= 0; i--)
					{
						Sequence s = sequences.get(i);

						if (RenderUtils.getWorld().isPaused == true)
						{
							//System.out.println("paused!");
							if (s.pauseNano == -1)
							{
								s.pauseNano = s.baseNano - currentNano;
							}
							else
							{
								s.baseNano = currentNano + s.pauseNano;
							}
						}
						else
						{
							s.pauseNano = -1;
						}

						//System.out.println("seq: " + s + ", will start in "+ Utils.nanosToMillis(s.baseNano - currentNano) + " millis... ");


						if (currentNano >= s.baseNano)
						{
							System.out.println("in executeSequences : executing a sequence!");
							//toBeScheduled.addAll(s.execute());
						//	s.execute();

							sequences.remove(s);
						}
					}
				}

		//	}
		//};
		//t.start();
	}
	

	
		
		
	/*
	public static void executeSequences(SortedMap<Long, List<Sequence>> sequences, 
	long currentNano)
	{
	//handle pauses correctly - prob a more efficient way to do this!
	synchronized (sequences)
	{
	Iterator<Map.Entry<Long, List<Sequence>>> i = sequences.entrySet().iterator();
	
	while (i.hasNext())
	{
	Map.Entry<Long, List<Sequence>> entry = i.next();
	
	for (Sequence s : entry.getValue())
	{
	if (BehaviorismDriver.renderer.currentWorld.isPaused == true)
	{
	
	if (s.pauseNano == -1)
	{
	s.pauseNano = s.baseNano - currentNano;
	}
	else
	{
	s.baseNano = currentNano + s.pauseNano;
	entry.
	}
	
	}
	else
	{
	s.pauseNano = -1;
	}
	
	System.out.println("pausing " + s + ", will start in "+ Utils.nanosToMillis(s.baseNano - currentNano) + " millis... ");
	
	}
	}
	}
	
	
	//make placeholder for new sequences that may be created via the execution
	List<Sequence> toBeScheduled = new ArrayList<Sequence>();
	
	//execute sequences
	synchronized (sequences)
	{
	SortedMap headMap = sequences.headMap(currentNano);
	Iterator<Map.Entry> i = headMap.entrySet().iterator();
	
	while (i.hasNext())
	{
	Map.Entry<Long, List<Sequence>> entry = i.next();
	
	for (Sequence s : entry.getValue())
	{
	toBeScheduled.addAll(s.execute());
	}
	}
	}
	
	//delete expired sequences
	synchronized (sequences)
	{
	SortedMap headMap = sequences.headMap(currentNano);
	Iterator<Map.Entry> i = headMap.entrySet().iterator();
	
	while (i.hasNext())
	{
	i.next();
	i.remove();
	}
	}
	
	//add any new sequences that may have been created
	Sequence.addSequences(sequences, toBeScheduled);
	}
	 */
	//public static void addSequences(SortedMap<Long, List<Sequence>> sequences, List<Sequence> ss)
	public static void addSequences(List<Sequence> sequences, List<Sequence> ss)
	{
		//synchronized (sequences)
		{
			for (Sequence s : ss)
			{
				sequences.add(s);
			}
		}
	}
	//public static void addSequence(SortedMap<Long, List<Sequence>> sequences, Sequence s)
	public void addSequence(Sequence s)
	{
			sequences.add(s);
	}

  public void removeSequence(Sequence s)
	{
			sequences.remove(s);
	}

	@Override
	public String toString()
	{
		return "" + baseNano;
	}
}
