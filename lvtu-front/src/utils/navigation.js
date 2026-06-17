export const goBack = (router, fallback = '/') => {
  if (typeof window !== 'undefined' && window.history.length > 1) {
    router.back()
    return
  }

  router.push(fallback)
}
