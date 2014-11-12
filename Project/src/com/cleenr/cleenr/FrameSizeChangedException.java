package com.cleenr.cleenr;

import org.opencv.core.Size;

public class FrameSizeChangedException extends Exception 
{
	private Size mOldSize, mNewSize;
	
	/**
	 *  Totally randomly generated
	 */
	private static final long serialVersionUID = -6688928905936399759L;
	public FrameSizeChangedException(int oldWidth, int oldHeight, int newWidth, int newHeight)
	{
		this(new Size(oldWidth, oldHeight), new Size(newWidth, newHeight));
	}
	public FrameSizeChangedException(Size oldSize, Size newSize)
	{
		mOldSize = oldSize;
		mNewSize = newSize;
	}
	public Size getOldSize()
	{
		return mOldSize;
	}
	public Size getNewSize()
	{
		return mNewSize;
	}

}
