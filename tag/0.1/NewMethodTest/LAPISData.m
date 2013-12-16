classdef LAPISData < handle
%     LAPIS data object.  This object must be used if publishing a variable
%     to a LAPIS network.  The object has two fields:  data and name.  In
%     order to access the data or set the data to this object, the "data"
%     field must be used.
% EXAMPLE:
%    x = LAPISData('x', [1 2 3 4 5]);            
%    x.data = [5 6 7 8 9];
%    y = x.data;
    
    properties
        name;           %Name of the variable on the LAPIS network.  Used by other nodes to get and set data
        data;           %Data of the variable
        lapReference;
    end

    methods

        function obj = LAPISData(name, data)
            %Constructor.  args(name, data)
            obj.name = name;
            obj.data = data;
        end
                
        function obj = set.data(obj, value)
%             Setter for data property.
            try
                obj.lapReference.setCachedValue(obj.name, value);
                obj.data = value;
            catch e
                warning('Value was not set in LAPIS');
                obj.data = value;
            end
            
        end
        
        function result = get.data(obj)   
%             Getter for the data property
            try
                 result = obj.lapReference.retrieveCachedValue(obj.name);
            catch e
                warning('Value was not gotten');
                result = obj.data;
            end
        end
        
               
        function obj = setLapisReference(obj, lap)
%             Sets the LAPIS reference in order to connect the reference to to a lapis network
            obj.lapReference = lap;
        end

        function result = display(obj)
                % Overridden display method for object
            disp(' ');
            disp('LAPISData Object : ');
            display(obj.data);
        end
        
        function result = length(obj)
            %Returns length of the data in the object
            result = length(obj.data);
            
        end
        
        function result = size(obj)
            %Returns size of the data in the object
            result = size(obj.data);
            
        end

    end

end