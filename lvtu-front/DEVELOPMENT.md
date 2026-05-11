# lvtu-front 开发规范文档

## 项目概述

**lvtu-front** 是一个基于 Vue 3 + TypeScript + Vite 的现代化前端应用，专注于律师服务平台的客户端实现。

### 技术栈

- **框架**: Vue 3
- **语言**: JavaScript/TypeScript
- **构建工具**: Vite
- **UI 框架**: Element Plus
- **路由**: Vue Router 5
- **状态管理**: Pinia
- **HTTP 客户端**: Axios

---

## 项目结构与规范

### 根目录文件说明

| 文件/文件夹            | 作用                | 说明                                   |
| ---------------------- | ------------------- | -------------------------------------- |
| `package.json`       | 项目配置            | 定义项目元数据、依赖版本、可用命令脚本 |
| `vite.config.ts`     | Vite 配置           | 配置构建工具、代理、别名等             |
| `tsconfig.json`      | TypeScript 基础配置 | TypeScript 编译器选项                  |
| `tsconfig.app.json`  | 应用 TS 配置        | 应用代码的 TypeScript 配置             |
| `tsconfig.node.json` | Node TS 配置        | 构建工具相关代码的 TypeScript 配置     |
| `index.html`         | 入口 HTML           | 应用启动的 HTML 模板文件               |
| `README.md`          | 项目说明            | 项目简介与快速开始指南                 |
| `.gitignore`         | Git 忽略            | 定义不提交到版本控制的文件             |

---

## 文件夹结构详解

### `/public` - 公共静态资源

**作用**: 存放不需要经过构建工具处理的静态资源

eg：网站logo、favicon、后台模板静态文件、不变的banner

**命名规范**:

- 图片: `image.png`, `icon.svg`
- 字体: `font-name.ttf`, `font-name.woff2`
- 其他: 按资源类型分类

**示例**:

```
public/
  ├── images/          # 静态图片
  ├── icons/           # 图标文件
  ├── fonts/           # 字体文件
  └── documents/       # 文档资源
```

---

### `/src` - 源代码根目录

#### `/src/main.js` - 应用入口

- **作用**: Vue 应用启动文件
- **包含**: 应用初始化、全局配置、根组件挂载
- **不修改**: 除非需要改变应用初始化逻辑

#### `/src/App.vue` - 根组件

- **作用**: 应用根组件，包含全局布局（导航栏、侧边栏等）
- **包含**: 全局路由视图、布局结构、通用样式
- **命名**: 始终为 `App.vue`

#### `/src/style.css` - 全局样式

- **作用**: 全局 CSS 样式定义
- **包含**:
  - CSS 变量定义
  - 全局重置样式
  - 通用工具类
- **命名**: 使用 `style.css` 或 `global.css`

---

### `/src/api` - API 接口层

**作用**: 集中管理所有后端 API 调用

#### `/src/api/request.js` - HTTP 请求配置

- **作用**: Axios 实例配置和拦截器设置
- **包含**:
  - 基础 URL 配置
  - 请求/响应拦截器
  - 错误处理
  - 超时配置
- **命名**: 固定为 `request.js`

#### `/src/api/lawyer.js` - 律师相关接口

- **作用**: 律师信息 API 接口定义
- **包含**: 获取律师列表、律师详情、搜索等接口
- **命名规范**:
  - 文件名: 业务模块名 + `.js` (如 `lawyer.js`, `case.js`, `user.js`)
  - 函数名: `get*()`, `post*()`, `update*()`, `delete*()` (如 `getLawyerList()`, `getLawyerDetail()`)

**示例**:

```javascript
// lawyer.js
import request from './request'

// 获取律师列表
export const getLawyerList = (params) => request.get('/api/lawyers', { params })

// 获取律师详情
export const getLawyerDetail = (id) => request.get(`/api/lawyers/${id}`)

// 创建律师
export const createLawyer = (data) => request.post('/api/lawyers', data)
```

---

### `/src/components` - 可复用组件

