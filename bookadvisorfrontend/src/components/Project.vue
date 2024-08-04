<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import AppBar from './AppBar.vue'
import Navigation from './Navigation.vue'

interface Project {
  'projects/id': string
  'projects/name': string
  'projects/description': string
}

const projects = ref<Project[]>([])
const selectedProject = ref<Project>({
  'projects/id': '',
  'projects/name': '',
  'projects/description': ''
})
const errorMessage = ref('')

const getProject = async () => {
  await axios
    .get<Project[]>('/api/projects')
    .then(async (response) => {
      if (response.status !== 200) {
        errorMessage.value = 'Cannot retrieve Projects'
        console.error('Projects retrieve error: ', response.status, response.data)
        return
      }
      const data = await response.data
      projects.value = []
      const isJson = response.headers['content-type'].includes('application/json')
      if (isJson) {
        projects.value = data
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot retrieve Plots'
      console.error('Plots retrieve error: ', error)
    })
}

const selectProject = (projectId: string) => {
  const project = projects.value.filter((p) => p['projects/id'] === projectId)
  if (project.length > 0) {
    selectedProject.value = project[0]
  } else {
    selectedProject.value = {
      'projects/id': '',
      'projects/name': '',
      'projects/description': ''
    }
  }
}

onMounted(async () => {
  await getProject()
})
</script>

<template>
  <v-app>
    <AppBar />
    <Navigation />
    <v-main>
      <span>
        <v-container style="max-width: 100%">
          <v-row>
            <v-col cols="12" md="4">
              <v-card color="grey-lighten-4" rounded="0">
                <v-card-text>
                  <v-list-item
                    v-for="(project, index) in projects"
                    :key="index"
                    @click="selectProject(project['projects/id'])"
                  >
                    <v-list-item-title>{{ project['projects/name'] }}</v-list-item-title>
                  </v-list-item>
                </v-card-text>
              </v-card>
            </v-col>
            <v-col cols="12" md="6">
              <v-card rounded="0">
                <v-card-text>
                  {{ selectedProject['projects/description'] }}
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </v-container>
      </span>
    </v-main>
  </v-app>
</template>
