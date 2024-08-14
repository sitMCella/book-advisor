<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useTheme } from 'vuetify'
import axios from 'axios'
import { Quill } from '@vueup/vue-quill'
import AppBar from './AppBar.vue'
import Navigation from './Navigation.vue'

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
  visible: boolean
}

interface EditorInsert {
  insert: string
}

interface EditorText {
  ops: EditorInsert[]
}

const projectTags = ref<string[]>([])
const chapters = ref<Chapter[]>([])
const plots = ref<Plot[]>([])
const scenes = ref<Scene[]>([])
const errorMessage = ref('')
const chapterName = ref('')
const operation = ref('')
const plotName = ref('')
const sceneTitle = ref('')
const sceneExtract = ref('')
const chapterId = ref('')
const plotId = ref('')
const formValid = ref(true)
const sceneId = ref('')
const sceneValue = ref('')
const sceneValueEditor = ref('')
const sceneTags = ref<string[]>([])
const sceneTab = ref()
const selectedTags = ref<string[]>([])

const validationRules = [
  (value: string) => {
    if (value) return true
    return 'Required field.'
  }
]

const isDark = () => {
  return theme.global.current.value.dark
}

const getProject = async () => {
  await axios
    .get<Project>('/api/projects/' + props.projectId)
    .then(async (response) => {
      if (response.status !== 200) {
        errorMessage.value = 'Cannot retrieve Project'
        console.error('Project retrieve error: ', response.status, response.data)
        return
      }
      const data = await response.data
      const isJson = response.headers['content-type'].includes('application/json')
      if (isJson) {
        projectTags.value = data['projects/tags']
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot retrieve Project'
      console.error('Project retrieve error: ', error)
    })
}

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
        scenes.value.every((v) => (v.visible = true))
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot retrieve Scenes'
      console.error('Scenes retrieve error: ', error)
    })
}

const filterScenes = (plot: Plot): Scene[][] => {
  const filteredScenes = scenes.value.filter((s: Scene) => s['scenes/plot_id'] === plot['plots/id'])
  let updatedScenes = []
  for (const chapter of chapters.value) {
    const filteredScenesByChapter = filteredScenes.filter(
      (s: Scene) => s['scenes/chapter_id'] === chapter['chapters/id']
    )
    if (filteredScenesByChapter.length > 0) {
      updatedScenes.push(filteredScenesByChapter)
    } else {
      updatedScenes.push([])
    }
  }
  return updatedScenes
}

