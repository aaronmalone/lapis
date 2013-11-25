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
            
            obj.lapisTimer = timer('TimerFcn', @(event, data)lapisUpdate(obj));
            set(obj.lapisTimer, 'ExecutionMode', 'fixedRate');
            set(obj.lapisTimer, 'Period', 0.02);
            set(obj.lapisTimer, 'BusyMode', 'drop');
            set(obj.lapisTimer, 'ErrorFcn', @(event, data)timerErr(obj));
            
            import edu.osu.lapis.MatlabLapis;
            import edu.osu.lapis.LapisOperationType;
            
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

             start(obj.lapisTimer);
        end
        
        
        function obj = publish(obj, name, data)
            %Publishes a variable.  Args(variableName, LapisDataObject).
            
            if ~isa(data, 'LAPISData')
                error('Published datatype must be type "LAPISData"');
            end
            
            
            obj.dataTable(name) = data;
            obj.lapisJava.publish(java.lang.String(name), data.data);
        end
        
        function obj = lapisUpdate(obj, varargin)
            %Timer callback for lapis interupt handling
            
            hasOp = obj.lapisJava.hasOperation;
           
            if hasOp == 1
                op = obj.lapisJava.retrieveOperation;
                varName = char(op.getVariableName);
                
				import edu.osu.lapis.LapisOperationType;
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
        
        
        function obj = timerErr(obj, varargin)
            %Timer error function.  Restarts timer if there is a failure.
            warning(lasterr);
            start(obj.lapisTimer);
            
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
            result = obj.lapisJava.get(java.lang.String(fullName));
        end
        
        function obj = shutdown(obj)
            %Shuts down the LAPIS nework.  This step is required if clearing variables.
           obj.lapisJava.shutdown();
        end
        
    end

end