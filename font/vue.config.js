module.exports = {
  devServer: {
    proxy: {
      '/api': {
        target: 'http://192.168.137.2:8080',  // 后端服务器地址
        changeOrigin: true,  // 修改源
        pathRewrite: { '^/api': '' },  // 重写路径
      },
    },
  },
};
