import axios from 'axios';

const baseUrl = 'http://localhost:8080/api/v1/';

const request = axios.create({
  baseURL: baseUrl,
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 5000, // request timeout
});

/* 请求拦截器 */
request.interceptors.request.use(
  (config) => {
    // 你可以在这里添加 token 或其他 headers
    // config.headers['Authorization'] = `Bearer YOUR_TOKEN`;
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);
/* 响应拦截器 */
request.interceptors.response.use(
  (response) => {
    // 处理正常返回的数据
    return response.data; // ✅ 这里要 return 数据
  },
  (error) => {
    // 处理请求错误
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);
export default request;
