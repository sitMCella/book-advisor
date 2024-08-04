<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Project from '../components/Project.vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const projectId = ref<number>(0)

const selectProject = async (selectedProjectId: number) => {
  projectId.value = selectedProjectId
}

onMounted(async () => {
  projectId.value = parseInt(route.params.id[0])
})
</script>

<template>
  <main>
    <Project :projectId="projectId" @selected-project-id="(id) => selectProject(id)" />
  </main>
</template>
