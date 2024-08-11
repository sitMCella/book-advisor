<script setup lang="ts">
import ProjectBook from '../components/ProjectBook.vue'
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <main>
    <ProjectBook :projectId="route.params.id" />
  </main>
</template>
