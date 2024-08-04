<script setup lang="ts">
const props = defineProps(['projectId'])
</script>

<template>
  <v-navigation-drawer rail>
    <v-list density="compact" nav>
      <v-list-item
        prepend-icon="mdi-home"
        title="Projects"
        value="projects"
        :to="{ path: '/project/' + props.projectId }"
      ></v-list-item>
      <v-divider></v-divider>
      <v-list-item
        prepend-icon="mdi-view-grid-outline"
        title="Project Map"
        value="projectMap"
        :to="{ path: '/projectmap/' + props.projectId }"
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
</template>
