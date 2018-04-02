import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import com.google.api.services.calendar.model.*;

public class OBAutoCalendar {
 
   
   

   public static void main(String[] args) 
   { 
      String email_id = args[0];
      String password = args[1];
      Event event = null;
      
      List<Correo> correosGDCArg = DescargaCorreos.getCorreos(email_id, password, ArgentinaGDC.getNombreCarpeta());
      List<ArgentinaGDC> lArgGDCEvents = new ArrayList<ArgentinaGDC>();
      List<Event> lEvents = new ArrayList<Event>();
      for(Correo corr : correosGDCArg)
      {
	 ArgentinaGDC evArgGDC = null;
         evArgGDC = new ArgentinaGDC(corr);
         event = evArgGDC.getEvent();
         evArgGDC.setEvent(event);
         lArgGDCEvents.add(evArgGDC);
         evArgGDC = null;
         event = null;
      }
      try{
         CalendarInteract.subirEvents(lArgGDCEvents, ArgentinaGDC.getCalendarioAsignado());
      }
      catch (Exception e) {
         e.printStackTrace();
      }
      
      
   }



   
}
