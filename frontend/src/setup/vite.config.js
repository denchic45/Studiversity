import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
    plugins: [vue()],
    build: {
        outDir: '../../../backend/src/main/resources/setup-web'
    }
    // configureWebpack: {
    //     devtool: 'source-map',
    //   },
})
