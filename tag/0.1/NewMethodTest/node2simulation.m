%% Node 2 Simulation File.  Decrements 5 element vector once Node1 is finished incrementing the same vector.
%IMPORTANT:  IN ORDER FOR THIS FILE TO WORK, YOU MUST START node1simulation.m
%IN A DIFFERENT MATLAB WINDOW FIRST!!!!!!!!!!!!!!!!!!!

% NOTE FOR LOGGING (optional):
% the following two lines from $matlabroot/toolbox/local/classpath.txt must be commented out (or else logging won't work ...it will give a WARN)
% #$matlabroot/java/jarext/jxbrowser/slf4j-api.jar
% #$matlabroot/java/jarext/jxbrowser/slf4j-log4j12.jar

%BEWARE OF OVERPOLLING when using a lapis.get operation.  It will
%behave unexpectedly.  Use sets when possible (for current implementation)

delete(timerfindall) %Deletes all left over timers (for safety)
clear all
clear classes
clear java
javaaddpath([pwd '\lapis-core-1.0-SNAPSHOT-jar-with-dependencies.jar']);  %add the lapis jar file.  Future releases will include this in the LapisAPI class.


%% set up LAPIS
coordinatorAddress = 'http://127.0.0.1:7777';
nodeName = 'Node2';

lap = LapisAPI(nodeName, coordinatorAddress, 'http://127.0.0.1:8888');

x = LAPISData('node2copy', [1 2 3 4 5]);            %Starter counting vector
lap.publish('node2copy', x);

finishFlag = LAPISData('finishFlag', [0]);  %Local finish flag
lap.publish('finishFlag', finishFlag);

simFinishFlag = LAPISData('simFinishFlag', [0]);    %simulation finished flag
lap.publish('simFinishFlag', simFinishFlag);

ready = LAPISData('ready', [1]);    %simulation finished flag
lap.publish('ready', ready);

node2finish = LAPISData('node1finish', [0]);    %simulation finished flag
lap.publish('node1finish', node2finish);

%%
% Wait for Node1 to finish counting
while 1
%     disp('Waiting for node 1.');
    
    lap.get('Node1', 'x');
    
    
    if node2finish.data
        break;  %other node is ready
    end
%     lap.forceLapisUpdate();
     pause(1);
    
end

% Start the simulation
while 1
    
    x.data = x.data - 1;    %increment the array by 1
    disp(x.data);
%     pause(1);       %pause for 1 second
    
    if x(1) == 0
        
        lap.set('Node1', 'node2FinishFlag', 1);
        finishFlag.data = 1;
        
        break;
    end
end

disp('Done with my counting!')

%Use this below at your own risk.  There is a bug that is still being
%resolved here using GET operations when both models are in while loops and GETting.  Race condition!
% lap.forceLapisUpdate();
while 1
    lap.forceLapisUpdate();
    finish = lap.get('Node1', 'simFinishFlag');
    
    if finish
        disp('Done!');
        lap.shutdown();
        break;
    end
%     pause(0.02);
%     lap.forceLapisUpdate();
end


%Remember to shutdown LAPIS!  You have to manually for this example.
% lap.shutdown();
