function handle(r)

    -- Debug logging { helpps me for debugging
    r:err("Handling request: " .. r.uri)
    
    -- if r.uri == "/favicon.ico": It handles the browser's request for a favicon. By setting r.status = 204 (No Content), it tells the browser there's no favicon to display and avoids unnecessary processing.
    if r.uri == "/favicon.ico" then
        r:err("Handling favicon.ico")
        r.status = 204  -- No Content
        return apache2.OK
    end
    
    -- if r.uri == "/": It serves a hardcoded HTML string for the homepage.
    if r.uri == "/" then
        r:err("Serving root landing page")
        r.content_type = "text/html"                --r.content_type = "text/html": Sets the response header to tell the browser it's receiving HTML content.
                                                    --r:puts([[...]]): This is a multi-line string literal in Lua, enclosed in [[...]]. r:puts() writes the string to the response body.

        r:puts([[
            <!DOCTYPE html>
            <html>                                                         
            <head>
                <title>Multi-Service Web App</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; }
                    h1 { color: #333; }
                    ul { list-style-type: none; padding: 0; }
                    li { margin: 10px 0; }
                    a { color: #0066cc; text-decoration: none; }
                </style>
            </head>
            <body>
                <h1>Multi-Service Web Application</h1>
                <p>Available services:</p>
                <ul>
                    <li><a href="/service1">Service 1</a></li>
                    <li><a href="/service2">Service 2</a></li>
                    <li><a href="/service3">Service 3</a></li>
                </ul>
            </body>
            </html>
        ]])
        return apache2.OK
    end
    
    -- Service routing logic
    -- local target = nil and local path = nil: It initializes two local variables to store the target service name and the path to be forwarded.
    local target = nil
    local path = nil
    
    
    --r.uri:match("^/service1"): This uses Lua's string pattern matching. ^ anchors the pattern to the beginning of the string. The code checks if the request URI starts with /service1, /service2, or /service3.
    if r.uri:match("^/service1") then     
        r:err("Routing to service1")
        target = "service1"
        path = r.uri:gsub("^/service1", "") or "/"   -- r.uri:gsub("^/service1", "")    If r.uri is /service1/api/data, path becomes /api/data
        if path == "" then path = "/" end
        
    elseif r.uri:match("^/service2") then
        r:err("Routing to service2")
        target = "service2"
        path = r.uri:gsub("^/service2", "") or "/"
        if path == "" then path = "/" end
        
    elseif r.uri:match("^/service3") then
        r:err("Routing to service3")
        target = "service3"
        path = r.uri:gsub("^/service3", "") or "/"
        if path == "" then path = "/" end
    end
    
    -- Forward to the appropriate service
    if target then
        local proxy_url = "http://" .. target .. ":8080" .. path            --constructs the full URL for the backend service.
        r:err("Proxying to: " .. proxy_url)
        
        -- Set up the proxy request
        r.handler = "proxy-server"                                           --r.handler = "proxy-server": This tells Apache's mod_lua to use the built-in proxy handler.
        r.proxyreq = apache2.PROXYREQ_REVERSE                                -- r.proxyreq = apache2.PROXYREQ_REVERSE: Specifies that this is a reverse proxy request.
        r.filename = "proxy:" .. proxy_url                                    --r.filename = "proxy:" .. proxy_url: This is the crucial part that passes the target URL to the Apache proxy handler.
        

        
        return apache2.DECLINED -- Let the proxy module handle it
    else
        -- No matching service
        r:err("No service match for: " .. r.uri)
        r.content_type = "text/html"
        r.status = 404
        r:puts([[
            <!DOCTYPE html>
            <html>
            <head>
                <title>404 Not Found</title>
            </head>
            <body>
                <h1>404 Not Found</h1>
                <p>The requested URL ]] .. r.uri .. [[ was not found on this server.</p>
                <p><a href="/">Return to homepage</a></p>
            </body>
            </html>
        ]])
        return apache2.OK
    end
end
