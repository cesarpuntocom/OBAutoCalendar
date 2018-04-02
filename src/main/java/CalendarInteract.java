import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CalendarInteract
{
   /** Application name. */
   private static final String APPLICATION_NAME = "OBAutoCalendar";

   /** Directory to store user credentials for this application. */
   private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/OBAutoCalendar");

   /** Global instance of the {@link FileDataStoreFactory}. */
   private static FileDataStoreFactory DATA_STORE_FACTORY;

   /** Global instance of the JSON factory. */
   private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

   /** Global instance of the HTTP transport. */
   private static HttpTransport HTTP_TRANSPORT;

   /** Global instance of the scopes required by this quickstart.
    *
    * If modifying these scopes, delete your previously saved credentials
    * at ~/.credentials/calendar-java-quickstart
    */
   private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR);

   static {
      try {
         HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
         DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
      } catch (Throwable t) {
         t.printStackTrace();
         System.exit(1);
      }
   }

   /**
   * Build and return an authorized Calendar client service.
   * @return an authorized Calendar client service
   * @throws IOException
   */
   public static com.google.api.services.calendar.Calendar getCalendarService() throws IOException 
   {
      Credential credential = authorize();
      return new com.google.api.services.calendar.Calendar.Builder(
         HTTP_TRANSPORT, JSON_FACTORY, credential)
         .setApplicationName(APPLICATION_NAME)
         .build();
   }

   /**
    * Creates an authorized Credential object.
    * @return an authorized Credential object.
    * @throws IOException
    */
   
   public static Credential authorize() throws IOException {
      // Load client secrets.
      InputStream in = Event.class.getResourceAsStream("/client_secret.json");
      GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

      // Build flow and trigger user authorization request.
      GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
         HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
         .setDataStoreFactory(DATA_STORE_FACTORY)
         .setAccessType("offline")
         .build();
      Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
      System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
      return credential;
   }


   public static List<Event> descargaEventsCalendar(String calId) throws Exception
   {
      DateTime now = new DateTime(System.currentTimeMillis());
      com.google.api.services.calendar.Calendar service = getCalendarService();
      Events events = service.events().list(calId)
         //.setICalUID(calendarId)
         .setTimeMin(now)
         .setOrderBy("startTime")
         .setSingleEvents(true)
         .execute();
      List<Event> lEvents = events.getItems();
// debug
for(Event event1 : lEvents)
{
System.out.println(event1.getSummary());
System.out.println(event1.getDescription());
System.out.println(event1.getStart());
System.out.println(event1.getEnd());
}

// debug
      return lEvents;
   }

   public static void subirEvents(List<ArgentinaGDC> lArgGDCSubir, String calendario) throws Exception
   {

      // Dejar este método como es debido

      com.google.api.services.calendar.Calendar service = getCalendarService();
      String calId = getIdCalendar(calendario, service);
      List<Event> lEventsAgendados = descargaEventsCalendar(calId);
      List<Event> lEventsParaSubir = compruebaEventosAgendados(lArgGDCSubir, lEventsAgendados);      
      for(Event ev : lEventsParaSubir)
      {
         //ev = service.events().insert(calId, ev).execute();
         System.out.println("Evento a subir:");
         System.out.println(ev.getSummary());
         System.out.println(ev.getLocation());
         DateTime start = ev.getStart().getDateTime();
         if (start == null) {
            start = ev.getStart().getDate();
         }
         System.out.println(start.toString());    
         DateTime end = ev.getEnd().getDateTime();
         if (end == null) {
            end = ev.getEnd().getDate();
         }
         System.out.println(end.toString());   
         System.out.println(ev.getDescription());      
      }
   }

   public static void borraEventCalendar(String eventId, String calId) throws Exception
   {
      com.google.api.services.calendar.Calendar service = getCalendarService();   
      service.events().delete(calId, eventId).execute();
   }
      
   
   public static String getIdCalendar(String calendario, com.google.api.services.calendar.Calendar service) throws Exception
   {

      String pageToken = null;
      String calendarId = null;
      do {
         CalendarList calendarList = service.calendarList().list().setPageToken(pageToken).execute();
         List<CalendarListEntry> listaCalendarios = calendarList.getItems();
         for (CalendarListEntry calendarListEntry : listaCalendarios) {
           
           if(calendarListEntry.getSummary().matches(calendario))
           {
              calendarId = calendarListEntry.getId();
              break;
           }
         }
         pageToken = calendarList.getNextPageToken();
      } while (pageToken != null);
      return calendarId;
   }
 


   public static List<Event> compruebaEventosAgendados(List<ArgentinaGDC> lEventsCorreos, List<Event> lEventsAgendados) throws Exception
   {
      int evArgTipoNotif;
       List<Event> lEventsParaSubir = new ArrayList<Event>();
/*      for(ArgentinaGDC ag : lEventsCorreos)
      {
         System.out.println("\nEvents de correos en CalendarINteract: ");
         System.out.println(ag.getEvent().getSummary());

      }*/
     
      for(ArgentinaGDC argGDCEvent : lEventsCorreos)  
      {
         //System.out.println("\n Iterando \n");// debug
         evArgTipoNotif = argGDCEvent.getTipoNotif();
         if(evArgTipoNotif == 1)
         {
            
            // Notificación normal, comprobar si está agendada y si no, meter este event para agendar
            //System.out.println("\n Evento " + argGDCEvent.getEvent().getSummary() + " es del Caso 1 \n");
            for(Event eventAgendado : lEventsAgendados)
            {
               if((eventAgendado.getLocation() != null) && (eventAgendado.getLocation().matches(argGDCEvent.getEvent().getLocation())) && (eventAgendado.getStart() == argGDCEvent.getEvent().getStart()) && (eventAgendado.getEnd() == argGDCEvent.getEvent().getEnd()))
               {
                  // Es una notificación normal y ya está agendada, pero la lógica para comprobar si es el mismo o varía algo es compleja, 
                  // así que eliminamos el event del calendario y subimos el que tenemos del correo.
                  borraEventCalendar(eventAgendado.getId(), argGDCEvent.getCalendarioAsignado());
                  break;
               }
            }
            //System.out.println("\n Comprobando que se añade: Evento " + argGDCEvent.getEvent().getSummary() + " del caso 1111\n");
            lEventsParaSubir.add(argGDCEvent.getEvent());
            
         }
         else if(evArgTipoNotif == 2)
         {
//            System.out.println("\n Evento " + argGDCEvent.getEvent().getSummary() + " es del Caso 2 \n");
            for(Event eventAgendado : lEventsAgendados)
             {
                if((eventAgendado.getLocation() != null) && (eventAgendado.getLocation().matches(argGDCEvent.getEvent().getLocation())))
                {
                   // Actualización de evento agendado, eliminar el evento de calendar si ya existe, y subir el que lo actualiza
                   borraEventCalendar(eventAgendado.getId(), argGDCEvent.getCalendarioAsignado());
                   break;
                }
            }
            //System.out.println("\n Comprobando que se añade: Evento " + argGDCEvent.getEvent().getSummary() + " del caso 2222\n");
            lEventsParaSubir.add(argGDCEvent.getEvent());
         }
         else if(evArgTipoNotif == 3)
         {
            // Notificación cancelada, eliminar el evento de los agendados. 
//System.out.println("\n Comprobando que se borra: Evento " + argGDCEvent.getEvent().getSummary() + " del caso 3333\n");
            for(Event eventAgendado : lEventsAgendados)
             {
                if((eventAgendado.getLocation().matches(argGDCEvent.getEvent().getLocation())))
                {
                   // Actualización de evento agendado, eliminar el evento de calendar si ya existe, y subir el que lo actualiza
                   borraEventCalendar(eventAgendado.getId(), argGDCEvent.getCalendarioAsignado());
                   break;
                }
             }
           }
  
            else if(evArgTipoNotif == 4)
            {
             // Notificación de terminación de un evento, no hacer nada.
//System.out.println("\n Comprobando que no se hace nada. Evento: " + argGDCEvent.getEvent().getSummary() + " del caso 4444\n");
               
         }
         
      }
      return lEventsParaSubir;
   }

}
