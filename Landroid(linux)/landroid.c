#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <string.h>
#include <stdlib.h>
#include "devicelist.h"

#define MAX_DEVICES 10
#define MAX_SERIAL  20
#define MAX_BUFFER 200

// Pre Declearation
int CheckUsbDevices(int);
int CheckAdbDevices(int);
int InstallApp(char*);

void main (int argc, char** argv) 
{
	// Init Device Node
	Node *Now;
	InitList();

	
	// Init Variables 
	int countListDevices = 0;
	int countAdbDevices = 0;
	int i = 0;


	// Init Environment
	// Installed ADB Shell
	// Required SU Permission
	//system("mkdir ~/landroid 2> /dev/null");	
	//system("mkdir ~/landroid/bin 2> /dev/null");	
	//system("mkdir ~/landroid/temp 2> /dev/null");	
	system("sudo iptables -t nat -F");
	system("sudo iptables -t filter -F");
	system("sudo iptables -t filter -P FORWARD ACCEPT");
	system("sudo iptables -t nat -P PREROUTING ACCEPT");
	system("sudo iptables -t nat -P POSTROUTING ACCEPT");
	system("sudo iptables -t nat -P OUTPUT ACCEPT");
	system("echo 1 | sudo tee /proc/sys/net/ipv4/ip_forward > /dev/null");

	
	// Start ADB Server
	printf("Start ADB Server\n");
	system("sudo ./bin/adb kill-server > /dev/null");
	system("sudo ./bin/adb start-server > /dev/null");
	//printf("Running ADB Server\n");

	
	// Start Checking Daemon
	printf("Start Checking Daemon\n");
	while (TRUE) 
	{
		countListDevices = GetListCount();
		countAdbDevices  = CheckAdbDevices(countListDevices);
		
		printf("%d %d %d\n", countListDevices, countAdbDevices, CheckUsbDevices(countListDevices));
		sleep(1);
	} // end-while(true)
}

int CheckUsbDevices(int countList) {
	// Return USB Device Couting
	// Init Variables
	char buffer[MAX_BUFFER];
	char HWaddr[MAX_DEVICES][MAX_SERIAL];
	char usbName[5];
	char* temp;
	FILE* file;
	Node* Now;
	int countDevices = 0;
	int usbNo;

	// Check Usb Devices	
	sprintf(buffer, "sudo ifconfig | grep HWaddr 2> temp/ifconfig.tmp 1>> temp/ifconfig.tmp");
	system(buffer);
	
	file = fopen ("temp/ifconfig.tmp", "rt");
	
	if (file == NULL) {
		printf("\tERR : Fail to open file [ifconfig.tmp]\n");
		return -1;
	}
	
	// Dectect USB Ethernet Module & Match Node
	while ( fgets(buffer, MAX_BUFFER, file) != NULL ) 
	{
		temp = strtok (buffer, " ");

		if ( strstr(temp, "usb") != NULL) {
			// Check USB Ethernet Module
			strcpy(usbName, temp);
			
			while ( temp = strtok(NULL, " ") ) 
			{				
				if (strcmp(temp, "HWaddr") == 0) 
				{
					// Check HW address of USB Ethernet Module

					strcpy ( HWaddr[countDevices], strtok(NULL, " ") );

					// printf("\t %s", HWaddr[countDevices]);
				}
			}	 

			// Find Node by HW address
			Now = FindNodeByHWaddr( HWaddr[countDevices] );
			if (Now == NULL) {
				//printf("\tERR : Fail to find node by HW address\n");
				continue;
			}
			printf("Usb name %s\n",usbName); 
			// Match Usb Number to Node			
			usbNo = (int) (usbName[3] - '0');
			
			if ( (Now->usbNo == usbNo) && (Now->set == TRUE) ) 
			{
				// Already Set State
				continue;
			}
			else 
			{
				Now->usbNo = usbNo;			
			}
			// USB Ethernet Set

			printf("USB Ethernet Module Set\n");
			printf("\t+Serial No : %s\n", Now->serialNo);
			printf("\t+USB    No : %d\n", Now->usbNo);
			printf("\t+H/W  addr : %s\n", Now->HWaddr);
			
			sprintf(buffer, "sudo ifconfig usb%d 192.168.%d.1/24", Now->usbNo, (40 + Now->usbNo));
			system(buffer);

			sprintf(buffer, "sudo iptables -t nat -A POSTROUTING -o eth0 -s 192.168.%d.0/24 -j SNAT --to 211.189.127.76", (40 + Now->usbNo));
//			system(buffer);

			sprintf(buffer, "sudo iptables -t nat -F");
//			system(buffer);

			sprintf(buffer, "sudo iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE");
			system(buffer);

			sprintf(buffer, "sudo ./bin/adb -s %s shell su -c 'ifconfig rndis0 192.168.%d.2'",Now->serialNo, (40 + Now->usbNo));
			system(buffer);
			
			sprintf(buffer, "sudo ./bin/adb -s %s shell su -c 'route add default gw 192.168.%d.1 dev rndis0'", Now->serialNo, (40 + Now->usbNo));
			system(buffer);
			
			Now->set = TRUE;
			countDevices++;		
			
		}	
	
	} // end-while(fgets)
	
	fclose(file);

	return countDevices;
}

