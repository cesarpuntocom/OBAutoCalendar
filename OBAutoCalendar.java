import java.util.*;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.*;

public class OBAutoCalendar {
   public static void main(String[] args) 
   { 
      String email_id = args[0];
      String password = args[1];
      List<Correo> correosGDCArg = DescargaCorreos.getCorreos(email_id, password, ArgentinaGDC.getNombreCarpeta());
      List<ArgentinaGDC> lArgGDCEvents = new ArrayList<ArgentinaGDC>();
      for(Correo corr : correosGDCArg)
      {
	     ArgentinaGDC evArgGDC = new ArgentinaGDC(corr);
         lArgGDCEvents.add(evArgGDC);
      }
      try{
         CalendarInteract.subirEvents(lArgGDCEvents, ArgentinaGDC.getCalendarioAsignado());
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }
}
