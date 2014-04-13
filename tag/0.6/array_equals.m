function eq = array_equals( ar1, ar2 )
    if length(ar1) == length(ar2)
        eq = true;
        for i = 1:length(ar1)
            if ar1(i) ~= ar2(i)
               eq = false;
               break
            end
        end
    else 
       eq = false;
    end
end

