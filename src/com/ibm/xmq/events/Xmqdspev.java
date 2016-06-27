package com.ibm.xmq.events;

import java.io.PrintStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQException;
import com.ibm.mq.MQTopic;

import com.ibm.mq.constants.CMQC;
import com.ibm.mq.MQC;
import com.ibm.mq.constants.CMQXC;
import com.ibm.mq.constants.CMQCFC;
import com.ibm.mq.pcf.MQCFGR;
import com.ibm.mq.pcf.PCFMessage;
import com.ibm.mq.pcf.PCFException;
import com.ibm.mq.pcf.PCFMessageAgent;

import com.ibm.xmq.events.EMailer;
import com.ibm.xmq.events.MQTMC2;
import com.ibm.xmq.events.XMQUtils;
import com.ibm.xmq.events.XWriter;

/**
 * __  __                    _
 * \ \/ /_ __ ___   __ _  __| |___ _ __   _____   __
 *  \  /| '_ ` _ \ / _` |/ _` / __| '_ \ / _ \ \ / /
 *  /  \| | | | | | (_| | (_| \__ \ |_) |  __/\ V /
 * /_/\_\_| |_| |_|\__, |\__,_|___/ .__/ \___| \_/
 *                    |_|         |_| 
 * 
 * Xmqdspev - Display IBM WebSphere MQ Events
 * 
 * Xmqdspev reads, interprets and displays IBM WebSphere MQ generated
 * event messages from SYSTEM.ADMIN.* event queues.
 *
 * Changes in v1.1:
 *      - Fixed bug related to event Not Authorized (type 5) - wrong identifier - MQCACF_ADMIN_TOPIC_NAMES     
 *      - Fixed missing BaseType for event Unknown Alias Base Queue
 *      - Fixed wrong name for BaseObjectName (BaseQName) for event Unknown Alias Base Queue
 *      - Added hidden -z flag to turn on debug mode
 *      - Added option -y to specify user-id to use when connecting to the queue manager (client connection only)
 *      - Fixed wrong import for XMQUtils (com.ibm.xmq.utilities.XMQUtils)
 *      - Added support for WebSphere MQ 7.1 and WebSphere MQ 7.5
 *      - Added support for WebSphere MQ AMS 7.0.1.2 command and configuration events
 *      - Added support for iSeries
 *      
 * Changes in v1.2:
 *      - Display channel authentication record used on Blocked Channel event
 *      - Added support for z/OS  
 *      - Added option -g to only display events matching a string
 *      - Added option -e to e-mail events
 *      - Added option -o to omit a list of events 
 *      - Added option -t to process events published on topics
 *      - Fixed various bugs 
 *      
 * Changes in v1.3:
 *      - Reactivated changes made for WMQ 7.1 (display of ConName and ChannelName on some events)
 *      - Added option -z to support WMQ 8.0 Connection Authentication
 *      - Tested for WMQ 8.0
 *
 * Changes in v1.4:
 * 		- Several bug fixes discovered in v1.3 for IBM MQ V8
 * 			- Use of user-id and password not allowed in bindings mode
 * 			- Missing event for MQRQ_CSP_NOT_AUTHORIZED (undocumented!)
 */
public class Xmqdspev {
	
	public static final String progName = "Xmqdspev";
	public static final String progVersion = "1.4";
	public static final String progAuthor = "Oliver Fisse (IBM)";
	public static final String progAuthorEmail = "fisse@us.ibm.com";
	public static final String progCopyright = "Copyright (c) IBM Corp. 2011-2015, all rights reserved";
	
	private volatile boolean shutdown = false;
	private volatile boolean shutdownComplete = false;
	
	private String qmgrName;
	private String qName;
	private String channelName;
	private String connectionName;
	private int portNumber;
	private String fileName;
	private boolean browseMode;
	private boolean displayDetails;
	private boolean waitMode;
	private PrintStream ps;
	private boolean useMQSERVER;
	private boolean useMQCHLTAB;
	private String channelTableName;
	private String cipherSuite;
	private int readFirstNEvents;
	private boolean skipPastLastEvent;
	private Calendar afterEventDate;
	private Calendar priorEventDate;
	private int waitInterval;
	private boolean debugMode;
	private String userId;
	private MQQueueManager qmgr;
    private PCFMessageAgent pcfAgent;
    private XWriter xw;
    private String grepString;
    private String emailConfigFileName;
    private EMailer em;
    private boolean omitEvent;
    private int omitEventList[];
    private String topicString;
    private String password;
	
	
	/**
	 * Constructor
	 */
	public Xmqdspev() {
		
		this.qName = null;
		this.qmgrName = null;
		this.channelName = null;
		this.connectionName = null;
		this.portNumber = 1414;
		this.fileName = null;
		this.browseMode = true;
		this.displayDetails = false;
		this.waitMode = false;
		this.ps = null;
		this.useMQSERVER = false;
		this.useMQCHLTAB = false;
		this.channelTableName = null;
		this.cipherSuite = null;
		this.readFirstNEvents = 0;
		this.skipPastLastEvent = false;
		this.afterEventDate = null;
		this.priorEventDate = null;
		this.waitInterval = 0;
		this.debugMode = false;
		this.userId = null;
		this.qmgr = null;
		this.pcfAgent = null;
        this.xw = null;
        this.grepString = null;
        this.emailConfigFileName = null;
        this.em = null;
        this.omitEvent = false;
        this.omitEventList = null;
        this.topicString = null;
        this.password = null;
	} // end of constructor
	
	private String applTypeToString(int applType) {
		
		String applTypeString = null;
		
		switch (applType) {
			case CMQC.MQAT_UNKNOWN:
				applTypeString = "MQAT_UNKNOWN";
				break;
			case CMQC.MQAT_NO_CONTEXT:
				applTypeString = "MQAT_NO_CONTEXT";
				break;
			case CMQC.MQAT_CICS:
				applTypeString = "MQAT_CICS";
				break;
			case CMQC.MQAT_ZOS:
				applTypeString = "MQAT_ZOS";
				break;
			case CMQC.MQAT_IMS:
				applTypeString = "MQAT_IMS";
				break;
			case CMQC.MQAT_OS2:
				applTypeString = "MQAT_OS2";
				break;
			case CMQC.MQAT_DOS:
				applTypeString = "MQAT_DOS";
				break;
			case CMQC.MQAT_UNIX:
				applTypeString = "MQAT_UNIX";
				break;
			case CMQC.MQAT_QMGR:
				applTypeString = "MQAT_QMGR";
				break;
			case CMQC.MQAT_OS400:
				applTypeString = "MQAT_OS400";
				break;
			case CMQC.MQAT_WINDOWS:
				applTypeString = "MQAT_WINDOWS";
				break;
			case CMQC.MQAT_CICS_VSE:
				applTypeString = "MQAT_CICS_VSE";
				break;
			case CMQC.MQAT_WINDOWS_NT:
				applTypeString = "MQAT_WINDOWS_NT";
				break;
			case CMQC.MQAT_VMS:
				applTypeString = "MQAT_VMS";
				break;
			case CMQC.MQAT_NSK:
				applTypeString = "MQAT_NSK";
				break;
			case CMQC.MQAT_VOS:
				applTypeString = "MQAT_VOS";
				break;
			case CMQC.MQAT_OPEN_TP1:
				applTypeString = "MQAT_OPEN_TP1";
				break;
			case CMQC.MQAT_VM:
				applTypeString = "MQAT_VM";
				break;
			case CMQC.MQAT_IMS_BRIDGE:
				applTypeString = "MQAT_IMS_BRIDGE";
				break;
			case CMQC.MQAT_XCF:
				applTypeString = "MQAT_XCF";
				break;
			case CMQC.MQAT_CICS_BRIDGE:
				applTypeString = "MQAT_CICS_BRIDGE";
				break;
			case CMQC.MQAT_NOTES_AGENT:
				applTypeString = "MQAT_NOTES_AGENT";
				break;
			case CMQC.MQAT_TPF:
				applTypeString = "MQAT_TPF";
				break;
			case CMQC.MQAT_USER:
				applTypeString = "MQAT_USER";
				break;
			case CMQC.MQAT_QMGR_PUBLISH:
				applTypeString = "MQAT_BROKER/MQAT_QMGR_PUBLISH";
				break;
			case CMQC.MQAT_JAVA:
				applTypeString = "MQAT_JAVA";
				break;
			case CMQC.MQAT_DQM:
				applTypeString = "MQAT_DQM";
				break;
			case CMQC.MQAT_CHANNEL_INITIATOR:
				applTypeString = "MQAT_CHANNEL_INITIATOR";
				break;
			case CMQC.MQAT_WLM:
				applTypeString = "MQAT_WLM";
				break;
			case CMQC.MQAT_BATCH:
				applTypeString = "MQAT_BATCH";
				break;
			case CMQC.MQAT_RRS_BATCH:
				applTypeString = "MQAT_RRS_BATCH";
				break;
			case CMQC.MQAT_SIB:
				applTypeString = "MQAT_SIB";
				break;
			case CMQC.MQAT_SYSTEM_EXTENSION:
				applTypeString = "MQAT_SYSTEM_EXTENSION";
				break;
			case CMQC.MQAT_MCAST_PUBLISH:
				applTypeString = "MQAT_MCAST_PUBLISH";
				break;
			default:
				applTypeString = "Unknown!";
				break;
		} // end switch
		
		return applTypeString; 
	} // end of method applTypeToString()
	
	/**
	 * Translate command as integer into a string
	 * 
	 * @param command Command encoded as an integer
	 * @return The name of the command as a string
	 */
	private String commandToString(int command, boolean mqsc) {
		
		String commandString = null;
		String mqscCommandString = null;
		
		switch (command) {
			case CMQCFC.MQCMD_ACCOUNTING_MQI:
				commandString = "MQCMD_ACCOUNTING_MQI";
				break;
			case CMQCFC.MQCMD_ACCOUNTING_Q:
				commandString = "MQCMD_ACCOUNTING_Q";
				break;
			case CMQCFC.MQCMD_ACTIVITY_MSG:
				commandString = "MQCMD_ACTIVITY_MSG";
				break;
			case CMQCFC.MQCMD_ACTIVITY_TRACE:
				commandString = "MQCMD_ACTIVITY_TRACE";
				break;
			case CMQCFC.MQCMD_ARCHIVE_LOG: 
				commandString = "MQCMD_ARCHIVE_LOG";
				break;
			case CMQCFC.MQCMD_BACKUP_CF_STRUC: 
				commandString = "MQCMD_BACKUP_CF_STRUC";
				break;
			case CMQCFC.MQCMD_BROKER_INTERNAL:
				commandString = "MQCMD_BROKER_INTERNAL";
				break;
			case CMQCFC.MQCMD_CHANGE_AUTH_INFO: 
				commandString = "MQCMD_CHANGE_AUTH_INFO";
				break;
			case CMQCFC.MQCMD_CHANGE_BUFFER_POOL: 
				commandString = "MQCMD_CHANGE_BUFFER_POOL";
				break;
			case CMQCFC.MQCMD_CHANGE_CF_STRUC: 
				commandString = "MQCMD_CHANGE_CF_STRUC";
				break;
			case CMQCFC.MQCMD_CHANGE_CHANNEL: 
				commandString = "MQCMD_CHANGE_CHANNEL";
				break;
			case CMQCFC.MQCMD_CHANGE_COMM_INFO:
				commandString = "MQCMD_CHANGE_COMM_INFO";
				break;
			case CMQCFC.MQCMD_CHANGE_LISTENER: 
				commandString = "MQCMD_CHANGE_LISTENER";
				break;
			case CMQCFC.MQCMD_CHANGE_NAMELIST: 
				commandString = "MQCMD_CHANGE_NAMELIST";
				break;
			case CMQCFC.MQCMD_CHANGE_PAGE_SET: 
				commandString = "MQCMD_CHANGE_PAGE_SET";
				break;
			case CMQCFC.MQCMD_CHANGE_PROCESS: 
				commandString = "MQCMD_CHANGE_PROCESS";
				mqscCommandString = "ALTER";
				break;
			case CMQCFC.MQCMD_CHANGE_PROT_POLICY:
				commandString = "MQCMD_CHANGE_PROT_POLICY";
				break;
			case CMQCFC.MQCMD_CHANGE_Q: 
				commandString = "MQCMD_CHANGE_Q";
				mqscCommandString = "ALTER";
				break;
			case CMQCFC.MQCMD_CHANGE_Q_MGR: 
				commandString = "MQCMD_CHANGE_Q_MGR";
				mqscCommandString = "ALTER QMGR";
				break;
			case CMQCFC.MQCMD_CHANGE_SECURITY: 
				commandString = "MQCMD_CHANGE_SECURITY";
				break;
			case CMQCFC.MQCMD_CHANGE_SERVICE: 
				commandString = "MQCMD_CHANGE_SERVICE";
				mqscCommandString = "ALTER";
				break;
			case CMQCFC.MQCMD_CHANGE_SMDS:
				commandString = "MQCMD_CHANGE_SMDS";
				break;
			case CMQCFC.MQCMD_CHANGE_SUBSCRIPTION: 
				commandString = "MQCMD_CHANGE_SUBSCRIPTION";
				break;
			case CMQCFC.MQCMD_CHANGE_STG_CLASS: 
				commandString = "MQCMD_CHANGE_STG_CLASS";
				break;
			case CMQCFC.MQCMD_CHANGE_TOPIC: 
				commandString = "MQCMD_CHANGE_TOPIC";
				break;
			case CMQCFC.MQCMD_CHANGE_TRACE: 
				commandString = "MQCMD_CHANGE_TRACE";
				break;
			case CMQCFC.MQCMD_CHANNEL_EVENT:
				commandString = "MQCMD_CHANNEL_EVENT";
				break;
			case CMQCFC.MQCMD_CLEAR_Q: 
				commandString = "MQCMD_CLEAR_Q";
				break;
			case CMQCFC.MQCMD_CLEAR_TOPIC_STRING: 
				commandString = "MQCMD_CLEAR_TOPIC_STRING";
				break;
			case CMQCFC.MQCMD_COMMAND_EVENT:
				commandString = "MQCMD_COMMAND_EVENT";
				break;
			case CMQCFC.MQCMD_CONFIG_EVENT:
				commandString = "MQCMD_CONFIG_EVENT";
				break;
			case CMQCFC.MQCMD_COPY_AUTH_INFO: 
				commandString = "MQCMD_COPY_AUTH_INFO";
				break;
			case CMQCFC.MQCMD_COPY_CF_STRUC: 
				commandString = "MQCMD_COPY_CF_STRUC";
				break;
			case CMQCFC.MQCMD_COPY_CHANNEL: 
				commandString = "MQCMD_COPY_CHANNEL";
				break;
			case CMQCFC.MQCMD_COPY_COMM_INFO:
				commandString = "MQCMD_COPY_COMM_INFO";
				break;
			case CMQCFC.MQCMD_COPY_LISTENER: 
				commandString = "MQCMD_COPY_LISTENER";
				break;
			case CMQCFC.MQCMD_COPY_NAMELIST: 
				commandString = "MQCMD_COPY_NAMELIST";
				break;
			case CMQCFC.MQCMD_COPY_PROCESS: 
				commandString = "MQCMD_COPY_PROCESS";
				break;
			case CMQCFC.MQCMD_COPY_Q: 
				commandString = "MQCMD_COPY_Q";
				break;
			case CMQCFC.MQCMD_COPY_SERVICE: 
				commandString = "MQCMD_COPY_SERVICE";
				break;
			case CMQCFC.MQCMD_COPY_STG_CLASS: 
				commandString = "MQCMD_COPY_STG_CLASS";
				break;
			case CMQCFC.MQCMD_COPY_SUBSCRIPTION: 
				commandString = "MQCMD_COPY_SUBSCRIPTION";
				break;
			case CMQCFC.MQCMD_COPY_TOPIC: 
				commandString = "MQCMD_COPY_TOPIC";
				break;
			case CMQCFC.MQCMD_CREATE_AUTH_INFO: 
				commandString = "MQCMD_CREATE_AUTH_INFO";
				break;
			case CMQCFC.MQCMD_CREATE_BUFFER_POOL: 
				commandString = "MQCMD_CREATE_BUFFER_POOL";
				break;
			case CMQCFC.MQCMD_CREATE_CF_STRUC: 
				commandString = "MQCMD_CREATE_CF_STRUC";
				break;
			case CMQCFC.MQCMD_CREATE_CHANNEL: 
				commandString = "MQCMD_CREATE_CHANNEL";
				mqscCommandString = "DEFINE";
				break;
			case CMQCFC.MQCMD_CREATE_COMM_INFO:
				commandString = "MQCMD_CREATE_COMM_INFO";
				break;
			case CMQCFC.MQCMD_CREATE_LISTENER: 
				commandString = "MQCMD_CREATE_LISTENER";
				mqscCommandString = "DEFINE";
				break;
			case CMQCFC.MQCMD_CREATE_LOG: 
				commandString = "MQCMD_CREATE_LOG";
				break;
			case CMQCFC.MQCMD_CREATE_NAMELIST: 
				commandString = "MQCMD_CREATE_NAMELIST";
				break;
			case CMQCFC.MQCMD_CREATE_PAGE_SET: 
				commandString = "MQCMD_CREATE_PAGE_SET";
				break;
			case CMQCFC.MQCMD_CREATE_PROCESS: 
				commandString = "MQCMD_CREATE_PROCESS";
				break;
			case CMQCFC.MQCMD_CREATE_PROT_POLICY:
				commandString = "MQCMD_CREATE_PROT_POLICY";
				break;
			case CMQCFC.MQCMD_CREATE_Q: 
				commandString = "MQCMD_CREATE_Q";
				mqscCommandString = "DEFINE";
				break;
			case CMQCFC.MQCMD_CREATE_SERVICE: 
				commandString = "MQCMD_CREATE_SERVICE";
				break;
			case CMQCFC.MQCMD_CREATE_SUBSCRIPTION: 
				commandString = "MQCMD_CREATE_SUBSCRIPTION";
				break;
			case CMQCFC.MQCMD_CREATE_STG_CLASS: 
				commandString = "MQCMD_CREATE_STG_CLASS";
				break;
			case CMQCFC.MQCMD_CREATE_TOPIC: 
				commandString = "MQCMD_CREATE_TOPIC";
				break;
			case CMQCFC.MQCMD_DELETE_AUTH_INFO: 
				commandString = "MQCMD_DELETE_AUTH_INFO";
				break;
			case CMQCFC.MQCMD_DELETE_AUTH_REC:
				commandString = "MQCMD_DELETE_AUTH_REC";
				break;
			case CMQCFC.MQCMD_DELETE_BUFFER_POOL: 
				commandString = "MQCMD_DELETE_BUFFER_POOL";
				break;	
			case CMQCFC.MQCMD_DELETE_CF_STRUC: 
				commandString = "MQCMD_DELETE_CF_STRUC";
				break;
			case CMQCFC.MQCMD_DELETE_CHANNEL: 
				commandString = "MQCMD_DELETE_CHANNEL";
				break;
			case CMQCFC.MQCMD_DELETE_COMM_INFO:
				commandString = "MQCMD_DELETE_COMM_INFO";
				break;
			case CMQCFC.MQCMD_DELETE_LISTENER: 
				commandString = "MQCMD_DELETE_LISTENER";
				break;	
			case CMQCFC.MQCMD_DELETE_NAMELIST: 
				commandString = "MQCMD_DELETE_NAMELIST";
				break;
			case CMQCFC.MQCMD_DELETE_PAGE_SET: 
				commandString = "MQCMD_DELETE_PAGE_SET";
				break;
			case CMQCFC.MQCMD_DELETE_PROCESS: 
				commandString = "MQCMD_DELETE_PROCESS";
				break;	
			case CMQCFC.MQCMD_DELETE_PROT_POLICY:
				commandString = "MQCMD_DELETE_PROT_POLICY";
				break;
			case CMQCFC.MQCMD_DELETE_PUBLICATION:
				commandString = "MQCMD_DELETE_PUBLICATION";
				break;
			case CMQCFC.MQCMD_DELETE_Q: 
				commandString = "MQCMD_DELETE_Q";
				break;
			case CMQCFC.MQCMD_DELETE_SERVICE: 
				commandString = "MQCMD_DELETE_SERVICE";
				break;
			case CMQCFC.MQCMD_DELETE_SUBSCRIPTION: 
				commandString = "MQCMD_DELETE_SUBSCRIPTION";
				break;
			case CMQCFC.MQCMD_DELETE_STG_CLASS: 
				commandString = "MQCMD_DELETE_STG_CLASS";
				break;
			case CMQCFC.MQCMD_DELETE_TOPIC: 
				commandString = "MQCMD_DELETE_TOPIC";
				break;
			case CMQCFC.MQCMD_DEREGISTER_PUBLISHER:
				commandString = "MQCMD_DEREGISTER_PUBLISHER";
				break;
			case CMQCFC.MQCMD_DEREGISTER_SUBSCRIBER:
				commandString = "MQCMD_DEREGISTER_SUBSCRIBER";
				break;
			case CMQCFC.MQCMD_INQUIRE_ARCHIVE: 
				commandString = "MQCMD_INQUIRE_ARCHIVE";
				break;	
			case CMQCFC.MQCMD_INQUIRE_AUTH_INFO: 
				commandString = "MQCMD_INQUIRE_AUTH_INFO";
				break;
			case CMQCFC.MQCMD_INQUIRE_AUTH_INFO_NAMES: 
				commandString = "MQCMD_INQUIRE_AUTH_INFO_NAMES";
				break;
			case CMQCFC.MQCMD_INQUIRE_AUTH_RECS:
				commandString = "MQCMD_INQUIRE_AUTH_RECS";
				break;
			case CMQCFC.MQCMD_INQUIRE_AUTH_SERVICE:
				commandString = "MQCMD_INQUIRE_AUTH_SERVICE";
				break;
			case CMQCFC.MQCMD_INQUIRE_CF_STRUC: 
				commandString = "MQCMD_INQUIRE_CF_STRUC";
				break;
			case CMQCFC.MQCMD_INQUIRE_CF_STRUC_NAMES: 
				commandString = "MQCMD_INQUIRE_CF_STRUC_NAMES";
				break;
			case CMQCFC.MQCMD_INQUIRE_CF_STRUC_STATUS: 
				commandString = "MQCMD_INQUIRE_CF_STRUC_STATUS";
				break;	
			case CMQCFC.MQCMD_INQUIRE_CHANNEL: 
				commandString = "MQCMD_INQUIRE_CHANNEL";
				break;
			case CMQCFC.MQCMD_INQUIRE_CHANNEL_NAMES: 
				commandString = "MQCMD_INQUIRE_CHANNEL_NAMES";
				break;
			case CMQCFC.MQCMD_INQUIRE_CHANNEL_INIT: 
				commandString = "MQCMD_INQUIRE_CHANNEL_INIT";
				break;
			case CMQCFC.MQCMD_INQUIRE_CHANNEL_STATUS: 
				commandString = "MQCMD_INQUIRE_CHANNEL_STATUS";
				break;	
			case CMQCFC.MQCMD_INQUIRE_CHLAUTH_RECS:
				commandString = "MQCMD_INQUIRE_CHLAUTH_RECS";
				break;
			case CMQCFC.MQCMD_INQUIRE_CLUSTER_Q_MGR: 
				commandString = "MQCMD_INQUIRE_CLUSTER_Q_MGR";
				break;
			case CMQCFC.MQCMD_INQUIRE_CMD_SERVER: 
				commandString = "MQCMD_INQUIRE_CMD_SERVER";
				break;
			case CMQCFC.MQCMD_INQUIRE_COMM_INFO:
				commandString = "MQCMD_INQUIRE_COMM_INFO";
				break;
			case CMQCFC.MQCMD_INQUIRE_CONNECTION: 
				commandString = "MQCMD_INQUIRE_CONNECTION";
				break;	
			case CMQCFC.MQCMD_INQUIRE_ENTITY_AUTH:
				commandString = "MQCMD_INQUIRE_ENTITY_AUTH";
				break;
			case CMQCFC.MQCMD_INQUIRE_LISTENER: 
				commandString = "MQCMD_INQUIRE_LISTENER";
				break;	
			case CMQCFC.MQCMD_INQUIRE_LISTENER_STATUS: 
				commandString = "MQCMD_INQUIRE_LISTENER_STATUS";
				break;	
			case CMQCFC.MQCMD_INQUIRE_LOG: 
				commandString = "MQCMD_INQUIRE_LOG";
				break;
			case CMQCFC.MQCMD_INQUIRE_MQXR_STATUS:
				commandString = "MQCMD_INQUIRE_MQXR_STATUS";
				break;
			case CMQCFC.MQCMD_INQUIRE_NAMELIST: 
				commandString = "MQCMD_INQUIRE_NAMELIST";
				break;
			case CMQCFC.MQCMD_INQUIRE_NAMELIST_NAMES: 
				commandString = "MQCMD_INQUIRE_NAMELIST_NAMES";
				break;
			case CMQCFC.MQCMD_INQUIRE_PROCESS: 
				commandString = "MQCMD_INQUIRE_PROCESS";
				break;	
			case CMQCFC.MQCMD_INQUIRE_PROCESS_NAMES: 
				commandString = "MQCMD_INQUIRE_PROCESS_NAMES";
				break;	
			case CMQCFC.MQCMD_INQUIRE_PROT_POLICY:
				commandString = "MQCMD_INQUIRE_PROT_POLICY";
				break;
			case CMQCFC.MQCMD_INQUIRE_PUBSUB_STATUS : 
				commandString = "MQCMD_INQUIRE_PUBSUB_STATUS ";
				break;		
			case CMQCFC.MQCMD_INQUIRE_Q: 
				commandString = "MQCMD_INQUIRE_Q";
				break;
			case CMQCFC.MQCMD_INQUIRE_Q_NAMES: 
				commandString = "MQCMD_INQUIRE_Q_NAMES";
				break;
			case CMQCFC.MQCMD_INQUIRE_Q_MGR: 
				commandString = "MQCMD_INQUIRE_Q_MGR";
				break;
			case CMQCFC.MQCMD_INQUIRE_Q_MGR_STATUS: 
				commandString = "MQCMD_INQUIRE_Q_MGR_STATUS";
				break;
			case CMQCFC.MQCMD_INQUIRE_QSG: 
				commandString = "MQCMD_INQUIRE_QSG";
				break;		
			case CMQCFC.MQCMD_INQUIRE_Q_STATUS: 
				commandString = "MQCMD_INQUIRE_Q_STATUS";
				break;
			case CMQCFC.MQCMD_INQUIRE_SECURITY: 
				commandString = "MQCMD_INQUIRE_SECURITY";
				break;
			case CMQCFC.MQCMD_INQUIRE_SERVICE: 
				commandString = "MQCMD_INQUIRE_SERVICE";
				break;
			case CMQCFC.MQCMD_INQUIRE_SERVICE_STATUS: 
				commandString = "MQCMD_INQUIRE_SERVICE_STATUS";
				break;
			case CMQCFC.MQCMD_INQUIRE_SMDS:
				commandString = "MQCMD_INQUIRE_SMDS";
				break;
			case CMQCFC.MQCMD_INQUIRE_SMDSCONN:
				commandString = "MQCMD_INQUIRE_SMDSCONN";
				break;
			case CMQCFC.MQCMD_INQUIRE_SUBSCRIPTION: 
				commandString = "MQCMD_INQUIRE_SUBSCRIPTION";
				break;	
			case CMQCFC.MQCMD_INQUIRE_STG_CLASS: 
				commandString = "MQCMD_INQUIRE_STG_CLASS";
				break;
			case CMQCFC.MQCMD_INQUIRE_STG_CLASS_NAMES: 
				commandString = "MQCMD_INQUIRE_STG_CLASS_NAMES";
				break;	
			case CMQCFC.MQCMD_INQUIRE_SUB_STATUS: 
				commandString = "MQCMD_INQUIRE_SUB_STATUS";
				break;		
			case CMQCFC.MQCMD_INQUIRE_SYSTEM: 
				commandString = "MQCMD_INQUIRE_SYSTEM";
				break;
			case CMQCFC.MQCMD_INQUIRE_THREAD: 
				commandString = "MQCMD_INQUIRE_THREAD";
				break;
			case CMQCFC.MQCMD_INQUIRE_TOPIC: 
				commandString = "MQCMD_INQUIRE_TOPIC";
				break;
			case CMQCFC.MQCMD_INQUIRE_TOPIC_NAMES: 
				commandString = "MQCMD_INQUIRE_TOPIC_NAMES";
				break;
			case CMQCFC.MQCMD_INQUIRE_TOPIC_STATUS: 
				commandString = "MQCMD_INQUIRE_TOPIC_STATUS";
				break;	
			case CMQCFC.MQCMD_INQUIRE_TRACE: 
				commandString = "MQCMD_INQUIRE_TRACE";
				break;	
			case CMQCFC.MQCMD_INQUIRE_USAGE: 
				commandString = "MQCMD_INQUIRE_USAGE";
				break;
			case CMQCFC.MQCMD_INQUIRE_XR_CAPABILITY:
				commandString = "MQCMD_INQUIRE_XR_CAPABILITY";
				break;
			case CMQCFC.MQCMD_LOGGER_EVENT:
				commandString = "MQCMD_LOGGER_EVENT";
				break;
			case CMQCFC.MQCMD_MOVE_Q: 
				commandString = "MQCMD_MOVE_Q";
				break;
			case CMQCFC.MQCMD_MQXR_DIAGNOSTICS:
				commandString = "MQCMD_MQXR_DIAGNOSTICS";
				break;
			case CMQCFC.MQCMD_ESCAPE:
				commandString = "MQCMD_ESCAPE";
				break;
			case CMQCFC.MQCMD_PERFM_EVENT:
				commandString = "MQCMD_PERFM_EVENT";
				break;
			case CMQCFC.MQCMD_PING_CHANNEL: 
				commandString = "MQCMD_PING_CHANNEL";
				break;
			case CMQCFC.MQCMD_PING_Q_MGR:
				commandString = "MQCMD_PING_Q_MGR";
				break;
			case CMQCFC.MQCMD_PUBLISH:
				commandString = "MQCMD_PUBLISH";
				break;
			case CMQCFC.MQCMD_PURGE_CHANNEL:
				commandString = "MQCMD_PURGE_CHANNEL";
				break;
			case CMQCFC.MQCMD_Q_MGR_EVENT:
				commandString = "MQCMD_Q_MGR_EVENT";
				break;
			case CMQCFC.MQCMD_RECOVER_BSDS: 
				commandString = "MQCMD_RECOVER_BSDS";
				break;
			case CMQCFC.MQCMD_RECOVER_CF_STRUC: 
				commandString = "MQCMD_RECOVER_CF_STRUC";
				break;			
			case CMQCFC.MQCMD_REFRESH_CLUSTER: 
				commandString = "MQCMD_REFRESH_CLUSTER";
				break;
			case CMQCFC.MQCMD_REFRESH_Q_MGR: 
				commandString = "MQCMD_REFRESH_Q_MGR";
				break;		
			case CMQCFC.MQCMD_REFRESH_SECURITY: 
				commandString = "MQCMD_REFRESH_SECURITY";
				break;
			case CMQCFC.MQCMD_REGISTER_PUBLISHER:
				commandString = "MQCMD_REGISTER_PUBLISHER";
				break;
			case CMQCFC.MQCMD_REGISTER_SUBSCRIBER:
				commandString = "MQCMD_REGISTER_SUBSCRIBER";
				break;
			case CMQCFC.MQCMD_REQUEST_UPDATE:
				commandString = "MQCMD_REQUEST_UPDATE";
				break;
			case CMQCFC.MQCMD_RESET_CF_STRUC:
				commandString = "MQCMD_RESET_CF_STRUC";
				break;
			case CMQCFC.MQCMD_RESET_CHANNEL: 
				commandString = "MQCMD_RESET_CHANNEL";
				break;
			case CMQCFC.MQCMD_RESET_CLUSTER: 
				commandString = "MQCMD_RESET_CLUSTER";
				break;	
			case CMQCFC.MQCMD_RESET_Q_MGR:
				commandString = "QCMD_RESET_Q_MGR";
				break;
			case CMQCFC.MQCMD_RESET_Q_STATS: 
				commandString = "MQCMD_RESET_Q_STATS";
				break;
			case CMQCFC.MQCMD_RESET_SMDS:
				commandString = "MQCMD_RESET_SMDS";
				break;
			case CMQCFC.MQCMD_RESET_TPIPE: 
				commandString = "MQCMD_RESET_TPIPE";
				break;		
			case CMQCFC.MQCMD_RESOLVE_CHANNEL: 
				commandString = "MQCMD_RESOLVE_CHANNEL";
				break;
			case CMQCFC.MQCMD_RESOLVE_INDOUBT: 
				commandString = "MQCMD_RESOLVE_INDOUBT";
				break;	
			case CMQCFC.MQCMD_RESUME_Q_MGR: 
				commandString = "MQCMD_RESUME_Q_MGR";
				break;
			case CMQCFC.MQCMD_RESUME_Q_MGR_CLUSTER: 
				commandString = "MQCMD_RESUME_Q_MGR_CLUSTER";
				break;		
			case CMQCFC.MQCMD_REVERIFY_SECURITY:
				commandString = "MQCMD_REVERIFY_SECURITY";
				break;
			case CMQCFC.MQCMD_SET_ARCHIVE: 
				commandString = "MQCMD_SET_ARCHIVE";
				break;
			case CMQCFC.MQCMD_SET_AUTH_REC:
				commandString = "MQCMD_SET_AUTH_REC";
				break;
			case CMQCFC.MQCMD_SET_CHLAUTH_REC:
				commandString = "MQCMD_SET_CHLAUTH_REC";
				break;
			case CMQCFC.MQCMD_SET_LOG: 
				commandString = "MQCMD_SET_LOG";
				break;				
			case CMQCFC.MQCMD_SET_SYSTEM: 
				commandString = "MQCMD_SET_SYSTEM";
				break;
			case CMQCFC.MQCMD_START_CHANNEL: 
				commandString = "MQCMD_START_CHANNEL";
				break;		
			case CMQCFC.MQCMD_START_CHANNEL_INIT: 
				commandString = "MQCMD_START_CHANNEL_INIT";
				break;
			case CMQCFC.MQCMD_START_CHANNEL_LISTENER: 
				commandString = "MQCMD_START_CHANNEL_LISTENER";
				break;
			case CMQCFC.MQCMD_START_CLIENT_TRACE:
				commandString = "MQCMD_START_CLIENT_TRACE";
				break;
			case CMQCFC.MQCMD_START_CMD_SERVER: 
				commandString = "MQCMD_START_CMD_SERVER";
				break;
			case CMQCFC.MQCMD_START_Q_MGR:
				commandString = "MQCMD_START_Q_MGR";
				break;
			case CMQCFC.MQCMD_START_SERVICE: 
				commandString = "MQCMD_START_SERVICE";
				break;
			case CMQCFC.MQCMD_START_SMDSCONN:
				commandString = "MQCMD_START_SMDSCONN";
				break;
			case CMQCFC.MQCMD_START_TRACE: 
				commandString = "MQCMD_START_TRACE";
				break;
			case CMQCFC.MQCMD_STATISTICS_CHANNEL:
				commandString = "MQCMD_STATISTICS_CHANNEL";
				break;
			case CMQCFC.MQCMD_STATISTICS_MQI:
				commandString = "MQCMD_STATISTICS_MQI";
				break;
			case CMQCFC.MQCMD_STATISTICS_Q:
				commandString = "MQCMD_STATISTICS_Q";
				break;
			case CMQCFC.MQCMD_STOP_CHANNEL: 
				commandString = "MQCMD_STOP_CHANNEL";
				break;		
			case CMQCFC.MQCMD_STOP_CHANNEL_INIT: 
				commandString = "MQCMD_STOP_CHANNEL_INIT";
				break;
			case CMQCFC.MQCMD_STOP_CHANNEL_LISTENER: 
				commandString = "MQCMD_STOP_CHANNEL_LISTENER";
				break;
			case CMQCFC.MQCMD_STOP_CMD_SERVER: 
				commandString = "MQCMD_STOP_CMD_SERVER";
				break;	
			case CMQCFC.MQCMD_STOP_CLIENT_TRACE:
				commandString = "MQCMD_STOP_CLIENT_TRACE";
				break;
			case CMQCFC.MQCMD_STOP_CONNECTION: 
				commandString = "MQCMD_STOP_CONNECTION";
				break;	
			case CMQCFC.MQCMD_STOP_Q_MGR:
				commandString = "MQCMD_STOP_Q_MGR";
				break;	
			case CMQCFC.MQCMD_STOP_SERVICE: 
				commandString = "MQCMD_STOP_SERVICE";
				break;
			case CMQCFC.MQCMD_STOP_SMDSCONN:
				commandString = "MQCMD_STOP_SMDSCONN";
				break;
			case CMQCFC.MQCMD_STOP_TRACE: 
				commandString = "MQCMD_STOP_TRACE";
				break;	
			case CMQCFC.MQCMD_SUSPEND_Q_MGR: 
				commandString = "MQCMD_SUSPEND_Q_MGR";
				break;	
			case CMQCFC.MQCMD_SUSPEND_Q_MGR_CLUSTER: 
				commandString = "MQCMD_SUSPEND_Q_MGR_CLUSTER";
				break;
			case CMQCFC.MQCMD_TRACE_ROUTE:
				commandString = "MQCMD_TRACE_ROUTE";
				break;
			default:
				commandString = "Unknown!";
		} // end switch
		
		if (mqsc) return mqscCommandString;
		else return commandString;
	} // end of method commandToString()
	
