import java.awt.Color;
import java.util.ArrayList;


public class TactileMap {

	public float[] m_tactilePressure;               // value of each sensor neurons
	public float[] m_tactilePressureOld;
	
	public float[] m_tactileValue;                  // value of sensors
	
	public float[][] m_constraints;
	public double[][] m_distances;
	public float[][] m_connectionsLenght;
	
	public float[] m_tactileVariations;				// neurons "capacity"
	
	public double[][] connections;					// average distance between neurons
	public double[][] connectConfidence;
	
	public ArrayList<float[]> flowVectorX;			// movement vector on each neuron
	public ArrayList<float[]> flowVectorY;
	public ArrayList<float[]> vectorConfidence;

	public Color[] m_tactileObject;
	public double[] sensorX;                        // position of sensor neurons
	public double[] sensorY;
	public double[] valueX;							// position of the detected point
	public double[] valueY;
	public double[] valueOldX;
	public double[] valueOldY;
	public double attraction,repulsion;
	
	public ErnestModel ernest;
	
	public int resolution;                              // nb of tactile sensors
	public int sensorRes;                               // nb of neurons per sensor
	
	public float chargeMap0[][][];
	public float chargeMap1[][][];
	public float chargeMapP[][][];
	public float objectMap[][][];
	
	public float potentialMap[][];
	public float potentialMap2[][][];
	public float potentialMapOld[][];
	public float potentialConfidenceMap[][];
	
	public boolean potentialTestMap[][];
	public boolean chargeTestMap[][];
	
	public ArrayList<float[][]> flowX1;				// real flow
	public ArrayList<float[][]> flowY1;
	public ArrayList<float[][]> flowX2;				// flow with reduced noise
	public ArrayList<float[][]> flowY2;
	public ArrayList<float[][]> confidenceFlow;
	
	public ArrayList<float[][][]> flowLineX1;		// flow line chains
	public ArrayList<float[][][]> flowLineY1;
	public ArrayList<float[][][]> flowLineX2;		// extrapolated flow line chains
	public ArrayList<float[][][]> flowLineY2;
	
	public int mapSize,mapPSize1,mapPSize2;
	public int flowLength;
	
	public ArrayList<Float> mTranslationX;
	public ArrayList<Float> mTranslationY;
	public ArrayList<Float> mRotation;
	
	public int counter;
	
	
	public TactileMap(ErnestModel e){
		resolution=18;
		sensorRes=3;
		ernest=e;
		
		initialize();
	}
	
	
	public TactileMap(ErnestModel e,int res, int sensor_res){
		resolution=res;
		sensorRes=sensor_res;
		ernest=e;
		
		initialize();
	}
	
	private void initialize(){
		mapSize=50;
		mapPSize1=90;
		mapPSize2=30;
		
		m_tactilePressure=new float[resolution*sensorRes];
		m_tactileValue=new float[resolution];
		m_tactilePressureOld=new float[resolution*sensorRes];
		m_tactileVariations=new float[resolution*sensorRes];
		flowVectorX=new ArrayList<float[]>();
		flowVectorY=new ArrayList<float[]>();
		vectorConfidence=new ArrayList<float[]>();
		m_tactileObject=new Color[resolution];
		m_constraints=new float[resolution*sensorRes][resolution*sensorRes];
		m_distances=new double[resolution*sensorRes][resolution*sensorRes];
		m_connectionsLenght=new float[resolution*sensorRes][resolution*sensorRes];
		connections=new double[resolution*sensorRes][resolution*sensorRes];
		connectConfidence =new double[resolution*sensorRes][resolution*sensorRes];
		sensorX=new double[resolution*sensorRes];
		sensorY=new double[resolution*sensorRes];
		valueX=new double[resolution];
		valueY=new double[resolution];
		valueOldX=new double[resolution];
		valueOldY=new double[resolution];
		attraction=0.001;
		
		
		chargeMap0=new float[mapSize][mapSize][3];
		chargeMap1=new float[mapSize][mapSize][3];
		chargeMapP=new float[mapPSize1][mapPSize2][3];
		objectMap=new float[mapSize][mapSize][3];
		potentialMap=new float[mapSize][mapSize];
		potentialMap2=new float[mapSize][mapSize][3];
		potentialMapOld=new float[mapSize][mapSize];
		potentialConfidenceMap=new float[mapSize][mapSize];
		potentialTestMap=new boolean[mapSize][mapSize];
		chargeTestMap=new boolean[mapSize][mapSize];
		flowX1=new ArrayList<float[][]>();
		flowY1=new ArrayList<float[][]>();
		flowX2=new ArrayList<float[][]>();
		flowY2=new ArrayList<float[][]>();
		confidenceFlow=new ArrayList<float[][]>();
		
		flowLength=60;
		flowLineX1=new ArrayList<float[][][]>();
		flowLineY1=new ArrayList<float[][][]>();
		flowLineX2=new ArrayList<float[][][]>();
		flowLineY2=new ArrayList<float[][][]>();
		
		mTranslationX=new ArrayList<Float>();
		mTranslationY=new ArrayList<Float>();
		mRotation    =new ArrayList<Float>();
		
		for (int i=0;i<resolution*sensorRes;i++){
			m_tactilePressure[i]=0;
			m_tactilePressureOld[i]=0;
			m_tactileVariations[i]=0;
			for (int j=0;j<resolution*sensorRes;j++){
				connections[i][j]=0;
				connectConfidence[i][j]=0;
				m_constraints[i][j]=0;
				m_distances[i][j]=0.5;				
				m_connectionsLenght[i][j]=0;
			}
			
			// initialize neurons positions
			//sensorX[i]= (float) (Math.random()*100-50);//-50*Math.sin(360/resolution*sensorRes*i*Math.PI/180);
			//sensorY[i]= (float) (Math.random()*100-50);// 50*Math.cos(360/resolution*sensorRes*i*Math.PI/180);
		}
		
		
		for (int j=0;j<sensorRes;j++){
			for (int i=0;i<resolution;i++){
				float r=0;
				if (j==0) r=10;
				if (j==1) r=20;
				if (j==2) r=30;
				sensorX[i+j*resolution]= -(r)*Math.sin(360/resolution*i*Math.PI/180);
				sensorY[i+j*resolution]=  (r)*Math.cos(360/resolution*i*Math.PI/180);
			}
		}
		
		for (int i=0;i<mapSize;i++){
			for (int j=0;j<mapSize;j++){
				chargeMap0[i][j][0]=0;
				chargeMap0[i][j][1]=0;
				chargeMap0[i][j][2]=0;
				
				chargeMap1[i][j][0]=0;
				chargeMap1[i][j][1]=0;
				chargeMap1[i][j][2]=0;
				
				potentialMap[i][j]=0;
				
				potentialMap2[i][j][0]=0;
				potentialMap2[i][j][1]=0;
				potentialMap2[i][j][2]=0;
				potentialTestMap[i][j]=false;
				chargeTestMap[i][j]=false;
				
			}
		}
		
		for (int i=0;i<mapPSize1;i++){
			for (int j=0;j<mapPSize2;j++){
				chargeMapP[i][j][0]=0;
				chargeMapP[i][j][1]=0;
				chargeMapP[i][j][2]=0;
			}
		}
		
		counter=0;
	}
	

	
	
