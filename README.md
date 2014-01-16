## LAPIS

### About

LAPIS is a framework for incorporating computational steering in Java and MATLAB applications. It allows applications to expose variables through a REST interface so that the values of those variables can be retrieved and set at runtime by users or external processes. It also allows programmers to build networks of computationally-steered applications, each of which can be steered programmatically or manually through the REST interface exposed by each network node.  LAPIS is designed to make it simple for engineers and scientists who do not have significant experience in computer programming to easily deploy a communication network for High Performance Computing applications.

LAPIS is inspired by the dissertation of Harrison B. Smith at The Ohio State University and is developed with the support of the National Science Foundation (NSF).

### LAPIS features overview

This is a high-level overview of the features of the LAPIS framework. Each feature will be covered in more depth in the sections on using LAPIS in MATLAB and Java.

##### Publish variables

The core feature of the LAPIS framework is the ability to "publish" variables, which exposes the variables through LAPIS's REST interface. Within an application, published variables can then be used like regular variables (though they should not be re-assigned). The present values of published variables can be retrieved through LAPIS's REST interface, and the values of these variables can be set through the same interface. LAPIS will propagate the change to the application code in a way that is transparent to the application.

Applications can also publish variables as "read-only" within LAPIS. The values of these variables are still available to be retrieved, but cannot be set, through LAPIS's REST interface.

<!--

##### Redact variables

Variables which have been published can be un-published through LAPIS's redact method. Redacted variables can then be used like regular variables. They will no longer be exposed in LAPIS's REST interface.

-->

##### Creating LAPIS networks

LAPIS allows programmers to easily build networks of computationally-steered applications without the need for sophisticated software packages or network communication libraries. This makes it simple to implement applications which programmatically steer each other. Nodes within a LAPIS network can get and set each other's published variables. LAPIS handles the details of inter-process communication, so that client code remains simple.

<!-- 
##### Wait for ready network node

To facilitate coordination among multiple nodes in a LAPIS network, LAPIS allows applications to pause while waiting for other nodes to join a network, perform their initialization processing, and declare themselves ready. 
-->

##### REST interface

As mentioned previously, LAPIS exposes the values of published variables through a REST interface for easy debugging, control, and monitoring. The values of published variables can be retrieved. The values can also be set if the variables have not been published as "read-only" within LAPIS. In addition to getting and setting variable values, LAPIS also exposes variable meta-data and network information through its REST interface. Further detail on the REST interface will be provided in a separate section.


### Creating LAPIS networks

LAPIS allows programmers to build networks of computationally steered applications. Each application (also known as a _simulation_, or _model_) within the network can access other applications' published variables.

To create a LAPIS network, you must first create a LAPIS coordinator node. The coordinator node within a LAPIS network is the node that is responsible for maintaining a record of all of the nodes on the network. This record is stored within the LAPIS framework, so the client does not need to be concerned about it. After the coordinator node has been created, you can add multiple non-coordinator nodes to the network.  An important note is that the coordinator node is only responsible for keeping network information.  No application specific data is transmitted or received by the coordinator at any time during a communication.

There are three key details that client programmers must be aware of regarding coordinator and non-coordinator LAPIS nodes:
1. Coordinator nodes and non-coordinator nodes are created using different constructors for the LAPIS API object (LapisAPI in MATLAB, LapisApi in Java). The details for LAPIS API object creation are described in the sections on using LAPIS in MATLAB and Java. 
2. The coordinator node _must_ be running before non-coordinator nodes can join the network. If a non-coordinator node starts up before the coordinator for its network is running, an exception will be thrown when the non-coordinator attempts to connect to the coordinator and subsequently fails.
3. Each LAPIS network has only one coordinator, but a single application can participate in multiple LAPIS networks - such as by instantiating multiple LAPIS API Objects that connect to different LAPIS networks.

### Use in MATLAB

LAPIS includes a MATLAB client for incorporating steering in MATLAB applications.

##### Dependencies

To use the LAPIS client in MATLAB, you will need the LAPISData.m and LapisAPI.m files, and the lapis-matlab .jar file (as of the time of this writing, lapis-matlab-1.0-SNAPSHOT-jar-with-dependencies.jar).

Use MATLAB's ```javaaddpath``` command to add the lapis-matlab .jar file to the Java path within MATLAB. All necessary Java dependencies are packaged within this .jar file, so it will be the only .jar file you need in order to use LAPIS.

##### Instantiate a LapisAPI object

To start using LAPIS in your MATLAB application, you need to instantiate a ```LapisAPI``` object.

