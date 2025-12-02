package logic;

import Database.ConectionDDBB;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


public class Logic 
{
	public static ArrayList<Measurement> getDataFromDB()
	{
		ArrayList<Measurement> values = new ArrayList<Measurement>();
		
		ConectionDDBB conector = new ConectionDDBB();
		Connection con = null;
		try
		{
			con = conector.obtainConnection(true);
			Log.log.info("Database Connected");
			
			PreparedStatement ps = ConectionDDBB.GetDataBD(con);
			Log.log.info("Query=>" + ps.toString());
			ResultSet rs = ps.executeQuery();
			while (rs.next())
			{
				Measurement measure = new Measurement();
				measure.setValue(rs.getInt("VALUE"));
				measure.setDate(rs.getTimestamp("DATE"));
				values.add(measure);
			}	
		} catch (SQLException e)
		{
			Log.log.error("Error: " + e);
			values = new ArrayList<Measurement>();
		} catch (NullPointerException e)
		{
			Log.log.error("Error: " + e);
			values = new ArrayList<Measurement>();
		} catch (Exception e)
		{
			Log.log.error("Error:" + e);
			values = new ArrayList<Measurement>();
		}
		conector.closeConnection(con);
		return values;
	}

	public static ArrayList<Measurement> setDataToDB(int value)
	{
		ArrayList<Measurement> values = new ArrayList<Measurement>();
		
		ConectionDDBB conector = new ConectionDDBB();
		Connection con = null;
		try
		{
			con = conector.obtainConnection(true);
			Log.log.info("Database Connected");

			PreparedStatement ps = ConectionDDBB.SetDataBD(con);
			ps.setInt(1, value);
			ps.setTimestamp(2, new Timestamp((new Date()).getTime()));
			Log.log.info("Query=>" + ps.toString());
			ps.executeUpdate();
		} catch (SQLException e)
		{
			Log.log.error("Error: " + e);
			values = new ArrayList<Measurement>();
		} catch (NullPointerException e)
		{
			Log.log.error("Error: " + e);
			values = new ArrayList<Measurement>();
		} catch (Exception e)
		{
			Log.log.error("Error:" + e);
			values = new ArrayList<Measurement>();
		}
		conector.closeConnection(con);
		return values;
	}
        
