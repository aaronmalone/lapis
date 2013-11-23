classdef LapisAPI < handle
    
    
    properties
        
        dataTable;          %Datatable for local published variables
        lapisTimer;         %Interupt timer for LAPIS network
        lapisJava;          %Java LAPIS API
        modelName;          %Name of model
        coordinatorAddress; %Coordinator address
    end
    
    
    methods
        
        function obj = LapisAPI(modelName, coordinatorAddress, port, isCoordinator)
            
            obj.dataTable = containers.Map;
            
            obj.lapisTimer = timer('TimerFcn', @(event, data)lapisUpdate(obj));
            set(obj.lapisTimer, 'ExecutionMode', 'fixedRate');
            set(obj.lapisTimer, 'Period', 0.02);
            set(obj.lapisTimer, 'BusyMode', 'drop');
            set(obj.lapisTimer, 'ErrorFcn', @(event, data)timerErr(obj));
            
            
            
            import edu.osu.lapis.MatlabLapis;
            obj.lapisJava = MatlabLapis(java.lang.String(modelName), java.lang.String(coordinatorAddress), java.lang.String(port), java.lang.String(isCoordinator));
            
            
             start(obj.lapisTimer);
        end
        
        
        function obj = publish(obj, name, data)
            
            
            if ~isa(data, 'LAPISData')
                error('Published datatype must be type "LAPISData"');
            end
            
            
            obj.dataTable(name) = data;
            obj.lapisJava.publish(java.lang.String(name), data.data);
        end
        
        function obj = lapisUpdate(obj, varargin)
            
            
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
            
            
            %             keys = obj.dataTable.keys();
            %
            %
            %             for i = 1:length(keys)
            %
            %                 % Set the Java data first
            %                 if obj.dataTable(keys{i}).changeFlag
            %
            %                     data = obj.dataTable(keys{i}).data;
            %                     %%%
            %                     %%%
            %                     %set java lapis data
            %                      javaData = javaArray('java.lang.Double', size(data,1),size(data,2));
            %
            %
            %
            %                     for m = 1:size(data,1)
            %                         for n = 1:size(data,2)
            %                             javaData(m,n) = java.lang.Double(data(m,n));
            %                         end
            %                     end
            %
            %                     javaData;
            %
            %                     %%%
            %                     %%%
            %
            %
            %
            %                     %Resets the flag
            %                     t = obj.dataTable(keys{i});
            %                     t.changeFlag = 0;
            %                 end
            %
            %             end
            
            
            %%%
            %%%
            %%%
            %Check for new data from Java and change if needed
            %%%
            %%%
            %%%
            
            
        end
        
        
        function obj = timerErr(obj, varargin)
            
            warning(lasterr);
            start(obj.lapisTimer);
            
        end
        
        
        function obj = delete(obj)
           
            delete(obj.lapisTimer);
            
        end
        
        function obj = set(obj,modelName, varName, data)

           fullName = [varName  '@'  modelName];
           obj.lapisJava.set(fullName, data);
           
        end
        
        function result = get(obj, modelName, varName)
            
            fullName = [varName  '@'  modelName];
            result = obj.lapisJava.get(java.lang.String(fullName));
        end
        
        function obj = shutdown(obj)
           obj.lapisJava.shutdown
        end
        
    end

end