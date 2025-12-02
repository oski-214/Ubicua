package logic;

import java.sql.Timestamp;

public class Measurement 
{
    private int value;
    private Timestamp date;
    // Campos raíz
    private String sensorId;        // sensor_id
    private String sensorType;      // sensor_type
    private String streetId;        // street_id
    private Timestamp timestamp;    // timestamp

    // Campos de location
    private double latitude;
    private double longitude;
    private String district;
    private String neighborhood;
    private String postalCode;
    private String streetName;
    private double streetLength;
    private String surfaceType;
    private int speedLimit;

    // Campos de data
    private int vehicleCount;
    private int pedestrianCount;
    private int bicycleCount;
    private int carCount;
    private int truckCount;
    private int ecoCount;
    private int gasCount;
    private String plate;
    private double meanPressure;
    private double distance;
    private String typeVehicle;
    private String direction;
    private String counterType;
    private String technology;
    
 
    // constructors
    public Measurement() 
    {
    	this.value = 0;
    	this.date = null;
    }

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}
        
        // Getters y setters

        public String getSensorId() {
            return sensorId;
        }

        public void setSensorId(String sensorId) {
            this.sensorId = sensorId;
        }

        public String getSensorType() {
            return sensorType;
        }

        public void setSensorType(String sensorType) {
            this.sensorType = sensorType;
        }

        public String getStreetId() {
            return streetId;
        }

        public void setStreetId(String streetId) {
            this.streetId = streetId;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getNeighborhood() {
            return neighborhood;
        }

        public void setNeighborhood(String neighborhood) {
            this.neighborhood = neighborhood;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getStreetName() {
            return streetName;
        }

        public void setStreetName(String streetName) {
            this.streetName = streetName;
        }

        public double getStreetLength() {
            return streetLength;
        }

        public void setStreetLength(double streetLength) {
            this.streetLength = streetLength;
        }

        public String getSurfaceType() {
            return surfaceType;
        }

        public void setSurfaceType(String surfaceType) {
            this.surfaceType = surfaceType;
        }

        public int getSpeedLimit() {
            return speedLimit;
        }

        public void setSpeedLimit(int speedLimit) {
            this.speedLimit = speedLimit;
        }

        public int getVehicleCount() {
            return vehicleCount;
        }

        public void setVehicleCount(int vehicleCount) {
            this.vehicleCount = vehicleCount;
        }

        public int getPedestrianCount() {
            return pedestrianCount;
        }

        public void setPedestrianCount(int pedestrianCount) {
            this.pedestrianCount = pedestrianCount;
        }

        public int getBicycleCount() {
            return bicycleCount;
        }

        public void setBicycleCount(int bicycleCount) {
            this.bicycleCount = bicycleCount;
        }

        public int getCarCount() {
            return carCount;
        }

        public void setCarCount(int carCount) {
            this.carCount = carCount;
        }

        public int getTruckCount() {
            return truckCount;
        }

        public void setTruckCount(int truckCount) {
            this.truckCount = truckCount;
        }

        public int getEcoCount() {
            return ecoCount;
        }

        public void setEcoCount(int ecoCount) {
            this.ecoCount = ecoCount;
        }

        public int getGasCount() {
            return gasCount;
        }

        public void setGasCount(int gasCount) {
            this.gasCount = gasCount;
        }

        public String getPlate() {
            return plate;
        }

        public void setPlate(String plate) {
            this.plate = plate;
        }

        public double getMeanPressure() {
            return meanPressure;
        }

        public void setMeanPressure(double meanPressure) {
            this.meanPressure = meanPressure;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public String getTypeVehicle() {
            return typeVehicle;
        }

        public void setTypeVehicle(String typeVehicle) {
            this.typeVehicle = typeVehicle;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public String getCounterType() {
            return counterType;
        }

        public void setCounterType(String counterType) {
            this.counterType = counterType;
        }

        public String getTechnology() {
            return technology;
        }

        public void setTechnology(String technology) {
            this.technology = technology;
        }
        
    
 }
