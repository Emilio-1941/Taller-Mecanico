<template>
  <div class="min-h-screen bg-zinc-100 p-6">
    <div class="max-w-5xl mx-auto bg-white rounded-2xl shadow-xl p-8 border border-zinc-200">
      <div class="flex items-center justify-between mb-6">
        <div>
          <p class="text-zinc-500 text-sm">Taller Mecánico</p>
          <h1 class="text-2xl font-semibold text-zinc-800">Lista de clientes</h1>
        </div>
        <router-link to="/clientes/nuevo" class="text-slate-600 hover:underline text-sm">
          Registrar cliente
        </router-link>
      </div>

      <!-- Filtros -->
      <div class="flex flex-wrap gap-4 mb-6 items-end">
        <div>
          <label class="block text-sm font-medium text-zinc-600 mb-1">Ordenar por</label>
          <select v-model="ordenarPor" @change="cargar()"
            class="rounded-lg border border-zinc-300 px-3 py-2 text-zinc-800">
            <option value="nombre_completo">Nombre</option>
            <option value="fecha_registro">Fecha de registro</option>
          </select>
        </div>

        <button @click="ordenAsc = !ordenAsc; cargar()"
          class="px-3 py-2 rounded-lg border border-zinc-300 text-zinc-700 hover:bg-zinc-50 text-sm">
          {{ ordenAsc ? 'Ascendente ↑' : 'Descendente ↓' }}
        </button>

        <label v-if="esAdmin" class="flex items-center gap-2 text-sm text-zinc-600">
          <input type="checkbox" v-model="incluirSuspendidos" @change="pagina = 0; cargar()" />
          Incluir suspendidos
        </label>
      </div>

      <!-- Mensaje de error -->
      <p v-if="mensaje" :class="error ? 'text-red-600' : 'text-green-600'" class="text-sm mb-4">
        {{ mensaje }}
      </p>

      <!-- Cargando -->
      <p v-if="cargando" class="text-zinc-500 text-sm">Cargando clientes...</p>

      <!-- Tabla de clientes -->
      <div v-else class="overflow-x-auto">
        <table class="w-full text-sm text-left">
          <thead>
            <tr class="border-b border-zinc-200 text-zinc-500">
              <th class="py-2 pr-4">Foto</th>
              <th class="py-2 pr-4">Nombre</th>
              <th class="py-2 pr-4">Email</th>
              <th class="py-2 pr-4">Teléfono</th>
              <th class="py-2 pr-4">Taller</th>
              <th class="py-2 pr-4">Estado</th>
              <th class="py-2 pr-4">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="c in clientes" :key="c.id" class="border-b border-zinc-100">
              <td class="py-2 pr-4">
                <img v-if="fotoUrls[c.id]" :src="fotoUrls[c.id]"
                  class="w-10 h-10 rounded-full object-cover" />
                <span v-else class="text-zinc-300">—</span>
              </td>
              <td class="py-2 pr-4 text-zinc-800">{{ c.nombreCompleto }}</td>
              <td class="py-2 pr-4 text-zinc-600">{{ c.emailPersonal }}</td>
              <td class="py-2 pr-4 text-zinc-600">{{ c.telefonoPersonal }}</td>
              <td class="py-2 pr-4 text-zinc-600">{{ c.tallerNombre || c.tallerId }}</td>
              <td class="py-2 pr-4">
                <span :class="c.activo === false ? 'text-red-600' : 'text-green-600'">
                  {{ c.activo === false ? 'Suspendido' : 'Activo' }}
                </span>
              </td>
              <td class="py-2 pr-4 space-x-2">
                <button @click="router.push(`/clientes/${c.id}/editar`)"
                  class="text-slate-600 hover:underline">Editar</button>
                <button v-if="puedeRegistrarVehiculo && c.activo === true" @click="abrirModalVehiculo(c)"
                  class="text-slate-700 hover:underline">Registrar vehículo</button>
                <button v-if="esAdmin && c.activo !== false" @click="suspender(c)"
                  class="text-red-600 hover:underline">Suspender</button>
              </td>
            </tr>
            <tr v-if="clientes.length === 0">
              <td colspan="7" class="py-6 text-center text-zinc-400">No hay clientes para mostrar.</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Paginación -->
      <div class="flex items-center justify-between mt-6">
        <button :disabled="pagina === 0" @click="pagina--; cargar()"
          class="px-3 py-1.5 rounded-lg border border-zinc-300 text-zinc-700 disabled:opacity-40">
          Anterior
        </button>
        <span class="text-sm text-zinc-500">Página {{ pagina + 1 }} de {{ paginas || 1 }}</span>
        <button :disabled="pagina + 1 >= paginas" @click="pagina++; cargar()"
          class="px-3 py-1.5 rounded-lg border border-zinc-300 text-zinc-700 disabled:opacity-40">
          Siguiente
        </button>
      </div>
    </div>
    <div v-if="mostrarModalVehiculo" class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4" role="dialog" aria-modal="true" aria-labelledby="vehiculo-title" @click.self="cerrarModalVehiculo" @keydown.esc.window="cerrarModalVehiculo">
      <section class="max-h-[90vh] w-full max-w-3xl overflow-y-auto rounded-xl border border-zinc-200 bg-white p-6 shadow-xl">
        <header class="mb-5 flex items-start justify-between gap-4"><div><p class="text-sm text-zinc-500">Registro de vehículo</p><h2 id="vehiculo-title" class="mt-1 text-xl font-semibold text-zinc-900">Nuevo vehículo</h2></div><button type="button" aria-label="Cerrar" @click="cerrarModalVehiculo" class="px-2 text-xl text-zinc-500 hover:text-zinc-900">×</button></header>
        <div class="mb-5 border-l-2 border-zinc-800 pl-3"><p class="text-xs text-zinc-500">Cliente activo seleccionado</p><p class="font-medium">{{ clienteSeleccionado?.nombreCompleto }}</p><p class="text-sm text-zinc-600">{{ clienteSeleccionado?.emailPersonal }}</p></div>
        <form class="grid gap-4 sm:grid-cols-2" @submit.prevent="registrarVehiculo">
          <label class="text-sm font-medium">VIN<input v-model="vehiculoForm.vin" required maxlength="40" autocomplete="off" class="mt-1 block w-full border border-zinc-300 px-3 py-2.5 outline-none focus:border-zinc-900" /></label>
          <label class="text-sm font-medium">Placa<input v-model="vehiculoForm.placa" required maxlength="30" class="mt-1 block w-full border border-zinc-300 px-3 py-2.5 outline-none focus:border-zinc-900" /></label>
          <label class="text-sm font-medium">Marca<input v-model="vehiculoForm.marca" required maxlength="60" class="mt-1 block w-full border border-zinc-300 px-3 py-2.5 outline-none focus:border-zinc-900" /></label>
          <label class="text-sm font-medium">Modelo<input v-model="vehiculoForm.modelo" required maxlength="60" class="mt-1 block w-full border border-zinc-300 px-3 py-2.5 outline-none focus:border-zinc-900" /></label>
          <label class="text-sm font-medium">Año<input v-model.number="vehiculoForm.anio" required type="number" min="1980" :max="new Date().getFullYear()+1" class="mt-1 block w-full border border-zinc-300 px-3 py-2.5 outline-none focus:border-zinc-900" /></label>
          <label class="text-sm font-medium">Versión (opcional)<input v-model="vehiculoForm.version" maxlength="80" class="mt-1 block w-full border border-zinc-300 px-3 py-2.5 outline-none focus:border-zinc-900" /></label>
          <label class="text-sm font-medium">Color<input v-model="vehiculoForm.color" required maxlength="40" class="mt-1 block w-full border border-zinc-300 px-3 py-2.5 outline-none focus:border-zinc-900" /></label>
          <label class="text-sm font-medium">Kilometraje<input v-model.number="vehiculoForm.kilometraje" required type="number" min="0" class="mt-1 block w-full border border-zinc-300 px-3 py-2.5 outline-none focus:border-zinc-900" /></label>
          <label class="text-sm font-medium sm:col-span-2">Número de serie<input v-model="vehiculoForm.numeroSerie" required maxlength="40" class="mt-1 block w-full border border-zinc-300 px-3 py-2.5 outline-none focus:border-zinc-900" /></label>
          <p v-if="vehiculoMensaje" role="alert" :class="vehiculoError?'bg-red-50 text-red-700':'bg-emerald-50 text-emerald-700'" class="px-3 py-2 text-sm sm:col-span-2">{{ vehiculoMensaje }}</p>
          <div class="flex justify-end gap-3 sm:col-span-2"><button type="button" @click="cerrarModalVehiculo" class="border border-zinc-300 px-4 py-2.5 text-sm">Cancelar</button><button :disabled="guardandoVehiculo" class="bg-zinc-900 px-4 py-2.5 text-sm font-medium text-white disabled:opacity-50">{{guardandoVehiculo?'Guardando...':'Registrar vehículo'}}</button></div>
        </form>
      </section>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount,onMounted,ref } from 'vue'
