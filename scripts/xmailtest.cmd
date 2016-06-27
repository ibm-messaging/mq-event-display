
@echo off

rem --------------------------------------------------------
rem  Execute Xmailtest - Test Mail Configuration File
rem
rem  (c) Copyright IBM Corp. 2011-2015, all rights reserved
rem --------------------------------------------------------

rem Set MQ_COMMAND_TOOLS to the path where you installed the com.ibm.xmq.events.jar JAR file and other required JAR files
set MQ_COMMAND_TOOLS=

rem Windows
java -cp "%MQ_COMMAND_TOOLS%\mail.jar;%MQ_COMMAND_TOOLS%\activation.jar;%MQ_COMMAND_TOOLS%\com.ibm.xmq.events.jar" com.ibm.xmq.events.Xmailtest %*


rem Required JAR files not shipped with this SupportPac
rem mail.jar in part of JavaMail - http://www.oracle.com/technetwork/java/javamail/index.html
rem activation.jar is part of JavaBeans Activation Framework (JAF) - http://www.oracle.com/technetwork/java/javase/index-jsp-136939.html
rem Note that activation.jar is only needed if the JVM is 1.5 or lower