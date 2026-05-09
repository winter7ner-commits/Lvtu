
# 律途 - 前端开发规范

## 项目概述

本项目是律途法律服务平台的前端项目，采用 Vue 3 + Vite 技术栈开发。

## 技术栈

- **Vue 3.4.0+
- **Vite 5.0.0+
- **Vue Router 4.2.5+
- **Axios 1.6.2+
- **Element Plus 2.4.4+ (可选)
- **Pinia 2.1.7+ (可选)

## 项目结构

```
lvtu-front/
├── public/                 # 静态资源目录
│   ├── icons/           # 图标文件
│   └── ...
├── src/
│   ├── api/             # API 接口封装
│   │   └── api.js      # 所有 API 接口
│   ├── components/       # 公共组件
│   │   └── layout/
│   │       └── TheHeader.vue
│   ├── router/          # 路由配置
│   │   └── index.js
│   ├── views/           # 页面视图
│   │   ├── auth/
│   │   │   └── Login.vue
│   │   ├── lawyer/
│   │   │   ├── LawArticleList.vue
│   │   │   ├── LawyerDetail.vue
│   │   │   └── LawyerList.vue
│   │   └── Home.vue
│   ├── App.vue
│   └── main.js
├── index.html
├── package.json
├── vite.config.ts
└── package-lock.json
```

## 命名规范

### 组件文件命名

- **Vue 组件文件使用 PascalCase（大驼峰），如 `TheHeader.vue`、`LawyerList.vue`
- 组件内部使用 PascalCase

### 变量和函数命名

- **变量和函数名使用 camelCase（小驼峰），如 `userName`、`formatDate()`
- **常量名使用 UPPER_SNAKE_CASE，如 `API_BASE_URL`、`MAX_PAGE_SIZE`

### 样式命名

- **CSS 类名使用 kebab-case，如 `.header-container`、`.logo-img`

### 路由命名

- **路由名称使用 kebab-case，如 `/law-article-list`
- **路由参数使用 camelCase，如 `/lawyer/:id`

## 代码风格

### Vue 组件结构

```vue
&lt;template&gt;
  &lt;!-- HTML 结构 --&gt;
&lt;/template&gt;

&lt;script setup&gt;
// 导入
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

// 响应式数据
const state = ref({})

// 计算属性
const computedValue = computed(() =&gt; {})

// 生命周期
onMounted(() =&gt; {})

// 函数
const handleClick = () =&gt; {}
&lt;/script&gt;

&lt;style scoped&gt;
/* 组件样式
&lt;/style&gt;
```

### CSS 样式规范

- 使用 scoped 样式防止样式污染
- 使用 BEM 命名法或类名规范
- 优先使用 Flexbox 或 Grid 布局
- 避免使用 !important

## API 接口规范

### API 封装规范

1. 所有 API 接口统一在 `src/api/api.js` 中定义
2. 使用 Axios 发送请求
3. 统一错误处理
4. 统一请求和响应拦截器（可选）

### API 响应格式

```javascript
// 统一响应格式
{
  code: 200,
  message: 'success',
  data: {},
  timestamp: '2024-01-01T00:00:00.000Z'
}

// 分页响应格式
{
  code: 200,
  message: 'success',
  data: {
    total: 100,
    page: 1,
    size: 10,
    records: []
  },
  timestamp: '2024-01-01T00:00:00.000Z'
}
```

## 路由规范

### 路由定义

```javascript
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () =&gt; import('@/views/Home.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
```

## 开发指南

### 快速启动

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

### 开发规范

1. 组件尽量复用
2. 组件单一职责原则
3. 组件合理拆分
4. 使用 Composition API 优先
5. 使用 TypeScript（可选）
6. 编写清晰的代码注释
7. 使用响应式数据
8. 合理使用 computed 和 watch
9. 使用合理的目录结构
10. 合理使用路由守卫

## 常见问题

### 添加新页面

1. 在 `src/views/` 下创建新的页面组件
2. 在 `src/router/index.js` 中添加路由配置
3. 如果需要，在 `src/api/api.js` 中添加 API 接口

### 添加新组件

1. 在 `src/components/` 下创建新的组件
2. 在需要的地方引入并使用

### 样式规范

- 使用 scoped 样式
- 使用 CSS 变量（可选）
- 使用 Tailwind CSS（可选）

## 最佳实践

1. **组件设计原则**: 单一职责、复用优先
2. **状态管理**: 合理使用 ref、reactive、computed
3. **性能优化**: 合理使用 v-if 和 v-show
4. **代码规范**: 遵循 ESLint 和 Prettier（可选）
5. **开发效率**: 使用 Vue DevTools、浏览器开发者工具
6. **调试技巧**: 使用 console.log 或专门的日志工具
7. **测试**: 编写单元测试（可选）
8. **Git 提交**: 使用 Conventional Commits（可选）
