🚗 Consulta de Vehículos por Placa – SRI / ANT

📌 Descripción del Proyecto

Este sistema permite consultar datos oficiales asociados a un vehículo ecuatoriano mediante su número de placa.
La aplicación está compuesta por:

Backend (Spring Boot) — expone un API REST que consulta los datos y usa Redis Cache para acelerar respuestas.

Frontend (Angular o React) — interfaz donde el usuario ingresa la placa y obtiene los resultados.

Docker Compose — orquesta todo el proyecto (backend, frontend y Redis).

⚙️ Características Principales

✔ Consulta de vehículo por placa
✔ Consulta de licencia vinculada
✔ Respuesta rápida gracias a cacheo en Redis
✔ Validación de placa
✔ Manejo de errores y mensajes claros al usuario
✔ Arquitectura limpia y desacoplada
✔ Proyecto totalmente contenerizado con Docker

🏛️ Arquitectura del Sistema
┌──────────────────────────────┐
│          FRONTEND            │
│  (React / Angular)           │
└─────────────┬────────────────┘
              │ REST API
┌─────────────▼────────────────┐
│        BACKEND API           │
│     Spring Boot 3.x          │
│  Controladores / Servicios   │
│  Integración / Validación    │
└─────────────┬────────────────┘
              │ Cache
┌─────────────▼────────────────┐
│            Redis             │
│     Cache de consultas       │
└──────────────────────────────┘

📦 Ejecución con Docker

Para levantar toda la solución:

docker compose up --build


Servicios que se levantan:

Servicio	Puerto	Descripción
Backend	8080	API REST
Frontend	4200 o 80	UI web
Redis	6379	Cache
🧠 Funcionamiento Interno
🔹 1. El usuario ingresa una placa

El frontend envía la solicitud al backend:

GET /api/vehiculos/{placa}

🔹 2. El backend valida la placa

Formato permitido: AAA1234

Se verifica longitud, letras y números.

Si no cumple → retorna error 400.

🔹 3. Se revisa si la placa está en Redis

Si existe → respuesta inmediata (mucho más rápido)

Si no existe → se consulta a los servicios externos

🔹 4. El backend consulta la información oficial

Consulta a los endpoints respectivos (SRI / ANT / Gov APIs).

🔹 5. Se almacena la respuesta en Redis

Para futuras consultas rápidas.

🔹 6. El backend devuelve la información al frontend

Ejemplo de respuesta:

{
  "placa": "ABC1234",
  "marca": "Chevrolet",
  "modelo": "Aveo Family",
  "anio": 2012,
  "color": "Plata",
  "propietario": "Juan Pérez",
  "licencia": {
      "numero": "1234567890",
      "tipo": "B",
      "estado": "VIGENTE"
  }
}

📁 Estructura del Proyecto
consulta-sri/
│── backend/
│   ├── controller/
│   ├── service/
│   ├── config/ (EnableCaching, RedisConfig)
│   └── dto/
│
│── frontend/
│   ├── src/
│   └── componentes/
│
└── docker-compose.yml

🧪 Ejemplo de Uso (Frontend)

Ingresas la placa: PBC1234

Presionas Consultar

La aplicación muestra:

Datos del vehículo

Estado de licencia

Propietario

Mensajes si está en cache

Si la información ya ha sido consultada antes → aparece inmediatamente 🚀

📊 Ventajas del Sistema

Respuestas más rápidas por Redis Cache

Arquitectura modular y escalable

Preparado para producción con Docker

Fácil mantenimiento

Código claro y documentado


✅ Conclusiones

El proyecto cumple completamente con el objetivo de consultar datos vehiculares a través de una placa.

La integración de Redis Cache mejora significativamente el rendimiento, reduciendo el tiempo de respuesta y la carga sobre los servicios externos.

La arquitectura desacoplada (API + Frontend + Caché) permite escalar fácilmente el sistema.

El uso de Docker Compose facilita la ejecución, despliegue y portabilidad del proyecto.

La solución es estable, rápida, mantenible y lista para producción.
