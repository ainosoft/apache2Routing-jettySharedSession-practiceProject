const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
  // Use 'development' for easy debugging.
  mode: 'development',

  // This setting improves debugging in the browser.
  devtool: 'eval-source-map',

  // Multiple entry points for login and dashboard
  entry: {
    index: './src/index.js',
    dashboard: './src/dashboard.js',
  },

  output: {
    filename: '[name].js',
    path: path.resolve(__dirname, 'dist'),
    clean: true,
  },

  
  devServer: {
    static: './dist',
    port: 9000, 
    open: true, 
    historyApiFallback: true,
    proxy: [
      {
        context: ['/UserService'],
        target: 'http://localhost:8084', // Your Java backend address
        changeOrigin: true,
        logLevel: 'debug',
      },
      {
        context: ['/BuilderService'],
        target: 'http://localhost:9003', // Builder backend address
        changeOrigin: true,
        logLevel: 'debug',
      },
      {
        context: ['/DeploymentManager'],
        target: 'http://localhost:9023', // DeploymentManager backend address
        changeOrigin: true,
        logLevel: 'debug',
      },
    ],
  },

  plugins: [
    // This plugin creates `index.html` from your login template and injects the JavaScript.
    new HtmlWebpackPlugin({
      template: './src/login.html',
      filename: 'index.html',
      chunks: ['index'],
    }),

    // This plugin creates `appops_dashboard.html` from your dashboard template.
    new HtmlWebpackPlugin({
      template: './src/appops_dashboard.html',
      filename: 'appops_dashboard.html',
      chunks: ['dashboard'], // Inject dashboard.js only
    }),

    // This plugin creates `org_details.html` for organization details page.
    new HtmlWebpackPlugin({
      template: './src/org_details.html',
      filename: 'org_details.html',
      chunks: [], // No JS for now
    }),
  ],

  module: {
    rules: [
      {
        test: /\.css$/i,
        use: ['style-loader', 'css-loader'],
      },
    ],
  },
}