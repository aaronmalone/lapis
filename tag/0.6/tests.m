
% A sort of "unit test" script for LAPIS' MATLAB client.


% NOTE:
% the following line from $matlabroot/toolbox/local/classpath.txt MUST be commented out or LAPIS wll not work
% $matlabroot/java/jarext/axis2/httpcore.jar

% NOTE FOR LOGGING (optional):
% the following two lines from $matlabroot/toolbox/local/classpath.txt must be commented out (or logging won't work...it will give a WARN)
% #$matlabroot/java/jarext/jxbrowser/slf4j-api.jar
% #$matlabroot/java/jarext/jxbrowser/slf4j-log4j12.jar

clear all
clear classes
clear java

%% set up LAPIS

coordinatorAddress = 'http://127.0.0.1:7777';
node1Name = 'Node1';
node2Name = 'Node2';
node2Address = 'http://127.0.0.1:8888';

node1 = LapisAPI(node1Name, coordinatorAddress); %Node1 is the coordinator
node2 = LapisAPI(node2Name, coordinatorAddress, node2Address); 


%% publish array and retrieve value

node1Array = LAPISData('node1Array', [1 2 3 4 5]);
node1.publish(node1Array);
retrieved = node2.get(node1Name, 'node1Array');
equals = array_equals(node1Array.data, retrieved);
assert(equals, 'Retrieved array value was not as expected.');

%% modify published array and retrieve value
node1Array.data = rand(4);
retrieved = node2.get(node1Name, 'node1Array');
equals = array_equals(node1Array.data, retrieved);
assert(equals, 'Retrieved array value was not as expected.');

%% publish map and retrieve value
node2Map = LAPISMap('node2Map');
node2.publish(node2Map);
node2Map.set('rand3', rand(3));
node2Map.set('aString', 'qwerty');
retrievedMap = node1.get(node2Name, 'node2Map');
assert(isa(node2Map.get('rand3'), 'double'));
assert(isa(retrievedMap.rand3, 'double'), 'Type of value from map is not as expected');
%%

node1.shutdown;
node2.shutdown;