import './assets/main.css'
import '@mdi/font/css/materialdesignicons.css'
import '@vueup/vue-quill/dist/vue-quill.snow.css';

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { aliases, mdi } from 'vuetify/iconsets/mdi'
import { QuillEditor } from '@vueup/vue-quill'

const vuetify = createVuetify({
  components,
  directives,
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: {
      mdi
    }
  },
  theme: {
    defaultTheme: 'light'
  }
})

const app = createApp(App)

app.use(router)
app.use(vuetify)
app.component('QuillEditor', QuillEditor)

app.mount('#app')
