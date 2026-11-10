<template>
  <div class="min-h-screen bg-zinc-100 p-6">
    <div class="max-w-6xl mx-auto bg-white rounded-2xl shadow-xl p-8 border border-zinc-200">
      <div class="flex items-center justify-between mb-6">
        <div>
          <p class="text-zinc-500 text-sm">Taller Mecánico</p>
          <h1 class="text-2xl font-semibold text-zinc-800">Lista de vehículos</h1>
        </div>
        <router-link to="/clientes" class="text-slate-600 hover:underline text-sm">
          Ver clientes (para registrar un vehículo)
        </router-link>
      </div>

      <div class="flex flex-wrap gap-4 mb-6 items-end">
        <div>
          <label class="block text-sm font-medium text-zinc-600 mb-1">Ordenar por</label>
          <select v-model="ordenarPor" @change="pagina = 0; cargar()"
            class="rounded-lg border border-zinc-300 px-3 py-2 text-zinc-800">
            <option value="cliente">Cliente</option>
            <option value="placa">Placa</option>
            <option value="marca">Marca</option>
            <option value="anio">Año</option>
            <option value="fecha_registro">Fecha de registro</option>
          </select>
        </div>
        <button @click="ordenAsc = !ordenAsc; pagina = 0; cargar()"
          class="px-3 py-2 rounded-lg border border-zinc-300 text-zinc-700 hover:bg-zinc-50 text-sm">
          {{ ordenAsc ? 'Ascendente ↑' : 'Descendente ↓' }}
        </button>
        <p class="text-sm text-zinc-500 ml-auto">Total: {{ total }} vehículo(s)</p>
      </div>

      <p v-if="mensaje" class="text-sm mb-4 text-red-600">{{ mensaje }}</p>
      <p v-if="cargando" class="text-zinc-500 text-sm">Cargando vehículos...</p>

      <div v-else class="overflow-x-auto">
        <table class="w-full text-sm text-left">
          <thead>
            <tr class="border-b border-zinc-200 text-zinc-500">
              <th class="py-2 pr-4">Cliente</th>
              <th class="py-2 pr-4">Placa</th>
              <th class="py-2 pr-4">VIN</th>
              <th class="py-2 pr-4">Marca / Modelo</th>
              <th class="py-2 pr-4">Año</th>
              <th class="py-2 pr-4">Color</th>
              <th class="py-2 pr-4">Km</th>
              <th class="py-2 pr-4">Estatus</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="v in vehiculos" :key="v.id" class="border-b border-zinc-100">
              <td class="py-2 pr-4 text-zinc-800">{{ v.clienteNombre }}</td>
              <td class="py-2 pr-4 font-medium text-zinc-800">{{ v.placa }}</td>
              <td class="py-2 pr-4 text-zinc-600 font-mono text-xs">{{ v.vin }}</td>
              <td class="py-2 pr-4 text-zinc-600">{{ v.marca }} {{ v.modelo }}<span v-if="v.version"> {{ v.version }}</span></td>
              <td class="py-2 pr-4 text-zinc-600">{{ v.anio }}</td>
              <td class="py-2 pr-4 text-zinc-600">{{ v.color }}</td>
              <td class="py-2 pr-4 text-zinc-600">{{ Number(v.kilometraje).toLocaleString('es-MX') }}</td>
              <td class="py-2 pr-4">
                <span :class="v.estatus === 'activo' ? 'text-green-600' : 'text-red-600'">{{ v.estatus }}</span>
              </td>
            </tr>
            <tr v-if="vehiculos.length === 0">
              <td colspan="8" class="py-6 text-center text-zinc-400">No hay vehículos registrados.</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="flex items-center justify-between mt-6">
        <button :disabled="pagina === 0" @click="pagina--; cargar()"
          class="px-3 py-1.5 rounded-lg border border-zinc-300 text-zinc-700 disabled:opacity-40">Anterior</button>
        <span class="text-sm text-zinc-500">Página {{ pagina + 1 }} de {{ paginas || 1 }}</span>
        <button :disabled="pagina + 1 >= paginas" @click="pagina++; cargar()"
          class="px-3 py-1.5 rounded-lg border border-zinc-300 text-zinc-700 disabled:opacity-40">Siguiente</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
const vehiculos = ref([]), pagina = ref(0), paginas = ref(0), total = ref(0), ordenAsc = ref(true), ordenarPor = ref('cliente'), mensaje = ref(''), cargando = ref(true)
const sesion = () => JSON.parse(localStorage.getItem('sesion') || '{}')
async function cargar() {
  cargando.value = true; mensaje.value = ''
  try {
    const q = new URLSearchParams({ pagina: String(pagina.value), orden: ordenAsc.value ? 'asc' : 'desc', ordenar_por: ordenarPor.value })
    const r = await fetch(`https://taller-mecanico-production-9449.up.railway.app/api/vehiculos?${q}`, { headers: { Authorization: `Bearer ${sesion().token || ''}` } })
    const data = await r.json()
    if (!r.ok) throw Error(data.mensaje || 'No se pudo cargar la lista de vehículos.')
    vehiculos.value = data.content; paginas.value = data.totalPages; total.value = data.totalElements
  } catch (e) { mensaje.value = e.message } finally { cargando.value = false }
}
onMounted(cargar)
</script>
