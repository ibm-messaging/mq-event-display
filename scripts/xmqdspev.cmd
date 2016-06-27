@echo off

rem --------------------------------------------------------
rem  Execute Xmqdspev - Display IBM WebSphere MQ events
rem
rem  (c) Copyright IBM Corp. 2011-2015, all rights reserved
rem --------------------------------------------------------

rem INSTRUCTIONS TO CUSTOMIZE THIS SCRIPT FOR YOUR ENVIRONMENT
rem ----------------------------------------------------------
rem
rem 1. Set the MQ_COMMAND_TOOLS to the path where JAR file com.ibm.xmq.events.jar and other necessary JAR files are located
rem 2. Uncomment one of the java line based on your WMQ version and operating system
rem 3. Optionally, to use SSL add the -Djava.net.ssl.* options to the java command line
rem 4. To e-mail events, add JAR file mail.jar and if needed (JRE 1.5 and lower) JAR file activation.jar to the classpath

rem Set MQ_COMMAND_TOOLS to the path where you installed the com.ibm.xmq.events.jar JAR file and other required JAR files.
set MQ_COMMAND_TOOLS=

rem Windows & WMQ V6 (requires com.ibm.mq.pcf-6.1.jar which is shipped in SupportPac MS0B)
rem java -Djava.library.path="%MQ_JAVA_LIB_PATH%" -cp "%MQ_JAVA_LIB_PATH%\com.ibm.mq.jar";"%MQ_COMMAND_TOOLS%\com.ibm.mq.pcf-6.1.jar";"%MQ_COMMAND_TOOLS%\com.ibm.xmq.events.jar" com.ibm.xmq.events.Xmqdspev %*

rem Windows & WMQ V7, V7.1, V7.5 or V8.0
rem java -Djava.library.path="%MQ_JAVA_LIB_PATH%" -cp "%MQ_JAVA_LIB_PATH%\com.ibm.mq.jar";"%MQ_JAVA_LIB_PATH%\com.ibm.mq.pcf.jar";"%MQ_COMMAND_TOOLS%\com.ibm.xmq.events.jar" com.ibm.xmq.events.Xmqdspev %*

rem To use SSL update and add the following four options on the above java command lines.
rem     -Djavax.net.ssl.keyStore=<path and name of keystore>
rem     -Djavax.net.ssl.keyStorePassword=<keystore password>
rem     -Djavax.net.ssl.trustStore=<path and name of trustore>
rem     -Djavax.net.ssl.trustStorePassword=<trustore password>

rem Required JAR files not shipped with this SupportPac
rem mail.jar in part of JavaMail - http://www.oracle.com/technetwork/java/javamail/index.html
rem activation.jar is part of JavaBeans Activation Framework (JAF) - http://www.oracle.com/technetwork/java/javase/index-jsp-136939.html
rem Note that activation.jar is only needed if the JVM is 1.5 or lower
rem To e-mail events add JAR files mail.jar and activation.jar to the classpath
rem     -cp "%MQ_COMMAND_TOOLS%\mail.jar";"%MQ_COMMAND_TOOLS%\activation.jar"