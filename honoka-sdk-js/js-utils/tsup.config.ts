import { defineConfig } from 'tsup'

//noinspection JSUnusedGlobalSymbols
export default defineConfig({
  entry: ['src/**/*.ts'],
  format: ['esm'],
  dts: true,
  splitting: true,
  sourcemap: true,
  clean: true,
  bundle: false
})
