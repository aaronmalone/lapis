classdef LapisAPI < handle
    
    
    properties
        
        dataTable;          %Datatable for local published variables
        lapisTimer;         %Interupt timer for LAPIS network
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
            
%             obj.lapisTimer = timer('TimerFcn', @(event, data)lapisUpdate(obj));
%             set(obj.lapisTimer, 'ExecutionMode', 'fixedRate');
%             set(obj.lapisTimer, 'Period', 0.5);
%             set(obj.lapisTimer, 'BusyMode', 'drop');
%             set(obj.lapisTimer, 'ErrorFcn', @(event, data)timerErr(obj));
            
           
           
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

%              start(obj.lapisTimer);
        end
        
        
        function obj = publish(obj, name, data)
            %Publishes a variable.  Args(variableName, LapisDataObject).
            
            if ~isa(data, 'LAPISData')
                error('Published datatype must be type "LAPISData"');
            end

            data.setLapisReference(obj);
            
            obj.dataTable(name) = data;
            obj.lapisJava.publish(java.lang.String(name), data.data);
        end
        
        function obj = forceLapisUpdate(obj)
%             stop(obj.lapisTimer);
%             obj.lapisUpdate();
%             start(obj.lapisTimer);
        end
        
        
        function obj = setCachedValue(obj, varName, data)            
            obj.lapisJava.setCachedValue(varName, data);
        end
        
        function result = retrieveCachedValue(obj, varName)
           result = obj.lapisJava.retrieveCachedValue(varName); 
        end
        
        
        function obj = lapisUpdate(obj, varargin)
            %Timer callback for lapis interupt handling
            
            hasOp = obj.lapisJava.hasOperation;
            disp(['I got an operation signal: ' num2str(hasOp)]); 
            if hasOp == 1
                
               
                
                op = obj.lapisJava.retrieveOperation
                
                 disp(['I got an operation: ']); 
                
                varName = char(op.getVariableName);
                
				
                if op.getOperationType == LapisOperationType.GET
                    obj.lapisJava.operationResult(op, obj.dataTable(varName).data);
                    
                else
                    %SET%
                    handl = obj.dataTable(varName);
                    handl.data = op.getData;
                    
                    obj.lapisJava.operationResult(op, 1);
                    
                end
            end

        end
        
        
        function result = checkForGETOperation(obj, varName)
            
            hasOp = obj.lapisJava.hasOperation;
            
            if hasOp == 1

                op = obj.lapisJava.retrieveOperation
                
                disp(['I got an operation ']); 
                
                networkVarName = char(op.getVariableName);
                
				
                if op.getOperationType == LapisOperationType.SET && strcmp(char(networkVarName), varName)
                    
                    %SET%
                    handl = obj.dataTable(varName);
                    handl.data = op.getData;
                    
                    result = op.getData;
                    
                    obj.lapisJava.operationResult(op, 1);
                else
                    
                    result = [];
                end
            else
                
                result = [];
                
            end
            
        end
        
        
        function obj = timerErr(obj, varargin)
            %Timer error function.  Restarts timer if there is a failure.
            warning(lasterr);
%             start(obj.lapisTimer);
            
        end
        
        
        function obj = delete(obj)
           %Deletes the object.
           
            obj.shutdown;
            delete(obj.lapisTimer);
            
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
                obj.forceLapisUpdate();
                result = obj.lapisJava.get(java.lang.String(fullName));
            end
        end
        
        function obj = shutdown(obj)
            %Shuts down the LAPIS nework.  This step is required if clearing variables.
           obj.lapisJava.shutdown();
        end
        
    end

end