# Reverse proxy of production (module N2a). The active colour lives in /etc/nginx/upstream/active.conf,
# on a named volume, so that a switch survives a restart. Blue is the default on the very first start.
FROM nginx:1.31.8-alpine3.24
COPY default.conf /etc/nginx/conf.d/default.conf
CMD ["sh", "-c", "[ -f /etc/nginx/upstream/active.conf ] || echo 'upstream app { server app-blue:8080; }' > /etc/nginx/upstream/active.conf; exec nginx -g 'daemon off;'"]
