package com.ibm.xmq.events;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;

import com.ibm.mq.MQEnvironment;


/**
 * XMQ utilities class
 * 
 * @author <a href="mailto:fisse@us.ibm.com">Oliver Fisse</a>
 *
 */
public final class XMQUtils {
	
	/*public static String getAttributeName(int attributeCode) {
		
		String an = null;
		
		switch (attributeCode) {
			case 1:     // MQIA_APPL_TYPE / MQIA_FIRST    
				an = "?()";
				break;
			case 2:     // MQIA_CODED_CHAR_SET_ID    
				an = "CCSID()";
				break;
			case 3:     // MQIA_CURRENT_Q_DEPTH     
				an = "CURDEPTH()";
				break;
			case 4:     // MQIA_DEF_INPUT_OPEN_OPTION     
				an = "DEFSOPT()";
				break;
			case 5:     // MQIA_DEF_PERSISTENCE     
				an = "DEFPSIST()";
				break;
			case 6:     // MQIA_DEF_PRIORITY     
				an = "DEFPRTY()";
				break;
			case 7:     // MQIA_DEFINITION_TYPE     
				an = "TYPE()";
				break;
			case 8:     // MQIA_HARDEN_GET_BACKOUT    
				an = "HARDENBO";
				break;
			case 9:     // MQIA_INHIBIT_GET   
				an = "GET()";
				break;
			case 10:    // MQIA_INHIBIT_PUT   
				an = "PUT()";
				break;
			case 11:    // MQIA_MAX_HANDLES   
				an = "MAXHANDS()";
				break;
			case 12:    // MQIA_USAGE  
				an = "USAGE()";
				break;
			case 13:    // MQIA_MAX_MSG_LENGTH   
				an = "MAXMSGL()";
				break;
			case 14:    // MQIA_MAX_PRIORITY   
				an = "MAXPRTY()";
				break;
			case 15:    // MQIA_MAX_Q_DEPTH  
				an = "MAXDEPTH()";
				break;
			case 16:    // MQIA_MSG_DELIVERY_SEQUENCE  
				an = "MSGDLVSQ()";
				break;
			case 17:    // MQIA_OPEN_INPUT_COUNT  
				an = "IPPROCS()";
				break;
			case 18:    // MQIA_OPEN_OUTPUT_COUNT  
				an = "OPPROCS()";
				break;
			case 19:    // MQIA_NAME_COUNT
				an = "?()";
				break;
			case 20:    // MQIA_Q_TYPE
				an = "TYPE()";
				break;
			case 21:    // MQIA_RETENTION_INTERVAL
				an = "RETINTVL()";
				break;
			case 22:    // MQIA_BACKOUT_THRESHOLD
				an = "BOTHRESH()";
				break;
			case 23:    // MQIA_SHAREABILITY
				an = "SHARE OR NOSHARE";
				break;
			case 24:    // MQIA_TRIGGER_CONTROL
				an = "TRIGGER OR NOTRIGGER";
				break;
			case 25:    // MQIA_TRIGGER_INTERVAL
				an = "?()";
				break;
			case 26:    // MQIA_TRIGGER_MSG_PRIORITY
				an = "TRIGMPRI()";
				break;
			case 27:    // MQIA_CPI_LEVEL
				an = "?()";
				break;
			case 28:    // MQIA_TRIGGER_TYPE
				an = "TRIGTYPE()";
				break;
			case 29:    // MQIA_TRIGGER_DEPTH
				an = "TRIGDPTH()";
				break;
			case 30:    // MQIA_CPI_LEVEL
				an = "SYNCPT";
				break;
			case 31:    // MQIA_COMMAND_LEVEL 
				an = "CMDLEVEL()";
				break;
			case 32:    // MQIA_PLATFORM 
				an = "PLATFORM()";
				break;
			case 33:    // MQIA_MAX_UNCOMMITTED_MSGS  
				an = "MAXUMSGS()";
				break;
			case 34:    // MQIA_DIST_LISTS 
				an = "DISTL()";
				break;
			case 35:    // MQIA_TIME_SINCE_RESET
				an = "?()";
				break;
			case 36:    // MQIA_HIGH_Q_DEPTH
				an = "?()";
				break;
			case 37:    // MQIA_MSG_ENQ_COUNT
				an = "?()";
				break;
			case 38:    // MQIA_MSG_DEQ_COUNT
				an = "?()";
				break;
			case 39:    // MQIA_EXPIRY_INTERVAL
				an = "?()";
				break;
			case 40:    // MQIA_Q_DEPTH_HIGH_LIMIT
				an = "QDEPTHHI()";
				break;
			case 41:    // MQIA_Q_DEPTH_LOW_LIMIT
				an = "QDEPTHLO()";
				break;
			case 42:    // MQIA_Q_DEPTH_MAX_EVENT
				an = "QDPMAXEV()";
				break;
			case 43:    // MQIA_Q_DEPTH_HIGH_EVENT
				an = "QDPHIEV()";
				break;
			case 44:    // MQIA_Q_DEPTH_LOW_EVENT
				an = "QDPLOEV()";
				break;
			case 45:    // MQIA_SCOPE
				an = "SCOPE()";
				break;
			case 46:    // MQIA_Q_SERVICE_INTERVAL_EVENT
				an = "?()";
				break;
			case 47:    // MQIA_AUTHORITY_EVENT
				an = "AUTHOREV()";
				break;
			case 48:    // MQIA_INHIBIT_EVENT
				an = "INHIBTEV()";
				break;
			case 49:    // MQIA_LOCAL_EVENT
				an = "LOCALEV()";
				break;
			case 50:    // MQIA_REMOTE_EVENT
				an = "REMOTEEV()";
				break;
			case 51:    // MQIA_CONFIGURATION_EVENT
				an = "CONFIGEV()";
				break;
			case 52:    // MQIA_START_STOP_EVENT
				an = "STRSTPEV()";
				break;
			case 53:    // MQIA_PERFORMANCE_EVENT 
				an = "PERFMEV()";
				break;
			case 54:    // MQIA_Q_SERVICE_INTERVAL
				an = "?()";
				break;
			case 55:    // MQIA_CHANNEL_AUTO_DEF
				an = "CHAD()";
				break;
			case 56:    // MQIA_CHANNEL_AUTO_DEF_EVENT
				an = "CHADEV()";
				break;
			case 57:    // MQIA_INDEX_TYPE
				an = "INDXTYPE()";
				break;
			case 58:    // MQIA_CLUSTER_WORKLOAD_LENGTH
				an = "CLWLLEN()";
				break;
			case 59:    // MQIA_CLUSTER_Q_TYPE
				an = "?()";
				break;
			case 60:    // MQIA_ARCHIVE
				an = "?()";
				break;
			case 61:    // MQIA_DEF_BIND
				an = "DEFBIND()";
				break;
			case 62:    // MQIA_PAGESET_ID
				an = "?()";
				break;
			case 63:    // MQIA_QSG_DISP
				an = "?()";
				break;
			case 64:    // MQIA_INTRA_GROUP_QUEUING
				an = "?()";
				break;
			case 65:    // MQIA_IGQ_PUT_AUTHORITY
				an = "?()";
				break;
			case 66:    // MQIA_AUTH_INFO_TYPE
				an = "?()";
				break;
			case 68:    // MQIA_MSG_MARK_BROWSE_INTERVAL
				an = "?()";
				break;
			case 69:    // MQIA_SSL_TASKS
				an = "?()";
				break;
			case 70:    // MQIA_CF_LEVEL
				an = "?()";
				break;
			case 71:    // MQIA_CF_RECOVER
				an = "?()";
				break;
			case 72:    // MQIA_NAMELIST_TYPE
				an = "?()";
				break;
			case 73:    // MQIA_CHANNEL_EVENT
				an = "CHLEV()";
				break;
			case 74:    // MQIA_BRIDGE_EVENT
				an = "?()";
				break;
			case 75:    // MQIA_SSL_EVENT
				an = "SSLEV()";
				break;
			case 76:    // MQIA_SSL_RESET_COUNT
				an = "?()";
				break;
			case 77:    // MQIA_SHARED_Q_Q_MGR_NAME
				an = "?()";
				break;
			case 78:    // MQIA_NPM_CLASS
				an = "NPMCLASS()";
				break;
			case 80:    // MQIA_MAX_OPEN_Q
				an = "?()";
				break;
			case 81:    // MQIA_MONITOR_INTERVAL
				an = "?()";
				break;
			case 82:    // MQIA_Q_USERS
				an = "?()";
				break;
			case 83:    // MQIA_MAX_GLOBAL_LOCKS
				an = "?()";
				break;
			case 84:    // MQIA_MAX_LOCAL_LOCKS
				an = "?()";
				break;
			case 85:    // MQIA_LISTENER_PORT_NUMBER
				an = "?()";
				break;
			case 86:    // MQIA_BATCH_INTERFACE_AUTO
				an = "?()";
				break;
			case 87:    // MQIA_CMD_SERVER_AUTO
				an = "?()";
				break;
			case 88:    // MQIA_CMD_SERVER_CONVERT_MSG
				an = "?()";
				break;
			case 89:    // MQIA_CMD_SERVER_DLQ_MSG
				an = "?()";
				break;
			case 90:    // MQIA_MAX_Q_TRIGGERS
				an = "?()";
				break;
			case 91:    // MQIA_TRIGGER_RESTART 
				an = "?()";
				break;
			case 92:    // MQIA_SSL_FIPS_REQUIRED
				an = "?()";
				break;
			case 93:    // MQIA_IP_ADDRESS_VERSION
				an = "?()";
				break;
			case 94:    // MQIA_LOGGER_EVENT
				an = "LOGGEREV()";
				break;
			case 95:    // MQIA_CLWL_Q_RANK
				an = "?()";
				break;
			case 96:    // MQIA_CLWL_Q_PRIORITY
				an = "?()";
				break;
			case 97:    // MQIA_CLWL_MRU_CHANNELS
				an = "CLWLMRUC()";
				break;
			case 98:    // MQIA_CLWL_USEQ
				an = "CLWLUSEQ()";
				break;
			case 99:    // MQIA_COMMAND_EVENT
				an = "CMDEV()";
				break;
			case 100:   // MQIA_ACTIVE_CHANNELS
				an = "?()";
				break;
			case 101:   // MQIA_CHINIT_ADAPTERS
				an = "?()";
				break;
			case 102:   // MQIA_ADOPTNEWMCA_CHECK
				an = "?()";
				break;
			case 103:   // MQIA_ADOPTNEWMCA_TYPE
				an = "?()";
				break;
			case 104:   // MQIA_ADOPTNEWMCA_INTERVAL
				an = "?()";
				break;
			case 105:   // MQIA_CHINIT_DISPATCHERS
				an = "?()";
				break;
			case 106:   // MQIA_DNS_WLM
				an = "?()";
				break;
			case 107:   // MQIA_LISTENER_TIMER
				an = "?()";
				break;
			case 108:   // MQIA_LU62_CHANNELS
				an = "?()";
				break;
			case 109:   // MQIA_MAX_CHANNELS
				an = "?()";
				break;
			case 110:   // MQIA_OUTBOUND_PORT_MIN
				an = "?()";
				break;
			case 111:   // MQIA_RECEIVE_TIMEOUT
				an = "?()";
				break;
			case 112:   // MQIA_RECEIVE_TIMEOUT_TYPE
				an = "?()";
				break;
			case 113:   // MQIA_RECEIVE_TIMEOUT_MIN
				an = "?()";
				break;
			case 114:   // MQIA_TCP_CHANNELS
				an = "?()";
				break;
			case 115:   // MQIA_TCP_KEEP_ALIVE
				an = "?()";
				break;
			case 116:   // MQIA_TCP_STACK_TYPE
				an = "?()";
				break;
			case 117:   // MQIA_CHINIT_TRACE_AUTO_START
				an = "?()";
				break;
			case 118:   // MQIA_CHINIT_TRACE_TABLE_SIZE 
				an = "?()";
				break;
			case 119:   // MQIA_CHINIT_CONTROL
				an = "?()";
				break;
			case 120:   // MQIA_CMD_SERVER_CONTROL
				an = "?()";
				break;
			case 121:   // MQIA_SERVICE_TYPE
				an = "?()";
				break;
			case 122:   // MQIA_MONITORING_CHANNEL 
				an = "?()";
				break;
			case 123:   // MQIA_MONITORING_Q 
				an = "?()";
				break;
			case 124:   // MQIA_MONITORING_AUTO_CLUSSDR
				an = "?()";
				break;
			case 127:   // MQIA_STATISTICS_MQI
				an = "?()";
				break;
			case 128:   // MQIA_STATISTICS_Q
				an = "?()";
				break;
			case 129:   // MQIA_STATISTICS_CHANNEL
				an = "?()";
				break;
			case 130:   // MQIA_STATISTICS_AUTO_CLUSSDR
				an = "?()";
				break;
			case 131:   // MQIA_STATISTICS_INTERVAL
				an = "?()";
				break;
			case 133:   // MQIA_ACCOUNTING_MQI
				an = "ACCTMQI()";
				break;
			case 134:   // MQIA_ACCOUNTING_Q 
				an = "ACCTQ()";
				break;
			case 135:   // MQIA_ACCOUNTING_INTERVAL
				an = "ACCTINT()";
				break;
			case 136:   // MQIA_ACCOUNTING_CONN_OVERRIDE
				an = "ACCTCONO()";
				break;
			case 137:   // MQIA_TRACE_ROUTE_RECORDING
				an = "?()";
				break;
			case 138:   // MQIA_ACTIVITY_RECORDING
				an = "ACTIVREC()";
				break;
			case 139:   // MQIA_SERVICE_CONTROL
				an = "?()";
				break;
			case 140:   // MQIA_OUTBOUND_PORT_MAX
				an = "?()";
				break;
			case 141:   // MQIA_SECURITY_CASE
				an = "?()";
				break;
			case 150:   // MQIA_QMOPT_CSMT_ON_ERROR
				an = "?()";
				break;
			case 151:   // MQIA_QMOPT_CONS_INFO_MSGS
				an = "?()";
				break;
			case 152:   // MQIA_QMOPT_CONS_WARNING_MSGS
				an = "?()";
				break;
			case 153:   // MQIA_QMOPT_CONS_ERROR_MSGS
				an = "?()";
				break;
			case 154:   // MQIA_QMOPT_CONS_COMMS_MSGS
				an = "?()";
				break;
			case 155:   // MQIA_QMOPT_CONS_COMMS_MSGS
				an = "?()";
				break;
			case 156:   // MQIA_QMOPT_CONS_REORG_MSGS
				an = "?()";
				break;
			case 157:   // MQIA_QMOPT_CONS_SYSTEM_MSGS
				an = "?()";
				break;
			case 158:   // MQIA_QMOPT_LOG_INFO_MSGS
				an = "?()";
				break;
			case 159:   // MQIA_QMOPT_LOG_WARNING_MSGS
				an = "?()";
				break;
			case 160:   // MQIA_QMOPT_LOG_ERROR_MSGS 
				an = "?()";
				break;
			case 161:   // MQIA_QMOPT_LOG_CRITICAL_MSGS 
				an = "?()";
				break;
			case 162:   // MQIA_QMOPT_LOG_COMMS_MSGS 
				an = "?()";
				break;
			case 163:   // MQIA_QMOPT_LOG_REORG_MSGS 
				an = "?()";
				break;
			case 164:   // MQIA_QMOPT_LOG_SYSTEM_MSGS 
				an = "?()";
				break;
			case 165:   // MQIA_QMOPT_TRACE_MQI_CALLS 
				an = "?()";
				break;
			case 166:   // MQIA_QMOPT_TRACE_COMMS 
				an = "?()";
				break;
			case 167:   // MQIA_QMOPT_TRACE_REORG 
				an = "?()";
				break;
			case 168:   // MQIA_QMOPT_TRACE_CONVERSION 
				an = "?()";
				break;
			case 169:   // MQIA_QMOPT_TRACE_SYSTEM 
				an = "?()";
				break;
			case 170:   // MQIA_QMOPT_INTERNAL_DUMP
				an = "?()";
				break;
			case 171:   // MQIA_MAX_RECOVERY_TASKS
				an = "?()";
				break;
			case 172:   // MQIA_MAX_CLIENTS 
				an = "?()";
				break;
			case 173:   // MQIA_AUTO_REORGANIZATION 
				an = "?()";
				break;
			case 174:   // MQIA_AUTO_REORG_INTERVAL
				an = "?()";
				break;
			case 175:   // MQIA_DURABLE_SUB
				an = "?()";
				break;
			case 176:   // MQIA_MULTICAST
				an = "?()";
				break;
			case 181:   // MQIA_INHIBIT_PUB
				an = "?()";
				break;
			case 182:   // MQIA_INHIBIT_SUB
				an = "?()";
				break;
			case 183:   // MQIA_TREE_LIFE_TIME
				an = "?()";
				break;
			case 184:   // MQIA_DEF_PUT_RESPONSE_TYPE
				an = "DEFPRESP()";
				break;
			case 185:   // MQIA_TOPIC_DEF_PERSISTENCE
				an = "?()";
				break;
			case 186:   // MQIA_MASTER_ADMIN
				an = "?()";
				break;
			case 187:   // MQIA_PUBSUB_MODE
				an = "PSMODE()";
				break;
			case 188:   // MQIA_DEF_READ_AHEAD
				an = "DEFREADA()";
				break;
			case 189:   // MQIA_READ_AHEAD
				an = "?()";
				break;
			case 190:   // MQIA_PROPERTY_CONTROL
				an = "?()";
				break;
			case 192:   // MQIA_MAX_PROPERTIES_LENGTH
				an = "?()";
				break;
			case 193:   // MQIA_BASE_TYPE 
				an = "?()";
				break;
			case 195:   // MQIA_PM_DELIVERY
				an = "?()";
				break;
			case 196:   // MQIA_NPM_DELIVERY 
				an = "?()";
				break;
			case 199:   // MQIA_PROXY_SUB 
				an = "?()";
				break;
			case 203:   // MQIA_PUBSUB_NP_MSG 
				an = "?()";
				break;	
			case 204:   // MQIA_SUB_COUNT
				an = "?()";
				break;	
			case 205:   // MQIA_PUBSUB_NP_RESP 
				an = "?()";
				break;	
			case 206:   // MQIA_PUBSUB_MAXMSG_RETRY_COUNT 
				an = "?()";
				break;
			case 207:   // MQIA_PUBSUB_SYNC_PT 
				an = "?()";
				break;
			case 208:   // MQIA_TOPIC_TYPE 
				an = "?()";
				break;
			case 215:   // MQIA_PUB_COUNT 
				an = "?()";
				break;
			case 216:   // MQIA_WILDCARD_OPERATION
				an = "?()";
				break;
			case 218:   // MQIA_SUB_SCOPE
				an = "?()";
				break;
			case 219:   // MQIA_PUB_SCOPE
				an = "?()";
				break;
			case 221:   // MQIA_GROUP_UR
				an = "?()";
				break;
			case 222:   // MQIA_UR_DISP
				an = "?()";
				break;
			case 223:   // MQIA_COMM_INFO_TYPE
				an = "?()";
				break;
			case 224:   // MQIA_CF_OFFLOAD
				an = "?()";
				break;
			case 225:   // MQIA_CF_OFFLOAD_THRESHOLD1
				an = "?()";
				break;
			case 226:   // MQIA_CF_OFFLOAD_THRESHOLD2
				an = "?()";
				break;
			case 227:   // MQIA_CF_OFFLOAD_THRESHOLD3
				an = "?()";
				break;
			case 228:   // MQIA_CF_SMDS_BUFFERS
				an = "?()";
				break;
			case 229:   // MQIA_CF_OFFLDUSE
				an = "?()";
				break;
			case 230:   // MQIA_MAX_RESPONSES
				an = "?()";
				break;
			case 231:   // MQIA_RESPONSE_RESTART_POINT
				an = "?()";
				break;
			case 232:   // MQIA_COMM_EVENT
				an = "?()";
				break;
			case 233:   // MQIA_MCAST_BRIDGE
				an = "?()";
				break;
			case 234:   // MQIA_USE_DEAD_LETTER_Q
				an = "?()";
				break;
			case 235:   // MQIA_TOLERATE_UNPROTECTED
				an = "?()";
				break;
			case 236:   // MQIA_SIGNATURE_ALGORITHM
				an = "?()";
				break;
			case 237:   // MQIA_ENCRYPTION_ALGORITHM
				an = "?()";
				break;
			case 238:   // MQIA_POLICY_VERSION
				an = "?()";
				break;
			case 239:   // MQIA_ACTIVITY_CONN_OVERRIDE
				an = "ACTVCONO()";
				break;
			case 240:   // MQIA_ACTIVITY_TRACE 
				an = "?()";
				break;
			case 242:   // MQIA_SUB_CONFIGURATION_EVENT 
				an = "?()";
				break;
			case 243:   // MQIA_XR_CAPABILITY 
				an = "?()";
				break;
			case 244:   // MQIA_CF_RECAUTO
				an = "?()";
				break;
			case 245:   // MQIA_QMGR_CFCONLOS
				an = "?()";
				break;
			case 246:   // MQIA_CF_CFCONLOS
				an = "?()";
				break;
			case 247:   // MQIA_SUITE_B_STRENGTH
				an = "?()";
				break;
			case 248:   // MQIA_CHLAUTH_RECORDS 
				an = "CHLAUTH()";
				break;
			case 249:   // MQIA_PUBSUB_CLUSTER
				an = "?()";
				break;
			case 250:   // MQIA_DEF_CLUSTER_XMIT_Q_TYPE
				an = "DEFCLXQ()";
				break;
			case 251:   // MQIA_PROT_POLICY_CAPABILITY
				an = "?()";
				break;
			case 252:   // MQIA_CERT_VAL_POLICY / MQIA_LAST_USED
				an = "CERTVPOL()";
				break;
            case 1001:  // MQIACF_Q_MGR_ATTRS / MQIACF_FIRST
                an = "?()";
                break;
            case 1002:  // MQIACF_Q_ATTRS
                an = "?()";
                break;
            case 1003:  // MQIACF_PROCESS_ATTRS
                an = "?()";
                break;
            case 1004:  // MQIACF_NAMELIST_ATTRS
                an = "?()";
                break;
            case 1005:  // MQIACF_FORCE
                an = "?()";
                break;
            case 1006:  // MQIACF_REPLACE
                an = "?()";
                break;
            case 1007:  // MQIACF_PURGE
                an = "?()";
                break;           
            case 1008:  // MQIACF_MODE / MQIACF_QUIESCE
                an = "?()";
                break;
            case 1009:  // MQIACF_ALL
                an = "?()";
                break;
            case 1010:  // MQIACF_EVENT_APPL_TYPE
                an = "?()";
                break;
            case 1011:  // MQIACF_EVENT_ORIGIN
                an = "?()";
                break;
            case 1012:  // MQIACF_PARAMETER_ID
                an = "?()";
                break;
            case 1013:  // MQIACF_ERROR_IDENTIFIER / MQIACF_ERROR_ID
                an = "?()";
                break;
            case 1014:  // MQIACF_SELECTOR
                an = "?()";
                break;
            case 1015:  // MQIACF_CHANNEL_ATTRS
                an = "?()";
                break;
            case 1016:  // MQIACF_OBJECT_TYPE
                an = "?()";
                break;
            case 1017:  // MQIACF_ESCAPE_TYPE
                an = "?()";
                break;
            case 1018:  // MQIACF_ERROR_OFFSET
                an = "?()";
                break;
            case 1019:  // MQIACF_AUTH_INFO_ATTRS
                an = "?()";
                break;
            case 1020:  // MQIACF_REASON_QUALIFIER
                an = "?()";
                break;
            case 1021:  // MQIACF_COMMAND
                an = "?()";
                break;
            case 1022:  // MQIACF_OPEN_OPTIONS
                an = "?()";
                break;
            case 1023:  // MQIACF_OPEN_TYPE
                an = "?()";
                break;
            case 1024:  // MQIACF_PROCESS_ID
                an = "?()";
                break;
            case 1025:  // MQIACF_THREAD_ID
                an = "?()";
                break;
            case 1026:  // MQIACF_Q_STATUS_ATTRS
                an = "?()";
                break;
            case 1027:  // MQIACF_UNCOMMITTED_MSGS
                an = "?()";
                break;
            case 1028:  // MQIACF_HANDLE_STATE
                an = "?()";
                break;
            case 1070:  // MQIACF_AUX_ERROR_DATA_INT_1
                an = "?()";
                break;
            case 1071:  // MQIACF_AUX_ERROR_DATA_INT_2
                an = "?()";
                break;
            case 1072:  // MQIACF_CONV_REASON_CODE
                an = "?()";
                break;
            case 1073:  // MQIACF_BRIDGE_TYPE
                an = "?()";
                break;
            case 1074:  // MQIACF_INQUIRY
                an = "?()";
                break;
            case 1075:  // MQIACF_WAIT_INTERVAL
                an = "?()";
                break;
            case 1076:  // MQIACF_OPTIONS
                an = "?()";
                break;
            case 1077:  // MQIACF_BROKER_OPTIONS
                an = "?()";
                break;
            case 1078:  // MQIACF_REFRESH_TYPE
                an = "?()";
                break;
            case 1079:  // MQIACF_SEQUENCE_NUMBER
                an = "?()";
                break;
            case 1080:  // MQIACF_INTEGER_DATA
                an = "?()";
                break;
            case 1081:  // MQIACF_REGISTRATION_OPTIONS
                an = "?()";
                break;
            case 1082:  // MQIACF_PUBLICATION_OPTIONS
                an = "?()";
                break;
            case 1083:  // MQIACF_CLUSTER_INFO
                an = "?()";
                break;
            case 1084:  // MQIACF_Q_MGR_DEFINITION_TYPE
                an = "?()";
                break;
            case 1085:  // MQIACF_Q_MGR_TYPE
                an = "?()";
                break;
            case 1086:  // MQIACF_ACTION
                an = "?()";
                break;
            case 1087:  // MQIACF_SUSPEND
                an = "?()";
                break;
            case 1088:  // MQIACF_BROKER_COUNT
                an = "?()";
                break;
            case 1089:  // MQIACF_APPL_COUNT
                an = "?()";
                break;
            case 1090:  // MQIACF_ANONYMOUS_COUNT
                an = "?()";
                break;
            case 1091:  // MQIACF_REG_REG_OPTIONS
                an = "?()";
                break;
            case 1092:  // MQIACF_DELETE_OPTIONS
                an = "?()";
                break;
            case 1093:  // MQIACF_CLUSTER_Q_MGR_ATTRS
                an = "?()";
                break;
            case 1094:  // MQIACF_REFRESH_INTERVAL
                an = "?()";
                break;
            case 1095:  // MQIACF_REFRESH_REPOSITORY
                an = "?()";
                break;
            case 1096:  // MQIACF_REMOVE_QUEUES
                an = "?()";
                break;
            case 1098:  // MQIACF_OPEN_INPUT_TYPE
                an = "?()";
                break;
            case 1099:  // MQIACF_OPEN_OUTPUT
                an = "?()";
                break;
            case 1100:  // MQIACF_OPEN_SET
                an = "?()";
                break;
            case 1101:  // MQIACF_OPEN_INQUIRE
                an = "?()";
                break;
            case 1102:  // MQIACF_OPEN_BROWSE
                an = "?()";
                break;
            case 1103:  // MQIACF_Q_STATUS_TYPE
                an = "?()";
                break;
            case 1104:  // MQIACF_Q_HANDLE
                an = "?()";
                break;
            case 1105:  // MQIACF_Q_STATUS
                an = "?()";
                break;
            case 1106:  // MQIACF_SECURITY_TYPE
                an = "?()";
                break;
            case 1107:  // MQIACF_CONNECTION_ATTRS
                an = "?()";
                break;
            case 1108:  // MQIACF_CONNECT_OPTIONS
                an = "?()";
                break;
            case 1110:  // MQIACF_CONN_INFO_TYPE
                an = "?()";
                break;
            case 1111:  // MQIACF_CONN_INFO_CONN
                an = "?()";
                break;
            case 1112:  // MQIACF_CONN_INFO_HANDLE
                an = "?()";
                break;
            case 1113:  // MQIACF_CONN_INFO_ALL
                an = "?()";
                break;
            case 1114:  // MQIACF_AUTH_PROFILE_ATTRS
                an = "?()";
                break;
            case 1115:  // MQIACF_AUTHORIZATION_LIST
                an = "?()";
                break;
            case 1116:  // MQIACF_AUTH_ADD_AUTHS
                an = "?()";
                break;
            case 1117:  // MQIACF_AUTH_REMOVE_AUTHS
                an = "?()";
                break;
            case 1118:  // MQIACF_ENTITY_TYPE
                an = "?()";
                break;
            case 1120:  // MQIACF_COMMAND_INFO
                an = "?()";
                break;
            case 1121:  // MQIACF_CMDSCOPE_Q_MGR_COUNT
                an = "?()";
                break;
            case 1122:  // MQIACF_Q_MGR_SYSTEM
                an = "?()";
                break;
            case 1123:  // MQIACF_Q_MGR_EVENT
                an = "?()";
                break;
            case 1124:  // MQIACF_Q_MGR_DQM
                an = "?()";
                break;
            case 1125:  // MQIACF_Q_MGR_CLUSTER
                an = "?()";
                break;
            case 1126:  // MQIACF_QSG_DISPS
                an = "?()";
                break;
            case 1128:  // MQIACF_UOW_STATE
                an = "?()";
                break;
            case 1129:  // MQIACF_SECURITY_ITEM
                an = "?()";
                break;
            case 1130:  // MQIACF_CF_STRUC_STATUS
                an = "?()";
                break;
            case 1132:  // MQIACF_UOW_TYPE
                an = "?()";
                break;
            case 1133:  // MQIACF_CF_STRUC_ATTRS
                an = "?()";
                break;
            case 1134:  // MQIACF_EXCLUDE_INTERVAL
                an = "?()";
                break;
            case 1135:  // MQIACF_CF_STATUS_TYPE
                an = "?()";
                break;
            case 1136:  // MQIACF_CF_STATUS_SUMMARY
                an = "?()";
                break;
            case 1137:  // MQIACF_CF_STATUS_CONNECT
                an = "?()";
                break;
            case 1138:  // MQIACF_CF_STATUS_BACKUP
                an = "?()";
                break;
            case 1139:  // MQIACF_CF_STRUC_TYPE
                an = "?()";
                break;
            case 1140:  // MQIACF_CF_STRUC_SIZE_MAX
                an = "?()";
                break;
            case 1141:  // MQIACF_CF_STRUC_SIZE_USED
                an = "?()";
                break;
            case 1142:  // MQIACF_CF_STRUC_ENTRIES_MAX
                an = "?()";
                break;
            case 1143:  // MQIACF_CF_STRUC_ENTRIES_USED
                an = "?()";
                break;
            case 1144:  // MQIACF_CF_STRUC_BACKUP_SIZE
                an = "?()";
                break;
            case 1145:  // MQIACF_MOVE_TYPE
                an = "?()";
                break;
            case 1146:  // MQIACF_MOVE_TYPE_MOVE
                an = "?()";
                break;
            case 1147:  // MQIACF_MOVE_TYPE_ADD
                an = "?()";
                break;
            case 1148:  // MQIACF_Q_MGR_NUMBER
                an = "?()";
                break;
            case 1149:  // MQIACF_Q_MGR_STATUS
                an = "?()";
                break;
            case 1150:  // MQIACF_DB2_CONN_STATUS
                an = "?()";
                break;
            case 1151:  // MQIACF_SECURITY_ATTRS
                an = "?()";
                break;
            case 1152:  // MQIACF_SECURITY_TIMEOUT
                an = "?()";
                break;
            case 1153:  // MQIACF_SECURITY_INTERVAL
                an = "?()";
                break;
            case 1154:  // MQIACF_SECURITY_SWITCH
                an = "?()";
                break;
            case 1155:  // MQIACF_SECURITY_SETTING
                an = "?()";
                break;
            case 1156:  // MQIACF_STORAGE_CLASS_ATTRS
                an = "?()";
                break;
            case 1157:  // MQIACF_USAGE_TYPE
                an = "?()";
                break;
            case 1158:  // MQIACF_BUFFER_POOL_ID
                an = "?()";
                break;
            case 1159:  // MQIACF_USAGE_TOTAL_PAGES
                an = "?()";
                break;
            case 1160:  // MQIACF_USAGE_UNUSED_PAGES
                an = "?()";
                break;
            case 1161:  // MQIACF_USAGE_PERSIST_PAGES
                an = "?()";
                break;
            case 1162:  // MQIACF_USAGE_NONPERSIST_PAGES
                an = "?()";
                break;
            case 1163:  // MQIACF_USAGE_RESTART_EXTENTS
                an = "?()";
                break;
            case 1164:  // MQIACF_USAGE_EXPAND_COUNT
                an = "?()";
                break;
            case 1165:  // MQIACF_PAGESET_STATUS
                an = "?()";
                break;
            case 1166:  // MQIACF_USAGE_TOTAL_BUFFERS
                an = "?()";
                break;
            case 1167:  // MQIACF_USAGE_DATA_SET_TYPE
                an = "?()";
                break;
            case 1168:  // MQIACF_USAGE_PAGESET
                an = "?()";
                break;
            case 1169:  // MQIACF_USAGE_DATA_SET
                an = "?()";
                break;
            case 1170:  // MQIACF_USAGE_BUFFER_POOL
                an = "?()";
                break;
            case 1171:  // MQIACF_MOVE_COUNT
                an = "?()";
                break;
            case 1172:  // MQIACF_EXPIRY_Q_COUNT
                an = "?()";
                break;
            case 1173:  // MQIACF_CONFIGURATION_OBJECTS
                an = "?()";
                break;
            case 1174:  // MQIACF_CONFIGURATION_EVENTS
                an = "?()";
                break;
            case 1175:  // MQIACF_SYSP_TYPE
                an = "?()";
                break;
            case 1176:  // MQIACF_SYSP_DEALLOC_INTERVAL
                an = "?()";
                break;
            case 1177:  // MQIACF_SYSP_MAX_ARCHIVE
                an = "?()";
                break;
            case 1178:  // MQIACF_SYSP_MAX_READ_TAPES
                an = "?()";
                break;
            case 1179:  // MQIACF_SYSP_IN_BUFFER_SIZE
                an = "?()";
                break;
            case 1180:  // MQIACF_SYSP_OUT_BUFFER_SIZE
                an = "?()";
                break;
            case 1181:  // MQIACF_SYSP_OUT_BUFFER_COUNT
                an = "?()";
                break;
            case 1182:  // MQIACF_SYSP_ARCHIVE
                an = "?()";
                break;
            case 1183:  // MQIACF_SYSP_DUAL_ACTIVE
                an = "?()";
                break;
            case 1184:  // MQIACF_SYSP_DUAL_ARCHIVE
                an = "?()";
                break;
            case 1185:  // MQIACF_SYSP_DUAL_BSDS
                an = "?()";
                break;
            case 1186:  // MQIACF_SYSP_MAX_CONNS
                an = "?()";
                break;
            case 1187:  // MQIACF_SYSP_MAX_CONNS_FORE
                an = "?()";
                break;
            case 1188:  // MQIACF_SYSP_MAX_CONNS_BACK
                an = "?()";
                break;
            case 1189:  // MQIACF_SYSP_EXIT_INTERVAL
                an = "?()";
                break;
            case 1190:  // MQIACF_SYSP_EXIT_TASKS
                an = "?()";
                break;
            case 1191:  // MQIACF_SYSP_CHKPOINT_COUNT
                an = "?()";
                break;
            case 1192:  // MQIACF_SYSP_OTMA_INTERVAL
                an = "?()";
                break;
            case 1193:  // MQIACF_SYSP_Q_INDEX_DEFER
                an = "?()";
                break;
            case 1194:  // MQIACF_SYSP_DB2_TASKS
                an = "?()";
                break;
            case 1195:  // MQIACF_SYSP_RESLEVEL_AUDIT
                an = "?()";
                break;
            case 1196:  // MQIACF_SYSP_ROUTING_CODE
                an = "?()";
                break;
            case 1197:  // MQIACF_SYSP_SMF_ACCOUNTING
                an = "?()";
                break;
            case 1198:  // MQIACF_SYSP_SMF_STATS
                an = "?()";
                break;
            case 1199:  // MQIACF_SYSP_SMF_INTERVAL
                an = "?()";
                break;
            case 1200:  // MQIACF_SYSP_TRACE_CLASS
                an = "?()";
                break;
            case 1201:  // MQIACF_SYSP_TRACE_SIZE
                an = "?()";
                break;
            case 1202:  // MQIACF_SYSP_WLM_INTERVAL
                an = "?()";
                break;
            case 1203:  // MQIACF_SYSP_ALLOC_UNIT
                an = "?()";
                break;
            case 1204:  // MQIACF_SYSP_ARCHIVE_RETAIN
                an = "?()";
                break;
            case 1205:  // MQIACF_SYSP_ARCHIVE_WTOR
                an = "?()";
                break;
            case 1206:  // MQIACF_SYSP_BLOCK_SIZE
                an = "?()";
                break;
            case 1207:  // MQIACF_SYSP_CATALOG
                an = "?()";
                break;
            case 1208:  // MQIACF_SYSP_COMPACT
                an = "?()";
                break;
            case 1209:  // MQIACF_SYSP_ALLOC_PRIMARY
                an = "?()";
                break;
            case 1210:  // MQIACF_SYSP_ALLOC_SECONDARY
                an = "?()";
                break;
            case 1211:  // MQIACF_SYSP_PROTECT
                an = "?()";
                break;
            case 1212:  // MQIACF_SYSP_QUIESCE_INTERVAL
                an = "?()";
                break;
            case 1213:  // MQIACF_SYSP_TIMESTAMP
                an = "?()";
                break;
            case 1214:  // MQIACF_SYSP_UNIT_ADDRESS
                an = "?()";
                break;
            case 1215:  // MQIACF_SYSP_UNIT_STATUS
                an = "?()";
                break;
            case 1216:  // MQIACF_SYSP_LOG_COPY
                an = "?()";
                break;
            case 1217:  // MQIACF_SYSP_LOG_USED
                an = "?()";
                break;
            case 1218:  // MQIACF_SYSP_LOG_SUSPEND
                an = "?()";
                break;
            case 1219:  // MQIACF_SYSP_OFFLOAD_STATUS
                an = "?()";
                break;
            case 1220:  // MQIACF_SYSP_TOTAL_LOGS
                an = "?()";
                break;
            case 1221:  // MQIACF_SYSP_FULL_LOGS
                an = "?()";
                break;
            case 1222:  // MQIACF_LISTENER_ATTRS
                an = "?()";
                break;
            case 1223:  // MQIACF_LISTENER_STATUS_ATTRS
                an = "?()";
                break;
            case 1224:  // MQIACF_SERVICE_ATTRS
                an = "?()";
                break;
            case 1225:  // MQIACF_SERVICE_STATUS_ATTRS
                an = "?()";
                break;
            case 1226:  // MQIACF_Q_TIME_INDICATOR
                an = "?()";
                break;
            case 1227:  // MQIACF_OLDEST_MSG_AGE
                an = "?()";
                break;
            case 1228:  // MQIACF_AUTH_OPTIONS
                an = "?()";
                break;
            case 1229:  // MQIACF_Q_MGR_STATUS_ATTRS
                an = "?()";
                break;
            case 1230:  // MQIACF_CONNECTION_COUNT
                an = "?()";
                break;
            case 1231:  // MQIACF_Q_MGR_FACILITY
                an = "?()";
                break;
            case 1232:  // MQIACF_CHINIT_STATUS
                an = "?()";
                break;
            case 1233:  // MQIACF_CMD_SERVER_STATUS
                an = "?()";
                break;
            case 1234:  // MQIACF_ROUTE_DETAIL
                an = "?()";
                break;
            case 1235:  // MQIACF_RECORDED_ACTIVITIES
                an = "?()";
                break;
            case 1236:  // MQIACF_MAX_ACTIVITIES
                an = "?()";
                break;
            case 1237:  // MQIACF_DISCONTINUITY_COUNT
                an = "?()";
                break;
            case 1238:  // MQIACF_ROUTE_ACCUMULATION
                an = "?()";
                break;
            case 1239:  // MQIACF_ROUTE_DELIVERY
                an = "?()";
                break;
            case 1240:  // MQIACF_OPERATION_TYPE
                an = "?()";
                break;
            case 1241:  // MQIACF_BACKOUT_COUNT
                an = "?()";
                break;
            case 1242:  // MQIACF_COMP_CODE
                an = "?()";
                break;
            case 1243:  // MQIACF_ENCODING
                an = "?()";
                break;
            case 1244:  // MQIACF_EXPIRY
                an = "?()";
                break;
            case 1245:  // MQIACF_FEEDBACK
                an = "?()";
                break;
            case 1247:  // MQIACF_MSG_FLAGS
                an = "?()";
                break;
            case 1248:  // MQIACF_MSG_LENGTH
                an = "?()";
                break;
            case 1249:  // MQIACF_MSG_TYPE
                an = "?()";
                break;
            case 1250:  // MQIACF_OFFSET
                an = "?()";
                break;
            case 1251:  // MQIACF_ORIGINAL_LENGTH
                an = "?()";
                break;
            case 1252:  // MQIACF_PERSISTENCE
                an = "?()";
                break;
            case 1253:  // MQIACF_PRIORITY
                an = "?()";
                break;
            case 1254:  // MQIACF_REASON_CODE
                an = "?()";
                break;
            case 1255:  // MQIACF_REPORT
                an = "?()";
                break;
            case 1256:  // MQIACF_VERSION
                an = "?()";
                break;
            case 1257:  // MQIACF_UNRECORDED_ACTIVITIES
                an = "?()";
                break;
            case 1258:  // MQIACF_MONITORING
                an = "?()";
                break;
            case 1259:  // MQIACF_ROUTE_FORWARDING
                an = "?()";
                break;
            case 1260:  // MQIACF_SERVICE_STATUS
                an = "?()";
                break;
            case 1261:  // MQIACF_Q_TYPES
                an = "?()";
                break;
            case 1262:  // MQIACF_USER_ID_SUPPORT
                an = "?()";
                break;
            case 1263:  // MQIACF_INTERFACE_VERSION
                an = "?()";
                break;
            case 1264:  // MQIACF_AUTH_SERVICE_ATTRS
                an = "?()";
                break;
            case 1265:  // MQIACF_USAGE_EXPAND_TYPE
                an = "?()";
                break;
            case 1266:  // MQIACF_SYSP_CLUSTER_CACHE
                an = "?()";
                break;
            case 1267:  // MQIACF_SYSP_DB2_BLOB_TASKS
                an = "?()";
                break;
            case 1268:  // MQIACF_SYSP_WLM_INT_UNITS
                an = "?()";
                break;
            case 1269:  // MQIACF_TOPIC_ATTRS
                an = "?()";
                break;
            case 1271:  // MQIACF_PUBSUB_PROPERTIES
                an = "?()";
                break;
            case 1273:  // MQIACF_DESTINATION_CLASS
                an = "?()";
                break;
            case 1274:  // MQIACF_DURABLE_SUBSCRIPTION
                an = "?()";
                break;
            case 1275:  // MQIACF_SUBSCRIPTION_SCOPE
                an = "?()";
                break;
            case 1277:  // MQIACF_VARIABLE_USER_ID
                an = "?()";
                break;
            case 1280:  // MQIACF_REQUEST_ONLY
                an = "?()";
                break;
            case 1283:  // MQIACF_PUB_PRIORITY
                an = "?()";
                break;
            case 1287:  // MQIACF_SUB_ATTRS
                an = "?()";
                break;
            case 1288:  // MQIACF_WILDCARD_SCHEMA
                an = "?()";
                break;
            case 1289:  // MQIACF_SUB_TYPE
                an = "?()";
                break;
            case 1290:  // MQIACF_MESSAGE_COUNT
                an = "?()";
                break;
            case 1291:  // MQIACF_Q_MGR_PUBSUB
                an = "?()";
                break;
            case 1292:  // MQIACF_Q_MGR_VERSION
                an = "?()";
                break;
            case 1294:  // MQIACF_SUB_STATUS_ATTRS
                an = "?()";
                break;
            case 1295:  // MQIACF_TOPIC_STATUS
                an = "?()";
                break;
            case 1296:  // MQIACF_TOPIC_SUB
                an = "?()";
                break;
            case 1297:  // MQIACF_TOPIC_PUB
                an = "?()";
                break;
            case 1300:  // MQIACF_RETAINED_PUBLICATION
                an = "?()";
                break;
            case 1301:  // MQIACF_TOPIC_STATUS_ATTRS
                an = "?()";
                break;
            case 1302:  // MQIACF_TOPIC_STATUS_TYPE
                an = "?()";
                break;
            case 1303:  // MQIACF_SUB_OPTIONS
                an = "?()";
                break;
            case 1304:  // MQIACF_PUBLISH_COUNT
                an = "?()";
                break;
            case 1305:  // MQIACF_CLEAR_TYPE
                an = "?()";
                break;
            case 1306:  // MQIACF_CLEAR_SCOPE
                an = "?()";
                break;
            case 1307:  // MQIACF_SUB_LEVEL
                an = "?()";
                break;
            case 1308:  // MQIACF_ASYNC_STATE
                an = "?()";
                break;
            case 1309:  // MQIACF_SUB_SUMMARY
                an = "?()";
                break;
            case 1310:  // MQIACF_OBSOLETE_MSGS
                an = "?()";
                break;
            case 1311:  // MQIACF_PUBSUB_STATUS
                an = "?()";
                break;
            case 1314:  // MQIACF_PS_STATUS_TYPE
                an = "?()";
                break;
            case 1318:  // MQIACF_PUBSUB_STATUS_ATTRS
                an = "?()";
                break;
            case 1321:  // MQIACF_SELECTOR_TYPE
                an = "?()";
                break;
            case 1322:  // MQIACF_LOG_COMPRESSION
                an = "?()";
                break;
            case 1323:  // MQIACF_GROUPUR_CHECK_ID
                an = "?()";
                break;
            case 1324:  // MQIACF_MULC_CAPTURE
                an = "?()";
                break;
            case 1325:  // MQIACF_PERMIT_STANDBY
                an = "?()";
                break;
            case 1326:  // MQIACF_OPERATION_MODE
                an = "?()";
                break;
            case 1327:  // MQIACF_COMM_INFO_ATTRS
                an = "?()";
                break;
            case 1328:  // MQIACF_CF_SMDS_BLOCK_SIZE
                an = "?()";
                break;
            case 1329:  // MQIACF_CF_SMDS_EXPAND
                an = "?()";
                break;
            case 1330:  // MQIACF_USAGE_FREE_BUFF
                an = "?()";
                break;
            case 1331:  // MQIACF_USAGE_FREE_BUFF_PERC
                an = "?()";
                break;
            case 1332:  // MQIACF_CF_STRUC_ACCESS
                an = "?()";
                break;
            case 1333:  // MQIACF_CF_STATUS_SMDS
                an = "?()";
                break;
            case 1334:  // MQIACF_SMDS_ATTRS
                an = "?()";
                break;
            case 1335:  // MQIACF_USAGE_SMDS
                an = "?()";
                break;
            case 1336:  // MQIACF_USAGE_BLOCK_SIZE
                an = "?()";
                break;
            case 1337:  // MQIACF_USAGE_DATA_BLOCKS
                an = "?()";
                break;
            case 1338:  // MQIACF_USAGE_EMPTY_BUFFERS
                an = "?()";
                break;
            case 1339:  // MQIACF_USAGE_INUSE_BUFFERS
                an = "?()";
                break;
            case 1340:  // MQIACF_USAGE_LOWEST_FREE
                an = "?()";
                break;
            case 1341:  // MQIACF_USAGE_OFFLOAD_MSGS
                an = "?()";
                break;
            case 1342:  // MQIACF_USAGE_READS_SAVED
                an = "?()";
                break;
            case 1343:  // MQIACF_USAGE_SAVED_BUFFERS
                an = "?()";
                break;
            case 1344:  // MQIACF_USAGE_TOTAL_BLOCKS
                an = "?()";
                break;
            case 1345:  // MQIACF_USAGE_USED_BLOCKS
                an = "?()";
                break;
            case 1346:  // MQIACF_USAGE_USED_RATE
                an = "?()";
                break;
            case 1347:  // MQIACF_USAGE_WAIT_RATE
                an = "?()";
                break;
            case 1348:  // MQIACF_SMDS_OPENMODE
                an = "?()";
                break;
            case 1349:  // MQIACF_SMDS_STATUS
                an = "?()";
                break;
            case 1350:  // MQIACF_SMDS_AVAIL
                an = "?()";
                break;
            case 1351:  // MQIACF_MCAST_REL_INDICATOR
                an = "?()";
                break;
            case 1352:  // MQIACF_CHLAUTH_TYPE
                an = "?()";
                break;
            case 1354:  // MQIACF_MQXR_DIAGNOSTICS_TYPE
                an = "?()";
                break;
            case 1355:  // MQIACF_CHLAUTH_ATTRS
                an = "?()";
                break;
            case 1356:  // MQIACF_OPERATION_ID
                an = "?()";
                break;
            case 1357:  // MQIACF_API_CALLER_TYPE
                an = "?()";
                break;
            case 1358:  // MQIACF_API_ENVIRONMENT
                an = "?()";
                break;
            case 1359:  // MQIACF_TRACE_DETAIL
                an = "?()";
                break;
            case 1360:  // MQIACF_HOBJ
                an = "?()";
                break;
            case 1361:  // MQIACF_CALL_TYPE
                an = "?()";
                break;
            case 1362:  // MQIACF_MQCB_OPERATION
                an = "?()";
                break;
            case 1363:  // MQIACF_MQCB_TYPE
                an = "?()";
                break;
            case 1364:  // MQIACF_MQCB_OPTIONS
                an = "?()";
                break;
            case 1365:  // MQIACF_CLOSE_OPTIONS
                an = "?()";
                break;
            case 1366:  // MQIACF_CTL_OPERATION
                an = "?()";
                break;
            case 1367:  // MQIACF_GET_OPTIONS
                an = "?()";
                break;
            case 1368:  // MQIACF_RECS_PRESENT
                an = "?()";
                break;
            case 1369:  // MQIACF_KNOWN_DEST_COUNT
                an = "?()";
                break;
            case 1370:  // MQIACF_UNKNOWN_DEST_COUNT
                an = "?()";
                break;
            case 1371:  // MQIACF_INVALID_DEST_COUNT
                an = "?()";
                break;
            case 1372:  // MQIACF_RESOLVED_TYPE
                an = "?()";
                break;
            case 1373:  // MQIACF_PUT_OPTIONS
                an = "?()";
                break;
            case 1374:  // MQIACF_BUFFER_LENGTH
                an = "?()";
                break;
            case 1375:  // MQIACF_TRACE_DATA_LENGTH
                an = "?()";
                break;
            case 1376:  // MQIACF_SMDS_EXPANDST
                an = "?()";
                break;
            case 1377:  // MQIACF_STRUC_LENGTH
                an = "?()";
                break;
            case 1378:  // MQIACF_ITEM_COUNT
                an = "?()";
                break;
            case 1379:  // MQIACF_EXPIRY_TIME
                an = "?()";
                break;
            case 1380:  // MQIACF_CONNECT_TIME
                an = "?()";
                break;
            case 1381:  // MQIACF_DISCONNECT_TIME
                an = "?()";
                break;
            case 1382:  // MQIACF_HSUB
                an = "?()";
                break;
            case 1383:  // MQIACF_SUBRQ_OPTIONS
                an = "?()";
                break;
            case 1384:  // MQIACF_XA_RMID
                an = "?()";
                break;
            case 1385:  // MQIACF_XA_FLAGS
                an = "?()";
                break;
            case 1386:  // MQIACF_XA_RETCODE
                an = "?()";
                break;
            case 1387:  // MQIACF_XA_HANDLE
                an = "?()";
                break;
            case 1388:  // MQIACF_XA_RETVAL
                an = "?()";
                break;
            case 1389:  // MQIACF_STATUS_TYPE
                an = "?()";
                break;
            case 1390:  // MQIACF_XA_COUNT
                an = "?()";
                break;
            case 1391:  // MQIACF_SELECTOR_COUNT
                an = "?()";
                break;
            case 1392:  // MQIACF_SELECTORS
                an = "?()";
                break;
            case 1393:  // MQIACF_INTATTR_COUNT
                an = "?()";
                break;
            case 1394:  // MQIACF_INT_ATTRS
                an = "?()";
                break;
            case 1395:  // MQIACF_SUBRQ_ACTION
                an = "?()";
                break;
            case 1396:  // MQIACF_NUM_PUBS
                an = "?()";
                break;
            case 1397:  // MQIACF_POINTER_SIZE
                an = "?()";
                break;
            case 1398:  // MQIACF_REMOVE_AUTHREC
                an = "?()";
                break;
            case 1399:  // MQIACF_XR_ATTRS
                an = "?()";
                break;
            case 1400:  // MQIACF_APPL_FUNCTION_TYPE
                an = "?()";
                break;
            case 1402:  // MQIACF_EXPORT_TYPE
                an = "?()";
                break;
            case 1403:  // MQIACF_EXPORT_ATTRS
                an = "?()";
                break;
            case 1404:  // MQIACF_SYSTEM_OBJECTS // MQIACF_LAST_USED
                an = "?()";
                break;
            case 1501:  // MQIACH_XMIT_PROTOCOL_TYPE / MQIACH_FIRST
                an = "?()";
                break;
            case 1502:  // MQIACH_BATCH_SIZE
                an = "?()";
                break;
            case 1503:  // MQIACH_DISC_INTERVAL
                an = "?()";
                break;
            case 1504:  // MQIACH_SHORT_TIMER
                an = "?()";
                break;
            case 1505:  // MQIACH_SHORT_RETRY
                an = "?()";
                break;
            case 1506:  // MQIACH_LONG_TIMER
                an = "?()";
                break;
            case 1507:  // MQIACH_LONG_RETRY
                an = "?()";
                break;
            case 1508:  // MQIACH_PUT_AUTHORITY
                an = "?()";
                break;
            case 1509:  // MQIACH_SEQUENCE_NUMBER_WRAP
                an = "?()";
                break;
            case 1510:  // MQIACH_MAX_MSG_LENGTH
                an = "?()";
                break;
            case 1511:  // MQIACH_CHANNEL_TYPE
                an = "?()";
                break;
            case 1512:  // MQIACH_DATA_COUNT
                an = "?()";
                break;
            case 1513:  // MQIACH_NAME_COUNT
                an = "?()";
                break;
            case 1514:  // MQIACH_MSG_SEQUENCE_NUMBER
                an = "?()";
                break;
            case 1515:  // MQIACH_DATA_CONVERSION
                an = "?()";
                break;
            case 1516:  // MQIACH_IN_DOUBT
                an = "?()";
                break;
            case 1517:  // MQIACH_MCA_TYPE
                an = "?()";
                break;
            case 1518:  // MQIACH_SESSION_COUNT
                an = "?()";
                break;
            case 1519:  // MQIACH_ADAPTER
                an = "?()";
                break;
            case 1520:  // MQIACH_COMMAND_COUNT
                an = "?()";
                break;
            case 1521:  // MQIACH_SOCKET
                an = "?()";
                break;
            case 1522:  // MQIACH_PORT
                an = "?()";
                break;
            case 1523:  // MQIACH_CHANNEL_INSTANCE_TYPE
                an = "?()";
                break;
            case 1524:  // MQIACH_CHANNEL_INSTANCE_ATTRS
                an = "?()";
                break;
            case 1525:  // MQIACH_CHANNEL_ERROR_DATA
                an = "?()";
                break;
            case 1526:  // MQIACH_CHANNEL_TABLE
                an = "?()";
                break;
            case 1527:  // MQIACH_CHANNEL_STATUS
                an = "?()";
                break;
            case 1528:  // MQIACH_INDOUBT_STATUS
                an = "?()";
                break;
            case 1529:  // MQIACH_LAST_SEQUENCE_NUMBER / MQIACH_LAST_SEQ_NUMBER
                an = "?()";
                break;
            case 1531:  // MQIACH_CURRENT_MSGS
                an = "?()";
                break;
            case 1532:  // MQIACH_CURRENT_SEQUENCE_NUMBER / MQIACH_CURRENT_SEQ_NUMBER
                an = "?()";
                break;
            case 1533:  // MQIACH_SSL_RETURN_CODE
                an = "?()";
                break;
            case 1534:  // MQIACH_MSGS
                an = "?()";
                break;
            case 1535:  // MQIACH_BYTES_SENT
                an = "?()";
                break;
            case 1536:  // MQIACH_BYTES_RECEIVED / MQIACH_BYTES_RCVD
                an = "?()";
                break;
            case 1537:  // MQIACH_BATCHES
                an = "?()";
                break;
            case 1538:  // MQIACH_BUFFERS_SENT
                an = "?()";
                break;
            case 1539:  // MQIACH_BUFFERS_RECEIVED / MQIACH_BUFFERS_RCVD
                an = "?()";
                break;
            case 1540:  // MQIACH_LONG_RETRIES_LEFT
                an = "?()";
                break;
            case 1541:  // MQIACH_SHORT_RETRIES_LEFT
                an = "?()";
                break;
            case 1542:  // MQIACH_MCA_STATUS
                an = "?()";
                break;
            case 1543:  // MQIACH_STOP_REQUESTED
                an = "?()";
                break;
            case 1544:  // MQIACH_MR_COUNT
                an = "?()";
                break;
            case 1545:  // MQIACH_MR_INTERVAL
                an = "?()";
                break;
            case 1562:  // MQIACH_NPM_SPEED
                an = "?()";
                break;
            case 1563:  // MQIACH_HB_INTERVAL
                an = "?()";
                break;
            case 1564:  // MQIACH_BATCH_INTERVAL
                an = "?()";
                break;
            case 1565:  // MQIACH_NETWORK_PRIORITY
                an = "?()";
                break;
            case 1566:  // MQIACH_KEEP_ALIVE_INTERVAL
                an = "?()";
                break;
            case 1567:  // MQIACH_BATCH_HB
                an = "?()";
                break;
            case 1568:  // MQIACH_SSL_CLIENT_AUTH
                an = "?()";
                break;
            case 1570:  // MQIACH_ALLOC_RETRY
                an = "?()";
                break;
            case 1571:  // MQIACH_ALLOC_FAST_TIMER
                an = "?()";
                break;
            case 1572:  // MQIACH_ALLOC_SLOW_TIMER
                an = "?()";
                break;
            case 1573:  // MQIACH_DISC_RETRY
                an = "?()";
                break;
            case 1574:  // MQIACH_PORT_NUMBER
                an = "?()";
                break;
            case 1575:  // MQIACH_HDR_COMPRESSION
                an = "?()";
                break;
            case 1576:  // MQIACH_MSG_COMPRESSION
                an = "?()";
                break;
            case 1577:  // MQIACH_CLWL_CHANNEL_RANK
                an = "?()";
                break;
            case 1578:  // MQIACH_CLWL_CHANNEL_PRIORITY
                an = "?()";
                break;
            case 1579:  // MQIACH_CLWL_CHANNEL_WEIGHT
                an = "?()";
                break;
            case 1580:  // MQIACH_CHANNEL_DISP
                an = "?()";
                break;
            case 1581:  // MQIACH_INBOUND_DISP
                an = "?()";
                break;
            case 1582:  // MQIACH_CHANNEL_TYPES
                an = "?()";
                break;
            case 1583:  // MQIACH_ADAPS_STARTED
                an = "?()";
                break;
            case 1584:  // MQIACH_ADAPS_MAX
                an = "?()";
                break;
            case 1585:  // MQIACH_DISPS_STARTED
                an = "?()";
                break;
            case 1586:  // MQIACH_DISPS_MAX
                an = "?()";
                break;
            case 1587:  // MQIACH_SSLTASKS_STARTED
                an = "?()";
                break;
            case 1588:  // MQIACH_SSLTASKS_MAX
                an = "?()";
                break;
            case 1589:  // MQIACH_CURRENT_CHL
                an = "?()";
                break;
            case 1590:  // MQIACH_CURRENT_CHL_MAX
                an = "?()";
                break;
            case 1591:  // MQIACH_CURRENT_CHL_TCP
                an = "?()";
                break;
            case 1592:  // MQIACH_CURRENT_CHL_LU62
                an = "?()";
                break;
            case 1593:  // MQIACH_ACTIVE_CHL
                an = "?()";
                break;
            case 1594:  // MQIACH_ACTIVE_CHL_MAX
                an = "?()";
                break;
            case 1595:  // MQIACH_ACTIVE_CHL_PAUSED
                an = "?()";
                break;
            case 1596:  // MQIACH_ACTIVE_CHL_STARTED
                an = "?()";
                break;
            case 1597:  // MQIACH_ACTIVE_CHL_STOPPED
                an = "?()";
                break;
            case 1598:  // MQIACH_ACTIVE_CHL_RETRY
                an = "?()";
                break;
            case 1599:  // MQIACH_LISTENER_STATUS
                an = "?()";
                break;
            case 1600:  // MQIACH_SHARED_CHL_RESTART
                an = "?()";
                break;
            case 1601:  // MQIACH_LISTENER_CONTROL
                an = "?()";
                break;
            case 1602:  // MQIACH_BACKLOG
                an = "?()";
                break;
            case 1604:  // MQIACH_XMITQ_TIME_INDICATOR
                an = "?()";
                break;
            case 1605:  // MQIACH_NETWORK_TIME_INDICATOR
                an = "?()";
                break;
            case 1606:  // MQIACH_EXIT_TIME_INDICATOR
                an = "?()";
                break;
            case 1607:  // MQIACH_BATCH_SIZE_INDICATOR
                an = "?()";
                break;
            case 1608:  // MQIACH_XMITQ_MSGS_AVAILABLE
                an = "?()";
                break;
            case 1609:  // MQIACH_CHANNEL_SUBSTATE
                an = "?()";
                break;
            case 1610:  // MQIACH_SSL_KEY_RESETS
                an = "?()";
                break;
            case 1611:  // MQIACH_COMPRESSION_RATE
                an = "?()";
                break;
            case 1612:  // MQIACH_COMPRESSION_TIME
                an = "?()";
                break;
            case 1613:  // MQIACH_MAX_XMIT_SIZE
                an = "?()";
                break;
            case 1614:  // MQIACH_DEF_CHANNEL_DISP
                an = "?()";
                break;
            case 1615:  // MQIACH_SHARING_CONVERSATIONS
                an = "?()";
                break;
            case 1616:  // MQIACH_MAX_SHARING_CONVS
                an = "?()";
                break;
            case 1617:  // MQIACH_CURRENT_SHARING_CONVS
                an = "?()";
                break;
            case 1618:  // MQIACH_MAX_INSTANCES
                an = "?()";
                break;
            case 1619:  // MQIACH_MAX_INSTS_PER_CLIENT
                an = "?()";
                break;
            case 1620:  // MQIACH_CLIENT_CHANNEL_WEIGHT
                an = "?()";
                break;
            case 1621:  // MQIACH_CONNECTION_AFFINITY
                an = "?()";
                break;
            case 1623:  // MQIACH_RESET_REQUESTED
                an = "?()";
                break;
            case 1624:  // MQIACH_BATCH_DATA_LIMIT
                an = "?()";
                break;
            case 1625:  // MQIACH_MSG_HISTORY
                an = "?()";
                break;
            case 1626:  // MQIACH_MULTICAST_PROPERTIES
                an = "?()";
                break;
            case 1627:  // MQIACH_NEW_SUBSCRIBER_HISTORY
                an = "?()";
                break;
            case 1628:  // MQIACH_MC_HB_INTERVAL
                an = "?()";
                break;
            case 1629:  // MQIACH_USE_CLIENT_ID
                an = "?()";
                break;
            case 1630:  // MQIACH_MQTT_KEEP_ALIVE
                an = "?()";
                break;
            case 1631:  // MQIACH_IN_DOUBT_IN
                an = "?()";
                break;
            case 1632:  // MQIACH_IN_DOUBT_OUT
                an = "?()";
                break;
            case 1633:  // MQIACH_MSGS_SENT
                an = "?()";
                break;
            case 1634:  // MQIACH_MSGS_RCVD / MQIACH_MSGS_RECEIVED
                an = "?()";
                break;
            case 1635:  // MQIACH_PENDING_OUT
                an = "?()";
                break;
            case 1636:  // MQIACH_AVAILABLE_CIPHERSPECS
                an = "?()";
                break;
            case 1637:  // MQIACH_MATCH
                an = "?()";
                break;
            case 1638:  // MQIACH_USER_SOURCE
                an = "?()";
                break;
            case 1639:  // MQIACH_WARNING
                an = "?()";
                break;
            case 1640:  // MQIACH_DEF_RECONNECT
                an = "?()";
                break;
            case 1642:  // MQIACH_CHANNEL_SUMMARY_ATTRS / MQIACH_LAST_USED
                an = "?()";
                break;
			case 2000:  // MQIA_USER_LIST / MQIA_LAST 
				an = "?()";
				break;
			case 2001:  // MQCA_APPL_ID / MQCA_FIRST
				an = "?()";
				break;
			case 2002:  // MQCA_BASE_OBJECT_NAME / MQCA_BASE_Q_NAME
				an = "TARGET()";
				break;
			case 2003:  // MQCA_COMMAND_INPUT_Q_NAME
				an = "COMMANDQ()";
				break;
			case 2004:  // MQCA_CREATION_DATE
				an = "CRDATE()";
				break;
			case 2005:  // MQCA_CREATION_TIME
				an = "CRTIME()";
				break;
			case 2006:  // MQCA_DEAD_LETTER_Q_NAME
				an = "DEADQ()";
				break;
			case 2007:  // MQCA_ENV_DATA 
				an = "?()";
				break;
			case 2008:  // MQCA_INITIATION_Q_NAME
				an = "INITQ()";
				break;
			case 2009:  // MQCA_NAMELIST_DESC
				an = "DESCR()";
				break;
			case 2010:  // MQCA_NAMELIST_NAME
				an = "NAMELIST()";
				break;
			case 2011:  // MQCA_PROCESS_DESC
				an = "DESCR()";
				break;
			case 2012:  // MQCA_PROCESS_NAME
				an = "PROCESS()";
				break;
			case 2013:  // MQCA_Q_DESC
				an = "DESCR()";
				break;
			case 2014:  // MQCA_Q_MGR_DESC
				an = "DESCR()";
				break;
			case 2015:  // MQCA_Q_MGR_NAME
				an = "QMNAME()";
				break;
			case 2016:  // MQCA_Q_NAME
				an = "QUEUE()";
				break;
			case 2017:  // MQCA_REMOTE_Q_MGR_NAME
				an = "RQMNAME()";
				break;
			case 2018:  // MQCA_REMOTE_Q_NAME
				an = "RNAME()";
				break;
			case 2019:  // MQCA_BACKOUT_REQ_Q_NAME 
				an = "?()";
				break;
			case 2020:  // MQCA_NAMES 
				an = "?()";
				break;
			case 2021:  // MQCA_USER_DATA
				an = "?()";
				break;
			case 2022:  // MQCA_STORAGE_CLASS 
				an = "?()";
				break;
			case 2023:  // MQCA_TRIGGER_DATA
				an = "?()";
				break;
			case 2024:  // MQCA_XMIT_Q_NAME
				an = "?()";
				break;
			case 2025:  // MQCA_DEF_XMIT_Q_NAME 
				an = "DEFXMITQ()";
				break;
			case 2026:  // MQCA_CHANNEL_AUTO_DEF_EXIT 
				an = "?()";
				break;
			case 2027:  // MQCA_ALTERATION_DATE
				an = "ALTDATE()";
				break;
			case 2028:  // MQCA_ALTERATION_TIME
				an = "ALTTIME()";
				break;
			case 2029:  // MQCA_CLUSTER_NAME 
				an = "?()";
				break;
			case 2030:  // MQCA_CLUSTER_NAMELIST 
				an = "?()";
				break;
			case 2031:  // MQCA_CLUSTER_Q_MGR_NAME
				an = "?()";
				break;
			case 2032:  // MQCA_Q_MGR_IDENTIFIER
				an = "?()";
				break;
			case 2033:  // MQCA_CLUSTER_WORKLOAD_EXIT
				an = "CLWLEXIT()";
				break;
			case 2034:  // MQCA_CLUSTER_WORKLOAD_DATA
				an = "CLWLDATA()";
				break;
			case 2035:  // MQCA_REPOSITORY_NAME
				an = "REPOS()";
				break;
			case 2036:  // MQCA_REPOSITORY_NAMELIST 
				an = "REPOSNL()";
				break;
			case 2037:  // MQCA_CLUSTER_DATE
				an = "?()";
				break;
			case 2038:  // MQCA_CLUSTER_TIME
				an = "?()";
				break;
			case 2039:  // MQCA_CF_STRUC_NAME
				an = "?()";
				break;
			case 2040:  // MQCA_QSG_NAME
				an = "?()";
				break;
			case 2041:  // MQCA_IGQ_USER_ID
				an = "?()";
				break;
			case 2042:  // MQCA_STORAGE_CLASS_DESC
				an = "?()";
				break;
			case 2043:  // MQCA_XCF_GROUP_NAME
				an = "?()";
				break;
			case 2044:  // MQCA_XCF_MEMBER_NAME
				an = "?()";
				break;
			case 2045:  // MQCA_AUTH_INFO_NAME
				an = "?()";
				break;
			case 2046:  // MQCA_AUTH_INFO_DESC
				an = "?()";
				break;
			case 2047:  // MQCA_LDAP_USER_NAME
				an = "?()";
				break;
			case 2048:  // MQCA_LDAP_PASSWORD
				an = "?()";
				break;
			case 2049:  // MQCA_SSL_KEY_REPOSITORY
				an = "SSLKEYR()";
				break;
			case 2050:  // MQCA_SSL_CRL_NAMELIST
				an = "?()";
				break;
			case 2051:  // MQCA_SSL_CRYPTO_HARDWARE
				an = "?()";
				break;
			case 2052:  // MQCA_CF_STRUC_DESC
				an = "?()";
				break;
			case 2053:  // MQCA_AUTH_INFO_CONN_NAME
				an = "?()";
				break;
			case 2060:  // MQCA_CICS_FILE_NAME
				an = "?()";
				break;	
			case 2061:  // MQCA_TRIGGER_TRANS_ID
				an = "?()";
				break;	
			case 2062:  // MQCA_TRIGGER_PROGRAM_NAME
				an = "?()";
				break;	
			case 2063:  // MQCA_TRIGGER_TERM_ID
				an = "?()";
				break;	
			case 2064:  // MQCA_TRIGGER_CHANNEL_NAME
				an = "?()";
				break;
			case 2065:  // MQCA_SYSTEM_LOG_Q_NAME
				an = "?()";
				break;	
			case 2066:  // MQCA_MONITOR_Q_NAME
				an = "?()";
				break;	
			case 2067:  // MQCA_COMMAND_REPLY_Q_NAME
				an = "?()";
				break;	
			case 2068:  // MQCA_BATCH_INTERFACE_ID
				an = "?()";
				break;
			case 2069:  // MQCA_SSL_KEY_LIBRARY
				an = "?()";
				break;
			case 2070:  // MQCA_SSL_KEY_MEMBER 
				an = "?()";
				break;
			case 2071:  // MQCA_DNS_GROUP
				an = "?()";
				break;
			case 2072:  // MQCA_LU_GROUP_NAME
				an = "?()";
				break;
			case 2073:  // MQCA_LU_NAME
				an = "?()";
				break;
			case 2074:  // MQCA_LU62_ARM_SUFFIX
				an = "?()";
				break;
			case 2075:  // MQCA_TCP_NAME
				an = "?()";
				break;
			case 2076:  // MQCA_CHINIT_SERVICE_PARM
				an = "?()";
				break;
			case 2077:  // MQCA_SERVICE_NAME
				an = "SERVICE()";
				break;	
			case 2078:  // MQCA_SERVICE_DESC
				an = "?()";
				break;
			case 2079:  // MQCA_SERVICE_START_COMMAND
				an = "?()";
				break;
			case 2080:  // MQCA_SERVICE_START_ARGS
				an = "?()";
				break;
			case 2081:  // MQCA_SERVICE_STOP_COMMAND
				an = "?()";
				break;
			case 2082:  // MQCA_SERVICE_STOP_ARGS
				an = "?()";
				break;
			case 2083:  // MQCA_STDOUT_DESTINATION
				an = "?()";
				break;
			case 2084:  // MQCA_STDERR_DESTINATION
				an = "?()";
				break;
			case 2085:  // MQCA_TPIPE_NAME
				an = "?()";
				break;
			case 2086:  // MQCA_PASS_TICKET_APPL
				an = "?()";
				break;
			case 2090:  // MQCA_AUTO_REORG_START_TIME
				an = "?()";
				break;
			case 2091:  // MQCA_AUTO_REORG_CATALOG
				an = "?()";
				break;
			case 2092:  // MQCA_TOPIC_NAME
				an = "?()";
				break;
			case 2093:  // MQCA_TOPIC_DESC
				an = "?()";
				break;
			case 2094:  // MQCA_TOPIC_STRING
				an = "?()";
				break;
			case 2096:  // MQCA_MODEL_DURABLE_Q
				an = "?()";
				break;
			case 2097:  // MQCA_MODEL_NON_DURABLE_Q
				an = "?()";
				break;
			case 2098:  // MQCA_RESUME_DATE
				an = "?()";
				break;
			case 2099:  // MQCA_RESUME_TIME
				an = "?()";
				break;
			case 2102:  // MQCA_PARENT
				an = "?()";
				break;
			case 2105:  // MQCA_ADMIN_TOPIC_NAME
				an = "?()";
				break;
			case 2108:  // MQCA_TOPIC_STRING_FILTER
				an = "?()";
				break;
			case 2109:  // MQCA_AUTH_INFO_OCSP_URL
				an = "?()";
				break;
			case 2110:  // MQCA_COMM_INFO_NAME
				an = "?()";
				break;
			case 2111:  // MQCA_COMM_INFO_DESC
				an = "?()";
				break;
			case 2112:  // MQCA_POLICY_NAME
				an = "?()";
				break;
			case 2113:  // MQCA_SIGNER_DN
				an = "?()";
				break;
			case 2114:  // MQCA_RECIPIENT_DN 
				an = "?()";
				break;
			case 2115:  // MQCA_INSTALLATION_DESC
				an = "?()";
				break;
			case 2116:  // MQCA_INSTALLATION_NAME
				an = "?()";
				break;
			case 2117:  // MQCA_INSTALLATION_PATH
				an = "?()";
				break;
			case 2118:  // MQCA_CHLAUTH_DESC
				an = "?()";
				break;
			case 2119:  // MQCA_CUSTOM
				an = "CUSTOM()";
				break;
			case 2120:  // MQCA_VERSION
				an = "VERSION()";
				break;
			case 2121:  // MQCA_CHILD  
				an = "?()";
				break;
			case 2122:  // MQCA_XR_VERSION 
				an = "?()";
				break;
			case 2123:  // MQCA_XR_SSL_CIPHER_SUITES 
				an = "?()";
				break;
			case 2124:  // MQCA_CLUS_CHL_NAME / MQCA_LAST_USED
				an = "?()";
				break;
            case 3001:  // MQCACF_FROM_Q_NAME / MQCACF_FIRST
                an = "?()";
                break;
            case 3002:  // MQCACF_TO_Q_NAME
                an = "?()";
                break;
            case 3003:  // MQCACF_FROM_PROCESS_NAME
                an = "?()";
                break;
            case 3004:  // MQCACF_TO_PROCESS_NAME
                an = "?()";
                break;
            case 3005:  // MQCACF_FROM_NAMELIST_NAME
                an = "?()";
                break;
            case 3006:  // MQCACF_TO_NAMELIST_NAME
                an = "?()";
                break;
            case 3007:  // MQCACF_FROM_CHANNEL_NAME
                an = "?()";
                break;
            case 3008:  // MQCACF_TO_CHANNEL_NAME
                an = "?()";
                break;
            case 3009:  // MQCACF_FROM_AUTH_INFO_NAME
                an = "?()";
                break;
            case 3010:  // MQCACF_TO_AUTH_INFO_NAME
                an = "?()";
                break;
            case 3011:  // MQCACF_Q_NAMES
                an = "?()";
                break;
            case 3012:  // MQCACF_PROCESS_NAMES
                an = "?()";
                break;
            case 3013:  // MQCACF_NAMELIST_NAMES
                an = "?()";
                break;
            case 3014:  // MQCACF_ESCAPE_TEXT
                an = "?()";
                break;
            case 3015:  // MQCACF_LOCAL_Q_NAMES
                an = "?()";
                break;
            case 3016:  // MQCACF_MODEL_Q_NAMES
                an = "?()";
                break;
            case 3017:  // MQCACF_ALIAS_Q_NAMES
                an = "?()";
                break;
            case 3018:  // MQCACF_REMOTE_Q_NAMES
                an = "?()";
                break;
            case 3019:  // MQCACF_SENDER_CHANNEL_NAMES
                an = "?()";
                break;
            case 3020:  // MQCACF_SERVER_CHANNEL_NAMES
                an = "?()";
                break;
            case 3021:  // MQCACF_REQUESTER_CHANNEL_NAMES
                an = "?()";
                break;
            case 3022:  // MQCACF_RECEIVER_CHANNEL_NAMES
                an = "?()";
                break;
            case 3023:  // MQCACF_OBJECT_Q_MGR_NAME
                an = "?()";
                break;
            case 3024:  // MQCACF_APPL_NAME
                an = "?()";
                break;
            case 3025:  // MQCACF_USER_IDENTIFIER
                an = "?()";
                break;
            case 3026:  // MQCACF_AUX_ERROR_DATA_STR_1
                an = "?()";
                break;
            case 3027:  // MQCACF_AUX_ERROR_DATA_STR_2
                an = "?()";
                break;
            case 3028:  // MQCACF_AUX_ERROR_DATA_STR_3
                an = "?()";
                break;
            case 3029:  // MQCACF_BRIDGE_NAME
                an = "?()";
                break;
            case 3030:  // MQCACF_STREAM_NAME
                an = "?()";
                break;
            case 3031:  // MQCACF_TOPIC
                an = "?()";
                break;
            case 3032:  // MQCACF_PARENT_Q_MGR_NAME
                an = "?()";
                break;
            case 3033:  // MQCACF_CORREL_ID
                an = "?()";
                break;
            case 3034:  // MQCACF_PUBLISH_TIMESTAMP
                an = "?()";
                break;
            case 3035:  // MQCACF_STRING_DATA
                an = "?()";
                break;
            case 3036:  // MQCACF_SUPPORTED_STREAM_NAME
                an = "?()";
                break;
            case 3037:  // MQCACF_REG_TOPIC
                an = "?()";
                break;
            case 3038:  // MQCACF_REG_TIME
                an = "?()";
                break;
            case 3039:  // MQCACF_REG_USER_ID
                an = "?()";
                break;
            case 3040:  // MQCACF_CHILD_Q_MGR_NAME
                an = "?()";
                break;
            case 3041:  // MQCACF_REG_STREAM_NAME
                an = "?()";
                break;
            case 3042:  // MQCACF_REG_Q_MGR_NAME
                an = "?()";
                break;
            case 3043:  // MQCACF_REG_Q_NAME
                an = "?()";
                break;
            case 3044:  // MQCACF_REG_CORREL_ID
                an = "?()";
                break;
            case 3045:  // MQCACF_EVENT_USER_ID
                an = "?()";
                break;
            case 3046:  // MQCACF_OBJECT_NAME
                an = "?()";
                break;
            case 3047:  // MQCACF_EVENT_Q_MGR
                an = "?()";
                break;
            case 3048:  // MQCACF_AUTH_INFO_NAMES
                an = "?()";
                break;
            case 3049:  // MQCACF_EVENT_APPL_IDENTITY
                an = "?()";
                break;
            case 3050:  // MQCACF_EVENT_APPL_NAME
                an = "?()";
                break;
            case 3051:  // MQCACF_EVENT_APPL_ORIGIN
                an = "?()";
                break;
            case 3052:  // MQCACF_SUBSCRIPTION_NAME
                an = "?()";
                break;
            case 3053:  // MQCACF_REG_SUB_NAME
                an = "?()";
                break;
            case 3054:  // MQCACF_SUBSCRIPTION_IDENTITY
                an = "?()";
                break;
            case 3055:  // MQCACF_REG_SUB_IDENTITY
                an = "?()";
                break;
            case 3056:  // MQCACF_SUBSCRIPTION_USER_DATA
                an = "?()";
                break;
            case 3057:  // MQCACF_REG_SUB_USER_DATA
                an = "?()";
                break;
            case 3058:  // MQCACF_APPL_TAG
                an = "?()";
                break;
            case 3059:  // MQCACF_DATA_SET_NAME
                an = "?()";
                break;
            case 3060:  // MQCACF_UOW_START_DATE
                an = "?()";
                break;
            case 3061:  // MQCACF_UOW_START_TIME
                an = "?()";
                break;
            case 3062:  // MQCACF_UOW_LOG_START_DATE
                an = "?()";
                break;
            case 3063:  // MQCACF_UOW_LOG_START_TIME
                an = "?()";
                break;
            case 3064:  // MQCACF_UOW_LOG_EXTENT_NAME
                an = "?()";
                break;
            case 3065:  // MQCACF_PRINCIPAL_ENTITY_NAMES
                an = "?()";
                break;
            case 3066:  // MQCACF_GROUP_ENTITY_NAMES
                an = "?()";
                break;
            case 3067:  // MQCACF_AUTH_PROFILE_NAME
                an = "?()";
                break;
            case 3068:  // MQCACF_ENTITY_NAME
                an = "?()";
                break;
            case 3069:  // MQCACF_SERVICE_COMPONENT
                an = "?()";
                break;
            case 3070:  // MQCACF_RESPONSE_Q_MGR_NAME
                an = "?()";
                break;
            case 3071:  // MQCACF_CURRENT_LOG_EXTENT_NAME
                an = "?()";
                break;
            case 3072:  // MQCACF_RESTART_LOG_EXTENT_NAME
                an = "?()";
                break;
            case 3073:  // MQCACF_MEDIA_LOG_EXTENT_NAME
                an = "?()";
                break;
            case 3074:  // MQCACF_LOG_PATH
                an = "?()";
                break;
            case 3075:  // MQCACF_COMMAND_MQSC
                an = "?()";
                break;
            case 3076:  // MQCACF_Q_MGR_CPF
                an = "?()";
                break;
            case 3078:  // MQCACF_USAGE_LOG_RBA
                an = "?()";
                break;
            case 3079:  // MQCACF_USAGE_LOG_LRSN
                an = "?()";
                break;
            case 3080:  // MQCACF_COMMAND_SCOPE
                an = "?()";
                break;
            case 3081:  // MQCACF_ASID
                an = "?()";
                break;
            case 3082:  // MQCACF_PSB_NAME
                an = "?()";
                break;
            case 3083:  // MQCACF_PST_ID
                an = "?()";
                break;
            case 3084:  // MQCACF_TASK_NUMBER
                an = "?()";
                break;
            case 3085:  // MQCACF_TRANSACTION_ID
                an = "?()";
                break;
            case 3086:  // MQCACF_Q_MGR_UOW_ID
                an = "?()";
                break;
            case 3088:  // MQCACF_ORIGIN_NAME
                an = "?()";
                break;
            case 3089:  // MQCACF_ENV_INFO
                an = "?()";
                break;
            case 3090:  // MQCACF_SECURITY_PROFILE
                an = "?()";
                break;
            case 3091:  // MQCACF_CONFIGURATION_DATE
                an = "?()";
                break;
            case 3092:  // MQCACF_CONFIGURATION_TIME
                an = "?()";
                break;
            case 3093:  // MQCACF_FROM_CF_STRUC_NAME
                an = "?()";
                break;
            case 3094:  // MQCACF_TO_CF_STRUC_NAME
                an = "?()";
                break;
            case 3095:  // MQCACF_CF_STRUC_NAMES
                an = "?()";
                break;
            case 3096:  // MQCACF_FAIL_DATE
                an = "?()";
                break;
            case 3097:  // MQCACF_FAIL_TIME
                an = "?()";
                break;
            case 3098:  // MQCACF_BACKUP_DATE
                an = "?()";
                break;
            case 3099:  // MQCACF_BACKUP_TIME
                an = "?()";
                break;
            case 3100:  // MQCACF_SYSTEM_NAME
                an = "?()";
                break;
            case 3101:  // MQCACF_CF_STRUC_BACKUP_START
                an = "?()";
                break;
            case 3102:  // MQCACF_CF_STRUC_BACKUP_END
                an = "?()";
                break;
            case 3103:  // MQCACF_CF_STRUC_LOG_Q_MGRS
                an = "?()";
                break;
            case 3104:  // MQCACF_FROM_STORAGE_CLASS
                an = "?()";
                break;
            case 3105:  // MQCACF_TO_STORAGE_CLASS
                an = "?()";
                break;
            case 3106:  // MQCACF_STORAGE_CLASS_NAMES
                an = "?()";
                break;
            case 3108:  // MQCACF_DSG_NAME
                an = "?()";
                break;
            case 3109:  // MQCACF_DB2_NAME
                an = "?()";
                break;
            case 3110:  // MQCACF_SYSP_CMD_USER_ID
                an = "?()";
                break;
            case 3111:  // MQCACF_SYSP_OTMA_GROUP
                an = "?()";
                break;
            case 3112:  // MQCACF_SYSP_OTMA_MEMBER
                an = "?()";
                break;
            case 3113:  // MQCACF_SYSP_OTMA_DRU_EXIT
                an = "?()";
                break;
            case 3114:  // MQCACF_SYSP_OTMA_TPIPE_PFX
                an = "?()";
                break;
            case 3115:  // MQCACF_SYSP_ARCHIVE_PFX1
                an = "?()";
                break;
            case 3116:  // MQCACF_SYSP_ARCHIVE_UNIT1
                an = "?()";
                break;
            case 3117:  // MQCACF_SYSP_LOG_CORREL_ID
                an = "?()";
                break;
            case 3118:  // MQCACF_SYSP_UNIT_VOLSER
                an = "?()";
                break;
            case 3119:  // MQCACF_SYSP_Q_MGR_TIME
                an = "?()";
                break;
            case 3120:  // MQCACF_SYSP_Q_MGR_DATE
                an = "?()";
                break;
            case 3121:  // MQCACF_SYSP_Q_MGR_RBA
                an = "?()";
                break;
            case 3122:  // MQCACF_SYSP_LOG_RBA
                an = "?()";
                break;
            case 3123:  // MQCACF_SYSP_SERVICE
                an = "?()";
                break;
            case 3124:  // MQCACF_FROM_LISTENER_NAME
                an = "?()";
                break;
            case 3125:  // MQCACF_TO_LISTENER_NAME
                an = "?()";
                break;
            case 3126:  // MQCACF_FROM_SERVICE_NAME
                an = "?()";
                break;
            case 3127:  // MQCACF_TO_SERVICE_NAME
                an = "?()";
                break;
            case 3128:  // MQCACF_LAST_PUT_DATE
                an = "?()";
                break;
            case 3129:  // MQCACF_LAST_PUT_TIME
                an = "?()";
                break;
            case 3130:  // MQCACF_LAST_GET_DATE
                an = "?()";
                break;
            case 3131:  // MQCACF_LAST_GET_TIME
                an = "?()";
                break;
            case 3132:  // MQCACF_OPERATION_DATE
                an = "?()";
                break;
            case 3133:  // MQCACF_OPERATION_TIME
                an = "?()";
                break;
            case 3134:  // MQCACF_ACTIVITY_DESC
                an = "?()";
                break;
            case 3135:  // MQCACF_APPL_IDENTITY_DATA
                an = "?()";
                break;
            case 3136:  // MQCACF_APPL_ORIGIN_DATA
                an = "?()";
                break;
            case 3137:  // MQCACF_PUT_DATE
                an = "?()";
                break;
            case 3138:  // MQCACF_PUT_TIME
                an = "?()";
                break;
            case 3139:  // MQCACF_REPLY_TO_Q
                an = "?()";
                break;
            case 3140:  // MQCACF_REPLY_TO_Q_MGR
                an = "?()";
                break;
            case 3141:  // MQCACF_RESOLVED_Q_NAME
                an = "?()";
                break;
            case 3142:  // MQCACF_STRUC_ID
                an = "?()";
                break;
            case 3143:  // MQCACF_VALUE_NAME
                an = "?()";
                break;
            case 3144:  // MQCACF_SERVICE_START_DATE
                an = "?()";
                break;
            case 3145:  // MQCACF_SERVICE_START_TIME
                an = "?()";
                break;
            case 3146:  // MQCACF_SYSP_OFFLINE_RBA
                an = "?()";
                break;
            case 3147:  // MQCACF_SYSP_ARCHIVE_PFX2
                an = "?()";
                break;
            case 3148:  // MQCACF_SYSP_ARCHIVE_UNIT2
                an = "?()";
                break;
            case 3149:  // MQCACF_TO_TOPIC_NAME
                an = "?()";
                break;
            case 3150:  // MQCACF_FROM_TOPIC_NAME
                an = "?()";
                break;
            case 3151:  // MQCACF_TOPIC_NAMES
                an = "?()";
                break;
            case 3152:  // MQCACF_SUB_NAME
                an = "?()";
                break;
            case 3153:  // MQCACF_DESTINATION_Q_MGR
                an = "?()";
                break;
            case 3154:  // MQCACF_DESTINATION
                an = "?()";
                break;
            case 3156:  // MQCACF_SUB_USER_ID
                an = "?()";
                break;
            case 3159:  // MQCACF_SUB_USER_DATA
                an = "?()";
                break;
            case 3160:  // MQCACF_SUB_SELECTOR
                an = "?()";
                break;
            case 3161:  // MQCACF_LAST_PUB_DATE
                an = "?()";
                break;
            case 3162:  // MQCACF_LAST_PUB_TIME
                an = "?()";
                break;
            case 3163:  // MQCACF_FROM_SUB_NAME
                an = "?()";
                break;
            case 3164:  // MQCACF_TO_SUB_NAME
                an = "?()";
                break;
            case 3167:  // MQCACF_LAST_MSG_TIME
                an = "?()";
                break;
            case 3168:  // MQCACF_LAST_MSG_DATE
                an = "?()";
                break;
            case 3169:  // MQCACF_SUBSCRIPTION_POINT
                an = "?()";
                break;
            case 3170:  // MQCACF_FILTER
                an = "?()";
                break;
            case 3171:  // MQCACF_NONE
                an = "?()";
                break;
            case 3172:  // MQCACF_ADMIN_TOPIC_NAMES
                an = "?()";
                break;
            case 3173:  // MQCACF_ROUTING_FINGER_PRINT
                an = "?()";
                break;
            case 3174:  // MQCACF_APPL_DESC
                an = "?()";
                break;
            case 3175:  // MQCACF_Q_MGR_START_DATE
                an = "?()";
                break;
            case 3176:  // MQCACF_Q_MGR_START_TIME
                an = "?()";
                break;
            case 3177:  // MQCACF_FROM_COMM_INFO_NAME
                an = "?()";
                break;
            case 3178:  // MQCACF_TO_COMM_INFO_NAME
                an = "?()";
                break;
            case 3179:  // MQCACF_CF_OFFLOAD_SIZE1
                an = "?()";
                break;
            case 3180:  // MQCACF_CF_OFFLOAD_SIZE2
                an = "?()";
                break;
            case 3181:  // MQCACF_CF_OFFLOAD_SIZE3
                an = "?()";
                break;
            case 3182:  // MQCACF_CF_SMDS_GENERIC_NAME
                an = "?()";
                break;
            case 3183:  // MQCACF_CF_SMDS
                an = "?()";
                break;
            case 3184:  // MQCACF_RECOVERY_DATE
                an = "?()";
                break;
            case 3185:  // MQCACF_RECOVERY_TIME
                an = "?()";
                break;
            case 3186:  // MQCACF_CF_SMDSCONN
                an = "?()";
                break;
            case 3187:  // MQCACF_CF_STRUC_NAME
                an = "?()";
                break;
            case 3188:  // MQCACF_ALTERNATE_USERID
                an = "?()";
                break;
            case 3189:  // MQCACF_CHAR_ATTRS
                an = "?()";
                break;
            case 3190:  // MQCACF_DYNAMIC_Q_NAME
                an = "?()";
                break;
            case 3191:  // MQCACF_HOST_NAME
                an = "?()";
                break;
            case 3192:  // MQCACF_MQCB_NAME
                an = "?()";
                break;
            case 3193:  // MQCACF_OBJECT_STRING
                an = "?()";
                break;
            case 3194:  // MQCACF_RESOLVED_LOCAL_Q_MGR
                an = "?()";
                break;
            case 3195:  // MQCACF_RESOLVED_LOCAL_Q_NAME
                an = "?()";
                break;
            case 3196:  // MQCACF_RESOLVED_OBJECT_STRING
                an = "?()";
                break;
            case 3197:  // MQCACF_RESOLVED_Q_MGR
                an = "?()";
                break;
            case 3198:  // MQCACF_SELECTION_STRING
                an = "?()";
                break;
            case 3199:  // MQCACF_XA_INFO
                an = "?()";
                break;
            case 3200:  // MQCACF_APPL_FUNCTION
                an = "?()";
                break;
            case 3201:  // MQCACF_XQH_REMOTE_Q_NAME
                an = "?()";
                break;
            case 3202:  // MQCACF_XQH_REMOTE_Q_MGR
                an = "?()";
                break;
            case 3203:  // MQCACF_XQH_PUT_TIME
                an = "?()";
                break;
            case 3204:  // MQCACF_XQH_PUT_DATE / MQCACF_LAST_USED
                an = "?()";
                break;
            case 3501:  // MQCACH_CHANNEL_NAME / MQCACH_FIRST
                an = "?()";
                break;
            case 3502:  // MQCACH_DESC
                an = "?()";
                break;
            case 3503:  // MQCACH_MODE_NAME
                an = "?()";
                break;
            case 3504:  // MQCACH_TP_NAME
                an = "?()";
                break;
            case 3505:  // MQCACH_XMIT_Q_NAME
                an = "?()";
                break;
            case 3506:  // MQCACH_CONNECTION_NAME
                an = "?()";
                break;
            case 3507:  // MQCACH_MCA_NAME
                an = "?()";
                break;
            case 3508:  // MQCACH_SEC_EXIT_NAME
                an = "?()";
                break;
            case 3509:  // MQCACH_MSG_EXIT_NAME
                an = "?()";
                break;
            case 3510:  // MQCACH_SEND_EXIT_NAME
                an = "?()";
                break;
            case 3511:  // MQCACH_RCV_EXIT_NAME
                an = "?()";
                break;
            case 3512:  // MQCACH_CHANNEL_NAMES
                an = "?()";
                break;
            case 3513:  // MQCACH_SEC_EXIT_USER_DATA
                an = "?()";
                break;
            case 3514:  // MQCACH_MSG_EXIT_USER_DATA
                an = "?()";
                break;
            case 3515:  // MQCACH_SEND_EXIT_USER_DATA
                an = "?()";
                break;
            case 3516:  // MQCACH_RCV_EXIT_USER_DATA
                an = "?()";
                break;
            case 3517:  // MQCACH_USER_ID
                an = "?()";
                break;
            case 3518:  // MQCACH_PASSWORD
                an = "?()";
                break;
            case 3520:  // MQCACH_LOCAL_ADDRESS
                an = "?()";
                break;
            case 3521:  // MQCACH_LOCAL_NAME
                an = "?()";
                break;
            case 3524:  // MQCACH_LAST_MSG_TIME
                an = "?()";
                break;
            case 3525:  // MQCACH_LAST_MSG_DATE
                an = "?()";
                break;
            case 3527:  // MQCACH_MCA_USER_ID
                an = "?()";
                break;
            case 3528:  // MQCACH_CHANNEL_START_TIME
                an = "?()";
                break;
            case 3529:  // MQCACH_CHANNEL_START_DATE
                an = "?()";
                break;
            case 3530:  // MQCACH_MCA_JOB_NAME
                an = "?()";
                break;
            case 3531:  // MQCACH_LAST_LUWID
                an = "?()";
                break;
            case 3532:  // MQCACH_CURRENT_LUWID
                an = "?()";
                break;
            case 3533:  // MQCACH_FORMAT_NAME
                an = "?()";
                break;
            case 3534:  // MQCACH_MR_EXIT_NAME
                an = "?()";
                break;
            case 3535:  // MQCACH_MR_EXIT_USER_DATA
                an = "?()";
                break;
            case 3544:  // MQCACH_SSL_CIPHER_SPEC
                an = "?()";
                break;
            case 3545:  // MQCACH_SSL_PEER_NAME
                an = "?()";
                break;
            case 3546:  // MQCACH_SSL_HANDSHAKE_STAGE
                an = "?()";
                break;
            case 3547:  // MQCACH_SSL_SHORT_PEER_NAME
                an = "?()";
                break;
            case 3548:  // MQCACH_REMOTE_APPL_TAG
                an = "?()";
                break;
            case 3549:  // MQCACH_SSL_CERT_USER_ID
                an = "?()";
                break;
            case 3550:  // MQCACH_SSL_CERT_ISSUER_NAME
                an = "?()";
                break;
            case 3551:  // MQCACH_LU_NAME
                an = "?()";
                break;
            case 3552:  // MQCACH_IP_ADDRESS
                an = "?()";
                break;
            case 3553:  // MQCACH_TCP_NAME
                an = "?()";
                break;
            case 3554:  // MQCACH_LISTENER_NAME
                an = "?()";
                break;
            case 3555:  // MQCACH_LISTENER_DESC
                an = "?()";
                break;
            case 3556:  // MQCACH_LISTENER_START_DATE
                an = "?()";
                break;
            case 3557:  // MQCACH_LISTENER_START_TIME
                an = "?()";
                break;
            case 3558:  // MQCACH_SSL_KEY_RESET_DATE
                an = "?()";
                break;
            case 3559:  // MQCACH_SSL_KEY_RESET_TIME
                an = "?()";
                break;
            case 3560:  // MQCACH_REMOTE_VERSION
                an = "?()";
                break;
            case 3561:  // MQCACH_REMOTE_PRODUCT
                an = "?()";
                break;
            case 3562:  // MQCACH_GROUP_ADDRESS
                an = "?()";
                break;
            case 3563:  // MQCACH_JAAS_CONFIG
                an = "?()";
                break;
            case 3564:  // MQCACH_CLIENT_ID
                an = "?()";
                break;
            case 3565:  // MQCACH_SSL_KEY_PASSPHRASE
                an = "?()";
                break;
            case 3566:  // MQCACH_CONNECTION_NAME_LIST
                an = "?()";
                break;
            case 3567:  // MQCACH_CLIENT_USER_ID
                an = "?()";
                break;
            case 3568:  // MQCACH_MCA_USER_ID_LIST
                an = "?()";
                break;
            case 3569:  // MQCACH_SSL_CIPHER_SUITE / MQCACH_LAST_USED
                an = "?()";
                break;
			case 4000:  // MQCA_LAST / MQCA_USER_LIST 
				an = "?()";
				break;
			default:
				an = "UnKnOwN()";
				break;
		}
		
		return an;
	} // end of method get AttributeName() */
	
