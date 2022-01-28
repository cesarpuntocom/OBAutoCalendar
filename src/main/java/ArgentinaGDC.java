import com.google.api.services.calendar.model.*;
import com.google.api.client.util.DateTime;
import java.text.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class ArgentinaGDC extends CPUEvent
{
// This class represents the way we will process mails from GDC Argentina and convert them into Calendar.Event objects


   
   public static String carpeta = "GDC_Notificaciones";
   public static String calendarioAsignado = "Ventanas GDI/GDC";
   private String numNotificacion;
   // var tipoNotif is for record the type of the notification, and can be:
   //   1 -> Normal;  2 -> Update; 3 -> Cancel; 4 -> Fin
   private int tipoNotif = 0;

   // var notifNum is for separate the number of the notification
   private String notifNum = "";

 
 // var tipoNotificacion is for having in a String the kind of notification we have ("Inicio", "Fin", "Fin (Cancelada)", etc)
   private String tipoNotificacion = "";

   
   public ArgentinaGDC(Correo corr)
   {
      event = new Event();
      //setCarpeta("GDC_Notificaciones");
      setRemitenteEsperado("GDC-Notificaciones@tgtarg.com");
      
      // We set the summary of the Calendar Event object to the subject of the mail
      event.setSummary(corr.getAsunto());
      // We call the funtion textoEvento to get the text between the string "Notificacion Nro " and the first space
      notifNum = textoEvento(event.getSummary(), "Notificacion Nro ", ' ');
      tipoNotificacion = textoEvento(event.getSummary(), getNotifNum() + " \\- ", '-');
      tipoNotif = setTipoNotif();

      
      // We set the location of the Calendar Event object to the concatenation of the GDC ticket ID we can see in the mail body after the text: "Evento Lista de Mensajes: ",
      // and the received date value as a String 
      event.setLocation(numNotificacion);
      String auxiliar = "";
      auxiliar = "Módulo: " + textoEvento(corr.getCuerpo(), "Modulo: ");
      auxiliar = auxiliar + "\nEvento Lista de Mensajes: " + textoEvento(corr.getCuerpo(), "Evento Lista de Mensajes: ");
      auxiliar = auxiliar + "\nDescripción: " + textoEvento(corr.getCuerpo(), "Descripcion: ");
      auxiliar = auxiliar + "\nCausa: " + textoEvento(corr.getCuerpo(), "Causa: ") + "\n";
      event.setDescription(auxiliar);


      // Implementar la fecha y hora del event
      String fechaIni = textoEvento(corr.getCuerpo(), "Fecha y Hora de Inicio: ");
      String fechaFin = "";
      auxiliar = textoEvento(corr.getCuerpo(), "Fecha y Hora de Fin: ");
      if(auxiliar == "")
         auxiliar = (textoEvento(corr.getCuerpo(), "Fecha y Hora Estimada de Solucion: "));
      fechaFin = auxiliar;
      event.setStart(fechaFromString(fechaIni));
      event.setEnd(fechaFromString(fechaFin));
   }
   
   

   // The method below returns the EventDateTime with the value of the date String received as parameter formatted as is propper to this standard mail instance 
   public EventDateTime fechaFromString(String fecha)
   {
      String fechaCompleta = "";
      DateTime date;
      if(fecha.length() > 16)
      {
         String cadInputDia = fecha.substring(0, 10);
         String cadInputHora = fecha.substring(11, 16);
         String cadOutputDia = "";
         String cadOutputHora = "";
         cadInputHora = cadInputHora + " ART";
         SimpleDateFormat formatoAntDia = new SimpleDateFormat("dd/MM/yyyy");
         SimpleDateFormat formatoNuevoDia = new SimpleDateFormat("yyyy-MM-dd");

         SimpleDateFormat formatoAntHora = new SimpleDateFormat("hh:mm zzz");   
         SimpleDateFormat formatoNuevoHora = new SimpleDateFormat("hh:mm:ss.SSS");
         //System.out.println("Cadena de fecha anterior: \n" + cadInputDia);
         //System.out.println("Cadena de hora anterior: \n" + cadInputHora);
         try {
            cadOutputDia = formatoNuevoDia.format(formatoAntDia.parse(cadInputDia));
            cadOutputHora = formatoNuevoHora.format(formatoAntHora.parse(cadInputHora));
            fechaCompleta = cadOutputDia + "T" + cadOutputHora + "+01:00";
         } catch (ParseException e) {
            e.printStackTrace();
         }
      }
      else
        System.out.println("Cadena de fecha no válida: \n" + fecha);
      date = new DateTime(fechaCompleta);
      EventDateTime devolver = new EventDateTime()
         .setDateTime(date)
         .setTimeZone("America/Argentina/Buenos_Aires");
      return devolver;
   }

   public static Event getEvent()
   {
      return event;
   }

   public static void setEvent(Event ev)
   {
      event = ev;
   }
   public String getTipoNotificacion()
   {
      return tipoNotificacion;
   }

   public String getNotifNum()
   {
      return notifNum;
   }


   public void setNotifNum(String notifNumManual)
   {
      notifNum = notifNumManual;
   }

   public static String getCalendarioAsignado()
   {
      return calendarioAsignado;
   }  


   public int getTipoNotif()
   {
      return tipoNotif;
   }
   
   public int setTipoNotif()
   {
      if(tipoNotificacion.contains("Actualizacion") || tipoNotificacion.contains("Fe de Erratas"))
         return 2;
      else if(tipoNotificacion.contains("Cancelada"))
         return 3;
      else if(tipoNotificacion.contains("Fin"))
         return 4;
      else
         return 1;    
   } 
      
   public static String getNombreCarpeta()
   {
      return carpeta;
   }

/*
   
         for(Evento eventoSubir : lEventos)
         {
             evSub = new Event()
              .setSummary(eventoSubir.getTitulo())
              .setLocation(eventoSubir.getIdEvento())
              .setDescription(eventoSubir.getDescripcion());

             DateTime startDateTime = new DateTime(eventoSubir.getFechaIni());
             EventDateTime start3 = new EventDateTime()
              .setDateTime(startDateTime)
              .setTimeZone("America/Argentina/Buenos_Aires");
             evSub.setStart(start3);

             DateTime endDateTime = new DateTime(eventoSubir.getFechaFin());
             EventDateTime end3 = new EventDateTime()
              .setDateTime(endDateTime)
              .setTimeZone("America/Argentina/Buenos_Aires");
             evSub.setEnd(end3);

             EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("email").setMinutes(1 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
             };
             Event.Reminders reminders = new Event.Reminders()
               .setUseDefault(false)
               .setOverrides(Arrays.asList(reminderOverrides));
             evSub.setReminders(reminders);

             lEventsSubir.add(evSub);
         }

         if (lEventsSubir.size() == 0) {
            System.out.println("No hay eventos para subir.");
         } 

/*
         else {
            System.out.println("Eventos para subir:");
            for (Event event : lEvents) {
               start = event.getStart().getDateTime();
               if (start == null) {
                  start = event.getStart().getDate();
               end = event.getEnd().getDateTime();
               if (end == null) {
                  end = event.getEnd().getDate();
            }
            System.out.printf("%s \n starts -> (%s) \n ends -> %s  .. ID: %s\n\n.......\n\n", event.getSummary(), start, end, event.getLocation());            
         }

      }
      return lEventsSubir;
   }
*/
}
