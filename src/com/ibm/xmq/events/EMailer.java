package com.ibm.xmq.events;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EMailer {

	private String host;
    private String port;
    private boolean authentication;
    private boolean debug;
    private boolean debugAuth;
    private boolean enableSSL;
    private boolean startTLS;
    private String user;
    private String password;
    private String from;
    private ArrayList to;
    private ArrayList cc;
    private ArrayList bcc;
    private String subject;

    public EMailer() {
    	
        host = "localhost";
        port = "25";
        user = "";
        password = "";
        authentication = false;
        debug = false;
        debugAuth = false;
        enableSSL = false;
        startTLS = false;
        from = "";
        to = new ArrayList(10);
        cc = new ArrayList(5);
        bcc = new ArrayList(2);
        subject = "";
    } // end of constructor

    public EMailer(String emailerConfigFile)
        throws IOException {
    
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(emailerConfigFile);
        
        // Load configuration file
        props.load(fis);
        fis.close();
        
        // Set values and default values if needed
        host = props.getProperty("mail.host", "localhost");
        port = props.getProperty("mail.port", "25");
        authentication = props.getProperty("mail.auth", "false").toLowerCase().startsWith("true");
        debug = props.getProperty("mail.debug", "false").toLowerCase().startsWith("true");
        debugAuth = props.getProperty("mail.debug.auth", "false").toLowerCase().startsWith("true");
        enableSSL = props.getProperty("mail.ssl", "false").toLowerCase().startsWith("true");
        startTLS = props.getProperty("mail.starttls", "false").toLowerCase().startsWith("true");
        user = props.getProperty("mail.user", "");
        password = props.getProperty("mail.password", "");
        from = props.getProperty("mail.from", "");
        
        this.to = new ArrayList(10);
        String to = props.getProperty("mail.to", "");
        if (to.length() > 0) {
            String toArr[] = to.split(",");
            for(int i = 0; i < toArr.length; i++)
                this.to.add(toArr[i]);
        } // end if
        
        this.cc = new ArrayList(5);
        String cc = props.getProperty("mail.cc", "");
        if(cc.length() > 0) {
            String ccArr[] = cc.split(",");
            for(int i = 0; i < ccArr.length; i++)
                this.cc.add(ccArr[i]);
        } // end if
        
        this.bcc = new ArrayList(2);
        String bcc = props.getProperty("mail.bcc", "");
        if(bcc.length() > 0) {
            String bccArr[] = bcc.split(",");
            for(int i = 0; i < bccArr.length; i++)
                this.bcc.add(bccArr[i]);
        } // end if
        
        subject = "";
    } // end of constructor
    
    public int getToCount() {
    	
    	return to.size();
    } 
    
    public String getTo(int index) {
    	
    	return (String)to.get(index - 1);
    }
    
    public Iterator getTo() {
    	
    	return to.iterator();
    }
    
    public int getCcCount() {
    	
    	return cc.size();
    } 
    
    public String getCc(int index) {
    	
    	return (String)cc.get(index - 1);
    }
    
    public Iterator getCc() {
    	
    	return cc.iterator();
    }
    
    public int getBccCount() {
    	
    	return bcc.size();
    }
    
    public String getBcc(int index) {
    	
    	return (String)bcc.get(index - 1);
    }
    
    public Iterator getBcc() {
    	
    	return bcc.iterator();
    }

    public boolean isEnableSSL() {
		return enableSSL;
	}

	public void setEnableSSL(boolean enableSSL) {
		this.enableSSL = enableSSL;
	}

	public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public String getPort()
    {
        return port;
    }

    public void setPort(String port)
    {
        this.port = port;
    }

    public boolean isAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(boolean authentication)
    {
        this.authentication = authentication;
    }

    public boolean isDebug()
    {
        return debug;
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    public boolean isDebugAuth()
    {
        return debugAuth;
    }

    public void setDebugAuth(boolean debugAuth)
    {
        this.debugAuth = debugAuth;
    }

    public String getFrom()
    {
        return from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public void addBcc(String bccEmail)
    {
        bcc.add(bccEmail);
    }

    public void addCc(String ccEmail)
    {
        cc.add(ccEmail);
    }

    public void addTo(String toEmail)
    {
        to.add(toEmail);
    }
    
    public void sendMail(String mailText) {
    	
    	Transport t = null;
    	Session session = null;
    	
    	try {
    		// Create properties
    		Properties props = new Properties();
    		
    		if (debugAuth) props.put("mail.debug.auth", "true");
	    
    		if (enableSSL) {
    			props.put("mail.smtps.host", host);
			    props.put("mail.smtps.port", port);
			    if (authentication) props.put("mail.smtps.auth", "true");
			    session = Session.getDefaultInstance(props);
			    if (debug) session.setDebug(true);
			    t = session.getTransport("smtps");
		    } else {
		    	props.put("mail.smtp.host", host);
			    props.put("mail.smtp.port", port);
			    if (authentication) props.put("mail.smtp.auth", "true");
			    if (startTLS) props.put("mail.smtp.starttls.enable", "true");
			    session = Session.getDefaultInstance(props);
			    if (debug) session.setDebug(true);
			    t = session.getTransport("smtp");
		    } // end if
		    
	    	// Build message
	    	MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            
            if (!to.isEmpty()) {
                for(int i = 0; i < to.size(); i++)
                    message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress((String)to.get(i)));
            } // end if
            
            if (!cc.isEmpty()) {
                for(int i = 0; i < cc.size(); i++)
                    message.addRecipient(javax.mail.Message.RecipientType.CC, new InternetAddress((String)cc.get(i)));
            } // end if
            
            if (!bcc.isEmpty()) {
                for(int i = 0; i < bcc.size(); i++)
                    message.addRecipient(javax.mail.Message.RecipientType.BCC, new InternetAddress((String)bcc.get(i)));
            } // end if
            
            message.setSubject(subject);
            message.setText(mailText);
            
            // Send message
    	    if (authentication) t.connect(user, password);
    	    else t.connect();
    	    t.sendMessage(message, message.getAllRecipients());
    	    t.close();
        } catch(MessagingException mex) {
        	System.err.println("E-mail message not sent because of error stated below!");
        	System.err.println("MessagingException: " + mex.getLocalizedMessage());
            //mex.printStackTrace();
        }
	    
    } // end of method sendMail()
    
    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }
    
    public boolean isStartTLS() {
		return startTLS;
	}

	public void setStartTLS(boolean startTLS) {
		this.startTLS = startTLS;
	}

} // end of Class EMailer
