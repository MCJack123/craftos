local args = { ... }

if args[1] == "list" then
    local settings = config.list()
    for k,v in pairs(settings) do
        print(v .. " = " .. tostring(config.get(v)))
    end
elseif args[1] == "get" and args[2] ~= nil then
    local setting = config.get(args[2])
    if setting == nil then error("Unknown setting " .. args[2])
    else print(tostring(setting)) end
elseif args[1] == "set" and args[3] ~= nil then
    local type = config.getType(args[2])
    if type == 0 then config.set(args[2], args[3] == "true" or args[3] == "1")
    elseif type == 1 then config.set(args[2], args[3])
    elseif type == 2 then config.set(args[2], tonumber(args[3]))
    else error("Unknown setting " .. args[2]) end
else
    term.setTextColor(colors.red)
    print("Usage: config <get|set|list> [setting] [value]")
    term.setTextColor(colors.white)
end