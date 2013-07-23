package ideal.vacuum;


import ideal.vacuum.agent.vision.Eye ;
import ideal.vacuum.agent.vision.PhotoreceptorCell ;
import ideal.vacuum.agent.vision.RayTracing ;

import java.awt.Color ;
import java.util.LinkedList ;
import java.util.Queue ;

import javax.vecmath.Matrix3f ;
import javax.vecmath.Vector3f ;

import tracing.ITracer;

import ernest.Ernest ;
import ernest.IErnest ;

/**************************************
 * A Model for Ernest 
 * This class gathers methods that, we believe, will survive generations of Ernests.
 * @author ogeorgeon
 **************************************/
public class ErnestModel extends Model 
{

	public final static Color AGENT_COLOR = new Color(128,128,128);
	//public final static Color AGENT_COLOR = new Color(90,90,90);

	public static int ACTION_FORWARD = 0;
	public static int ACTION_LEFT = 1;
	public static int ACTION_RIGHT = 2;
	
	public static int AGENT_STOP = 0;
	public static int AGENT_RUN = 1;
	public static int AGENT_STEP = 2;
	
	public int cognitiveMode = AGENT_STOP;

	/** The angular field of each eye. */
	private double m_eyeAngle ;
	protected void setEyeAngle(double angle) {m_eyeAngle = angle;}
	protected double getEyeAngle() {return m_eyeAngle;}
	
	/** Ernest's sensorymotor system. */
	protected IErnest m_ernest;
	//protected ISensorymotorSystem m_sensorymotorSystem;
	protected ITracer m_tracer;

	/**
	 * Value of the diagonal projection in 2D:
	 * 1 for a square diagonal,
	 * 1/sqrt(2) for a circle diagonal.
	 */
	public final static float INV_SQRT_2 = (float) (1/Math.sqrt(2));
	final private float DIAG2D_PROJ = INV_SQRT_2;

	// Local directions
	final public Vector3f DIRECTION_AHEAD = new Vector3f(1, 0, 0);
	final public Vector3f DIRECTION_BEHIND = new Vector3f(-1, 0, 0);
	final public Vector3f DIRECTION_LEFT = new Vector3f(0, 1, 0);
	final public Vector3f DIRECTION_RIGHT = new Vector3f(0, -1, 0);
	final public Vector3f DIRECTION_AHEAD_LEFT = new Vector3f(DIAG2D_PROJ, DIAG2D_PROJ, 0);
	final public Vector3f DIRECTION_AHEAD_RIGHT = new Vector3f(DIAG2D_PROJ, -DIAG2D_PROJ, 0);
	final public Vector3f DIRECTION_BEHIND_LEFT = new Vector3f(-DIAG2D_PROJ, DIAG2D_PROJ, 0);
	final public Vector3f DIRECTION_BEHIND_RIGHT = new Vector3f(-DIAG2D_PROJ, -DIAG2D_PROJ, 0);	
	final public static float SOMATO_RADIUS = 1.1f;
	final public static float TACTILE_RADIUS = .8f;
	
	// Absolute directions in Cartesian coordinates (0,0) bottom left.
	final protected Vector3f DIRECTION_NORTH = new Vector3f(0, 1, 0);
	final protected Vector3f DIRECTION_NORTHEAST = new Vector3f(1, 1, 0);
	final protected Vector3f DIRECTION_EAST = new Vector3f(1, 0, 0);
	final protected Vector3f DIRECTION_SOUTHEAST = new Vector3f(1, -1, 0);
	final protected Vector3f DIRECTION_SOUTH = new Vector3f(0, -1, 0);
	final protected Vector3f DIRECTION_SOUTHWEST = new Vector3f(-1, -1, 0);
	final protected Vector3f DIRECTION_WEST = new Vector3f(-1, 0, 0);
	final protected Vector3f DIRECTION_NORTHWEST = new Vector3f(-1, 1, 0);
	
	public int lastAction;
	
	//private int m_updateCount;
	
	public Vector3f mSpeedT;    // translation and rotation speed in absolute reference
	public Vector3f mSpeedR;    // translation and rotation speed in absolute reference
	
	public Vector3f mEgoSpeedT;    // translation and rotation speed in absolute reference
	
	public ErnestModel(int i) 
	{
		super(i);
		mSpeedT=new Vector3f(0f,0f,0f);
		mSpeedR=new Vector3f(0f,0f,0f);
		
		mEgoSpeedT=new Vector3f(0f,0f,0f);
		
	}
	
	/**
	 * Initialize the Ernest agent.
	 */
	public void initErnest()
	{
		
	}
	
	public void closeErnest()
	{
		//m_tracer.close();
		mOrientation.z=(float) (Math.PI/2);
		m_ernest = null;
	}
	
	/**
	 * Update the agent when the environment is refreshed.
	 * (not necessarily a cognitive step for the agent).
	 */
	public void update()
	{
//		int[] intention = stepErnest(status);
//		status = enactSchema(intention);
	}
	
