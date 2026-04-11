package features.snack4.issue;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 *
 * @author noear 2026/4/11 created
 *
 */
public class Issue_IIGHAX {
    @Test
    public void case1() {
       ONode oNode =  ONode.ofJson(json);

       System.out.println(oNode.toJson());
    }

    String json = "[{\"task_id\": \"backend\", \"agent_name\": \"backend-dev\", \"description\": \"后端Solon项目开发\", \"prompt\": \"你是后端开发工程师，负责开发 demo1-web 权限管理系统的后端。\n" +
            "\n" +
            "## 项目背景\n" +
            "- 项目路径：demo1-web/backend/\n" +
            "- 技术栈：Solon v3.10.1 + Java 8 + Maven + MyBatis + MySQL\n" +
            "- 架构文档：已存在于 demo1-web/architecture.md\n" +
            "- 数据库设计：已存在于 demo1-web/database/schema.sql\n" +
            "- API规范：已存在于 demo1-web/api-spec.md\n" +
            "\n" +
            "## 你的任务\n" +
            "\n" +
            "### 1. 创建 Maven 项目结构\n" +
            "在 demo1-web/backend/ 目录下创建完整的 Maven 项目：\n" +
            "- pom.xml（包含 Solon 3.10.1、MyBatis、MySQL、HikariCP 等依赖）\n" +
            "- Java 8 兼容\n" +
            "\n" +
            "### 2. 创建核心代码\n" +
            "按照以下结构创建代码：\n" +
            "\n" +
            "**启动类**: App.java\n" +
            "**配置文件**: application.yml（数据库连接、端口8080）\n" +
            "**实体类**: User.java, Role.java, Permission.java\n" +
            "**DTO类**: LoginDTO, UserDTO, PageDTO\n" +
            "**VO类**: Result（统一响应）, UserVO, RoleVO, PermissionVO\n" +
            "**Mapper**: UserMapper.java + UserMapper.xml\n" +
            "**Service**: UserService, RoleService, PermissionService\n" +
            "**Controller**: AuthController, UserController, RoleController, PermissionController\n" +
            "\n" +
            "### 3. 实现功能模块\n" +
            "\n" +
            "#### 认证模块 (AuthController)\n" +
            "- POST /api/v1/auth/login - 登录（返回JWT Token）\n" +
            "- GET /api/v1/auth/info - 获取当前用户信息\n" +
            "- POST /api/v1/auth/logout - 登出\n" +
            "\n" +
            "#### 用户管理模块 (UserController)\n" +
            "- GET /api/v1/users - 用户列表（分页）\n" +
            "- GET /api/v1/users/{id} - 用户详情\n" +
            "- POST /api/v1/users - 新增用户\n" +
            "- PUT /api/v1/users/{id} - 更新用户\n" +
            "- DELETE /api/v1/users/{id} - 删除用户\n" +
            "\n" +
            "#### 角色管理模块 (RoleController)\n" +
            "- GET /api/v1/roles - 角色列表\n" +
            "- GET /api/v1/roles/{id} - 角色详情\n" +
            "- POST /api/v1/roles - 新增角色\n" +
            "- PUT /api/v1/roles/{id} - 更新角色\n" +
            "- DELETE /api/v1/roles/{id} - 删除角色\n" +
            "- GET /api/v1/roles/options - 角色下拉选项\n" +
            "\n" +
            "#### 权限管理模块 (PermissionController)\n" +
            "- GET /api/v1/permissions - 权限列表（树形）\n" +
            "- GET /api/v1/permissions/{id} - 权限详情\n" +
            "- POST /api/v1/permissions - 新增权限\n" +
            "- PUT /api/v1/permissions/{id} - 更新权限\n" +
            "- DELETE /api/v1/permissions/{id} - 删除权限\n" +
            "\n" +
            "### 4. 工具类\n" +
            "- JwtUtils.java - JWT 工具（生成/验证 Token）\n" +
            "- PasswordUtils.java - 密码加密工具（MD5）\n" +
            "\n" +
            "### 5. 配置类\n" +
            "- MybatisConfig.java - MyBatis 配置\n" +
            "- CorsConfig.java - 跨域配置\n" +
            "\n" +
            "## 重要提示\n" +
            "1. 使用 Solon 注解：@SolonMain, @Controller, @Inject, @Mapping, @Get, @Post, @Put, @Delete, @Param, @Body\n" +
            "2. 统一响应格式使用 Result<T> 类\n" +
            "3. 密码使用 MD5 加密（简单起见）\n" +
            "4. 暂时不需要实际的数据库连接，使用内存数据模拟即可（方便测试）\n" +
            "5. 确保代码可以编译通过\n" +
            "\n" +
            "请完整创建所有必要的文件！\"}, {\"task_id\": \"frontend\", \"agent_name\": \"frontend-dev\", \"description\": \"前端Vue3项目开发\", \"prompt\": \"你是前端开发工程师，负责开发 demo1-web 权限管理系统的前端界面。\n" +
            "\n" +
            "## 项目背景\n" +
            "- 项目路径：demo1-web/frontend/\n" +
            "- 技术栈：Vue 3 + Vite + Element Plus + Pinia + Vue Router + Axios\n" +
            "- UI风格：清爽、现代、简约\n" +
            "- API规范：已存在于 demo1-web/api-spec.md\n" +
            "\n" +
            "## 你的任务\n" +
            "\n" +
            "### 1. 创建 Vue 3 项目结构\n" +
            "在 demo1-web/frontend/ 目录下创建完整的项目：\n" +
            "- package.json\n" +
            "- vite.config.js\n" +
            "- index.html\n" +
            "- src/main.js\n" +
            "- src/App.vue\n" +
            "\n" +
            "### 2. 创建核心文件\n" +
            "\n" +
            "#### 路由配置 (src/router/index.js)\n" +
            "- /login - 登录页\n" +
            "- / - 主布局\n" +
            "  - /dashboard - 首页\n" +
            "  - /system/user - 用户管理\n" +
            "  - /system/role - 角色管理\n" +
            "  - /system/permission - 权限管理\n" +
            "\n" +
            "#### 状态管理 (src/stores/)\n" +
            "- user.js - 用户状态（token、userInfo）\n" +
            "- permission.js - 权限状态\n" +
            "\n" +
            "#### API 封装 (src/api/)\n" +
            "- auth.js - 认证接口\n" +
            "- user.js - 用户接口\n" +
            "- role.js - 角色接口\n" +
            "- permission.js - 权限接口\n" +
            "\n" +
            "#### 工具函数 (src/utils/)\n" +
            "- request.js - Axios 封装（请求/响应拦截器）\n" +
            "- auth.js - Token 管理\n" +
            "\n" +
            "#### 样式文件 (src/style/)\n" +
            "- variables.scss - SCSS 变量（主题色等）\n" +
            "- index.scss - 全局样式\n" +
            "\n" +
            "### 3. 创建页面组件\n" +
            "\n" +
            "#### 登录页面 (src/views/login/index.vue)\n" +
            "- 用户名输入框\n" +
            "- 密码输入框\n" +
            "- 登录按钮\n" +
            "- 清爽的渐变背景\n" +
            "\n" +
            "#### 主布局 (src/views/layout/)\n" +
            "- index.vue - 整体布局\n" +
            "- Sidebar.vue - 左侧菜单栏（Element Plus 菜单组件）\n" +
            "- Header.vue - 顶部导航（用户信息、退出按钮）\n" +
            "\n" +
            "#### 首页 (src/views/dashboard/index.vue)\n" +
            "- 欢迎信息\n" +
            "- 基础统计卡片\n" +
            "\n" +
            "#### 用户管理 (src/views/system/user/index.vue)\n" +
            "- 搜索栏（关键字搜索）\n" +
            "- 用户列表表格（分页）\n" +
            "- 新增/编辑用户对话框\n" +
            "- 删除确认\n" +
            "\n" +
            "#### 角色管理 (src/views/system/role/index.vue)\n" +
            "- 角色列表表格\n" +
            "- 新增/编辑角色对话框\n" +
            "- 权限分配（树形复选框）\n" +
            "\n" +
            "#### 权限管理 (src/views/system/permission/index.vue)\n" +
            "- 权限树形表格\n" +
            "- 新增/编辑权限对话框\n" +
            "\n" +
            "### 4. 公共组件 (src/components/)\n" +
            "- Pagination.vue - 分页组件封装\n" +
            "\n" +
            "## UI 设计要求\n" +
            "1. **配色方案**：\n" +
            "   - 主色：#409EFF（Element Plus 默认蓝）\n" +
            "   - 背景：#f5f7fa\n" +
            "   - 文字：#303133\n" +
            "\n" +
            "2. **布局**：\n" +
            "   - 左侧固定侧边栏（深色背景 #304156）\n" +
            "   - 顶部导航栏（白色背景）\n" +
            "   - 内容区域带灰色背景和白色卡片\n" +
            "\n" +
            "3. **交互**：\n" +
            "   - 表格带斑马纹\n" +
            "   - 操作按钮带图标\n" +
            "   - 加载状态和骨架屏\n" +
            "   - 友好的错误提示\n" +
            "\n" +
            "## 重要提示\n" +
            "1. 使用 Composition API（<script setup>）\n" +
            "2. Element Plus 组件按需导入\n" +
            "3. 暂时使用 Mock 数据，方便后续对接真实 API\n" +
            "4. 确保代码可以运行（npm run dev）\n" +
            "\n" +
            "请完整创建所有必要的文件！\"}]";
}
