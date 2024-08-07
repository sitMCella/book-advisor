<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useTheme } from 'vuetify'
import axios from 'axios'
import AppBar from './AppBar.vue'
import Navigation from './Navigation.vue'

const theme = useTheme()
const props = defineProps(['projectId'])
const emit = defineEmits(['selectedProjectId'])

interface Project {
  'projects/id': string
  'projects/name': string
  'projects/description': string
}

const operation = ref('')
const projects = ref<Project[]>([])
const selectedProject = ref<Project>({
  'projects/id': '',
  'projects/name': '',
  'projects/description': 'Select a project.'
})
const projectName = ref('')
const projectDescription = ref('')
const projectId = ref<number>(props.projectId)
const errorMessage = ref('')

const getProjects = async () => {
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
        if (props.projectId !== null && props.projectId !== '0') {
          const projectToSelect = data.filter((p) => p['projects/id'] === props.projectId)
          if (projectToSelect.length > 0) {
            selectedProject.value = projectToSelect[0]
          }
        }
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot retrieve Plots'
      console.error('Plots retrieve error: ', error)
    })
}

const createProject = async () => {
  await axios
    .post<Project>('/api/projects', {
      'projects/id': '0',
      'project/name': projectName.value,
      'project/description': projectDescription.value
    })
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

const updateProject = async (projectId: string, index: number) => {
  await axios
    .put<Project>('/api/projects', {
      'projects/id': projectId,
      'project/name': projectName.value,
      'project/description': projectDescription.value
    })
    .then(async (response) => {
      if (response.status !== 200) {
        errorMessage.value = 'Cannot update Project'
        console.error('Project update error: ', response.status, response.data)
        return
      }
      const isJson = response.headers['content-type'].includes('application/json')
      const data = await response.data
      if (isJson) {
        projects.value[index] = data
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot update Project'
      console.error('Project update error: ', error)
    })
}

const deleteProject = async (projectId: string, index: number) => {
  await axios
    .delete<Project>('/api/projects/' + projectId)
    .then(async (response) => {
      if (response.status !== 200) {
        errorMessage.value = 'Cannot delete Project'
        console.error('Project delete error: ', response.status, response.data)
        return
      }
      projects.value.splice(index, 1)
    })
    .catch((error) => {
      errorMessage.value = 'Cannot delete Project'
      console.error('Project delete error: ', error)
    })
}

const selectProject = (id: string) => {
  const project = projects.value.filter((p) => p['projects/id'] === id)
  if (project.length > 0) {
    selectedProject.value = project[0]
    projectId.value = parseInt(project[0]['projects/id'])
    // emits('selectedProjectId', projectId.value)
  } else {
    selectedProject.value = {
      'projects/id': '',
      'projects/name': '',
      'projects/description': 'Select a project.'
    }
  }
}

onMounted(async () => {
  await getProjects()
})
</script>

<template>
  <v-app>
    <AppBar />
    <Navigation :projectId="props.projectId" />
    <v-main>
      <v-card height="70px" rounded="0" flat>
        <v-toolbar>
          <v-toolbar-items>
            <v-dialog max-width="500">
              <template v-slot:activator="{ props: activatorProps }">
                <v-btn
                  v-bind="activatorProps"
                  text="Project"
                  prepend-icon="mdi-plus"
                  @click="[(projectName = ''), (projectDescription = '')]"
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
                <v-card rounded="0">
                  <v-card-text>
                    <v-list-item
                      v-for="(project, index) in projects"
                      :key="index"
                      @click="
                        [
                          $emit('selectedProjectId', project['projects/id']),
                          selectProject(project['projects/id'])
                        ]
                      "
                      :variant="project['projects/id'] == props.projectId ? 'tonal' : 'plain'"
                    >
                      <div class="project-name">
                        <v-dialog max-width="500">
                          <template v-slot:activator="{ props: activatorProps }">
                            <span>
                              <v-list-item-title>
                                {{ project['projects/name'] }}
                                <v-icon
                                  icon="mdi-pencil"
                                  size="19"
                                  style="margin-left: 10px; cursor: pointer"
                                  class="icon-hide"
                                  v-bind="activatorProps"
                                  @click="
                                    [
                                      (operation = 'update'),
                                      (projectName = project['projects/name']),
                                      (projectDescription = project['projects/description'])
                                    ]
                                  "
                                ></v-icon>
                                <v-icon
                                  icon="mdi-trash-can"
                                  size="19"
                                  style="margin-left: 10px; cursor: pointer"
                                  class="icon-hide"
                                  v-bind="activatorProps"
                                  @click="
                                    [
                                      (operation = 'delete'),
                                      (projectName = project['projects/name'])
                                    ]
                                  "
                                ></v-icon>
                              </v-list-item-title>
                            </span>
                          </template>

                          <template v-slot:default="{ isActive }">
                            <v-card title="Update Project" v-if="operation == 'update'">
                              <v-card-text>
                                Update the Project name.
                                <v-form>
                                  <v-text-field
                                    v-model="projectName"
                                    label="Name"
                                    hide-details
                                    :counter="10"
                                    required
                                  ></v-text-field>

                                  <v-divider class="border-opacity-0 mt-4 pr-6"></v-divider>

                                  Update the Project description.
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
                                <v-btn
                                  text="Save"
                                  @click="
                                    [
                                      updateProject(project['projects/id'], index),
                                      (isActive.value = false)
                                    ]
                                  "
                                ></v-btn>
                                <v-btn text="Close" @click="isActive.value = false"></v-btn>
                              </v-card-actions>
                            </v-card>
                            <v-card title="Delete Project" v-if="operation == 'delete'">
                              <v-card-text>
                                Are you sure you want to delete the project
                                {{ project['projects/name'] }} ?
                              </v-card-text>

                              <v-card-actions>
                                <v-spacer></v-spacer>
                                <v-btn
                                  text="Delete"
                                  @click="
                                    [
                                      deleteProject(project['projects/id'], index),
                                      (isActive.value = false)
                                    ]
                                  "
                                ></v-btn>
                                <v-btn text="Close" @click="isActive.value = false"></v-btn>
                              </v-card-actions>
                            </v-card>
                          </template>
                        </v-dialog>
                      </div>
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

<style scoped>
.project-name .icon-hide {
  visibility: hidden;
}

.project-name:hover .icon-hide {
  visibility: visible;
}
</style>
