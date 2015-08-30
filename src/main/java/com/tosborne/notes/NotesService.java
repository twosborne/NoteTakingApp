package com.tosborne.notes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Root resource (exposed at "/api" path),
 * this object performs the CRUD operations for the caller's notes.
 * 
 */
@Path("/api")
public class NotesService 
{
	private static AtomicInteger currentId = new AtomicInteger(0);
	/** 
	 * The notes data.  
	 * The key for the map is the ID of the individual note; 
	 * the value for the map is the object representing the note. 
	 */
	private static ArrayList<NoteBean> theNotes = new ArrayList<NoteBean>();

    /**
     * Retrieves a specific note by ID.
     *
     * @param	id		the id of the note to retrieve
     * @return 	the note that is referred to by the given id.
     * 			A null is returned if there is no note for the given id.
     */
    @GET
    @Path("/notes/{id: \\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public NoteBean getNote( @PathParam("id") int id ) 
    {
    	synchronized(theNotes)
    	{
    		// If we ever add a modify/update/delete to this service,
    		// this will need to be revisited.  
    		// 1. This expects the notes to be in order, starting at 1,
    		//    which is currently true.  A delete function
    		//    would kill that.
    		// 2, There is no specific concurrency protection against 
    		//    someone updating the note while the framework is 
    		//    converting it from bean to JSON.
    		// That's currently not an issue and fixing it would slow
    		// the processing down, so I'm not stressing over it yet.
    		// Fix it if/when it is necessary.
    		if (id > 0 && id <= theNotes.size())
    		{
    			return theNotes.get(id-1);
    		}
    	}
    	return null;
    }

    /**
     * Retrieves the full list of notes that have been created thus far.
     *
     * @param	query	optional input parameter; if specified, instead of a full
     * 					list of notes being returned, only those notes that match
     * 					the query are returned
     * @return 	all of the notes (if query is not specified), or all of the notes
     * 			whose body includes the text specified in query (if it is).
     * 			An empty collection is returned if there are no notes to return.
     */
	@GET
    @Path("/notes")
    @Produces(MediaType.APPLICATION_JSON)
    public List<NoteBean> getAllNotes( @QueryParam("query") String query ) 
    {
    	List<NoteBean> retVal = new ArrayList<NoteBean>();
    	
    	synchronized (theNotes) 
    	{
    		for (NoteBean bean : theNotes)
    		{
    			if (query == null || bean.getBody().contains(query))
				{
    				// As above, if we add an update capability, we
    				// may want to clone the beans as we put them into
    				// the collection being returned, to avoid possible
    				// concurrency issues
    				retVal.add(bean);
				}
    		}
    		return retVal;
		}
    }
    
    /**
     * Creates a new note and adds it to the collection of notes being kept.
     * This includes setting a unique ID on the note, and saving it into cache.
     *
     * @param	bean	the data to add to the collection of beans
     * @return 	the bean, updated to include the ID
     */
    @POST
    @Path("/notes")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public NoteBean saveNote(NoteBean bean) 
    {
    	int newId = currentId.incrementAndGet();
		bean.setId(newId);
		
    	synchronized (theNotes) 
		{
			theNotes.add(bean);
		}
		return bean;
	}

    /**
     * Creates a new note and adds it to the collection of notes being kept.
     * This is the same as saveNote, but for those who thoughtlessly forget to
     * set the Content-Type in their HTTP header to 'application/json'.
     * Since the assignment is set up that way, I felt I had to add this version
     * in, along with the one I would have expected.
     *
     * @param	str		a string version of the JSON
     * @return 	the bean, updated to include the ID.  A null is returned
     * 			if an exception is found while processing the input
     * 			JSON string.
     */
    @POST
    @Path("/notes")
	public NoteBean saveTextNote(String str)
    {
    	int newId = currentId.incrementAndGet();
    	try
    	{
			@SuppressWarnings("unchecked")
			HashMap<String,Object> result =
			        new ObjectMapper().readValue(str, HashMap.class);
	
			NoteBean bean = new NoteBean(newId, ""+result.get("body"));
			
	    	synchronized (theNotes) 
			{
				theNotes.add(bean);
			}
	    	return bean;
    	}
    	catch(IOException e)
    	{
        	return null;
    	}
	}
}
