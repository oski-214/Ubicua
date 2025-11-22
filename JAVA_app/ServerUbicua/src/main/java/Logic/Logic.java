package logic;

import Database.ConectionDDBB;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;



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
        
        public static void insertStreetData(String street_id, Double street_length, double latitude, double longitude, String district, String neighborhood, int postal_code, String street_name, String surface_type, int speed_limit){
            ConectionDDBB conector = new ConectionDDBB();
            Connection con =null ;
            
            try{
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
                
            }
            catch (Exception e){
                System.out.println("Error inserting street data: " + e.getMessage());
                e.printStackTrace();
                Log.log.error("Error inserting street data: " + e);
            }
            finally {
                conector.closeConnection(con);
            }
        }
      
}


