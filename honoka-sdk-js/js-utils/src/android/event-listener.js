//noinspection JSUnusedGlobalSymbols

class EventListenerUtils {

  listeners = {
    onBackButtonPressed: {},
    onActivityPause: {},
    onActivityResume: {}
  }

  //仅在main.js中调用一次
  exposeToGlobal() {
    if(!window.android) {
      window.android = {}
    }
    window.android.eventListenerUtils = this
  }

  invokeListeners(type) {
    let listenerGroups = this.listeners[type]
    if(!listenerGroups) return
    let result = false
    Object.values(listenerGroups).forEach(group => {
      for(let listener of group) {
        //监听器方法若返回true，表示监听器的预定义行为被触发
        if(listener()) result = true
      }
    })
    return result
  }
}

const eventListenerUtils = new EventListenerUtils()

export default eventListenerUtils
