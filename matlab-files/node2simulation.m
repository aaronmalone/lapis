clear all
clear all
delete(timerfindall)

javaaddpath C:\Users\Aaron\.m2\repository\edu\osu\lapis\lapis-core\1.0-SNAPSHOT\lapis-core-1.0-SNAPSHOT.jar
javaaddpath C:\Users\Aaron\.m2\repository\org\springframework\spring-beans\3.2.4.RELEASE\spring-beans-3.2.4.RELEASE.jar
javaaddpath C:\Users\Aaron\.m2\repository\org\springframework\spring-context\3.2.4.RELEASE\spring-context-3.2.4.RELEASE.jar
javaaddpath C:\Users\Aaron\.m2\repository\org\springframework\spring-core\3.2.4.RELEASE\spring-core-3.2.4.RELEASE.jar
javaaddpath C:\Users\Aaron\.m2\repository\org\springframework\spring-expression\3.2.4.RELEASE\spring-expression-3.2.4.RELEASE.jar
javaaddpath C:\Users\Aaron\.m2\repository\org\restlet\jse\org.restlet\2.0.0\org.restlet-2.0.0.jar
javaaddpath C:\Users\Aaron\.m2\repository\com\google\code\gson\gson\2.2.4\gson-2.2.4.jar
javaaddpath C:\Users\Aaron\.m2\repository\org\apache\commons\commons-lang3\3.1\commons-lang3-3.1.jar
javaaddpath C:\Users\Aaron\.m2\repository\org\restlet\jse\org.restlet.ext.slf4j\2.0.0\org.restlet.ext.slf4j-2.0.0.jar
javaaddpath C:\Users\Aaron\.m2\repository\com\google\guava\guava\15.0\guava-15.0.jar
javaaddpath C:\Users\Aaron\.m2\repository\com\google\code\gson\gson\2.2.4\gson-2.2.4.jar


%% set up LAPIS
coordinatorAddress = 'http://127.0.0.1:8888';
nodeName = 'Node2';

lap = LapisAPI(nodeName, coordinatorAddress, '9999', 'false');


x = LAPISData('x', [0 0 0 0 0]);            %Starter counting vector (will be set by Node 1)
simFlag = LAPISData('simFlag', [0]);  %Local finish flag
simFinishFlag = LAPISData('simFinishFlag', [0]);    %simulation finished flag
ready = LAPISData('ready', [1]);    %simulation finished flag
lap.publish('simFlag', simFlag);
lap.publish('ready', ready);
%%

% Wait for Node1 to notify of finish
while 1    
    if simFlag.data
        break;  %ready to begin counting (set by Node1)
    end
    pause(0.5);
end
    
% Start the simulation
while 1

    x.data = x.data + 1;    %increment the array by 1
    disp(x.data);
    pause(1);       %pause for 1 second

    if x(1) == 200
        lap.set('Node3', 'x', x)
        lap.set('Node3', 'simFlag', 1)
        break;  %finished with counting
    end
end

disp('Done with my counting!')
disp('waiting for others to finish');

%Wait for Node 3 to set the simulation finish flag
while simFinishFlag.data ~= 1
    pause(0.5);
end

disp('Simulation Finished!');