	/**
	 * Translate PCF reason as integer into a string
	 * 
	 * @param pcfReason PCF reason encoded as an integer
	 * @return The name of the PCF reason as a string
	 */
	public static String pcfReasonToString(int pcfReason)
    {
        String pcfReasonAsString;
        switch(pcfReason)
        {
        case CMQC.MQRC_ALIAS_BASE_Q_TYPE_ERROR: 
            pcfReasonAsString = "Alias Base Queue Type Error";
            break;

        case CMQC.MQRC_BRIDGE_STARTED: 
            pcfReasonAsString = "Bridge Started";
            break;

        case CMQC.MQRC_BRIDGE_STOPPED: 
            pcfReasonAsString = "Bridge Stopped";
            break;

        case CMQC.MQRC_CONFIG_CHANGE_OBJECT: 
            pcfReasonAsString = "Change Object";
            break;

        case CMQC.MQRC_CHANNEL_ACTIVATED: 
            pcfReasonAsString = "Channel Activated";
            break;

        case CMQC.MQRC_CHANNEL_AUTO_DEF_ERROR: 
            pcfReasonAsString = "Channel Auto-definition Error";
            break;

        case CMQC.MQRC_CHANNEL_AUTO_DEF_OK: 
            pcfReasonAsString = "Channel Auto-definition OK";
            break;

        case CMQC.MQRC_CHANNEL_BLOCKED: 
            pcfReasonAsString = "Channel Blocked";
            break;

        case CMQC.MQRC_CHANNEL_BLOCKED_WARNING: 
            pcfReasonAsString = "Channel Blocked (Warning)";
            break;

        case CMQC.MQRC_CHANNEL_CONV_ERROR: 
            pcfReasonAsString = "Channel Conversion Error";
            break;

        case CMQC.MQRC_CHANNEL_NOT_ACTIVATED: 
            pcfReasonAsString = "Channel Not Activated";
            break;

        case CMQC.MQRC_CHANNEL_NOT_AVAILABLE: 
            pcfReasonAsString = "Channel Not Available";
            break;

        case CMQC.MQRC_CHANNEL_SSL_ERROR: 
            pcfReasonAsString = "Channel SSL Error";
            break;

        case CMQC.MQRC_CHANNEL_SSL_WARNING: 
            pcfReasonAsString = "Channel SSL Warning";
            break;

        case CMQC.MQRC_CHANNEL_STARTED: 
            pcfReasonAsString = "Channel Started";
            break;

        case CMQC.MQRC_CHANNEL_STOPPED: 
            pcfReasonAsString = "Channel Stopped";
            break;

        case CMQC.MQRC_CHANNEL_STOPPED_BY_USER: 
            pcfReasonAsString = "Channel Stopped By User";
            break;

        case CMQC.MQRC_COMMAND_MQSC: 
            pcfReasonAsString = "MQSC Command";
            break;

        case CMQC.MQRC_COMMAND_PCF: 
            pcfReasonAsString = "PCF Command";
            break;

        case CMQC.MQRC_CONFIG_CREATE_OBJECT: 
            pcfReasonAsString = "Create Object";
            break;

        case CMQC.MQRC_DEF_XMIT_Q_TYPE_ERROR: 
            pcfReasonAsString = "Default Transmission Queue Type Error";
            break;

        case CMQC.MQRC_DEF_XMIT_Q_USAGE_ERROR: 
            pcfReasonAsString = "Default Transmission Queue Usage Error";
            break;

        case CMQC.MQRC_CONFIG_DELETE_OBJECT: 
            pcfReasonAsString = "Delete Object";
            break;

        case CMQC.MQRC_GET_INHIBITED: 
            pcfReasonAsString = "Get Inhibited";
            break;

        case CMQC.MQRC_LOGGER_STATUS: 
            pcfReasonAsString = "Logger Status";
            break;

        case CMQC.MQRC_NOT_AUTHORIZED: 
            pcfReasonAsString = "Not Authorized";
            break;

        case CMQC.MQRC_PUT_INHIBITED: 
            pcfReasonAsString = "Put Inhibited";
            break;

        case CMQC.MQRC_Q_DEPTH_HIGH: 
            pcfReasonAsString = "Queue Depth High";
            break;

        case CMQC.MQRC_Q_DEPTH_LOW: 
            pcfReasonAsString = "Queue Depth Low";
            break;

        case CMQC.MQRC_Q_FULL: 
            pcfReasonAsString = "Queue Full";
            break;

        case CMQC.MQRC_Q_MGR_ACTIVE: 
            pcfReasonAsString = "Queue Manager Active";
            break;

        case CMQC.MQRC_Q_MGR_NOT_ACTIVE: 
            pcfReasonAsString = "Queue Manager Not Active";
            break;

        case CMQC.MQRC_Q_SERVICE_INTERVAL_HIGH: 
            pcfReasonAsString = "Queue Service Interval High";
            break;

        case CMQC.MQRC_Q_SERVICE_INTERVAL_OK: 
            pcfReasonAsString = "Queue Service Interval OK";
            break;

        case CMQC.MQRC_Q_TYPE_ERROR: 
            pcfReasonAsString = "Queue Type Error";
            break;

        case CMQC.MQRC_CONFIG_REFRESH_OBJECT: 
            pcfReasonAsString = "Refresh Object";
            break;

        case CMQC.MQRC_REMOTE_Q_NAME_ERROR: 
            pcfReasonAsString = "Remote Queue Name Error";
            break;

        case CMQC.MQRC_XMIT_Q_TYPE_ERROR: 
            pcfReasonAsString = "Transmission Queue Type Error";
            break;

        case CMQC.MQRC_XMIT_Q_USAGE_ERROR: 
            pcfReasonAsString = "Transmission Queue Usage Error";
            break;

        case CMQC.MQRC_UNKNOWN_ALIAS_BASE_Q: 
            pcfReasonAsString = "Unknown Alias Base Queue";
            break;

        case CMQC.MQRC_UNKNOWN_DEF_XMIT_Q: 
            pcfReasonAsString = "Unknown Default Transmission Queue";
            break;

        case CMQC.MQRC_UNKNOWN_OBJECT_NAME: 
            pcfReasonAsString = "Unknown Object Name";
            break;

        case CMQC.MQRC_UNKNOWN_REMOTE_Q_MGR: 
            pcfReasonAsString = "Unknown Remote Queue Manager";
            break;

        case CMQC.MQRC_UNKNOWN_XMIT_Q: 
            pcfReasonAsString = "Unknown Transmission Queue";
            break;

        default:
            pcfReasonAsString = "Unknown";
            break;
        }
        return pcfReasonAsString;
    }
	
	/**
	 * Translate platform as integer into a string
	 * 
	 * @param platform Platform encoded as an integer
	 * @return The name of the platform as a string
	 */
	public static String platformToString(int platform) {
		
		String platformAsString;
		
		switch (platform) {
			case CMQC.MQPL_UNIX: 
				platformAsString = "UNIX";
				break;
			case CMQC.MQPL_NSK: 
				platformAsString = "NSK";
				break;
			case CMQC.MQPL_OS400: 
				platformAsString = "OS400";
				break;
			case CMQC.MQPL_VMS: 
				platformAsString = "VMS";
				break;
			case CMQC.MQPL_WINDOWS: 
				platformAsString = "WINDOWS";
				break;
			case CMQC.MQPL_WINDOWS_NT: 
				platformAsString = "WINDOWS NT";
				break;
			case CMQC.MQPL_ZOS: 
				platformAsString = "ZOS";
				break;
			case CMQC.MQPL_OS2: 
				platformAsString = "OS2";
				break;
			case CMQC.MQPL_VSE: 
				platformAsString = "VSE";
				break;
			default: platformAsString = "UNKNOWN";
		} // end switch
		
		return platformAsString;
	} // end of method platformToString()
	
	/**
	 * Translate user source as integer into a string
	 * 
	 * @param userSource User source encoded as an integer
	 * @return The name of the user source as a string
	 */
	private String userSourceToString(int userSource) {
   
        String userSourceAsString;
        switch(userSource)
        {
        case CMQC.MQUSRC_MAP: 
            userSourceAsString = "MAP";
            break;

        case CMQC.MQUSRC_NOACCESS:
            userSourceAsString = "NOACCESS";
            break;

        case CMQC.MQUSRC_CHANNEL:
            userSourceAsString = "CHANNEL";
            break;

        default:
            userSourceAsString = "UNKNOWN";
            break;
        }
        return userSourceAsString;
    } // end of method userSoirceToString()

	/**
	 * Translate warning as integer into a string
	 * 
	 * @param warn Warning encoded as an integer
	 * @return The name of the warning as a string
	 */
    private String warnToString(int warn) {
  
        String warnAsString;
        switch(warn)
        {
        case CMQC.MQWARN_NO:
            warnAsString = "NO";
            break;

        case CMQC.MQWARN_YES:
            warnAsString = "YES";
            break;

        default:
            warnAsString = "UNKNOWN";
            break;
        }
        return warnAsString;
    } // end of method warnToString()

    private String rtrim(String original) {
    
        if(original == null)
            return null;
        int l = original.length();
        if(l == 0 || original.charAt(l - 1) != ' ')
            return original;
        int i;
        for(i = l - 2; i >= 0 && original.charAt(i) == ' '; i--);
        if(i < 0) return "";
       
        return original.substring(0, i + 1);
    } // end of method rtrim() 

    private void createPCFAgent(MQQueueManager qmgr) {
 
        try
        {
            if(debugMode)
                xw.println("Creating PCF Message Agent for queue manager '" + qmgrName + "'...");
            pcfAgent = new PCFMessageAgent(qmgr);
            if(debugMode)
                xw.println("PCF Message Agent reply queue name is '" + pcfAgent.getReplyQueueName() + "'...");
        }
        catch(PCFException pcfe)
        {
            System.err.println("PCF Message Agent creation ended with reason code " + pcfe.reasonCode + " (PCFExeption)");
            System.err.println(pcfe.getLocalizedMessage());
        }
        catch(MQException mqe)
        {
            System.err.println("PCF Message Agent creation ended with reason code " + mqe.reasonCode + " (MQException)");
            System.err.println(mqe.getLocalizedMessage());
        }
    } // end of method createPCFAgent()
	
