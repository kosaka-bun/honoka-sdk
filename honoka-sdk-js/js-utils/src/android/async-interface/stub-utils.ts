import codeUtils from '@/basic/code'
import axios, { AxiosInstance } from 'axios'

class AndroidInterfaceStubUtils {

  enableWarning = true

  showErrorMsg(msg: string): void {}

  #axios?: AxiosInstance

  constructor() {
    this.#initAxios()
  }

  #initAxios() {
    this.#axios = axios.create({
      baseURL: '/jsInterface',
      timeout: 10 * 1000
    })
    this.#axios.interceptors.response.use(response => {
      if(response.status === 200 && response.data.success) {
        console.log(
          `Call ${response.config.url}`,
          '\nparams:', response.config.data,
          '\nresult:', response.data.data
        )
        return response.data.data
      } else {
        return Promise.reject()
      }
    }, error => {
      if(error.code === 'ERR_NETWORK') {
        console.error(`Call ${error.config.url}\n本地网络请求失败`)
        this.showErrorMsg('本地网络请求失败')
      } else {
        let msg = error.response.data?.msg
        if(!msg || msg === '') {
          msg = error.message
        }
        console.error(
          `Call ${error.config.url}`,
          '\nparams:', error.config.data,
          '\nerror:', error.response.data
        )
        this.showErrorMsg(msg)
      }
      return Promise.reject(error.response.data ?? error)
    })
  }

  #warning(name: any) {
    if(!this.enableWarning) return
    let msg = `You are calling an Android JavaScript Interface function "${name}" ` +
      'directly in browser!'
    console.warn(msg)
  }

  getStub(interfaceName: string, definition: any): any {
    let androidInterface = codeUtils.window[`android_${interfaceName}`]
    let stub: any = {}
    Object.keys(definition).forEach(it => {
      let methodDef = definition[it]
      if(methodDef instanceof Function) {
        stub[it] = androidInterface ? this.#getMethodStub(androidInterface, it) : (...args: any) => {
          this.#warning(`${interfaceName}.${it}()`)
          return methodDef(...args)
        }
        return
      }
      if(methodDef instanceof Object) {
        let isAsync = methodDef.isAsync ?? false
        if(isAsync) {
          stub[it] = androidInterface ? this.#getAsyncMethodStub(interfaceName, it) : (
            async (...args: any) => {
              this.#warning(`${interfaceName}.${it}()`)
              return await methodDef.fallback(...args)
            }
          )
        } else {
          stub[it] = androidInterface ? this.#getMethodStub(androidInterface, it) : (...args: any) => {
            this.#warning(`${interfaceName}.${it}()`)
            return methodDef.fallback(...args)
          }
        }
        return
      }
      throw new Error(`Unknown Android interface method stub definition: ${it} -> ${typeof methodDef}`)
    })
    return stub
  }

  #getMethodStub(androidInterface: any, methodName: string) {
    return (...args: any) => androidInterface[methodName](...args)
  }

  #getAsyncMethodStub(interfaceName: string, methodName: string) {
    return (...args: any) => (this.#axios!)({
      url: `/${interfaceName}/${methodName}`,
      method: 'post',
      data: args
    })
  }
}

const androidInterfaceStubUtils: AndroidInterfaceStubUtils = new AndroidInterfaceStubUtils()

export default androidInterfaceStubUtils
