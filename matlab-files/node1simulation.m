%% Node1 simulation file.  Increments a vector and then waits for Node2 to decrement the same vector.

%YOU MUST RUN THIS FILE BEFORE RUNNING node2simulation.m

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
javaaddpath([pwd '\lapis-matlab-0.4-jar-with-dependencies.jar']);   %add the lapis jar file.  Future releases will include this in the LapisAPI class.

%% set up LAPIS
coordinatorAddress = 'http://127.0.0.1:7777';
nodeName = 'Node1';

lap = LapisAPI(nodeName, coordinatorAddress);   %Node1 is the coordinator

x = LAPISData('x', [1 2 3 4 5]);            %Starter counting vector
lap.publish(x);

finishFlag = LAPISData('finishFlag', [0]);  %Local finish flag
lap.publish(finishFlag);

simFinishFlag = LAPISData('simFinishFlag', [0]);    %overall simulation finished flag (node 1 acts as the master)
lap.publish(simFinishFlag);

node2FinishFlag = LAPISData('node2FinishFlag', [0]);    %node2finish finished flag
lap.publish(node2FinishFlag);

readOnlyVar = LAPISData('readOnlyVar', [7 7 7]);
lap.publishReadOnly(readOnlyVar);

lap.ready();

%%
    
% Start the simulation
while 1
    
     x.data = x.data + 1;    %increment the array by 1
     disp(x.data);
     pause(0.25);

    if x.data(1) > 10

        finishFlag.data = 1;
        
        break;
    end
end

disp('Done with my counting!')
disp('waiting for Node 2 to finish');

%Wait for Node 2 to set the simulation finish flag
while node2FinishFlag.data ~= 1
    disp('Waiting for Node 2 to set node2FinishFlag (a published variable in this node)...')
    pause(1.5);
end

disp('Getting node2copy...')
node2Copy = lap.get('Node2', 'node2copy');

disp('Node2 copy: ');
disp(node2Copy)

simFinishFlag.data = 1;

lap.redact(finishFlag);
lap.redact(node2FinishFlag);

disp('Simulation Finished!');

%Make sure you shut down LAPIS before clearing it!
% lap.shutdown();

