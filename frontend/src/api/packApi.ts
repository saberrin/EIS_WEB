import request from './request';

// 获取pack数据
export const GetPackMetrics = async (containerId: number, packId: number) => {
  return request({
    url: `/containers/${containerId}/pack-metrics/${packId}`,
    method: 'get',
  });
};

//首页集装箱级数据
export const GetPackMetricsBycontainer = async (containerId: number) => {
  return request({
    url: `/containers/${containerId}/pack-metrics`,
    method: 'get',
  });
};
// 获取单个电池数据
export const GetCellMetrics = async (
  containerId: number,
  packId: number,
  cellId: number,
  data: any
) => {
  return request({
    url: `/containers/${containerId}/packs/${packId}/cells-metrics/${cellId}`,
    method: 'get',
    params: data,
  });
};
//获取总览数据
export const GetOverViewData = async () => {
  return request({
    url: '/overview',
    method: 'get',
  });
};
//获取系统配置
export const GetSystemConfiguration = async () => {
  return request({
    url: '/system-configuration',
    method: 'get',
  });
};

export const exportAPI = async (data: any) => {
  return request({
    responseType: 'blob', // 这里设置响应类型为blob
    url: '/export-data',
    method: 'get',
    params: data,
  });
};
