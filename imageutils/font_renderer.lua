local _swChar = function(x, y, b, f, c)
    if not term.getGraphicsMode() then error("cannot use software fonts in text mode", 2) end
    local cv = term.native().currentFont[string.byte(c)]
    for py = 0, term.native().currentFont.height-1 do
        for px = 0, term.native().currentFont.width do
            --os.debug(tostring(f))
            if bit.band(cv[py], bit.blshift(1, px)) ~= 0 then term.setPixel(x + px, y + py, f)
            else term.setPixel(x + px, y + py, b) end

        end
    end
end

write = function(text)
    if not term.getGraphicsMode() then error("cannot use software fonts in text mode", 2) end
    local w, h = term.getSize()
    if term.native().currentFont.yPos > h * 9 then return end
    for s in string.gmatch(text, ".") do
        if term.native().currentFont.xPos + term.native().currentFont.width > w * 6 then
            term.native().currentFont.xPos = 0
            term.native().currentFont.yPos = term.native().currentFont.yPos + term.native().currentFont.height
            if term.native().currentFont.yPos > h * 9 then return end
        end
        _swChar(term.native().currentFont.xPos, term.native().currentFont.yPos, term.getBackgroundColor(), term.getTextColor(), s)
        term.native().currentFont.xPos = term.native().currentFont.xPos + term.native().currentFont.width
    end
end

blit = function(text, bg, fg)
    if not term.getGraphicsMode() then error("cannot use software fonts in text mode", 2) end
    local w, h = term.getSize()
    if term.native().currentFont.yPos > h * 9 then return end
    for i = 1, string.len(text) + 1 do
        if term.native().currentFont.xPos + term.native().currentFont.width > w * 6 then
            term.native().currentFont.xPos = 0
            term.native().currentFont.yPos = term.native().currentFont.yPos + term.native().currentFont.height
            if term.native().currentFont.yPos > h * 9 then return end
        end
        local pbg = bit.blshift(1, string.find("0123456789abcdef", bg[i]) - 1)
        local pfg = bit.blshift(1, string.find("0123456789abcdef", fg[i]) - 1)
        _swChar(term.native().currentFont.xPos, term.native().currentFont.yPos, pbg, pfg, text[i])
        term.native().currentFont.xPos = term.native().currentFont.xPos + term.native().currentFont.width
    end
end

setCursorPos = function(x, y)
    if not term.getGraphicsMode() then error("cannot use software fonts in text mode", 2) end
    term.native().currentFont.xPos = x
    term.native().currentFont.yPos = y
end

getCursorPos = function()
    if not term.getGraphicsMode() then error("cannot use software fonts in text mode", 2) end
    return term.native().currentFont.xPos, term.native().currentFont.yPos
end

term.loadFontFile()