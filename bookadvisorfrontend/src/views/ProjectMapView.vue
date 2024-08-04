<script setup lang="ts">
import ProjectMap from '../components/ProjectMap.vue'
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <main>
    <ProjectMap :projectId="route.params.id" />
  </main>
</template>
