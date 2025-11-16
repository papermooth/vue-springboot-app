# Harbor 镜像仓库配置指南

本文档提供了如何在Jenkins流水线中配置Harbor镜像仓库认证和推送流程的说明。

## 1. Harbor 仓库准备

在开始之前，请确保您已经：

- 搭建了Harbor镜像仓库服务器
- 创建了项目 `vue-springboot`
- 准备了具有推送权限的Harbor账户

## 2. Jenkins 凭据配置

在Jenkins中配置Harbor访问凭据：

1. 登录Jenkins管理界面
2. 导航至：`凭据管理` > `系统` > `全局凭据`
3. 点击 `添加凭据`
4. 选择 `Username with password` 类型
5. 填写以下信息：
   - Username: Harbor用户名
   - Password: Harbor用户密码
   - ID: `harbor-credentials` (必须与Jenkinsfile中配置的一致)
   - Description: Harbor镜像仓库凭据
6. 点击 `确定` 保存

## 3. Jenkins 全局配置

设置Harbor相关的全局变量：

1. 导航至：`系统管理` > `系统配置` > `全局属性`
2. 勾选 `环境变量`
3. 添加以下环境变量：
   - `HARBOR_URL`: 您的Harbor仓库地址 (例如: `https://harbor.example.com`)

## 4. 镜像命名规范

在流水线中，我们使用以下命名规范：

- 后端镜像: `${HARBOR_URL}/${HARBOR_PROJECT}/vue-springboot-backend:${BUILD_NUMBER}`
- 前端镜像: `${HARBOR_URL}/${HARBOR_PROJECT}/vue-springboot-frontend:${BUILD_NUMBER}`
- 同时还会创建 `latest` 标签指向最新构建

## 5. 推送流程说明

Jenkinsfile中的推送流程：

1. 使用 `withCredentials` 插件获取Harbor凭据
2. 使用 `docker login` 命令登录Harbor
3. 推送带构建号标签的镜像
4. 推送latest标签的镜像

## 6. 常见问题排查

### 推送失败

1. 检查Harbor凭据是否正确
2. 确保Harbor服务器可以从Jenkins服务器访问
3. 验证Harbor用户是否有该项目的推送权限
4. 检查Docker守护进程是否运行正常

### TLS/SSL 问题

如果Harbor使用自签名证书，需要在Jenkins服务器上配置：

```bash
# 将Harbor证书复制到Docker证书目录
mkdir -p /etc/docker/certs.d/${HARBOR_URL}
cp harbor.crt /etc/docker/certs.d/${HARBOR_URL}/ca.crt

# 重启Docker服务
# 注意：在Jenkins容器中可能需要特殊处理
```

## 7. 安全最佳实践

- 定期更新Harbor凭据
- 使用最小权限原则配置Harbor用户权限
- 考虑启用镜像签名验证
- 配置适当的镜像保留策略，避免存储过多旧镜像