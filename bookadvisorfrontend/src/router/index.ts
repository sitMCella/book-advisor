import { createRouter, createWebHistory } from 'vue-router'
import ProjectMapView from '../views/ProjectMapView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'projectmap',
      component: ProjectMapView
    }
  ]
})

export default router
