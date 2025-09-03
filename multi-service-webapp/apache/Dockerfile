FROM httpd:2.4.58

# Update system packages and install dependencies
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y --no-install-recommends \
        curl \
        lua5.3 \
        liblua5.3-dev \
        dnsutils \
        build-essential \
        apache2-dev && \
    apt-get autoremove -y && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Security hardening
RUN echo "ServerTokens Prod" >> /usr/local/apache2/conf/httpd.conf && \
    echo "ServerSignature Off" >> /usr/local/apache2/conf/httpd.conf && \
    echo "LoadModule headers_module modules/mod_headers.so" >> /usr/local/apache2/conf/httpd.conf && \
    echo "Header always set X-Content-Type-Options nosniff" >> /usr/local/apache2/conf/httpd.conf && \
    echo "Header always set X-Frame-Options DENY" >> /usr/local/apache2/conf/httpd.conf && \
    echo "Header always set X-XSS-Protection \"1; mode=block\"" >> /usr/local/apache2/conf/httpd.conf

# Enable required modules
RUN sed -i 's/^#\(LoadModule proxy_module\)/\1/' /usr/local/apache2/conf/httpd.conf && \
    sed -i 's/^#\(LoadModule proxy_http_module\)/\1/' /usr/local/apache2/conf/httpd.conf && \
    sed -i 's/^#\(LoadModule rewrite_module\)/\1/' /usr/local/apache2/conf/httpd.conf

# Ensure mod_lua is loaded correctly
COPY mod_lua.conf /usr/local/apache2/conf/extra/mod_lua.conf
RUN echo "Include conf/extra/mod_lua.conf" >> /usr/local/apache2/conf/httpd.conf

# Create a directory for Lua scripts
RUN mkdir -p /usr/local/apache2/conf/lua

# Configuration
RUN echo "ServerName localhost" >> /usr/local/apache2/conf/httpd.conf && \
    echo "Include conf/extra/httpd-vhosts.conf" >> /usr/local/apache2/conf/httpd.conf

# Copy configuration files
COPY url_mapper.lua /usr/local/apache2/conf/lua/
COPY httpd-vhosts.conf /usr/local/apache2/conf/extra/

# Create a test index file
RUN echo "<html><body><h1>Apache is running</h1></body></html>" > /usr/local/apache2/htdocs/index.html

CMD ["httpd-foreground"]
