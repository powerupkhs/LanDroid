#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>

#define MAX_DEVICES 10

typedef enum { FALSE = 0, TRUE = 1 } BOOL;

typedef struct AndroidNode {
	struct AndroidNode* next;
	struct AndroidNode* prev;		
	int deviceNo;
	BOOL set;
	int usbNo;
//	char vendor[5];
//	char prodID[5];
	char serialNo[20];
	char HWaddr[20];
} Node;

Node *head;

void InitList();
Node *InsertNodeRight (Node* targetNode, Node *newNode) ;
Node *InsertNodeLeft (Node* targetNode, Node *newNode) ;
void AppendNode (Node *newNode) ;
BOOL DeleteNode (Node *targetNode) ;
Node *FindNodeByIndex(int idx) ;
Node *FindNodeBySerialNo(char* sn) ;
Node *FindNodeByHWaddr(char* addr) ;
int GetNodeIndex(Node *targetNode) ;
int GetListCount() ;
void DeleteList() ;