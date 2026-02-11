import androidInterfaceStubUtils from '@/android/async-interface/stub-utils'

const definition = {
  openNewWebActivity: (path: string) => {
    window.location.href = path
  },
  finishCurrentWebActivity: () => {
    history.back()
  }
}

const basicInterfaceStub: any = androidInterfaceStubUtils.getStub(
  'BasicJsInterface', definition
) ?? definition

export default basicInterfaceStub
