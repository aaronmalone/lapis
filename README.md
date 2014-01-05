## LAPIS

### About

LAPIS is a framework for incorporating computational steering in Java and MATLAB applications. It allows applications to expose variables through a REST interface so that the values of those variables can be retrieved and set at runtime by users or external processes. It also allows programmers to build networks of computationally-steered applications, each of which can be steered programmatically or manually through the REST interface exposed by each network node.

### LAPIS features overview

This is a high-level overview of the features of the LAPIS framework. Each feature will be covered in more depth in the sections on using LAPIS in MATLAB and Java.

##### Publish variables

The core feature of the LAPIS framework is the ability to "publish" variables, which exposes the variables through LAPIS's REST interface. Within an application, published variables can then be used like regular variables (though they should not be re-assigned). The present values of published variables can be retrieved through LAPIS's REST interface, and the values of these variables can be set through the same interface. LAPIS will propagate the change to the application code in a way that is transparent to the application.

Applications can also publish variables as "read-only" within LAPIS. The values of these variables are still available to be retrieved, but cannot be set, through LAPIS's REST interface.

<!--

##### Redact variables

Variables which have been published can be un-published through LAPIS's redact method. Redacted variables can then be used like regular variables. They will no longer be exposed in LAPIS's REST interface.

-->

##### Create LAPIS networks

LAPIS allows programmers to build networks of computationally-steered applications. This makes it simple to implement applications which programmatically steer each other. Nodes within a LAPIS network can get and set each other's published variables. LAPIS handles the details of inter-process communication, so that client code remains simple.

<!-- 
##### Wait for ready network node

To facilitate coordination among multiple nodes in a LAPIS network, LAPIS allows applications to pause while waiting for other nodes to join a network, perform their initialization processing, and declare themselves ready. 
-->

##### REST interface

As mentioned previously, LAPIS exposes the values of published variables through a REST interface. The values of published variables can be retrieved. The values can also be set if the variables have not been published as "read-only" within LAPIS. In addition to getting and setting variable values, LAPIS also exposes variable meta-data and network information through its REST interface. Further detail on the REST interface will be provided in a separate section.


### Creating LAPIS networks

LAPIS allows programmers to build networks of computationally steered applications. Each application within the network can access other applications' published variables.

To create a LAPIS network, you must first create a LAPIS coordinator node. The coordinator node within a LAPIS network is the node that is responsible for maintaining a record of all of the nodes on the network. This record is stored within the LAPIS framework, so the client does not need to be concerned about it. After the coordinator node has been created, you can add multiple non-coordinator nodes to the network.

There are three key details that client programmers must be aware of regarding coordinator and non-coordinator LAPIS nodes:
1. Coordinator nodes and non-coordinator nodes are created using different constructors for the LAPIS API object (LapisAPI in MATLAB, LapisApi in Java). The details for LAPIS API object creation are described in the sections on using LAPIS in MATLAB and Java. 
2. The coordinator node must be running before non-coordinator nodes can join the network. If a non-coordinator node starts up before the coordinator for its network is running, an exception will be thrown when the non-coordinator attempts to connect to the coordinator and subsequently fails.
3. Each LAPIS network has only one coordinator, but a single application can participate in multiple LAPIS networks.

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

##### Create a LAPISData object

To publish MATLAB variables in LAPIS, you must use ```LAPISData``` wrapper objects. 

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

##### Use a published variable

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

Note that you should not re-assign the variable that refers to your published ```LAPISData``` object.

```Matlab
% do not do this -- re-assigning a LAPISData variable
anArray = LAPISData('anArray', [1 2 3 4 5])
anArray = [5 6 7 8]

```

Re-assigning a  ```LAPISData``` variable, as in the example above, causes two problems. It prevents the application from changing the value of the published variable that is exposed through the REST interface (and, consequently, to other LAPIS nodes on the same network), and it prevents any changes made through the REST interface (or by other network nodes) from being seen in the MATLAB application. This happens because the reference to the published variable is lost.

It may also be worth noting that the re-assignment in the example above does _not_ change the value of the published variable. The value in the example remains ```[1 2 3 4 5]```.

##### Redact a published variable

To "un-publish" a published variable, use the ```redact``` method:

```Matlab
% lapisApi is an instance of LapisAPI
% lapisData is an instance of LAPISData that has already been published
lapisApi.redact(lapisData)
```

Note that most applications will not need to un-publish any variables, so use of the ```redact``` method should be rare.

##### Create a non-coordinator node

The first example of instantiating the ```LapisAPI``` object showed you how to set-up a coordinator node. To set up a non-coordinator node, use the constructor that takes three arguments:

```Matlab
myNodeName = 'non-coordinator-node'
coordinatorAddress = 'http://127.0.0.1:7777'
myAddress = 'http://127.0.0.1:8888'
lapisApi = LapisAPI(myNodeName, coordinatorAddress, myAddress)
```

Note that a non-coordinator node will immediately attempt to connect to the network coordinator at the specified coordinator address. An exception will be thrown if it is unable to connect.

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

Applications within the same LAPIS network can get and set each other's published variables, as in the example below. Note that the application must refer to other nodes' published variables using the "full name" of the published variable, i.e. published name + ```@``` + node name. This may change in a future release.

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