```Matlab
myNodeName = 'MATLAB-node'
myAddress = 'http://127.0.0.1:7777'
% create coordinator LAPIS node
lapisApi = LapisAPI(myNodeName, myAddress)
```

Note that the LapisAPI constructor above creates a coordinator node. If you intend to use LAPIS in a standalone application, and _not_ as part of a network of LAPIS nodes, you should use the constructor that creates a coordinator node.

##### Create a non-coordinator node

The first example of instantiating the ```LapisAPI``` object showed you how to set-up a coordinator node. To set up a non-coordinator node, use the constructor that takes three arguments:

```Matlab
myNodeName = 'non-coordinator-node'
coordinatorAddress = 'http://127.0.0.1:7777'
myAddress = 'http://127.0.0.1:8888'
lapisApi = LapisAPI(myNodeName, coordinatorAddress, myAddress)
```

Note that a non-coordinator node will immediately attempt to connect to the network coordinator at the specified coordinator address. An exception will be thrown if it is unable to connect.


##### Create a LAPISData object

To publish MATLAB variables in LAPIS, you must use ```LAPISData``` wrapper objects. Using standard MATLAB datatypes are not directly supported by LAPIS due to MATLAB's variable reference methodology.  

<!-- TODO: discuss the reason for this in the implementation details section -->

```Matlab
% instantiate a LAPISDAta object
anArray = LAPISData('anArray', [1 2 3 4 5])
```

```LAPISData``` objects have two fields. The first, ```.name```, is the name that LAPIS will use for the variable if it is published by the application. The value of the variable can be retrieved, by name, in the REST interface, and other nodes on a LAPIS network can get and set the variable using the published name and the name of the node on which the variable was published. In the code example above, the ```.name``` field of the instantiated ```LAPISData``` object is set to 'anArray'.

The second field of the ```LAPISData``` object is the ```.data``` field. This holds a reference to the current value of the variable. In the example code, the ```.data``` field is initially assigned an array with five elements.


##### Publish a variable

Use the LapisAPI instance you have already created to publish your LAPISData object:

```Matlab
anArray = LAPISData('anArray', [1 2 3 4 5])
% lapisApi is a previously-created LapisAPI object
lapisApi.publish(anArray)
```

The variable is now published, and can be retrieved and set through the REST interface and by other nodes on the network.

Currently, the MATLAB implementation of LAPIS supports scalars, and 1D, 2D, and 3D vectors.  Other datatypes, such as cell arrays, structs, and objects are not currently supported.  

##### Using a published variable

In your MATLAB code, use the ```.data``` field of the published ```LAPISData``` object to reference the value of the variable. The value returned by ```.data``` includes any changes that may have been made by the variable being set manually through LAPIS's REST interface or programmatically from another LAPIS node. When you set the value of ```.data``` from within your application, the new value is available through the REST interface and to other network nodes.


<!-- maybe add note that implementation details are elsewhere -->

```Matlab
anArray = LAPISData('anArray', [1 2 3 4 5])
lapisApi.publish(anArray)

% get the current value of the published variable
valueOfTheArray = anArray.data

% increment each element within the array
anArray.data = 1 + anArray.data

% change the value of the array
anArray.data = [-5 -4 -3 -2 -1]
```

Note that if the value of a published variable is assigned to another variable, and that second variable is subsequently modified, the value of the published variable does not change.


```Matlab
anArray = LAPISData('anArray', [1 2 3 4 5])
lapisApi.publish(anArray)

% this does NOT change the value of anArray.data
someValue = anArray.data
someValue = 1 + someValue
% someValue now [2 3 4 5 6]
% anArray.data is still [1 2 3 4 5]

% this DOES change the value of anArray.data
anArray.data = anArray.data + 1
% anArray.data is now [2 3 4 5 6]
```

Note that you should not re-assign the variable that refers to your published ```LAPISData``` object if the intention is to still use the variable on a LAPIS network.

```Matlab
% do not do this -- re-assigning a LAPISData variable
anArray = LAPISData('anArray', [1 2 3 4 5])
anArray = [5 6 7 8]

```

Re-assigning a  ```LAPISData``` variable, as in the example above, causes the LAPISData object to be overwritten, resulting in the loss of reference to the LAPISData object.  This will render the modification of the variable by the application programmer futile.  In order to allow the LAPIS API to keep the reference to the LAPISData object and subsequently allow the network to maintain a current record of the LAPIS variable data, the reassignment of data should occur by reference the ```.data``` property of the LAPISData object.

```Matlab
% do not do this -- re-assigning a LAPISData variable
anArray = LAPISData('anArray', [1 2 3 4 5])
anArray.data = [5 6 7 8]

```

