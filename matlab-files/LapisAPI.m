classdef LapisAPI < handle
%     LAPIS API object.  Responsible for connecting and maintaing a LAPIS
%     network connection. Use this API to do SETs and GETs on a LAPIS
%     network.  Depends on a LAPIS java JAR file.
% EXAMPLE:   
% coordinatorAddress = 'http://127.0.0.1:7777';
% nodeName = 'Node1';
% lap = LapisAPI(nodeName, coordinatorAddress); 



    properties
        
        dataTable;          %Datatable for local published variables
        lapisJava;          %Java LAPIS API
        modelName;          %Name of model
        coordinatorAddress; %Coordinator address
        modelAddress;       %Model port and address
        isCoordinator;      %Status of coordinator
    end
    
    
    methods
        
        function obj = LapisAPI(varargin)
            %Constructor.  If model is coordinator, use: Args(modelName, coordinatorAddress).  If model is not coordinator, use Args(modelName, coordinatorAddress, modelAddress)
            
            obj.dataTable = containers.Map;
            
            % set up logging
            java.lang.System.setProperty('line.separator',char(10)) %prevents double-spacing of log output
            org.apache.log4j.helpers.LogLog.setInternalDebugging(1)
            org.apache.log4j.PropertyConfigurator.configure([pwd char(java.lang.System.getProperty('file.separator')) 'log4j.properties'])
            org.apache.log4j.helpers.LogLog.setInternalDebugging(0)
            
            import edu.osu.lapis.MatlabLapis;
           
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
            %Publishes a variable.  Args(variableName, LapisDataObject).
            
            if ~isa(data, 'LAPISData')
                error('Published datatype must be type "LAPISData"');
            end

            data.setLapisReference(obj);
            
            obj.dataTable(data.name) = data;
            obj.lapisJava.publish(java.lang.String(data.name), data.data);
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
              error('Setting types other than doubles are not currently supported'); 
           end
           
           fullName = [varName  '@'  modelName];
           obj.lapisJava.set(fullName, data);
           
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
        end
        
        function obj = shutdown(obj)
            %Shuts down the LAPIS nework.  This step is required if clearing variables.
           obj.lapisJava.shutdown();
        end
        
    end

end