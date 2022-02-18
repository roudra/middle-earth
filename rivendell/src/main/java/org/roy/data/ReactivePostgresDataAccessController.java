package org.roy.data;

import io.smallrye.common.constraint.NotNull;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.vertx.mutiny.sqlclient.Tuple;
import lombok.extern.java.Log;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.roy.model.Trip;
import org.roy.model.UploadResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@ApplicationScoped
@Log
public class ReactivePostgresDataAccessController {

    private static final String select_query = "select * from trip";
    private static final String select_id_query = "select rideid from trip";
    private static final String select_exists_query = "select rideid from trip where rideid = ANY($1)";
    private static final String insert_query = "insert into trip (rideid, endedat, endlat,endlong,endstationid,endstationname,membertype,rideabletype,startedat,startlat,startlong,startstationid,startstationname) values ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,$13) RETURNING *";
    @Inject
    io.vertx.mutiny.pgclient.PgPool client;
    @Inject
    @Channel("upload-status")
//    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 10000)
    MutinyEmitter<UploadResponse> uploadStatus;
    private final Executor executor = Executors.newCachedThreadPool();

    public Uni<UploadResponse> addTrip(Trip t) {
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return client
                .preparedQuery(insert_query)
                .execute(Tuple.of(t.rideId).addLocalDateTime(t.endedAt.toInstant().atZone(ZoneId.of(ZoneId.SHORT_IDS.get("EST"))).toLocalDateTime()).addDouble(t.endLat).addDouble(t.endLong).addString(t.endStationId).addString(t.endStationName).addString(t.memberType).addString(t.rideableType).addLocalDateTime(t.startedAt.toInstant().atZone(ZoneId.of(ZoneId.SHORT_IDS.get("EST"))).toLocalDateTime()).addDouble(t.startLat).addDouble(t.startLong).addString(t.startStationId).addString(t.startStationName))
                .onItem()
                .transform(rowSet -> new UploadResponse().setRecordId(rowSet.iterator().next().getString("rideid")).setUploadStatus(true).setMessage("Successfully inserted"))
//                .log("Inserted")
                .onFailure()
                .recoverWithItem(ex -> new UploadResponse().setRecordId(t.rideId).setUploadStatus(false).setMessage(ex.getMessage()));
    }

    public void addTripsAsync(@NotNull Collection<Trip> trips) {
//        List<String> existingIds = findExistingTripIds(trips.stream().map(trip -> trip.rideId).collect(Collectors.toList()));
//        log.info("CSV Records : "+trips.size()+" Existing:"+existingIds.size());
        Multi.createFrom().iterable(trips)
                .emitOn(executor)
//                .filter(p -> !(existingIds.contains(p.rideId)))
                .onItem().transformToUniAndMerge(trip -> addTrip(trip))
//                .log()
//                .invoke(t -> uploadStatus.sendAndForget(t))
                .subscribe().with(t -> uploadStatus.sendAndForget(t));
    }

    private List<String> findExistingTripIds(List<String> ids) {
        //Find Existing Ids from Database
        List<String> existingIds = client.preparedQuery(select_exists_query).execute(Tuple.of(ids.toArray()))
                //Convert to Multi to handle each record in own event loop
                .onItem().transformToMulti(rowSet -> Multi.createFrom().iterable(rowSet))
                //extract ID
                //.onItem().invoke(row -> System.out.println(row.getString("rideid")))
                .onItem().transform(row -> row.getString("rideid"))
                //Transform
                .collect().asList().await().indefinitely();
        log.info("Existing Db ids " + existingIds.size());
        return existingIds;
    }


//    private List<Tuple> prepareDataToInsert(Collection<Trip> trips) {
//        //get trip ids from user uploaded file.
//        List<String> ids = trips.stream().map(trip -> trip.rideId).collect(Collectors.toList());
//        log.info("User Provided Ids =" + ids.size());
//        List<String> existingIds = findExistingTripIds(ids);
//        List<Tuple> tuples = trips.stream()
//                //remove existing DB Records
//                .filter(trip -> !existingIds.contains(trip.rideId))
//                //.peek(trip -> System.out.print(trip.rideId+","))
//                //Create Tuple. This is stream map and not Mutiny map
//                .map(t -> {
//                    return Tuple.of(t.rideId).addLocalDateTime(t.endedAt.toInstant().atZone(ZoneId.of(ZoneId.SHORT_IDS.get("EST"))).toLocalDateTime()).addDouble(t.endLat).addDouble(t.endLong).addString(t.endStationId).addString(t.endStationName).addString(t.memberType).addString(t.rideableType).addLocalDateTime(t.startedAt.toInstant().atZone(ZoneId.of(ZoneId.SHORT_IDS.get("EST"))).toLocalDateTime()).addDouble(t.startLat).addDouble(t.startLong).addString(t.startStationId).addString(t.startStationName);
//                })
//                .collect(Collectors.toList());
//        log.info("Final Rows to Insert " + tuples.size());
//        return tuples;
//    }

//    @Deprecated
//    public int addTrips(@NotNull Collection<Trip> trips) {
//        PreparedQuery<RowSet<Row>> preparedQuery = client.preparedQuery(insert_query);
//        List<Tuple> tuples = prepareDataToInsert(trips);
//        int finalRowCount = 0;
//        if (tuples.size() > 0) {
//            Uni<RowSet<Row>> rowSet = preparedQuery.executeBatch(tuples);
//            Uni<Integer> rc = rowSet
//                    //Loop through rowset and add the impacted row count.
//                    .onItem().transform(rs -> {
//                        int total = 0;
//                        do {
//                            total += rs.rowCount();
//                        } while ((rs = rs.next()) != null);
//                        return total;
//                    });
//            //get Final Number
//            finalRowCount = rc.await().indefinitely();
//        }
//        return finalRowCount;
//    }


}