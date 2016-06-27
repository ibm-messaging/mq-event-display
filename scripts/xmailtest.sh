#!/bin/sh

#---------------------------------------------------------#
# Execute Xmqdspev - Test Mail Configuration FIle         #
#                                                         #
# (c) Copyright IBM Corp. 2011-2015, all rights reserved. #
#---------------------------------------------------------#

# INSTRUCTIONS TO CUSTOMIZE THIS SCRIPT FOR YOUR ENVIRONMENT
# ----------------------------------------------------------
#
# 1. Set the MQ_COMMAND_TOOLS to the path where JAR file com.ibm.xmq.events.jar and other necessary JAR files are located


# Export MQ_COMMAND_TOOLS environment variable to the path where your install the com.ibm.xmq.events.jar JAR file and other required JAR files.
export MQ_COMMAND_TOOLS=

# All platforms
java -cp $MQ_COMMAND_TOOLS/mail.jar:$MQ_COMMAND_TOOLS/activation.jar:$MQ_COMMAND_TOOLS/com.ibm.xmq.events.jar com.ibm.xmq.events.Xmailtest $*


# Required JAR files not shipped with this SupportPac 
# mail.jar in part of JavaMail - http://www.oracle.com/technetwork/java/javamail/index.html
# activation.jar is part of JavaBeans Activation Framework (JAF) - http://www.oracle.com/technetwork/java/javase/index-jsp-136939.html
# Note that activation.jar is only needed if the JVM is 1.5 or lower