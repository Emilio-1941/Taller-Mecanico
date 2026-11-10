<script setup>
import { onMounted,ref,watch } from 'vue'
import { useRoute,useRouter } from 'vue-router'
const form=ref({nombreCompleto:'',contactoAlternativo:'',edad:'',fechaNacimiento:'',telefonoPersonal:'',telefonoTrabajo:'',emailPersonal:'',emailTrabajo:'',calle:'',colonia:'',municipio:'',estado:'',codigoPostal:'',tallerId:''})
const route=useRoute(),router=useRouter(),editarId=route.params.id||null,talleres=ref([])
let precargando=false
const sesionActual=()=>JSON.parse(localStorage.getItem('sesion')||'{}')
onMounted(async()=>{
 const headers={Authorization:`Bearer ${sesionActual().token||''}`}
 try{
  const tr=await fetch('http://localhost:8080/api/clientes/talleres',{headers}),ts=await tr.json()
  if(!tr.ok)throw Error(ts.mensaje||'No se pudieron cargar los talleres.')
  talleres.value=ts
 }catch(e){mensaje.value=e.message;error.value=true}
 if(!editarId)return
 const headers2={Authorization:`Bearer ${sesionActual().token||''}`}
 try{
  const r=await fetch(`http://localhost:8080/api/clientes/${editarId}`,{headers:headers2}),c=await r.json()
  if(!r.ok)throw Error(c.mensaje||'No se pudo cargar el cliente.')
  precargando=true;for(const k of Object.keys(form.value))form.value[k]=c[k]??'';precargando=false
 }catch(e){precargando=false;mensaje.value=e.message;error.value=true}
})
const foto=ref(null),mensaje=ref(''),error=ref(false),cargando=ref(false),colonias=ref([])
let codigoPostalTimer,consultaPostal=0,ubicacionAutocompletada={estado:'',municipio:'',colonia:''}
watch(()=>form.value.codigoPostal,codigo=>{if(precargando)return
 clearTimeout(codigoPostalTimer)
 const consulta=++consultaPostal
 for(const campo of ['estado','municipio','colonia'])if(ubicacionAutocompletada[campo]&&form.value[campo]===ubicacionAutocompletada[campo])form.value[campo]=''
 ubicacionAutocompletada={estado:'',municipio:'',colonia:''};colonias.value=[];form.value.colonia=''
 const cp=String(codigo||'').trim()
 if(cp.length!==5||!Array.from(cp).every(ch=>ch>='0'&&ch<='9')){if(mensaje.value.startsWith('Código postal')||mensaje.value.startsWith('Consultando código postal')){mensaje.value='';error.value=false}return}
 codigoPostalTimer=setTimeout(async()=>{
  mensaje.value='Consultando código postal...';error.value=false
  try{
   const respuesta=await fetch('http://localhost:8080/api/codigos-postales/'+encodeURIComponent(cp))
   const datos=await respuesta.json().catch(()=>({}))
   if(consulta!==consultaPostal)return
   if(!respuesta.ok)throw new Error(respuesta.status===404?'no encontrado':'servicio no disponible')
   form.value.estado=datos.estado||'';form.value.municipio=datos.municipio||''
   ubicacionAutocompletada.estado=form.value.estado;ubicacionAutocompletada.municipio=form.value.municipio
   colonias.value=Array.isArray(datos.colonias)?[...new Set(datos.colonias.filter(Boolean).map(x=>String(x).trim()))]:[]
   if(colonias.value.length===1){form.value.colonia=colonias.value[0];ubicacionAutocompletada.colonia=form.value.colonia}
   mensaje.value=colonias.value.length>1?'Código postal encontrado. Selecciona una colonia.':colonias.value.length===1?'Código postal encontrado.':'Código postal encontrado. Completa la colonia manualmente.'
  }catch(e){if(consulta!==consultaPostal)return;mensaje.value=e.message==='no encontrado'?'Código postal no encontrado. Puedes completar los datos manualmente.':'No se pudo consultar el código postal. Puedes completar los datos manualmente.';error.value=true}
 },350)
})
function onFile(e){foto.value=e.target.files?.[0]||null;if(foto.value&&foto.value.size>7*1024*1024){mensaje.value='La foto supera 7 MB.';error.value=true;foto.value=null;e.target.value=''}}
async function guardar(){mensaje.value='';error.value=false;cargando.value=true;try{const data=new FormData();for(const [k,v] of Object.entries(form.value))data.append(k,v);if(foto.value)data.append('foto',foto.value);const sesion=sesionActual();const url=editarId?`http://localhost:8080/api/clientes/${editarId}`:'http://localhost:8080/api/clientes';const r=await fetch(url,{method:editarId?'PUT':'POST',headers:{Authorization:`Bearer ${sesion.token||''}`},body:data});const body=await r.json();if(!r.ok)throw Error(body.mensaje||'No se pudo guardar.');mensaje.value=body.mensaje||'Cliente actualizado correctamente.';if(editarId)await router.push('/clientes');else{form.value=Object.fromEntries(Object.keys(form.value).map(k=>[k,'']));foto.value=null;document.querySelector('#foto-cliente').value=''}}catch(e){mensaje.value=e.message||'Error de conexion.';error.value=true}finally{cargando.value=false}}
const fields=[['nombreCompleto','Nombre completo','text'],['contactoAlternativo','Contacto alternativo','text'],['edad','Edad','number'],['fechaNacimiento','Fecha de nacimiento','date'],['telefonoPersonal','Telefono personal','tel'],['telefonoTrabajo','Telefono de trabajo','tel'],['emailPersonal','Email personal','email'],['emailTrabajo','Email de trabajo (opcional)','email'],['calle','Calle','text'],['colonia','Colonia','text'],['municipio','Municipio','text'],['estado','Estado','text'],['codigoPostal','Codigo postal','text'],['tallerId','Taller','number']]
</script>
<template>
 <main class="min-h-screen bg-zinc-100 px-4 py-8 text-zinc-900"><section class="mx-auto max-w-4xl border border-zinc-200 bg-white p-6 shadow-sm sm:p-8">
  <header class="mb-6 flex flex-wrap items-center justify-between gap-3"><div><p class="text-sm text-zinc-500">Taller mecánico</p><h1 class="mt-1 text-2xl font-semibold">{{editarId?'Editar cliente':'Registro de cliente'}}</h1></div><RouterLink to="/clientes" class="text-sm underline">Ver clientes</RouterLink></header>
  <form @submit.prevent="guardar" class="grid gap-4 sm:grid-cols-2">
   <label v-for="[key,label,type] in fields" :key="key" class="block"><span class="mb-1.5 block text-sm font-medium">{{label}}</span>
    <select v-if="key==='tallerId'" v-model="form.tallerId" required class="w-full border border-zinc-300 bg-white px-3 py-2.5 outline-none focus:border-zinc-900"><option value="" disabled>Selecciona un taller</option><option v-for="t in talleres" :key="t.id" :value="t.id">{{t.nombre}}</option></select>
    <input v-else-if="key!=='colonia'||!colonias.length" v-model="form[key]" :type="type" :min="key==='edad'?0:undefined" :max="key==='edad'?130:undefined" :required="!['contactoAlternativo','emailTrabajo'].includes(key)" class="w-full border border-zinc-300 px-3 py-2.5 outline-none focus:border-zinc-900">
    <select v-else v-model="form.colonia" required class="w-full border border-zinc-300 bg-white px-3 py-2.5 outline-none focus:border-zinc-900"><option value="" disabled>Selecciona una colonia</option><option v-for="colonia in colonias" :key="colonia" :value="colonia">{{colonia}}</option></select>
   </label>
   <label class="block sm:col-span-2"><span class="mb-1.5 block text-sm font-medium">Fotografía (JPG, PNG o WEBP, máximo 7 MB)</span><input id="foto-cliente" type="file" accept="image/jpeg,image/png,image/webp" @change="onFile" class="block w-full text-sm file:mr-3 file:border-0 file:bg-zinc-100 file:px-3 file:py-2"></label>
   <p v-if="mensaje" role="status" :class="error?'bg-red-50 text-red-700':'bg-emerald-50 text-emerald-700'" class="sm:col-span-2 px-3 py-2 text-sm">{{mensaje}}</p>
   <div class="flex flex-wrap gap-3 sm:col-span-2"><button :disabled="cargando" class="bg-zinc-900 px-5 py-2.5 text-sm font-medium text-white disabled:opacity-50">{{cargando?'Guardando...':editarId?'Guardar cambios':'Guardar cliente'}}</button><RouterLink to="/clientes" class="border border-zinc-300 px-5 py-2.5 text-sm">Lista de clientes</RouterLink></div>
  </form>
 </section></main>
</template>
