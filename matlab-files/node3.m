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
nodeName = 'Node3';

lap = LapisAPI(nodeName, coordinatorAddress, '9988', 'false');