import request from './request'

// 订单API服务
export const orderApi = {
  // 获取所有订单
  getAllOrders: () => {
    return request({
      url: '/orders',
      method: 'get'
    })
  },
  
  // 根据ID获取订单详情
  getOrderById: (id) => {
    return request({
      url: `/orders/${id}`,
      method: 'get'
    })
  },
  
  // 创建订单
  createOrder: (orderData) => {
    return request({
      url: '/orders',
      method: 'post',
      data: orderData
    })
  }
}