**作用**: 存放可在多个页面复用的 Vue 组件

**命名规范**:

- **文件名**: PascalCase (如 `LawyerCard.vue`, `SearchBar.vue`)
- **component name**: 与文件名相同
- **前缀**:
  - `Base`: 基础通用组件 (如 `BaseButton.vue`, `BaseDialog.vue`)
  - `The`: 单一实例组件 (如 `TheHeader.vue`, `TheSidebar.vue`)
  - 其他: 业务相关组件

**结构示例**:

```
components/
  ├── base/                    # 基础组件
  │   ├── BaseButton.vue
  │   ├── BaseCard.vue
  │   └── BaseInput.vue
  ├── layout/                  # 布局组件
  │   ├── TheHeader.vue
  │   ├── TheSidebar.vue
  │   └── TheFooter.vue
  ├── lawyer/                  # 业务组件 - 律师相关
  │   ├── LawyerCard.vue
  │   ├── LawyerFilter.vue
  │   └── LawyerInfo.vue
  └── common/                  # 通用业务组件
      ├── SearchBar.vue
      ├── Pagination.vue
      └── EmptyState.vue
```

---

### `/src/router` - 路由配置

#### `/src/router/index.js` - 路由定义

- **作用**: Vue Router 配置和路由定义
- **包含**:
  - 路由表定义
  - 路由元信息
  - 导航守卫
  - 路由懒加载配置
- **命名**: 固定为 `index.js`

**示例结构**:

```
router/
  ├── index.js                 # 主路由配置
  └── guards/                  # 导航守卫 (可选)
      └── authGuard.js
```

---

### `/src/store` - 状态管理 (Pinia)

**作用**: 全局状态管理

**命名规范**:

- **文件名**: 使用小写加下划线 (如 `user_store.js`, `lawyer_store.js`)
- **Store 名称**: 使用 `use` 前缀的 camelCase (如 `useUserStore`, `useLawyerStore`)

**结构示例**:

```
store/
  ├── modules/                 # Store 模块
  │   ├── user_store.js       # 用户状态
  │   ├── lawyer_store.js     # 律师状态
  │   └── case_store.js       # 案件状态
  └── index.js                # Store 导出入口 (可选)
```

**示例**:

```javascript
// store/modules/user_store.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)
  
  const isLoggedIn = computed(() => !!userInfo.value)
  
  const setUserInfo = (user) => {
    userInfo.value = user
  }
  
  return { userInfo, isLoggedIn, setUserInfo }
})
```

---

### `/src/views` - 页面组件

**作用**: 路由关联的页面级别组件

**命名规范**:

- **文件名**: PascalCase (如 `LawyerList.vue`, `LawyerDetail.vue`)
- **后缀**: 通常以 `List`, `Detail`, `Form`, `Index` 结尾
- **目录结构**: 按功能模块划分

**结构示例**:

```
views/
  ├── lawyer/                  # 律师模块
  │   ├── LawyerList.vue      # 律师列表页
  │   ├── LawyerDetail.vue    # 律师详情页
  │   └── LawyerForm.vue      # 律师表单页 (编辑/新增)
  ├── case/                    # 案件模块
  │   ├── CaseList.vue
  │   └── CaseDetail.vue
  ├── user/                    # 用户模块
  │   ├── UserProfile.vue
  │   └── UserSettings.vue
  ├── auth/                    # 认证模块
  │   ├── Login.vue
  │   └── Register.vue
  └── 404.vue                  # 404 页面
```

---

### `/src/utils` - 工具函数

**作用**: 存放可复用的工具函数和辅助方法

#### `/src/utils/request.js` - 请求工具

- **作用**: API 调用的请求工具函数
- **包含**: 通用请求方法、格式转换等
- **注**: 与 `/src/api/request.js` 的区别在于粒度和用途

**结构示例**:

