<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

defineProps<{
  msg: string
}>()

interface Chapter {
  'chapters/id': string
  'chapters/name': string
}

interface Plot {
  'plots/id': string
  'plots/name': string
}

interface Scene {
  'scenes/id': string
  'scenes/title': string
  'scenes/extract': string
  'scenes/value': string
  'scenes/chapter_id': string
  'scenes/plot_id': string
}

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

const validationRules = [
  (value: string) => {
    if (value) return true
    return 'Required field.'
  }
]

const getChapters = async () => {
  await axios
    .get<Chapter[]>('/api/chapters')
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
    .get<Plot[]>('/api/plots')
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
    .get<Scene[]>('/api/scenes')
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
      updatedScenes.push([
        {
          'scenes/id': '-1',
          'scenes/title': '',
          'scenes/extract': '',
          'scenes/value': '',
          'scenes/chapter_id': chapter['chapters/id'],
          'scenes/plot_id': plot['plots/id']
        }
      ])
    }
  }
  return updatedScenes
}

const createChapter = async () => {
  await axios
    .post<Chapter>('/api/chapters', { 'chapters/id': '0', 'chapters/name': chapterName.value })
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
    .put<Chapter>('/api/chapters', { 'chapters/id': chapterId, 'chapters/name': chapterName.value })
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
    .delete<Chapter>('/api/chapters/' + chapterId)
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
    .post<Plot>('/api/plots', { 'plots/id': '0', 'plots/name': plotName.value })
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
    .put<Plot>('/api/plots', { 'plots/id': plotId, 'plots/name': plotName.value })
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
    .delete<Plot>('/api/plots/' + plotId)
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
    .post<Scene>('/api/scenes', {
      'scenes/id': '0',
      'scenes/title': sceneTitle.value,
      'scenes/extract': sceneExtract.value,
      'scenes/value': '',
      'scenes/chapter_id': chapterId.value,
      'scenes/plot_id': plotId.value
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
        scenes.value.push(data)
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot create Scene'
      console.error('Scene creation error: ', error)
    })
}

onMounted(async () => {
  await getChapters()
  await getPlots()
  await getScenes()
})
</script>

<template>
  <v-app>
    <v-app-bar :elevation="2">
      <template v-slot:prepend>
        <v-app-bar-nav-icon></v-app-bar-nav-icon>
      </template>

      <v-app-bar-title>Book Advisor</v-app-bar-title>
    </v-app-bar>
    <v-navigation-drawer rail>
      <v-list density="compact" nav>
        <v-list-item prepend-icon="mdi-home" title="Projects" value="projects"></v-list-item>
        <v-divider></v-divider>
        <v-list-item
          prepend-icon="mdi-view-grid-outline"
          title="Project Map"
          value="projectMap"
        ></v-list-item>
        <v-divider></v-divider>
        <v-list-item
          prepend-icon="mdi-book-open-blank-variant-outline"
          title="Project Book"
          value="projectBook"
        ></v-list-item>
        <v-divider></v-divider>
      </v-list>
    </v-navigation-drawer>
    <v-main>
      <v-card color="grey-lighten-4" height="70px" rounded="0" flat>
        <v-toolbar>
          <v-toolbar-items>
            <v-dialog max-width="500">
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

            <v-dialog max-width="500">
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

            <v-dialog max-width="500">
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
          </v-toolbar-items>
        </v-toolbar>
      </v-card>

      <div style="overflow-x: scroll">
        <div style="display: inline-flex">
          <span
            style="
              padding-top: 10px;
              padding-bottom: 10px;
              margin: 10px;
              width: 200px;
              max-width: 200px;
              min-width: 200px;
            "
          ></span>
          <span
            style="
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
            "
            v-for="(chapter, chapterIndex) in chapters"
            :key="chapterIndex"
          >
            <v-sheet align="center" justify="center" class="pa-2" color="grey-lighten-3">
              <div class="chapter-title">
                <v-dialog max-width="500">
                  <template v-slot:activator="{ props: activatorProps }">
                    <span>
                      <h3 style="display: inline">
                        {{ chapter['chapters/name'] }}
                      </h3>
                      <v-icon
                        icon="mdi-pencil"
                        size="19"
                        style="margin-left: 10px; cursor: pointer"
                        class="icon-hide"
                        v-bind="activatorProps"
                        @click="[(operation = 'update'), (chapterName = chapter['chapters/name'])]"
                      ></v-icon>
                      <v-icon
                        icon="mdi-trash-can"
                        size="19"
                        style="margin-left: 10px; cursor: pointer"
                        class="icon-hide"
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
                        Are you sure you want to delete the chapter {{ chapter['chapters/name'] }} ?
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
          <div v-for="(plot, plotIndex) in plots" :key="plotIndex" style="display: flex">
            <span
              style="
                padding-top: 10;
                padding-bottom: 10px;
                margin-left: 10px;
                margin-right: 10px;
                margin-top: 0px;
                margin-bottom: 0px;
                width: 200px;
                max-width: 200px;
                min-width: 200px;
                justify-content: center;
                align-items: center;
              "
            >
              <div class="plot-title">
                <v-dialog max-width="500">
                  <template v-slot:activator="{ props: activatorProps }">
                    <span>
                      <h3 style="display: inline">
                        {{ plot['plots/name'] }}
                      </h3>
                      <v-icon
                        icon="mdi-pencil"
                        size="19"
                        style="margin-left: 10px; cursor: pointer"
                        class="icon-hide"
                        v-bind="activatorProps"
                        @click="[(operation = 'update'), (plotName = plot['plots/name'])]"
                      ></v-icon>
                      <v-icon
                        icon="mdi-trash-can"
                        size="19"
                        style="margin-left: 10px; cursor: pointer"
                        class="icon-hide"
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
              style="
                padding-top: 10;
                padding-bottom: 10px;
                margin-left: 10px;
                margin-right: 10px;
                margin-top: 0px;
                margin-bottom: 0px;
                width: 450px;
                max-width: 450px;
                min-width: 450px;
                height: 100%;
                min-height: 100%;
                justify-content: center;
                align-items: center;
                background-color: #f5f5f5;
              "
              v-for="(sceneList, sceneListIndex) in filterScenes(plot)"
              :key="sceneListIndex"
            >
              <v-sheet class="pa-2" color="grey-lighten-3" style="min-height: 200px">
                <span v-for="scene in sceneList">
                  <v-card
                    border="start"
                    class="mx-auto"
                    elevation="4"
                    max-width="344"
                    v-if="scene['scenes/id'] !== '-1'"
                  >
                    <v-card-item>
                      <v-card-title>
                        {{ scene['scenes/title'] }}
                      </v-card-title>
                    </v-card-item>

                    <v-card-text class="bg-surface-light pt-4">
                      {{ scene['scenes/extract'] }}
                    </v-card-text>
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
.chapter-title .icon-hide {
  visibility: hidden;
}

.chapter-title:hover .icon-hide {
  visibility: visible;
}

.plot-title .icon-hide {
  visibility: hidden;
}

.plot-title:hover .icon-hide {
  visibility: visible;
}
</style>
