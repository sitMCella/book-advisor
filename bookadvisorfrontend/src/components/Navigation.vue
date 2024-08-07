<script setup lang="ts">
import { useTheme } from 'vuetify'

const theme = useTheme()
const props = defineProps(['projectId'])

const changeTheme = () => {
  theme.global.name.value = theme.global.current.value.dark ? 'light' : 'dark'
}
</script>

<template>
  <v-navigation-drawer rail permanent>
    <v-layout>
      <v-list density="compact" nav>
        <v-list-item value="projects" :to="{ path: '/project/' + props.projectId }">
          <v-list-item-icon>
            <v-icon small>mdi-home</v-icon>
          </v-list-item-icon>
        </v-list-item>
        <v-divider></v-divider>
        <v-list-item value="projectMap" :to="{ path: '/projectmap/' + props.projectId }">
          <v-list-item-icon>
            <v-icon small>mdi-view-grid-outline</v-icon>
          </v-list-item-icon>
        </v-list-item>
        <v-divider></v-divider>
        <v-list-item value="projectBook">
          <v-list-item-icon>
            <v-icon small>mdi-book-open-blank-variant-outline</v-icon>
          </v-list-item-icon>
        </v-list-item>
      </v-list>
      <v-list style="position: fixed; bottom: 0">
        <v-list-item value="theme" @click="changeTheme">
          <v-list-item-icon>
            <v-icon small>mdi-theme-light-dark</v-icon>
          </v-list-item-icon>
        </v-list-item>
      </v-list>
    </v-layout>
  </v-navigation-drawer>
</template>
