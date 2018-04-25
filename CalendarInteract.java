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
        // .setTimeMin(new DateTime(now.getValue() - 86400000))
         .setOrderBy("startTime")
         .setSingleEvents(true)
         .execute();
      List<Event> lEvents = events.getItems();

      return lEvents;
   }

   public static void subirEvents(List<ArgentinaGDC> lArgGDCSubir, String calendario) throws Exception
   {
      com.google.api.services.calendar.Calendar service = getCalendarService();
      String calId = getIdCalendar(calendario, service);
      // Descargar los eventos agendados
      List<Event> lEventsAgendados = descargaEventsCalendar(calId);
      List<ArgentinaGDC> lEventsTotales = lArgGDCSubir;
      List<ArgentinaGDC> lEventsOrdenados;
      for(Event newArgG : lEventsAgendados)
      {
         ArgentinaGDC argG = new ArgentinaGDC(newArgG);
         lEventsTotales.add(argG);
      }
      // Ordenar eventos agendados y generados por fecha de recepción
      lEventsOrdenados = ordenarEvents(lEventsTotales);
      // Recibimos los eventos que hay que subir al calendario.
      // Hay que eliminar aquellos que están ya en el calendario antes de subir los que los actualizan
      for(Event evAg : lEventsAgendados)
      {
         for(ArgentinaGDC evSubir : lEventsOrdenados)
         {
            if(evAg.getLocation().matches(evSubir.getEvent().getLocation()))
            {
               if(evAg.getId() != null)
                  borraEventCalendar(evAg.getId(), calId);
            }
         }
      }

      List<ArgentinaGDC> lEventsParaSubir = compruebaEventosAgendados(lEventsOrdenados);
      for(ArgentinaGDC ArgEv : lEventsParaSubir)
      {
         Event ev = service.events().insert(calId, ArgEv.getEvent()).execute();
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
 


   public static List<ArgentinaGDC> compruebaEventosAgendados(List<ArgentinaGDC> lEventsOrdenados) throws Exception
   {
      DateTime now = new DateTime(System.currentTimeMillis());
      //int evArgTipoNotif;
      com.google.api.services.calendar.Calendar service = getCalendarService();
      ArgentinaGDC arg = new ArgentinaGDC(new Correo());
      String calId = getIdCalendar(arg.getCalendarioAsignado(), service);
      List<ArgentinaGDC> lEventsParaSubir = new ArrayList<ArgentinaGDC>();
      boolean added = false;
      // Los eventos están ordenados por fecha de recepción.
      // La aplicación tomará cada evento del array ordenado, y si su número de
      // notificación no está en el array de retorno, lo añadirá. Si
      // sí que está, significará que hay otro evento (agendado o no) que se ha recibido
      // posteriormente a este, así que no se añadirá. Si el evento que se
      // añade tiene su EventID distinto de null (estaba agendado), lo eliminaremos de Calendar
      if(lEventsOrdenados.size() > 0) {
         for (ArgentinaGDC argOrd : lEventsOrdenados) {
            if(lEventsParaSubir.size() == 0)
               lEventsParaSubir.add(argOrd);
            else
            {
               for(ArgentinaGDC argSub : lEventsParaSubir)
               {
                  if(argOrd.getEvent().getLocation().matches(argSub.getEvent().getLocation()))
                     added = true;
               }
               if(!added) {
                  lEventsParaSubir.add(argOrd);
               }
               else
                  added = false;
            }
         }
         for(ArgentinaGDC argPaSubir : lEventsParaSubir)
         {
            for(ArgentinaGDC argOrd : lEventsOrdenados) {
               if (argPaSubir.getEvent().getLocation().matches(argOrd.getEvent().getLocation())) {
                  if (argOrd.getEvent().getId() != null) {
                     borraEventCalendar(argPaSubir.getEvent().getId(), calId);
                     argOrd.getEvent().setId(null);
                  }
               }
            }
         }
      }
      return lEventsParaSubir;

/*


      // Ordenar eventos de correos
      for(ArgentinaGDC argGDCEvent : lEventsCorreos) {
         Event even = argGDCEvent.getEvent();
         evArgTipoNotif = argGDCEvent.getTipoNotif();
         if (evArgTipoNotif == 1 && even.getStart().getDate().getValue() > now.getValue()) {
            // Notificación normal, comprobar si está agendada y si no, meter este event para agendar

            for (Event eventAgendado : lEventsAgendados) {
               if ((eventAgendado.getLocation() != null) && (eventAgendado.getLocation().matches(even.getLocation())) && (eventAgendado.getStart() == even.getStart()) && (eventAgendado.getEnd() == even.getEnd())) {
                  // Es una notificación normal y ya está agendada, pero la lógica para comprobar si es el mismo o varía algo es compleja, 
                  // así que eliminamos el event del calendario y subimos el que tenemos del correo.
                  borraEventCalendar(eventAgendado.getId(), calId);
                  break;
               }
            }
            lEventsParaSubir.add(even);

         } else if (evArgTipoNotif == 2 && even.getStart().getDate().getValue() > now.getValue()) {

            for (Event eventAgendado : lEventsAgendados) {
               if ((eventAgendado.getLocation() != null) && (eventAgendado.getLocation().matches(argGDCEvent.getEvent().getLocation()))) {
                  // Actualización de evento agendado, eliminar el evento de calendar si ya existe, y subir el que lo actualiza
                  borraEventCalendar(eventAgendado.getId(), calId);
                  break;
               }
            }
            lEventsParaSubir.add(even);
         } else if (evArgTipoNotif == 3 && even.getStart().getDate().getValue() > now.getValue()) {
            // Notificación cancelada, eliminar el evento de los agendados.
            for (Event eventAgendado : lEventsAgendados) {
               if ((eventAgendado.getLocation().matches(argGDCEvent.getEvent().getLocation()))) {
                  // Actualización de evento agendado, eliminar el evento de calendar si ya existe, y subir el que lo actualiza
                  borraEventCalendar(eventAgendado.getId(), calId);
               }
            }
            for (Event eventPSub : lEventsParaSubir) {
               if (eventPSub.getLocation() == even.getLocation()) {
                  lEventsParaSubir.remove(eventPSub);
               }
            }
         } else if (evArgTipoNotif == 4 && even.getStart().getDate().getValue() > now.getValue()) {
            for (Event eventAgendado : lEventsAgendados) {
               if ((eventAgendado.getLocation().matches(argGDCEvent.getEvent().getLocation()))) {
                  // Fin de evento agendado, eliminar el evento de calendar si ya existe, y subir el que lo actualiza
                  borraEventCalendar(eventAgendado.getId(), calId);
               }
            }
            for (Event eventPSub : lEventsParaSubir) {
               if (eventPSub.getLocation() == even.getLocation() && eventPSub.getSummary().matches(" - Fin -")) {
                  lEventsParaSubir.remove(eventPSub);
               }
            }
            lEventsParaSubir.add(even);
         }
      }
      return lEventsParaSubir;
      */
   }



   private static ArgentinaGDC masReciente(List<ArgentinaGDC> lEventos) {
      ArgentinaGDC masReciente = null;
      if (lEventos.size() > 0) {
         masReciente = lEventos.get(0);
         for (ArgentinaGDC ev : lEventos) {
            if (ev.getFechaRecibido().getValue() > masReciente.getFechaRecibido().getValue())
               masReciente = ev;
         }
         return masReciente;
      }
      else
         return null;
   }


   public static List<ArgentinaGDC> ordenarEvents(List<ArgentinaGDC> lEventsParaOrdenar)
   {
      List<ArgentinaGDC> lEventsDevolver = new ArrayList<ArgentinaGDC>();
      List<ArgentinaGDC> lEventsOrd = new ArrayList<ArgentinaGDC>();
      ArgentinaGDC ar = null;
      int i = lEventsParaOrdenar.size();
      do {
         ar = masReciente(lEventsParaOrdenar);
         lEventsOrd.add(ar);
         lEventsParaOrdenar.remove(ar);
      }
      while(lEventsOrd.size() < i);
      // Ahora tenemos los eventos ordenados de más reciente a menos.
      // Añadir solo los últimos eventos recibidos pertenecientes a la misma notificación
      boolean esta = false;
      if(lEventsOrd.size()>0) {
         for(ArgentinaGDC evOrd : lEventsOrd) {
            if(lEventsDevolver.size()==0)
               lEventsDevolver.add(lEventsOrd.get(0));
            for(ArgentinaGDC evDev : lEventsDevolver) {
               if(evDev.getEvent().getLocation().matches(evOrd.getEvent().getLocation())) {
                  esta = true;
               }
            }
            if(!esta)
               lEventsDevolver.add(evOrd);
            esta = false;
         }
      }
      return lEventsDevolver;
   }
}
