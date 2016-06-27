package com.ibm.xmq.events;

public class MQTMC2 {
	
	public String strucId;
	public String version;
	public String qName;
	public String processName;
	public String triggerData;
	public String applType;
	public String applId;
	public String envData;
	public String userData;
	public String qMgrName;
	
	public MQTMC2() {
		
		this.strucId = "TMC ";
		this.version = "   1";
		this.qName = "                                                ";
		this.processName = "                                                ";
		this.triggerData = "                                                                ";
		this.applType = "    ";
		this.applId = "                                                                                                                                                                                                                                                                ";
		this.envData = "                                                                                                                                ";
		this.userData = "                                                                                                                                ";
		this.qMgrName = "                                                ";
	} // end of constructor
	
	public MQTMC2(String struc) {
		
		this();
		this.strucId = struc.substring(0, 4);
		this.version = struc.substring(4, 8);
		this.qName = struc.substring(8, 56);
		this.processName = struc.substring(56, 104);
		this.triggerData = struc.substring(104, 168);
		this.applType = struc.substring(168, 172);
		this.applId = struc.substring(172, 428);
		this.envData = struc.substring(428, 556);
		this.userData = struc.substring(556, 684);
		this.qMgrName = struc.substring(684, 732);
	} // end of constructor
} // end of class MQTMC2
