module.exports = {
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // 后端服务器地址
        changeOrigin: true,  // 修改源
        pathRewrite: { '^/api': '' },  // 重写路径
      },
    },
  },
};
