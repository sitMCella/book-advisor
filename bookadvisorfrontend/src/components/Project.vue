<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import AppBar from './AppBar.vue'
import Navigation from './Navigation.vue'
</script>

<template>
    <v-app>
        <AppBar />
        <Navigation />
        <v-main>
            Projects
        </v-main>
    </v-app>
</template>