<!--two problems. It prevents the application from changing the value of the published variable that is exposed through the REST interface (and, consequently, to other LAPIS nodes on the same network), and it prevents any changes made through the REST interface (or by other network nodes) from being seen in the MATLAB application. This happens because the reference to the published variable is lost.-->



##### Redact a published variable

To "un-publish" a published variable, use the ```redact``` method:

```Matlab
% lapisApi is an instance of LapisAPI
% lapisData is an instance of LAPISData that has already been published
lapisApi.redact(lapisData)
```

It is rare that applications will need to un-publish variables, so use of the ```redact``` method does not need to be used in all applications.


##### Get values of variables published by other nodes

When you have multiple nodes on a LAPIS network, you can retrieve the value of variables that have been published by other nodes. If you have a variable called _var1_ that has been published by a node called _node1_, you would retrieve the variable value like this.

```Matlab
% retrieve the value of var1, a variable that was published by node1
valueOfVar1 = lapisApi.get('node1', 'var1')
```
Note that the value returned by the ```get``` call is not a ```LAPISData``` object. It is equal to the value returned by a call to the ```.data``` field of the published ```LAPISData``` object on node1. It is most likely an array or matrix.

##### Set the value of variables published by other nodes

If a node has published a variable and it has _not_ been published as "read-only", then other LAPIS nodes on the same network can set the value of the variable.

```Matlab
% set the value of var1, a variable published by node1
valueToSet = magic(10)
lapisApi.set('node1', 'var1', valueToSet)
```

### Use in Java

LAPIS includes a Java client for incorporating steering in Java applications.

##### Dependencies

To use the LAPIS client in Java, you will need the lapis-java-client .jar file (as of the time of this writing, lapis-java-client-1.0-SNAPSHOT-jar-with-dependencies.jar).

##### Instantiate a LapisApi object

To start using LAPIS in your Java application, instantiate a ```edu.osu.lapis.LapisApi``` object:

```Java
// this creates a coordinator node
String myNodeName = "Java-node";
String myAddress = "http://localhost:7788";
LapisApi lapisApi = new LapisApi(myNodeName, myAddress);
```

##### Publish variables

As of this time, variables published through LAPIS's Java client must be one-, two-, or three-dimensional arrays of a Java primitives.

```Java
final int[] ints = new int[] {8, 6, 7, -5, 3, 0, 9};
//lapisApi is an instance of edu.osu.lapis.LapisApi
lapisApi.publish("publishedInts", ints);
```

In the example above, an array of integers is published with the name "publishedInts". Other LAPIS nodes on a network can access the present state of ```ints``` by using the published name of the variable.

Note that the recommend practice is to publish ```final``` variables. Published variables should not be re-assigned. Re-assigning a published variable causes two problems. It prevents the application from changing the value of the published variable that is exposed through the REST interface, and it prevents any changes made through the REST interface (either manually or programmatically by other nodes in a LAPIS network) from being seen within the Java application.

##### Redact variables

Most applications will have no need to un-publish variables, but if you wish to remove a previously-published variable, you can use the ```redact``` method:

```Java
//lapisApi is an instance of edu.osu.lapis.LapisApi
lapisApi.redact("nameOfPreviouslyPublishedVariable");
```

Note that the argument passed to the ```redact``` method is the _name_ of the published variable within LAPIS, not the published object itself.

##### Get and set variables published by other nodes

Applications within the same LAPIS network can get and set each other's published variables, as in the example below. Note that the application must refer to other nodes' published variables using the "full name" of the published variable, i.e. published name + ```@``` + node name. Note: This _WILL_ change in a future release.

```Java
// "doubles" is an array of doubles published by a node named "otherNode"
String variableName = "doubles";
String otherNodeName = "otherNode";
String variableFullName = variableName + '@' + otherNodeName;

// get the value of the published variable by referring to the "full name"
double[] localDoubles = lapisApi.getArrayOfDouble(variableFullName);

// make some change and set the value on the other node
for(int i = 0; i < localDoubles.length; ++i) {
    localDoubles[i] = Math.sqrt(localDoubles[i]);
}
lapisApi.set(variableFullName, localDoubles);
```

## Release 0.3 notes

This release includes some changes and new features, discussed below. Note that examples provided are for the MATLAB client. For the Java client, please look at the source code, unit tests, and Javadoc for the the lapis-java-client project, and at the examples provided earlier in this document.

##### Modified publish function

The function for publishing variables has changed so that you no longer need to specify a name string when calling the ```publish``` function. Internally, LAPIS will use the ```.name``` field of the ```LAPISData``` passed to the function as the published named of the variable. 

Here is an example:

