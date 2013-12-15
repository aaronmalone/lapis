classdef LAPISData < handle
    
    properties
        name;
        data;
        changeFlag = 0;
        lapReference;
    end

    methods

        function obj = LAPISData(name, data)
            obj.name = name;
            obj.data = data;
        end
                
        function obj = set.data(obj, value)                     
            try
                obj.lapReference.setCachedValue(obj.name, value);
                obj.data = value;
            catch e
                warning('Value was not set in LAPIS');
                obj.data = value;
            end
            
        end
        
        function result = get.data(obj)           
            try
                 result = obj.lapReference.retrieveCachedValue(obj.name);
            catch e
                warning('Value was not gotten');
                result = obj.data;
            end
        end
        
               
        function obj = setLapisReference(obj, lap)
            
            obj.lapReference = lap;
            
        end

        function result = display(obj)
            disp(' ');
            disp('LAPISData Object : ');
            display(obj.data);
        end
        
        function result = length(obj)
            
            result = length(obj.data);
            
        end
        
        function result = size(obj)
            
            result = size(obj.data);
            
        end
        
        
        
        
    end
    
    
    
end