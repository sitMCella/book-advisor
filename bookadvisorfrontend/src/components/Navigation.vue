<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
</script>

<template>
    <v-navigation-drawer rail>
        <v-list density="compact" nav>
            <v-list-item prepend-icon="mdi-home" title="Projects" value="projects" :to="{path: '/'}"></v-list-item>
            <v-divider></v-divider>
            <v-list-item
            prepend-icon="mdi-view-grid-outline"
            title="Project Map"
            value="projectMap"
            :to="{path: '/projectmap'}"
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
