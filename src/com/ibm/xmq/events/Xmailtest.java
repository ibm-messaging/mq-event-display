package com.ibm.xmq.events;

import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Iterator;


import com.ibm.xmq.events.EMailer;

/**
 * __  __                _ _ _            _   
 * \ \/ /_ __ ___   __ _(_) | |_ ___  ___| |_ 
 *  \  /| '_ ` _ \ / _` | | | __/ _ \/ __| __|
 *  /  \| | | | | | (_| | | | ||  __/\__ \ |_ 
 * /_/\_\_| |_| |_|\__,_|_|_|\__\___||___/\__|
 *
 *                    
 * Xmailtest - Display Statistics about IBM WebSphere MQ Events
 * 
 * Xmailtest reads an IBM WebSphere MQ event queue (SYSTEM.ADMIN.*) and
 * generates various statistics about the generated events.
 *
 */
public class Xmailtest {
	
	public static final String progName = "Xmailtest";
	public static final String progVersion = "1.2";
	public static final String progAuthor = "Oliver Fisse (IBM)";
	public static final String progAuthorEmail = "fisse@us.ibm.com";
	public static final String progCopyright = "Copyright (c) IBM Corp. 2012-2013, all rights reserved";
	
    private String emailConfigFileName;
	
   
	/**
	 * Constructor
	 */
	public Xmailtest() {
		
		this.emailConfigFileName = null;
	} // end of constructor
	
	
	/**
	 * Parse command line arguments
	 * 
	 * @param args Command line arguments
	 */
	private int parseArgs(String[] args) {
		
		int argc = args.length;
		char c = ' ';
		
		// Display usage if no argument passed or ? is passed
		if (argc == 0 || (argc == 1 && args[0].compareTo("?") == 0)) {
			usage();
			return 99;
		}
		
		System.out.println(Xmailtest.progName + " v" + Xmailtest.progVersion + " - Developed by " + Xmailtest.progAuthor);
		System.out.println();
		
		// Parse arguments
		for (int i = 0; i < argc; i++) {
			
			if (args[i].startsWith("-")) {
				c = args[i].charAt(1);
				
				switch (c) {
					case 'e':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							return 98;
						} 
						this.emailConfigFileName = args[i + 1];
						i++;
						break;
					default: 
						System.err.println(args[i] + " is not a valid option");
						return 98;
				} // end switch
			} // end if
        } // end for
		
		if (this.emailConfigFileName == null) {
			System.err.println("Missing option -e, email configuration file name must be specified!");
			return 98;
		}
		
		return 0;
	} // end of method parseArgs()
	
	/**
	 * Run the utility
	 * 
	 * @param args Command line arguments
	 */
	private int run(String[] args) {
		
		EMailer em = null;
		Iterator it = null;
		
		int rc = 0;
		
		if ((rc = parseArgs(args)) == 0) {
			
			try {
				System.out.println("Reading configuration file '" + this.emailConfigFileName +"'...");
				em = new EMailer(this.emailConfigFileName);
			} catch (IOException ioe) {
				System.err.println("IOException while opening file '" + this.emailConfigFileName + "'!");
				System.err.println(ioe.getLocalizedMessage());
				return 99;
			}
			
			System.out.println();
			
			System.out.println(" Host: " + em.getHost());
			System.out.println(" Port: " + em.getPort());
			System.out.println(" Auth: " + em.isAuthentication());
			System.out.println(" User: " + em.getUser());
			System.out.println("  PWD: " + em.getPassword());
			System.out.println("  SSL: " + em.isEnableSSL());
			System.out.println("  TLS: " + em.isStartTLS());
			System.out.println("Debug: " + em.isDebug());
			
			System.out.println();
			
			System.out.println(" From: " + em.getFrom());
			
			System.out.print("   To: ");
			if (em.getToCount() == 0) System.out.println();
			else {
				it = em.getTo();
				while (it.hasNext()) {
					System.out.println((String)it.next());
					System.out.print("       ");
				} // end while
				
				System.out.println();
			} // end if
			
			System.out.print("   Cc: ");
			if (em.getCcCount() == 0) System.out.println();
			else {
				it = em.getCc();
				while (it.hasNext()) {
					System.out.println((String)it.next());
					System.out.print("       ");
				} // end while
				
				System.out.println();
			} // end if
			
			System.out.print("  Bcc: ");
			if (em.getBccCount() == 0) System.out.println();
			else {
				it = em.getBcc();
				while (it.hasNext()) {
					System.out.println((String)it.next());
					System.out.print("       ");
				} // end while
				
				System.out.println();
			} // end if
			
			System.out.println();
			System.out.println("Sending test e-mail...");
			em.setSubject("Test message from Xmailtest");
			em.sendMail("This is a test message from Xmailtest!");
				
			System.out.println();	
			System.out.println(Xmailtest.progName + " v" + Xmailtest.progVersion + " ended.");
		} // end if
		
		return rc;
	} // end of method run()
	
	/**
	 * Display usage
	 * 
	 */
	private static void usage() {
	
        System.out.println("__  __                _ _ _            _");   
        System.out.println("\\ \\/ /_ __ ___   __ _(_) | |_ ___  ___| |_"); 
        System.out.println(" \\  /| '_ ` _ \\ / _` | | | __/ _ \\/ __| __|");
        System.out.println(" /  \\| | | | | | (_| | | | ||  __/\\__ \\ |_"); 
        System.out.println("/_/\\_\\_| |_| |_|\\__,_|_|_|\\__\\___||___/\\__|");
        System.out.println();
        
        System.out.println(Xmailtest.progName + " v" + Xmailtest.progVersion + " - Test Mail Configuration File");
		System.out.println(Xmailtest.progCopyright);
		System.out.println();
		
        System.out.println(Xmailtest.progName + " tests mail configuration file by sending a sample e-mail.");
        System.out.println();
        
        System.out.println("Usage: " + Xmailtest.progName + " -e email-config");
        System.out.println();
        
        System.out.println("Options:");
        System.out.println("    -e email-config  Email configuration file name");
        System.out.println();
        
        System.out.println("Send bug reports, comments, etc... to " + Xmailtest.progAuthor + " at " + Xmailtest.progAuthorEmail);
		System.out.println();
        
        System.out.println("This tool is provided in good faith and AS-IS. There is no warranty");
        System.out.println("or further service implied or committed via IBM product service channels.");
	} // end of method usage()

	/**
	 * Main
	 * 
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		
		int retCode = 0;
		
		// Run the utility
		Xmailtest util = new Xmailtest();
		retCode = util.run(args);			
		
		System.exit(retCode);
	} // end of method main()

} // end of class Xmailtest
