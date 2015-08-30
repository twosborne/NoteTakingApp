package com.tosborne.notes;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Main class.  Starts a little stub server.
 */
public class Main 
{
    public static final String BASE_URI = "http://localhost:8080/";
    public static final String PACKAGES = "com.tosborne.notes";

    /**
     * Standard main method.
     * 
     * @param 	args	command line parameters entered by the user
     * @throws IOException
     */
    public static void main(String[] args) throws IOException 
    {
        final ResourceConfig rc = new ResourceConfig().packages(PACKAGES);
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
        System.out.println("Test app started with WADL available at " + BASE_URI 
                			+ "application.wadl\nHit enter to stop it...");
        System.in.read();
        server.shutdownNow();
    }
}