	/**
	 * Run Ernest one step
	 */
//	public int[] stepErnest(boolean status)
//	{
//
//		// Sense the environment
//		int [][] matrix = new int [2][1];
//		
//		String intention = Character.toString((char)m_ernest.step(matrix)[0]);
//
//		//return intention;
//		return m_ernest.step(matrix);
//	}
	
	/**
	 * Trace an event generated by the user, typically: a mouse click to change a square on the grid.
	 * @param type The event's type.
	 * @param x The x coordinate on the grid.
	 * @param y The y coordinate on the grid.
	 */
	public void traceUserEvent(String type, int x, int y)
	{
		//Object element = m_tracer.newEvent("user", type, m_counter);
		//m_tracer.addSubelement(element, "x", x + "");
		//m_tracer.addSubelement(element, "y", y + "");
	}
	
//	/**
//	 * Generates a retina image from Ernest's view point.
//	 * (Uses Ernest's orientationRad value, trigonometric, counterclockwise, radius).
//	 * @return The array of colors projected onto the retina.
//	 */ 
//	public Queue<PhotoreceptorCell> getRetina(double orientationRad) {
//		System.out.println("vision");
//		double angleOrigin = orientationRad - Math.PI/2;
//		double angleSpan = Math.PI;
//		RayTracing cellsTracing = new RayTracing( this.m_env , this , this.mPosition , this.mName , angleOrigin , angleSpan ) ;
//		Queue<PhotoreceptorCell> cells = cellsTracing.rayTrace() ;
//		
//		for ( PhotoreceptorCell photoreceptorCell : cells ) {
//			System.out.println("retina (" + photoreceptorCell.getxBlockPosition() + "," + photoreceptorCell.getyBlockPosition() + ")");
//		}
//		
//		// Agent up, left, down
//		if ((Math.abs(orientationRad - Math.PI/2) < .1f) || (Math.abs(orientationRad + Math.PI/2) < .1f) || (Math.abs(Math.PI - orientationRad) < .1f || Math.abs(orientationRad + Math.PI) < .1f)){
//			for ( PhotoreceptorCell photoreceptorCell : cells ) {
//				photoreceptorCell.orienteAxis( orientationRad );
//			}
//		}
//
//		return cells;
//	}
	
	/**
	 * @param localVec A position relative to Ernest.
	 * @return The absolute position relative to the board ((rotZ(mOrientation.z) * localVec) + mPosition). 
	 */
	public Vector3f localToParentRef(Vector3f localVec) 
	{
		Matrix3f rot = new Matrix3f();
		rot.rotZ(mOrientation.z);
		
		Vector3f parentVec = new Vector3f();
		rot.transform(localVec, parentVec); // (rot * localVec) is placed into parentVec
		//parentVec.add(new Vector3f(m_x, m_y, 0));
		parentVec.add(mPosition); // now parentVec = (rotZ(mOrientation.z) * localVec) + mPosition.
		return parentVec;
	}	
	
	/**
	 * Ernest's 
	 */
	public void ernestDynamic()
	{
		mTranslation.scale(.9f);
		mPosition.add(mTranslation);
		//m_x = mPosition.x;
		//m_y = m_h - mPosition.y;
		
		mRotation.scale(.9f);
		mOrientation.add(mRotation);
	}
	
	public Vector3f cellCenter(Vector3f position)
	{
		Vector3f cellCenter = new Vector3f(Math.round(position.x), Math.round(position.y), Math.round(position.z));
		return cellCenter;
	}

	public void keepDistance(Vector3f position, Vector3f point, float distance)
	{
		if (point != null)
		{
			Vector3f toPoint = new Vector3f(point);
			toPoint.sub(position);
			if (toPoint.length() < distance)
			{
				//position.add(toPoint);
				position.set(point);
				toPoint.normalize();
				toPoint.scale(- distance);
				position.add(toPoint);
			}
		}
	}
	
//	public List<IPlace> getPlaceList(){
//		return m_ernest.getPlaceList();
//	}
	
	
	
	
	//******************************************
	////////////////////////////////////////////
	//******************************************
	
	public void updateColliculus(double[] rv2, Color[] colorMap2, double[] rt2, int[] tactileMap2, int lastAction2, float speed){
		
	}
	
    public Color getColor()
    {
    	return AGENT_COLOR;
    }
    
    public boolean affordEat()
    {
    	return false;
    }
	
    public boolean affordCuddle()
    {
    	return true;
    }
    
    public boolean isAgent(){
    	return true;
    }
	
	public int getCounter()
	{
		if (m_ernest != null)
			return m_ernest.getClock();
		else
			return 0;
	}
	
	public boolean getCuddle()
	{
		return m_cuddle;
	}

	public boolean getEat()
	{
		return m_eat;
	}
	
	public IErnest getErnest()
	{
		return m_ernest;
	}
	
//	protected void count()
//	{
//		m_updateCount++;
//	}

	public int getUpdateCount()
	{
		return m_ernest.getUpdateCount();
	}
	
}