	private String displayAlias_Base_Queue_Type_Error(PCFMessage pcfMsg)
		throws PCFException {
		
		int qType = pcfMsg.getIntParameterValue(CMQC.MQIA_Q_TYPE);

		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Alias Base Queue Type Error - MQRC_ALIAS_BASE_Q_TYPE_ERROR (2001, X'7D1')");
		xw.println("      EventType: Local");
		xw.println("    Description: An MQOPEN or MQPUT1 call was issued specifying an alias queue");
		xw.println("                 as the destination, but the BaseObjectName in the alias queue");
		xw.println("                 definition resolves to a queue that is not a local queue, or");
		xw.println("                 local definition of a remote queue.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
		xw.println(" BaseObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_BASE_OBJECT_NAME));
		xw.print("          QType: " + qType + " (");
		
		switch (qType) {
			case CMQC.MQQT_ALIAS: 
				xw.println("MQQT_ALIAS)");
				break;
			case CMQC.MQQT_MODEL:
				xw.println("MQQT_MODEL)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
		
		xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
									          applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
		xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));	
		try {
			xw.println(" ObjectQMgrName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_OBJECT_Q_MGR_NAME));
		} catch (MQException mqe) {;}
		
		// New field for 7.1
		try {
			this.ps.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		} catch (MQException mqe) {;}
		
		// New field for 7.1
		try {
			this.ps.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		} catch (MQException mqe) {;}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_BASE_OBJECT_NAME);
	} // end of method displayAlias_Base_Queue_Type_Error()
	
	private String displayBridge_Started(PCFMessage pcfMsg)
		throws PCFException {
	
		int bridgeType = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_BRIDGE_TYPE);

		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Bridge Started - MQRC_BRIDGE_STARTED (2125, X'84D')");
		xw.println("      EventType: IMS Bridge");
		xw.println("    Description: The IMS bridge has been started.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.print("     BridgeType: " + bridgeType + " (");
		
		switch (bridgeType) {
			case CMQCFC.MQBT_OTMA: 
				xw.println("MQBT_OTMA)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
		
		xw.println("     BridgeName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_BRIDGE_NAME));
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_BRIDGE_NAME);
	} // end of method displayBridge_Started()
	
	private String displayBridge_Stopped(PCFMessage pcfMsg)
		throws PCFException {
	
		int reasonQualifier = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_REASON_QUALIFIER);
		int bridgeType = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_BRIDGE_TYPE);
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Bridge Stopped - MQRC_BRIDGE_STOPPED (2126, X'84E')");
		xw.println("      EventType: IMS Bridge");
		xw.println("    Description: The IMS bridge has been stopped.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.print("ReasonQualifier: " + reasonQualifier + " (");
		
		switch (reasonQualifier) {
			case CMQCFC.MQRQ_BRIDGE_STOPPED_OK: 
				xw.println("MQRQ_BRIDGE_STOPPED_OK)");
				break;
			case CMQCFC.MQRQ_BRIDGE_STOPPED_ERROR: 
				xw.println("MQRQ_BRIDGE_STOPPED_ERROR)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
		
		xw.print("     BridgeType: " + bridgeType + " (");
		
		switch (bridgeType) {
			case CMQCFC.MQBT_OTMA: 
				xw.println("MQBT_OTMA)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
		
		xw.println("     BridgeName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_BRIDGE_NAME));
		
		if (reasonQualifier == CMQCFC.MQRQ_BRIDGE_STOPPED_ERROR) xw.println("ErrorIdentifier: " + pcfMsg.getIntParameterValue(CMQCFC.MQIACF_ERROR_IDENTIFIER));
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_BRIDGE_NAME);
	} // end of method displayBridge_Stopped()
	
	private String displayChange_Object(PCFMessage pcfMsg, int platform)
		throws PCFException {
		
		String retString = "";
		
		int eventOrigin = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_EVENT_ORIGIN);
		int objectType = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_OBJECT_TYPE);
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Change Object - MQRC_CONFIG_CHANGE_OBJECT (2368, X'940')");
		xw.println("      EventType: Configuration");
		xw.println("    Description: An ALTER or DEFINE REPLACE command or an MQSET call was");
		xw.println("                 issued that successfully changed an existing object.");
		xw.println("    EventUserId: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_USER_ID));
		
		try {
			xw.println("     SecurityId: X'" + toHex(new String(pcfMsg.getBytesParameterValue(CMQCFC.MQBACF_EVENT_SECURITY_ID))) + "'");
		} catch (MQException mqe) {;}
		
		xw.print("    EventOrigin: " + eventOrigin + " (");
		
		switch (eventOrigin) {
			case CMQCFC.MQEVO_CONSOLE: 
				xw.println("MQEVO_CONSOLE)");
				break;
			case CMQCFC.MQEVO_INIT: 
				xw.println("MQEVO_INIT)");
				break;
			case CMQCFC.MQEVO_INTERNAL: 
				xw.println("MQEVO_INTERNAL)");
				break;
			case CMQCFC.MQEVO_MQSET: 
				xw.println("MQEVO_MQSET)");
				break;
			case CMQCFC.MQEVO_MSG: 
				xw.println("MQEVO_MSG)");
				break;
			case CMQCFC.MQEVO_OTHER: 
				xw.println("MQEVO_OTHER)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
		
		xw.println("      EventQMgr: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_Q_MGR));
		
		if (eventOrigin == CMQCFC.MQEVO_MSG) {
			xw.println("EventAccntToken: X'" + toHex(new String(pcfMsg.getBytesParameterValue(CMQCFC.MQBACF_EVENT_ACCOUNTING_TOKEN))) + "'");
			xw.println("EventApplIdenty: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_APPL_IDENTITY));
			xw.println("  EventApplType: " + pcfMsg.getIntParameterValue(CMQCFC.MQIACF_EVENT_APPL_TYPE) + " (" +
					                              applTypeToString(pcfMsg.getIntParameterValue(CMQCFC.MQIACF_EVENT_APPL_TYPE)) + ")");
			xw.println("  EventApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_APPL_NAME));
			xw.println("EventApplOrigin: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_APPL_ORIGIN));
		}
		
		xw.print("     ObjectType: " + objectType + " (");
		
		switch (objectType) {
			case CMQC.MQOT_CHANNEL: 
				xw.println("MQOT_CHANNEL)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
				retString = "MQOT_CHANNEL ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
				break;
			// New for 7.1
			case CMQC.MQOT_CHLAUTH: 
				xw.println("MQOT_CHLAUTH)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
				retString = "MQOT_CHLAUTH ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
				break;
			case CMQC.MQOT_NAMELIST: 
				xw.println("MQOT_NAMELIST)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_NAMELIST_NAME));
				retString = "MQOT_NAMELIST ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_NAMELIST_NAME);
				break;
			// New for 7.1
			case CMQC.MQOT_NONE: 
				xw.println("MQOT_NONE)");
				xw.println("     ObjectName: " + "");
				retString = "MQOT_NONE";
				break;
			case CMQC.MQOT_PROCESS: 
				xw.println("MQOT_PROCESS)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_PROCESS_NAME));
				retString = "MQOT_PROCESS ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_PROCESS_NAME);
				break;
			case CMQC.MQOT_Q: 
				xw.println("MQOT_Q)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
				retString = "MQOT_Q ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME);
				break;
			case CMQC.MQOT_Q_MGR: 
				xw.println("MQOT_Q_MGR)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
				retString = "MQOT_Q_MGR ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME);
				break;
			case CMQC.MQOT_STORAGE_CLASS: 
				xw.println("MQOT_STORAGE_CLASS)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_STORAGE_CLASS));
				retString = "MQOT_STORAGE_CLASS ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_STORAGE_CLASS);
				break;
			case CMQC.MQOT_AUTH_INFO: 
				xw.println("MQOT_AUTH_INFO)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_AUTH_INFO_NAME));
				retString = "MQOT_AUTH_INFO ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_AUTH_INFO_NAME);
				break;
			case CMQC.MQOT_CF_STRUC: 
				xw.println("MQOT_CF_STRUC)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_CF_STRUC_NAME));
				retString = "MQOT_CF_STRUC ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_CF_STRUC_NAME);
				break;
			// New in 7.1
			case CMQC.MQOT_TOPIC: 
				xw.println("MQOT_TOPIC)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_TOPIC_NAME));
				retString = "MQOT_TOPIC ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_TOPIC_NAME);
				break;
			case CMQC.MQOT_COMM_INFO: 
				xw.println("MQOT_COMM_INFO)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_COMM_INFO_NAME));
				retString = "MQOT_COMM_INFO ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_COMM_INFO_NAME);
				break;
			case CMQC.MQOT_LISTENER: 
				xw.println("MQOT_LISTENER)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_LISTENER_NAME));
				retString = "MQOT_LISTENER ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_LISTENER_NAME);
				break;
			// Support for WebSphere MQ AMS 7.0.1.2
			case CMQC.MQOT_PROT_POLICY: 
				xw.println("MQOT_PROT_POLICY)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_POLICY_NAME));
				retString = "MQOT_PROT_POLICY ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_POLICY_NAME);
				break;
			default:
				xw.println("Unknown!)");
			    retString = "Unknown!";
		} // end switch
		
		if (platform == CMQC.MQPL_ZOS && objectType != CMQC.MQOT_Q_MGR && objectType != CMQC.MQOT_CF_STRUC) {
			int disposition  = pcfMsg.getIntParameterValue(CMQC.MQIA_QSG_DISP);
			
			xw.print("    Disposition: " + objectType + " (");
		
			switch (disposition) {
				case CMQC.MQQSGD_Q_MGR: 
					xw.println("MQQSGD_Q_MGR)");
					break;
				case CMQC.MQQSGD_SHARED: 
					xw.println("MQQSGD_SHARED)");
					break;
				case CMQC.MQQSGD_GROUP: 
					xw.println("MQQSGD_GROUP)");
					break;
				case CMQC.MQQSGD_COPY: 
					xw.println("MQQSGD_COPY)");
					break;
				default:
					xw.println("Unknown!)");
			} // end switch
		} // end if
		
		if (this.displayDetails) {
			//int messageCount = 0;
		
			//xw.println("Message " + ++messageCount + ": " + pcfMsg + "\n");
			xw.println();
			xw.println("**************************** PCF Message Dump Start ****************************");
			xw.println(pcfMsg);
			xw.println("**************************** PCF Message Dump End   ****************************");
			xw.println();
		}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_Q_MGR)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " - " + retString;
	} // end of method displayChange_Object()
	
	private String displayChannel_Activated(PCFMessage pcfMsg)
		throws PCFException {
		
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Channel Activated - MQRC_CHANNEL_ACTIVATED (2295, X'8F7')");
		xw.println("      EventType: Channel");
		xw.println("    Description: This condition is detected when a channel that has been");
		xw.println("                 waiting to become active, and for which a Channel Not");
		xw.println("                 Activated event has been generated, is now able to become");
		xw.println("                 active, because an active slot has been released by another");
		xw.println("                 channel. This event is not generated for a channel that is able");
		xw.println("                 to become active without waiting for an active slot to be");
		xw.println("                 released.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		
		try {
			xw.println("      XmitQName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_XMIT_Q_NAME));
		} catch (MQException mqe) {;}
		try {
			xw.println(" ConnectionName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		} catch (MQException mqe) {;}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
	} // end of method displayChannel_Activated()
	
	private String displayChannel_Auto_Definition_Error(PCFMessage pcfMsg)
		throws PCFException {
	
		int channelType = pcfMsg.getIntParameterValue(CMQCFC.MQIACH_CHANNEL_TYPE);
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Channel Auto-Definition Error - MQRC_CHANNEL_AUTO_DEF_ERROR (2234, X'8BA')");
		xw.println("      EventType: Channel");
		xw.println("    Description: This condition is detected when the automatic definition of a channel");
		xw.println("                 fails; this may be because an error occurred during the definition");
		xw.println("                 process, or because the channel automatic-definition exit inhibited");
		xw.println("                 the definition. Additional information indicating the reason for the");
		xw.println("                 failure is returned in the event message.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		xw.print("    ChannelType: " + channelType + " (");
		
		switch (channelType) {
			case CMQXC.MQCHT_RECEIVER: 
				xw.println("MQCHT_RECEIVER)");
				break;
			case CMQXC.MQCHT_SVRCONN: 
				xw.println("MQCHT_SVRCONN)");
				break;
			case CMQXC.MQCHT_CLUSSDR: 
				xw.println("MQCHT_CLUSSDR)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
	
		xw.println("ErrorIdentifier: " + pcfMsg.getIntParameterValue(CMQCFC.MQIACF_ERROR_IDENTIFIER));
		xw.println(" ConnectionName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		
		try {
			xw.println(" AuxErrDataInt1: " + pcfMsg.getIntParameterValue(CMQCFC.MQIACF_AUX_ERROR_DATA_INT_1));
		} catch (MQException mqe) {;}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
	} // end of method displayChannel_Auto_Definition_Error()
	
	private String displayChannel_Auto_Definition_OK(PCFMessage pcfMsg)
		throws PCFException {
	
		int channelType = pcfMsg.getIntParameterValue(CMQCFC.MQIACH_CHANNEL_TYPE);
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Channel Auto-Definition OK - MQRC_CHANNEL_AUTO_DEF_OK (2233, X'8B9')");
		xw.println("      EventType: Channel");
		xw.println("    Description: This condition is detected when the automatic definition of");
		xw.println("                 a channel is successful. The channel is defined by the MCA.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		xw.print("    ChannelType: " + channelType + " (");
		
		switch (channelType) {
			case CMQXC.MQCHT_RECEIVER: 
				xw.println("MQCHT_RECEIVER)");
				break;
			case CMQXC.MQCHT_SVRCONN: 
				xw.println("MQCHT_SVRCONN)");
				break;
			case CMQXC.MQCHT_CLUSSDR: 
				xw.println("MQCHT_CLUSSDR)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch

		xw.println(" ConnectionName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));	
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
	} // end of method displayChannel_Auto_Definition_OK()
	
	private String displayChannel_Blocked(PCFMessage pcfMsg)
		throws PCFException {
		
		PCFMessage pcfResp[] = (PCFMessage[])null;
        PCFMessage inquireChlAuth = null;
        String channelName = null;
        String connectionName = null;
        String clientUserId = null;
        String remoteQmgrName = null;
        String sslPeerName = null;
        String retString = "";
        
		int reasonCode = pcfMsg.getReason();
		int reasonQualifier = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_REASON_QUALIFIER);
		
		xw.println("     ReasonCode: " + reasonCode);
			
		if (reasonCode == CMQC.MQRC_CHANNEL_BLOCKED) xw.println("      EventName: Channel Blocked - MQRC_CHANNEL_BLOCKED (2577, X'A11')");
		else xw.println("      EventName: Channel Blocked (warning) - MQRC_CHANNEL_BLOCKED_WARNING (2578, X'A12')");
			
		xw.println("      EventType: Channel");
											  
		xw.println("    Description: This event is issued when an attempt to start an inbound");
		xw.println("                 channel is blocked. For MQRC_CHANNEL_BLOCKED_WARNING,");
		xw.println("                 temporary access has been granted to the channel because the");
		xw.println("                 channel authentication record is defined with WARN set to YES.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.print("ReasonQualifier: " + reasonQualifier + " (");
			
		switch (reasonQualifier) {
			case CMQCFC.MQRQ_CHANNEL_BLOCKED_ADDRESS: 
				xw.println("MQRQ_CHANNEL_BLOCKED_ADDRESS)");
				break;
			case CMQCFC.MQRQ_CHANNEL_BLOCKED_USERID: 
				xw.println("MQRQ_CHANNEL_BLOCKED_USERID)");
				break;
			case CMQCFC.MQRQ_CHANNEL_BLOCKED_NOACCESS: 
				xw.println("MQRQ_CHANNEL_BLOCKED_NOACCESS)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch

		if (reasonQualifier != CMQCFC.MQRQ_CHANNEL_BLOCKED_ADDRESS) {
			channelName = pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
			xw.println("    ChannelName: " + channelName);
			retString = channelName;
		} // end if
		
		if (reasonQualifier == CMQCFC.MQRQ_CHANNEL_BLOCKED_USERID) xw.println( " UserIdentifier: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_USER_IDENTIFIER));
		
		connectionName = pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME);
		xw.println(" ConnectionName: " + connectionName);
			
		try {
			remoteQmgrName = pcfMsg.getStringParameterValue(CMQC.MQCA_REMOTE_Q_MGR_NAME);
			xw.println(" RemoteQMgrName: " + remoteQmgrName);
		} catch (MQException mqe) {;}
			
		try {
			sslPeerName = pcfMsg.getStringParameterValue(CMQCFC.MQCACH_SSL_PEER_NAME);
			xw.println("    SSLPeerName: " + sslPeerName);
		} catch (MQException mqe) {;}
			
		if (reasonQualifier != CMQCFC.MQRQ_CHANNEL_BLOCKED_ADDRESS) {
			try {
				clientUserId = pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CLIENT_USER_ID);
				xw.println("   ClientUserId: " + clientUserId);
			} catch (MQException mqe) {;}
				
			try {
				xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
				           					     applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
			} catch (MQException mqe) {;}
				
				
			try {
				xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));
			} catch (MQException mqe) {;}
		} // end if
			
		if (pcfAgent == null) createPCFAgent(qmgr);
		
	    inquireChlAuth = new PCFMessage(CMQCFC.MQCMD_INQUIRE_CHLAUTH_RECS);
	    
	    if (channelName == null) {
	    	inquireChlAuth.addParameter(CMQCFC.MQCACH_CHANNEL_NAME, "*");
	        inquireChlAuth.addParameter(CMQCFC.MQIACF_CHLAUTH_TYPE, CMQCFC.MQCAUT_BLOCKADDR);
	        inquireChlAuth.addParameter(CMQCFC.MQCACH_CONNECTION_NAME, connectionName);
	    } else {
	        inquireChlAuth.addParameter(CMQCFC.MQCACH_CHANNEL_NAME, channelName);
	        inquireChlAuth.addParameter(CMQCFC.MQIACH_MATCH, CMQCFC.MQMATCH_RUNCHECK);
	        inquireChlAuth.addParameter(CMQCFC.MQCACH_CONNECTION_NAME, connectionName);
	        if (remoteQmgrName != null) inquireChlAuth.addParameter(CMQC.MQCA_REMOTE_Q_MGR_NAME, remoteQmgrName);
	 	    else inquireChlAuth.addParameter(CMQCFC.MQCACH_CLIENT_USER_ID, clientUserId);
	        if (sslPeerName != null) inquireChlAuth.addParameter(CMQCFC.MQCACH_SSL_PEER_NAME, sslPeerName);
	    } // end if
	    
	    //inquireChlAuth.addParameter(CMQCFC.MQCACH_CONNECTION_NAME, connectionName);
	    
	    //if (reasonQualifier != CMQCFC.MQRQ_CHANNEL_BLOCKED_ADDRESS) inquireChlAuth.addParameter(CMQCFC.MQCACH_CLIENT_USER_ID, clientUserId);
	   // if (remoteQmgrName != null) inquireChlAuth.addParameter(CMQC.MQCA_REMOTE_Q_MGR_NAME, remoteQmgrName);
	   // else inquireChlAuth.addParameter(CMQCFC.MQCACH_CLIENT_USER_ID, clientUserId);
	    //if (reasonQualifier == CMQCFC.MQRQ_CHANNEL_BLOCKED_USERID) inquireChlAuth.addParameter(CMQCFC.MQCACH_CLIENT_USER_ID, clientUserId);
	    //if (sslPeerName != null) inquireChlAuth.addParameter(CMQCFC.MQCACH_SSL_PEER_NAME, sslPeerName);
	        
	    try {
	    	//System.out.println(inquireChlAuth);
	    	pcfResp = pcfAgent.send(inquireChlAuth);
	        displayChannel_Authentication_Record(pcfResp);
	    } catch(MQException mqe) {
	    	if (mqe.reasonCode == CMQCFC.MQRCCF_CHLAUTH_NOT_FOUND) xw.println("   Chl Auth Rec: Match(RUNCHECK) did not return a record!");
	        else xw.println("   Chl Auth Rec: Failed with error " + mqe.getLocalizedMessage());
	    } catch(IOException ioexception) { }
	    
	    return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + retString;
	} // end of method displayChannel_Blocked()
	
	private void displayChannel_Authentication_Record(PCFMessage pcfResp[]) {
			
		String custom, mcaUser, address, userSrc, warn, altDate, altTime, userList, sslPeer, clntUser, qmName;
		
        for(int i = 0; i < pcfResp.length; i++)
            try
            {
                int type = pcfResp[i].getIntParameterValue(1352);
                switch(type)
                {
                case 1: // '\001'         	
                	try {
                		custom = rtrim(pcfResp[i].getStringParameterValue(2119));
                	} catch (PCFException pcfe) { custom = ""; }
                	
                	try {
                		userList = rtrim(pcfResp[i].getStringParameterValue(3568));
                	} catch (PCFException pcfe) { userList = ""; }
                	
                	try {
                		warn = warnToString(pcfResp[i].getIntParameterValue(1639));
                	} catch (PCFException pcfe) { warn = ""; }
                	
                	try {
                		altDate = rtrim(pcfResp[i].getStringParameterValue(2027));
                	} catch (PCFException pcfe) { altDate = ""; }
                	
                	try {
                		altTime = pcfResp[i].getStringParameterValue(2028);
                	} catch (PCFException pcfe) { altTime = ""; }
                	
                    xw.println("   Chl Auth Rec: CHLAUTH(" + rtrim(pcfResp[i].getStringParameterValue(3501)) + ") TYPE(BLOCKUSER)");
                    xw.println("                 DESC(" + rtrim(pcfResp[i].getStringParameterValue(2118)) + ")");
                    xw.println("                 CUSTOM(" + custom + ") USERLIST(" + userList + ")");
                    xw.println("                 WARN(" + warn + ") ALTDATE(" + altDate + ")");
                    xw.println("                 ALTTIME(" + altTime + ")");
                    break;

                case 2: // '\002'
                	try {
                		custom = rtrim(pcfResp[i].getStringParameterValue(2119));
                	} catch (PCFException pcfe) { custom = ""; }
                	
                	try {
                		warn = warnToString(pcfResp[i].getIntParameterValue(1639));
                	} catch (PCFException pcfe) { warn = ""; }
                	
                	try {
                		altDate = rtrim(pcfResp[i].getStringParameterValue(2027));
                	} catch (PCFException pcfe) { altDate = ""; }
                	
                	try {
                		altTime = pcfResp[i].getStringParameterValue(2028);
                	} catch (PCFException pcfe) { altTime = ""; }
                	
                    xw.println("   Chl Auth Rec: CHLAUTH(" + rtrim(pcfResp[i].getStringParameterValue(3501)) + ") TYPE(BLOCKADDR)");
                    xw.println("                 DESC(" + rtrim(pcfResp[i].getStringParameterValue(2118)) + ")");
                    xw.print("                 CUSTOM(" + custom + ") ADDRLIST(");
                    
                    if(pcfResp[i].getParameterValue(3566) instanceof String)
                    {
                        xw.print(rtrim(pcfResp[i].getStringParameterValue(3566)));
                    } else
                    {
                        String addrList[] = pcfResp[i].getStringListParameterValue(3566);
                        for(int j = 0; j < addrList.length; j++)
                        {
                            if(j != 0)
                                xw.print(", ");
                            xw.print(rtrim(addrList[j].replace('"', ' ')));
                        }

                    }
                    xw.println(")");
                    xw.println("                 WARN(" + warn + ") ALTDATE(" + altDate + ")");
                    xw.println("                 ALTTIME(" + altTime + ")");
                    break;

                case 3: // '\003'
                	try {
                		sslPeer = rtrim(pcfResp[i].getStringParameterValue(3545));
                	} catch (PCFException pcfe) { sslPeer = ""; }
                	
                	try {
                		custom = rtrim(pcfResp[i].getStringParameterValue(2119));
                	} catch (PCFException pcfe) { custom = ""; }
                	
                	try {
                		mcaUser = rtrim(pcfResp[i].getStringParameterValue(3527));
                	} catch (PCFException pcfe) { mcaUser = ""; }
                	
                	try {
                		address = rtrim(pcfResp[i].getStringParameterValue(3506));
                	} catch (PCFException pcfe) { address = ""; }
                	
                	try {
                		userSrc = userSourceToString(pcfResp[i].getIntParameterValue(1638));
                	} catch (PCFException pcfe) { userSrc = ""; }
                	
                	try {
                		warn = warnToString(pcfResp[i].getIntParameterValue(1639));
                	} catch (PCFException pcfe) { warn = ""; }
                	
                	try {
                		altDate = rtrim(pcfResp[i].getStringParameterValue(2027));
                	} catch (PCFException pcfe) { altDate = ""; }
                	
                	try {
                		altTime = pcfResp[i].getStringParameterValue(2028);
                	} catch (PCFException pcfe) { altTime = ""; }
                	
                    xw.println("   Chl Auth Rec: CHLAUTH(" + rtrim(pcfResp[i].getStringParameterValue(3501)) + ") TYPE(SSLPEERMAP)");
                    xw.println("                 DESC(" + rtrim(pcfResp[i].getStringParameterValue(2118)) + ")");
                    xw.println("                 SSLPEER(" + sslPeer + ")");
                    xw.println("                 CUSTOM(" + custom + ") ADDRESS(" + address + ")");
                    xw.println("                 MCAUSER(" + mcaUser + ") USERSRC(" + userSrc + ")");
                    xw.println("                 WARN(" + warn + ") ALTDATE(" + altDate + ")");
                    xw.println("                 ALTTIME(" + altTime + ")");
                    break;

                case 4: // '\004'
                	try {
                		custom = rtrim(pcfResp[i].getStringParameterValue(2119));
                	} catch (PCFException pcfe) { custom = ""; }
                	
                	try {
                		mcaUser = rtrim(pcfResp[i].getStringParameterValue(3527));
                	} catch (PCFException pcfe) { mcaUser = ""; }
                	
                	try {
                		address = rtrim(pcfResp[i].getStringParameterValue(3506));
                	} catch (PCFException pcfe) { address = ""; }
                	
                	try {
                		userSrc = userSourceToString(pcfResp[i].getIntParameterValue(1638));
                	} catch (PCFException pcfe) { userSrc = ""; }
                	
                	try {
                		warn = warnToString(pcfResp[i].getIntParameterValue(1639));
                	} catch (PCFException pcfe) { warn = ""; }
                	
                	try {
                		altDate = rtrim(pcfResp[i].getStringParameterValue(2027));
                	} catch (PCFException pcfe) { altDate = ""; }
                	
                	try {
                		altTime = pcfResp[i].getStringParameterValue(2028);
                	} catch (PCFException pcfe) { altTime = ""; }
                	
                    xw.println("   Chl Auth Rec: CHLAUTH(" + rtrim(pcfResp[i].getStringParameterValue(3501)) + ") TYPE(ADDRESSMAP)");
                    xw.println("                 DESC(" + rtrim(pcfResp[i].getStringParameterValue(2118)) + ")");
                    xw.println("                 CUSTOM(" + custom + ") ADDRESS(" + address + ")");
                    xw.println("                 MCAUSER(" + mcaUser + ") USERSRC(" + userSrc + ")");
                    xw.println("                 WARN(" + warn + ") ALTDATE(" + altDate + ")");
                    xw.println("                 ALTTIME(" + altTime + ")");
                    break;

                case 5: // '\005'
                	try {
                		custom = rtrim(pcfResp[i].getStringParameterValue(2119));
                	} catch (PCFException pcfe) { custom = ""; }
                	
                	try {
                		mcaUser = rtrim(pcfResp[i].getStringParameterValue(3527));
                	} catch (PCFException pcfe) { mcaUser = ""; }
                	
                	try {
                		address = rtrim(pcfResp[i].getStringParameterValue(3506));
                	} catch (PCFException pcfe) { address = ""; }
                	
                	try {
                		clntUser = rtrim(pcfResp[i].getStringParameterValue(3567));
                	} catch (PCFException pcfe) { clntUser = ""; }
                	
                	try {
                		userSrc = userSourceToString(pcfResp[i].getIntParameterValue(1638));
                	} catch (PCFException pcfe) { userSrc = ""; }
                	
                	try {
                		warn = warnToString(pcfResp[i].getIntParameterValue(1639));
                	} catch (PCFException pcfe) { warn = ""; }
                	
                	try {
                		altDate = rtrim(pcfResp[i].getStringParameterValue(2027));
                	} catch (PCFException pcfe) { altDate = ""; }
                	
                	try {
                		altTime = pcfResp[i].getStringParameterValue(2028);
                	} catch (PCFException pcfe) { altTime = ""; }
                	
                    xw.println("   Chl Auth Rec: CHLAUTH(" + rtrim(pcfResp[i].getStringParameterValue(3501)) + ") TYPE(USERMAP)");
                    xw.println("                 DESC(" + rtrim(pcfResp[i].getStringParameterValue(2118)) + ")");
                    xw.println("                 CUSTOM(" + custom + ") ADDRESS(" + address + ")");
                    xw.println("                 CLNTUSER(" + clntUser + ") MCAUSER(" + mcaUser + ")");
                    xw.println("                 USERSRC(" + userSrc + ") WARN(" + warn + ")");
                    xw.println("                 ALTDATE(" + altDate + ") ALTTIME(" + altTime + ")");
                    break;

                case 6: // '\006'
                	try {
                		custom = rtrim(pcfResp[i].getStringParameterValue(2119));
                	} catch (PCFException pcfe) { custom = ""; }
                	
                	try {
                		mcaUser = rtrim(pcfResp[i].getStringParameterValue(3527));
                	} catch (PCFException pcfe) { mcaUser = ""; }
                	
                	try {
                		address = rtrim(pcfResp[i].getStringParameterValue(3506));
                	} catch (PCFException pcfe) { address = ""; }
                	
                	try {
                		qmName = rtrim(pcfResp[i].getStringParameterValue(2017));
                	} catch (PCFException pcfe) { qmName = ""; }
                	
                	try {
                		userSrc = userSourceToString(pcfResp[i].getIntParameterValue(1638));
                	} catch (PCFException pcfe) { userSrc = ""; }
                	
                	try {
                		warn = warnToString(pcfResp[i].getIntParameterValue(1639));
                	} catch (PCFException pcfe) { warn = ""; }
                	
                	try {
                		altDate = rtrim(pcfResp[i].getStringParameterValue(2027));
                	} catch (PCFException pcfe) { altDate = ""; }
                	
                	try {
                		altTime = pcfResp[i].getStringParameterValue(2028);
                	} catch (PCFException pcfe) { altTime = ""; }
                	
                    xw.println("   Chl Auth Rec: CHLAUTH(" + rtrim(pcfResp[i].getStringParameterValue(3501)) + ") TYPE(QMGRMAP)");
                    xw.println("                 DESC(" + rtrim(pcfResp[i].getStringParameterValue(2118)) + ")");
                    xw.println("                 CUSTOM(" + custom + ") ADDRESS(" + address + ")");
                    xw.println("                 QMNAME(" + qmName + ") MCAUSER(" + mcaUser + ")");
                    xw.println("                 USERSRC(" + userSrc + ") WARN(" + warn + ")");
                    xw.println("                 ALTDATE(" + altDate + ") ALTTIME(" + altTime + ")");
                    break;

                default:
                    xw.println("Channel authentication record type is unknown!");
                    break;
                }
            }
            catch(PCFException pcfexception) { }
            
            //System.out.println("*******");
            //for (int k=1; k <= xw.getTotalLines(); k++) System.out.println(xw.getLine(k));
            //System.out.println("*******");
            
    } // end of method displayChannel_Authentication_Record()
	
	private String displayChannel_Conversion_Error(PCFMessage pcfMsg)
		throws PCFException {
	
		int convReasonCode = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_CONV_REASON_CODE);
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Channel Conversion Error - MQRC_CHANNEL_CONV_ERROR (2284, X'8EC')");
		xw.println("      EventType: Channel");
		xw.println("    Description: This condition is detected when a channel is unable to carry");
		xw.println("                 out data conversion and the MQGET call to get a message from");
		xw.println("                 the transmission queue resulted in a data conversion error.");
		xw.println("                 The reason for the failure is identified by ConversionReasonCode.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.print(" ConvReasonCode: " + convReasonCode + " (");
		
		switch (convReasonCode) {
			case CMQC.MQRC_CONVERTED_MSG_TOO_BIG: 
				xw.println("MQRC_CONVERTED_MSG_TOO_BIG)");
				break;
			case CMQC.MQRC_FORMAT_ERROR: 
				xw.println("MQRC_FORMAT_ERROR)");
				break;
			case CMQC.MQRC_NOT_CONVERTED: 
				xw.println("MQRC_NOT_CONVERTED)");
				break;
			case CMQC.MQRC_SOURCE_CCSID_ERROR: 
				xw.println("MQRC_SOURCE_CCSID_ERROR)");
				break;
			case CMQC.MQRC_SOURCE_DECIMAL_ENC_ERROR: 
				xw.println("MQRC_SOURCE_DECIMAL_ENC_ERROR)");
				break;
			case CMQC.MQRC_SOURCE_FLOAT_ENC_ERROR: 
				xw.println("MQRC_SOURCE_FLOAT_ENC_ERROR)");
				break;
			case CMQC.MQRC_SOURCE_INTEGER_ENC_ERROR: 
				xw.println("MQRC_SOURCE_INTEGER_ENC_ERROR)");
				break;
			case CMQC.MQRC_TARGET_CCSID_ERROR: 
				xw.println("MQRC_TARGET_CCSID_ERROR)");
				break;
			case CMQC.MQRC_TARGET_DECIMAL_ENC_ERROR: 
				xw.println("MQRC_TARGET_DECIMAL_ENC_ERROR)");
				break;
			case CMQC.MQRC_TARGET_FLOAT_ENC_ERROR: 
				xw.println("MQRC_TARGET_FLOAT_ENC_ERROR)");
				break;
			case CMQC.MQRC_TARGET_INTEGER_ENC_ERROR: 
				xw.println("MQRC_TARGET_INTEGER_ENC_ERROR)");
				break;
			case CMQC.MQRC_TRUNCATED_MSG_ACCEPTED: 
				xw.println("MQRC_TRUNCATED_MSG_ACCEPTED)");
				break;
			case CMQC.MQRC_TRUNCATED_MSG_FAILED: 
				xw.println("MQRC_TRUNCATED_MSG_FAILED)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
		
		xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		xw.println("         Format: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_FORMAT_NAME));
		xw.println("      XmitQName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_XMIT_Q_NAME));
		xw.println(" ConnectionName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
	} // end of method displayChannel_Conversion_Error()
	
	private String displayChannel_Not_Activated(PCFMessage pcfMsg)
		throws PCFException {
		
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Channel Not Activated - MQRC_CHANNEL_NOT_ACTIVATED (2296, X'8F8')");
		xw.println("      EventType: Channel");
		xw.println("    Description: This condition is detected when a channel is required to become");
		xw.println("                 active, either because it is starting, or because it is about to");
		xw.println("                 make another attempt to establish connection with its partner.");
		xw.println("                 However, it is unable to do so because the limit on the number of");
		xw.println("                 active channels has been reached. Check the MaxActiveChannels");
		xw.println("                 parameter on distributed platforms or ACTCHL on z/OS.");
		xw.println("                 The channel waits until it is able to take over an active");
		xw.println("                 slot released when another channel ceases to be active. At");
		xw.println("                 that time a Channel Activated event is generated");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		try {
			xw.println("      XmitQName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_XMIT_Q_NAME));
		} catch (MQException mqe) {;}
		try {
			xw.println(" ConnectionName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		} catch (MQException mqe) {;}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
	} // end of method displayChannel_Not_Activated()
	
	private String displayChannel_Not_Available(PCFMessage pcfMsg)
		throws PCFException {
		
		int reasonQualifier = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_REASON_QUALIFIER);
			
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Channel Not Available - MQRC_CHANNEL_NOT_AVAILABLE (2537, X'9E9')");
		xw.println("      EventType: Channel");
		xw.println("    Description: This is issued when an attempt to start an inbound channel");
		xw.println("                 is rejected");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
			
		xw.print("ReasonQualifier: " + reasonQualifier + " (");
			
			switch (reasonQualifier) {
				case CMQCFC.MQRQ_MAX_ACTIVE_CHANNELS: 
					xw.println("MQRQ_MAX_ACTIVE_CHANNELS)");
					break;
				case CMQCFC.MQRQ_MAX_CHANNELS : 
					xw.println("MQRQ_MAX_CHANNELS )");
					break;
				case CMQCFC.MQRQ_SVRCONN_INST_LIMIT : 
					xw.println("MQRQ_SVRCONN_INST_LIMIT)");
					break;
				case CMQCFC.MQRQ_CLIENT_INST_LIMIT: 
					xw.println("MQRQ_CLIENT_INST_LIMIT)");
					break;
				case CMQCFC.MQRQ_CAF_NOT_INSTALLED: 
					xw.println("MQRQ_CAF_NOT_INSTALLED)");
					break;
				default:
					xw.println("Unknown!)");
			} // end switch
			
			xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
			
			try {
				xw.println(" ConnectionName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
			} catch (MQException mqe) {;}
			
			if (reasonQualifier == CMQCFC.MQRQ_MAX_ACTIVE_CHANNELS) xw.println(" MaxActChannels: " + pcfMsg.getIntParameterValue(CMQC.MQIA_ACTIVE_CHANNELS));
			if (reasonQualifier == CMQCFC.MQRQ_MAX_CHANNELS) xw.println("    MaxChannels: " + pcfMsg.getIntParameterValue(CMQC.MQIA_MAX_CHANNELS));
			if (reasonQualifier == CMQCFC.MQRQ_SVRCONN_INST_LIMIT) xw.println("   MaxInstances: " + pcfMsg.getIntParameterValue(CMQCFC.MQIACH_MAX_INSTANCES));
			if (reasonQualifier == CMQCFC.MQRQ_CLIENT_INST_LIMIT) xw.println("MaxCltInstances: " + pcfMsg.getIntParameterValue(CMQCFC.MQIACH_MAX_INSTS_PER_CLIENT));

			return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
		} // end of method displayChannel_Not_Available()
	
	private String displayChannel_SSL_Error(PCFMessage pcfMsg)
		throws PCFException {
	
		String retString = "";
		int reasonQualifier = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_REASON_QUALIFIER);
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Channel SSL Error - MQRC_CHANNEL_SSL_ERROR (2371, X'943')");
		xw.println("      EventType: SSL");
		xw.println("    Description: This condition is detected when a channel using Secure");
		xw.println("                 Sockets Layer (SSL) or Transport Layer Security (TLS)");
		xw.println("                 fails to establish a connection. ReasonQualifier");
		xw.println("                 identifies the nature of the error.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.print("ReasonQualifier: " + reasonQualifier + " (");
		
		switch (reasonQualifier) {
			case CMQCFC.MQRQ_SSL_HANDSHAKE_ERROR: 
				xw.println("MQRQ_SSL_HANDSHAKE_ERROR)");
				break;
			case CMQCFC.MQRQ_SSL_CIPHER_SPEC_ERROR: 
				xw.println("MQRQ_SSL_CIPHER_SPEC_ERROR)");
				break;
			case CMQCFC.MQRQ_SSL_PEER_NAME_ERROR: 
				xw.println("MQRQ_SSL_PEER_NAME_ERROR)");
				break;
			case CMQCFC.MQRQ_SSL_CLIENT_AUTH_ERROR: 
				xw.println("MQRQ_SSL_CLIENT_AUTH_ERROR)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
		
		try {
			retString = pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
			xw.println("    ChannelName: " + retString);
		} catch (MQException mqe) {;}
		try {
			xw.println("      XmitQName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_XMIT_Q_NAME));
		} catch (MQException mqe) {;}
		try {
			xw.println(" ConnectionName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		} catch (MQException mqe) {;}
		
		if (reasonQualifier == CMQCFC.MQRQ_SSL_HANDSHAKE_ERROR) xw.println("SSLHandshkStage: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_SSL_HANDSHAKE_STAGE));
		if (reasonQualifier == CMQCFC.MQRQ_SSL_HANDSHAKE_ERROR) xw.println("  SSLReturnCode: " + pcfMsg.getIntParameterValue(CMQCFC.MQIACH_SSL_RETURN_CODE));
		
		try {
			xw.println("    SSLPeerName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_SSL_PEER_NAME));
		} catch (MQException mqe) {;}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + retString;
	} // end of method displayChannel_SSL_Error()
	
	private String displayChannel_SSL_Warning(PCFMessage pcfMsg)
		throws PCFException {
		
		String retString = "";
		int reasonQualifier = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_REASON_QUALIFIER);
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Channel SSL Warning - MQRC_CHANNEL_SSL_WARNING (2552, X'9F8')");
		xw.println("      EventType: SSL");
		xw.println("    Description: This condition is detected when a channel using Secure");
		xw.println("                 Sockets Layer (SSL) or Transport Layer Security (TLS)");
		xw.println("                 experiences a problem that does not cause it to fail to");
		xw.println("                 establish an SSL or TLS connection. ReasonQualifier");
		xw.println("                 identifies the nature of the event.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.print("ReasonQualifier: " + reasonQualifier + " (");
		
		switch (reasonQualifier) {
			case CMQCFC.MQRQ_SSL_UNKNOWN_REVOCATION: 
				xw.println("MQRQ_SSL_UNKNOWN_REVOCATION)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
		
		try {
			retString = pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
			xw.println("    ChannelName: " + retString);
		} catch (MQException mqe) {;}
		try {
			xw.println("      XmitQName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_XMIT_Q_NAME));
		} catch (MQException mqe) {;}
		try {
			xw.println(" ConnectionName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		} catch (MQException mqe) {;}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + retString;
	} // end of method displayChannel_SSL_Warning()
	
	private String displayChannel_Started(PCFMessage pcfMsg)
		throws PCFException {
		
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Channel Started - MQRC_CHANNEL_STARTED (2282, X'8EA')");
		xw.println("      EventType: Channel");
		xw.println("   Desscription: Either an operator has issued a Start Channel command,");
		xw.println("                 or an instance of a channel has been successfully");
		xw.println("                 established. This condition is detected when Initial Data");
		xw.println("                 negotiation is complete and resynchronization has been");
		xw.println("                 performed where necessary, such that message transfer");
		xw.println("                 can proceed.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		try {
			xw.println("      XmitQName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_XMIT_Q_NAME));
		} catch (MQException mqe) {;}
		try {
			xw.println(" ConnectionName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		} catch (MQException mqe) {;}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
	} // end of method displayChannel_Started()
	
	private String displayChannel_Stopped(PCFMessage pcfMsg)
		throws PCFException {
	
		int reasonQualifier = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_REASON_QUALIFIER);
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Channel Stopped - MQRC_CHANNEL_STOPPED (2283, X'8EB')");
		xw.println("      EventType: Channel");
		xw.println("    Description: This is issued when a channel instance stops. It will");
		xw.println("                 only be issued if the channel instance previously issued");
		xw.println("                 a channel started event.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.print("ReasonQualifier: " + reasonQualifier + " (");
		
		switch (reasonQualifier) {
			case CMQCFC.MQRQ_CHANNEL_STOPPED_OK: 
				xw.println("MQRQ_CHANNEL_STOPPED_OK)");
				break;
			case CMQCFC.MQRQ_CHANNEL_STOPPED_ERROR: 
				xw.println("MQRQ_CHANNEL_STOPPED_ERROR)");
				break;
			case CMQCFC.MQRQ_CHANNEL_STOPPED_RETRY: 
				xw.println("MQRQ_CHANNEL_STOPPED_RETRY)");
				break;
			case CMQCFC.MQRQ_CHANNEL_STOPPED_DISABLED: 
				xw.println("MQRQ_CHANNEL_STOPPED_DISABLED)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
		
		xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		xw.println("ErrorIdentifier: " + pcfMsg.getIntParameterValue(CMQCFC.MQIACF_ERROR_IDENTIFIER));
		xw.println(" AuxErrDataInt1: " + pcfMsg.getIntParameterValue(CMQCFC.MQIACF_AUX_ERROR_DATA_INT_1));
		xw.println(" AuxErrDataInt2: " + pcfMsg.getIntParameterValue(CMQCFC.MQIACF_AUX_ERROR_DATA_INT_2));
		xw.println(" AuxErrDataStr1: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_AUX_ERROR_DATA_STR_1));
		xw.println(" AuxErrDataStr2: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_AUX_ERROR_DATA_STR_2));
		xw.println(" AuxErrDataStr3: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_AUX_ERROR_DATA_STR_3));
		try {
			xw.println("      XmitQName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_XMIT_Q_NAME));
		} catch (MQException mqe) {;}
		try {
			xw.println(" ConnectionName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		} catch (MQException mqe) {;}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
	} // end of method displayChannel_Stopped()
	
	private String displayChannel_Stopped_By_User(PCFMessage pcfMsg)
		throws PCFException {
	
		int reasonQualifier = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_REASON_QUALIFIER);
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Channel Stopped By User - MQRC_CHANNEL_STOPPED_BY_USER (2279, X'8E7')");
		xw.println("      EventType: Channel");
		xw.println("    Description: This is issued when a user issues a STOP CHL command.");
		xw.println("                 ReasonQualifier identifies the reasons for stopping.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.print("ReasonQualifier: " + reasonQualifier + " (");
		
		switch (reasonQualifier) {
			case CMQCFC.MQRQ_CHANNEL_STOPPED_DISABLED: 
				xw.println("MQRQ_CHANNEL_STOPPED_DISABLED)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
		
		xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
	} // end of method displayChannel_Stopped_By_User()
	
	private String displayCommand(PCFMessage pcfMsg, int platform)
		throws PCFException {
		
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.print("      EventName: Command - ");
		if (pcfMsg.getReason() == CMQC.MQRC_COMMAND_MQSC) xw.println("MQRC_COMMAND_MQSC (2412, X'96C')");
		else xw.println("MQRC_COMMAND_PCF (2413, X'96D')");
		
		xw.println("      EventType: Command");
		
		xw.println("    Description: Command successfully issued.");
		
		MQCFGR commandContext = (MQCFGR)pcfMsg.getParameter(CMQCFC.MQGACF_COMMAND_CONTEXT);
		
		int eventOrigin = commandContext.getIntParameterValue(CMQCFC.MQIACF_EVENT_ORIGIN);
	
		xw.println("    EventUserId: " + commandContext.getStringParameterValue(CMQCFC.MQCACF_EVENT_USER_ID));
		xw.print("    EventOrigin: " + eventOrigin + " (");
		
		switch (eventOrigin) {
			case CMQCFC.MQEVO_CONSOLE: 
				xw.println("MQEVO_CONSOLE)");
				break;
			case CMQCFC.MQEVO_INIT: 
				xw.println("MQEVO_INIT)");
				break;
			case CMQCFC.MQEVO_INTERNAL: 
				xw.println("MQEVO_INTERNAL)");
				break;
			case CMQCFC.MQEVO_MSG: 
				xw.println("MQEVO_MSG)");
				break;
			case CMQCFC.MQEVO_OTHER: 
				xw.println("MQEVO_OTHER)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
		
		xw.println("      EventQMgr: " + commandContext.getStringParameterValue(CMQCFC.MQCACF_EVENT_Q_MGR));
		
		if (eventOrigin == CMQCFC.MQEVO_MSG) {
			xw.println("EventAccntToken: X'" + toHex(new String(commandContext.getBytesParameterValue(CMQCFC.MQBACF_EVENT_ACCOUNTING_TOKEN))) + "'");
			xw.println("EventApplIdenty: " + commandContext.getStringParameterValue(CMQCFC.MQCACF_EVENT_APPL_IDENTITY));
			xw.println("  EventApplType: " + commandContext.getIntParameterValue(CMQCFC.MQIACF_EVENT_APPL_TYPE) + " (" +
					                              applTypeToString(commandContext.getIntParameterValue(CMQCFC.MQIACF_EVENT_APPL_TYPE)) + ")");
			xw.println("  EventApplName: " + commandContext.getStringParameterValue(CMQCFC.MQCACF_EVENT_APPL_NAME));
			xw.println("EventApplOrigin: " + commandContext.getStringParameterValue(CMQCFC.MQCACF_EVENT_APPL_ORIGIN));
		}
		
		int command = commandContext.getIntParameterValue(CMQCFC.MQIACF_COMMAND);
		
		xw.println("        Command: " + command + " (" + commandToString(command, false) + ")");
		
		MQCFGR commandData = (MQCFGR)pcfMsg.getParameter(CMQCFC.MQGACF_COMMAND_DATA);
		
		if (pcfMsg.getReason() == CMQC.MQRC_COMMAND_MQSC && platform == CMQC.MQPL_ZOS) xw.println("    CommandMQSC: " + commandData.getStringParameterValue(CMQCFC.MQCACF_COMMAND_MQSC));
		else {
			
			//if (pcfMsg.getReason() == CMQC.MQRC_COMMAND_MQSC) xw.println("    CommandMQSC: " + commandData.getStringParameterValue(CMQCFC.MQCACF_COMMAND_MQSC));
			//commandData.getIntListParameterValue(arg0)
			
			if (this.displayDetails) {

				//int messageCount = 0;
			
				//xw.println("Message " + ++messageCount + ": " + pcfMsg + "\n");
				//xw.println("Message: " + commandData + "\n");
				xw.println();
				xw.println("**************************** PCF Command Dump Start ***************************");
				xw.println(commandData);
				xw.println("**************************** PCF Command Dump End   ***************************");
				//xw.println();
			}
		}
		
		return rtrim(commandContext.getStringParameterValue(CMQCFC.MQCACF_EVENT_Q_MGR)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + commandToString(command, false);
	} // end of method displayCommand()
	
	private String displayCreate_Object(PCFMessage pcfMsg, int platform)
		throws PCFException {
		
		String retString = "";
		
		int eventOrigin = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_EVENT_ORIGIN);
		int objectType = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_OBJECT_TYPE);
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Create Object - MQRC_CONFIG_CREATE_OBJECT (2367, X'93F')");
		xw.println("      EventType: Configuration");
		xw.println("    Description: A DEFINE or DEFINE REPLACE command was issued which");
		xw.println("                 successfully created a new object.");
		xw.println("    EventUserId: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_USER_ID));
		
		try {
			xw.println("     SecurityId: X'" + toHex(new String(pcfMsg.getBytesParameterValue(CMQCFC.MQBACF_EVENT_SECURITY_ID))) + "'");
		} catch (MQException mqe) {;}
		
		xw.print("    EventOrigin: " + eventOrigin + " (");
		
		switch (eventOrigin) {
			case CMQCFC.MQEVO_CONSOLE: 
				xw.println("MQEVO_CONSOLE)");
				break;
			case CMQCFC.MQEVO_INIT: 
				xw.println("MQEVO_INIT)");
				break;
			case CMQCFC.MQEVO_INTERNAL: 
				xw.println("MQEVO_INTERNAL)");
				break;
			case CMQCFC.MQEVO_MQSET: 
				xw.println("MQEVO_MQSET)");
				break;
			case CMQCFC.MQEVO_MSG: 
				xw.println("MQEVO_MSG)");
				break;
			case CMQCFC.MQEVO_OTHER: 
				xw.println("MQEVO_OTHER)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
		
		xw.println("      EventQMgr: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_Q_MGR));
		
		if (eventOrigin == CMQCFC.MQEVO_MSG) {
			xw.println("EventAccntToken: X'" + toHex(new String(pcfMsg.getBytesParameterValue(CMQCFC.MQBACF_EVENT_ACCOUNTING_TOKEN))) + "'");
			xw.println("EventApplIdenty: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_APPL_IDENTITY));
			xw.println("  EventApplType: " + pcfMsg.getIntParameterValue(CMQCFC.MQIACF_EVENT_APPL_TYPE) + " (" +
					                              applTypeToString(pcfMsg.getIntParameterValue(CMQCFC.MQIACF_EVENT_APPL_TYPE)) + ")");
			xw.println("  EventApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_APPL_NAME));
			xw.println("EventApplOrigin: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_APPL_ORIGIN));
		}
		
		xw.print("     ObjectType: " + objectType + " (");
		
		switch (objectType) {
			case CMQC.MQOT_CHANNEL: 
				xw.println("MQOT_CHANNEL)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
				retString = "MQOT_CHANNEL ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
				break;
			// New in 7.1
			case CMQC.MQOT_CHLAUTH: 
				xw.println("MQOT_CHLAUTH)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
				retString = "MQOT_CHLAUTH ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
				break;
			case CMQC.MQOT_NAMELIST: 
				xw.println("MQOT_NAMELIST)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_NAMELIST_NAME));
				retString = "MQOT_NAMELIST ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_NAMELIST_NAME);
				break;
			// New in 7.1
			case CMQC.MQOT_NONE: 
				xw.println("MQOT_NONE)");
				xw.println("     ObjectName: " + "");
				retString = "MQOT_NONE";
				break;
			case CMQC.MQOT_PROCESS: 
				xw.println("MQOT_PROCESS)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_PROCESS_NAME));
				retString = "MQOT_PROCESS ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_PROCESS_NAME);
				break;
			case CMQC.MQOT_Q: 
				xw.println("MQOT_Q)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
				retString = "MQOT_Q ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME);
				break;
			case CMQC.MQOT_STORAGE_CLASS: 
				xw.println("MQOT_STORAGE_CLASS)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_STORAGE_CLASS));
				retString = "MQOT_STORAGE_CLASS ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_STORAGE_CLASS);
				break;
			case CMQC.MQOT_AUTH_INFO: 
				xw.println("MQOT_AUTH_INFO)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_AUTH_INFO_NAME));
				retString = "MQOT_AUTH_INFO ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_AUTH_INFO_NAME);
				break;
			case CMQC.MQOT_CF_STRUC: 
				xw.println("MQOT_CF_STRUC)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_CF_STRUC_NAME));
				retString = "MQOT_CF_STRUC ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_CF_STRUC_NAME);
				break;
			// New in 7.1
			case CMQC.MQOT_TOPIC: 
				xw.println("MQOT_TOPIC)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_TOPIC_NAME));
				retString = "MQOT_TOPIC ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_TOPIC_NAME);
				break;
			case CMQC.MQOT_COMM_INFO: 
				xw.println("MQOT_COMM_INFO)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_COMM_INFO_NAME));
				retString = "MQOT_COMM_INFO ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_COMM_INFO_NAME);
				break;
			case CMQC.MQOT_LISTENER: 
				xw.println("MQOT_LISTENER)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_LISTENER_NAME));
				retString = "MQOT_LISTENER ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_LISTENER_NAME);
				break;
			// Support for WebSphere MQ AMS 7.0.1.2
			case CMQC.MQOT_PROT_POLICY: 
				xw.println("MQOT_PROT_POLICY)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_POLICY_NAME));
				retString = "MQOT_PROT_POLICY ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_POLICY_NAME);
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
		
		if (platform == CMQC.MQPL_ZOS && objectType != CMQC.MQOT_Q_MGR && objectType != CMQC.MQOT_CF_STRUC) {
			int disposition  = pcfMsg.getIntParameterValue(CMQC.MQIA_QSG_DISP);
			
			xw.print("    Disposition: " + objectType + " (");
		
			switch (disposition) {
				case CMQC.MQQSGD_Q_MGR: 
					xw.println("MQQSGD_Q_MGR)");
					break;
				case CMQC.MQQSGD_SHARED: 
					xw.println("MQQSGD_SHARED)");
					break;
				case CMQC.MQQSGD_GROUP: 
					xw.println("MQQSGD_GROUP)");
					break;
				case CMQC.MQQSGD_COPY: 
					xw.println("MQQSGD_COPY)");
					break;
				default:
					xw.println("Unknown!)");
			} // end switch
		} // end if
		
		if (this.displayDetails) {
			//int messageCount = 0;
		
			//xw.println("Message " + ++messageCount + ": " + pcfMsg + "\n");
			xw.println();
			xw.println("**************************** PCF Message Dump Start ***************************");
			xw.println(pcfMsg);
			xw.println("**************************** PCF Message Dump End   ***************************");
			//xw.println();
		}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_Q_MGR)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " - " + retString;
	} // end of method displayCreate_Object()
	
	private String displayDefault_Transmission_Queue_Type_Error(PCFMessage pcfMsg)
		throws PCFException {
		
		int qType = pcfMsg.getIntParameterValue(CMQC.MQIA_Q_TYPE);
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Default Transmission Queue Type Error - MQRC_DEF_XMIT_Q_TYPE_ERROR (2198, X'896')");
		xw.println("      EventType: Remote");
		xw.println("    Description: An MQOPEN or MQPUT1 call was issued specifying a remote queue as");
		xw.println("                 the destination. Either a local definition of the remote queue");
		xw.println("                 was specified or a queue-manager alias was being resolved, but");
		xw.println("                 in either case the XmitQName attribute in the local definition");
		xw.println("                 is blank.");
		xw.println("                 No transmission queue is defined with the same name as the");
		xw.println("                 destination queue manager, so the local queue manager has");
		xw.println("                 attempted to use the default transmission queue. However,");
		xw.println("                 although there is a queue defined by the DefXmitQName");
		xw.println("                 queue-manager attribute, it is not a local queue.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
		xw.println("      XmitQName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_XMIT_Q_NAME));
		xw.print("          QType: " + qType + " (");
		
		switch (qType) {
			case CMQC.MQQT_ALIAS: 
				xw.println("MQQT_ALIAS)");
				break;
			case CMQC.MQQT_REMOTE:
				xw.println("MQQT_REMOTE)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
		
		xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
				                              applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
		xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));	
		try {
			xw.println(" ObjectQMgrName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_OBJECT_Q_MGR_NAME));
		} catch (MQException mqe) {;}
		
		// New field for 7.1
		try {
			xw.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		} catch (MQException mqe) {;}
				
		// New field for 7.1
		try {
			xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		} catch (MQException mqe) {;}	
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME);
	} // end of method displayDefault_Transmission_Queue_Type_Error()
	
	private String displayDefault_Transmission_Queue_Usage_Error(PCFMessage pcfMsg)
		throws PCFException {
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Default Transmission Queue Usage Error - MQRC_DEF_XMIT_Q_USAGE_ERROR (2199, X'897')");
		xw.println("      EventType: Remote");
		xw.println("    Description: An MQOPEN or MQPUT1 call was issued specifying a remote queue as");
		xw.println("                 the destination. Either a local definition of the remote queue was");
		xw.println("                 specified or a queue-manager alias was being resolved, but in either");
		xw.println("                 case the XmitQName attribute in the local definition is blank.");
		xw.println("                 No transmission queue is defined with the same name as the");
		xw.println("                 destination queue manager, so the local queue manager has");
		xw.println("                 attempted to use the default transmission queue. However,");
		xw.println("                 the queue defined by the DefXmitQName queue-manager attribute");
		xw.println("                 does not have a Usage attribute of MQUS_TRANSMISSION.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
		xw.println("      XmitQName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_XMIT_Q_NAME));
		xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
				                              applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
		xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));	
		try {
			xw.println(" ObjectQMgrName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_OBJECT_Q_MGR_NAME));
		} catch (MQException mqe) {;}
		
		// New field for 7.1
		try {
			xw.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		} catch (MQException mqe) {;}
						
		// New field for 7.1
		try {
			xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		} catch (MQException mqe) {;}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME);
	} // end of method displayDefault_Transmission_Queue_Usage_Error()
	
	private String displayDelete_Object(PCFMessage pcfMsg, int platform)
		throws PCFException {
		
		String retString = "";
		
		int eventOrigin = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_EVENT_ORIGIN);
		int objectType = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_OBJECT_TYPE);
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Delete Object - MQRC_CONFIG_DELETE_OBJECT (2369, X'941')");
		xw.println("      EventType: Configuration");
		xw.println("    Description: A DELETE command or MQCLOSE call was issued that");
		xw.println("                 successfully deleted an object.");
		xw.println("    EventUserId: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_USER_ID));
		
		try {
			xw.println("     SecurityId: X'" + toHex(new String(pcfMsg.getBytesParameterValue(CMQCFC.MQBACF_EVENT_SECURITY_ID))) + "'");
		} catch (MQException mqe) {;}
		
		xw.print("    EventOrigin: " + eventOrigin + " (");
		
		switch (eventOrigin) {
			case CMQCFC.MQEVO_CONSOLE: 
				xw.println("MQEVO_CONSOLE)");
				break;
			case CMQCFC.MQEVO_INIT: 
				xw.println("MQEVO_INIT)");
				break;
			case CMQCFC.MQEVO_INTERNAL: 
				xw.println("MQEVO_INTERNAL)");
				break;
			case CMQCFC.MQEVO_MSG: 
				xw.println("MQEVO_MSG)");
				break;
			case CMQCFC.MQEVO_OTHER: 
				xw.println("MQEVO_OTHER)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
		
		xw.println("      EventQMgr: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_Q_MGR));
		
		if (eventOrigin == CMQCFC.MQEVO_MSG) {
			xw.println("EventAccntToken: X'" + toHex(new String(pcfMsg.getBytesParameterValue(CMQCFC.MQBACF_EVENT_ACCOUNTING_TOKEN))) +"'");
			xw.println("EventApplIdenty: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_APPL_IDENTITY));
			xw.println("  EventApplType: " + pcfMsg.getIntParameterValue(CMQCFC.MQIACF_EVENT_APPL_TYPE) + " (" +
					                              applTypeToString(pcfMsg.getIntParameterValue(CMQCFC.MQIACF_EVENT_APPL_TYPE)) + ")");
			xw.println("  EventApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_APPL_NAME));
			xw.println("EventApplOrigin: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_APPL_ORIGIN));
		}
		
		xw.print("     ObjectType: " + objectType + " (");
		
		switch (objectType) {
			case CMQC.MQOT_CHANNEL: 
				xw.println("MQOT_CHANNEL)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
				retString = "MQOT_CHANNEL ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
				break;
			case CMQC.MQOT_NAMELIST: 
				xw.println("MQOT_NAMELIST)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_NAMELIST_NAME));
				retString = "MQOT_NAMELIST ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_NAMELIST_NAME);
				break;
			// New in 7.1
			case CMQC.MQOT_NONE: 
				xw.println("MQOT_NONE)");
				xw.println("     ObjectName: " + "");
				retString = "MQOT_NONE";
				break;
			case CMQC.MQOT_PROCESS: 
				xw.println("MQOT_PROCESS)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_PROCESS_NAME));
				retString = "MQOT_PROCESS ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_PROCESS_NAME);
				break;
			case CMQC.MQOT_Q: 
				xw.println("MQOT_Q)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
				retString = "MQOT_Q ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME);
				break;
			case CMQC.MQOT_STORAGE_CLASS: 
				xw.println("MQOT_STORAGE_CLASS)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_STORAGE_CLASS));
				retString = "MQOT_STORAGE_CLASS ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_STORAGE_CLASS);
				break;
			case CMQC.MQOT_AUTH_INFO: 
				xw.println("MQOT_AUTH_INFO)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_AUTH_INFO_NAME));
				retString = "MQOT_AUTH_INFO ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_AUTH_INFO_NAME);
				break;
			case CMQC.MQOT_CF_STRUC: 
				xw.println("MQOT_CF_STRUC)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_CF_STRUC_NAME));
				retString = "MQOT_CF_STRUC ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_CF_STRUC_NAME);
				break;
			// New in 7.1
			case CMQC.MQOT_TOPIC: 
				xw.println("MQOT_TOPIC)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_TOPIC_NAME));
				retString = "MQOT_TOPIC ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_TOPIC_NAME);
				break;
			case CMQC.MQOT_COMM_INFO: 
				xw.println("MQOT_COMM_INFO)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_COMM_INFO_NAME));
				retString = "MQOT_COMM_INFO ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_COMM_INFO_NAME);
				break;
			case CMQC.MQOT_LISTENER: 
				xw.println("MQOT_LISTENER)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_LISTENER_NAME));
				retString = "MQOT_LISTENER ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_LISTENER_NAME);
				break;
			// Support for WebSphere MQ AMS 7.0.1.2
			case CMQC.MQOT_PROT_POLICY: 
				xw.println("MQOT_PROT_POLICY)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_POLICY_NAME));
				retString = "MQOT_PROT_POLICY ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_POLICY_NAME);
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
		
		if (platform == CMQC.MQPL_ZOS && objectType != CMQC.MQOT_Q_MGR && objectType != CMQC.MQOT_CF_STRUC) {
			int disposition  = pcfMsg.getIntParameterValue(CMQC.MQIA_QSG_DISP);
			
			xw.print("    Disposition: " + objectType + " (");
		
			switch (disposition) {
				case CMQC.MQQSGD_Q_MGR: 
					xw.println("MQQSGD_Q_MGR)");
					break;
				case CMQC.MQQSGD_SHARED: 
					xw.println("MQQSGD_SHARED)");
					break;
				case CMQC.MQQSGD_GROUP: 
					xw.println("MQQSGD_GROUP)");
					break;
				case CMQC.MQQSGD_COPY: 
					xw.println("MQQSGD_COPY)");
					break;
				default:
					xw.println("Unknown!)");
			} // end switch
		} // end if
		
		if (this.displayDetails) {
			//int messageCount = 0;
		
			//xw.println("Message " + ++messageCount + ": " + pcfMsg + "\n");
			xw.println();
			xw.println("**************************** PCF Message Dump Start ***************************");
			xw.println(pcfMsg);
			xw.println("**************************** PCF Message Dump End   ***************************");
			//xw.println();
		}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_Q_MGR)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " - " + retString;
	} // end of method displayDelete_Object()
	
	private String displayGet_Inhibited(PCFMessage pcfMsg)
		throws PCFException {
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Get Inhibited - MQRC_GET_INHIBITED (2016, X'7E0')");
		xw.println("      EventType: Inhibit");
		xw.println("    Description: MQGET calls are currently inhibited for the queue");
		xw.println("                 or for the queue to which this queue resolves.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
		xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
				                              applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
		xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));	
		
		// New field for 7.1
		try {
			xw.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		} catch (MQException mqe) {;}
						
		// New field for 7.1
		try {
			xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		} catch (MQException mqe) {;}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME);
	} // end of method displayGet_Inhibited()
	
	private String displayLogger_Status(PCFMessage pcfMsg)
		throws PCFException {
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Logger Status - MQRC_LOGGER_STATUS (2411, X'96B')");
		xw.println("      EventType: Logger");
		xw.println("    Description: Issued when a queue manager starts writing to a new");
		xw.println("                 log extent or on i5/OS a new journal receiver.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("  CurrLogExtent: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_CURRENT_LOG_EXTENT_NAME));
		xw.println("ResRecLogExtent: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_RESTART_LOG_EXTENT_NAME));
		xw.println("MedRecLogExtent: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_MEDIA_LOG_EXTENT_NAME));
		xw.println("        LogPath: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_LOG_PATH));
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason());
	} // end of method displayLogger_Status()
	
	private String displayNot_Authorized(PCFMessage pcfMsg)
		throws PCFException {
		
		StringBuffer retString = new StringBuffer();
		
		String[] adminTopicNames;
		
		int command, openOptions, option;
		int reasonQualifier = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_REASON_QUALIFIER);
		
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		
		switch (reasonQualifier) {
			case CMQCFC.MQRQ_CONN_NOT_AUTHORIZED:
				xw.println("      EventName: Not Authorized Type 1 - MQRC_NOT_AUTHORIZED (2035, X'7F3')");
				xw.println("      EventType: Authority");
				xw.println("    Description: On an MQCONN or system connection call, the user is not");
				xw.println("                 authorized to connect to the queue manager. ReasonQualifier");
				xw.println("                 identifies the nature of the error.");
				xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
				xw.print("ReasonQualifier: " + reasonQualifier);
				
				switch (reasonQualifier) {
					case CMQCFC.MQRQ_CONN_NOT_AUTHORIZED:
						xw.println(" (MQRQ_CONN_NOT_AUTHORIZED)");
						break;
					case CMQCFC.MQRQ_SYS_CONN_NOT_AUTHORIZED:
						xw.println(" (MQRQ_SYS_CONN_NOT_AUTHORIZED)");
						break;
					default:
						xw.println("Unknown!)");
				} // end switch
				
				xw.println(" UserIdentifier: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_USER_IDENTIFIER));
				xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
						                              applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
				xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));
				
				// New field for 7.1
				try {
					xw.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
				} catch (MQException mqe) {;}
										
				// New field for 7.1
				try {
					xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
				} catch (MQException mqe) {;}
				retString.append(rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " Type 1 (Conn) ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_USER_IDENTIFIER));
				break;
				
			case CMQCFC.MQRQ_OPEN_NOT_AUTHORIZED:
				xw.println("      EventName: Not Authorized Type 2 - MQRC_NOT_AUTHORIZED (2035, X'7F3')");
				xw.println("      EventType: Authority");
				xw.println("    Description: On an MQOPEN or MQPUT1 call, the user is not authorized to"); 
				xw.println("                 open the object for the options specified.");
				xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
				xw.println("ReasonQualifier: " + reasonQualifier + " (MQRQ_OPEN_NOT_AUTHORIZED)");
				
				retString.append(rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " Type 2 (Open) ==> ");
				
				openOptions = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_OPEN_OPTIONS);
				xw.println("    OpenOptions: " + openOptions);
				
				option = 1048576;
				while (openOptions > 0) {
					if (openOptions - option >= 0) {
						openOptions = openOptions - option;
						
					xw.print("                 ");
						
						switch (option) {
							case CMQC.MQOO_INPUT_AS_Q_DEF:
								xw.println("MQOO_INPUT_AS_Q_DEF");
								break;
							case CMQC.MQOO_INPUT_SHARED:
								xw.println("MQOO_INPUT_SHARED");
								break;
							case CMQC.MQOO_INPUT_EXCLUSIVE:
								xw.println("MQOO_INPUT_EXCLUSIVE");
								break;
							case CMQC.MQOO_BROWSE:
								xw.println("MQOO_BROWSE");
								break;
							case CMQC.MQOO_OUTPUT:
								xw.println("MQOO_OUTPUT");
								break;
							case CMQC.MQOO_INQUIRE:
								xw.println("MQOO_INQUIRE");
								break;
							case CMQC.MQOO_SET:
								xw.println("MQOO_SET");
								break;
							case CMQC.MQOO_SAVE_ALL_CONTEXT:
								xw.println("MQOO_SAVE_ALL_CONTEXT");
								break;
							case CMQC.MQOO_PASS_IDENTITY_CONTEXT:
								xw.println("MQOO_PASS_IDENTITY_CONTEXT");
								break;
							case CMQC.MQOO_PASS_ALL_CONTEXT:
								xw.println("MQOO_PASS_ALL_CONTEXT");
								break;
							case CMQC.MQOO_SET_IDENTITY_CONTEXT:
								xw.println("MQOO_SET_IDENTITY_CONTEXT");
								break;
							case CMQC.MQOO_SET_ALL_CONTEXT:
								xw.println("MQOO_SET_ALL_CONTEXT");
								break;
							case CMQC.MQOO_ALTERNATE_USER_AUTHORITY:
								xw.println("MQOO_ALTERNATE_USER_AUTHORITY");
								break;
							case CMQC.MQOO_FAIL_IF_QUIESCING:
								xw.println("MQOO_FAIL_IF_QUIESCING");
								break;
							case CMQC.MQOO_BIND_ON_OPEN:
								xw.println("MQOO_BIND_ON_OPEN");
								break;
							case CMQC.MQOO_BIND_NOT_FIXED:
								xw.println("MQOO_BIND_NOT_FIXED");
								break;
							case CMQC.MQOO_CO_OP:
								xw.println("MQOO_CO_OP");
								break;
							case CMQC.MQOO_NO_READ_AHEAD:
								xw.println("MQOO_NO_READ_AHEAD");
								break;
							case CMQC.MQOO_READ_AHEAD:
								xw.println("MQOO_READ_AHEAD");
								break;
							case CMQC.MQOO_RESOLVE_NAMES :
								xw.println("MQOO_RESOLVE_NAMES ");
								break;
							case CMQC.MQOO_RESOLVE_LOCAL_Q:
								xw.println("MQOO_RESOLVE_LOCAL_Q");
								break;			
						} // end witch
					} // end if
					
					option = option / 2;
				} // end while
				
				
				xw.println(" UserIdentifier: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_USER_IDENTIFIER));
				xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
						                              applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
				xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));
				try {
					xw.println(" ObjectQMgrName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_OBJECT_Q_MGR_NAME));
				} catch (MQException mqe) {;}
				
				try {
					int objectType = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_OBJECT_TYPE);
					xw.print("         ObjectType: " + objectType + " (");
					
					switch (objectType) {
						case CMQC.MQOT_NAMELIST:
							xw.println("MQOT_NAMELIST)");
							retString.append("Namelist: ");
							break;
						case CMQC.MQOT_PROCESS:
							xw.println("MQOT_PROCESS)");
							retString.append("Process: ");
							break;
						case CMQC.MQOT_Q:
							xw.println("MQOT_Q)");
							retString.append("Queue: ");
							break;
						case CMQC.MQOT_Q_MGR:
							xw.println("MQOT_Q_MGR)");
							retString.append("QMgr: ");
							break;
						case CMQC.MQOT_TOPIC:
							xw.println("MQOT_TOPIC)");
							retString.append("Topic: ");
							break;
						default:
							xw.println("Unknow!)");
							retString.append("Unknown!");
					} // end switch
				} catch (MQException mqe) {;}
					
				try {
					xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
					retString.append(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
				} catch (MQException mqe) {;}
				try {
					xw.println("    ProcessName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_PROCESS_NAME));
					retString.append(pcfMsg.getStringParameterValue(CMQC.MQCA_PROCESS_NAME));
				} catch (MQException mqe) {;}
				try {
					xw.println("    TopicString: " + pcfMsg.getStringParameterValue(CMQC.MQCA_TOPIC_STRING));
					retString.append(pcfMsg.getStringParameterValue(CMQC.MQCA_TOPIC_STRING));
				} catch (MQException mqe) {;}
				
				try {
					adminTopicNames = pcfMsg.getStringListParameterValue(CMQCFC.MQCACF_ADMIN_TOPIC_NAMES);
					
					xw.print("AdminTopicNames: ");
					for (int i = 0; i < adminTopicNames.length; i++) {
						if (i > 0) xw.print("                 ");
						xw.println(adminTopicNames[i]);
						retString.append(adminTopicNames[i]);
					} // end for
				} catch (MQException mqe) {;}
				
				try {
					xw.println("   NamelistName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_NAMELIST_NAME));
					retString.append(pcfMsg.getStringParameterValue(CMQC.MQCA_NAMELIST_NAME));
				} catch (MQException mqe) {;}
				
				// New field for 7.1
				try {
					xw.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
				} catch (MQException mqe) {;}
										
				// New field for 7.1
				try {
					xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
				} catch (MQException mqe) {;}
				
				break;
				
			case CMQCFC.MQRQ_CLOSE_NOT_AUTHORIZED:
				xw.println("      EventName: Not Authorized Type 3 - MQRC_NOT_AUTHORIZED (2035, X'7F3')");
				xw.println("      EventType: Authority");
				xw.println("    Description: On an MQCLOSE call, the user is not authorized to delete the"); 
				xw.println("                 object, which is a permanent dynamic queue, and the Hobj"); 
				xw.println("                 parameter specified on the MQCLOSE call is not the handle"); 
				xw.println("                 returned by the MQOPEN call that created the queue.");
				xw.println("                 On an MQCLOSE call, the user has requested that the");
				xw.println("                 subscription is removed using the MQCO_REMOVE_SUB option,");
				xw.println("                 but the user is not the creator of the subscription or does");
				xw.println("                 not have sub authority on the topic associated with the");
				xw.println("                 subscription.");
				xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
				xw.println("ReasonQualifier: " + reasonQualifier + " (MQRQ_CLOSE_NOT_AUTHORIZED)");
				
				retString.append(rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " Type 3 (Close) ==> ");
				
				try {
					xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
					retString.append(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
				} catch (MQException mqe) {;}
				try {
					xw.println("        SubName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_SUB_NAME));
					retString.append(pcfMsg.getStringParameterValue(CMQCFC.MQCACF_SUB_NAME));
				} catch (MQException mqe) {;}
				try {
					xw.println("    TopicString: " + pcfMsg.getStringParameterValue(CMQC.MQCA_TOPIC_STRING));
					retString.append(pcfMsg.getStringParameterValue(CMQC.MQCA_TOPIC_STRING));
				} catch (MQException mqe) {;}
				try {
					xw.print("AdminTopicNames: ");
					
					adminTopicNames = pcfMsg.getStringListParameterValue(CMQCFC.MQCACF_ADMIN_TOPIC_NAMES);
					for (int i = 0; i < adminTopicNames.length; i++) {
						if (i > 0) xw.print("                 ");
						xw.println(adminTopicNames[i]);
						retString.append(adminTopicNames[i]);
					} // end for
				} catch (MQException mqe) {;}
				
				xw.println(" UserIdentifier: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_USER_IDENTIFIER));
				xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
						                              applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
				xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));
				
				// New field for 7.1
				try {
					xw.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
				} catch (MQException mqe) {;}
										
				// New field for 7.1
				try {
					xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
				} catch (MQException mqe) {;}
				
				break;
				
			case CMQCFC.MQRQ_CMD_NOT_AUTHORIZED:
				xw.println("      EventName: Not Authorized Type 4 - MQRC_NOT_AUTHORIZED (2035, X'7F3')");
				xw.println("      EventType: Authority");
				xw.println("    Description: Indicates that a command has been issued from a user ID that");  
				xw.println("                 is not authorized to access the object specified in the");
				xw.println("                 command.");
				xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
				xw.println("ReasonQualifier: " + reasonQualifier + " (MQRQ_CMD_NOT_AUTHORIZED)");
				
				command = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_COMMAND);
				xw.println("        Command: " + command + " (" + commandToString(command, false) + ")");
				xw.println(" UserIdentifier: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_USER_IDENTIFIER));
				
				retString.append(rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " Type 4 (Command) ==> " + commandToString(command, false));
				
				break;
			case CMQCFC.MQRQ_SUB_NOT_AUTHORIZED:
				xw.println("      EventName: Not Authorized Type 5 - MQRC_NOT_AUTHORIZED (2035, X'7F3')");
				xw.println("      EventType: Authority");
				xw.println("    Description: On an MQSUB call, the user is not authorized to subscribe to");  
				xw.println("                 the specified topic."); 
				xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
				xw.println("ReasonQualifier: " + reasonQualifier + " (MQRQ_SUB_NOT_AUTHORIZED)");
				xw.println("        Options: " + pcfMsg.getIntParameterValue(CMQCFC.MQIACF_SUB_OPTIONS));
				xw.println(" UserIdentifier: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_USER_IDENTIFIER));
				xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
						                              applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
				xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));
				xw.println("    TopicString: " + pcfMsg.getStringParameterValue(CMQC.MQCA_TOPIC_STRING));
				xw.print("AdminTopicNames: ");
				
				adminTopicNames = pcfMsg.getStringListParameterValue(CMQCFC.MQCACF_ADMIN_TOPIC_NAMES);
				for (int i = 0; i < adminTopicNames.length; i++) {
					if (i > 0) xw.print("                 ");
					xw.println(adminTopicNames[i]);
				} // end for
				
				// New field for 7.1
				try {
					xw.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
				} catch (MQException mqe) {;}
										
				// New field for 7.1
				try {
					xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
				} catch (MQException mqe) {;}
				
				retString.append(rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " Type 5 (Sub) ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_TOPIC_STRING));
				
				break;
				
			case CMQCFC.MQRQ_SUB_DEST_NOT_AUTHORIZED:
				xw.println("      EventName: Not Authorized Type 6 - MQRC_NOT_AUTHORIZED (2035, X'7F3')");
				xw.println("      EventType: Authority");
				xw.println("    Description: On an MQSUB call, the user is not authorized to use the");
				xw.println("                 destination queue with the required level of access. This");  
				xw.println("                 event is only returned for subscriptions using non-managed");
				xw.println("                 destination queues.");
				xw.println("                 When creating, altering, or resuming a subscription, and a");
				xw.println("                 handle to the destination queue is supplied on the request,");
				xw.println("                 the user does not have PUT authority on the destination queue");
				xw.println("                 provided.");
				xw.println("                 When resuming or alerting a subscription and the handle to the");
				xw.println("                 destination queue is to be returned on the MQSUB call, and the");
				xw.println("                 user does not have PUT, GET and BROWSE authority on the");
				xw.println("                 destination queue.");
				xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
				xw.println("ReasonQualifier: " + reasonQualifier + " (MQRO_SUB_DEST_NOT_AUTHORIZED)");
				xw.println("        Options: " + pcfMsg.getIntParameterValue(CMQCFC.MQIACF_SUB_OPTIONS));
				xw.println(" UserIdentifier: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_USER_IDENTIFIER));
				xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
						                              applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
				xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));
				xw.println("    TopicString: " + pcfMsg.getStringParameterValue(CMQC.MQCA_TOPIC_STRING));
				
				try {
					xw.println("   DestQMgrName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_OBJECT_Q_MGR_NAME));
				} catch (MQException mqe) {;}
				
				xw.println("      DestQName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
				xw.println("DestOpenOptions: " + pcfMsg.getIntParameterValue(CMQCFC.MQIACF_OPEN_OPTIONS));
				
				// New field for 7.1
				try {
					xw.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
				} catch (MQException mqe) {;}
										
				// New field for 7.1
				try {
					xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
				} catch (MQException mqe) {;}
				
				 retString.append(rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " Type 6 (Sub) ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
				
				break;
			case CMQCFC.MQRQ_SYS_CONN_NOT_AUTHORIZED:
				xw.println("      EventName: Not Authorized Type X - MQRC_NOT_AUTHORIZED (2035, X'7F3')");
				xw.println("      EventType: Authority");
				xw.println("    Description: System connection not authorized (undocumented)!");  
				xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
				xw.println("ReasonQualifier: " + reasonQualifier + " (MQRQ_SYS_CONN_NOT_AUTHORIZED)");
				xw.println(" UserIdentifier: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_USER_IDENTIFIER));
				xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
						                              applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
				xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));
				
				// New field for 7.1
				try {
					xw.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
				} catch (MQException mqe) {;}
										
				// New field for 7.1
				try {
					xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
				} catch (MQException mqe) {;}
				
				retString.append(rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " Type X (Sys) ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_USER_IDENTIFIER));
				
				break;
				
			case CMQCFC.MQRQ_CSP_NOT_AUTHORIZED:
				xw.println("      EventName: Not Authorized Type X - MQRC_NOT_AUTHORIZED (2035, X'7F3')");
				xw.println("      EventType: Authority");
				xw.println("    Description: Connection (user-id/password) not authorized (undocumented)!");  
				xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
				xw.println("ReasonQualifier: " + reasonQualifier + " (MQRQ_CSP_NOT_AUTHORIZED)");
				xw.println(" UserIdentifier: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_USER_IDENTIFIER));
				xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
						                              applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
				xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));
				
				// New field for 7.1
				try {
					xw.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
				} catch (MQException mqe) {;}
										
				// New field for 7.1
				try {
					xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
				} catch (MQException mqe) {;}
				
				retString.append(rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " Type X (Sys) ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_USER_IDENTIFIER));
				
				break;
				
			default:
				xw.println("Unknown reason qualifier (" + reasonQualifier + ")");
			    retString.append("Unknown reason qualifier (" + reasonQualifier + ")");
		} // end switch
		
		return retString.toString();
	} // end of method displayNot_Authorized()
	
	private String displayPut_Inhibited(PCFMessage pcfMsg)
		throws PCFException {

		String retString = "";
		
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Put Inhibited - MQRC_PUT_INHIBITED (2051, X'803')");
		xw.println("      EventType: Inhibit");
		xw.println("    Description: MQPUT and MQPUT1 calls are currently inhibited for");
		xw.println("                 the queue or for the queue to which this queue resolves.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		
		try {
			xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
			retString = pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME);
		} catch (MQException mqe) {;}
		
		xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
				                              applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
		xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));
		
		try {
			xw.println(" ObjectQMgrName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_OBJECT_Q_MGR_NAME));
		} catch (MQException mqe) {;}
		
		try {
			xw.println("    TopicString: " + pcfMsg.getStringParameterValue(CMQC.MQCA_TOPIC_STRING));
			retString = pcfMsg.getStringParameterValue(CMQC.MQCA_TOPIC_STRING);
		} catch (MQException mqe) {;}
		
		// New field for 7.1
		try {
			xw.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		} catch (MQException mqe) {;}
								
		// New field for 7.1
		try {
			xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		} catch (MQException mqe) {;}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + retString;
	} // end of method displayPut_Inhibited()
	
	private String displayQueue_Depth_High(PCFMessage pcfMsg)
		throws PCFException {
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Queue Depth High - MQRC_Q_DEPTH_HIGH (2224, X'8B0')");
		xw.println("      EventType: Performance");
		xw.println("    Description: An MQPUT or MQPUT1 call has caused the queue depth to");
		xw.println("                 be incremented to or above the limit specified in the");
		xw.println("                 QDepthHighLimit attribute.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_BASE_Q_NAME));
		xw.println(" TimeSinceReset: " + pcfMsg.getIntParameterValue(CMQC.MQIA_TIME_SINCE_RESET));
		xw.println("     HighQDepth: " + pcfMsg.getIntParameterValue(CMQC.MQIA_HIGH_Q_DEPTH));	
		xw.println("    MsgEnqCount: " + pcfMsg.getIntParameterValue(CMQC.MQIA_MSG_ENQ_COUNT));
		xw.println("    MsgDeqCount: " + pcfMsg.getIntParameterValue(CMQC.MQIA_MSG_DEQ_COUNT));
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_BASE_Q_NAME);
	} // end of method displayQueue_Depth_High()
	
	private String displayQueue_Depth_Low(PCFMessage pcfMsg)
		throws PCFException {
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Queue Depth Low - MQRC_Q_DEPTH_LOW (2225, X'8B1')");
		xw.println("      EventType: Performance");
		xw.println("    Description: A get operation has caused the queue depth to be");
		xw.println("                 decremented to or below the limit specified in the");
		xw.println("                 QDepthLowLimit attribute.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_BASE_Q_NAME));
		xw.println(" TimeSinceReset: " + pcfMsg.getIntParameterValue(CMQC.MQIA_TIME_SINCE_RESET));
		xw.println("     HighQDepth: " + pcfMsg.getIntParameterValue(CMQC.MQIA_HIGH_Q_DEPTH));	
		xw.println("    MsgEnqCount: " + pcfMsg.getIntParameterValue(CMQC.MQIA_MSG_ENQ_COUNT));
		xw.println("    MsgDeqCount: " + pcfMsg.getIntParameterValue(CMQC.MQIA_MSG_DEQ_COUNT));
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_BASE_Q_NAME);
	} // end of method displayQueue_Depth_Low()
	
	private String displayQueue_Full(PCFMessage pcfMsg)
		throws PCFException {
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Queue Full - MQRC_Q_FULL (2053, X'805')");
		xw.println("      EventType: Performance");
		xw.println("    Description: On an MQPUT or MQPUT1 call, the call failed because");
		xw.println("                 the queue is full. That is, it already contains the");
		xw.println("                 maximum number of messages possible (see the MaxQDepth");
		xw.println("                 local-queue attribute).");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_BASE_Q_NAME));
		xw.println(" TimeSinceReset: " + pcfMsg.getIntParameterValue(CMQC.MQIA_TIME_SINCE_RESET));
		xw.println("     HighQDepth: " + pcfMsg.getIntParameterValue(CMQC.MQIA_HIGH_Q_DEPTH));	
		xw.println("    MsgEnqCount: " + pcfMsg.getIntParameterValue(CMQC.MQIA_MSG_ENQ_COUNT));
		xw.println("    MsgDeqCount: " + pcfMsg.getIntParameterValue(CMQC.MQIA_MSG_DEQ_COUNT));
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_BASE_Q_NAME);
	} // end of method displayQueue_Service_Interval_High()
	
	private String displayQueue_Manager_Active(PCFMessage pcfMsg)
		throws PCFException {
		
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Queue Manager Active - MQRC_Q_MGR_ACTIVE (2222, X'8AE')");
		xw.println("      EventType: Start and Stop");
		xw.println("    Description: This condition is detected when a queue manager becomes");
		xw.println("                 active.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason());
	} // end of method displayQueue_Full()
	
	private String displayQueue_Manager_Not_Active(PCFMessage pcfMsg)
		throws PCFException {
		
		int reasonQualifier = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_REASON_QUALIFIER);
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Queue Manager Not Active - MQRC_Q_MGR_NOT_ACTIVE (2223, X'8AF')");
		xw.println("      EventType: Start and Stop");
		xw.println("    Description: This condition is detected when a queue manager is requested");
		xw.println("                 to stop or quiesce.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.print("ReasonQualifier: " + reasonQualifier + " (");
		
		switch (reasonQualifier) {
			case CMQCFC.MQRQ_Q_MGR_STOPPING: 
				xw.println("MQRQ_Q_MGR_STOPPING)");
				break;
			case CMQCFC.MQRQ_Q_MGR_QUIESCING:
				xw.println("MQRQ_Q_MGR_QUIESCING)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason());
	} // end of method displayQueue_Manager_Not_Active()
	
	private String displayQueue_Service_Interval_High(PCFMessage pcfMsg)
		throws PCFException {
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Queue Service Interval High - MQRC_Q_SERVICE_INTERVAL_HIGH (2226, X'8B2')");
		xw.println("      EventType: Performance");
		xw.println("    Description: No successful get operations or MQPUT calls have been detected");
		xw.println("                 within an interval greater than the limit specified in the");
		xw.println("                 QServiceInterval attribute.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_BASE_Q_NAME));
		xw.println(" TimeSinceReset: " + pcfMsg.getIntParameterValue(CMQC.MQIA_TIME_SINCE_RESET));
		xw.println("     HighQDepth: " + pcfMsg.getIntParameterValue(CMQC.MQIA_HIGH_Q_DEPTH));	
		xw.println("    MsgEnqCount: " + pcfMsg.getIntParameterValue(CMQC.MQIA_MSG_ENQ_COUNT));
		xw.println("    MsgDeqCount: " + pcfMsg.getIntParameterValue(CMQC.MQIA_MSG_DEQ_COUNT));
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_BASE_Q_NAME);
	} // end of method displayQueue_Service_Interval_High()
	
	private String displayQueue_Service_Interval_OK(PCFMessage pcfMsg)
		throws PCFException {
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Queue Service Interval OK - MQRC_Q_SERVICE_INTERVAL_OK (2227, X'8B3')");
		xw.println("      EventType: Performance");
		xw.println("    Description: A successful get operation has been detected within an");
		xw.println("                 interval less than or equal to the limit specified in the");
		xw.println("                 QServiceInterval attribute.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_BASE_Q_NAME));
		xw.println(" TimeSinceReset: " + pcfMsg.getIntParameterValue(CMQC.MQIA_TIME_SINCE_RESET));
		xw.println("     HighQDepth: " + pcfMsg.getIntParameterValue(CMQC.MQIA_HIGH_Q_DEPTH));	
		xw.println("    MsgEnqCount: " + pcfMsg.getIntParameterValue(CMQC.MQIA_MSG_ENQ_COUNT));
		xw.println("    MsgDeqCount: " + pcfMsg.getIntParameterValue(CMQC.MQIA_MSG_DEQ_COUNT));
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_BASE_Q_NAME);
	} // end of method displayQueue_Service_Interval_OK()
	
	private String displayQueue_Type_Error(PCFMessage pcfMsg)
		throws PCFException {
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Queue Type Error - MQRC_Q_TYPE_ERROR (2057, X'809')");
		xw.println("      EventType: Remote");
		xw.println("    Description: On an MQOPEN call, the ObjectQMgrName field in the object");
		xw.println("                 descriptor specifies the name of a local definition of a");
		xw.println("                 remote queue (in order to specify a queue-manager alias).");
		xw.println("                 In that local definition the RemoteQMgrName attribute is");
		xw.println("                 the name of the local queue manager. However, the ObjectName");
		xw.println("                 field specifies the name of a model queue on the local queue");
		xw.println("                 manager, which is not allowed.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
		xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
				                              applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
		xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));	
		xw.println(" ObjectQMgrName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_OBJECT_Q_MGR_NAME));
		
		// New field for 7.1
		try {
			xw.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		} catch (MQException mqe) {;}
										
		// New field for 7.1
		try {
			xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		} catch (MQException mqe) {;}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_OBJECT_Q_MGR_NAME);
	} // end of method displayQueue_Type_Error()
		
	private String displayRefresh_Object(PCFMessage pcfMsg, int platform)
		throws PCFException {
		
		String retString = "";
		
		int eventOrigin = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_EVENT_ORIGIN);
		int objectType = pcfMsg.getIntParameterValue(CMQCFC.MQIACF_OBJECT_TYPE);
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Refresh Object - MQRC_CONFIG_REFRESH_OBJECT (2370, X'942')");
		xw.println("      EventType: Configuration");
		xw.println("    Description: A REFRESH QMGR command specifying TYPE (CONFIGEV) was issued.");
		xw.println("    EventUserId: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_USER_ID));
		
		try {
			xw.println("     SecurityId: X'" + toHex(new String(pcfMsg.getBytesParameterValue(CMQCFC.MQBACF_EVENT_SECURITY_ID))) +"'");
		} catch (MQException mqe) {;}
		
		xw.print("    EventOrigin: " + eventOrigin + " (");
		
		switch (eventOrigin) {
			case CMQCFC.MQEVO_CONSOLE: 
				xw.println("MQEVO_CONSOLE)");
				break;
			case CMQCFC.MQEVO_INIT: 
				xw.println("MQEVO_INIT)");
				break;
			case CMQCFC.MQEVO_INTERNAL: 
				xw.println("MQEVO_INTERNAL)");
				break;
			case CMQCFC.MQEVO_MSG: 
				xw.println("MQEVO_MSG)");
				break;
			case CMQCFC.MQEVO_OTHER: 
				xw.println("MQEVO_OTHER)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
		
		xw.println("      EventQMgr: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_Q_MGR));
		
		if (eventOrigin == CMQCFC.MQEVO_MSG) {
			xw.println("EventAccntToken: X'" + toHex(new String(pcfMsg.getBytesParameterValue(CMQCFC.MQBACF_EVENT_ACCOUNTING_TOKEN))) + "'");
			xw.println("EventApplIdenty: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_APPL_IDENTITY));
			xw.println("  EventApplType: " + pcfMsg.getIntParameterValue(CMQCFC.MQIACF_EVENT_APPL_TYPE) + " (" +
					                              applTypeToString(pcfMsg.getIntParameterValue(CMQCFC.MQIACF_EVENT_APPL_TYPE)) + ")");
			xw.println("  EventApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_APPL_NAME));
			xw.println("EventApplOrigin: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_APPL_ORIGIN));
		}
		
		xw.print("     ObjectType: " + objectType + " (");
		
		switch (objectType) {
			case CMQC.MQOT_CHANNEL: 
				xw.println("MQOT_CHANNEL)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
				retString = "MQOT_CHANNEL ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
				break;
			// New in 7.1
			case CMQC.MQOT_CHLAUTH: 
				xw.println("MQOT_CHLAUTH)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
				retString = "MQOT_CHLAUTH ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME);
				break;
			case CMQC.MQOT_LISTENER: 
				xw.println("MQOT_LISTENER)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_LISTENER_NAME));
				retString = "MQOT_LISTENER ==> " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_LISTENER_NAME);
				break;
			case CMQC.MQOT_NAMELIST: 
				xw.println("MQOT_NAMELIST)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_NAMELIST_NAME));
				retString = "MQOT_NAMELIST ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_NAMELIST_NAME);
				break;
			// New in 7.1
			case CMQC.MQOT_NONE: 
				xw.println("MQOT_NONE)");
				xw.println("     ObjectName: " + "");
				retString = "MQOT_NONE";
				break;
			case CMQC.MQOT_PROCESS: 
				xw.println("MQOT_PROCESS)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_PROCESS_NAME));
				retString = "MQOT_PROCESS ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_PROCESS_NAME);
				break;
			case CMQC.MQOT_Q: 
				xw.println("MQOT_Q)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
				retString = "MQOT_Q ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME);
				break;
			case CMQC.MQOT_Q_MGR: 
				xw.println("MQOT_Q_MGR)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
				retString = "MQOT_Q_MGR ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME);
				break;
			case CMQC.MQOT_STORAGE_CLASS: 
				xw.println("MQOT_STORAGE_CLASS)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_STORAGE_CLASS));
				retString = "MQOT_STORAGE_CLASS ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_STORAGE_CLASS);
				break;
			case CMQC.MQOT_AUTH_INFO: 
				xw.println("MQOT_AUTH_INFO)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_AUTH_INFO_NAME));
				retString = "MQOT_AUTH_INFO ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_AUTH_INFO_NAME);
				break;
			case CMQC.MQOT_CF_STRUC: 
				xw.println("MQOT_CF_STRUC)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_CF_STRUC_NAME));
				retString = "MQOT_CF_STRUC ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_CF_STRUC_NAME);
				break;
			// New in 7.1
			case CMQC.MQOT_TOPIC: 
				xw.println("MQOT_TOPIC)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_TOPIC_NAME));
				retString = "MQOT_TOPIC ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_TOPIC_NAME);
				break;
			case CMQC.MQOT_COMM_INFO: 
				xw.println("MQOT_COMM_INFO)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_COMM_INFO_NAME));
				retString = "MQOT_COMM_INFO ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_COMM_INFO_NAME);
				break;
			// Support for WebSphere MQ AMS 7.0.1.2
			case CMQC.MQOT_PROT_POLICY: 
				xw.println("MQOT_PROT_POLICY)");
				xw.println("     ObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_POLICY_NAME));
				retString = "MQOT_PROT_POLICY ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_POLICY_NAME);
				break;
			default:
				xw.println("Unknown!)");
				retString = "Unknown!";
		} // end switch
		
		if (platform == CMQC.MQPL_ZOS && objectType != CMQC.MQOT_Q_MGR && objectType != CMQC.MQOT_CF_STRUC) {
			int disposition  = pcfMsg.getIntParameterValue(CMQC.MQIA_QSG_DISP);
			
			xw.print("    Disposition: " + objectType + " (");
		
			switch (disposition) {
				case CMQC.MQQSGD_Q_MGR: 
					xw.println("MQQSGD_Q_MGR)");
					break;
				case CMQC.MQQSGD_SHARED: 
					xw.println("MQQSGD_SHARED)");
					break;
				case CMQC.MQQSGD_GROUP: 
					xw.println("MQQSGD_GROUP)");
					break;
				case CMQC.MQQSGD_COPY: 
					xw.println("MQQSGD_COPY)");
					break;
				default:
					xw.println("Unknown!)");
			} // end switch
		} // end if
		
		if (this.displayDetails) {
			//int messageCount = 0;
		
			//xw.println("Message " + ++messageCount + ": " + pcfMsg + "\n");
			xw.println();
			xw.println("**************************** PCF Message Dump Start ***************************");
			xw.println(pcfMsg);
			xw.println("**************************** PCF Message Dump End   ***************************");
			//xw.println();
		}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQCFC.MQCACF_EVENT_Q_MGR)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + retString;
	} // end of method displayRefresh_Object()
	
	private String displayRemote_Queue_Name_Error(PCFMessage pcfMsg)
		throws PCFException {
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Remote Queue Name Error - MQRC_REMOTE_Q_NAME_ERROR (2184, X'888')");
		xw.println("      EventType: Remote");
		xw.println("    Description: On an MQOPEN or MQPUT1 call one of the following occurs:");
		xw.println("                    - A local definition of a remote queue (or an alias to one) was");
		xw.println("                      specified, but the RemoteQName attribute in the remote queue");
		xw.println("                      definition is blank. Note that this error occurs even if the");
		xw.println("                      XmitQName in the definition is not blank.");
		xw.println("                    - The ObjectQMgrName field in the object descriptor is not blank");
		xw.println("                      and not the name of the local queue manager, but the ObjectName");
		xw.println("                      field is blank.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
		xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
				                              applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
		xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));	
		try {
			xw.println(" ObjectQMgrName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_OBJECT_Q_MGR_NAME));
		} catch (MQException mqe) {;}
		
		// New field for 7.1
		try {
			xw.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		} catch (MQException mqe) {;}
												
		// New field for 7.1
		try {
			xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		} catch (MQException mqe) {;}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME);
	} // end of method displayRemote_Queue_Name_Error()
	
	private String displayTransmission_Queue_Type_Error(PCFMessage pcfMsg)
		throws PCFException {
		
		int qType = pcfMsg.getIntParameterValue(CMQC.MQIA_Q_TYPE);
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Transmission Queue Type Error - MQRC_XMIT_Q_TYPE_ERROR (2091, X'82B')");
		xw.println("      EventType: Remote");
		xw.println("    Description: On an MQOPEN or MQPUT1 call, a message is to be sent to a remote");
		xw.println("                 queue manager. The ObjectName or ObjectQMgrName field in the object");
		xw.println("                 descriptor specifies the name of a local definition of a remote queue");
		xw.println("                 but one of the following applies to the XmitQName attribute of the");
		xw.println("                 definition. Either:"); 
		xw.println("                    - XmitQName is not blank, but specifies a queue that is not a local");
		xw.println("                      queue, or");
        xw.println("                    - XmitQName is blank, but RemoteQMgrName specifies a queue that is");
        xw.println("                      not a local queue.");
        xw.println("                 This also occurs if the queue name is resolved through a cell directory,");
        xw.println("                 and the remote queue manager name obtained from the cell directory is the");
        xw.println("                 name of a queue, but this is not a local queue.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
		xw.println("      XmitQName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_XMIT_Q_NAME));
		xw.print("          QType: " + qType + " (");
		
		switch (qType) {
			case CMQC.MQQT_ALIAS: 
				xw.println("MQQT_ALIAS)");
				break;
			case CMQC.MQQT_REMOTE:
				xw.println("MQQT_REMOTE)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
		
		xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
				                              applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
		xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));	
		try {
			xw.println(" ObjectQMgrName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_OBJECT_Q_MGR_NAME));
		} catch (MQException mqe) {;}
		
		// New field for 7.1
		try {
			xw.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		} catch (MQException mqe) {;}
														
		// New field for 7.1
		try {
			xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		} catch (MQException mqe) {;}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME);
	} // end of method displayTransmission_Queue_Type_Error()
	
	private String displayTransmission_Queue_Usage_Error(PCFMessage pcfMsg)
		throws PCFException {
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Transmission Queue Usage Error - MQRC_XMIT_Q_USAGE_ERROR (2092, X'82C')");
		xw.println("      EventType: Remote");
		xw.println("    Description: On an MQOPEN or MQPUT1 call, a message is to be sent to a");
		xw.println("                 remote queue manager, but one of the following occurred:"); 
		xw.println("                   - ObjectQMgrName specifies the name of a local queue, but it");
		xw.println("                     does not have a Usage attribute of MQUS_TRANSMISSION.");
		xw.println("                   - The ObjectName or ObjectQMgrName field in the object");
		xw.println("                     descriptor specifies the name of a local definition of a");
		xw.println("                     remote queue but one of the following applies to the");
		xw.println("                     XmitQName attribute of the definition:");
		xw.println("                        - XmitQName is not blank, but specifies a queue that");
		xw.println("                          does not have a Usage attribute of MQUS_TRANSMISSION.");
		xw.println("                        - XmitQName is blank, but RemoteQMgrName specifies a");
		xw.println("                          queue that does not have a Usage attribute of");
		xw.println("                          MQUS_TRANSMISSION.");
		xw.println("                   - The queue name is resolved through a cell directory, and");
		xw.println("                     the remote queue manager name obtained from the cell");
		xw.println("                     directory is the name of a local queue, but it does not");
		xw.println("                     have a Usage attribute of MQUS_TRANSMISSION.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
		xw.println("      XmitQName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_XMIT_Q_NAME));
		xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
				                              applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
		xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));	
		
		try {
			xw.println(" ObjectQMgrName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_OBJECT_Q_MGR_NAME));
		} catch (MQException mqe) {;}
		
		// New field for 7.1
		try {
			xw.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		} catch (MQException mqe) {;}
														
		// New field for 7.1
		try {
			xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		} catch (MQException mqe) {;}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_XMIT_Q_NAME);
	} // end of method displayTransmission_Queue_Usage_Error()
	
	private String displayUnknown_Alias_Base_Queue(PCFMessage pcfMsg)
		throws PCFException {
		
		int baseType = pcfMsg.getIntParameterValue(CMQC.MQIA_BASE_TYPE);
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Unknown Alias Base Queue - MQRC_UNKOWN_ALIAS_BASE_Q (2082, X'822')");
		xw.println("      EventType: Local");
		xw.println("    Description: An MQOPEN or MQPUT1 call was issued specifying an alias queue");
		xw.println("                 as the destination, but the BaseObjectName in the alias queue");
		xw.println("                 attributes is not recognized as a queue or topic name.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
		xw.println(" BaseObjectName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_BASE_OBJECT_NAME));
		xw.print("       BaseType: " + baseType + " (");
		
		switch (baseType) {
			case CMQC.MQOT_Q: 
				xw.println("MQOT_Q)");
				break;
			case CMQC.MQOT_TOPIC:
				xw.println("MQOT_TOPIC)");
				break;
			default:
				xw.println("Unknown!)");
		} // end switch
		
		xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
				                              applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
		xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));	
		
		try {
			xw.println(" ObjectQMgrName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_OBJECT_Q_MGR_NAME));
		} catch (MQException mqe) {;}
		
		// New field for 7.1
		try {
			xw.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		} catch (MQException mqe) {;}
				
		// New field for 7.1
		try {
			xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		} catch (MQException mqe) {;}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_BASE_OBJECT_NAME);
	} // end of method displayUnknown_Alias_Base_Queue()h
	
	private String displayUnknown_Default_Transmission_Queue(PCFMessage pcfMsg)
		throws PCFException {
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Unknown Default Transmission Queue - MQRC_UNKNOWN_DEF_XMIT_Q (2197, X'895')");
		xw.println("      EventType: Remote");
		xw.println("    Description: An MQOPEN or MQPUT1 call was issued specifying a remote queue as the");
		xw.println("                 destination. If a local definition of the remote queue was specified, or");
		xw.println("                 if a queue-manager alias is being resolved, the XmitQName attribute in the");
		xw.println("                 local definition is blank.");
		xw.println("                 No queue is defined with the same name as the destination queue manager.");
		xw.println("                 The queue manager has therefore attempted to use the default transmission");
		xw.println("                 queue. However, the name defined by the DefXmitQName queue-manager attribute");
		xw.println("                 is not the name of a locally-defined queue.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
		xw.println("      XmitQName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_XMIT_Q_NAME));
		xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
				                              applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
		xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));	
		
		try {
			xw.println(" ObjectQMgrName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_OBJECT_Q_MGR_NAME));
		} catch (MQException mqe) {;}
		
		// New field for 7.1
		try {
			xw.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		} catch (MQException mqe) {;}
						
		// New field for 7.1
		try {
			xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		} catch (MQException mqe) {;}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_XMIT_Q_NAME);
	} // end of method displayUnknown_Default_Transmission_Queue()
	
	private String displayUnknown_Object_Name(PCFMessage pcfMsg)
		throws PCFException {
	
		String retString = "";
		
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Unknown Object Name - MQRC_UNKNOWN_OBJECT_NAME (2085, X'825')");
		xw.println("      EventType: Local");
		xw.println("    Description: On an MQOPEN or MQPUT1 call, the ObjectQMgrName field in the");
		xw.println("                 object descriptor MQOD is set to one of the following.");
		xw.println("                 It is either: ");
		xw.println("                     - Blank");
		xw.println("                     - The name of the local queue manager");
		xw.println("                     - The name of a local definition of a remote queue manager");
		xw.println("                       (a queue-manager alias) in which the RemoteQMgrName");
		xw.println("                       attribute is the name of the local queue manager.");
		xw.println("                 However, the ObjectName in the object descriptor is not");
		xw.println("                 recognized for the specified object type.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
				                              applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
		xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));	
		try {
			xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
			retString = pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME);
		} catch (MQException mqe) {;}
		try {
			xw.println("    ProcessName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_PROCESS_NAME));
			retString = pcfMsg.getStringParameterValue(CMQC.MQCA_PROCESS_NAME);
		} catch (MQException mqe) {;}
		try {
			xw.println(" ObjectQMgrName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_OBJECT_Q_MGR_NAME));
			retString = pcfMsg.getStringParameterValue(CMQCFC.MQCACF_OBJECT_Q_MGR_NAME);
		} catch (MQException mqe) {;}
		try {
			xw.println("      TopicName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_TOPIC_NAME));
			retString = pcfMsg.getStringParameterValue(CMQC.MQCA_TOPIC_NAME);
		} catch (MQException mqe) {;}
		
		// New field for 7.1
		try {
			xw.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		} catch (MQException mqe) {;}
								
		// New field for 7.1
		try {
			xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		} catch (MQException mqe) {;}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + retString;
	} // end of method displayUnknown_Object_Name()
	
	private String displayUnknown_Remote_Queue_Manager(PCFMessage pcfMsg)
		throws PCFException {
	
		String qMgr = pcfMsg.getStringParameterValue(2015);
		
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Unknown Remote Queue Manager - MQRC_UNKNOWN_REMOTE_Q_MGR (2087, X'827')");
		xw.println("      EventType: Remote");
		xw.println("    Description: On an MQOPEN or MQPUT1 call, an error occurred with the");
		xw.println("                 queue-name resolution, for different reasons (refer to the");
		xw.println("                 documentation for complete details).");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
		xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
				                              applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
		xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));	
		try {
			qMgr = pcfMsg.getStringParameterValue(CMQCFC.MQCACF_OBJECT_Q_MGR_NAME);
			xw.println(" ObjectQMgrName: " + qMgr);
		} catch (MQException mqe) {;}
		
		// New field for 7.1
		try {
			xw.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		} catch (MQException mqe) {;}
										
		// New field for 7.1
		try {
			xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		} catch (MQException mqe) {;}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + qMgr;
	} // end of method displayUnknown_Remote_Queue_Manager()
	
	private String displayUnknown_Transmission_Queue(PCFMessage pcfMsg)
		throws PCFException {
	
		xw.println("     ReasonCode: " + pcfMsg.getReason());
		xw.println("      EventName: Unknown Transmission Queue - MQRC_UNKNOWN_XMIT_Q (2196, X'894')");
		xw.println("      EventType: Remote");
		xw.println("    Description: On an MQOPEN or MQPUT1 call, a message is to be sent to a");
		xw.println("                 remote queue manager. The ObjectName or the ObjectQMgrName");
		xw.println("                 in the object descriptor specifies the name of a local");
		xw.println("                 definition of a remote queue (in the latter case");
		xw.println("                 queue-manager aliasing is being used). However, the XmitQName");
		xw.println("                 attribute of the definition is not blank and not the name of");
		xw.println("                 a locally-defined queue.");
		xw.println("       QMgrName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
		xw.println("          QName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_Q_NAME));
		xw.println("      XmitQName: " + pcfMsg.getStringParameterValue(CMQC.MQCA_XMIT_Q_NAME));
		xw.println("       ApplType: " + pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE) + " (" +
				                              applTypeToString(pcfMsg.getIntParameterValue(CMQC.MQIA_APPL_TYPE)) + ")");
		xw.println("       ApplName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_APPL_NAME));	
		try {
			xw.println(" ObjectQMgrName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACF_OBJECT_Q_MGR_NAME));
		} catch (MQException mqe) {;}
		
		// New field for 7.1
		try {
			xw.println("       ConnName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CONNECTION_NAME));
		} catch (MQException mqe) {;}
										
		// New field for 7.1
		try {
			xw.println("    ChannelName: " + pcfMsg.getStringParameterValue(CMQCFC.MQCACH_CHANNEL_NAME));
		} catch (MQException mqe) {;}
		
		return rtrim(pcfMsg.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME)) + " - " + pcfMsg.getReason() + " - " + pcfReasonToString(pcfMsg.getReason()) + " ==> " + pcfMsg.getStringParameterValue(CMQC.MQCA_XMIT_Q_NAME);
	} // end of method displayUnknown_Transmission_Queue()
	
	/**
	 * Parse command line arguments
	 * 
	 * @param args Command line arguments
	 */
	private int parseArgs(String[] args) {
		
		SimpleDateFormat format = null;
		
		int argc = args.length;
		char c = ' ';
		
		// Display usage if no argument passed or ? is passed
		if (argc == 0 || (argc == 1 && (args[0].compareTo("?") == 0 ||
				                        args[0].compareTo("-?") == 0 ||
				                        args[0].compareTo("-h") == 0 ||
				                        args[0].compareTo("--help") == 0))) {
			usage();
			return 99;
		}
		
		System.out.println(Xmqdspev.progName + " v" + Xmqdspev.progVersion + " - Developed by " + Xmqdspev.progAuthor);
		System.out.println();
		
		// Parse arguments
		for (int i = 0; i < argc; i++) {
			
			if (args[i].startsWith("-")) {
				c = args[i].charAt(1);
				
				switch (c) {
					case 'a':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							return 98;
						}
						this.afterEventDate = Calendar.getInstance();
						format = new SimpleDateFormat("yyyyMMddHHmmss");
						try {
							this.afterEventDate.setTime(format.parse(args[i + 1]));
						} catch (ParseException pe) {
							System.err.println("Argument for option -" + c + " is an invalid timestamp!");
							System.err.println(pe.getLocalizedMessage());
							return 98;
						}
							
						i++;
						break;
					case 'b':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							return 98;
						}
						this.channelTableName = args[i + 1];
						i++;
						break;
					case 'c':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							return 98;
						} 
						this.channelName = args[i + 1];
						i++;
						break;
					case 'd':
						if (i + 1 < argc && !args[i + 1].startsWith("-")) {
							System.err.println("Option -" + c + " does not accept any arguments!");
							return 98;
						} 
						this.displayDetails = true;
						break;
					case 'e':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							return 98;
						} 
						this.emailConfigFileName = args[i + 1];
						i++;
						break;
					case 'f':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							return 98;
						} 
						this.fileName = args[i + 1];
						i++;
						break;
					case 'g':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							return 98;
						} 
						this.grepString = args[i + 1];
						i++;
						break;
					case 'i':
						try {
							if (i + 1 >= argc || args[i + 1].startsWith("-")) {
								System.err.println("Argument for option -" + c + " must be specified!");
								return 98;
							}
							this.waitInterval = Integer.parseInt(args[i + 1]);	
						} catch (NumberFormatException e) {
							System.err.println("Argument (" + args[i + 1] + ") for option -" + c + " must be an integer!");
							return 98;
						}
						i++;
						break;
					case 'l':
						if (i + 1 < argc && !args[i + 1].startsWith("-")) {
							System.err.println("Option -" + c + " does not accept any arguments!");
							return 98;
						}
						this.useMQCHLTAB = true;
						break;
					case 'm':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							return 98;
						} 
						this.qmgrName = args[i + 1];
						i++;
						break;	
					case 'n':
						try {
							if (i + 1 >= argc || args[i + 1].startsWith("-")) {
								System.err.println("Argument for option -" + c + " must be specified!");
								return 98;
							}
							this.readFirstNEvents = Integer.parseInt(args[i + 1]);	
						} catch (NumberFormatException e) {
							System.err.println("Argument (" + args[i + 1] + ") for option -" + c + " must be an integer!");
							return 98;
						}
						i++;
						break;
					case 'o':
						if(i + 1 >= argc || args[i + 1].startsWith("-")) {
				            System.err.println("Argument for option -" + c + " must be specified!");
				            return 98;
				        }
						
				        String event = null;
				        String ev[] = args[i + 1].split(",");
				        omitEventList = new int[ev.length];
				        
				        try {
				        	for (int j = 0; j < ev.length; j++) {
				                event = ev[j];
				                omitEventList[j] = Integer.parseInt(ev[j]);
				            } // end for
				        
				        this.omitEvent = true;

				        } catch(NumberFormatException e) {
				            System.err.println("Argument (" + event + ") for option -" + c + " must be an integer!");
				            return 98;
				        }
				        i++;
				        break;
					case 'p':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							return 98;
						}
						this.priorEventDate = Calendar.getInstance();
						format = new SimpleDateFormat("yyyyMMddHHmmss");
						try {
							this.priorEventDate.setTime(format.parse(args[i + 1]));
						} catch (ParseException pe) {
							System.err.println("Argument for option -" + c + " is an invalid timestamp!");
							System.err.println(pe.getLocalizedMessage());
							return 98;
						}
							
						i++;
						break;
					case 'q':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							return 98;
						} 
						this.qName = args[i + 1];
						i++;
						break;
					case 'r':
						if (i + 1 < argc && !args[i + 1].startsWith("-")) {
							System.err.println("Option -" + c + " does not accept any arguments!");
							return 98;
						} 
						this.browseMode = false;
						break;
					case 's':
						if (i + 1 < argc && !args[i + 1].startsWith("-")) {
							System.err.println("Option -" + c + " does not accept any arguments!");
							return 98;
						}
						this.skipPastLastEvent = true;
						break;
					case 't':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							return 98;
						} 
						this.topicString = args[i + 1];
						i++;
						break;
					case 'u':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							return 98;
						}
						this.cipherSuite = args[i + 1];
						i++;
						break;
					case 'v':
						if (i + 1 < argc && !args[i + 1].startsWith("-")) {
							System.err.println("Option -" + c + " does not accept any arguments!");
							return 98;
						}
						this.useMQSERVER = true;
						break;
					case 'w':
						if (i + 1 < argc && !args[i + 1].startsWith("-")) {
							System.err.println("Option -" + c + " does not accept any arguments!");
							return 98;
						} 
						this.waitMode = true;
						if (this.waitInterval == 0) this.waitInterval = 2000;
						break;
					case 'x':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							return 98;
						} 
							
						int p1, p2;
						if ((p1 = args[i + 1].indexOf('(')) != -1) {
							p2 = args[i + 1].indexOf(')');
							this.connectionName = args[i + 1].substring(0, p1);
							this.portNumber = new Integer(args[i + 1].substring(p1 + 1, p2)).intValue();
						} else this.connectionName = args[i + 1];
						i++;
						break;
					case 'y':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							return 98;
						} 
						this.userId = args[i + 1];
						i++;
						break;	
					case 'z':
						if (i + 1 >= argc || args[i + 1].startsWith("-")) {
							System.err.println("Argument for option -" + c + " must be specified!");
							return 98;
						} 
						this.password = args[i + 1];
						i++;
						break;	
					case '0':
						if (i + 1 < argc && !args[i + 1].startsWith("-")) {
							System.err.println("Option -" + c + " does not accept any arguments!");
							return 98;
						} 
						this.debugMode = true;
						break;
					default: 
						System.err.println(args[i] + " is not a valid option");
						return 98;
				} // end switch
			} // end if
        } // end for
		
		if (this.qmgrName == null) {
			System.err.println("Missing option -m, queue manager name must be specified!");
			return 98;
		}
		
		if (this.qName == null && this.topicString == null) {
			System.err.println("One of the following options -q, -t must be specified!");
			return 98;
		}
		
		if (this.qName != null && this.topicString != null) {
			System.err.println("Options -q and -t cannot be specified together!");
			return 98;
		}
		
		if (this.topicString != null) {
			if (this.readFirstNEvents != 0 ||
				this.skipPastLastEvent ||
				this.afterEventDate != null ||
				this.priorEventDate != null ||
				!this.browseMode) {
					System.err.println("Options -a, -p, -n, -r, and -s are not valid with option -t!");
					return 98;
			}
		}
		
		if (this.topicString != null) this.browseMode = false;
		
		if (this.channelName != null && this.connectionName == null) {
			System.err.println("Missing option -x, connection name must be specified when option -c is used!");
			return 98;
		}
		
		if (this.connectionName != null && this.channelName == null) {
			System.err.println("Missing option -c, channel name must be specified when option -x is used!");
			return 98;
		}
		
		if (this.cipherSuite != null && this.connectionName == null && this.channelName == null) {
			System.err.println("Option -u, cipher suite must be specified only when options -c and -x are used!");
			return 98;
		}
		
		if (this.channelTableName != null && (this.connectionName != null || this.channelName != null ||
				                              this.cipherSuite != null || this.useMQCHLTAB || this.useMQSERVER)) {
			System.err.println("Option -b, channel table name cannot be used in conjunction with options -c, -l, -u, -v, -x!");
			return 98;
		}
		
		if (this.useMQCHLTAB && (this.connectionName != null || this.channelName != null ||
                this.cipherSuite != null || this.channelTableName != null || this.useMQSERVER)) {
			System.err.println("Option -l, MQCHLTAB/MQCHLLIB environment variables cannot be used in conjunction with options -b, -c, -u, -v, -x!");
			return 98;
		}
		
		if (this.useMQSERVER && (this.connectionName != null || this.channelName != null ||
                this.cipherSuite != null || this.channelTableName != null || this.useMQCHLTAB)) {
			System.err.println("Option -v, MQSERVER environment variable cannot be used in conjunction with options -b, -c, -l, -u, -x!");
			return 98;
		}
		
		if (this.readFirstNEvents > 0 && this.waitMode) {
			System.err.println("Options -n and -w cannot be specified together!");
			return 98;
		}
		
		if (this.waitInterval > 0 && !this.waitMode) {
			System.err.println("Option -i, wait interval must be used in conjunction with option -w!");
			return 98;
		}
		
		if (this.skipPastLastEvent && !this.waitMode) {
			System.err.println("Option -s, skip past last event must be used in conjunction with option -w!");
			return 98;
		}
		
		if (this.afterEventDate != null && this.priorEventDate != null) {
			System.err.println("Options -a and -p cannot be specified together!");
			return 98;
		}
		
		if (this.priorEventDate != null && this.waitMode) {
			System.err.println("Options -p and -w cannot be specified together!");
			return 98;
		}
		
		if(emailConfigFileName != null)
            try
            {
                em = new EMailer(emailConfigFileName);
                System.out.println("E-mailing of events has been enabled...");
                System.out.println("   SMTP server: " + em.getHost());
                System.out.println("     SMTP port: " + em.getPort());
                System.out.println("     SMTP auth: " + em.isAuthentication());
                System.out.println("      SMTP SSL: " + em.isEnableSSL());
                System.out.println("      SMTP TLS: " + em.isStartTLS());
                System.out.println("         DEBUG: " + em.isDebug() + " (Auth: " + em.isDebugAuth() + ")");
                System.out.println();
            }
            catch(IOException ioe)
            {
                System.err.println("IOException caught while reading email config file (" + emailConfigFileName + ")!");
                System.err.println("IOException returned is " + ioe.getLocalizedMessage());
                System.err.println("-e option has been disabled for this run.");
                System.err.println();
                emailConfigFileName = null;
            }
            
        xw = new XWriter();
        if(fileName != null)
            try
            {
                xw.setPrintToFile(fileName, true);
            }
            catch(FileNotFoundException fnfe)
            {
                System.err.println("FileNotFoundException - " + fnfe.getLocalizedMessage());
                System.err.println("Unable to use file '" + fileName + "' because of above error!");
                System.err.println("Option -f disabled. Reverting back to console mode only.");
                System.err.println();
                xw.setPrintToOut(true);
            }
        else
            xw.setPrintToOut(true);
		
		return 0;
	} // end of method parseArgs()
	
	/**
	 * Process the PCF message
	 * 
	 * @param msg MQ message read from the queue
	 */
	private boolean processPCFMessage(MQMessage msg, int platform, PCFMessageAgent pcfAgent) {
		
		PCFMessage pcfMsg = null;
		String timeStamp = null;
		String retValue = null;
        boolean eventDisplayed = true;
		//Calendar cal = null;
		
		int reasonCode;
		
		try {
			
			// Remove RFH2 header if not already removed (MQ bug?)
			/*try {
				if (msg.readStringOfCharLength(CMQC.MQ_FUNCTION_LENGTH).startsWith("RFH")) {
					msg.readInt();
					int sl = msg.readInt();
					int ml = msg.getMessageLength();
				
					byte msgData[] = new byte[ml];
					msg.seek(0);
					msg.readFully(msgData);
					msg.clearMessage();
					
					msg.write(msgData, sl, ml - sl);
				}
				
				msg.seek(0);
					
			} catch (java.io.EOFException eofe) { 
				eofe.printStackTrace(); 
			}	*/
				
			// Parse PCF message
			pcfMsg = new PCFMessage(msg);
			
			reasonCode = pcfMsg.getReason();
			
			if (this.omitEvent) {
				for (int i=0; i < this.omitEventList.length; i++) {
					if (reasonCode == this.omitEventList[i]) return false;
				} // end for
			} // end if
			
			timeStamp = new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss").format(msg.putDateTime.getTime());
			/*cal = Calendar.getInstance();
			cal = (Calendar)msg.putDateTime;
			
			if (this.afterEventDate != null && cal.before(this.afterEventDate)) return;
			if (this.priorEventDate != null && cal.after(this.priorEventDate)) return;*/
			
			xw.println("-------------------------------------------------------[" + timeStamp + "]---");
			
			switch (reasonCode) {
				// MQRC_ALIAS_BASE_Q_TYPE_ERROR
				case 2001:
					retValue = displayAlias_Base_Queue_Type_Error(pcfMsg);
					break;
				// MQRC_BRIDGE_STARTED (z/OS only)
				case 2125:
					retValue = displayBridge_Started(pcfMsg);
					break;
				// MQRC_BRIDGE_STOPPED (z/OS only)
				case 2126:
					retValue = displayBridge_Stopped(pcfMsg);
					break;
				// MQRC_CONFIG_CHANGE_OBJECT (z/OS only)
				case 2368:
					retValue = displayChange_Object(pcfMsg, platform);
					break;
				// MQRC_CHANNEL_ACTIVATED
				case 2295:
					retValue = displayChannel_Activated(pcfMsg);
					break;
				// MQRC_CHANNEL_AUTO_DEF_ERROR (not z/OS)
				case 2234:
					retValue = displayChannel_Auto_Definition_Error(pcfMsg);
					break;
				// MQRC_CHANNEL_AUTO_DEF_OK (not z/OS)
				case 2233:
					retValue = displayChannel_Auto_Definition_OK(pcfMsg);
					break;
				// New for 7.1
				case 2577:
				case 2578:
					retValue = displayChannel_Blocked(pcfMsg);
					break;
				// MQRC_CHANNEL_CONV_ERROR
				case 2284:
					retValue = displayChannel_Conversion_Error(pcfMsg);
					break;
				// MQRC_CHANNEL_NOT_ACTIVATED
				case 2296:
					retValue = displayChannel_Not_Activated(pcfMsg);
					break;
				// New for 7.1
				// MQRC_CHANNEL_NOT_AVAILABLE
				case 2537:
					retValue = displayChannel_Not_Available(pcfMsg);
					break;
				// MQRC_CHANNEL_SSL_ERROR
				case 2371:
					retValue = displayChannel_SSL_Error(pcfMsg);
					break;
					// MQRC_CHANNEL_SSL_WARNING
				case 2552:
					retValue = displayChannel_SSL_Warning(pcfMsg);
					break;
				// MQRC_CHANNEL_STARTED
				case 2282:
					retValue = displayChannel_Started(pcfMsg);
					break;	
				// MQRC_CHANNEL_STOPPED
				case 2283:
					retValue = displayChannel_Stopped(pcfMsg);
					break;
				// MQRC_CHANNEL_STOPPED_BY_USER
				case 2279:
					retValue = displayChannel_Stopped_By_User(pcfMsg);
					break;	
				// MQRC_COMMAND_MQSC
				case 2412:
				// MQRC_COMMAND_PCF
				case 2413:
					retValue = displayCommand(pcfMsg, platform);
					break;	
				// MQRC_CONFIG_CREATE_OBJECT (z/OS only)
				case 2367:
					retValue = displayCreate_Object(pcfMsg, platform);
					break;	
				// MQRC_DEF_XMIT_Q_TYPE_ERROR
				case 2198:
					retValue = displayDefault_Transmission_Queue_Type_Error(pcfMsg);
					break;	
				// MQRC_DEF_XMIT_Q_USAGE_ERROR
				case 2199:
					retValue = displayDefault_Transmission_Queue_Usage_Error(pcfMsg);
					break;
				// MQRC_CONFIG_DELETE_OBJECT (z/OS only)
				case 2369:
					retValue = displayDelete_Object(pcfMsg, platform);
					break;		
				// MQRC_GET_INHIBITED
				case 2016:
					retValue = displayGet_Inhibited(pcfMsg);
					break;	
				// MQRC_LOGGER_STATUS (z/OS only)
				case 2411:
					retValue = displayLogger_Status(pcfMsg);
					break;
				// MQRC_NOT_AUTHORIZED (not z/OS)
				case 2035:
					retValue = displayNot_Authorized(pcfMsg);
					break;
				// MQRC_PUT_INHIBITED
				case 2051:
					retValue = displayPut_Inhibited(pcfMsg);
					break;
				// MQRC_Q_DEPTH_HIGH
				case 2224:
					retValue = displayQueue_Depth_High(pcfMsg);
					break;	
				// MQRC_Q_DEPTH_LOW
				case 2225:
					retValue = displayQueue_Depth_Low(pcfMsg);
					break;	
				// MQRC_Q_FULL
				case 2053:
					retValue = displayQueue_Full(pcfMsg);
					break;
				// MQRC_Q_MGR_ACTIVE
				case 2222:
					retValue = displayQueue_Manager_Active(pcfMsg);
					break;
				// MQRC_Q_MGR_NOT_ACTIVE (not z/OS)
				case 2223:
					retValue = displayQueue_Manager_Not_Active(pcfMsg);
					break;	
				// MQRC_Q_SERVICE_INTERVAL_HIGH
				case 2226:
					retValue = displayQueue_Service_Interval_High(pcfMsg);
					break;	
				// MQRC_Q_SERVICE_INTERVAL_OK
				case 2227:
					retValue = displayQueue_Service_Interval_OK(pcfMsg);
					break;
				// MQRC_Q_TYPE_ERROR
				case 2057:
					retValue = displayQueue_Type_Error(pcfMsg);
					break;	
				// MQRC_CONFIG_REFRESH_OBJECT (z/OS only)
				case 2370:
					retValue = displayRefresh_Object(pcfMsg, platform);
					break;			
				// MQRC_REMOTE_Q_NAME_ERROR
				case 2184:
					retValue = displayRemote_Queue_Name_Error(pcfMsg);
					break;	
				// MQRC_XMIT_Q_TYPE_ERROR
				case 2091:
					retValue = displayTransmission_Queue_Type_Error(pcfMsg);
					break;
				// MQRC_XMIT_Q_USAGE_ERROR
				case 2092:
					retValue = displayTransmission_Queue_Usage_Error(pcfMsg);
					break;	
				// MQRC_UNKOWN_ALIAS_BASE_Q
				case 2082:
					retValue = displayUnknown_Alias_Base_Queue(pcfMsg);
					break;
				// MQRC_UNKNOWN_DEF_XMIT_Q
				case 2197:
					retValue = displayUnknown_Default_Transmission_Queue(pcfMsg);
					break;
				// MQRC_UNKNOWN_OBJECT_NAME
				case 2085:
					retValue = displayUnknown_Object_Name(pcfMsg);
					break;
				// MQRC_UNKNOWN_REMOTE_Q_MGR
				case 2087:
					retValue = displayUnknown_Remote_Queue_Manager(pcfMsg);
					break;					
				// MQRC_UNKNOWN_XMIT_Q
				case 2196:
					retValue = displayUnknown_Transmission_Queue(pcfMsg);
					break;
				default:
					xw.println("MQCFH (PCF Header) reason code unknown!");
				    xw.println("Message is probably not an event message.");
			} // end switch
			
			if (grepString != null) {
				if (xw.searchAllLines(grepString)) {
	                displayEvent(pcfMsg.getReason(), retValue);
	            } else {
	                xw.clearAllLines();
	                eventDisplayed = false;
	            }
	        } else {
	            displayEvent(pcfMsg.getReason(), retValue);
	        }
			
			// Debug mode active?
			if (this.debugMode) {
				System.out.println("******* DEBUG MODE (option -z) ********");
				System.out.println(">>>>> Dump of PCF Message - START <<<<<");
				System.out.println(pcfMsg);
				System.out.println(">>>>> Dump of PCF Message - END   <<<<<");
			} // end if
			
		} catch (IOException ioe) {
	    	  System.err.println("IOException while processing PCF message - " + ioe.getLocalizedMessage());
		} catch (MQException mqe) {
			System.err.println("MQException while processing PCF message - CC: " + mqe.completionCode + " RC: " + mqe.reasonCode);
			if (mqe.reasonCode == CMQC.MQRC_INSUFFICIENT_DATA) {
				System.err.println("Message is not in a proper PCF format and is unlikely to be an event message!");
			} else {
				System.err.println(mqe.getLocalizedMessage());
			} // end if
		} 
		
		return eventDisplayed;
	} // end of method processPCFMessage()
	
	private void displayEvent(int eventReason, String eventData) {
		
        StringBuffer sb = new StringBuffer();
        if(emailConfigFileName != null) {
            for(int i = 1; i <= xw.getTotalLines(); i++)
                sb.append(xw.getLine(i));

            em.setSubject("MQ Event - " + eventData);
            em.sendMail(sb.toString());
        } // end if
        xw.flushAllLines();
    } // end of method displayEvent()
	
	/**
	 * Run the utility
	 * 
	 * @param args Command line arguments
	 */
	private int run(String[] args) {
		
		Calendar cal = null;
		
		URL ccdt = null;
		
		//MQQueueManager qmgr = null;
		MQQueue queue = null;
		MQTopic topic = null;
		MQGetMessageOptions gmo = new MQGetMessageOptions();
		MQMessage msg = new MQMessage();
		
		int platform;
		int version = 0;
		
		int eventsReadCount = 0;
		int eventsDisplayedCount = 0;
		int eventsSkipCount = 0;
		
		boolean loop = true;
		
		int rc = 0;
		
		MQException.log = null;
		
		// Process the MQTMC2 header if the utility was triggered
		if (args.length != 0 && args[0].startsWith("TMC ")) {
			MQTMC2 trigMsg = new MQTMC2(args[0]);
			
			args = ("-m " + trigMsg.qMgrName + " -q " + trigMsg.qName + " " + trigMsg.userData).split("\\s");
		}
		
		if ((rc = parseArgs(args)) == 0) {
			
			// Debug mode active?
			if (this.debugMode) {
				System.out.println("*** Debug mode is active ***");
				System.out.println();
			} // end if
			
			// Connect to queue manager
			try {
				if (this.userId != null) MQEnvironment.userID = this.userId;
				if (this.password != null) MQEnvironment.password = this.password;
				
				// Use specified client connection (options -c and -x)
				if (this.connectionName != null) {
					MQEnvironment.channel = this.channelName;
					MQEnvironment.port = this.portNumber;
					MQEnvironment.hostname = this.connectionName;
					//MQEnvironment.userID = System.getProperty("user.name");
					
					if (this.cipherSuite != null) {
						MQEnvironment.sslCipherSuite = this.cipherSuite;
					} // end if
					
					this.qmgr = new MQQueueManager(this.qmgrName);
					
				    // Use environment variable MQSERVER (option -r)
				} else if (useMQSERVER) {
					XMQUtils.useEnvVarMQSERVER();
					this.qmgr = new MQQueueManager(this.qmgrName);
					// Use environment variables MQCHLTAB and MQCHLLIB (option -l)
				} else if (useMQCHLTAB) {
					//System.setProperty("javax.net.ssl.trustStore","C:\\mq\\ssl\\key.jks"); //Path to trustStore 
					//System.setProperty("javax.net.ssl.trustStorePassword", "oliver2407"); //keyStore password 
					//System.setProperty("javax.net.ssl.keyStore","C:\\mq\\ssl\\key.jks");  //Path to keyStore 
					//System.setProperty("javax.net.ssl.keyStorePassword", "oliver2407"); //keyStore password
					ccdt = XMQUtils.useEnvVarMQCHLTAB();
					this.qmgr = new MQQueueManager(this.qmgrName, ccdt);
					// Use client channel table name (option -b)
				} else if (channelTableName != null) {
					ccdt = XMQUtils.useClientChannelTable(this.channelTableName);
					this.qmgr = new MQQueueManager(this.qmgrName, ccdt);
				} else {
					this.qmgr = new MQQueueManager(this.qmgrName);
				} // end if
			
				//this.qmgr = new MQQueueManager(this.qmgrName);
			} catch (MQException mqe) {
				System.err.println("MQCONN ended with reason code " + mqe.reasonCode);
				return mqe.reasonCode;
			}
			
			System.out.print("Connected to queue manager '" + this.qmgrName + "'");
			
			// Get queue manager platform and level
			try {
				version = this.qmgr.getCommandLevel();
				if (this.qmgr.getCommandLevel() > 699) {
					int[] qmgrAttrs = new int[2];
					this.qmgr.inquire(new int[] { CMQC.MQIA_PLATFORM, CMQC.MQIA_COMMAND_LEVEL }, qmgrAttrs, new char[0]);
					platform = qmgrAttrs[0];
					version = qmgrAttrs[1];
					System.out.print(" (Platform=" + platformToString(platform) + ", Level=" + version + ")");
				} else {
					// For WMQ V6
					version = this.qmgr.getCommandLevel();
					if (this.qmgr.getCommandInputQueueName().equalsIgnoreCase("SYSTEM.COMMAND.INPUT")) platform = CMQC.MQPL_ZOS;
					else platform = 0;
					System.out.print(" (Level=" + version + ")");
				} // end if
			} catch (MQException mqe) {
				System.err.println("Attempt to retrieve the platform type ended with reason code " + mqe.reasonCode);
				try {
					this.qmgr.disconnect();
				} catch (MQException mqe2) {;}
				return mqe.reasonCode;
			} catch (NoSuchMethodError nsme) {
				platform = 0;
				System.out.print(" (Level=" + version + ")");
			}
			
			System.out.println();
			
			// Open queue for read or browse or topic for read
			try {
				//String uniqueID = UUID.randomUUID().toString();
				//if (this.topicString != null) topic = this.qmgr.accessTopic(this.topicString, null, CMQC.MQSO_CREATE | CMQC.MQSO_NON_DURABLE | CMQC.MQSO_FAIL_IF_QUIESCING | CMQC.MQSO_MANAGED, null, "MH05 - Topic: " + this.topicString);
				if (this.topicString != null) topic = this.qmgr.accessTopic(this.topicString, null, CMQC.MQTOPIC_OPEN_AS_SUBSCRIPTION, CMQC.MQSO_CREATE | CMQC.MQSO_NON_DURABLE | CMQC.MQSO_FAIL_IF_QUIESCING | CMQC.MQSO_MANAGED, null);	
				else {
					if (this.browseMode) queue = this.qmgr.accessQueue(qName, CMQC.MQOO_INPUT_AS_Q_DEF | CMQC.MQOO_BROWSE | CMQC.MQOO_FAIL_IF_QUIESCING, null, null, null);
					else queue = this.qmgr.accessQueue(this.qName, CMQC.MQOO_INPUT_AS_Q_DEF | CMQC.MQOO_FAIL_IF_QUIESCING, null, null, null);
				}
			} catch (MQException mqe) {
				System.err.println("MQOPEN ended with reason code " + mqe.reasonCode);
				try {
					if (pcfAgent != null) pcfAgent.disconnect();
					this.qmgr.disconnect();
				} catch (MQException mqe2) {;}
				return mqe.reasonCode;
			}
			
			if (this.browseMode) {
				if (this.waitMode) {
					gmo.options = CMQC.MQGMO_WAIT | CMQC.MQGMO_BROWSE_FIRST | CMQC.MQGMO_FAIL_IF_QUIESCING | CMQC.MQGMO_CONVERT;
					gmo.waitInterval = this.waitInterval;
				} else gmo.options = CMQC.MQGMO_BROWSE_FIRST | CMQC.MQGMO_FAIL_IF_QUIESCING | CMQC.MQGMO_CONVERT;
			} else if (this.waitMode) {
				gmo.options = CMQC.MQGMO_WAIT | CMQC.MQGMO_FAIL_IF_QUIESCING | CMQC.MQGMO_CONVERT;
				gmo.waitInterval = this.waitInterval;
			} else gmo.options = CMQC.MQGMO_FAIL_IF_QUIESCING | CMQC.MQGMO_CONVERT; 
			
			Runtime.getRuntime().addShutdownHook(new RunWhenShuttingDown());
			
			if (this.topicString != null) System.out.println("Processing EVENT topic '" + this.topicString + "'...");
			else System.out.println("Processing EVENT queue '" + this.qName + "'...");
			
			System.out.println();
			
			if (this.skipPastLastEvent) System.out.println("Skipping past the last event message...");
			
			do {
				try {
					if (this.qmgr.getCommandLevel() > 699) gmo.options = gmo.options | CMQC.MQGMO_NO_PROPERTIES;
					
					msg.clearMessage();
					//msg.correlationId = CMQC.MQCI_NONE;
					//msg.messageId = CMQC.MQMI_NONE;
					msg.correlationId = MQC.MQCI_NONE;
					msg.messageId = MQC.MQMI_NONE;
					msg.characterSet = CMQC.MQCCSI_Q_MGR;
					msg.encoding = CMQC.MQENC_NATIVE;
					
					//if (this.skipPastLastEvent) queue.get(msg, gmo, 0);
					if (this.topicString != null) topic.get(msg, gmo);
					else queue.get(msg, gmo);
				
					if (!this.skipPastLastEvent) {
						/*if (eventsSkipCount > 0) {
							System.out.println(eventsSkipCount + " event messages skipped");
							System.out.println();
							eventsSkipCount = 0;
						}*/
						
						cal = Calendar.getInstance();
						cal = (Calendar)msg.putDateTime;
						//System.out.println(cal.toString());
						
						if (this.afterEventDate != null && cal.before(this.afterEventDate)) { ; }
						else if (this.priorEventDate != null && cal.after(this.priorEventDate)) { ; }
						else {
							if (processPCFMessage(msg, platform, pcfAgent)) eventsDisplayedCount++;
							eventsReadCount++;
						}
					} else eventsSkipCount++;
					
					if (this.readFirstNEvents > 0 && eventsReadCount == this.readFirstNEvents) loop = false;
					
					if (this.browseMode) {
						if (this.waitMode) {
							gmo.options = CMQC.MQGMO_WAIT | CMQC.MQGMO_BROWSE_NEXT | CMQC.MQGMO_FAIL_IF_QUIESCING | CMQC.MQGMO_CONVERT;
							gmo.waitInterval = this.waitInterval;
						} else gmo.options = CMQC.MQGMO_BROWSE_NEXT | CMQC.MQGMO_FAIL_IF_QUIESCING | CMQC.MQGMO_CONVERT;
					}
				} catch (IOException ioe) {
					System.err.println("IOException while processing an MQ message | " + ioe.getLocalizedMessage());
				} catch (MQException mqe) {
					if (!(mqe.reasonCode == 2033)) {
						System.err.println("MQGET ended with reason code " + mqe.reasonCode);
						loop = false;
					} else if (!this.waitMode) loop = false;
						else {
							if (this.skipPastLastEvent) {
								System.out.println(eventsSkipCount + " event messages skipped");
								System.out.println();
								eventsSkipCount = 0;
							}
							this.skipPastLastEvent = false;
						}
				
				}
			} while (loop && !this.shutdown);
			
			shutdown = true;
			
			try {
				if (this.topicString != null) topic.close();
				else queue.close();
				if (pcfAgent != null) pcfAgent.disconnect();
				this.qmgr.disconnect();
			} catch (MQException mqe) { 
				; 
			} finally {
				System.out.println();
				System.out.println(eventsReadCount + " event message(s) processed.");
				System.out.println(eventsDisplayedCount + " event message(s) displayed.");
				System.out.println();
				System.out.println("Disconnected from queue manager '" + this.qmgrName + "'");
				System.out.println(Xmqdspev.progName + " v" + Xmqdspev.progVersion + " ended.");
				shutdownComplete = true;	
			}
			
		} // end if
		
		return rc;
	} // end of method run()
	
	private String toHex(String original) {
		
		if (original == null) return null;
		
		char[] chars = original.toCharArray();
		StringBuffer buffer = new StringBuffer(original.length() * 2);
		
		for (int i = 0; i < chars.length; i++){
			buffer.append(Integer.toString((chars[i] & 0xff) + 0x100, 16).substring(1));
		}

		return buffer.toString();
	} // end of method toHex()

	/**
	 * Display usage
	 * 
	 */
	private static void usage() {
		
		System.out.println("__  __                    _                      ");
        System.out.println("\\ \\/ /_ __ ___   __ _  __| |___ _ __   _____   __");
        System.out.println(" \\  /| '_ ` _ \\ / _` |/ _` / __| '_ \\ / _ \\ \\ / /");
        System.out.println(" /  \\| | | | | | (_| | (_| \\__ \\ |_) |  __/\\ V / ");
        System.out.println("/_/\\_\\_| |_| |_|\\__, |\\__,_|___/ .__/ \\___| \\_/  ");
        System.out.println("                   |_|         |_|               ");
        System.out.println();
        
		System.out.println(Xmqdspev.progName + " v" + Xmqdspev.progVersion + " - Display IBM WebSphere MQ Events");
		System.out.println(Xmqdspev.progCopyright);
		System.out.println();
		
		System.out.println(Xmqdspev.progName + " reads, interprets and displays IBM WebSphere MQ generated");
		System.out.println("event messages from SYSTEM.ADMIN.* event queues.");		
		System.out.println();
		
		System.out.println("Usage: Xmqdspev -m qmgr-name -q q-name [-d] [-f file] [-n num] [-r]");
        System.out.println("                [-w [-i interval] [-s]] ([-a timestamp] | [-p timestamp])");
        System.out.println("                [-e email-config] [-g grep-string] [-o event-list]");
        System.out.println("                [(-c chl-name -x conn-name [-u ciph-suite] | -v | -l |");
        System.out.println("                -b chl-tbl-name)] [-y user-id [-z pw]]");
        System.out.println();
        
        System.out.println("       Xmqdspev -m qmgr-name -t topic-string [-d] [-f file] [-w [-i interval]]");
        System.out.println("                [-e email-config] [-g grep-string] [-o event-list]");
        System.out.println("                [(-c chl-name -x conn-name [-u ciph-suite] | -v | -l |");
        System.out.println("                -b chl-tbl-name)] [-y user-id [-z pw]]");
		
		System.out.println("Options:");
		
		System.out.println("    -a timestamp     Display event messages dated after timestamp");
        System.out.println("    -b chl-tbl-name  Use the named client channel table");
        System.out.println("    -c chl-name      Channel name for client connection");
        System.out.println("    -d               Display details for commands & configuration events");
        System.out.println("    -e email-config  Email configuration file name to email events");
        System.out.println("    -f file          Output events to a file");
        System.out.println("    -g grep-string   Display only events matching a string");
        System.out.println("    -i interval      Wait interval (ms) to poll the event queue");
        System.out.println("    -l               Use the MQCHLTAB/MQCHLLIB environment variables");
        System.out.println("    -m qmgr-name     Name of the queue manager");
        System.out.println("    -n num           Display the first <num> event messages");
        System.out.println("    -o event-list    Omit specific events (comma delimited)");
        System.out.println("    -p timestamp     Display event messages dated before timestamp");
        System.out.println("    -q q-name        Name of the event queue");
        System.out.println("    -r               Destructively get event messages");
        System.out.println("    -s               Skip past the last event message");
        System.out.println("    -t topic-string  Topic string events are published on");
        System.out.println("    -u ciph-suite    Cipher suite for SSL connection");
        System.out.println("    -v               Use the MQSERVER environment variable");
        System.out.println("    -w               Wait mode");
        System.out.println("    -x conn-name     Connection name as host(port) for client connection");
        System.out.println("    -y user-id       User id used to connect to the queue manager");
        System.out.println("    -z pw            Password associated with the user-id");
        System.out.println();
		
		System.out.println("Send bug reports, comments, etc... to " + Xmqdspev.progAuthor + " at " + Xmqdspev.progAuthorEmail);
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
		Xmqdspev util = new Xmqdspev();
		retCode = util.run(args);			
		
		System.exit(retCode);
	} // end of method main()

	/**
	 * Execute the utility
	 * 
	 * @return the browse mode
	 */
	public int execute() {
		
		int rc = 0;
		StringBuffer args = new StringBuffer();
		
		if (this.qmgrName != null) args.append("-m " + this.qmgrName + " ");
		if (this.qName != null) args.append("-q " + this.qName + " ");
		if (this.fileName != null) args.append("-f " + this.fileName + " ");
		if (this.channelName != null) args.append("-c " + this.channelName + " ");
		if (this.connectionName != null) args.append("-x " + this.connectionName + " ");
		if (this.browseMode) args.append("-b ");
		if (this.waitMode) args.append("-w ");
		if (this.useMQSERVER) args.append("-v ");
		if (this.useMQCHLTAB) args.append("-l ");
		if (this.channelTableName != null) args.append("-b " + this.channelTableName + " ");
		if (this.cipherSuite != null) args.append("-u " + this.cipherSuite + " ");
		if (this.readFirstNEvents != 0) args.append("-n " + this.readFirstNEvents + " ");
		if (this.skipPastLastEvent) args.append("-s ");
		if (this.waitInterval != 0) args.append("-i " + this.waitInterval + " ");
		if (this.userId != null) args.append("-y " + this.userId + " ");
		//if (this.afterEventDate != null) args.append("-a ");
		//if (this.priorEventDate != null) args.append("-p ");
		
		rc = run((args.toString()).split("\\s"));
		
		return rc;
	} // end of method execute()
	
	/**
	 * Get browse mode
	 * 
	 * @return the browse mode
	 */
	public boolean isBrowseMode() {
		return browseMode;
	} // end of method isBrowseMode()

	/**
	 * Set browse mode
	 * 
	 * @param browseMode the browseMode to set
	 */
	public void setBrowseMode(boolean browseMode) {
		this.browseMode = browseMode;
	} // end of method setBrowseMode()

	/**
	 * Get channel name
	 * 
	 * @return the channelName
	 */
	public String getChannelName() {
		return channelName;
	} // end of method getChannelName()

	/**
	 * Set channel name
	 * 
	 * @param channelName the channelName to set
	 */
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	} // end of method setChannelName()
	
	/**
	 * Get channel table name
	 * 
	 * @return the channelTableName
	 */
	public String getChannelTableName() {
		return channelTableName;
	} // end of method getChannelTableName()

	/**
	 * Set channel table name
	 * 
	 * @param channelTableName the channelTableName to set
	 */
	public void setChannelTableName(String channelTableName) {
		this.channelTableName = channelTableName;
	} // end of method setChannelTableName()
	
	/**
	 * Get cipher suite
	 * 
	 * @return the cipherSuite
	 */
	public String getCipherSuite() {
		return cipherSuite;
	} // end of method getCipherSuite()

	/**
	 * Set cipher suite
	 * 
	 * @param cipherSuite the cipherSuite to set
	 */
	public void setCipherSuite(String cipherSuite) {
		this.cipherSuite = cipherSuite;
	} // end of method setCipherSuite()

	/**
	 * Get file name
	 * 
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	} // end of method getFileName()

	/**
	 * Set file name
	 * 
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	} // end of method setFileName()

	/**
	 * Get port number
	 * 
	 * @return the portNumber
	 */
	public int getPortNumber() {
		return portNumber;
	} // end of method getPortNumber()

	/**
	 * Set port number
	 * 
	 * @param portNumber the portNumber to set
	 */
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	} // end of method setPortNumber()

	/**
	 * Get queue manager name
	 * 
	 * @return the qmgrName
	 */
	public String getQmgrName() {
		return qmgrName;
	} // end of method getQmgrName()

	/**
	 * Set queue manager name
	 * 
	 * @param qmgrName the qmgrName to set
	 */
	public void setQmgrName(String qmgrName) {
		this.qmgrName = qmgrName;
	} // end of method setQmgrName()

	/**
	 * Get queue name
	 * 
	 * @return the qName
	 */
	public String getQName() {
		return qName;
	} // end of method getQName()

	/**
	 * Set queue name
	 * @param name the qName to set
	 */
	public void setQName(String name) {
		qName = name;
	} // end of method setQName()
	
	/**
	 * Get use MQCHLTAB environment variable
	 * 
	 * @return the useMQCHLTAB
	 */
	public boolean isUseMQCHLTAB() {
		return useMQCHLTAB;
	} // end of method isUseMQCHLTAB()

	/**
	 * Set use MQCHLTAB environment variable
	 * 
	 * @param useMQCHLTAB the useMQCHLTAB to set
	 */
	public void setUseMQCHTAB(boolean useMQCHLTAB) {
		this.useMQCHLTAB = useMQCHLTAB;
	} // end of method setUseMQCHLTAB()
	
	/**
	 * Get use MQSEVER environment variable
	 * 
	 * @return the useMQSERVER
	 */
	public boolean isUseMQSERVER() {
		return useMQSERVER;
	} // end of method isUseMQSERVER()

	/**
	 * Set use MQSERVER environment variable
	 * 
	 * @param useMQSERVER the useMQSERVER to set
	 */
	public void setUseMQSERVER(boolean useMQSERVER) {
		this.useMQSERVER = useMQSERVER;
	} // end of method setUseMQSERVER()

	/**
	 * Get wait mode
	 * 
	 * @return the waitMode
	 */
	public boolean isWaitMode() {
		return waitMode;
	} // end of method isWaitMode()

	/**
	 * Set wait mode
	 * 
	 * @param waitMode the waitMode to set
	 */
	public void setWaitMode(boolean waitMode) {
		this.waitMode = waitMode;
	} // end of method setWaitMode()

	/**
	 * Get display details
	 * 
	 * @return the displayDetails
	 */
	public boolean isDisplayDetails() {
		return displayDetails;
	} // end of method isDisplayDetails()

	/**
	 * Set display details
	 * 
	 * @param displayDetails the displayDetails to set
	 */
	public void setDisplayDetails(boolean displayDetails) {
		this.displayDetails = displayDetails;
	} // end of method setDisplayDetails()
	
	/**
	 * Get read first N events 
	 * 
	 * @return the readFirstNEvents
	 */
	public int getReadFirstNEvents() {
		return readFirstNEvents;
	} // end of method getReadFirstNEvents()

	/**
	 * Set read first N events
	 * 
	 * @param readFirstNEvents the readFirstNEvents to set
	 */
	public void setReadFirstNEvents(int readFirstNEvents) {
		this.readFirstNEvents = readFirstNEvents;
	} // end of method setReadFirstNEvents()


	public boolean isSkipPastLastEvent() {
		return skipPastLastEvent;
	}


	public void setSkipPastLastEvent(boolean skipPastLastEvent) {
		this.skipPastLastEvent = skipPastLastEvent;
	}


	public Calendar getAfterEventDate() {
		return afterEventDate;
	}


	public void setAfterEventDate(Calendar afterEventDate) {
		this.afterEventDate = afterEventDate;
	}


	public Calendar getPriorEventDate() {
		return priorEventDate;
	}


	public void setPriorEventDate(Calendar priorEventDate) {
		this.priorEventDate = priorEventDate;
	}


	public int getWaitInterval() {
		return waitInterval;
	}


	public void setWaitInterval(int waitInterval) {
		this.waitInterval = waitInterval;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	
	// Inner Classes 
    public class RunWhenShuttingDown extends Thread {
    	
        public void run() {
        	
        	if (!shutdown) {
        		System.out.println();
        		System.out.println("Program termination requested. Shutting down...");
        	} // end if
            
            shutdown = true;
            
            do {
	            try {
	            	Thread.sleep(500);
	            } catch (InterruptedException ie) {
	            	;
	            }
            //} while (!shutdownComplete && !error);
        	} while (!shutdownComplete);
        } // end of method run()
    } // Inner class RunWhenShuttingDown
	
} // end of class Xmqdspev
