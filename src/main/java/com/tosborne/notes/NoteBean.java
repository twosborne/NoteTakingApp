package com.tosborne.notes;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Simple java bean representing an individual note.
 */
@XmlRootElement
public class NoteBean 
{
	/** Unique identifier for the note */
	int id;
	/** Text of the note */
	String body;

	/** Default constructor */
	public NoteBean()
	{
		this(0, "");
	}
	/** 
	 * Utility constructor that fully populates the object.
	 * 
	 * @param newId		initial value for the 'id' attribute
	 * @param newBody	initial value for the 'body' attribute
	 */
	public NoteBean(int newId, String newBody)
	{
		id   = newId;
		body = newBody;
	}
	/**
	 * Simple accessor, returning the unique identifier for the note.
	 * @return	the current value of the 'id' attribute of this object
	 */
	public int getId() 
	{
		return id;
	}
	/**
	 * Simple update function, setting the unique identifier for the note
	 * @param 	id		value to place in the 'id' attribute of this object
	 */
	public void setId(int id) 
	{
		this.id = id;
	}
	/**
	 * Simple accessor, returning the text of the note.
	 * @return	the current value of the 'body' attribute of this object
	 */
	public String getBody() 
	{
		return body;
	}
	/**
	 * Simple update function, setting the text of the note
	 * @param 	id		value to place in the 'body' attribute of this object
	 */
	public void setBody(String body) 
	{
		this.body = body;
	}
	/**
	 * Standard toString function, returning a human readable version
	 * of this object and its contents.
	 */
	public String toString()
	{
		return " NoteBean{ id=" + id + ", body='" + body + "' } ";
	}
	/**
	 * Standard method used to test equality between this instance
	 * and another.
	 */
	public boolean equals(Object other)
	{
		if (!(other instanceof NoteBean))
		{
			return false;
		}
		NoteBean that = (NoteBean)other;
		return ( getId() == that.getId() && getBody().equals(that.getBody()) );
	}
}
