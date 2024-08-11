<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useTheme } from 'vuetify'
import axios from 'axios'
import AppBar from './AppBar.vue'
import Navigation from './Navigation.vue'
import { Quill } from '@vueup/vue-quill'

const theme = useTheme()
const props = defineProps(['projectId'])

interface Project {
  'projects/id': string
  'projects/name': string
  'projects/description': string
  'projects/tags': string[]
}

interface Chapter {
  'chapters/id': string
  'chapters/name': string
  'chapters/project_id': string
}

interface Plot {
  'plots/id': string
  'plots/name': string
  'plots/project_id': string
}

interface Scene {
  'scenes/id': string
  'scenes/title': string
  'scenes/extract': string
  'scenes/value': string
  'scenes/chapter_id': string
  'scenes/plot_id': string
  'scenes/project_id': string
  'scenes/tags': string[]
}

const errorMessage = ref('')
const chapters = ref<Chapter[]>([])
const plots = ref<Plot[]>([])
const scenes = ref<Scene[]>([])
const chapterOpen = ref([])
const plotOpen = ref([])
const scene = ref<Scene>({
  'scenes/id': '',
  'scenes/title': '',
  'scenes/extract': '',
  'scenes/value': '',
  'scenes/chapter_id': '',
  'scenes/plot_id': '',
  'scenes/project_id': '',
  'scenes/tags': []
})
const sceneValueEditor = ref('')

const getChapters = async () => {
  await axios
    .get<Chapter[]>('/api/projects/' + props.projectId + '/chapters')
    .then(async (response) => {
      if (response.status !== 200) {
        errorMessage.value = 'Cannot retrieve Chapters'
        console.error('Chapters retrieve error: ', response.status, response.data)
        return
      }
      const data = await response.data
      chapters.value = []
      const isJson = response.headers['content-type'].includes('application/json')
      if (isJson) {
        chapters.value = data
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot retrieve Chapters'
      console.error('Chapters retrieve error: ', error)
    })
}

const getPlots = async () => {
  await axios
    .get<Plot[]>('/api/projects/' + props.projectId + '/plots')
    .then(async (response) => {
      if (response.status !== 200) {
        errorMessage.value = 'Cannot retrieve Plots'
        console.error('Plots retrieve error: ', response.status, response.data)
        return
      }
      const data = await response.data
      plots.value = []
      const isJson = response.headers['content-type'].includes('application/json')
      if (isJson) {
        plots.value = data
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot retrieve Plots'
      console.error('Plots retrieve error: ', error)
    })
}

const getScenes = async () => {
  await axios
    .get<Scene[]>('/api/projects/' + props.projectId + '/scenes')
    .then(async (response) => {
      if (response.status !== 200) {
        errorMessage.value = 'Cannot retrieve Scenes'
        console.error('Scenes retrieve error: ', response.status, response.data)
        return
      }
      const data = await response.data
      scenes.value = []
      const isJson = response.headers['content-type'].includes('application/json')
      if (isJson) {
        scenes.value = data
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot retrieve Scenes'
      console.error('Scenes retrieve error: ', error)
    })
}

const updateScene = async () => {
  let text = sceneValueEditor.value
  while (text.endsWith('\n') || text.endsWith('\r')) {
    text = text.slice(0, -1)
  }
  await axios
    .put<Scene>('/api/projects/' + props.projectId + '/scenes/' + scene.value['scenes/id'], {
      'scenes/id': scene.value['scenes/id'],
      'scenes/title': scene.value['scenes/title'],
      'scenes/extract': scene.value['scenes/extract'],
      'scenes/value': text,
      'scenes/chapter_id': scene.value['scenes/chapter_id'],
      'scenes/plot_id': scene.value['scenes/plot_id'],
      'scenes/project_id': parseInt(props.projectId),
      'scenes/tags': scene.value['scenes/tags']
    })
    .then(async (response) => {
      if (response.status !== 200) {
        errorMessage.value = 'Cannot update Scene'
        console.error('Scene update error: ', response.status, response.data)
        return
      }
      const isJson = response.headers['content-type'].includes('application/json')
      const data = await response.data
      if (isJson) {
        const sceneIndex: number = scenes.value.findIndex((s) => {
          return s['scenes/id'] === scene.value['scenes/id']
        })
        scenes.value.splice(sceneIndex, 1, data)
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot update Scene'
      console.error('Scene update error: ', error)
    })
}

const selectScene = (sceneId: string) => {
  scene.value = scenes.value.filter((s: Scene) => s['scenes/id'] === sceneId)[0]
  sceneValueEditor.value = scene.value['scenes/value']
}

const filterScenes = (chapter: Chapter, plot: Plot): Scene[] => {
  return scenes.value.filter((s: Scene) => {
    return (
      s['scenes/chapter_id'] === chapter['chapters/id'] && s['scenes/plot_id'] === plot['plots/id']
    )
  })
}

const onEditorReady = (e: Quill) => {
  sceneValueEditor.value = scene.value?.['scenes/value']
}

const isDark = () => {
  return theme.global.current.value.dark
}

onMounted(async () => {
  await getChapters()
  await getPlots()
  await getScenes()
})
</script>

<template>
  <v-app>
    <AppBar />
    <Navigation :projectId="props.projectId" />
    <v-main>
      <v-container style="max-width: 100%">
        <v-row style="min-height: calc(100vh - 120px)">
          <v-col cols="12" md="3">
            <div v-for="(chapter, chapterIndex) in chapters">
              <v-list v-model:opened="chapterOpen[chapterIndex]">
                <v-list-group value="chapter">
                  <template v-slot:activator="{ props }">
                    <v-list-item v-bind="props">{{ chapter['chapters/name'] }}</v-list-item>
                  </template>

                  <div v-for="(plot, plotIndex) in plots">
                    <v-list v-model:opened="plotOpen[plotIndex]">
                      <v-list-group value="plot">
                        <template v-slot:activator="{ props }">
                          <v-list-item v-bind="props">{{ plot['plots/name'] }}</v-list-item>
                        </template>
                        <v-list>
                          <v-list-item
                            v-bind="props"
                            v-for="(scene, sceneIndex) in filterScenes(chapter, plot)"
                            @click="selectScene(scene['scenes/id'])"
                          >
                            {{ scene['scenes/title'] }}
                          </v-list-item>
                        </v-list>
                      </v-list-group>
                    </v-list>
                  </div>
                </v-list-group>
              </v-list>
            </div>
          </v-col>
          <v-col cols="12" md="9">
            <v-card style="height: 100%">
              <v-card-title>{{ scene?.['scenes/title'] }}</v-card-title>
              <v-card-text>
                <v-form>
                  <QuillEditor
                    ref="editor"
                    v-model:content="sceneValueEditor"
                    theme="snow"
                    toolbar="full"
                    contentType="html"
                    @ready="onEditorReady($event)"
                  />
                </v-form>
              </v-card-text>
            </v-card>
            <div class="editor-actions-container-overlay"></div>
            <div class="editor-actions-container" :class="{ 'theme-dark': isDark() }">
              <v-col>
                <span>
                  <v-btn text="Save" @click="[updateScene()]"></v-btn>
                </span>
              </v-col>
            </div>
          </v-col>
        </v-row>
      </v-container>
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
.editor-actions-container {
  position: fixed;
  z-index: 1;
  bottom: 0px;
  background-color: #ffffff;
  width: 100%;
}

.theme-dark.editor-actions-container {
  background-color: #111111;
}

.editor-actions-container-overlay {
  margin-top: 45px;
}
</style>
