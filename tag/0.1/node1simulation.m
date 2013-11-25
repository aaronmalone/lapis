%% Node1 simulation file.  Increments a vector and then waits for Node2 to decrement the same vector.

%YOU MUST RUN THIS FILE BEFORE RUNNING node2simulation.m

% NOTE FOR LOGGING (optional):
% the following two lines from $matlabroot/toolbox/local/classpath.txt must be commented out (or logging won't work...it will give a WARN)
% #$matlabroot/java/jarext/jxbrowser/slf4j-api.jar
% #$matlabroot/java/jarext/jxbrowser/slf4j-log4j12.jar


%BEWARE OF OVERPOLLING when using a lapis.get operation.  It will
%behave unexpectedly.  Use sets when possible (for current implementation)

delete(timerfindall)  %Deletes all left over timers (for safety)
clear all
clear classes
clear java
javaaddpath([pwd '\lapis-core-1.0-SNAPSHOT-jar-with-dependencies.jar']);   %add the lapis jar file.  Future releases will include this in the LapisAPI class.

%% set up LAPIS
coordinatorAddress = 'http://127.0.0.1:7777';
nodeName = 'Node1';

lap = LapisAPI(nodeName, coordinatorAddress);   %Node1 is the coordinator

x = LAPISData('x', [1 2 3 4 5]);            %Starter counting vector
lap.publish('x', x);

finishFlag = LAPISData('finishFlag', [0]);  %Local finish flag
lap.publish('finishFlag', finishFlag);

simFinishFlag = LAPISData('simFinishFlag', [0]);    %overall simulation finished flag (node 1 acts as the master)
lap.publish('simFinishFlag', simFinishFlag);

node2FinishFlag = LAPISData('node2FinishFlag', [0]);    %node2finish finished flag
lap.publish('node2FinishFlag', node2FinishFlag);


node2status = LAPISData('node2status', [0]);    %simulation finished flag
lap.publish('node2status', node2status);



%%
    
% Start the simulation
while 1
    
    x.data = x.data + 1;    %increment the array by 1
    disp(x.data);
    pause(1);       %pause for 1 second

    if x(1) == 10

        lap.set('Node2', 'node2copy', x.data);
        lap.set('Node2', 'node1finish', 1);
        finishFlag.data = 1;
        
        break;
    end
end

disp('Done with my counting!')
disp('waiting for Node 2 to finish');

%Wait for Node 2 to set the simulation finish flag
while node2FinishFlag.data ~= 1
    pause(0.5);
end

node2Copy = lap.get('Node2', 'node2copy');

disp('Node2 copy: ');
disp(node2Copy)
disp('Simulation Finished!');
simFinishFlag.data = 1;


lap.shutdown();

