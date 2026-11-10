# Caso de uso UC-CV-6: Registrar vehículo

## Actor
Administrador o recepcionista autenticado.

## Precondiciones
- El usuario cuenta con un JWT vigente, rol autorizado y taller asignado.
- Existe un cliente activo perteneciente al taller del usuario.
- La tabla `estatus` contiene el estatus activo con ID `1`.

## Flujo principal
1. El actor abre la lista de clientes.
2. Selecciona **Registrar vehículo** en la fila de un cliente activo.
3. El sistema abre el formulario modal con el cliente seleccionado.
4. El actor captura VIN, placa, marca, modelo, año, versión, color, kilometraje y número de serie.
5. El sistema valida los campos y envía el registro al backend.
6. El backend valida JWT/rol, taller, estado del cliente, formato y duplicados.
7. El backend guarda el vehículo asociado al cliente con estatus activo.
8. El sistema confirma el registro.

## Alternativas y errores
- Cliente inexistente o eliminado: responde `404`.
- Cliente suspendido: responde `409`.
- Taller del token distinto al del cliente o rol/taller no autorizado: responde `403`.
