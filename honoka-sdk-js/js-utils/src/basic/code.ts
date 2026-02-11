class CodeUtils {

  window = window as any

  sleep(timeMillis: any): Promise<unknown> {
    return new Promise(resolve => setTimeout(resolve, timeMillis))
  }

  getDomHeight(dom: Element): number {
    return parseFloat(window.getComputedStyle(dom).height)
  }

  async tryForResult(getter: () => any, times = 20, interval = 5): Promise<any> {
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
  convertObjectToNormal(object: any): any {
    let clonedObject: any = {}
    for(let prop in object) {
      clonedObject[prop] = object[prop]
    }
    return clonedObject
  }
}

const codeUtils: CodeUtils = new CodeUtils()

export default codeUtils
