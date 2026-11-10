<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'

const talleres = ref([]), fotos = ref({}), mensaje = ref(''), error = ref(false), cargando = ref(true)
onMounted(async () => {
  try {
    const response = await fetch('http://localhost:8080/api/talleres-info')
    const data = await response.json()
    if (!response.ok) throw new Error(data.mensaje || 'No se pudo cargar la lista de talleres.')
    talleres.value = data
    await Promise.all(data.filter(t => t.tieneFoto).map(async t => {
      const image = await fetch(`http://localhost:8080/api/talleres-info/${t.id}/foto`)
      if (image.ok) fotos.value[t.id] = URL.createObjectURL(await image.blob())
    }))
  } catch (e) { mensaje.value = e.message; error.value = true }
  finally { cargando.value = false }
})
onBeforeUnmount(() => Object.values(fotos.value).forEach(URL.revokeObjectURL))
</script>

<template>
  <main class="min-h-screen bg-zinc-100 px-4 py-8 text-zinc-900">
    <section class="mx-auto max-w-7xl border border-zinc-200 bg-white p-6 shadow-sm sm:p-8">
      <header class="mb-6 flex flex-wrap items-center justify-between gap-3">
        <div><p class="text-sm text-zinc-500">Taller mecánico</p><h1 class="mt-1 text-2xl font-semibold">Talleres registrados</h1></div>
        <RouterLink to="/talleres/nuevo" class="bg-zinc-900 px-4 py-2.5 text-sm text-white">Registrar taller</RouterLink>
      </header>
      <p v-if="mensaje" role="alert" :class="error ? 'bg-red-50 text-red-700' : 'bg-zinc-100'" class="mb-4 px-3 py-2 text-sm">{{ mensaje }}</p>
      <p v-else-if="cargando" class="py-8 text-sm text-zinc-500">Cargando talleres...</p>
      <p v-else-if="!talleres.length" class="py-8 text-sm text-zinc-500">No hay talleres registrados.</p>
      <div v-else class="overflow-x-auto">
        <table class="w-full min-w-[1100px] border-collapse text-left text-sm">
          <thead><tr class="border-b border-zinc-300 text-zinc-500"><th class="p-3">Foto</th><th class="p-3">Nombre</th><th class="p-3">Razón social</th><th class="p-3">RFC</th><th class="p-3">Teléfono</th><th class="p-3">Email</th><th class="p-3">Dirección</th><th class="p-3">Número de contacto</th></tr></thead>
          <tbody>
            <tr v-for="t in talleres" :key="t.id" class="border-b border-zinc-100">
              <td class="p-3"><img v-if="fotos[t.id]" :src="fotos[t.id]" :alt="t.nombre" class="h-12 w-12 rounded-full border border-zinc-200 object-cover"><span v-else class="text-zinc-400">—</span></td>
              <td class="p-3 font-medium">{{ t.nombre }}</td><td class="p-3">{{ t.razonSocial }}</td><td class="p-3">{{ t.rfc }}</td><td class="p-3">{{ t.telefono }}</td><td class="p-3">{{ t.email }}</td>
              <td class="p-3">{{ t.calle }}, {{ t.colonia }}, {{ t.municipio }}, {{ t.estado }}, {{ t.codigoPostal }}</td><td class="p-3">{{ t.numeroContacto }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</template>
