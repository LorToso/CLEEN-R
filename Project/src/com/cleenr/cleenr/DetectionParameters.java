package com.cleenr.cleenr;

public class DetectionParameters {
	int nMinimumObjectSize 		= 100;
	//int nMaximumObjectSize 		= 100;		TODO: Implement this
	int nSaturationThreshold 	= 128;
	int nDarknessThreshold		= 50;
	
	public void lower()
	{
		nMinimumObjectSize*=0.9;
		nSaturationThreshold*=0.9;
	}
	public void increase()
	{
		nMinimumObjectSize*=1.1;
		nSaturationThreshold*=1.1;
	}
}
