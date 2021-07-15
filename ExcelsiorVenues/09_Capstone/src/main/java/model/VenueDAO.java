package model;


import domain.Space;
import domain.Venue;

import java.util.List;

public interface VenueDAO {

    public List<Venue> getAllVenues();

    public List<Venue> getVenueCategory(String venueName);
}
