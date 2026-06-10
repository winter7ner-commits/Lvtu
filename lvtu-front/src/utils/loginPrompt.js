import { ElMessageBox } from 'element-plus'

const defaultMessage = '登录或注册后，你可以发布法律服务需求、查看订单进度、接收律师处理通知，并保存常用法条。'

export const promptLogin = async (router, targetRoute, message = defaultMessage) => {
  try {
    await ElMessageBox.confirm(message, '登录后继续使用', {
      confirmButtonText: '去登录',
      cancelButtonText: '取消',
      type: 'info'
    })
    const resolved = router.resolve(targetRoute)
    router.push({
      path: '/login',
      query: { redirect: resolved.fullPath }
    })
  } catch {
    // 用户取消登录引导时不需要额外提示。
  }
}

export const goWithLogin = (isLoggedIn, router, targetRoute, message) => {
  if (isLoggedIn) {
    router.push(targetRoute)
    return
  }
  promptLogin(router, targetRoute, message)
}