```
utils/
  ├── request.js              # HTTP 请求工具
  ├── date.js                 # 日期处理工具
  ├── string.js               # 字符串处理工具
  ├── validate.js             # 验证工具 (表单验证等)
  ├── format.js               # 数据格式化工具
  ├── storage.js              # 本地存储工具
  ├── device.js               # 设备检测工具
  ├── logger.js               # 日志工具
  └── constants.js            # 常量定义
```

**命名规范**:

- 文件名: 小写 + 下划线或小驼峰 (如 `date.js` 或 `dateUtils.js`)
- 函数名: 小驼峰 (如 `formatDate()`, `validateEmail()`)

---

### `/src/assets` - 编译资源

**作用**: 需要经过构建工具处理的资源

eg：律师头像、案例图片、首页轮播图、UI图标（icon）

**结构示例**:

```
assets/
  ├── images/                 # 图片资源
  │   ├── logo.png
  │   ├── backgrounds/
  │   └── icons/
  ├── styles/                 # 样式文件
  │   ├── variables.css       # CSS 变量定义
  │   ├── mixins.scss         # SCSS 混入
  │   └── themes/             # 主题文件
  └── fonts/                  # 字体文件
      └── custom-font/
```

---

## 文件命名规范总结

### Vue 组件文件

- **命名方式**: PascalCase (如 `MyComponent.vue`)
- **位置**: `/src/components` 或 `/src/views`
- **前缀约定**:
  - `Base*`: 基础组件
  - `The*`: 全局单一实例
  - 其他: 业务组件

### JavaScript/TypeScript 文件

- **工具函数**: 小驼峰 (如 `dateUtils.js`)
- **Store**: 小驼峰或下划线 (如 `userStore.js` 或 `user_store.js`)
- **API 模块**: 小驼峰 (如 `lawyerApi.js`)

### 文件夹

- **全部小写**:
  - 单数名词: `component`, `view`, `router`
  - 复数名词: `assets`, `utils`, `components`, `views`
  - 模块名: `lawyer`, `case`, `user`

---

## 开发工作流

### 本地开发

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览构建结果
npm run preview
```

### 代码组织原则

1. **单一责任**: 每个文件只做一件事
2. **可复用性**: 提取通用逻辑到 `components` 和 `utils`
3. **模块化**: 按功能而不是类型组织代码
4. **易测试**: 保持函数纯净，避免副作用
5. **性能**: 使用路由懒加载、代码分割、图片优化

### 导入路径建议

```javascript
// ✅ 推荐: 相对路径用于同目录
import MyComponent from './MyComponent.vue'

// ✅ 推荐: 绝对路径用于跨目录 (需配置别名)
import { getLawyerList } from '@/api/lawyer'
import MyButton from '@/components/base/BaseButton.vue'

// ❌ 避免: 过深的相对路径
import something from '../../../../../../utils/helpers.js'
```

---

## 最佳实践

1. **组件拆分**: 优先拆分成小的、可复用的组件
2. **类型安全**: 使用 TypeScript 定义组件 props 和事件
3. **响应式**: 合理使用 Vue 3 Composition API
4. **性能优化**:
   - 使用 `v-show` 替代 `v-if` 频繁切换
   - 列表渲染使用 `key` 绑定
   - 避免在模板中调用方法
5. **错误处理**: 在 API 调用中正确处理错误
6. **日志**: 使用统一的日志工具替代 `console.log`
7. **代码审查**: 提交前进行自检

---

## 配置和环境变量

### Vite 环境变量 (`.env` 文件)

```env
# .env.development
VITE_API_BASE_URL=http://localhost:8080

# .env.production
VITE_API_BASE_URL=https://api.example.com
```

### 在代码中使用

```javascript
const baseURL = import.meta.env.VITE_API_BASE_URL
```

---

## 相关资源

- [Vue 3 官方文档](https://vue3.vuejs.org/)
- [Vite 官方文档](https://vitejs.dev/)
- [Vue Router](https://router.vuejs.org/)
- [Pinia](https://pinia.vuejs.org/)
- [Element Plus](https://element-plus.org/)