	public void touchEnvironment(double[] r,Color[] c, int act,float speed){
		
		
		for (int i=0;i<resolution*sensorRes;i++){
			m_tactilePressureOld[i]=m_tactilePressure[i];
		}
		for (int i=0;i<resolution;i++){
			valueOldX[i]=valueX[i];
			valueOldY[i]=valueY[i];
		}
		
		
		////////////////////////////////////////////////////////
		// set sensors values
		////////////////////////////////////////////////////////
		
		// sensors around ernest
		senseAround(r,c);
		
		// sensors in front of ernest
		//senseFront(r,c);
		
		
		
		///////////////////////////////////////////////////////////////
		// place neurons on the map
		///////////////////////////////////////////////////////////////
		double dist,dist2,dist3;
		double a,b;
		
		float capacity=500; 
		
        // compute neuron "capacitor"
        for (int i=0;i<resolution*sensorRes;i++){
                if (m_tactilePressure[i] > m_tactilePressureOld[i])      m_tactileVariations[i]= capacity;
                else if (m_tactilePressure[i] < m_tactilePressureOld[i]) m_tactileVariations[i]=-capacity;
                else{
                        if (m_tactileVariations[i] > 0) m_tactileVariations[i]--;
                        else if (m_tactileVariations[i] < 0) m_tactileVariations[i]++;
                        
                        //m_tactileVariations[i]=m_tactileVariations[i]*(float)0.9;
                }
        }
        
		/*
        // compute relation between neurons
        for (int i=0;i<resolution*sensorRes;i++){
                for (int j=0;j<resolution*sensorRes;j++){
                        if (i!=j){
                                if ( (m_tactileVariations[i]== capacity && m_tactileVariations[j]>0) 
                                  || (m_tactileVariations[i]==-capacity && m_tactileVariations[j]<0) ){
                                        
                                        connections[i][j]= (connections[i][j]*connectConfidence[i][j]
                                                           + Math.abs(m_tactileVariations[j]))/(connectConfidence[i][j]+1);
                                        if (connectConfidence[i][j]<10000) connectConfidence[i][j]++;
                                }
                        }
                }
        }
		
        /*
		// change distance between neuron
		for (int k=0;k<5;k++){
			for (int i=0;i<resolution*sensorRes;i++){
				int i2= (int) (i/resolution);
				int i3= i-i2*resolution;
				for (int j=0;j<resolution*sensorRes;j++){
					int j2= (int) (j/resolution);
					int j3= j-j2*resolution;
					if (i!=j && i3!=j3 && connections[i][j]>200){
						dist2= (sensorX[i]-sensorX[j])*(sensorX[i]-sensorX[j]) + (sensorY[i]-sensorY[j])*(sensorY[i]-sensorY[j]);
						dist = Math.sqrt(dist2);
						dist3=dist/5;
						
						sensorX[i]+= ((capacity-connections[i][j])/10-dist3)
						              *( (sensorX[i]-sensorX[j]) / dist )*attraction;
							
						sensorY[i]+= ((capacity-connections[i][j])/10-dist3)
						              *( (sensorY[i]-sensorY[j]) / dist )*attraction;
						
						sensorX[j]-= ((capacity-connections[i][j])/10-dist3)
			              *( (sensorX[i]-sensorX[j]) / dist )*attraction;
				
						sensorY[j]-= ((capacity-connections[i][j])/10-dist3)
			              *( (sensorY[i]-sensorY[j]) / dist )*attraction;
							
						//m_constraints[i][j]=(float) ((40-connections[i][j])*m_distances[i][j]-dist3);
						
					}
				}
			}
		}
		normalize();
		/*
		// change distance scale according to the distance from the center
		for (int i=0;i<resolution*sensorRes;i++){
			for (int j=0;j<resolution*sensorRes;j++){
				
				dist2= (sensorX[i]-sensorX[j])*(sensorX[i]-sensorX[j]) + (sensorY[i]-sensorY[j])*(sensorY[i]-sensorY[j]);
				m_connectionsLenght[i][j] = (float) Math.sqrt(dist2);
				
				for (int k=0;k<resolution*sensorRes;k++){
					if (i!=j && i!=k && j!=k){
						
						dist2= (sensorX[i]-sensorX[k])*(sensorX[i]-sensorX[k]) + (sensorY[i]-sensorY[k])*(sensorY[i]-sensorY[k]);
						m_connectionsLenght[i][k] = (float) Math.sqrt(dist2);
						
						a= -( (sensorX[j]-sensorX[i])*(sensorX[k]-sensorX[i]) + (sensorX[j]-sensorX[i])*(sensorX[k]-sensorX[i]) )
						   / (m_connectionsLenght[i][j]*m_connectionsLenght[i][k]);
						
						if (m_constraints[i][j]*m_constraints[i][k]>0){
							if (m_constraints[i][j]>0) b= Math.min( m_constraints[i][j], m_constraints[i][k]);
							else                       b=-Math.min(-m_constraints[i][j],-m_constraints[i][k]);
						}
						else b=0;
						
						if (a>0 && b!=0){
							m_distances[i][j]+=0.0000001*a*b;
							m_distances[i][k]+=0.0000001*a*b;
						}
						else{
							if (m_distances[i][j]<1) m_distances[i][j]+=0.0000001;
							else          			 m_distances[i][j]-=0.0000001;    
							
							if (m_distances[i][k]<1) m_distances[i][k]+=0.0000001;
							else          			 m_distances[i][k]-=0.0000001;
						}
					}
				}
			}
		}*/
		
		///////////////////////////////////////////////////////
		// compute flow
		///////////////////////////////////////////////////////
		
		// add new flow map
		if (flowX1.size()<act+1){
			while (flowX1.size()<act+1){
				flowX1.add(new float[mapSize][mapSize]);
				flowY1.add(new float[mapSize][mapSize]);
				flowX2.add(new float[mapSize][mapSize]);
				flowY2.add(new float[mapSize][mapSize]);
				confidenceFlow.add(new float[mapSize][mapSize]);
				
				flowVectorX.add(new float[resolution*sensorRes]);
				flowVectorY.add(new float[resolution*sensorRes]);
				vectorConfidence.add(new float[resolution*sensorRes]);
				
				flowLineX1.add(new float[mapSize][mapSize][flowLength]);
				flowLineY1.add(new float[mapSize][mapSize][flowLength]);
				flowLineX2.add(new float[mapSize][mapSize][flowLength]);
				flowLineY2.add(new float[mapSize][mapSize][flowLength]);
				
				mTranslationX.add((float) 0);
				mTranslationY.add((float) 0);
				mRotation.add((float) 0);
				
				for (int i=0;i<mapSize;i++){
					for (int j=0;j<mapSize;j++){
						flowX1.get(act)[i][j]=0;
						flowY1.get(act)[i][j]=0;
						flowX2.get(act)[i][j]=0;
						flowY2.get(act)[i][j]=0;
						confidenceFlow.get(act)[i][j]=0;
						for (int k=0;k<flowLength;k++){
							flowLineX1.get(act)[i][j][k]=0;
							flowLineY1.get(act)[i][j][k]=0;
							flowLineX2.get(act)[i][j][k]=0;
							flowLineY2.get(act)[i][j][k]=0;
						}
					}
				}
				
				for (int i=0;i<resolution*sensorRes;i++){
					flowVectorX.get(act)[i]=0;
					flowVectorY.get(act)[i]=0;
					vectorConfidence.get(act)[i]=0;
				}
			}
		}
		
		
		// reset maps
		for (int i=0;i<mapSize;i++){
			for (int j=0;j<mapSize;j++){
				potentialMapOld[i][j]=potentialMap[i][j];
				potentialMap[i][j]=0;
				potentialMap2[i][j][0]=0;
				potentialMap2[i][j][1]=0;
				potentialMap2[i][j][2]=0;
				potentialConfidenceMap[i][j]=0;
				potentialTestMap[i][j]=false;
				chargeTestMap[i][j]=false;
			}
		}
		
		
		
		// compute neuron flow
		double d=0;
		double vect,vx,vy;
		double dt=0;
		for (int i=0;i<resolution*sensorRes;i++){
			for (int j=resolution;j<resolution*2;j++){
				if (i!=j){
					if ( (m_tactileVariations[i]== capacity && m_tactileVariations[j]>0) 
                      || (m_tactileVariations[i]==-capacity && m_tactileVariations[j]<0)
                      /*|| (m_tactileVariations[j]==-capacity && m_tactileVariations[i]<0)
                      || (m_tactileVariations[j]==-capacity && m_tactileVariations[i]<0)*/){
						
						d=Math.sqrt( (sensorX[i]-sensorX[j])*(sensorX[i]-sensorX[j])
                                    +(sensorY[i]-sensorY[j])*(sensorY[i]-sensorY[j]) );
                                    
						dt=Math.abs(m_tactileVariations[i])-Math.abs(m_tactileVariations[j]);
                                    
						if (dt!=0 && d>0 && d<20 && speed>1){
                                    
							vect= d/dt;
                                    
							vx= (1/speed)*vect * (sensorX[i]-sensorX[j])/d;
							vy= (1/speed)*vect * (sensorY[i]-sensorY[j])/d;

							flowVectorX.get(act)[j]=  (float) ((flowVectorX.get(act)[j]*vectorConfidence.get(act)[j] + vx)
                                    						 / (vectorConfidence.get(act)[j] +1));
							
							flowVectorY.get(act)[j]=  (float) ((flowVectorY.get(act)[j]*vectorConfidence.get(act)[j] + vy)
                                            				 / (vectorConfidence.get(act)[j] +1));
                                    
							if (vectorConfidence.get(act)[j]<100000) vectorConfidence.get(act)[j]++;
                                   
						}
						
					}

				}
            }
		}
		
		
		// set potential and charge Map values
		float scale=200/mapSize;
		double x,y;
		double dx,dy;
		float val=0;
		float value=0;
		int ix,jy;
		
		/*
		// set position of the detected point
		for (int i=0;i<resolution;i++){
			valueX[i]=0;
			valueY[i]=0;
			if (m_tactileValue[i] >=0 && m_tactileValue[i] < 5){
				valueX[i]= (sensorX[i]*(5-m_tactileValue[i])/5 + sensorX[i+resolution]*m_tactileValue[i]/5);
				valueY[i]= (sensorY[i]*(5-m_tactileValue[i])/5 + sensorY[i+resolution]*m_tactileValue[i]/5);
			}
			if (m_tactileValue[i] >=5 && m_tactileValue[i] <= 10){
				valueX[i]= (sensorX[i+resolution]*(10-m_tactileValue[i])/5 + sensorX[i+resolution*2]*(m_tactileValue[i]-5)/5);
				valueY[i]= (sensorY[i+resolution]*(10-m_tactileValue[i])/5 + sensorY[i+resolution*2]*(m_tactileValue[i]-5)/5);
			}
		}
		
		
		// fill the potential and charge map
		for (int i=0;i<resolution*(sensorRes-1);i++){
			int res=5;
			if (i>=resolution*(sensorRes-2)) res=6;
			for (int j=0;j<res;j++){
				
				dx=(sensorX[i+resolution]-sensorX[i])/5;
				dy=(sensorY[i+resolution]-sensorY[i])/5;
					
				// /!\ only because difference between two sensor neurons is 5
				if (i<resolution) val=j;
				else if (i<resolution*2) val=j+5;
				else val=j+10;
					
				
				int i2= i%resolution;
				if (m_tactileValue[i2]<=val+1 && m_tactileValue[i2]>=val-1) value=1;
				//if (m_tactileValue[i2]<=val) value=1;
				else value=0;

				if (j==0){
					x= Math.min(49, Math.max(0, (sensorX[i]+100)/scale));
					y= Math.min(49, Math.max(0, (sensorY[i]+100)/scale));
				}
				else if (j==5){
					x= Math.min(49, Math.max(0, (sensorX[i+resolution]+100)/scale));
					y= Math.min(49, Math.max(0, (sensorY[i+resolution]+100)/scale));
				}
				else{
					x= Math.min(49, Math.max(0, (sensorX[i]+j*dx+100)/scale));
					y= Math.min(49, Math.max(0, (sensorY[i]+j*dy+100)/scale));
				}
				
				ix=(int) Math.round(x);
				jy=(int) Math.round(y);
				
				
				
				int dmax=2;
				for (int j2=-dmax;j2<=dmax;j2++){
					for (int k2=-dmax;k2<=dmax;k2++){
						if (x+j2<49 && x+j2>0 && y+k2<49 && y+k2>0){
							//d= (x-(ix+j2))*(x-(ix+j2)) + (y-(jy+k2))*(y-(jy+k2));
							//d=Math.sqrt(d)*10+0.01;
							
							potentialMap[ix+j2][jy+k2]=  (  potentialMap[ix+j2][jy+k2]*potentialConfidenceMap[ix+j2][jy+k2]
							                            +  value )
						                              /( potentialConfidenceMap[ix+j2][jy+k2]+ 1 );
							
							if (m_tactileObject[i2].equals(new Color(0,128,0))){
								potentialMap2[ix+j2][jy+k2][0]=  (  potentialMap2[ix+j2][jy+k2][0]*potentialConfidenceMap[ix+j2][jy+k2]
								                                +  value )
								                    	      /( potentialConfidenceMap[ix+j2][jy+k2]+ 1 );
								potentialMap2[ix+j2][jy+k2][1]=  (  potentialMap2[ix+j2][jy+k2][1]*potentialConfidenceMap[ix+j2][jy+k2] )
								                			  /( potentialConfidenceMap[ix+j2][jy+k2]+ 1);
								
							}
							else{
								potentialMap2[ix+j2][jy+k2][1]=  (  potentialMap2[ix+j2][jy+k2][1]*potentialConfidenceMap[ix+j2][jy+k2]
								                                +  value )
								                			  /( potentialConfidenceMap[ix+j2][jy+k2]+ 1);
								potentialMap2[ix+j2][jy+k2][0]=  (  potentialMap2[ix+j2][jy+k2][0]*potentialConfidenceMap[ix+j2][jy+k2] )
								                			  /( potentialConfidenceMap[ix+j2][jy+k2]+ 1);

							}
							
							potentialMap2[ix+j2][jy+k2][2]=  (  potentialMap2[ix+j2][jy+k2][2]*potentialConfidenceMap[ix+j2][jy+k2]
							                                +  (1-value) )
														  /( potentialConfidenceMap[ix+j2][jy+k2]+ 1);
							

							potentialConfidenceMap[ix+j2][jy+k2]++;

							potentialTestMap[ix+j2][jy+k2]=true;
						}
					}
				}
				chargeTestMap[ix][jy]=true;

			}
		}
		
		
		/*for (int i=0;i<resolution*sensorRes;i++){
			x= (int) Math.min(49, Math.max(0, Math.round((sensorX[i]+200)/scale)));
			y= (int) Math.min(49, Math.max(0, Math.round((sensorY[i]+200)/scale)));
			
			int dmax=4;
			for (int j=-dmax;j<=dmax;j++){
				for (int k=-dmax;k<=dmax;k++){
					if (x+j<50 && x+j>=0 && y+k<50 && y+k>=0){
						d= Math.sqrt( (float)(j*j+k*k) )-1;
						if (d<=dmax){
							if (d<=0){
								potentialMap[x+j][y+k]=  (  potentialMap[x+j][y+k]*potentialConfidenceMap[x+j][y+k]
							                               +  m_tactilePressure[i]        )
							                              /( potentialConfidenceMap[x+j][y+k]+ 1);
								
								if (m_tactileObject[i].equals(new Color(0,128,0))){
									potentialMap2[x+j][y+k][0]=  (  potentialMap2[x+j][y+k][0]*potentialConfidenceMap[x+j][y+k]
									                    	       +  m_tactilePressure[i]        )
									                    	      /( potentialConfidenceMap[x+j][y+k]+ 1);
									potentialMap2[x+j][y+k][1]=  (  potentialMap2[x+j][y+k][1]*potentialConfidenceMap[x+j][y+k] )
									                			  /( potentialConfidenceMap[x+j][y+k]+ 1);
								}
								else{
									potentialMap2[x+j][y+k][1]=  (  potentialMap2[x+j][y+k][1]*potentialConfidenceMap[x+j][y+k]
									                			   +  m_tactilePressure[i]        )
									                			  /( potentialConfidenceMap[x+j][y+k]+ 1);
									potentialMap2[x+j][y+k][0]=  (  potentialMap2[x+j][y+k][0]*potentialConfidenceMap[x+j][y+k] )
									                			  /( potentialConfidenceMap[x+j][y+k]+ 1);
								}
								
								potentialMap2[x+j][y+k][2]=  (  potentialMap2[x+j][y+k][2]*potentialConfidenceMap[x+j][y+k]
								                    			+  (1-m_tactilePressure[i])        )
								                    		   /( potentialConfidenceMap[x+j][y+k]+ 1);
								
								potentialConfidenceMap[x+j][y+k]++;
							}
							else{
								potentialMap[x+j][y+k]=  (  potentialMap[x+j][y+k]*potentialConfidenceMap[x+j][y+k]
							                               +  m_tactilePressure[i]*( 1/(float)d  )        )
							                              /( potentialConfidenceMap[x+j][y+k]+ 1/(float)d);
								
								if (m_tactileObject[i].equals(new Color(0,128,0))){
									potentialMap2[x+j][y+k][0]=  (  potentialMap2[x+j][y+k][0]*potentialConfidenceMap[x+j][y+k]
									                    	       +  m_tactilePressure[i]*( 1/(float)d  )        )
									                    	      /( potentialConfidenceMap[x+j][y+k]+ 1/(float)d);
									potentialMap2[x+j][y+k][1]=  (  potentialMap2[x+j][y+k][1]*potentialConfidenceMap[x+j][y+k] )
									                			  /( potentialConfidenceMap[x+j][y+k]+ 1/(float)d);
								}
								else{
									potentialMap2[x+j][y+k][1]=  (  potentialMap2[x+j][y+k][1]*potentialConfidenceMap[x+j][y+k]
									                			   +  m_tactilePressure[i]*( 1/(float)d  )        )
									                			  /( potentialConfidenceMap[x+j][y+k]+ 1/(float)d);
									potentialMap2[x+j][y+k][0]=  (  potentialMap2[x+j][y+k][0]*potentialConfidenceMap[x+j][y+k] )
									                			  /( potentialConfidenceMap[x+j][y+k]+ 1/(float)d);
								}
								
								potentialMap2[x+j][y+k][2]=  (  potentialMap2[x+j][y+k][2]*potentialConfidenceMap[x+j][y+k]
								                    			+  (1-m_tactilePressure[i])*( 1/(float)d  )        )
								                    		   /( potentialConfidenceMap[x+j][y+k]+ 1/(float)d);
								
								
								potentialConfidenceMap[x+j][y+k]+= 1/(float)d;
							}
							
							testMap[x+j][y+k]=true;

						}
					}
				}
			}
		}*/
		
		/*
		for (int i=0;i<mapSize;i++){
			for (int j=0;j<mapSize;j++){
				if (chargeTestMap[i][j]){
					chargeMap1[i][j][0]=potentialMap2[i][j][0];
					chargeMap1[i][j][1]=potentialMap2[i][j][1];
					chargeMap1[i][j][2]=potentialMap2[i][j][2];
				}			
			}
		}*/
		
		/*
		// compute flow
		float fx,fy;
		int l=1;
		for (int i=l;i<mapSize-l;i++){
			for (int j=l;j<mapSize-l;j++){
				
				boolean test1=true;
				boolean test2=true;
				
				d=Math.sqrt( (double)((i-25)*(i-25) + (j-25)*(j-25) ));
				
				if (Math.abs(potentialMapOld[i-l][j]-potentialMapOld[i+l][j])>0.005
						&& Math.abs(potentialMapOld[i][j]-potentialMapOld[i+l][j])>0.001
						  && Math.abs(potentialMapOld[i][j]-potentialMapOld[i-l][j])>0.001
						  && ( ( potentialMapOld[i-l][j]>=potentialMapOld[i][j] && potentialMapOld[i+l][j]<=potentialMapOld[i][j])
							 ||( potentialMapOld[i-l][j]<=potentialMapOld[i][j] && potentialMapOld[i+l][j]>=potentialMapOld[i][j]) )
 
						  && potentialMap[i  ][j]>0 && potentialMap[i  ][j]<1 && potentialMapOld[i  ][j]>0 && potentialMapOld[i  ][j]<1
						  && potentialMap[i+l][j]>0 && potentialMap[i+l][j]<1 && potentialMapOld[i+l][j]>0 && potentialMapOld[i+l][j]<1
						  && potentialMap[i-l][j]>0 && potentialMap[i-l][j]<1 && potentialMapOld[i-l][j]>0 && potentialMapOld[i-l][j]<1
						  
						  && potentialTestMap[i][j] && potentialTestMap[i-l][j] 
						  && potentialTestMap[i+l][j] && potentialTestMap[i][j-l] && potentialTestMap[i][j+l]
						  && speed>1){
					
					fx=  (1/speed)* ( (potentialMap[i][j]-potentialMapOld[i][j]) / (potentialMapOld[i-l][j]-potentialMapOld[i+l][j]) );
					
				}
				else{
					test1=false;
					fx=0;
				}
				
				if (Math.abs(potentialMapOld[i][j-l]-potentialMapOld[i][j+l])>0.005
						  && Math.abs(potentialMapOld[i][j]-potentialMapOld[i][j+l])>0.001
						  && Math.abs(potentialMapOld[i][j]-potentialMapOld[i][j-l])>0.001
						  && ( ( potentialMapOld[i][j-l]>=potentialMapOld[i][j] && potentialMapOld[i][j+l]<=potentialMapOld[i][j])
							 ||( potentialMapOld[i][j-l]<=potentialMapOld[i][j] && potentialMapOld[i][j+l]>=potentialMapOld[i][j]) )
						
							 
						  && potentialMap[i  ][j]>0 && potentialMap[i  ][j]<1 && potentialMapOld[i  ][j]>0 && potentialMapOld[i  ][j]<1
						  && potentialMap[i][j+l]>0 && potentialMap[i][j+l]<1 && potentialMapOld[i][j+l]>0 && potentialMapOld[i][j+l]<1
						  && potentialMap[i][j-l]>0 && potentialMap[i][j-l]<1 && potentialMapOld[i][j-l]>0 && potentialMapOld[i][j-l]<1
						  
						  && potentialTestMap[i][j] && potentialTestMap[i-l][j] && potentialTestMap[i+l][j] 
						  && potentialTestMap[i][j-l] && potentialTestMap[i][j+l]
						  && speed>1){
					
					fy=  (1/speed)* ( (potentialMap[i][j]-potentialMapOld[i][j]) / (potentialMapOld[i][j-l]-potentialMapOld[i][j+l]) );
					
				}
				else{
					test2=true;
					fy=0;
				}
				
				if (test1 && test2){
					
					if (fx!=0) flowX1.get(act)[i][j]= ( flowX1.get(act)[i][j]*confidenceFlow.get(act)[i][j] + fx ) 
					                      /(confidenceFlow.get(act)[i][j]+1);
					if (fy!=0) flowY1.get(act)[i][j]= ( flowY1.get(act)[i][j]*confidenceFlow.get(act)[i][j] + fy ) 
					                      /(confidenceFlow.get(act)[i][j]+1);
				
					if (confidenceFlow.get(act)[i][j]<50000) confidenceFlow.get(act)[i][j]++;
				}
				
			}
		}
		
		// reduce noise
		int count;
		float mx,my;
		int s=5;
		int k=act;
		int i3,j3;

		for (int i=s;i<mapSize-s;i++){
				for (int j=s;j<mapSize-s;j++){
					if (potentialTestMap[i][j]){
						flowX2.get(k)[i][j]=0;
						flowY2.get(k)[i][j]=0;
						count=0;
						mx=0;
						my=0;
						for (int i2=-s;i2<=s;i2++){
						for (int j2=-s;j2<=s;j2++){
							i3=i+i2;
							j3=j+j2;
							if (flowX1.get(k)[i3][j3]!=0 || flowY1.get(k)[i3][j3]!=0){
								count++;
								mx+=flowX1.get(k)[i3][j3];
								my+=flowY1.get(k)[i3][j3];
							}
						}
						}
					
						if (count>30 && (mx!=0 || my!=0)){
							mx=mx/(float)count;
							my=my/(float)count;
						
						
							flowX2.get(k)[i][j]=mx;
							flowY2.get(k)[i][j]=my;
						
						}
					}
				}
		}*/

		
			
		
		////////////////////////////////////////////////////////////////////////
		// compute average translation and rotation vectors
		////////////////////////////////////////////////////////////////////////
		/*
		count=0;
		int count2=0;
		mx=0;
		my=0;
		float mTheta=0;
		double theta0,theta1;
		for (int i=s;i<mapSize-s;i++){
			for (int j=s;j<mapSize-s;j++){
				if (potentialTestMap[i][j] && confidenceFlow.get(act)[i][j]>10){
					// translation
					mx+=flowX2.get(k)[i][j];
					my+=flowY2.get(k)[i][j];
					
					//rotation
					
					// theta0
					if ((j-mapSize/2)!=0){
						theta0= 2*Math.atan( (j-mapSize/2) 
								            / ( (i-mapSize/2) + Math.sqrt((i-mapSize/2)*(i-mapSize/2)+(j-mapSize/2)*(j-mapSize/2)) ) 
								           );
					}
					else{
						if ((i-mapSize/2)>=0) theta0=0;
						else                  theta0=Math.PI;
					}
					
					// theta1
					if ((j-mapSize/2)!=0){
						theta1= 2*Math.atan( (j-mapSize/2+flowY2.get(k)[i][j]) 
								            / ( (i-mapSize/2+flowX2.get(k)[i][j]) 
								               + Math.sqrt( (i-mapSize/2+flowX2.get(k)[i][j])*(i-mapSize/2+flowX2.get(k)[i][j])
								                           +(j-mapSize/2+flowY2.get(k)[i][j])*(j-mapSize/2+flowY2.get(k)[i][j]))
								              )
								           );
					}
					else{
						if ((i-mapSize/2)>=0) theta1=0;
						else                  theta1=Math.PI;
					}

					
					mTheta+= theta1-theta0;
					
					count++;
				}
			}
		}
		if (count>0){
			//if (act==0){
				mTranslationX.set(act, mx/(float)count);
				mTranslationY.set(act, my/(float)count);
			//}else{
				mRotation.set(act, mTheta/(float)count);
			//}
		}
		
		// set the extrapolated flow field
		fx=0;
		fy=0;
		for (int i=0;i<mapSize;i++){
			for (int j=0;j<mapSize;j++){
				if (!potentialTestMap[i][j]){
					
					fx= (float) ((float)(i-mapSize/2)*Math.cos(mRotation.get(act)) - (float)(j-mapSize/2)*Math.sin(mRotation.get(act)));
					fy= (float) ((float)(i-mapSize/2)*Math.sin(mRotation.get(act)) + (float)(j-mapSize/2)*Math.cos(mRotation.get(act)));
					
					fx-=(float)(i-mapSize/2);
					fy-=(float)(j-mapSize/2);
					
					flowX2.get(act)[i][j]=fx +mTranslationX.get(act);
					flowY2.get(act)[i][j]=fy +mTranslationY.get(act);
					
					
					// corrections near known flow
					count=0;
					count2=0;
					mx=0;
					my=0;
					for (int i2=-4;i2<4;i2++){
						for (int j2=-4;j2<4;j2++){
							if ( (i+i2)>=0 && (i+i2)<mapSize && (j+j2)>=0 && (j+j2)<mapSize){
								if (potentialTestMap[i+i2][j+j2]){
									mx+=flowX2.get(k)[i+i2][j+j2];
									my+=flowY2.get(k)[i+i2][j+j2];
									count++;
								}
								count2++;
							}
						}
					}
					
					if (count>1 && count2>1){
						count2=Math.max(count2/2,count);
						flowX2.get(act)[i][j]= (flowX2.get(act)[i][j]*(count2-count) + mx )/count2;
						flowY2.get(act)[i][j]= (flowY2.get(act)[i][j]*(count2-count) + my )/count2;
					}
					
				}
			}
		} /* */
		
		
		////////////////////////////////////////////////////////////////////////
		// move charges
		////////////////////////////////////////////////////////////////////////
		/*
		mx=my=0;
		d=0;
		float countD=0;
		float chargeSum0=0;
		float chargeSum1=0;
		float chargeSum2=0;
		for (int i=0;i<mapSize;i++){
			for (int j=0;j<mapSize;j++){
				
				//if (!testMap[i][j]){
					mx=(float)i-flowX2.get(act)[i][j]*speed;
					my=(float)j-flowY2.get(act)[i][j]*speed;
			
					ix=Math.round(mx);
					jy=Math.round(my);
				
					chargeSum0=0;
					chargeSum1=0;
					chargeSum2=0;
					countD=0;
					for (int i2=-1;i2<=1;i2++){
						for (int j2=-1;j2<=1;j2++){
							if (ix+i2>=0 && ix+i2<mapSize && jy+j2>=0 && jy+j2<mapSize){
								d= ((float)(ix+i2)-mx)*((float)(ix+i2)-mx) 
							      +((float)(jy+j2)-my)*((float)(jy+j2)-my);
								d=Math.min(1,Math.sqrt(d));
								chargeSum0+=chargeMap0[ix+i2][jy+j2][0]*(1-d);
								chargeSum1+=chargeMap0[ix+i2][jy+j2][1]*(1-d);
								chargeSum2+=chargeMap0[ix+i2][jy+j2][2]*(1-d);
								countD+=(1-d);
							}
						}
					}
				
					if (countD>0){
						chargeSum0=chargeSum0/countD;
						chargeSum1=chargeSum1/countD;
						chargeSum2=chargeSum2/countD;
						if (!chargeTestMap[i][j]){
							chargeMap1[i][j][0]=(float) Math.min(1,chargeSum0);
							chargeMap1[i][j][1]=(float) Math.min(1,chargeSum1);
							chargeMap1[i][j][2]=(float) Math.min(1,chargeSum2);
						}
						else{
							chargeMap1[i][j][0]=potentialMap2[i][j][0];
							chargeMap1[i][j][1]=potentialMap2[i][j][1];
							chargeMap1[i][j][2]=potentialMap2[i][j][2];
						}
					}
				//}

			}
		}/**/ /*
		for (int i=0;i<mapSize;i++){
			for (int j=0;j<mapSize;j++){
				chargeMap0[i][j][0]=Math.min(1,chargeMap1[i][j][0]);
				chargeMap0[i][j][1]=Math.min(1,chargeMap1[i][j][1]);
				chargeMap0[i][j][2]=Math.min(1,chargeMap1[i][j][2]);
				chargeMap1[i][j][0]=0;
				chargeMap1[i][j][1]=0;
				chargeMap1[i][j][2]=0;		
			}
		}
		
		////////////////////////////////////////////////////////////////////////
		// generate polar map
		////////////////////////////////////////////////////////////////////////
		
		double Sum0,Sum1;
		float px,py;
		for (int i=0;i<90;i++){
			for (int j=0;j<30;j++){
				
				px=(float) ((double)j*Math.cos( ((double)i*4+90)*Math.PI/180))+25;
				py=(float) ((double)j*Math.sin( ((double)i*4+90)*Math.PI/180))+25;
				
				ix=Math.round(px);
				jy=Math.round(py);
				
				if (ix>=0 && jy>=0 && ix<50 && jy<50){
					
					Sum0=0;
					Sum1=0;
					countD=0;
					for (int i2=-1;i2<=1;i2++){
						for (int j2=-1;j2<=1;j2++){
							if (ix+i2>=0 && ix+i2<50 && jy+j2>=0 && jy+j2<50){
								d= ((float)(ix+i2)-px)*((float)(ix+i2)-px) 
								  +((float)(jy+j2)-py)*((float)(jy+j2)-py);
								d=Math.min(1,Math.sqrt(d));
								Sum0+=chargeMap0[ix+i2][jy+j2][0]*(1-d);
								Sum1+=chargeMap0[ix+i2][jy+j2][1]*(1-d);
								countD+=(1-d);
							}
						}
					}
					
					chargeMapP[i][j][0]=(float)(Sum0/countD);
					chargeMapP[i][j][1]=(float)(Sum1/countD);
				}
			}
		}/* */
		
		

		
	}


