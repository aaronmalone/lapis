classdef LAPISData < handle
    
    properties
        
        name;
        data;
        changeFlag = 0;
    end
    
    
    
    methods
        
        
        function obj = LAPISData(name, data)
            obj.name = name;
            obj.data = data;
        end
        
        
        function obj = set.data(obj, value)
            
            disp(['the data set is ' num2str(value)]);
            
            obj.data = value;
            
        end
        
        
        
        function obj = subsasgn(obj, S, B)
            
            if strcmp(S.type, '()')
                
                if length(S.subs) == 1
                    
                    if ~isa(B, 'double')
                        error('Class must be double');
                    end
                    
                    obj.data(S.subs{1}) = B;
                    
                elseif length(S.subs) == 2
                    obj.data(S.subs{1}, S.subs{2}) = B;
                    
                else
                    error('Matrices greater than 2 dimensions is not supported');
                end
                
                obj.changeFlag = 1;
                
            elseif strcmp(S.type, '.')
                
                if strcmp(S.subs, 'changeFlag')
                    obj.changeFlag = B;
                elseif strcmp(S.subs, 'name')
                    obj.name = B;
                else
                    
                    if ~isa(B, 'double')
                        error('Class must be double');
                    end
                    
                    obj.data = B;
                    obj.changeFlag = 1;
                end
                
                
                
            else
                error('Indexing with anything other than () or direct assignment is not supported');
            end
            
            
            
            
            
            
        end
        
        function result = subsref(obj, S)
            
            if strcmp(S.type, '()')
                
                if length(S.subs) == 1
                    
                    result = obj.data(S.subs{1});
                    
                elseif length(S.subs) == 2
                    result = obj.data(S.subs{1}, S.subs{2});
                    
                else
                    error('Matrices greater than 2 dimensions is not supported');
                end
                
            elseif strcmp(S.type, '.')
                
                if strcmp(S.subs, 'changeFlag')
                    result = obj.changeFlag;
                elseif strcmp(S.subs, 'name')
                    result = obj.name;
                else
                    result = obj.data;
                end
                
            else
                error('Indexing with anything other than () or directly is not supported');
            end
            
            
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