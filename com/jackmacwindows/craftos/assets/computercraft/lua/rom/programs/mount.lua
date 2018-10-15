local args = { ... }

if args[1] == "list" then
    local mounts = mounter.list()
    for k,v in pairs(mounts) do print(k .. " on " .. v) end
elseif args[1] == "mount" and args[3] ~= nil then
    mounter.mount(args[2], args[3], args[4] == "true")
elseif args[1] == "unmount" and args[2] ~= nil then
    mounter.unmount(args[2])
else
    term.setTextColor(colors.red)
    print("Usage: mount <mount|unmount|list> [name] [path]")
    term.setTextColor(colors.white)
end