package com.tosborne.notes;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tosborne.notes.Main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

/**
 * Simple JUnit unit test function.
 */
public class NotesTest 
{
    private HttpServer server;
    private WebTarget target;

    private static final HashMap<Integer, NoteBean> NOTES = new HashMap<Integer,NoteBean>()
    														{
																private static final long serialVersionUID = 1724381887920794257L;
																{ 
    																put(1, new NoteBean(1, "Pick up milk!"));
    																put(2, new NoteBean(2, "Pick up dry cleaning!"));
    																put(3, new NoteBean(3, "Pick up eggs!"));
    															}
															};
    private static final String BANNER_FMT = "\n\n*******************************\n%s\n*******************************\n\n"; 															
    @Before
    public void setUp() throws Exception 
    {
    	System.out.println(String.format(BANNER_FMT,  "setup"));    	

    	// start the server
        final ResourceConfig rc = new ResourceConfig().packages(Main.PACKAGES);
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(Main.BASE_URI), rc);
        
        // create the client
        Client c = ClientBuilder.newClient();

        target = c.target(Main.BASE_URI + "api/notes");
    }

    @After
    public void tearDown() throws Exception 
    {
    	System.out.println(String.format(BANNER_FMT,  "tearDown"));    	
        server.shutdownNow();
    }

    /**
     * Run the tests associated with this service.
     */
    @Test
    public void test() 
    {
    	testRetrievingFromEmptyColletion();	// Must run before testAdd
    	testAdd();				// Creates records used in later tests
    	testQueryById();
    	testGetAll();
    	testQuery();
    }
    /**
     * Test accessing data while the collection is still empty.
     * Expect empty returns.
     */
    public void testRetrievingFromEmptyColletion() 
    {
    	System.out.println(String.format(BANNER_FMT,  "testRetrievingFromEmptyColletion"));    	
    	// Test retrieve all as JSON while it is still empty
        {
        	GenericType<List<NoteBean>> responseType = new GenericType<List<NoteBean>>() {};
	        List<NoteBean> 	response = target.request().accept(MediaType.APPLICATION_JSON).get(responseType);
	
	        System.out.println("    Get all (expected []): " + response);    	
	        assertEquals(0, response.size());
        }
    	// Test retrieve a specific item as JSON before any are added
        {
        	GenericType<NoteBean> responseType = new GenericType<NoteBean>() {};
	        NoteBean       	response = target.path("/1").request().accept(MediaType.APPLICATION_JSON).get(responseType);
	
	        System.out.println("    Search response (expected null): " + response);    	
	        assertNull(response);
        }
    }
    
    /**
     * Test adding notes.
     */
    public void testAdd() 
    {
    	System.out.println(String.format(BANNER_FMT,  "testAdd"));    	
    	final String JSON_FMT = "{ \"body\" : \"%s\" }";
    	// Test adding a couple of notes
        Builder        	builder  = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
        String 			payload  = String.format(JSON_FMT, NOTES.get(1).getBody());  
        Response   		response = builder.post(Entity.json(payload));
        NoteBean		bean	 = response.readEntity(NoteBean.class);

        System.out.println("    Create response: " + bean);    	
        assertEquals(NOTES.get(1), bean);

        payload  = String.format(JSON_FMT, NOTES.get(2).getBody());  
        response = builder.post(Entity.json(payload));
        bean     = response.readEntity(NoteBean.class);

        System.out.println("    Create response: " + response);    	
        assertEquals(NOTES.get(2), bean);

        builder  = target.request(MediaType.TEXT_PLAIN).accept(MediaType.APPLICATION_JSON);
        payload  = String.format(JSON_FMT, NOTES.get(3).getBody());  
        response = builder.post(Entity.json(payload));
        bean     = response.readEntity(NoteBean.class);

        System.out.println("    Create response: " + response);    	
        assertEquals(NOTES.get(3), bean);
    }

    /**
     * Test retrieving notes by ID.
     */
    public void testQueryById() 
    {
    	System.out.println(String.format(BANNER_FMT,  "testQueryById"));    	
    	GenericType<NoteBean> responseType = new GenericType<NoteBean>() {};
        NoteBean       	response = target.path("/1").request().accept(MediaType.APPLICATION_JSON).get(responseType);

        System.out.println("    ID Lookup response (expected note 1): " + response);    	
        assertEquals(NOTES.get(1), response);

        response = target.path("/2").request().accept(MediaType.APPLICATION_JSON).get(responseType);
        System.out.println("    ID Lookup response (expected note 2): " + response);    	
        assertEquals(NOTES.get(2), response);

        response = target.path("/3").request().accept(MediaType.APPLICATION_JSON).get(responseType);
        System.out.println("    ID Lookup response (expected note 3): " + response);    	
        assertEquals(NOTES.get(3), response);

        response = target.path("/0").request().accept(MediaType.APPLICATION_JSON).get(responseType);
        System.out.println("    ID Lookup response (expected null): " + response);    	
        assertNull(response);

        response = target.path("/4").request().accept(MediaType.APPLICATION_JSON).get(responseType);
        System.out.println("    ID Lookup response (expected null): " + response);    	
        assertNull(response);
    }
    /**
     * Test retrieving all notes, or subsets based on a query string
     */
    public void testGetAll() 
    {
    	System.out.println(String.format(BANNER_FMT,  "testGetAll"));    	
    	GenericType<List<NoteBean>> responseType = new GenericType<List<NoteBean>>() {};
        List<NoteBean> 	response = target.request().accept(MediaType.APPLICATION_JSON).get(responseType);

        System.out.println("    Get all (expected 3): " + response);    
        validateGotAll(response);
    }

    /**
     * Test retrieving all notes, or subsets based on a query string
     */
    public void testQuery() 
    {
    	System.out.println(String.format(BANNER_FMT,  "testQuery"));    	
    	GenericType<List<NoteBean>> responseType = new GenericType<List<NoteBean>>() {};
        List<NoteBean> 	response = target.request().accept(MediaType.APPLICATION_JSON).get(responseType);

        response = target.queryParam("query", "up").request().accept(MediaType.APPLICATION_JSON).get(responseType);
        System.out.println("    Search for 'up' (expected 3): " + response);    	
        validateGotAll(response);

        response = target.queryParam("query", "!").request().accept(MediaType.APPLICATION_JSON).get(responseType);
        System.out.println("    Search for '!' (expected 3): " + response);    	
        validateGotAll(response);

        response = target.queryParam("query", "Pick").request().accept(MediaType.APPLICATION_JSON).get(responseType);
        System.out.println("    Search for 'Pick' (expected 3): " + response);    	
        validateGotAll(response);

        response = target.queryParam("query", "eggs").request().accept(MediaType.APPLICATION_JSON).get(responseType);
        System.out.println("    Search for 'eggs' (expected 1): " + response);    	
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(NOTES.get(3), response.get(0));

        response = target.queryParam("query", "pizza").request().accept(MediaType.APPLICATION_JSON).get(responseType);
        System.out.println("    Search for 'pizza' (expected none): " + response);    	
        assertNotNull(response);
        assertEquals(0, response.size());
    }

    /**
     * Compares the given list of beans to make sure we got all of them back,
     * not just some, and not the same one repeating...
     */
    private void validateGotAll(List<NoteBean> response) 
    {
        assertNotNull(response.size());
        assertEquals(NOTES.size(), response.size());
        for (NoteBean note : response)
        {
        	assertTrue( NOTES.containsValue(note) );
        }
        for (NoteBean note : NOTES.values())
        {
        	assertTrue( response.contains(note) );
        }
    }

}
