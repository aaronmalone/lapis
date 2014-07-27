classdef LapisAPI < handle
    %     LAPIS API object.  Responsible for connecting and maintaing a LAPIS
    %     network connection. Use this API to do SETs and GETs on a LAPIS
    %     network.  Depends on a LAPIS java JAR file.
    % EXAMPLE:
    % coordinatorAddress = 'http://127.0.0.1:7777';
    % nodeName = 'Node1';
    % lap = LapisAPI(nodeName, coordinatorAddress);
    
    
    
    properties
        lapisJava;          %Java LAPIS API
        modelName;          %Name of model
        coordinatorAddress; %Coordinator address
        modelAddress;       %Model port and address
        isCoordinator;      %Status of coordinator
    end
    
    
    methods
        
        function obj = LapisAPI(varargin)
            %Constructor.  If model is coordinator, use: Args(modelName, coordinatorAddress).  If model is not coordinator, use Args(modelName, coordinatorAddress, modelAddress)
            
            javaaddpath([pwd '\lapis-matlab-0.7-jar-with-dependencies.jar']);
            
            
            % set up logging
            java.lang.System.setProperty('line.separator',char(10)); %prevents double-spacing of log output
            org.apache.log4j.helpers.LogLog.setInternalDebugging(1);
            org.apache.log4j.PropertyConfigurator.configure([pwd char(java.lang.System.getProperty('file.separator')) 'log4j.properties']);
            org.apache.log4j.helpers.LogLog.setInternalDebugging(0);
            
            eval('import edu.osu.lapis.MatlabLapis');
           
            if nargin == 2  %Model is the coordinator
                obj.modelName = varargin{1};
                obj.coordinatorAddress = varargin{2};
                obj.modelAddress = obj.coordinatorAddress;
                obj.isCoordinator = true;
                
                obj.lapisJava = MatlabLapis(java.lang.String(obj.modelName), ...
                    java.lang.String(obj.coordinatorAddress));
                
            elseif nargin == 3   %Model is not coordinator
                obj.modelName = varargin{1};
                obj.coordinatorAddress = varargin{2};
                obj.modelAddress = varargin{3};
                obj.isCoordinator = false;
                
                obj.lapisJava = MatlabLapis(java.lang.String(obj.modelName), ...
                    java.lang.String(obj.coordinatorAddress), ...
                    java.lang.String(obj.modelAddress));
            else
                error('There is no Constructor signature with the specified number of parameters');
            end
        end
        
        
        function obj = publish(obj, data)
            obj.publishInternal(data, 0);
        end
        
        function obj = publishReadOnly(obj, data)
            obj.publishInternal(data, 1);
        end
        
        function obj = publishInternal(obj, data, readOnly)
            if ~isa(data, 'LAPISData') && ~isa(data, 'LAPISMap')
                error('Published datatype must be type "LAPISData" or "LAPISMap"');
            end
            
            data.setLapisReference(obj);
            
            if isa(data, 'LAPISData')
                if(readOnly == 1)
                    obj.lapisJava.publishReadOnly(data.name, data.data);
                else
                    obj.lapisJava.publish(data.name, data.data);
                end
            elseif isa(data, 'LAPISMap')
                if(readOnly == 1)
                    obj.lapisJava.publishNewReadOnlyMap(data.name);
                else
                    obj.lapisJava.publishNewMap(data.name);
                end
                
            end
            
        end
        
        function obj = redact(obj, data)
            % un-publish a varible
            obj.lapisJava.redact(data.name)
        end
                
        function obj = setCachedValue(obj, varName, data)    
%             Setter to put a value into the LAPIS cache
            obj.lapisJava.setCachedValue(varName, data);
        end
        
        function result = retrieveCachedValue(obj, varName)
%             Getter method to get a value from the LAPIS cache
           result = obj.lapisJava.retrieveCachedValue(varName); 
        end
        
        function obj = delete(obj)
            %Deletes the object.
            obj.shutdown;
        end
        
        function obj = set(obj,modelName, varName, data)
            %Sets a variable on another LAPIS node.  Args(modelName, variablename, data)
            
            if ~isa(data, 'double')
                % somebody who knows MATLAB can clean this up
                if ~isa(data, 'char')
                    if ~isa(data, 'struct')
                        error('Setting types other than double, char, or struct are not currently supported');
                    end
                end
            end
            
            fullName = [varName  '@'  modelName];
            
            
            if isa(data, 'struct')               
                map = java.util.HashMap();
                fnames = fieldnames(data);
                
                for i = 1:length(fnames)
                   map.put(fnames{i}, getfield(data, fnames{i}));
                end
                
                obj.lapisJava.set(fullName, map);
                
            else
                obj.lapisJava.set(fullName, data);
            end
            
            
            
            
        end
        
        function result = get(obj, modelName, varName)
            
            %Gets a variable on another LAPIS node.  Args(modelName, variablename, data)
            fullName = [varName  '@'  modelName];
            try
                result = obj.lapisJava.get(java.lang.String(fullName));
            catch e
                disp('There was an error getting the value.  Please try again.');
                result = obj.lapisJava.get(java.lang.String(fullName));
            end

            if isa(result, 'java.util.HashMap')
                mapResult = struct;
                iter = result.keySet.iterator;

                while iter.hasNext()
                    key = iter.next;
                    
                    if isa(result.get(key),'java.util.ArrayList')
                       mapResult = setfield(mapResult, key,  cell2mat(java.util.Vector(result.get(key)).toArray().cell));
                    else 
                       mapResult = setfield(mapResult, key,  result.get(key));
                    end
                    
                    
                end

                result = mapResult;
            end
        end
        
        function obj = shutdown(obj)
            %Shuts down the LAPIS nework.  This step is required if clearing variables.
           obj.lapisJava.shutdown();
        end
        
        function obj = ready(obj)
        %ready
        %  declares that this LAPIS node is now ready
        %
        %  The functions ready, notReady, waitForReadyNode, and
        %  waitForReadyNodeWithTimeout facilitate coordination among
        %  multiple nodes in a LAPIS network. It is not necessary for a
        %  node to declare that it is ready by using the ready function
        %  unless other nodes on the same network are or will be waiting
        %  for the node to become ready using either the waitForReadyNode
        %  or the waitForReadyNodeWithTimeout functions.
            obj.lapisJava.ready();
        end
        
        function obj = notReady(obj)
        %notReady
        %  declares that this LAPIS node is not ready
        %  
        %  The functions ready, notReady, waitForReadyNode, and
        %  waitForReadyNodeWithTimeout facilitate coordination among
        %  multiple nodes in a LAPIS network.
            obj.lapisJava.notReady();
        end
       
        function obj = waitForReadyNode(obj, nodeName)
        %waitForReadyNode
        %  waitForReadyNode(N) waits indefinitely for node N to become
        %  'ready' and does not time out
            obj.lapisJava.waitForReadyNode(nodeName);
        end
        
        function obj = waitForReadyNodeWithTimeout(obj, nodeName, millisToWait)
        %waitForReadyNodeWithTimeout
        %  waitForReadyNodeWithTimeout(N, M) waits for node N to become
        %  'ready' and times out with an exception after M milliseconds if
        %  node N has not become ready
            
            obj.lapisJava.waitForReadyNode(nodeName, millisToWait);
        end
        
    end

end