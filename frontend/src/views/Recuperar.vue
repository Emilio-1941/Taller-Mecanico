<script setup>
import { ref } from 'vue'

const usuario = ref('')
const mensaje = ref('')
const error = ref(false)
const cargando = ref(false)

async function solicitarAyuda() {
  mensaje.value = ''
  cargando.value = true
  try {
    const response = await fetch('http://localhost:8080/api/recuperar', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ usuario: usuario.value })
    })
    const data = await response.json()
    if (!response.ok) throw new Error(data.mensaje || 'No se pudo procesar la solicitud.')
    error.value = false
    mensaje.value = data.mensaje
  } catch (e) {
    error.value = true
    mensaje.value = e.message || 'No se pudo conectar con el servidor.'
  } finally {
    cargando.value = false
  }
}
</script>

<template>
  <main class="flex min-h-screen items-center justify-center bg-zinc-100 p-6">
    <section class="w-full max-w-md border border-zinc-200 bg-white p-8 shadow-sm">
      <p class="text-sm font-medium text-zinc-500">Taller mecanico</p>
      <h1 class="mt-1 text-2xl font-semibold text-zinc-900">Recuperar acceso</h1>
      <p class="mb-8 mt-2 text-sm leading-6 text-zinc-600">Ingresa tu usuario para consultar las opciones de recuperacion.</p>
      <form class="space-y-5" @submit.prevent="solicitarAyuda">
        <label class="block">
          <span class="mb-2 block text-sm font-medium text-zinc-700">Usuario</span>
          <input v-model.trim="usuario" required autocomplete="username" class="w-full border border-zinc-300 px-3 py-2.5 outline-none focus:border-zinc-900">
        </label>
        <p v-if="mensaje" role="status" :class="error ? 'bg-red-50 text-red-700' : 'bg-emerald-50 text-emerald-700'" class="px-3 py-2 text-sm">{{ mensaje }}</p>
        <button :disabled="cargando" class="w-full bg-zinc-900 px-4 py-3 text-sm font-medium text-white hover:bg-zinc-700 disabled:opacity-60">{{ cargando ? 'Enviando...' : 'Solicitar ayuda' }}</button>
        <RouterLink to="/login" class="block text-center text-sm text-zinc-600 hover:text-zinc-900">Volver al inicio de sesion</RouterLink>
      </form>
    </section>
  </main>
</template>
