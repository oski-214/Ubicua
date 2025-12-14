package com.example.ejemplopl3;

import java.lang.Record;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    /**
     * Obtiene los detalles de una calle específica por su ID.
     * Genera la URL: .../GetData?table=Street&street_id=valor
     * @param street_id El ID de la calle a buscar.
     * @return Un objeto Call que se espera que devuelva una lista (posiblemente de un solo elemento) de calles.
     */
    @GET("ServerExampleUbicomp-1.0-SNAPSHOT/GetData")
    Call<List<Street>> getStreetById(@Query("table") String table, @Query("street_id") String street_id);

    /**
     * Obtiene los datos de un vehículo (Coche, Camion, Bicicleta) por su matrícula.
     * Genera la URL: .../GetData?table=valor_tabla&matricula=valor_matricula
     * @param vehicleType El tipo de vehículo ("Coche", "Camion", "Bicicleta").
     * @param matricula La matrícula del vehículo a buscar.
     * @return Un objeto Call que se espera que devuelva una lista de objetos Vehicle.
     */
    @GET("ServerExampleUbicomp-1.0-SNAPSHOT/GetData")
    Call<List<Vehicle>> getVehicleByLicensePlate(@Query("table") String vehicleType, @Query("matricula") String matricula);

    /**
     * Obtiene los registros de una fecha específica.
     * Genera la URL: .../GetData?table=Registro&date=valor_fecha
     * @param date La fecha para filtrar los registros, en formato "YYYY-MM-DD".
     * @return Un objeto Call que se espera que devuelva una lista de objetos Record.
     */
    @GET("ServerExampleUbicomp-1.0-SNAPSHOT/GetData")
    Call<List<Registro>> getRecordsByDate(@Query("table") String table, @Query("date") String date);

    /**
     * Obtiene una lista de todos los IDs de las calles.
            * Asume que tendrás un servlet en "/GetStreetIds" o similar.
            * @return Una lista de Strings con los IDs de las calles.
            */
    @GET("ServerExampleUbicomp-1.0-SNAPSHOT/GetStreetIds")
    Call<List<String>> getAllStreetIds();

    /**
     * Obtiene una lista de todas las matrículas para un tipo de vehículo específico.
     * Asume un servlet en "/GetLicensePlates" que acepta el nombre de la tabla.
     * @param vehicleType El nombre de la tabla a consultar (ej. "Coche", "Camion").
     * @return Una lista de Strings con las matrículas.
     */
    @GET("ServerExampleUbicomp-1.0-SNAPSHOT/GetLicensePlates")
    Call<List<String>> getLicensePlatesByType(@Query("table") String vehicleType);
}