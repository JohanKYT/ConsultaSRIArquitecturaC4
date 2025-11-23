import React, { useState } from "react";
import api from "../api/axios"; // Asegúrate de que esto apunte a tu config de Axios

const ConsultaForm = () => {
    const [ruc, setRuc] = useState("");
    const [cedula, setCedula] = useState("");
    const [placa, setPlaca] = useState("");

    // Estados para guardar la data
    const [datosPersona, setDatosPersona] = useState(null);
    const [datosVehiculo, setDatosVehiculo] = useState(null);
    const [puntosLicencia, setPuntosLicencia] = useState(null);

    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    // 1. Consultar RUC y Persona
    const consultarSRI = async (e) => {
        e.preventDefault();
        setError("");
        setLoading(true);
        setDatosPersona(null);

        try {
            // Primero validamos si existe
            const existe = await api.get(`/sri/validar/${ruc}`);
            if (existe.data === true) {
                // Si existe, traemos los datos
                const response = await api.get(`/sri/persona/${ruc}`);
                setDatosPersona(response.data);
            } else {
                setError("El RUC no existe en el SRI.");
            }
        } catch (err) {
            console.error(err);
            setError("Error al consultar persona. Verifique el RUC.");
        } finally {
            setLoading(false);
        }
    };

    // 2. Consultar Vehículo
    const consultarVehiculo = async () => {
        if (!placa) return;
        setError("");
        setDatosVehiculo(null);

        try {
            const response = await api.get(`/vehiculo/${placa}`);
            console.log("Datos Vehiculo:", response.data); // Para depurar en consola
            setDatosVehiculo(response.data);
        } catch (err) {
            console.error(err);
            setError("No se encontró información del vehículo");
        }
    };

    // 3. Consultar Licencia (ANT)
    const consultarLicencia = async () => {
        if (!cedula) return;
        setError("");
        setPuntosLicencia(null);
        setLoading(true);

        try {
            const response = await api.get(`/licencia/${cedula}`);
            setPuntosLicencia(response.data);
        } catch (err) {
            console.error(err);
            setError("Error al consultar puntos de licencia.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container mt-5" style={{ maxWidth: "600px" }}>
            <h2 className="text-center mb-4">Consulta Ciudadana – SRI & ANT</h2>

            {/* --- SECCIÓN 1: PERSONA (RUC) --- */}
            <div className="card mb-4">
                <div className="card-body">
                    <h5 className="card-title">1. Datos del Contribuyente</h5>
                    <div className="input-group mb-3">
                        <input
                            type="text"
                            className="form-control"
                            placeholder="Ingrese RUC"
                            value={ruc}
                            onChange={(e) => setRuc(e.target.value)}
                        />
                        <button className="btn btn-primary" onClick={consultarSRI} disabled={loading}>
                            {loading ? "Buscando..." : "Buscar Persona"}
                        </button>
                    </div>

                    {datosPersona && (
                        <div className="alert alert-success">
                            <p><strong>Nombre:</strong> {datosPersona.nombre}</p>
                            <p><strong>RUC:</strong> {datosPersona.ruc}</p>
                            <p><strong>Estado:</strong> {datosPersona.estadoContribuyente}</p>
                            <p><strong>Tipo:</strong> {datosPersona.tipoContribuyente}</p>
                        </div>
                    )}
                </div>
            </div>

            {/* --- SECCIÓN 2: VEHÍCULO (PLACA) --- */}
            {/* Solo se muestra si ya buscamos persona (opcional, quita la condición si quieres) */}
            {datosPersona && (
                <div className="card mb-4">
                    <div className="card-body">
                        <h5 className="card-title">2. Datos del Vehículo</h5>
                        <div className="input-group mb-3">
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Placa (ej: PCX5592)"
                                value={placa}
                                onChange={(e) => setPlaca(e.target.value.toUpperCase())}
                            />
                            <button className="btn btn-secondary" onClick={consultarVehiculo}>
                                Consultar Vehículo
                            </button>
                        </div>

                        {datosVehiculo && (
                            <div className="alert alert-info">
                                {/* AQUI ESTABA EL ERROR: Ahora usamos las variables limpias */}
                                <p><strong>Placa:</strong> {datosVehiculo.placa}</p>
                                <p><strong>Marca:</strong> {datosVehiculo.marca}</p>
                                <p><strong>Modelo:</strong> {datosVehiculo.modelo}</p>
                                <p><strong>Año:</strong> {datosVehiculo.anio}</p>
                                <p><strong>Clase:</strong> {datosVehiculo.clase}</p>
                            </div>
                        )}
                    </div>
                </div>
            )}

            {/* --- SECCIÓN 3: LICENCIA (ANT) --- */}
            {datosVehiculo && (
                <div className="card mb-4">
                    <div className="card-body">
                        <h5 className="card-title">3. Puntos de Licencia</h5>
                        <div className="input-group mb-3">
                            <input
                                type="text"
                                className="form-control"
                                placeholder="Cédula"
                                value={cedula}
                                onChange={(e) => setCedula(e.target.value)}
                            />
                            <button className="btn btn-warning" onClick={consultarLicencia} disabled={loading}>
                                {loading ? "Consultando ANT..." : "Ver Puntos"}
                            </button>
                        </div>

                        {puntosLicencia && (
                            <div className={`alert ${puntosLicencia.puntos > 0 ? "alert-success" : "alert-danger"}`}>
                                <h4>Puntos: {puntosLicencia.puntos}</h4>
                                <p>Estado: {puntosLicencia.estado}</p>
                            </div>
                        )}
                    </div>
                </div>
            )}

            {error && <div className="alert alert-danger mt-3">{error}</div>}
        </div>
    );
};

export default ConsultaForm;