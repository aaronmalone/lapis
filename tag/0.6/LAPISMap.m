classdef LAPISMap < handle
%     LAPIS Map object.  This object must be used if publishing a map
%     to a LAPIS network.
    
    properties
        name;           %Name of the map variable on the LAPIS network.  Used by other nodes to get and set data
        data;           %Data of the variable
        lapReference;
    end

    methods

        function obj = LAPISMap(name)
            obj.name = name;
        end
        
        function obj = struct2Map(obj, data)
             fNames = fieldnames(data);
             for i = 1:length(fNames)
                 obj.lapReference.putInMap(obj.name, fNames{i}, getfield(a, fNames{i}));
             end
        end
        
        
        function obj = set(obj, key, value)
            try
                obj.lapReference.lapisJava.putInMap(obj.name, key, value);
            catch e
                 warning('Value was not set in LAPIS');
            end
            
        end
        
        function result = get(obj, key)   
%             Getter for the data property
            try
                 result = obj.lapReference.lapisJava.retrieveFromMap(obj.name, key);
            catch e
                warning('Value not retrieved from cache. This is normal when a variable is first published.');
                result = obj.data;
            end
        end
        
%         function result = getKeysInMap(obj)
%             result = obj.lapReference.
%             
%         end
        
               
        function obj = setLapisReference(obj, lap)
%             Sets the LAPIS reference in order to connect the reference to to a lapis network
            obj.lapReference = lap;
        end

        function result = display(obj)
                % Overridden display method for object
            disp(' ');
            
            %TODO: Add display of all keys in map
            
            disp('LAPISData Map : ');
            display(obj.data);
        end
        
        function result = length(obj)
            %Returns length of the data in the object
            result = length(obj.data);
            
        end

    end

end