#!/bin/sh

#---------------------------------------------------------#
# Execute Xmqdspev - Display IBM WebSphere MQ events      #
#                                                         #
# (c) Copyright IBM Corp. 2011-2015, all rights reserved. #
#---------------------------------------------------------#

# INSTRUCTIONS TO CUSTOMIZE THIS SCRIPT FOR YOUR ENVIRONMENT
# ----------------------------------------------------------
#
# 1. Set the MQ_COMMAND_TOOLS to the path where JAR file com.ibm.xmq.events.jar and other necessary JAR files are located
# 2. Uncomment one of the java line based on your WMQ version and operating system
# 3. Optionally, to use SSL add the -Djava.net.ssl.* options to the java command line
# 4. To e-mail events, add JAR file mail.jar and if needed (JRE 1.5 and lower) JAR file activation.jar to the classpath

# Export MQ_COMMAND_TOOLS environment variable to the path where your install the com.ibm.xmq.events.jar JAR file and other required JAR files.
export MQ_COMMAND_TOOLS=

# AIX & WMQ V6 (requires com.ibm.mq.pcf-6.1.jar which is shipped in SupportPac MS0B). If using a 64-bits JVM update /usr/mqm/java/lib to /usr/mqm/java/lib64.
#java -Djava.library.path=/usr/mqm/java/lib -cp /usr/mqm/java/lib/com.ibm.mq.jar:$MQ_COMMAND_TOOLS/com.ibm.mq.pcf-6.1.jar:$MQ_COMMAND_TOOLS/com.ibm.xmq.events.jar com.ibm.xmq.events.Xmqdspev $*

# AIX & WMQ V7, V7.1, V7.5 or V8.0. If using a 64-bits JVM update /usr/mqm/java/lib to /usr/mqm/java/lib64.
#java -Djava.library.path=/usr/mqm/java/lib -cp /usr/mqm/java/lib/com.ibm.mq.jar:/usr/mqm/java/lib/com.ibm.mq.pcf.jar:$MQ_COMMAND_TOOLS/com.ibm.xmq.events.jar com.ibm.xmq.events.Xmqdspev $*

# Linux, other UNIX & WMQ V6 (requires com.ibm.mq.pcf-6.1.jar which is shipped in SupportPac MS0B). If using a 64-bits JVM update /opt/mqm/java/lib to /opt/mqm/java/lib64.
#java -Djava.library.path=/opt/mqm/java/lib -cp /opt/mqm/java/lib/com.ibm.mq.jar:$MQ_COMMAND_TOOLS/com.ibm.mq.pcf-6.1.jar:$MQ_COMMAND_TOOLS/com.ibm.xmq.events.jar com.ibm.xmq.events.Xmqdspev $*

# Linux, other UNIX & WMQ V7, V7.1, V7.5 or V8.0. If using a 64-bits JVM update /opt/mqm/java/lib to /opt/mqm/java/lib64.
#java -Djava.library.path=/opt/mqm/java/lib -cp /opt/mqm/java/lib/com.ibm.mq.jar:/opt/mqm/java/lib/com.ibm.mq.pcf.jar:$MQ_COMMAND_TOOLS/com.ibm.xmq.events.jar com.ibm.xmq.events.Xmqdspev $*

# IBM iSeries & WMQ V6 requires com.ibm.mq.pcf-6.1.jar which is shipped in SupportPac MS0B). If using a 64-bits JVM update /qibm/proddata/mqm/java/lib to /qibm/proddata/mqm/java/lib64.
#java -Djava.library.path=/qibm/proddata/mqm/java/lib -cp /qibm/proddata/mqm/java/lib/com.ibm.mq.jar:/qibm/proddata/mqm/java/lib/com.ibm.mq.pcf-6.1.jar:$MQ_COMMAND_TOOLS/com.ibm.xmq.events.jar com.ibm.xmq.events.Xmqdspev $*

# IBM iSeries & WMQ V7 or V7.1. If using a 64-bits JVM update /qibm/proddata/mqm/java/lib to /qibm/proddata/mqm/java/lib64.
#java -Djava.library.path=/qibm/proddata/mqm/java/lib -cp /qibm/proddata/mqm/java/lib/com.ibm.mq.jar:/qibm/proddata/mqm/java/lib/com.ibm.mq.pcf.jar:$MQ_COMMAND_TOOLS/com.ibm.xmq.events.jar com.ibm.xmq.events.Xmqdspev $*


# To use SSL update and add the following four options on the above java command lines.
#     -Djavax.net.ssl.keyStore=<path and name of keystore>
#     -Djavax.net.ssl.keyStorePassword=<keystore password>
#     -Djavax.net.ssl.trustStore=<path and name of trustore>
#     -Djavax.net.ssl.trustStorePassword=<trustore password>

# Required JAR files not shipped with this SupportPac 
# mail.jar in part of JavaMail - http://www.oracle.com/technetwork/java/javamail/index.html
# activation.jar is part of JavaBeans Activation Framework (JAF) - http://www.oracle.com/technetwork/java/javase/index-jsp-136939.html
# Note that activation.jar is only needed if the JVM is 1.5 or lower
# To e-mail events add JAR files mail.jar and activation.jar to the classpath
#     -cp $MQ_COMMAND_TOOLS/mail.jar:$MQ_COMMAND_TOOLS/activation.jar