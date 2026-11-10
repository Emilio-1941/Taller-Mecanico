<script setup>
import { ref, watch } from 'vue'

const form = ref({ nombre: '', calle: '', colonia: '', municipio: '', estado: '', codigoPostal: '', razonSocial: '', telefono: '', rfc: '', email: '', numeroContacto: '' })
const foto = ref(null), colonias = ref([]), mensaje = ref(''), error = ref(false), cargando = ref(false)
let codigoPostalTimer, consultaPostal = 0

watch(() => form.value.codigoPostal, (value) => {
  clearTimeout(codigoPostalTimer)
  const consulta = ++consultaPostal
  form.value.estado = ''; form.value.municipio = ''; form.value.colonia = ''; colonias.value = []
  const cp = String(value || '').trim()
  if (!/^[0-9]{5}$/.test(cp)) {
    if (mensaje.value.startsWith('Código postal') || mensaje.value.startsWith('Consultando código postal')) { mensaje.value = ''; error.value = false }
    return
  }
  codigoPostalTimer = setTimeout(async () => {
    mensaje.value = 'Consultando código postal...'; error.value = false
    try {
      const response = await fetch(`http://localhost:8080/api/codigos-postales/${encodeURIComponent(cp)}`)
      const data = await response.json().catch(() => ({}))
      if (consulta !== consultaPostal) return
      if (!response.ok) throw new Error(data.mensaje || 'Código postal no encontrado.')
      form.value.estado = data.estado || ''
      form.value.municipio = data.municipio || ''
      colonias.value = Array.isArray(data.colonias) ? [...new Set(data.colonias.filter(Boolean))] : []
      if (colonias.value.length === 1) form.value.colonia = colonias.value[0]
      mensaje.value = colonias.value.length > 1 ? 'Código postal encontrado. Selecciona una colonia.' : 'Código postal encontrado.'
    } catch {
      if (consulta !== consultaPostal) return
      mensaje.value = 'Código postal no encontrado. Completa la ubicación manualmente.'; error.value = true
    }
  }, 350)
})

function onFile(event) {
  foto.value = event.target.files?.[0] || null
  if (foto.value && foto.value.size > 15 * 1024 * 1024) {
    mensaje.value = 'La foto no puede superar 15 MB.'; error.value = true; foto.value = null; event.target.value = ''
  }
}

async function guardar() {
  mensaje.value = ''; error.value = false; cargando.value = true
  try {
    const data = new FormData()
    for (const [key, value] of Object.entries(form.value)) data.append(key, key === 'rfc' ? value.trim().toUpperCase() : value)
    if (foto.value) data.append('foto', foto.value)
    const response = await fetch('http://localhost:8080/api/talleres-info', { method: 'POST', body: data })
    const body = await response.json()
    if (!response.ok) throw new Error(body.mensaje || 'No se pudo registrar el taller.')
    mensaje.value = body.mensaje
    form.value = Object.fromEntries(Object.keys(form.value).map(key => [key, '']))
    colonias.value = []; foto.value = null; document.querySelector('#foto-taller').value = ''
  } catch (e) { mensaje.value = e.message || 'Error de conexión.'; error.value = true }
  finally { cargando.value = false }
}

const fields = [
  ['nombre', 'Nombre', 'text'], ['calle', 'Calle', 'text'], ['colonia', 'Colonia', 'text'],
  ['municipio', 'Municipio', 'text'], ['estado', 'Estado', 'text'], ['codigoPostal', 'Código postal', 'text'],
  ['razonSocial', 'Razón social', 'text'], ['telefono', 'Teléfono', 'tel'], ['rfc', 'RFC', 'text'],
  ['email', 'Email', 'email'], ['numeroContacto', 'Número de contacto', 'tel']
]
</script>

<template>
  <main class="min-h-screen bg-zinc-100 px-4 py-8 text-zinc-900">
    <section class="mx-auto max-w-4xl border border-zinc-200 bg-white p-6 shadow-sm sm:p-8">
      <header class="mb-6 flex flex-wrap items-center justify-between gap-3">
        <div><p class="text-sm text-zinc-500">Taller mecánico</p><h1 class="mt-1 text-2xl font-semibold">Registro de taller</h1></div>
        <RouterLink to="/talleres" class="text-sm underline">Ver talleres</RouterLink>
      </header>
      <form @submit.prevent="guardar" class="grid gap-4 sm:grid-cols-2">
        <label v-for="[key, label, type] in fields" :key="key" class="block">
          <span class="mb-1.5 block text-sm font-medium">{{ label }}</span>
          <select v-if="key === 'colonia' && colonias.length" v-model="form.colonia" required class="w-full border border-zinc-300 bg-white px-3 py-2.5 outline-none focus:border-zinc-900">
            <option value="" disabled>Selecciona una colonia</option>
            <option v-for="colonia in colonias" :key="colonia" :value="colonia">{{ colonia }}</option>
          </select>
          <input v-else v-model="form[key]" :type="type" :maxlength="key === 'codigoPostal' ? 5 : key === 'rfc' ? 13 : undefined" :inputmode="key === 'codigoPostal' ? 'numeric' : undefined" :required="key !== 'colonia' || !colonias.length" @input="key === 'rfc' && (form.rfc = form.rfc.toUpperCase())" class="w-full border border-zinc-300 px-3 py-2.5 outline-none focus:border-zinc-900">
        </label>
        <label class="block sm:col-span-2">
          <span class="mb-1.5 block text-sm font-medium">Fotografía (JPG, PNG o WEBP, máximo 15 MB)</span>
          <input id="foto-taller" type="file" accept="image/jpeg,image/png,image/webp" @change="onFile" class="block w-full text-sm file:mr-3 file:border-0 file:bg-zinc-100 file:px-3 file:py-2">
        </label>
        <p v-if="mensaje" role="status" :class="error ? 'bg-red-50 text-red-700' : 'bg-emerald-50 text-emerald-700'" class="px-3 py-2 text-sm sm:col-span-2">{{ mensaje }}</p>
        <div class="flex flex-wrap gap-3 sm:col-span-2">
          <button :disabled="cargando" class="bg-zinc-900 px-5 py-2.5 text-sm font-medium text-white disabled:opacity-50">{{ cargando ? 'Guardando...' : 'Guardar taller' }}</button>
          <RouterLink to="/talleres" class="border border-zinc-300 px-5 py-2.5 text-sm">Lista de talleres</RouterLink>
        </div>
      </form>
    </section>
  </main>
</template>