import { useRouter } from 'vue-router'
const router=useRouter(),clientes=ref([]),pagina=ref(0),paginas=ref(0),ordenAsc=ref(true),ordenarPor=ref('nombre_completo'),incluirSuspendidos=ref(false),mensaje=ref(''),error=ref(false),cargando=ref(true),fotoUrls=ref({}),esAdmin=ref(false),puedeRegistrarVehiculo=ref(false)
const mostrarModalVehiculo=ref(false),clienteSeleccionado=ref(null),guardandoVehiculo=ref(false),vehiculoMensaje=ref(''),vehiculoError=ref(false)
const vehiculoVacio=()=>({clienteId:null,vin:'',placa:'',marca:'',modelo:'',anio:new Date().getFullYear(),version:'',color:'',kilometraje:0,numeroSerie:''})
const vehiculoForm=ref(vehiculoVacio())
const sesion=()=>JSON.parse(localStorage.getItem('sesion')||'{}'),headers=()=>({Authorization:`Bearer ${sesion().token||''}`})
async function cargar(){cargando.value=true;mensaje.value='';try{const q=new URLSearchParams({pagina:String(pagina.value),orden:ordenAsc.value?'asc':'desc',ordenar_por:ordenarPor.value,incluir_suspendidos:String(incluirSuspendidos.value)});const r=await fetch(`http://localhost:8080/api/clientes?${q}`,{headers:headers()}),data=await r.json();if(!r.ok)throw Error(data.mensaje||'No se pudo cargar la lista.');clientes.value=data.content;paginas.value=data.totalPages;Object.values(fotoUrls.value).forEach(URL.revokeObjectURL);fotoUrls.value={};await Promise.all(clientes.value.filter(c=>c.tieneFoto).map(async c=>{const img=await fetch(`http://localhost:8080/api/clientes/${c.id}/foto`,{headers:headers()});if(img.ok)fotoUrls.value[c.id]=URL.createObjectURL(await img.blob())}))}catch(e){mensaje.value=e.message;error.value=true}finally{cargando.value=false}}
function abrirModalVehiculo(c){if(!puedeRegistrarVehiculo.value||c.activo!==true)return;clienteSeleccionado.value=c;vehiculoForm.value={...vehiculoVacio(),clienteId:c.id};vehiculoMensaje.value='';vehiculoError.value=false;mostrarModalVehiculo.value=true}
function cerrarModalVehiculo(){if(guardandoVehiculo.value)return;mostrarModalVehiculo.value=false;clienteSeleccionado.value=null}
async function registrarVehiculo(){
 if(!clienteSeleccionado.value||clienteSeleccionado.value.activo!==true)return
 guardandoVehiculo.value=true;vehiculoMensaje.value='';vehiculoError.value=false
 try{
  const r=await fetch('http://localhost:8080/api/vehiculos',{method:'POST',headers:{...headers(),'Content-Type':'application/json'},body:JSON.stringify(vehiculoForm.value)})
  const data=await r.json().catch(()=>({}))
  if(!r.ok)throw Error(data.mensaje||'No se pudo registrar el vehículo.')
  vehiculoMensaje.value=data.mensaje||'Vehículo registrado correctamente.'
  mensaje.value=vehiculoMensaje.value;error.value=false
  cerrarModalVehiculo()
 }catch(e){vehiculoError.value=true;vehiculoMensaje.value=e.message||'No se pudo conectar con el servidor.'}
 finally{guardandoVehiculo.value=false}
}
async function suspender(c){if(!confirm(`¿Suspender a ${c.nombreCompleto}?`))return;try{const r=await fetch(`http://localhost:8080/api/clientes/${c.id}/suspender`,{method:'PATCH',headers:headers()}),data=await r.json();if(!r.ok)throw Error(data.mensaje||'No se pudo suspender.');await cargar()}catch(e){mensaje.value=e.message;error.value=true}}
onMounted(async()=>{const rol=sesion().rol?.toUpperCase();esAdmin.value=rol==='ADMINISTRADOR';puedeRegistrarVehiculo.value=['ADMINISTRADOR','RECEPCIONISTA'].includes(rol);try{await cargar()}catch(e){mensaje.value=e.message;error.value=true;cargando.value=false}})
onBeforeUnmount(()=>Object.values(fotoUrls.value).forEach(URL.revokeObjectURL))
</script>
