<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const mostrarNavegacion = computed(() => ['/clientes/nuevo', '/clientes', '/vehiculos', '/talleres/nuevo', '/talleres'].includes(route.path) || (route.path.startsWith('/clientes/') && route.path.endsWith('/editar')))
const enlaces = [
  { texto: 'Registrar cliente', ruta: '/clientes/nuevo' },
  { texto: 'Ver clientes', ruta: '/clientes' },
  { texto: 'Ver vehículos', ruta: '/vehiculos' },
  { texto: 'Registrar taller', ruta: '/talleres/nuevo' },
  { texto: 'Ver talleres', ruta: '/talleres' }
]
</script>

<template>
  <div>
    <nav v-if="mostrarNavegacion" aria-label="Navegación de módulos" class="border-b border-zinc-200 bg-white px-4 text-zinc-700">
      <div class="mx-auto flex max-w-7xl flex-wrap gap-x-6 gap-y-1">
        <RouterLink
          v-for="enlace in enlaces"
          :key="enlace.ruta"
          :to="enlace.ruta"
          class="border-b-2 px-1 py-3 text-sm transition-colors hover:text-zinc-950"
          :class="route.path === enlace.ruta ? 'border-zinc-900 font-medium text-zinc-950' : 'border-transparent'"
          :aria-current="route.path === enlace.ruta ? 'page' : undefined"
        >{{ enlace.texto }}</RouterLink>
      </div>
    </nav>
    <RouterView />
  </div>
</template>
