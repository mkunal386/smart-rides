const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
  app.use(
    '/api', // The path you want to proxy
    createProxyMiddleware({
      target: 'http://localhost:8080', // The address of your backend
      changeOrigin: true,
    })
  );
};