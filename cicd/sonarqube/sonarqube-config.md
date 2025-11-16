# SonarQube 代码质量检测配置指南

本文档提供了如何在Jenkins流水线中配置SonarQube代码质量检测集成的说明。

## 1. SonarQube 准备

在开始之前，请确保您已经：

- 搭建了SonarQube服务器
- 创建了管理员账户或具有项目创建权限的用户账户
- 在SonarQube中生成了访问令牌

## 2. Jenkins 配置

### 2.1 安装 SonarQube Scanner 插件

1. 登录Jenkins管理界面
2. 导航至：`插件管理` > `可用插件`
3. 搜索并安装以下插件：
   - `SonarQube Scanner for Jenkins`
   - `Quality Gates Plugin`
4. 安装完成后重启Jenkins

### 2.2 配置 SonarQube 服务器

1. 导航至：`系统管理` > `系统配置` > `SonarQube servers`
2. 点击 `Add SonarQube`
3. 填写以下信息：
   - Name: `SonarQube` (必须与Jenkinsfile中配置的一致)
   - Server URL: 您的SonarQube服务器地址 (例如: `http://sonarqube.example.com`)
   - Server authentication token: 点击 `添加` > `Jenkins凭据` > `Secret text`
     - Secret: SonarQube生成的访问令牌
     - ID: `sonar-token` (必须与Jenkinsfile中配置的一致)
4. 点击 `保存`

### 2.3 配置 SonarQube Scanner

1. 导航至：`系统管理` > `全局工具配置` > `SonarQube Scanner`
2. 点击 `Add SonarQube Scanner`
3. 选择 `Install automatically`
4. 选择最新版本的SonarQube Scanner
5. 点击 `保存`

## 3. 项目配置文件

在项目中需要添加SonarQube配置文件，以自定义代码质量检测的规则和范围。

### 3.1 后端配置 (backend/sonar-project.properties)

```properties
# 项目标识和名称
sonar.projectKey=vue-springboot-backend
sonar.projectName=Vue SpringBoot Backend
sonar.projectVersion=1.0

# 源码路径
sonar.sources=src/main/java
sonar.java.binaries=target/classes

# 测试路径
sonar.tests=src/test/java
sonar.java.test.binaries=target/test-classes

# 编码格式
sonar.sourceEncoding=UTF-8

# 排除目录
sonar.exclusions=**/test/**,**/generated/**,**/config/**

# 代码覆盖率配置
sonar.jacoco.reportPaths=target/jacoco.exec
```

### 3.2 前端配置 (frontend/sonar-project.properties)

```properties
# 项目标识和名称
sonar.projectKey=vue-springboot-frontend
sonar.projectName=Vue SpringBoot Frontend
sonar.projectVersion=1.0

# 源码路径
sonar.sources=src

# 编码格式
sonar.sourceEncoding=UTF-8

# 排除目录
sonar.exclusions=node_modules/**/*,**/*.test.js,**/*.spec.js,**/dist/**,**/coverage/**

# JavaScript和TypeScript配置
sonar.javascript.lcov.reportPaths=coverage/lcov.info
```

## 4. 质量门禁配置

在SonarQube中配置质量门禁，以确保代码质量达到标准：

1. 登录SonarQube
2. 导航至：`Quality Gates` > `Create`
3. 创建名为 `vue-springboot-quality-gate` 的质量门禁
4. 添加以下条件：
   - 代码覆盖率 > 80%
   - 代码重复率 < 5%
   - 严重性为Blocker和Critical的问题数量 = 0
   - 新增代码的技术债务比率 < 5%
5. 点击 `保存`
6. 将质量门禁关联到项目：
   - 导航至项目 > `Quality Gates` > `Select a Quality Gate` > 选择创建的质量门禁

## 5. 流水线集成说明

Jenkinsfile中的SonarQube集成流程：

1. 使用 `withSonarQubeEnv` 包装SonarQube扫描步骤
2. 分别对前后端进行代码质量检测
3. 使用 `waitForQualityGate` 等待质量门禁结果，如果不通过则中止流水线

## 6. 常见问题排查

### 扫描失败

1. 检查SonarQube服务器地址和令牌是否正确
2. 确保SonarQube服务器可以从Jenkins服务器访问
3. 验证项目配置文件是否正确

### 质量门禁不通过

1. 查看SonarQube界面上的详细报告
2. 修复发现的代码问题
3. 重新触发流水线

## 7. 最佳实践

- 定期更新代码质量规则
- 针对不同类型的项目设置不同的质量门禁
- 将代码质量检测结果集成到团队的开发流程中
- 培养开发人员关注代码质量的意识