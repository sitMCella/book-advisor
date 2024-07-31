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

const getChapters = async () => {
  await axios
    .get<Chapter[]>('/api/chapters')
    .then(async (response) => {
      const data = await response.data
      chapters.value = []
      const isJson = response.headers['content-type'].includes('application/json')
      if (isJson) {
        chapters.value = data
      }
      if (response.status !== 200) {
        errorMessage.value = 'Cannot retrieve Chapters'
        console.error('Chapters retrieve error: ', response.status, response.data)
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
      const data = await response.data
      plots.value = []
      const isJson = response.headers['content-type'].includes('application/json')
      if (isJson) {
        plots.value = data
      }
      if (response.status !== 200) {
        errorMessage.value = 'Cannot retrieve Plots'
        console.error('Plots retrieve error: ', response.status, response.data)
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
      const data = await response.data
      scenes.value = []
      const isJson = response.headers['content-type'].includes('application/json')
      if (isJson) {
        scenes.value = data
      }
      if (response.status !== 200) {
        errorMessage.value = 'Cannot retrieve Scenes'
        console.error('Scenes retrieve error: ', response.status, response.data)
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot retrieve Scenes'
      console.error('Scenes retrieve error: ', error)
    })
}

const filterScenes = (plot: Plot): Scene[] => {
  const filteredScenes = scenes.value.filter((s: Scene) => s['scenes/plot_id'] === plot['plots/id'])
  let updatedScenes = []
  for (const chapter of chapters.value) {
    const filteredScenesByChapter = filteredScenes.filter(
      (s: Scene) => s['scenes/chapter_id'] === chapter['chapters/id']
    )
    if (filteredScenesByChapter.length === 1) {
      updatedScenes.push(filteredScenesByChapter[0])
    } else {
      updatedScenes.push({
        'scenes/id': '-1',
        'scenes/title': '',
        'scenes/extract': '',
        'scenes/value': '',
        'scenes/chapter_id': chapter['chapters/id'],
        'scenes/plot_id': plot['plots/id']
      })
    }
  }
  return updatedScenes
}

const createChapter = async () => {
  await axios
    .post<Chapter>('/api/chapters', { 'chapters/id': '0', 'chapters/name': chapterName.value })
    .then(async (response) => {
      const isJson = response.headers['content-type'].includes('application/json')
      const data = await response.data
      if (isJson) {
        chapters.value.push(data)
      }
      if (response.status !== 200) {
        errorMessage.value = 'Cannot create Chapter'
        console.error('Chapter creation error: ', response.status, response.data)
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
      const isJson = response.headers['content-type'].includes('application/json')
      const data = await response.data
      if (isJson) {
        chapters.value[chapterIndex] = data
      }
      if (response.status !== 200) {
        errorMessage.value = 'Cannot update Chapter'
        console.error('Chapter update error: ', response.status, response.data)
      }
    })
    .catch((error) => {
      errorMessage.value = 'Cannot update Chapter'
      console.error('Chapter update error: ', error)
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
            <v-btn prepend-icon="mdi-plus">Plot</v-btn>
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
                      <h3
                        style="display: inline"
                      >
                        {{ chapter['chapters/name'] }}
                      </h3>
                      <v-icon
                        icon="mdi-pencil"
                        size="19"
                        style="margin-left: 10px; cursor: pointer"
                        class="icon-hide"
                        v-bind="activatorProps"
                        @click="chapterName = chapter['chapters/name']"
                      ></v-icon>
                    </span>
                  </template>

                  <template v-slot:default="{ isActive }">
                    <v-card title="Update Chapter">
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
              <v-sheet align="center" justify="center" class="pa-2">
                <h3>{{ plot['plots/name'] }}</h3>
              </v-sheet>
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
              v-for="(scene, sceneIndex) in filterScenes(plot)"
              :key="sceneIndex"
            >
              <v-sheet class="pa-2" color="grey-lighten-3" style="min-height: 200px">
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
  visibility:hidden;
}

.chapter-title:hover .icon-hide {
  visibility:visible;
}
</style>
