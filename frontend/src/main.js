import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import Login from './views/Login.vue'
import Registro from './views/Registro.vue'
import Recuperar from './views/Recuperar.vue'
import App from './App.vue'
import './style.css'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', component: Login },
    { path: '/registro', component: Registro },
    { path: '/recuperar', component: Recuperar }
  ]
})

createApp(App).use(router).mount('#app')
