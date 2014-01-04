## LAPIS

### About

LAPIS is a framework for incorporating computational steering in Java and MATLAB applications. It allows applications to expose variables through a REST interface so that the values of those variables can be retrieved and set at runtime by users or external processes. It also allows programmers to build networks of computationally-steered applications, each of which can be steered programmatically or manually through the REST interface exposed by each network node.

### LAPIS features

This is a high-level description of the features of the LAPIS framework. Each feature will be covered in more depth in the sections on using LAPIS in MATLAB and Java.

#### Publish variables

The core feature of the LAPIS framework is the ability to "publish" variables, which exposes the variables through LAPIS's REST interface. Within an application, published variables can then be used like regular variables (though they should not be re-assigned). The present values of published variables can be retrieved through LAPIS's REST interface, and the values of these variables can be set through the same interface. LAPIS will propagate the change to the application code in a way that is transparent to the application.

Applications can also publish variables as "read-only" within LAPIS. The values of these variables are still available to be retrieved, but cannot be set, through LAPIS's REST interface.

#### Redact variables

Variables which have been published can be un-published through LAPIS's redact method. Redacted variables can then be used like regular variables. They will no longer be exposed in LAPIS's REST interface.

#### LAPIS networks

LAPIS allows programmers to build networks of computationally-steered applications. This makes it simple to implement applications which programmatically steer each other.

#### Wait for ready network node

To facilitate coordination in networks of LAPIS nodes, LAPIS allows applications to pause while waiting for other nodes to join a network, perform their initialization processing, and declare themselves ready.