const createChapter = async () => {
  await axios
    .post<Chapter>('/api/projects/' + props.projectId + '/chapters', {
      'chapters/id': '0',
      'chapters/name': chapterName.value,
      'chapters/project_id': parseInt(props.projectId)
    })
    .then(async (response) => {
      if (response.status !== 200) {
        errorMessage.value = 'Cannot create Chapter'
        console.error('Chapter creation error: ', response.status, response.data)
        return
      }
      const isJson = response.headers['content-type'].includes('application/json')
      const data = await response.data
      if (isJson) {
        chapters.value.push(data)
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot create Chapter'
      console.error('Chapter creation error: ', error)
    })
}

const updateChapter = async (chapterId: string, chapterIndex: number) => {
  await axios
    .put<Chapter>('/api/projects/' + props.projectId + '/chapters', {
      'chapters/id': chapterId,
      'chapters/name': chapterName.value,
      'chapters/project_id': parseInt(props.projectId)
    })
    .then(async (response) => {
      if (response.status !== 200) {
        errorMessage.value = 'Cannot update Chapter'
        console.error('Chapter update error: ', response.status, response.data)
        return
      }
      const isJson = response.headers['content-type'].includes('application/json')
      const data = await response.data
      if (isJson) {
        chapters.value[chapterIndex] = data
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot update Chapter'
      console.error('Chapter update error: ', error)
    })
}

const deleteChapter = async (chapterId: string, chapterIndex: number) => {
  await axios
    .delete<Chapter>('/api/projects/' + props.projectId + '/chapters/' + chapterId)
    .then(async (response) => {
      if (response.status !== 200) {
        errorMessage.value = 'Cannot update Chapter'
        console.error('Chapter delete error: ', response.status, response.data)
        return
      }
      chapters.value.splice(chapterIndex, 1)
    })
    .catch((error) => {
      errorMessage.value = 'Cannot update Chapter'
      console.error('Chapter delete error: ', error)
    })
}

const createPlot = async () => {
  await axios
    .post<Plot>('/api/projects/' + props.projectId + '/plots', {
      'plots/id': '0',
      'plots/name': plotName.value,
      'plots/project_id': parseInt(props.projectId)
    })
    .then(async (response) => {
      if (response.status !== 200) {
        errorMessage.value = 'Cannot create Plot'
        console.error('Plot creation error: ', response.status, response.data)
        return
      }
      const isJson = response.headers['content-type'].includes('application/json')
      const data = await response.data
      if (isJson) {
        plots.value.push(data)
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot create Plot'
      console.error('Plot creation error: ', error)
    })
}

const updatePlot = async (plotId: string, plotIndex: number) => {
  await axios
    .put<Plot>('/api/projects/' + props.projectId + '/plots', {
      'plots/id': plotId,
      'plots/name': plotName.value,
      'plots/project_id': parseInt(props.projectId)
    })
    .then(async (response) => {
      if (response.status !== 200) {
        errorMessage.value = 'Cannot update Plot'
        console.error('Plot update error: ', response.status, response.data)
        return
      }
      const isJson = response.headers['content-type'].includes('application/json')
      const data = await response.data
      if (isJson) {
        plots.value[plotIndex] = data
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot update Plot'
      console.error('Plot update error: ', error)
    })
}

const deletePlot = async (plotId: string, plotIndex: number) => {
  await axios
    .delete<Plot>('/api/projects/' + props.projectId + '/plots/' + plotId)
    .then(async (response) => {
      if (response.status !== 200) {
        errorMessage.value = 'Cannot update Plot'
        console.error('Plot delete error: ', response.status, response.data)
        return
      }
      plots.value.splice(plotIndex, 1)
    })
    .catch((error) => {
      errorMessage.value = 'Cannot update Plot'
      console.error('Plot delete error: ', error)
    })
}

const createScene = async () => {
  if (sceneTitle.value === '' || chapterId.value === '' || plotId.value === '') {
    console.log('Cannot proceed to create the Scene.')
    return
  }
  await axios
    .post<Scene>('/api/projects/' + props.projectId + '/scenes', {
      'scenes/id': '0',
      'scenes/title': sceneTitle.value,
      'scenes/extract': sceneExtract.value,
      'scenes/value': '',
      'scenes/chapter_id': chapterId.value,
      'scenes/plot_id': plotId.value,
      'scenes/project_id': parseInt(props.projectId),
      'scenes/tags': []
    })
    .then(async (response) => {
      if (response.status !== 200) {
        errorMessage.value = 'Cannot create Scene'
        console.error('Scene creation error: ', response.status, response.data)
        return
      }
      const isJson = response.headers['content-type'].includes('application/json')
      const data = await response.data
      if (isJson) {
        data.visible = selectedTags.value.length == 0
        scenes.value.push(data)
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot create Scene'
      console.error('Scene creation error: ', error)
    })
}

const updateSceneTitle = async (sceneId: string) => {
  await axios
    .put<Scene>('/api/projects/' + props.projectId + '/scenes', {
      'scenes/id': sceneId,
      'scenes/title': sceneTitle.value,
      'scenes/extract': sceneExtract.value,
      'scenes/value': '',
      'scenes/chapter_id': chapterId.value,
      'scenes/plot_id': plotId.value,
      'scenes/project_id': parseInt(props.projectId),
      'scenes/tags': sceneTags.value
    })
    .then(async (response) => {
      if (response.status !== 200) {
        errorMessage.value = 'Cannot update Plot'
        console.error('Plot update error: ', response.status, response.data)
        return
      }
      const isJson = response.headers['content-type'].includes('application/json')
      const data = await response.data
      if (isJson) {
        const sceneIndex: number = scenes.value.findIndex((s) => {
          return s['scenes/id'] === sceneId
        })
        scenes.value[sceneIndex]['scenes/title'] = data['scenes/title']
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot update Plot'
      console.error('Plot update error: ', error)
    })
}

const getSceneValue = async (sceneId: string, e: Quill) => {
  await axios
    .get<Scene>('/api/projects/' + props.projectId + '/scenes/' + sceneId)
    .then(async (response) => {
      if (response.status !== 200) {
        errorMessage.value = 'Cannot retrieve Scene value'
        console.error('Scene value retrieve error: ', response.status, response.data)
        return
      }
      const data = await response.data
      sceneValue.value = ''
      const isJson = response.headers['content-type'].includes('application/json')
      if (isJson) {
        sceneValue.value = data['scenes/value']
        e.container.querySelector('.ql-editor').innerHTML = sceneValue.value
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot retrieve Scenes'
      console.error('Scenes retrieve error: ', error)
    })
}

const updateScene = async (sceneId: string) => {
  let text = sceneValueEditor.value
  while (text.endsWith('\n') || text.endsWith('\r')) {
    text = text.slice(0, -1)
  }
  await axios
    .put<Scene>('/api/projects/' + props.projectId + '/scenes/' + sceneId, {
      'scenes/id': sceneId,
      'scenes/title': sceneTitle.value,
      'scenes/extract': sceneExtract.value,
      'scenes/value': text,
      'scenes/chapter_id': chapterId.value,
      'scenes/plot_id': plotId.value,
      'scenes/project_id': parseInt(props.projectId),
      'scenes/tags': sceneTags.value
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
          return s['scenes/id'] === sceneId
        })
        data.visible = scenes.value[sceneIndex].visible
        scenes.value.splice(sceneIndex, 1, data)
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot update Scene'
      console.error('Scene update error: ', error)
    })
}

const deleteScene = async (sceneId: string) => {
  await axios
    .delete<Scene>('/api/projects/' + props.projectId + '/scenes/' + sceneId)
    .then(async (response) => {
      if (response.status !== 200) {
        errorMessage.value = 'Cannot update Scene'
        console.error('Scene delete error: ', response.status, response.data)
        return
      }
      const sceneIndex = scenes.value.findIndex((s) => {
        return s['scenes/id'] === sceneId
      })
      scenes.value.splice(sceneIndex, 1)
    })
    .catch((error) => {
      errorMessage.value = 'Cannot delete Scene'
      console.error('Scene delete error: ', error)
    })
}

const onEditorReady = (e: Quill, sceneId: string) => {
  getSceneValue(sceneId, e)
}

const remove = (item: any) => {
  sceneTags.value.splice(sceneTags.value.indexOf(item), 1)
}

const arrayIncludesAll = (arr: string[], values: string[]) => values.every((v) => arr.includes(v))

const filterScenesByTags = () => {
  if (selectedTags.value.length === 0) {
    scenes.value.forEach((s: Scene) => (s.visible = true))
  } else {
    scenes.value.forEach((s: Scene) => (s.visible = false))
    scenes.value
      .filter((s: Scene) => arrayIncludesAll(s['scenes/tags'], selectedTags.value))
      .forEach((s: Scene) => {
        console.log('scene: ' + s['scenes/title'])
        s.visible = true
      })
  }
}

const scenesContainerVisible = (sceneList: Scene[]) => {
  return sceneList.length > 0 && sceneList.filter((s: Scene) => s.visible === true).length > 0
}

onMounted(async () => {
  await getProject()
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
      <v-card class="actions-bar-container" rounded="0" flat>
        <v-toolbar>
          <v-toolbar-items>
            <v-dialog class="action-dialog">
              <template v-slot:activator="{ props: activatorProps }">
                <v-btn
                  v-bind="activatorProps"
                  text="Chapter"
                  prepend-icon="mdi-plus"
                  @click="chapterName = ''"
                ></v-btn>
              </template>

              <template v-slot:default="{ isActive }">
                <v-card title="Create Chapter">
                  <v-card-text>
                    Insert the Chapter name.
                    <v-form>
                      <v-text-field
                        v-model="chapterName"
                        label="Name"
                        hide-details
                        :counter="10"
                        required
                      ></v-text-field>
                    </v-form>
                  </v-card-text>

                  <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn text="Save" @click="[createChapter(), (isActive.value = false)]"></v-btn>
                    <v-btn text="Close" @click="isActive.value = false"></v-btn>
                  </v-card-actions>
                </v-card>
              </template>
            </v-dialog>

            <v-dialog class="action-dialog">
              <template v-slot:activator="{ props: activatorProps }">
                <v-btn
                  v-bind="activatorProps"
                  text="Plot"
                  prepend-icon="mdi-plus"
                  @click="plotName = ''"
                ></v-btn>
              </template>

              <template v-slot:default="{ isActive }">
                <v-card title="Create Plot">
                  <v-card-text>
                    Insert the Plot name.
                    <v-form>
                      <v-text-field
                        v-model="plotName"
                        label="Name"
                        hide-details
                        :counter="10"
                        required
                      ></v-text-field>
                    </v-form>
                  </v-card-text>

                  <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn text="Save" @click="[createPlot(), (isActive.value = false)]"></v-btn>
                    <v-btn text="Close" @click="isActive.value = false"></v-btn>
                  </v-card-actions>
                </v-card>
              </template>
            </v-dialog>

            <v-dialog class="action-dialog">
              <template v-slot:activator="{ props: activatorProps }">
                <v-btn
                  v-bind="activatorProps"
                  text="Scene"
                  prepend-icon="mdi-plus"
                  @click="[(sceneTitle = ''), (chapterId = ''), (plotId = ''), (sceneExtract = '')]"
                ></v-btn>
              </template>

              <template v-slot:default="{ isActive }">
                <v-form v-model="formValid" fast-fail @submit.prevent>
                  <v-card title="Create Scene">
                    <v-card-text>
                      <v-text-field
                        v-model="sceneTitle"
                        label="Name"
                        hide-details
                        :counter="10"
                        :rules="validationRules"
                      ></v-text-field>

                      <v-divider class="mt-4 pr-6"></v-divider>

                      <v-select
                        label="Chapter"
                        :items="chapters"
                        item-title="chapters/name"
                        item-value="chapters/id"
                        v-model="chapterId"
                        :rules="validationRules"
                      ></v-select>

                      <v-select
                        label="Plot"
                        :items="plots"
                        item-title="plots/name"
                        item-value="plots/id"
                        v-model="plotId"
                        :rules="validationRules"
                      ></v-select>

                      <v-textarea
                        label="Extract"
                        v-model="sceneExtract"
                        name="input-7-1"
                        variant="filled"
                        auto-grow
                      ></v-textarea>
                    </v-card-text>

                    <v-card-actions>
                      <v-spacer></v-spacer>
                      <v-btn
                        text="Save"
                        :disabled="!formValid"
                        @click="[createScene(), (isActive.value = false)]"
                      ></v-btn>
                      <v-btn text="Close" @click="isActive.value = false"></v-btn>
                    </v-card-actions>
                  </v-card>
                </v-form>
              </template>
            </v-dialog>

            <div style="margin-left: 10px; min-width: 200px">
              <v-select
                v-model="selectedTags"
                :items="projectTags"
                label="Tags"
                prepend-icon="mdi-filter-variant"
                multiple
                style="min-width: 100px"
                @update:menu="filterScenesByTags()"
              >
                <template v-slot:selection="{ item, index }">
                  <v-chip v-if="index < 2">
                    <strong>{{ item.value }}</strong
                    >&nbsp;
                  </v-chip>
                  <span v-if="index === 2" class="text-grey text-caption align-self-center">
                    (+{{ selectedTags.length - 2 }} others)
                  </span>
                </template>
              </v-select>
            </div>
          </v-toolbar-items>
        </v-toolbar>
      </v-card>

      <div class="project-map-container">
        <div class="project-map-corner" :class="{ 'theme-dark': isDark() }"></div>
        <div class="chapters-header-container" :class="{ 'theme-dark': isDark() }">
          <span class="empty-chapter"></span>
          <span class="chapter-box" v-for="(chapter, chapterIndex) in chapters" :key="chapterIndex">
            <v-sheet align="center" justify="center" class="pa-2">
              <div class="chapter-title">
                <v-dialog class="action-dialog">
                  <template v-slot:activator="{ props: activatorProps }">
                    <span>
                      <h3 style="display: inline">
                        {{ chapter['chapters/name'] }}
                      </h3>
                      <v-icon
                        icon="mdi-pencil"
                        size="19"
                        class="action-icon icon-hide"
                        v-bind="activatorProps"
                        @click="[(operation = 'update'), (chapterName = chapter['chapters/name'])]"
                      ></v-icon>
                      <v-icon
                        icon="mdi-trash-can"
                        size="19"
                        class="action-icon icon-hide"
                        v-bind="activatorProps"
                        @click="[(operation = 'delete'), (chapterName = chapter['chapters/name'])]"
                      ></v-icon>
                    </span>
                  </template>

                  <template v-slot:default="{ isActive }">
                    <v-card title="Update Chapter" v-if="operation == 'update'">
                      <v-card-text>
                        Update the Chapter name.
                        <v-form>
                          <v-text-field
                            v-model="chapterName"
                            label="Name"
                            hide-details
                            :counter="10"
                            required
                          ></v-text-field>
                        </v-form>
                      </v-card-text>

                      <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn
                          text="Save"
                          @click="
                            [
                              updateChapter(chapter['chapters/id'], chapterIndex),
                              (isActive.value = false)
                            ]
                          "
                        ></v-btn>
                        <v-btn text="Close" @click="isActive.value = false"></v-btn>
                      </v-card-actions>
                    </v-card>
                    <v-card title="Delete Chapter" v-if="operation == 'delete'">
                      <v-card-text>
                        Are you sure you want to delete the chapter
                        {{ chapter['chapters/name'] }} ?
                      </v-card-text>

                      <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn
                          text="Delete"
                          @click="
                            [
                              deleteChapter(chapter['chapters/id'], chapterIndex),
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
            </v-sheet>
          </span>
        </div>
        <div>
          <div v-for="(plot, plotIndex) in plots" :key="plotIndex" class="plot-row">
            <div class="plot-container-overlay" :class="{ 'theme-dark': isDark() }"></div>
            <span class="plot-container" :class="{ 'theme-dark': isDark() }">
              <div class="plot-title">
                <v-dialog class="action-dialog">
                  <template v-slot:activator="{ props: activatorProps }">
                    <span>
                      <h3 style="display: inline">
                        {{ plot['plots/name'] }}
                      </h3>
                      <v-icon
                        icon="mdi-pencil"
                        size="19"
                        class="action-icon icon-hide"
                        v-bind="activatorProps"
                        @click="[(operation = 'update'), (plotName = plot['plots/name'])]"
                      ></v-icon>
                      <v-icon
                        icon="mdi-trash-can"
                        size="19"
                        class="action-icon icon-hide"
                        v-bind="activatorProps"
                        @click="[(operation = 'delete'), (plotName = plot['plots/name'])]"
                      ></v-icon>
                    </span>
                  </template>

                  <template v-slot:default="{ isActive }">
                    <v-card title="Update Plot" v-if="operation == 'update'">
                      <v-card-text>
                        Update the Plot name.
                        <v-form>
                          <v-text-field
                            v-model="plotName"
                            label="Name"
                            hide-details
                            :counter="10"
                            required
                          ></v-text-field>
                        </v-form>
                      </v-card-text>

                      <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn
                          text="Save"
                          @click="
                            [updatePlot(plot['plots/id'], plotIndex), (isActive.value = false)]
                          "
                        ></v-btn>
                        <v-btn text="Close" @click="isActive.value = false"></v-btn>
                      </v-card-actions>
                    </v-card>
                    <v-card title="Delete Plot" v-if="operation == 'delete'">
                      <v-card-text>
                        Are you sure you want to delete the plot {{ plot['plots/name'] }} ?
                      </v-card-text>

                      <v-card-actions>
                        <v-spacer></v-spacer>
                        <v-btn
                          text="Delete"
                          @click="
                            [deletePlot(plot['plots/id'], plotIndex), (isActive.value = false)]
                          "
                        ></v-btn>
                        <v-btn text="Close" @click="isActive.value = false"></v-btn>
                      </v-card-actions>
                    </v-card>
                  </template>
                </v-dialog>
              </div>
            </span>

            <span
              class="scenes-container"
              :class="{ 'theme-dark': isDark() }"
              v-for="(sceneList, sceneListIndex) in filterScenes(plot)"
              :key="sceneListIndex"
            >
              <v-sheet>
                <span>
                  <v-divider class="border-opacity-1" thickness="5"></v-divider>
                </span>
              </v-sheet>

              <v-sheet
                class="pa-2 scenes-box"
                :class="{ 'theme-dark': isDark() }"
                v-if="scenesContainerVisible(sceneList)"
              >
                <span v-for="scene in sceneList">
                  <v-card
                    border="start"
                    class="mx-auto"
                    elevation="4"
                    max-width="344"
                    v-if="scene.visible === true"
                  >
                    <v-card-item>
                      <v-dialog class="action-dialog">
                        <template v-slot:activator="{ props: activatorProps }">
                          <span class="scene-title">
                            <span class="text-h7 mb-1">{{ scene['scenes/title'] }}</span>
                            <v-icon
                              icon="mdi-pencil"
                              size="19"
                              class="action-icon icon-hide"
                              v-bind="activatorProps"
                              @click="
                                [
                                  (operation = 'update'),
                                  (sceneTitle = scene['scenes/title']),
                                  (sceneExtract = scene['scenes/extract']),
                                  (chapterId = scene['scenes/chapter_id']),
                                  (plotId = scene['scenes/plot_id']),
                                  (sceneTags = scene['scenes/tags'])
                                ]
                              "
                            ></v-icon>
                            <v-icon
                              icon="mdi-trash-can"
                              size="19"
                              class="action-icon icon-hide"
                              v-bind="activatorProps"
                              @click="
                                [(operation = 'delete'), (sceneTitle = scene['scenes/title'])]
                              "
                            ></v-icon>
                          </span>
                        </template>

                        <template v-slot:default="{ isActive }">
                          <v-card title="Update Scene" v-if="operation == 'update'">
                            <v-card-text>
                              Update the Scene Title.
                              <v-form>
                                <v-text-field
                                  v-model="sceneTitle"
                                  label="Name"
                                  hide-details
                                  :counter="10"
                                  required
                                ></v-text-field>
                              </v-form>
                            </v-card-text>

                            <v-card-actions>
                              <v-spacer></v-spacer>
                              <v-btn
                                text="Save"
                                @click="
                                  [updateSceneTitle(scene['scenes/id']), (isActive.value = false)]
                                "
                              ></v-btn>
                              <v-btn text="Close" @click="isActive.value = false"></v-btn>
                            </v-card-actions>
                          </v-card>
                          <v-card title="Delete Scene" v-if="operation == 'delete'">
                            <v-card-text>
                              Are you sure you want to delete the scene
                              {{ scene['scenes/title'] }} ?
                            </v-card-text>

                            <v-card-actions>
                              <v-spacer></v-spacer>
                              <v-btn
                                text="Delete"
                                @click="[deleteScene(scene['scenes/id']), (isActive.value = false)]"
                              ></v-btn>
                              <v-btn text="Close" @click="isActive.value = false"></v-btn>
                            </v-card-actions>
                          </v-card>
                        </template>
                      </v-dialog>
                    </v-card-item>

                    <v-card-text class="bg-surface-light pt-4">
                      {{ scene['scenes/extract'] }}
                    </v-card-text>

                    <v-card-actions>
                      <v-spacer></v-spacer>
                      <v-dialog widht="100vw" height="100vw">
                        <template v-slot:activator="{ props: activatorProps }">
                          <v-btn
                            v-bind="activatorProps"
                            text="Edit"
                            size="small"
                            @click="
                              [
                                (sceneId = scene['scenes/id']),
                                (sceneTitle = scene['scenes/title']),
                                (sceneExtract = scene['scenes/extract']),
                                (chapterId = scene['scenes/chapter_id']),
                                (plotId = scene['scenes/plot_id']),
                                (sceneValueEditor = scene['scenes/value']),
                                (sceneTags = scene['scenes/tags']),
                                (sceneTab = 'content')
                              ]
                            "
                          ></v-btn>
                        </template>

                        <template v-slot:default="{ isActive }">
                          <v-card style="height: 100%">
                            <v-card-title>{{ sceneTitle }}</v-card-title>
                            <v-card-text>
                              <v-tabs v-model="sceneTab">
                                <v-tab value="content" text="Content"></v-tab>
                                <v-tab value="details" text="Details"></v-tab>
                              </v-tabs>
                              <v-tabs-window v-model="sceneTab">
                                <v-tabs-window-item value="content">
                                  <v-form>
                                    <QuillEditor
                                      ref="editor"
                                      v-model:content="sceneValueEditor"
                                      theme="snow"
                                      toolbar="full"
                                      contentType="html"
                                      @ready="onEditorReady($event, scene['scenes/id'])"
                                    />
                                  </v-form>
                                </v-tabs-window-item>

                                <v-tabs-window-item value="details">
                                  <v-form>
                                    <v-container style="max-width: 100%">
                                      <v-row>
                                        <v-col cols="12" md="3">
                                          <v-select
                                            label="Chapter"
                                            :items="chapters"
                                            item-title="chapters/name"
                                            item-value="chapters/id"
                                            v-model="chapterId"
                                            :rules="validationRules"
                                          ></v-select>
                                        </v-col>
                                        <v-col cols="12" md="3">
                                          <v-select
                                            label="Plot"
                                            :items="plots"
                                            item-title="plots/name"
                                            item-value="plots/id"
                                            v-model="plotId"
                                            :rules="validationRules"
                                          ></v-select>
                                        </v-col>
                                      </v-row>
                                      <v-row>
                                        <v-col cols="12" md="12">
                                          <v-textarea
                                            label="Extract"
                                            v-model="sceneExtract"
                                            name="input-7-1"
                                            variant="filled"
                                            rows="1"
                                            auto-grow
                                          ></v-textarea>
                                        </v-col>
                                      </v-row>
                                      <v-row>
                                        <v-col cols="12" md="12">
                                          <v-select
                                            v-model="sceneTags"
                                            :items="projectTags"
                                            label="Tags"
                                            prepend-icon="mdi-filter-variant"
                                            variant="solo"
                                            chips
                                            multiple
                                          >
                                          </v-select>
                                        </v-col>
                                      </v-row>
                                    </v-container>
                                  </v-form>
                                </v-tabs-window-item>
                              </v-tabs-window>
                            </v-card-text>

                            <v-card-actions>
                              <v-spacer></v-spacer>
                              <v-btn
                                text="Save"
                                @click="[updateScene(scene['scenes/id']), (isActive.value = false)]"
                              ></v-btn>
                              <v-btn text="Close" @click="isActive.value = false"></v-btn>
                            </v-card-actions>
                          </v-card>
                        </template>
                      </v-dialog>
                    </v-card-actions>
                  </v-card>
                  <v-divider class="border-opacity-0 mt-4 pr-6"></v-divider>
                </span>
              </v-sheet>
            </span>
          </div>
        </div>
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
.actions-bar-container {
  height: 70px;
}

.action-dialog {
  max-width: 500px;
}

.project-map-container {
  height: calc(100vh - 140px);
  overflow-y: auto;
}

.project-map-corner {
  width: 260px;
  height: 70px;
  position: absolute;
  z-index: 10;
  top: 128px;
  left: 0px;
  background-color: #ffffff;
}

.theme-dark.project-map-corner {
  background-color: #212121;
}

.chatpters-header-overlay {
  margin-top: 65px;
}

.chapters-header-container {
  position: sticky;
  z-index: 1;
  top: 0px;
  background-color: #ffffff;
  display: inline-flex;
}

.theme-dark.chapters-header-container {
  background-color: #212121;
}

.empty-chapter {
  padding-top: 10px;
  padding-bottom: 10px;
  margin: 10px;
  width: 200px;
  max-width: 200px;
  min-width: 200px;
}

.chapter-box {
  padding-top: 10;
  padding-bottom: 10px;
  margin-left: 10px;
  margin-right: 10px;
  margin-top: 10px;
  margin-bottom: 0px;
  width: 450px;
  max-width: 450px;
  min-width: 450px;
  justify-content: center;
  align-items: center;
}

.chapter-title .icon-hide {
  visibility: hidden;
}

.chapter-title:hover .icon-hide {
  visibility: visible;
}

.action-icon {
  margin-left: 10px;
  cursor: pointer;
}

.plot-row {
  display: flex;
}

.plot-container {
  padding-top: 10;
  padding-bottom: 10px;
  padding-left: 10px;
  margin-right: 10px;
  margin-top: 0px;
  margin-bottom: 0px;
  width: 200px;
  max-width: 200px;
  min-width: 200px;
  display: grid;
  align-items: center;
  position: sticky;
  left: 0px;
  z-index: 1;
  background-color: #ffffff;
}

.theme-dark.plot-container {
  background-color: #111111;
}

.plot-container-overlay {
  margin-left: 0px;
  position: fixed;
  left: 0px;
  z-index: 1;
  height: 100%;
  width: 260px;
  background-color: #ffffff;
}

.theme-dark.plot-container-overlay {
  background-color: #111111;
}

.plot-title .icon-hide {
  visibility: hidden;
}

.plot-title:hover .icon-hide {
  visibility: visible;
}

.scenes-container {
  padding-top: 0px;
  padding-bottom: 0px;
  margin-left: 10px;
  margin-right: 10px;
  margin-top: 0px;
  margin-bottom: 0px;
  width: 450px;
  max-width: 450px;
  min-width: 450px;
  min-height: 100%;
  justify-content: center;
  align-items: center;
}

.theme-dark.scenes-container {
  background-color: #424242;
}

.scenes-box {
  min-height: 200px;
  margin-top: 10px;
}

.theme-dark.scenes-box {
  background-color: #424242;
}

.scene-title .icon-hide {
  visibility: hidden;
}

.scene-title:hover .icon-hide {
  visibility: visible;
}
</style>