	public void senseAround(double[] r,Color[] c){
		float distance,distance2;
		int angle=360/resolution;
		
		int E_angle=ernest.m_orientation+540;
		int index=0;
		for (int i=0;i<360;i+=angle){
			distance=(float) r[(i+E_angle)%360];
			for (int j=0;j<sensorRes;j++){
				
				index=i/angle;
				m_tactileValue[index]=Math.min(16, distance-5);
				m_tactileObject[index]=c[(i+E_angle)%360];
				
				if (j==0){
					index=i/angle;
					if (distance<=5){
						distance2=1;
						//distance2= Math.min(1,1- (distance-5)/5);
						m_tactilePressure[index]= distance2;
					}
					else{
						m_tactilePressure[index]=0;
					}
				}
				else if (j==1){
					index=i/angle+resolution;
					if (distance<=10){
						distance2=1;
						//distance2=(float) Math.min(1,1- (distance-7)/5);
						m_tactilePressure[index]= distance2;
					}
					else{
						m_tactilePressure[index]=0;
					}
				}
				else{
					index=i/angle+2*resolution;
					if (distance<=15){
						distance2=1;
						//distance2=(float) Math.min(1,1- (distance-10)/5);
						m_tactilePressure[index]= distance2;
					}
					else{
						m_tactilePressure[index]=0;
					}
				}
			}
		}
	}
	
	
	public void senseFront(double[] r,Color[] c){
		float distance,distance2;
		int angle=180/resolution;
		int E_angle=ernest.m_orientation+630;  // 630 = -90 +720
		for (int i=0;i<180;i+=angle){
			distance=(float) r[(i+E_angle)%360];
			for (int j=0;j<sensorRes;j++){
				if (j==0){
					if (distance<=10){
						//distance2=1;
						distance2= Math.min(1,1- (distance-5)/5);
						m_tactilePressure[i/angle]= distance2;
						m_tactileObject[i/angle]=c[(i+E_angle)%360];
					}
					else{
						m_tactilePressure[i/angle]=0;
						m_tactileObject[i/angle]=Color.black;
					}
				}
				else if (j==1){
					if (distance<=12){
						//distance2=1;
						distance2=(float) Math.min(1,1- (distance-7)/5);
						m_tactilePressure[i/angle+resolution]= distance2;
						m_tactileObject[i/angle+resolution]=c[(i+E_angle)%360];
					}
					else{
						m_tactilePressure[i/angle+resolution]=0;
						m_tactileObject[i/angle+resolution]=Color.black;
					}
				}
				else{
					if (distance<=15){
						//distance2=1;
						distance2=(float) Math.min(1,1- (distance-10)/5);
						m_tactilePressure[i/angle+2*resolution]= distance2;
						m_tactileObject[i/angle+2*resolution]=c[(i+E_angle)%360];
					}
					else{
						m_tactilePressure[i/angle+2*resolution]=0;
						m_tactileObject[i/angle+2*resolution]=Color.black;
					}
				}
			}
		}
	}
	
	// normalize position and angle of the neuron network
	public void normalize(){
		float mx=0;
		float my=0;
		double d;
		// compute average position
		for (int i=0;i<resolution*sensorRes;i++){
			mx+=sensorX[i];
			my+=sensorY[i];
			
		}
		mx=mx/(resolution*sensorRes);
		my=my/(resolution*sensorRes);
		
		//
		for (int i=0;i<resolution*sensorRes;i++){
			sensorX[i]-=mx;
			sensorY[i]-=my;
		}
		d=Math.sqrt( sensorX[resolution/2]*sensorX[resolution/2] + sensorY[resolution/2]*sensorY[resolution/2]);
		double angle= Math.sin(sensorX[resolution/2]/d);
		
		for (int i=0;i<resolution*sensorRes;i++){
			sensorX[i]= sensorX[i]*Math.cos(-angle) - sensorY[i]*Math.sin(-angle);
			sensorY[i]= sensorX[i]*Math.sin(-angle) + sensorY[i]*Math.cos(-angle);
		}
	}
	
	
}