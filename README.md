<a name="__DdeLink__196_827735600"></a>  

# Project Goals

In this project, a system is developed which we called it a distributed system that achieving a goal that is “Message Passing on Behalf of Clients”. This system is intended to provide an abstraction for the client that they see the whole system as single view. Clients should connect to a server and that all, the servers will take care of all communications and messages related to the other servers and other clients in the network. All message will be stored in the authoritative servers of each client, thus the system will keep the state of the client when they lose the connections unintentionally, however if there are any fault related to the servers, we will lose the information as consequences of this fault.

# Logical View Point

We should develop a system that brings Transparency, Openness, Resource Accessibility and Scalability.

<a name="_GoBack"></a>We defined these concepts for our project they we design the system based on the system properties and our goals.

## Concepts

Here we discuss distributed system specification of our system from different stand points.

## Transparency

<center>

<table width="800" cellpadding="10" cellspacing="10"><colgroup><col width="300"> <col width="194"> <col width="193"></colgroup>

<tbody>

<tr>

<td width="193" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

Access

</td>

<td width="194" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

<font size="2" style="font-size: 9pt">Hide differences in data representation and how a resource is accessed</font>

</td>

<td width="193" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 3in; padding-right: 0.08in">

True

</td>

</tr>

<tr>

<td width="193" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

Location

</td>

<td width="194" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

<font size="2" style="font-size: 9pt">Location Hide where a resource is located</font>

</td>

<td width="193" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 1in; padding-right: 0.08in">

True

</td>

</tr>

<tr>

<td width="193" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

Migration

</td>

<td width="194" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

<font size="2" style="font-size: 9pt">Hide that a resource may move to another location</font>

</td>

<td width="193" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 1in; padding-right: 0.08in">

False

</td>

</tr>

<tr>

<td width="193" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

Relocation

</td>

<td width="194" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

<font size="2" style="font-size: 9pt">Hide that a resource may be moved to another location while in use</font>

</td>

<td width="193" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 1in; padding-right: 0.08in">

False

</td>

</tr>

<tr>

<td width="193" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

Replication

</td>

<td width="194" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

<font size="2" style="font-size: 9pt">Hide that a resource is replicated</font>

</td>

<td width="193" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 1in; padding-right: 0.08in">

False

</td>

</tr>

<tr>

<td width="193" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

Concurrency

</td>

<td width="194" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

<font size="2" style="font-size: 9pt">Hide that a resource may be shared by several competitive users</font>

</td>

<td width="193" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

True

</td>

</tr>

<tr>

<td width="193" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

Failure

</td>

<td width="194" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

<font size="2" style="font-size: 9pt">Hide the failure and recovery of a resource</font>

</td>

<td width="193" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

Partially True

</td>

</tr>

</tbody>

</table>

</center>

## Openness

System is closed.

## Resource Accessibility

We are connecting all clients (users) to the proper resources as well.

## Scalability

<center>

<table width="416" cellpadding="7" cellspacing="0"><colgroup><col width="193"> <col width="193"></colgroup>

<tbody>

<tr>

<td width="193" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

Algorithm

</td>

<td width="193" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

Distributed

</td>

</tr>

<tr>

<td width="193" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

Data

</td>

<td width="193" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

Distributed

</td>

</tr>

<tr>

<td width="193" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

Services

</td>

<td width="193" style="border: 1px solid #00000a; padding-top: 0in; padding-bottom: 0in; padding-left: 0.08in; padding-right: 0.08in">

Distributed

</td>

</tr>

</tbody>

</table>

</center>

## Architecture

We have developed a “Monolithic Architecture” that we will introduce them in the future.

![alt tag](https://raw.githubusercontent.com/erfan111/DistributedMessagingApplication-Client/master/o1.png)


# System Components

As we saw before components are working and connected to each other tightly and we are unable to consider these components without each other and reuse them somewhere else.

## Client Manager

Client Manager is responsible for some tasks that mainly related to the clients:

*   Each server is in charge of managing its clients

*   So it records their credentials to know them later

*   Client registration and de-registration

*   Client message storage

## Server Manager

Server Manager is responsible for some tasks that mainly related to the servers:

*   Exactly like Client manager, Servers should have a record of their neighboring servers

*   Neighbor registration and deregistration is also done here

*   The Temporary Message storage is managed by this component

## Routing System

One of the challenging parts of this project was Routing System and we keep client and server states in the messages so the routing system could take advantage of information provided in the messages.

We modified SIP protocol and make it simplified in our project. In other word we remove the session initiation part from SIP in our project. Moreover, we append some additional headers to the default sip headers so we put all necessary information in these header then Routing System will use this information for its decisions. These information in the message bring us some more advantages too. For instance, we don’t need broadcasting any more.

*   When a message received by the server,

    *   If the server doesn't know about the receiver,

        *   This component decides which server should the message be sent to

*   Avoiding the servers that we already sent to

## System Monitor

System monitor will make users aware of the clients in the network and it can be used for some debugging tasks in the system.

*   Program logs

*   Debugging logs

*   Graphical User interface for registration

*   Graphical User interface for deregistration

*   Graphical User interface for connection status

*   UI for sending and receiving messages

## Storage Manager

Storage manager is responsible for storing messages come from clients.

*   Messages which are for these clients should be stored temporarily in their authoritative server Until an acknowledgment comes from the receiving client

## Sip Stack

Actually, Sip Stack is the main interface between clients and the middleware in our system, this means we each client has this component too and they can communicate to each other as means of this interface. Furthermore, the protocol for all of the communications is SIP (Session Initiation Protocol).

# Scenarios

Scenarios are available in the POWER POINT visualized.

# System Activities

Activities are available in the attached files.

# Implementation View point

## Technology

We implement this system using Java and Jain Sip as SIP protocol provider.

## Deployment View Point

Connections are based on IP and PORT so clients and servers could connect to each other just by providing these two factors. Thus, in every platform such as virtualized or native, clients and servers could connect to each other providing that there is a network connection between them.

Client and server application are exported as a Java JAR file though we can run our server and client applications on any operating system leveraging Java Virtual Machine.

In order to join the network clients should their IP and PORT plus a username.  

-------
### Contributors

- Erfan Sharafzadeh
- Seyyed Alireza Sanaee
- Hooman BehnezhadFard
- Mahdi Bagvand

Iran University of Science and Technology  
Computer Engineering School  
Distributed Systems course  
Fall 2016