```Matlab
finishFlag = LAPISData('finishFlag', [0]);
lapisApi.publish(finishFlag);
% the name of the published variable will be 'finishFlag'
```

For reference, this is how variables were published prior to this release:

```Matlab
% this is code for the previous approach -- do not do this anymore
finishFlag = LAPISData('finishFlag', [0]);
lapisApi.publish('finishFlag', finishFlag);
```

##### Multi-server functionality

It is now possible to build networks of LAPIS nodes across independent nodes on an IP network. When constructing your ```LapisAPI``` object in MATLAB, you will have to specify the externally visible address of your LAPIS node--that is, an address visible to other servers on your network (but not necessarily to the wider internet). So, whereas you might have used ```'http://127.0.0.1:7777'``` as the address of your LAPIS node before, now you'll want to use the address by which other servers can access your node, such as ```'http://192.168.1.2:7777'```.  For more information, please refer to literature regarding IP networking.

The following examples demonstrate the use of addresses that will be visible to other servers on the network:

```Matlab
% constructor to create coordinator node
myAddress = 'http://192.168.2.2:7777'; % note: myAddress is also the coordinator address in this example
nodeName = 'Node1';
lapisApi = LapisAPI(nodeName, myAddress);
```

```Matlab
% constructor to create non-coordinator node
myAddress = 'http://192.168.2.55:7777';
coordinatorAddress = 'http://192.168.2.2:7777';
nodeName = 'Node2';
lapisApi = LapisAPI(nodeName, coordinatorAddress, myAddress);
```

Note that the ```http://192.168.xxx.xxx``` address is a typical address assigned by a DHCP server running on a standard home-networking router such as a Linksys (Cisco), or Belkin.  Your subnet can be verified by checking the IP address of a computer that is connected to the same network on which your LAPIS network is intended to be configured.

##### Wait for nodes to declare themselves 'ready'

To facilitate coordination among multiple nodes in a LAPIS network, LAPIS now allows applications to pause while waiting for other nodes to join a network, perform any initialization processing, and then declare themselves ready.

To wait for another node to declare that it is ready, use the ```waitForReadyNode``` or ```waitForReadyNodeWithTimeout``` function, as in the example below:

```Matlab
% waits 10000 milliseconds--or 10 seconds--for 'Node1' to become 'ready'
lapisApi.waitForReadyNodeWithTimeout('Node1', 10000);

% waits indefinitely for 'Node2' to become 'ready'
lapisApi.waitForReadyNode('Node2');
```

A node does not have to already be present on the LAPIS network for another node to begin waiting for it. The ```waitForReadyNodeWithTimeout``` function blocks until the specified node has become ready or the timeout is reached. If the timeout is reached, an exception will be thrown, so catch the exception if you wish to continue processing in the event of a timeout. The ```waitForReadyNode``` function blocks indefinitely. It will not return until the specified node has joined the network and declared itself to be ready. Use of ```waitForReadyNodeWithTimeout``` should be preferred over use of ```waitForReadyNode```.

Nodes upon which other nodes are waiting will need to declare themselves ready using the ```ready``` function. This 'ready' state is visible to all other nodes on the same LAPIS network. Any node currently waiting on the node that becomes ready will continue processing (the ```waitForReadyNode``` or ```waitForReadyNodeWithTimeout``` function will return), and any node that subsequently attempts to wait on the 'ready' node will continue processing almost immediately.

Nodes which must no longer appear as 'ready' to the rest of the network should call the ```notReady``` function. Note that calling the ```notReady``` function when ```ready``` has not been called will have no effect. Similarly, if a node is already in the 'ready' state, further calls to ```ready``` have no effect.

Applications do not have to use the functionality that LAPIS provides for waiting on nodes and declaring nodes 'ready', but these features help to build networks of applications that work together.

##### Clearing a LAPIS network in MATLAB

Before clearing your LAPIS network variable in MATLAB by using the ```clear``` command, you must use the ```shutdown``` method in order to force MATLAB to clean up the network.  If this is not done, the instantiated LAPIS network reference will continue to exist and will not allow another network to be created on the same port.

To shutdown a LAPIS network, simply type

```Matlab
%shuts down the network
lapisApi.shutdown()

%continue with other clearing functionality, such as 'clear' ...
clear all;
close all;
clc;

```

## Release 0.4 notes

##### Client communication change and possible classpath conflict

The client-side communication code has been re-implemented using Apache HttpClient. This may require MATLAB users to make a configuration change in order to run LAPIS, depending on their current configuration. The following line from $matlabroot/toolbox/local/classpath.txt MUST be commented out (if it is present) or LAPIS will not work:

$matlabroot/java/jarext/axis2/httpcore.jar

