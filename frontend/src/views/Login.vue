<script setup>
import { ref } from 'vue'

const usuario = ref('')
const contrasena = ref('')
const rol = ref('Administrador')
const mensaje = ref('')
const error = ref(false)
const cargando = ref(false)

async function iniciarSesion() {
  mensaje.value = ''
  cargando.value = true
  try {
    const response = await fetch('http://localhost:8080/api/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ usuario: usuario.value, contrasena: contrasena.value, rol: rol.value })
    })
    const data = await response.json()
    if (!response.ok) throw new Error(data.mensaje || 'No se pudo iniciar sesion.')
    error.value = false
    mensaje.value = data.mensaje
    localStorage.setItem('sesion', JSON.stringify(data))
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
      <h1 class="mb-8 mt-1 text-2xl font-semibold text-zinc-900">Iniciar sesion</h1>
      <form class="space-y-5" @submit.prevent="iniciarSesion">
        <label class="block">
          <span class="mb-2 block text-sm font-medium text-zinc-700">Usuario</span>
          <input v-model.trim="usuario" required autocomplete="username" class="w-full border border-zinc-300 px-3 py-2.5 outline-none focus:border-zinc-900">
        </label>
        <label class="block">
          <span class="mb-2 block text-sm font-medium text-zinc-700">Contrasena</span>
          <input v-model="contrasena" required type="password" autocomplete="current-password" class="w-full border border-zinc-300 px-3 py-2.5 outline-none focus:border-zinc-900">
        </label>
        <label class="block">
          <span class="mb-2 block text-sm font-medium text-zinc-700">Rol</span>
          <select v-model="rol" class="w-full border border-zinc-300 bg-white px-3 py-2.5 outline-none focus:border-zinc-900">
            <option>Administrador</option><option>Gerente</option><option>Cliente</option>
          </select>
        </label>
        <p v-if="mensaje" role="status" :class="error ? 'bg-red-50 text-red-700' : 'bg-emerald-50 text-emerald-700'" class="px-3 py-2 text-sm">{{ mensaje }}</p>
        <button :disabled="cargando" class="w-full bg-zinc-900 px-4 py-3 text-sm font-medium text-white hover:bg-zinc-700 disabled:opacity-60">{{ cargando ? 'Validando...' : 'Ingresar' }}</button>
        <nav class="flex justify-between text-sm text-zinc-600">
          <RouterLink to="/registro" class="hover:text-zinc-900">Crear cuenta</RouterLink>
          <RouterLink to="/recuperar" class="hover:text-zinc-900">Olvide mi contrasena</RouterLink>
        </nav>
      </form>
    </section>
  </main>
</template>
