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
  'projects/description': 'Select a project.'
})
const projectName = ref('')
const projectDescription = ref('')
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
      selectedProject.value = {
        'projects/id': '',
        'projects/name': '',
        'projects/description': 'Select a project.'
    }
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

const createProject = async () => {
  await axios
    .post<Project>('/api/projects', { 'projects/id': '0', 'project/name': projectName.value, 'project/description': projectDescription.value })
    .then(async (response) => {
      if (response.status !== 200) {
        errorMessage.value = 'Cannot create Project'
        console.error('Project creation error: ', response.status, response.data)
        return
      }
      const isJson = response.headers['content-type'].includes('application/json')
      const data = await response.data
      if (isJson) {
        projects.value.push(data)
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot create Project'
      console.error('Project creation error: ', error)
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
      'projects/description': 'Select a project.'
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
        <v-card color="grey-lighten-4" height="70px" rounded="0" flat>
        <v-toolbar>
          <v-toolbar-items>
            <v-dialog max-width="500">
              <template v-slot:activator="{ props: activatorProps }">
                <v-btn
                  v-bind="activatorProps"
                  text="Project"
                  prepend-icon="mdi-plus"
                  @click="projectName = ''"
                ></v-btn>
              </template>

              <template v-slot:default="{ isActive }">
                <v-card title="Create Project">
                  <v-card-text>
                    Insert the Project name.
                    <v-form>
                      <v-text-field
                        v-model="projectName"
                        label="Name"
                        hide-details
                        :counter="10"
                        required
                      ></v-text-field>

                      <v-divider class="border-opacity-0 mt-4 pr-6"></v-divider>

                      Insert the Project description.
                      <v-textarea
                        label="Description"
                        v-model="projectDescription"
                        name="project-description"
                        variant="filled"
                        auto-grow
                      ></v-textarea>
                    </v-form>
                  </v-card-text>

                  <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn text="Save" @click="[createProject(), (isActive.value = false)]"></v-btn>
                    <v-btn text="Close" @click="isActive.value = false"></v-btn>
                  </v-card-actions>
                </v-card>
              </template>
            </v-dialog>
          </v-toolbar-items>
        </v-toolbar>
      </v-card>

      <div>
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
        </div>

        <v-alert
        v-if="errorMessage !== ''"
        border="start"
        density="compact"
        title="Error"
        type="error"
        variant="tonal"
        closable
        >{{ errorMessage }}</v-alert
      >
    </v-main>
  </v-app>
</template>
