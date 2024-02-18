import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
    plugins: [vue()],
    configureWebpack: {
        devtool: 'source-map',
      },
})