int CheckAdbDevices(int countList) {
	// Return ADB Devices Counting
	// Init Variables
	char buffer[MAX_BUFFER];
	char serialNo[MAX_DEVICES][MAX_SERIAL];
	BOOL flag = FALSE;
	int countDevices = 0, i = 0;
	FILE* file, *adb;
	pid_t pid;
	Node* Now;
	
	// Check Adb Devices
	system("sudo ./bin/adb devices > temp/adbDevices.tmp");
	file = fopen ("temp/adbDevices.tmp", "rt");
	
	if (file == NULL) {
		printf("\tERR : Fail to open file [adbDevices.tmp]\n");
		return -1;
	}

	while ( fgets(buffer, MAX_BUFFER, file) != NULL )
	{
		if (strstr (buffer, "List of devices attached") != NULL) {		// Check Start mark
			flag = TRUE;
			//printf("\tAndroid Device List (ADB)\n");
			continue;
		}
		else if (flag) {							// Check Devices
			strcpy( serialNo[countDevices], strtok(buffer, " \t") );
			if (serialNo[countDevices][0] == '\n') {			// Check Last mark
				// printf("\tEnd of List (ADB)\n");
				continue;
			}
			else if (serialNo[countDevices][0] == '?') {			// Check Exception
				printf("\t ERR : Permission Denie\n");
				continue;
			}
			
			// Check New Device by Serial Number
			if (FindNodeBySerialNo(serialNo[countDevices]) == NULL) 
			{
				printf("\t +Find New Device\n");

				Node newNode;
		
				sprintf (buffer, "sudo ./bin/adb -s %s shell su -c 'cat /sys/devices/virtual/android_usb/android0/f_rndis/ethaddr' > temp/hwaddr.tmp", serialNo[countDevices]);
				system (buffer);	
			
				adb = fopen ("temp/hwaddr.tmp", "rt");
				if (adb == NULL) {
					printf("\tERR : Fail to open file [hwaddr.tmp]\n");
					return -1;
				}
		
				fgets (newNode.HWaddr, MAX_SERIAL, adb);
				newNode.HWaddr[17] = 0;			// End of String
				fclose(adb);
				
				strcpy (newNode.serialNo, serialNo[countDevices]);
				newNode.usbNo = -1;
				newNode.set = FALSE;
				
				InstallApp(newNode.serialNo);
				AppendNode(&newNode);				
			}
			countDevices++;
		}
	} // end-while(fgets())

	fclose(file);

	sleep(1);
	
	// Check Remove Device by Serial Number
	for (Now = head->next; Now; Now = Now->next) 
	{
		for (i = 0; i < countDevices; i++) 
		{
			if ( strcmp(Now->serialNo, serialNo[i]) == 0 ) {
				break;
			}

		} // end-for(i)

		// Check Non-Exist Node 
		if (i == countDevices)
		{
			printf("\t -Find Removed Device\n");
			DeleteNode (Now);	
		}

	} // end-for(Now)

	return countDevices;
}

int InstallApp (char* serialNo) {	
// Install App by ADB
	// Init Variables
	char buffer[MAX_BUFFER];
	FILE* file;

	Node* Now = FindNodeBySerialNo(serialNo);
	if (Now != NULL && Now->set == TRUE) {
		return 0;
	}

	// Install App 
	printf("\tInstalling App : Android Device(%s)\n", serialNo);

	//sprintf(buffer, "sudo adb -s %s install %s >&1 | tee temp/adbInstall.tmp", serialNo, "bin/XposedInstaller.apk");
	sprintf(buffer, "sudo ./bin/adb -s %s install %s > temp/adbInstall.tmp", serialNo, "bin/XposedInstaller.apk");
	system(buffer);

	//sprintf(buffer, "sudo adb -s %s install %s >&1 | tee -a temp/adbInstall.tmp", serialNo, "bin/EnableCallRecording.apk");
	//sprintf(buffer, "sudo adb -s %s install %s >> temp/adbInstall.tmp", serialNo, "bin/EnableCallRecording.apk");
	sprintf(buffer, "sudo ./bin/adb -s %s install %s > temp/adbInstall.tmp", serialNo, "bin/LANDroid.apk");
	system(buffer);
	
	file = fopen ("temp/adbInstall.tmp", "rt");
	if (file == NULL) {
		printf("\tERR : Fail to open file [adbInstall.tmp]\n");
		return -1;
	}

	while(fgets(buffer, MAX_BUFFER, file) != NULL) {
		if (strstr (buffer, "Success") != NULL) {
			printf("\t +Android App Installed  (ADB)\n");
			continue;
		}
		else if (strstr (buffer, "INSTALL_FAILED_ALREADY_EXISTS") != NULL) {
			printf("\t +Android App Already Installed (ADB)\n");
			continue;
		}
		else if (strstr (buffer, "Failture") != NULL) {
			printf("\tERR : Fail to Install APP  (ADB)");
			continue;
		}					
	}
	fclose(file);
	return 0;	
}






















