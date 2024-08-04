import { createRouter, createWebHistory } from 'vue-router'
import ProjectMapView from '../views/ProjectMapView.vue'
import ProjectView from '../views/ProjectView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'project',
      component: ProjectView
    },
    {
      path: '/projectmap',
      name: 'projectmap',
      component: ProjectMapView
    }
  ]
})

export default router
