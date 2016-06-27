__  __                    _
\ \/ /_ __ ___   __ _  __| |___ _ __   _____   __
 \  /| '_ ` _ \ / _` |/ _` / __| '_ \ / _ \ \ / /
 /  \| | | | | | (_| | (_| \__ \ |_) |  __/\ V /
/_/\_\_| |_| |_|\__, |\__,_|___/ .__/ \___| \_/
                   |_|         |_|

SupportPac MH05 v1.3 - IBM(R) WebSphere(R) MQ Events Display Tool

The IBM WebSphere MQ Events Display Tool SupportPac provides a simple but yet
powerful command line tool (Xmqdspev) to display WebSphere MQ events
that are generated on the SYSTEM.ADMIN.*.EVENT event queues. 

NAME
	Xmqdspev - Display IBM WebSphere MQ Events

VERSION
	1.3 (Java(TM))

PRE-REQS
        IBM WebSphere MQ V6, V7, V7.1, V7.5, V8.0

	If used with IBM WebSphere MQ V6 it requires JAR file com.ibm.mq.pcf-6.1.jar
    shipped with SupportPac MS0B - IBM WebSphere MQ Java classes for PCF.
	
	http://www-01.ibm.com/support/docview.wss?rs=171&uid=swg24000668&loc=en_US&cs=utf-8&lang=en
	
	If used to e-mail events it requires JAR file mail.jar shipped with Oracle(R) JavaMail API.
	
	http://www.oracle.com/technetwork/java/javamail/index.html
	
	If used to e-mail events and with a JRE/JDK 1.5 or lower it requires JAR file activation.jar
	shipped with Oracle(R) JavaBeans Activation Framework (JAF).
	
	http://www.oracle.com/technetwork/java/javase/index-jsp-136939.html

PURPOSE
	Xmqdspev reads, interprets and displays IBM WebSphere MQ generated
	event messages from SYSTEM.ADMIN.*.EVENT event queues:
	
	- SYSTEM.ADMIN.CHANNEL.EVENT
	- SYSTEM.ADMIN.COMMAND.EVENT
	- SYSTEM.ADMIN.CONFIG.EVENT
	- SYSTEM.ADMIN.LOGGER.EVENT
	- SYSTEM.ADMIN.PERFM.EVENT
	- SYSTEM.ADMIN.QMGR.EVENT

	The events supported are as follows:

	- Queue manager events
	- Channel and bridge events
	- Performance events
	- Configuration events
	- Command events
	- Logger events
	- WebSphere MQ AMS command and configuration events

USAGE		 		 
    Xmqdspev -m qmgr-name -q q-name [-d] [-f file] [-n num] [-r]
             [-w [-i interval] [-s]] ([-a timestamp] | [-p timestamp])
             [-e email-config] [-g grep-string] [-o event-list]
             [(-c chl-name -x conn-name [-u ciph-suite] [-y user-id]
             [-z pw] | -v | -l | -b chl-tbl-name)]

    Xmqdspev -m qmgr-name -t topic-string [-d] [-f file] [-w [-i interval]]
             [-e email-config] [-g grep-string] [-o event-list]
             [(-c chl-name -x conn-name [-u ciph-suite] [-y user-id]
             [-z pw] | -v | -l | -b chl-tbl-name)]
				 
OPTIONS

	?, -?, -h, --help or no options
		Displays usage and help.

	-a <timestamp>
		Only displays events generated after a specific date and time.
		Timestamp is expressed as yyyymmddhhmmss.

	-b <chl-tbl-name>
		Use the specified client channel table to connect to the
		queue manager.

	-c <chl-name>
		Specifies the name of the client channel to use to connect
		to the queue manager hosting the local queue. 

		This option is used to connect to a queue manager remotely
		using a client connection. Use this option in conjunction
                with options -x and -u if SSL is required.

	-d
		Displays command details for command and configuration events.
		
	-e <email-config>
		Specifies the name of a file containing SMTP configuration
		information to e-mail events to one or more e-mail address(es).

	-f <file-name>
		Specifies the name of a file to record the output generated
		by the tool. If the file specified already exists output is
 		appended. 
		
	-g <grep-string>
		Displays only events matching a string.

	-i <interval>
		Sets the wait interval (ms) to poll the event queue when wait mode is
 		selected (option -w). Defaults to 2000 ms if not specified.
		
	-l
		Connects to the queue manager using the MQCHLTAB/MQCHLLIB
		environment variables. If MQCHLTAB is not defined AMQCLCHL.TAB
		is used as the name of the client channel table. If MQCHLLIB is not
 		defined it defaults to the current directory.

	-m <qmgr-name>
		The name of the queue manager hosting the event queue.

	-n <num>
		Only displays the first <num> event messages.
		
	-o <event-list>
	    List of events (comma delimited) to omit.

	-p <timestamp>
		Only displays events generated prior to  a specific date and time.
		Timestamp is expressed as yyyymmddhhmmss.

	-q <q-name>
		The name of the event queue to be processed.

	-r
		Destructively read event messages from the event queue. When this option
		is not specified, event messages are browsed only.

	-s
		Skip pas the last event message on the event queue. Skipped event
		messages are not displayed. This option must be used in conjunction
		with option -w.
		
	-t <topic-string>
		The topic string the events are published on.

	-u <ciph-suite>
		Specifies which cipher suite to use for a SSL connection.
		
	-v
		Connects to the queue manager using the MQSERVER environment
		variable.	

	-w
		Wait mode. Once all event message have been processed, the tool waits
		indefinitely for a subsequent message. Use CTRL-C to terminate the
		execution of the tool.

	-x <conn-name>
		Specifies the TCP/IP connection name to use to connect
		to the queue manager hosting the local queue. The format is
		server-address(port) and must be enclosed in double quotes (""). 

		This option is used to connect to a queue manager remotely
		using a client connection. Use this option in conjunction
		with options -c and -u if SSL is required.

	-y <user-id>
       		User id used to connect to the queue manager (client connection only).
			
	-z <pw>
			Password associated with the user id.
		
INSTALLATION

	- Requires JRE or SDK 1.4.2 or higher
	- Unzip files to a directory of your choice

		- xmqdspev.cmd					Microsoft Windows script for xmqdspev
		- xmqdspev.sh					Linux/Unix script for xmqdspev
		- com.ibm.xmq.events.jar		JAR file containing the tool
		- readme.txt					This readme file
		- MH05.pdf                  	Full documentation
       	- Licenses              		Licenses directory

	- Customize the scripts for your environment as per instructions in the scripts
	- Run the tool, for example:

		* Bindings mode:
			xmqdspev -m QM -q SYSTEM.ADMIN.QMGR.EVENT

		* Client mode:
			xmqdspev-c SYSTEM.DEF.SVRCONN -m QM -q SYSTEM.ADMIN.QMGR.EVENT -x "localhost(1414)"

		* Display all event messages and remove them from the event queue
			xmqdspev -m QM -q SYSTEM.ADMIN.QMGR.EVENT -r

		* Wait mode
			xmqdspev -m QM -q SYSTEM.ADMIN.QMGR.EVENT -w

		* Skip past last event and wait for new event messages
			xmqdspev -m QM -q SYSTEM.ADMIN.QMGR.EVENT -s -w

		* Display the first 20 event messages
			xmqdspev -m QM -q SYSTEM.ADMIN.QMGR.EVENT -n 20

		* Display events prior to a specific date/time
			xmqdspev -m QM -q SYSTEM.ADMIN.QMGR.EVENT -p 20110120120000

		* Sending output to a file
			xmqdspev -m QM -q SYSTEM.ADMIN.QMGR.EVENT -f somefile
			
		* E-mail events
			xmqdspev -m QM -q SYSTEM.ADMIN.QMGR.EVENT -e mail.config
			
		* Processing events published to topics
			Convert SYSTEM.ADMIN.*.EVENT local queues to alias queues pointing to their
			respective topics. A sample topic hierarchy may be:
			
				system/admin/events/channel
				system/admin/events/command
				system/admin/events/config
				system/admin/events/logger
				system/admin/events/perfm
				system/admin/events/qmgr
			
			Using runmqsc:
				define topic(SYSTEM.ADMIN.CHANNEL.EVENT) type(LOCAL) topicstr(system/admin/events/channel)
				delete qlocal(SYSTEM.ADMIN.CHANNEL.EVENT)
				define qalias(SYSTEM.ADMIN.CHANNEL.EVENT) target(SYSTEM.ADMIN.CHANNEL.EVENT) targtype(TOPIC)
				
				define topic(SYSTEM.ADMIN.COMMAND.EVENT) type(LOCAL) topicstr(system/admin/events/command)
				delete qlocal(SYSTEM.ADMIN.COMMAND.EVENT)
				define qalias(SYSTEM.ADMIN.COMMAND.EVENT) target(SYSTEM.ADMIN.COMMAND.EVENT) targtype(TOPIC)
				
				define topic(SYSTEM.ADMIN.CONFIG.EVENT) type(LOCAL) topicstr(system/admin/events/config)
				delete qlocal(SYSTEM.ADMIN.CONFIG.EVENT)
				define qalias(SYSTEM.ADMIN.CONFIG.EVENT) target(SYSTEM.ADMIN.CONFIG.EVENT) targtype(TOPIC)
				
				define topic(SYSTEM.ADMIN.LOGGER.EVENT) type(LOCAL) topicstr(system/admin/events/logger)
				delete qlocal(SYSTEM.ADMIN.LOGGER.EVENT)
				define qalias(SYSTEM.ADMIN.LOGGER.EVENT) target(SYSTEM.ADMIN.LOGGER.EVENT) targtype(TOPIC)
				
				define topic(SYSTEM.ADMIN.PERFM.EVENT) type(LOCAL) topicstr(system/admin/events/perfm)
				delete qlocal(SYSTEM.ADMIN.PERFM.EVENT)
				define qalias(SYSTEM.ADMIN.PERFM.EVENT) target(SYSTEM.ADMIN.PERFM.EVENT) targtype(TOPIC)
				
				define topic(SYSTEM.ADMIN.QMGR.EVENT) type(LOCAL) topicstr(system/admin/events/qmgr)
				delete qlocal(SYSTEM.ADMIN.QMGR.EVENT)
				define qalias(SYSTEM.ADMIN.QMGR.EVENT) target(SYSTEM.ADMIN.QMGR.EVENT) targtype(TOPIC)
				
			To process specific event types:
				xmqdspev -m QM -t system/admin/events/qmgr -w
				
			To process all events:
				xmqdspev -m QM -t system/admin/events/# -w	
			
		If Wait Mode is used, use CTRL-C to stop the tool.

SAMPLE OUTPUT

C:\MQ>xmqdspev -m TEST -q SYSTEM.ADMIN.QMGR.EVENT
Xmqdspev v1.3 - Developed by Oliver Fisse (IBM)

Connected to queue manager 'TEST' (Platform=Windows, Level=701)
Processing EVENT queue 'SYSTEM.ADMIN.QMGR.EVENT'...

-------------------------------------------------------[01/22/2011-17:56:31]---
     ReasonCode: 2222
      EventName: Queue Manager Active - MQRC_Q_MGR_ACTIVE (2222, X'8AE')
    Description: This condition is detected when a queue manager becomes
                 active.
       QMgrName: TEST
-------------------------------------------------------[01/22/2011-17:57:19]---
     ReasonCode: 2085
      EventName: Unknown Object Name - MQRC_UNKNOWN_OBJECT_NAME (2085, X'825')
    Description: On an MQOPEN or MQPUT1 call, the ObjectQMgrName field in the
		         object descriptor MQOD is set to one of the following.
		         It is either:
			          - Blank
		              - The name of the local queue manager
		              - The name of a local definition of a remote queue manager
		               (a queue-manager alias) in which the RemoteQMgrName
		                attribute is the name of the local queue manager.
		         However, the ObjectName in the object descriptor is not
		         recognized for the specified object type.
       QMgrName: TEST
       ApplType: 11 (MQAT_WINDOWS_NT)
       ApplName: Q\TMAITM6\ekmqlocal_TEST.exe
          QName: KMQ.IRA.AGENT.QUEUE
		  
2 event message(s) processed.
2 event message(s) processed.

Disconnected from queue manager 'TEST'
Xmqdspev v1.2 ended.

C:\MQ>xmqdspev -m TEST -q SYSTEM.ADMIN.COMMAND.EVENT -r -w -d
Xmqdspev v1.3 - Developed by Oliver Fisse (IBM)

Connected to queue manager 'TEST' (Platform=Windows, Level=701)
Processing EVENT queue 'SYSTEM.ADMIN.COMMAND.EVENT'...

-------------------------------------------------------[01/22/2011-18:29:00]---
     ReasonCode: 2413
      EventName: Command - MQRC_COMMAND_PCF (2413, X'96D')
    Description: Command successfully issued.
    EventUserId: Administrato
    EventOrigin: 3 (MQEVO_MSG)
      EventQMgr: TEST
EventAccntToken: [B@399a399a
EventApplIdenty:
  EventApplType: 11 (MQAT_WINDOWS_NT)
  EventApplName: re MQ\java\jre\bin\javaw.exe
EventApplOrigin:
        Command: 2 (MQCMD_INQUIRE_Q_MGR)

**************************** PCF Command Dump Start ****************************
MQCFGR (com.ibm.mq.headers.internal.store.ByteStore [encoding: 0x00000111, ccsid
: 437, size: 44] @897987974)
        MQLONG Type: 20 (0x00000014)
        MQLONG StrucLength: 16 (0x00000010)
        MQLONG Parameter: 8002 (MQGACF_COMMAND_DATA)
        MQLONG ParameterCount: 1 (0x00000001) {
com.ibm.mq.pcf.MQCFIL:MQCFIL (com.ibm.mq.headers.internal.store.MQMessageStore [
encoding: 0x00000111, ccsid: 437])
        MQLONG Type: 5 (0x00000005)
        MQLONG StrucLength: 28 (0x0000001c)
        MQLONG Parameter: 1001 (MQIACF_FIRST/MQIACF_Q_MGR_ATTRS)
        MQLONG Count: 3 (0x00000003)
        MQLONG[] Values: {32, 2015, 2}} {
MQCFIL (com.ibm.mq.headers.internal.store.MQMessageStore [encoding: 0x00000111,
ccsid: 437])
        MQLONG Type: 5 (0x00000005)
        MQLONG StrucLength: 28 (0x0000001c)
        MQLONG Parameter: 1001 (MQIACF_FIRST/MQIACF_Q_MGR_ATTRS)
        MQLONG Count: 3 (0x00000003)
        MQLONG[] Values: {32, 2015, 2}}
**************************** PCF Command Dump End   ****************************
-------------------------------------------------------[01/22/2011-18:29:03]----

     ReasonCode: 2413
      EventName: Command - MQRC_COMMAND_PCF (2413, X'96D')
    Description: Command successfully issued.
    EventUserId: Administrato
    EventOrigin: 3 (MQEVO_MSG)
      EventQMgr: TEST
EventAccntToken: [B@77cd77cd
EventApplIdenty:
  EventApplType: 11 (MQAT_WINDOWS_NT)
  EventApplName: re MQ\java\jre\bin\javaw.exe
EventApplOrigin:
        Command: 41 (MQCMD_INQUIRE_Q_STATUS)

**************************** PCF Command Dump Start ****************************

MQCFGR (com.ibm.mq.headers.internal.store.ByteStore [encoding: 0x00000111, ccsid
: 437, size: 76] @1967813962)
        MQLONG Type: 20 (0x00000014)
        MQLONG StrucLength: 16 (0x00000010)
        MQLONG Parameter: 8002 (MQGACF_COMMAND_DATA)
        MQLONG ParameterCount: 3 (0x00000003) {
com.ibm.mq.pcf.MQCFST:MQCFST (com.ibm.mq.headers.internal.store.MQMessageStore [
encoding: 0x00000111, ccsid: 437])
        MQLONG Type: 4 (0x00000004)
        MQLONG StrucLength: 24 (0x00000018)
        MQLONG Parameter: 2016 (MQCA_Q_NAME)
        MQLONG CodedCharSetId: 437 (0x000001b5)
        MQLONG StringLength: 1 (0x00000001)
        MQCHAR[] String: "*"
com.ibm.mq.pcf.MQCFIN:MQCFIN (com.ibm.mq.headers.internal.store.MQMessageStore [
encoding: 0x00000111, ccsid: 437])
        MQLONG Type: 3 (0x00000003)
        MQLONG StrucLength: 16 (0x00000010)
        MQLONG Parameter: 1103 (MQIACF_Q_STATUS_TYPE)
        MQLONG Value: 1105 (0x00000451)
com.ibm.mq.pcf.MQCFIL:MQCFIL (com.ibm.mq.headers.internal.store.MQMessageStore [
encoding: 0x00000111, ccsid: 437])
        MQLONG Type: 5 (0x00000005)
        MQLONG StrucLength: 20 (0x00000014)
        MQLONG Parameter: 1026 (MQIACF_Q_STATUS_ATTRS)
        MQLONG Count: 1 (0x00000001)
        MQLONG[] Values: {1009}} {
MQCFST (com.ibm.mq.headers.internal.store.MQMessageStore [encoding: 0x00000111,
ccsid: 437])
        MQLONG Type: 4 (0x00000004)
        MQLONG StrucLength: 24 (0x00000018)
        MQLONG Parameter: 2016 (MQCA_Q_NAME)
        MQLONG CodedCharSetId: 437 (0x000001b5)
        MQLONG StringLength: 1 (0x00000001)
        MQCHAR[] String: "*"
MQCFIN (com.ibm.mq.headers.internal.store.MQMessageStore [encoding: 0x00000111,
ccsid: 437])
        MQLONG Type: 3 (0x00000003)
        MQLONG StrucLength: 16 (0x00000010)
        MQLONG Parameter: 1103 (MQIACF_Q_STATUS_TYPE)
        MQLONG Value: 1105 (0x00000451)
MQCFIL (com.ibm.mq.headers.internal.store.MQMessageStore [encoding: 0x00000111,
ccsid: 437])
        MQLONG Type: 5 (0x00000005)
        MQLONG StrucLength: 20 (0x00000014)
        MQLONG Parameter: 1026 (MQIACF_Q_STATUS_ATTRS)
        MQLONG Count: 1 (0x00000001)
        MQLONG[] Values: {1009}}
**************************** PCF Command Dump End   ***************************
-------------------------------------------------------[01/22/2011-18:29:03]---
     ReasonCode: 2413
      EventName: Command - MQRC_COMMAND_PCF (2413, X'96D')
    Description: Command successfully issued.
    EventUserId: Administrato
    EventOrigin: 3 (MQEVO_MSG)
      EventQMgr: TEST
EventAccntToken: [B@54f254f2
EventApplIdenty:
  EventApplType: 11 (MQAT_WINDOWS_NT)
  EventApplName: re MQ\java\jre\bin\javaw.exe
EventApplOrigin:
        Command: 70 (MQCMD_INQUIRE_CLUSTER_Q_MGR)

**************************** PCF Command Dump Start ***************************
MQCFGR (com.ibm.mq.headers.internal.store.ByteStore [encoding: 0x00000111, ccsid
: 437, size: 88] @1361269027)
        MQLONG Type: 20 (0x00000014)
        MQLONG StrucLength: 16 (0x00000010)
        MQLONG Parameter: 8002 (MQGACF_COMMAND_DATA)
        MQLONG ParameterCount: 3 (0x00000003) {
com.ibm.mq.pcf.MQCFST:MQCFST (com.ibm.mq.headers.internal.store.MQMessageStore [
encoding: 0x00000111, ccsid: 437])
        MQLONG Type: 4 (0x00000004)
        MQLONG StrucLength: 24 (0x00000018)
        MQLONG Parameter: 2031 (MQCA_CLUSTER_Q_MGR_NAME)
        MQLONG CodedCharSetId: 437 (0x000001b5)
        MQLONG StringLength: 1 (0x00000001)
        MQCHAR[] String: "*"
com.ibm.mq.pcf.MQCFIL:MQCFIL (com.ibm.mq.headers.internal.store.MQMessageStore [
encoding: 0x00000111, ccsid: 437])
        MQLONG Type: 5 (0x00000005)
        MQLONG StrucLength: 20 (0x00000014)
        MQLONG Parameter: 1093 (MQIACF_CLUSTER_Q_MGR_ATTRS)
        MQLONG Count: 1 (0x00000001)
        MQLONG[] Values: {1009}
com.ibm.mq.pcf.MQCFSF:MQCFSF (com.ibm.mq.headers.internal.store.MQMessageStore [
encoding: 0x00000111, ccsid: 437])
        MQLONG Type: 14 (0x0000000e)
        MQLONG StrucLength: 28 (0x0000001c)
        MQLONG Parameter: 2029 (MQCA_CLUSTER_NAME)
        MQLONG Operator: 18 (0x00000012)
        MQLONG CodedCharSetId: 437 (0x000001b5)
        MQLONG FilterValueLength: 1 (0x00000001)
        MQCHAR[] FilterValue: "*"} {
MQCFST (com.ibm.mq.headers.internal.store.MQMessageStore [encoding: 0x00000111,
ccsid: 437])
        MQLONG Type: 4 (0x00000004)
        MQLONG StrucLength: 24 (0x00000018)
        MQLONG Parameter: 2031 (MQCA_CLUSTER_Q_MGR_NAME)
        MQLONG CodedCharSetId: 437 (0x000001b5)
        MQLONG StringLength: 1 (0x00000001)
        MQCHAR[] String: "*"
MQCFIL (com.ibm.mq.headers.internal.store.MQMessageStore [encoding: 0x00000111,
ccsid: 437])
        MQLONG Type: 5 (0x00000005)
        MQLONG StrucLength: 20 (0x00000014)
        MQLONG Parameter: 1093 (MQIACF_CLUSTER_Q_MGR_ATTRS)
        MQLONG Count: 1 (0x00000001)
        MQLONG[] Values: {1009}
MQCFSF (com.ibm.mq.headers.internal.store.MQMessageStore [encoding: 0x00000111,
ccsid: 437])
        MQLONG Type: 14 (0x0000000e)
        MQLONG StrucLength: 28 (0x0000001c)
        MQLONG Parameter: 2029 (MQCA_CLUSTER_NAME)
        MQLONG Operator: 18 (0x00000012)
        MQLONG CodedCharSetId: 437 (0x000001b5)
        MQLONG FilterValueLength: 1 (0x00000001)
        MQCHAR[] FilterValue: "*"}
**************************** PCF Command Dump End   ***************************

Program termination requested. Shutting down...

3 event message(s) processed.
3 event message(s) dsiplayed.

Disconnected from queue manager 'TEST'
Xmqdspev v1.3 ended.
 
NOTES

	- Use CTRL-C to exit the Xmqdspev tool.
	- Supports z/OS queue managers as long as the queue manager supports PCF.

		


		
			
