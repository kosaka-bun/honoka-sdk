//noinspection JSUnusedGlobalSymbols

import axios from 'axios'

class AndroidInterfaceStubUtils {

  enableWarning = true

  #axios

  constructor() {
    this.#initAxios()
  }

  #initAxios() {
    this.#axios = axios.create({
      baseURL: '/jsInterface',
      timeout: 10 * 1000
    })
    this.#axios.interceptors.response.use(response => {
      if(response.status === 200 && response.data.status) {
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
      } else {
        console.error(
          `Call ${error.config.url}`,
          '\nparams:', error.config.data,
          '\nerror:', error.response.data.message
        )
      }
      return Promise.reject(error.response.data ?? error)
    })
  }

  #warning(name) {
    if(!this.enableWarning) return
    let msg = `You are calling an Android JavaScript Interface function "${name}" ` +
      'directly in browser!'
    console.warn(msg)
  }

  getStub(interfaceName, definition) {
    let androidInterface = window[`android_${interfaceName}`]
    let stub = {}
    Object.keys(definition).forEach(it => {
      let methodDef = definition[it]
      if(methodDef instanceof Function) {
        stub[it] = androidInterface ? this.#getMethodStub(androidInterface, it) : (...args) => {
          this.#warning(`${interfaceName}.${it}()`)
          return methodDef(...args)
        }
        return
      }
      if(methodDef instanceof Object) {
        let isAsync = methodDef.isAsync ?? false
        if(isAsync) {
          stub[it] = androidInterface ? this.#getAsyncMethodStub(interfaceName, it) : (
            async (...args) => {
              this.#warning(`${interfaceName}.${it}()`)
              return await methodDef.fallback(...args)
            }
          )
        } else {
          stub[it] = androidInterface ? this.#getMethodStub(androidInterface, it) : (...args) => {
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

  #getMethodStub(androidInterface, methodName) {
    return (...args) => androidInterface[methodName](...args)
  }

  #getAsyncMethodStub(interfaceName, methodName) {
    return (...args) => this.#axios({
      url: `/${interfaceName}/${methodName}`,
      method: 'post',
      data: args
    })
  }
}

const androidInterfaceStubUtils = new AndroidInterfaceStubUtils()

export default androidInterfaceStubUtils
