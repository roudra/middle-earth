package org.roy.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvNumber;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Trip {
    @Id
    @CsvBindByName(column = "ride_id")
    public String rideId;
    @CsvBindByName(column = "rideable_type")
    public String rideableType;
    @CsvBindByName(column = "started_at")
    @CsvDate("yyyy-MM-dd hh:mm:ss")
    public Date startedAt;
    @CsvBindByName(column = "ended_at")
    @CsvDate("yyyy-MM-dd hh:mm:ss")
    public Date endedAt;
    @CsvBindByName(column = "start_station_name")
    public String startStationName;
    @CsvBindByName(column = "start_station_id")
    public String startStationId;
    @CsvBindByName(column = "end_station_name")
    public String endStationName;
    @CsvBindByName(column = "end_station_id")
    public String endStationId;
    @CsvBindByName(column = "start_lat")
    @CsvNumber("##.##############")
    public double startLat;
    @CsvBindByName(column = "start_lng")
    @CsvNumber("##.##############")
    public double startLong;
    @CsvBindByName(column = "end_lat")
    @CsvNumber("##.##############")
    public double endLat;
    @CsvBindByName(column = "end_lng")
    @CsvNumber("##.##############")
    public double endLong;
    @CsvBindByName(column = "member_casual")
    public String memberType;
}
