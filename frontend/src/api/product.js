import request from './request'

// 产品API服务
export const productApi = {
  // 获取所有产品
  getAllProducts: () => {
    return request({
      url: '/products',
      method: 'get'
    })
  },
  
  // 根据ID获取产品
  getProductById: (id) => {
    return request({
      url: `/products/${id}`,
      method: 'get'
    })
  },
  
  // 创建产品
  createProduct: (productData) => {
    return request({
      url: '/products',
      method: 'post',
      data: productData
    })
  },
  
  // 更新产品
  updateProduct: (id, productData) => {
    return request({
      url: `/products/${id}`,
      method: 'put',
      data: productData
    })
  },
  
  // 删除产品
  deleteProduct: (id) => {
    return request({
      url: `/products/${id}`,
      method: 'delete'
    })
  }
}