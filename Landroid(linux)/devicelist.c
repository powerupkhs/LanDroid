#include "devicelist.h"

void InitList()
{
	head = (Node*) malloc (sizeof(Node));
	head->prev = NULL;
	head->next = NULL;
}

Node *InsertNodeRight (Node* targetNode, Node *newNode) 
{
	Node *New;
	Node *Right;
	
	New = (Node *) malloc (sizeof(Node));
	*New = *newNode;

	Right = targetNode->next;
	New->next = Right;
	New->prev = targetNode;
	targetNode->next = New;
	if(Right) {
		Right->prev = New;
	}
	return New;
}

Node *InsertNodeLeft (Node* targetNode, Node *newNode) 
{
	Node *Left;
	
	Left = targetNode->prev;
	if(Left) {
		return InsertNodeRight(Left,newNode);
	}
	return NULL;
}

void AppendNode (Node *newNode) 
{
	Node *tail;
	
	for(tail = head; tail->next; tail = tail->next) { ; }
	
	InsertNodeRight(tail, newNode);
}

BOOL DeleteNode (Node *targetNode) 
{	
	Node *Left, *Right;

	if (targetNode == NULL || targetNode == head) {
		return FALSE;
	}
	
	Left = targetNode->prev;
	Right = targetNode->next;
	
	Left->next = Right;
	if (Right) {
		Right->prev = Left;
	}
	
	free(targetNode);

	return TRUE;
}

Node *FindNodeByIndex(int idx) 
{
	Node *Now;
	int Index = 0;

	for (Now = head->next; Now; Now = Now->next) {
		if (Index == idx) {
			return Now;
		}
		Index++;
	}
	return NULL;
}

Node *FindNodeBySerialNo(char* sn) 
{
	Node *Now;

	for (Now = head->next; Now; Now = Now->next) {
		if (strcmp(sn, Now->serialNo) == 0) {
			return Now;
		}
	}
	return NULL;
}

Node *FindNodeByHWaddr(char* addr)
{
	Node *Now;

	for (Now = head->next; Now; Now = Now->next) {
		if (strcmp(addr, Now->HWaddr) == 0) {
			return Now;
		}
	}
	return NULL;
}
int GetNodeIndex(Node *targetNode) 
{
	Node *Now;
	int Index = 0;

	for (Now = head->next; Now; Now = Now->next) {
		if (Now == targetNode) {
			return Index;
		}
		Index++;
	}
	return -1;
}

int GetListCount() 
{
	Node *Now;
	int Count = 0;

	for (Now = head->next; Now; Now = Now->next) {
		Count++;
	}	
	return Count;
}

void DeleteList() 
{
	while (DeleteNode(head->next)) { ; }

	free(head);
	head = NULL;
}


