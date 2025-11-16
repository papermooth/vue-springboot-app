# Vue-SpringBoot CI/CD 流水线文档

本文档详细介绍了Vue-SpringBoot应用的CI/CD流水线配置，包括自动化构建、代码质量检测、镜像推送和Kubernetes部署的完整流程。

## 1. 流水线概述

本CI/CD流水线使用Jenkins实现，主要功能包括：

- **代码检出**：从版本控制系统拉取最新代码
- **代码质量检测**：使用SonarQube对前后端代码进行质量分析
- **质量门禁**：确保代码质量达到预设标准
- **Docker镜像构建**：构建前后端Docker镜像
- **Harbor镜像推送**：将构建好的镜像推送到Harbor镜像仓库
- **Kubernetes部署**：自动部署应用到Kubernetes集群

## 2. 系统架构

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│             │    │             │    │             │    │             │
│  Git仓库    │───>│  Jenkins    │───>│  Harbor     │───>│  Kubernetes │
│             │    │             │    │             │    │             │
└─────────────┘    │   流水线    │    │  镜像仓库   │    │    集群     │
                   │             │    └─────────────┘    └─────────────┘
                   │             │
                   │             │    ┌─────────────┐
                   │             │    │             │
                   │             │───>│  SonarQube  │
                   │             │    │             │
                   └─────────────┘    └─────────────┘
```

## 3. 环境要求

### 3.1 基础设施

- Jenkins服务器：推荐Jenkins 2.300+，需要安装Docker和Kubectl
- Harbor镜像仓库：推荐Harbor 2.0+，需要配置项目和用户权限
- SonarQube服务器：推荐SonarQube 9.0+，需要配置质量门禁
- Kubernetes集群：推荐Kubernetes 1.20+，需要配置Ingress控制器

### 3.2 软件依赖

- Docker 20.10+
- Maven 3.8+
- Node.js 16.x
- Kubectl 1.20+
- SonarQube Scanner 4.6+

## 4. 目录结构

```
vue-springboot-app/
├── backend/               # 后端应用代码
│   └── sonar-project.properties  # 后端SonarQube配置
├── frontend/              # 前端应用代码
│   └── sonar-project.properties  # 前端SonarQube配置
├── docker/                # Docker相关配置
└── cicd/                  # CI/CD相关配置
    ├── jenkins/           # Jenkins流水线配置
    │   ├── Jenkinsfile            # 主流水线脚本
    │   └── harbor-config.md       # Harbor配置说明
    ├── kubernetes/        # Kubernetes部署配置
    │   ├── namespace.yaml         # 命名空间配置
    │   ├── backend-config.yaml    # 后端配置
    │   ├── backend-deployment.yaml # 后端部署
    │   ├── backend-service.yaml   # 后端服务
    │   ├── frontend-deployment.yaml # 前端部署
    │   ├── frontend-service.yaml  # 前端服务
    │   ├── ingress.yaml           # Ingress配置
    │   └── registry-secret.yaml   # 镜像仓库凭据
    ├── sonarqube/         # SonarQube配置
    │   └── sonarqube-config.md    # SonarQube配置说明
    └── README.md          # CI/CD流水线说明文档
