package model;

import domain.Reservation;
import domain.Space;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface SpaceDAO {
    public List<Space> getAllSpaces();
    public List<Space> getByVenueId(long id);

    List<Space> getAvailableSpacesBySpaceId(Date startDate, Date endDate, int maxOccupancy, Long spaceId);

    public String intToMonth(int month);
    public List<Space> getAvailableSpaces(Date startDate, Date endDate, int maxOccupancy);
    //public List<Space> getAvailableSpaces(LocalDate startdate,LocalDate endDate);



    //    List<Space> spaces = spaceDAO.getById(ui.listspacesofuserselection);
//			for (Space space : spaces) {
//        System.out.println(space.getId() + " " + space.getName() + " " + space.getOpenFrom() + " " + space.getOpenTo() + " " + space.getDailyRate() + " " + space.getMaxOccupancy());
//        continue;
//    }
}