	public static URL useClientChannelTable(String clientChannelTableName) {
		
		File file = null;
		URL ccdt = null;
		
		try {
			file = new File(clientChannelTableName);
			ccdt = file.toURI().toURL();
		} catch (MalformedURLException me) {
			;
		} // end try
		
		return ccdt;
	} // end of method useClientChannelTable()
	
	public static URL useEnvVarMQCHLTAB() {
		
		File file = null;
		URL ccdt = null;
		
		String MQCHLLIB_envVar = System.getenv("MQCHLLIB");
		String MQCHLTAB_envVar = System.getenv("MQCHLTAB");
		
		if (MQCHLTAB_envVar == null) MQCHLTAB_envVar = new String("AMQCLCHL.TAB");
		if (MQCHLLIB_envVar == null) MQCHLLIB_envVar = new String(".");
		
		try {
			file = new File(MQCHLLIB_envVar + System.getProperty("file.separator") + MQCHLTAB_envVar);
			ccdt = file.toURI().toURL();
		} catch (MalformedURLException me) {
			;
		} // end try
		
		return ccdt;
	} // end of method useEnvVarMQCHLTAB()
	
	public static void useEnvVarMQSERVER() {
		
		String MQSERVER_envVar = System.getenv("MQSERVER");
		
		MQEnvironment.channel = "SYSTEM.DEF.SVRCONN";
		MQEnvironment.hostname = "127.0.0.1";
		MQEnvironment.port = 1414;
		
		if (MQSERVER_envVar != null) {
			int p1, p2;
			p1 = MQSERVER_envVar.indexOf('/');
			p2 = MQSERVER_envVar.lastIndexOf('/');
			if (p1 != -1 && p2 != -1) {
				MQEnvironment.channel = MQSERVER_envVar.substring(0, p1);
				p1 = MQSERVER_envVar.indexOf('(');
				if (p1 != -1) {
					MQEnvironment.hostname = MQSERVER_envVar.substring(p2 + 1, p1);
					p2 = MQSERVER_envVar.lastIndexOf(')');
					MQEnvironment.port = (new Integer(MQSERVER_envVar.substring(p1 + 1, p2))).intValue();
				} else
					MQEnvironment.hostname = MQSERVER_envVar.substring(p2 + 1);
			} // end if
		} // end if
	} // end of method useEnvVarMQSERVER()

} // end of class XMQUtils


	