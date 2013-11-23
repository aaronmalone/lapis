%%

% NOTE:
% the following two lines from classpath.txt must be commented out (or
% logging won't work)

% #$matlabroot/java/jarext/jxbrowser/slf4j-api.jar
% #$matlabroot/java/jarext/jxbrowser/slf4j-log4j12.jar

delete(timerfindall)
clear all
clear classes
clear java
javaaddpath C:\Users\Aaron\Documents\MATLAB\lapis-core-1.0-SNAPSHOT-jar-with-dependencies.jar

%% set up LAPIS
coordinatorAddress = 'http://127.0.0.1:7777';
nodeName = 'Node1';

lap = LapisAPI(nodeName, coordinatorAddress, '7777', 'true');

x = LAPISData('x', [1 2 3 4 5]);            %Starter counting vector
lap.publish('x', x);

finishFlag = LAPISData('finishFlag', [0]);  %Local finish flag
lap.publish('finishFlag', finishFlag);

simFinishFlag = LAPISData('simFinishFlag', [0]);    %simulation finished flag
lap.publish('simFinishFlag', simFinishFlag);

%%
% Wait for models to be ready
while 1
        node2status = lap.get('Node2', 'ready');
        
        if node2status 
            break;  %other node is ready
        end
    
    pause(0.5);
    
end
    
% Start the simulation
while 1
    
    x.data = x.data + 1;    %increment the array by 1
    disp(x.data);
    pause(1);       %pause for 1 second

    if x(1) == 10

        lap.set('Node2', 'node2copy', x.data);
        finishFlag.data = 1;
        
        break;
    end
end

disp('Done with my counting!')
disp('waiting for Node 2 to finish');

%Wait for Node 2 to set the simulation finish flag
while simFinishFlag.data ~= 1
    pause(0.5);
end

node2Copy = lap.get('Node2', 'node2copy');

disp('Node2 copy: ');
disp(node2Copy)
disp('Simulation Finished!');

