import request from './request'

// 用户API服务
export const userApi = {
  // 获取所有用户
  getAllUsers: () => {
    return request({
      url: '/users',
      method: 'get'
    })
  },
  
  // 根据ID获取用户
  getUserById: (id) => {
    return request({
      url: `/users/${id}`,
      method: 'get'
    })
  },
  
  // 创建用户
  createUser: (userData) => {
    return request({
      url: '/users',
      method: 'post',
      data: userData
    })
  },
  
  // 更新用户
  updateUser: (id, userData) => {
    return request({
      url: `/users/${id}`,
      method: 'put',
      data: userData
    })
  },
  
  // 删除用户
  deleteUser: (id) => {
    return request({
      url: `/users/${id}`,
      method: 'delete'
    })
  }
}