        public static void insertOrUpdateStreet(String street_id, Double street_length, double latitude, double longitude, String district, String neighborhood, int postal_code, String street_name, String surface_type, int speed_limit) {
            ConectionDDBB conector = new ConectionDDBB();
            Connection con = null;

            try {
                con = conector.obtainConnection(true);

                String sql = "INSERT INTO Street (street_id, street_length, latitude, longitude, district, neighborhood, postal_code, street_name, surface_type, speed_limit) VALUES (?,?,?,?,?,?,?,?,?,?)";

                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, street_id);
                ps.setDouble(2, street_length);
                ps.setDouble(3, latitude);
                ps.setDouble(4, longitude);
                ps.setString(5, district);
                ps.setString(6, neighborhood);
                ps.setInt(7, postal_code);
                ps.setString(8, street_name);
                ps.setString(9, surface_type);
                ps.setInt(10, speed_limit);

                ps.executeUpdate();
                Log.log.info("Street inserted: {}", street_id);

            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate entry")) {
                    Log.log.info("Street {} already exists, skipping insert", street_id);
                } else {
                    System.out.println("Error inserting Street data: " + e.getMessage());
                    Log.log.error("Error inserting Street data: {}", e.getMessage());
                }
            } catch (Exception e) {
                System.out.println("Error inserting Street data: " + e.getMessage());
                Log.log.error("Error inserting Street data: {}", e.getMessage());
            } finally {
                conector.closeConnection(con);
            }
        }



        
        public static void insertOrUpdateVehicle(String plate, Double mean_pressure, String technology, Double media_distancias, String street_id, String type_vehicle) {
            ConectionDDBB conector = new ConectionDDBB();
            Connection con = null;
            String tableName;

            switch (type_vehicle) {
                case "Coche":
                    tableName = "Coche";
                    break;
                case "Bicicleta":
                    tableName = "Bicicleta";
                    break;
                case "Camion":
                    tableName = "Camion";
                    break;
                default:
                    Log.log.warn("Tipo de vehiculo desconocido: {}", type_vehicle);
                    return;
            }

            try {
                con = conector.obtainConnection(true);

                String sql = "INSERT INTO " + tableName + " (matricula, media_presiones, tipo, media_distancias, street_id) VALUES (?,?,?,?,?)";

                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, plate);
                ps.setDouble(2, mean_pressure);
                ps.setString(3, technology);
                ps.setDouble(4, media_distancias);
                ps.setString(5, street_id);

                ps.executeUpdate();
                Log.log.info("Vehicle inserted: {} - {}", type_vehicle, plate);

            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate entry")) {
                    try {
                        String updateSql = "UPDATE " + tableName + " SET media_presiones=?, tipo=?, media_distancias=?, street_id=? WHERE matricula=?";
                        PreparedStatement updatePs = con.prepareStatement(updateSql);
                        updatePs.setDouble(1, mean_pressure);
                        updatePs.setString(2, technology);
                        updatePs.setDouble(3, media_distancias);
                        updatePs.setString(4, street_id);
                        updatePs.setString(5, plate);
                        updatePs.executeUpdate();
                        Log.log.info("Vehicle updated: {} - {}", type_vehicle, plate);
                    } catch (SQLException ex) {
                        Log.log.error("Error updating Vehicle: {}", ex.getMessage());
                    }
                } else {
                    Log.log.error("Error inserting Vehicle data: {}", e.getMessage());
                }
            } catch (Exception e) {
                Log.log.error("Error inserting Vehicle data: {}", e.getMessage());
            } finally {
                conector.closeConnection(con);
            }
        }

        
        public static int insertRegistro(String type_vehicle, String plate, String street_id, String timestamp, Integer car_count, Integer truck_count, Integer bicycle_count, Integer gas_count, Integer eco_count, String technology) {
            ConectionDDBB conector = new ConectionDDBB();
            Connection con = null;
            int registroId = -1;

            try {
                con = conector.obtainConnection(true);

                String sql = "INSERT INTO Registro (tipo_vehiculo, matricula, street_id, timestamp, contador_coches, contador_camiones, contador_bicicletas, contador_gasolinas, contador_electricos, tipo_emision) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                // Usar Statement.RETURN_GENERATED_KEYS para obtener el ID autoincremental
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                ps.setString(1, type_vehicle);
                ps.setString(2, plate);
                ps.setString(3, street_id);
                try {
                    // Convierte el String de formato SQL directamente a un objeto Timestamp
                    Timestamp sqlTimestamp = Timestamp.valueOf(timestamp);
                    ps.setTimestamp(4, sqlTimestamp);
                } catch (IllegalArgumentException e) {
                    Log.log.error("Formato de Timestamp inválido: {}. Usando hora actual del servidor.", timestamp);
                    // En caso de error de formato, usa el timestamp actual del servidor Java como fallback
                    ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                }

                ps.setInt(5, car_count);
                ps.setInt(6, truck_count);
                ps.setInt(7, bicycle_count);
                ps.setInt(8, gas_count);
                ps.setInt(9, eco_count);
                ps.setString(10, technology);

                ps.executeUpdate();
                Log.log.info("Registro insertado para matrícula: {}", plate);

                // Obtener el ID generado
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        registroId = rs.getInt(1); // El ID generado está en la primera columna
                    }
                }

            } catch (Exception e) {
                System.out.println("Error inserting Registro: " + e.getMessage());
                Log.log.error("Error inserting Registro: {}", e.getMessage());
            } finally {
                conector.closeConnection(con);
            }
            return registroId;
        }
        
        public static void insertManyToOne(String type_vehicle, String plate, int registroId) {
            ConectionDDBB conector = new ConectionDDBB();
            Connection con = null;
            String tableName;
            String columnPrefix;

            switch (type_vehicle) {
                case "Coche":
                    tableName = "many_Coche_has_many_Registro";
                    columnPrefix = "coche_matricula";
                    break;
                case "Bicicleta":
                    tableName = "many_Bicicleta_has_many_Registro";
                    columnPrefix = "bicicleta_matricula";
                    break;
                case "Camion":
                    tableName = "many_Camion_has_many_Registro";
                    columnPrefix = "camion_matricula";
                    break;
                default:
                    return;
            }

            try {
                con = conector.obtainConnection(true);

                // La consulta SQL se construye dinámicamente para la tabla de relación
                String sql = "INSERT INTO " + tableName + " (" + columnPrefix + ", registro_id) VALUES (?, ?)";

                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, plate);
                ps.setInt(2, registroId);

                ps.executeUpdate();
                Log.log.info("Relación M:M insertada en {} para vehículo {} y Registro ID: {}", tableName, plate, registroId);

            } catch (Exception e) {
                System.out.println("Error inserting Many-to-Many data: " + e.getMessage());
                Log.log.error("Error inserting Many-to-Many data: {}", e.getMessage());
            } finally {
                conector.closeConnection(con);
            }
        }
      
}


