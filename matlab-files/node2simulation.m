%% Node 2 Simulation File.  Decrements 5 element vector once Node1 is finished incrementing the same vector.
%IMPORTANT:  IN ORDER FOR THIS FILE TO WORK, YOU MUST START node1simulation.m
%IN A DIFFERENT MATLAB WINDOW FIRST!!!!!!!!!!!!!!!!!!!

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
javaaddpath([pwd '\lapis-matlab-0.4-SNAPSHOT-jar-with-dependencies.jar']);  %add the lapis jar file.  Future releases will include this in the LapisAPI class.


%% set up LAPIS
coordinatorAddress = 'http://127.0.0.1:7777';
nodeName = 'Node2';

lap = LapisAPI(nodeName, coordinatorAddress, 'http://127.0.0.1:8888');

x = LAPISData('node2copy', [1 2 3 4 5]);            %Starter counting vector
lap.publish(x);

%%

lap.waitForReadyNodeWithTimeout('Node1', 10000);

% Wait for Node1 to finish counting
while 1
    disp('Waiting for node 1...');
    if lap.get('Node1', 'finishFlag')        
        x.data = lap.get('Node1', 'x');
        break;  %other node is ready
    end
    pause(1);
    
end

% Start the simulation
while 1
    
    x.data = x.data - 1;    %decrement the array by 1
    disp(x.data);
    pause(0.25);
    
    if x.data(1) < 0
        
        lap.set('Node1', 'node2FinishFlag', 1);
        
        break;
    end
end

disp('Done with my counting!')


while 1
    finish = lap.get('Node1', 'simFinishFlag');
    if finish
        disp('Done!');
        break;
    end
    disp('Waiting for Node 1 to set simFinishFlag (a published variable on Node 1)...')
    pause(1)
end


%Remember to shutdown LAPIS!  You have to manually for this example.
% lap.shutdown();
