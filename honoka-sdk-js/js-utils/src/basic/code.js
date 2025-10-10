//noinspection JSUnusedGlobalSymbols

class CodeUtils {

  sleep(timeMillis) {
    return new Promise(resolve => setTimeout(resolve, timeMillis))
  }

  getDomHeight(dom) {
    return parseFloat(window.getComputedStyle(dom).height)
  }

  async tryForResult(getter, times = 20, interval = 5) {
    let result
    let exception
    for(let i = 0; i < times; i++) {
      try {
        result = getter()
        break
      } catch(e) {
        exception = e
        await this.sleep(interval)
      }
    }
    if(!result) throw exception
    return result
  }

  /**
   * 将使用class构造出的对象转换为普通的object
   */
  convertObjectToNormal(object) {
    let clonedObject = {}
    for(let prop in object) {
      clonedObject[prop] = object[prop]
    }
    return clonedObject
  }
}

const codeUtils = new CodeUtils()

export default codeUtils
