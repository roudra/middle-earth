package org.roy;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.extern.java.Log;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.resteasy.reactive.MultipartForm;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestSseElementType;
import org.reactivestreams.Publisher;
import org.roy.controller.FileController;
import org.roy.data.ReactivePostgresDataAccessController;
import org.roy.data.TripRepository;
import org.roy.model.FileUploadForm;
import org.roy.model.Trip;
import org.roy.model.UploadResponse;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Collection;
import java.util.logging.Level;

@Path("/trips")
@Log
public class TripResource {
    @Inject
    FileController fc;
    @Inject
    TripRepository tr;
    @Inject
    ReactivePostgresDataAccessController rpdac;

    @Inject
    @Channel("upload-status")
    Publisher<UploadResponse> uploadStatus;

    @GET
    @Path("/v1")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestSseElementType(MediaType.APPLICATION_JSON)
    public Multi<Trip> get() {
        return tr.streamAll();
    }

    @GET
    @Path("v1/{id}")
    public Uni<Response> getSingle(String id) {
        return tr.findById(id)
                .onItem().transform(trip -> trip != null ? Response.ok(trip) : Response.status(RestResponse.Status.BAD_REQUEST))
                .onItem().transform(responseBuilder -> responseBuilder.build());
    }

    @GET
    @Path("v1/status")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestSseElementType(MediaType.APPLICATION_JSON)
    public Publisher<UploadResponse> getUploadStatus() {
        return uploadStatus;
    }

    @POST
    @Path("/v1")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadTrips(@MultipartForm FileUploadForm form) {
        log.info("UPLOAD FILE");
        Collection<Trip> uploadedTrips = null;
        if (form.file.contentType().equalsIgnoreCase("text/csv")) {
            try {
                uploadedTrips = fc.processCsv(form.file.uploadedFile());
            } catch (FileNotFoundException e) {
                log.log(Level.SEVERE, "Unable to process File");
            }
        }
        if (uploadedTrips != null && !uploadedTrips.isEmpty()) {
            rpdac.addTripsAsync(uploadedTrips);
            return Response.ok(
                            String.format("File Name= %s\nContent Type= %s\nSize= %s\nCharset= %s\nTS= %s\nRows Uploaded=%s",
                                    form.file.fileName(),
                                    form.file.contentType(),
                                    form.file.size(),
                                    form.file.charSet(),
                                    Calendar.getInstance().getTime(),
                                    uploadedTrips.size()
                            ))
                    .status(Response.Status.CREATED)
                    .build();
        }
        return Response.ok(
                        String.format("File Name= %s\nContent Type= %s\nMessage= %s",
                                form.file.fileName(),
                                form.file.contentType(),
                                "Failed To Process"
                        ))
                .status(Response.Status.BAD_REQUEST)
                .build();

    }

//    @POST
//    @Path("/v2")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Produces(MediaType.SERVER_SENT_EVENTS)
//    @RestSseElementType(MediaType.APPLICATION_JSON)
//    @RequestBody(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA, schema = @Schema(implementation = FileUploadForm.class)))
//    @Operation(operationId = "uploadTripsV2")
//    @NonBlocking
//    public Multi<Response> uploadTripsV2(@MultipartForm FileUploadForm form) {
//
//        //OPTION 1
//        //        log.info("OPTION 1");
//        //        Uni<Iterable<Trip>> uniTrips = Uni.createFrom().item(form.file)
//        //                .onItem().transform(Unchecked.function(fileUpload -> {
//        //                    return Collections.unmodifiableList(fc.processCsv(fileUpload.uploadedFile()));
//        //                }));
//        //        Multi<Trip> multiTrip = uniTrips.onItem().transformToMulti(trips -> Multi.createFrom().iterable(trips));
//        //        Multi<UploadResponse> insertToDb = multiTrip.onItem().transformToUniAndMerge( trip ->
//        //                //isolate the operation to handle its failure independently
//        //                rpdac.addTrip(trip).onItem().transform(status-> new UploadResponse(status, trip.rideId)));
//
////        // OPTION 2
////        log.info("OPTION 2");
////        Collection<Trip> trips = null;
////        try {
////            trips = Collections.unmodifiableList(fc.processCsv(form.file.uploadedFile()));
////            log.info("IDENTIFIED NUMBER OF TRIPS : " + trips.size());
////        } catch (FileNotFoundException e) {
////            e.printStackTrace();
////        }
////        Multi<Trip> multiTrip = Multi.createFrom().iterable(trips);
////        Multi<UploadResponse> insertToDb = multiTrip.onItem().transform(trip -> new UploadResponse().setRecordId(trip.rideId));
////        return insertToDb
////                .onItem().transform(resObj -> resObj != null ? Response.ok(resObj) : Response.status(RestResponse.Status.EXPECTATION_FAILED))
////                .onItem().transform(responseBuilder -> responseBuilder.build());
//
//
////        return Uni.createFrom().item(form.file)
////                .onItem().transform(Unchecked.function(fileUpload -> {
////                    return Collections.unmodifiableList(fc.processCsv(fileUpload.uploadedFile()));
////                }))
//////                .log()
////                .onItem().transformToMulti(trips -> Multi.createFrom().iterable(trips))
//////                .log()
////                .onItem().transformToUniAndMerge( trip ->
////                        //isolate the operation to handle its failure independently
////                        Uni.createFrom().item(trip)
////                                .onItem().transform(trp ->
////                                {
////                                    rpdac.addTrip(trp);
////                                    return trp.rideId+" Inserted Successfully";
////                                })
////                                .onFailure().recoverWithItem(trip.rideId+" Failed")
////                )
////                .onItem().transform(resObj -> resObj != null ? Response.ok(resObj) : Response.status(RestResponse.Status.EXPECTATION_FAILED))
//////                .log()
////                .onItem().transform(responseBuilder -> responseBuilder.build());
//
//    }


//    @POST
//    @Path("/v1")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Produces(MediaType.TEXT_PLAIN)
//    public Response uploadTrips(@MultipartForm FileUploadForm form) {
//        Collection<Trip> uploadedTrips = null;
//        if (form.file.contentType().equalsIgnoreCase("text/csv")) {
//            try {
//                uploadedTrips = fc.processCsv(form.file.uploadedFile());
//            } catch (FileNotFoundException e) {
//                log.log(Level.SEVERE, "Unable to process File");
//            }
//        }
//        if (uploadedTrips != null && !uploadedTrips.isEmpty()) {
//            rpdac.addTrips(uploadedTrips);
//        }
//
//        return Response.ok(
//                        String.format("File Name= %s\nContent Type= %s\nSize= %s\nCharset= %d\nTS= %s\nRows Uploaded=%s",
//                                form.file.fileName(),
//                                form.file.contentType(),
//                                form.file.size(),
//                                form.file.charSet(),
//                                Calendar.getInstance().getTime(),
//                                uploadedTrips.size()
//                        ))
//                .status(Response.Status.CREATED)
//                .build();
//    }


}