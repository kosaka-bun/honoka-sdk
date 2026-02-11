import codeUtils from '@/basic/code'

class EventListenerUtils {

  listeners: any = {
    onBackButtonPressed: {},
    onActivityPause: {},
    onActivityResume: {}
  }

  //仅在main.ts中调用一次
  exposeToGlobal(): void {
    if(!codeUtils.window.android) {
      codeUtils.window.android = {}
    }
    codeUtils.window.android.eventListenerUtils = this
  }

  invokeListeners(type: string): boolean | undefined {
    let listenerGroups = this.listeners[type]
    if(!listenerGroups) return
    let result = false
    Object.values(listenerGroups).forEach((group: any) => {
      for(let listener of group) {
        //监听器方法若返回true，表示监听器的预定义行为被触发
        if(listener()) result = true
      }
    })
    return result
  }
}

const eventListenerUtils: EventListenerUtils = new EventListenerUtils()

export default eventListenerUtils