```

## 5. Jenkins配置

### 5.1 插件安装

需要安装以下Jenkins插件：

- Git Plugin
- Docker Pipeline
- Kubernetes Plugin
- SonarQube Scanner for Jenkins
- Quality Gates Plugin
- Credentials Binding Plugin
- Pipeline Utility Steps

### 5.2 凭据配置

在Jenkins中配置以下凭据：

1. **Git仓库凭据**：用于代码检出
2. **Harbor凭据**：ID为`harbor-credentials`，用于镜像推送
3. **SonarQube令牌**：ID为`sonar-token`，用于代码质量检测
4. **Kubernetes配置**：ID为`kubeconfig`，用于集群部署

### 5.3 全局配置

设置以下全局环境变量：

- `HARBOR_URL`：Harbor仓库地址
- `SONAR_URL`：SonarQube服务器地址
- `K8S_CONFIG_FILE`：Kubernetes配置文件路径

## 6. 流水线使用方法

### 6.1 创建Jenkins流水线项目

1. 在Jenkins中创建新的流水线项目
2. 在流水线配置中，选择"Pipeline script from SCM"
3. 配置SCM为Git，填写仓库地址和凭据
4. 设置脚本路径为`cicd/jenkins/Jenkinsfile`
5. 保存配置

### 6.2 触发流水线

流水线可以通过以下方式触发：

1. **手动触发**：在Jenkins界面点击"立即构建"
2. **Git Webhook**：配置Git仓库的Webhook，实现代码提交自动触发
3. **定时触发**：配置构建触发器，定时执行流水线

### 6.3 流水线参数化

可以配置流水线参数，实现更灵活的部署：

- `IMAGE_TAG`：自定义镜像标签
- `DEPLOY_ENV`：部署环境（dev/test/prod）
- `SKIP_TEST`：是否跳过测试阶段
- `SKIP_SONAR`：是否跳过SonarQube分析

## 7. 构建和部署流程

### 7.1 构建阶段

1. **代码检出**：从Git仓库拉取最新代码
2. **代码质量检测**：使用SonarQube对前后端代码进行质量分析
3. **质量门禁检查**：确保代码质量达到预设标准
4. **Docker镜像构建**：构建前后端Docker镜像

### 7.2 部署阶段

1. **登录Harbor**：使用配置的凭据登录Harbor
2. **推送镜像**：将构建好的镜像推送到Harbor仓库
3. **Kubernetes部署**：更新镜像标签并应用Kubernetes配置
4. **清理**：清理工作空间和本地Docker镜像

## 8. 配置文件说明

### 8.1 Jenkinsfile

主流水线脚本，定义了整个CI/CD流程，包括：

- 环境变量配置
- 流水线阶段定义
- 构建和部署步骤
- 错误处理和通知
- 清理工作

### 8.2 Kubernetes配置文件

- **namespace.yaml**：创建Kubernetes命名空间
- **backend-config.yaml**：后端应用配置（ConfigMap）
- **backend-deployment.yaml**：后端应用部署配置
- **backend-service.yaml**：后端服务配置
- **frontend-deployment.yaml**：前端应用部署配置
- **frontend-service.yaml**：前端服务配置
- **ingress.yaml**：Ingress配置，用于外部访问
- **registry-secret.yaml**：Harbor镜像仓库凭据

### 8.3 SonarQube配置文件

- **backend/sonar-project.properties**：后端代码质量检测配置
- **frontend/sonar-project.properties**：前端代码质量检测配置

## 9. 常见问题排查

### 9.1 构建失败

- 检查代码语法和依赖问题
- 查看Jenkins构建日志获取详细错误信息
- 确保Maven和Node.js环境配置正确

### 9.2 镜像推送失败

- 验证Harbor凭据是否正确
- 检查网络连接，确保Jenkins可以访问Harbor
- 确认Harbor用户权限配置正确

### 9.3 部署失败

- 检查Kubernetes集群连接状态
- 验证Kubernetes配置文件格式是否正确
- 查看Pod日志获取应用启动错误
- 确认命名空间和资源配额配置

### 9.4 代码质量检测失败

- 查看SonarQube报告，修复发现的问题
- 调整质量门禁配置，适应项目需求
- 检查SonarQube服务器连接状态

## 10. 安全最佳实践

- **凭据管理**：使用Jenkins凭据管理系统，避免明文存储敏感信息
- **最小权限原则**：为Harbor和Kubernetes用户配置最小必要权限
- **镜像扫描**：启用Harbor的镜像扫描功能，检测安全漏洞
- **定期更新**：及时更新依赖库和基础镜像，修复已知漏洞
- **代码审查**：结合SonarQube检测结果，加强代码审查流程
- **部署策略**：使用RollingUpdate部署策略，确保平滑升级

## 11. 性能优化

- **缓存优化**：在Jenkins中配置Maven和npm缓存，加速构建
- **并行构建**：使用Jenkins并行流水线，同时构建前后端
- **镜像分层**：优化Dockerfile，合理使用镜像层缓存
- **资源配置**：为Kubernetes Pod设置合理的资源限制和请求
- **构建超时**：为流水线设置适当的超时时间，避免资源浪费

## 12. 监控和告警

- 配置Jenkins构建通知，发送构建结果邮件
- 监控Kubernetes集群资源使用情况
- 设置应用健康检查和自动恢复机制
- 配置Prometheus和Grafana监控应用性能

## 13. 版本历史

| 版本 | 日期 | 描述 |
|------|------|------|
| 1.0  | 2023-11-16 | 初始版本，实现基本CI/CD流水线 |
| 1.1  | - | 添加SonarQube代码质量检测 |
| 1.2  | - | 优化Kubernetes部署配置 |

## 14. 维护和支持

如有任何问题或建议，请联系项目维护团队。

---

*文档更新日期：2023-11-16*