package org.roy.data;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import org.roy.model.Trip;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TripRepository implements PanacheRepositoryBase<Trip, String> {

}