To edit $matlabroot/toolbox/local/classpath.txt, do the following:

1. Select a text editor, right-click, and select 'Run as Administrator'.
2. In MATLAB, type the following to display the location of the classpath.txt file: ```[matlabroot '\toolbox\local\classpath.txt']```
3. Open the classpath.txt file in your editor from the first step.
4. Comment out the line that ends in 'httpcore.jar'
5. Save the file and exit the text editor.
6. Re-start MATLAB.

##### Java client change 

In the Java client in LAPIS 0.4, separate arguments are passed for the node name and variable name in get and set calls:

```Java
// get the "bytes" variable published by node "non-coor"
byte[] retrieved = coordinatorLapis.getArrayOfByte("non-coor", "bytes");

//set the value of variable "bools" published on node "non-coor"
boolean[] different = // create array...
coordinatorLapis.set("non-coor", "bools", different);
```

#### Debugging a LAPIS network 
Since LAPIS is designed to be easily accessible to the inexperienced programmer, accessing a LAPIS network using standard tools - such as a web browser - is included as part of the implementation. 

##### Using the REST interface
The LAPIS network is exposed through a RESTful API making it easily accessible for testing and verification.

For ```GET``` HTTP operations (which are the only operations shown below in the examples) it should be noted that you only need a web browser on a computer that has access to the same IP network to which your LAPIS network resides. 

For more sophisticated HTTP operations (such as ```PUT```, ```POST```, and ```DELETE```, many REST clients exist (desktop, web, and browser based).  The REST client that will be used as an example here can be downloaded as a [Google Chrome extension](https://chrome.google.com/webstore/detail/advanced-rest-client/hgmloofddffdnphfgcellkdfbfbjeloo?hl=en-US).  Note that you will need to run Google Chrome in order to download and use.  Please view the help of the extension for information on how to use it.

To view your network coordinator, either open up Advanced REST Client in Google Chrome or any web browser.  For the next examples, we will assume that the LAPIS network coordinator you are debugging is located at ```'http://localhost:7777'```, and that there are two nodes on the network located at ```'http://localhost:7777'``` and ```'http://localhost:8888'```.

To test whether or not your coordinator is running, enter ```http://localhost:7777/coordinator``` in the URL bar and perform a ```GET``` operation (for a browser, just execute the URL address).  If the coordinator is running, the server will respond with a ```200``` HTTP code and the following JSON

```JSON
[
  {
    "nodeName": "Node2",
    "url": "http://localhost:8888"
  },
  {
    "nodeName": "Node1",
    "url": "http://localhost:7777"
  }
]
```

To test for the existence of a node on a network, enter ```http://localhost:8888/network``` in the URL bar and perform a ```GET``` operation.  A JSON list will be returned such as:

```JSON
{
nodeName: "Node2"
url: "http://localhost:8888"
}
```

Let's assume that the node at ```http://localhost:7777``` has published a variable ```x``` that holds a 5 element vector and we need to check it's value on the LAPIS network.  To do this, enter ```http://localhost:7777/model/x``` in the URL test field and perform a ```GET``` operation.  If the variable exists, you should get the response:

```JSON
{
  "name": "x",
  "originalType": "[D",
  "data": [
    1.0,
    2.0,
    3.0,
    4.0,
    5.0
  ]
}
```

To view all published variables of the node at ```'http://localhost:8888'```, simply enter ```http://localhost:8888/metadata``` in the URL bar.  If the node exists, it will return a JSON string containing information about all the published variables:

```JSON
[
  {
    "name": "node1finish",
    "lapisPermission": "READ_WRITE"
  },
  {
    "name": "finishFlag",
    "lapisPermission": "READ_WRITE"
  },
  {
    "name": "simFinishFlag",
    "lapisPermission": "READ_WRITE"
  },
  {
    "name": "node2copy",
    "lapisPermission": "READ_WRITE",
    "type": "one dimensional array of double"
  },
  {
    "name": "ready",
    "lapisPermission": "READ_WRITE"
  }
]
```

We have only shown ```GET``` operations here, as other HTTP operations are more involved and do not normally need to be used in the REST client debugging.  If more sophisticated HTTP operations are required, please consult the Java Docs and Wiki for the full REST resource dictionary. 


##### Configurable logging

Logging can now be configured using the ```log4j.properties``` file that is included with the other MATLAB files for this release. In MATLAB, LAPIS locates this file in the current directory and uses it to configure logging whenever a ```LapisAPI``` object is instantiated. Instructions on the use of log4j can be found at http://logging.apache.org/log4j/1.2/manual.html, though most users will not have to make any changes to the log4j configuration.
