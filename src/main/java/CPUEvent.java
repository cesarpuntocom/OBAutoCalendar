import com.google.api.services.calendar.model.*;
import com.google.api.client.util.DateTime;
import java.text.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class CPUEvent
{

   //public static String carpeta;
   public static String remitenteEsperado;
   public static String calendarioAsignado;
   public static Event event;
   
 //  public static String getNombreCarpeta()
 //  {
 //     return carpeta;
 //  }

   public static String getRemitenteEsperado()
   {
      return remitenteEsperado;
   }

   public static String getCalendarioAsignado()
   {
      return calendarioAsignado;
   }

   public static void setCalendarioAsignado(String cal)
   {
      calendarioAsignado = cal;
   }
 //  public static void setCarpeta(String car)
 //  {
 //     carpeta = car;
 //  }
   public static void setRemitenteEsperado(String rem)
   {
      remitenteEsperado = rem;
   }

   public static Event getEvent()
   {
      return event;
   }

   

   // This method receives an string and a tag, and returns the text between this tag and the next return character
 /*  private static String textoEvento(String mail, String tag)
   {
      int indiceTag = 0;
      int indiceSalto = 0;
      String cadena = "";
      String cadenaChars = "";
      char[] cMail = new char[mail.length()];
      indiceTag = mail.indexOf(tag);
      indiceSalto = mail.indexOf('\n',indiceTag);
      if(indiceTag > 0)
      {
         mail.getChars(indiceTag+tag.length(), indiceSalto-1, cMail, 0);
         cadenaChars = new String (cMail);
         cadena = cadenaChars.substring(0, cadenaChars.indexOf('\u0000'));
      }
      return cadena;
   }
*/
   public String textoEvento(String mail, String tag, char hasta)
   {
      int indiceTag = 0;
      int indiceSalto = 0;
      int inicio = 0;
      int fin = 0;
      char[] cMail;
      String cadena = "";
      String cadenaChars = "";
      indiceTag = mail.indexOf(tag);
      indiceSalto = mail.indexOf(hasta,indiceTag);
      inicio = indiceTag + tag.length();
      fin = indiceSalto - 1;
      
      if(indiceTag > 0)
      {
         cMail = new char[mail.length()];

         // debug
         System.out.println(inicio);
         System.out.println(fin);
         System.out.println(cMail);
         System.out.println(mail);
         System.out.println("---------------------\n\n");
        // debug
         mail.getChars(inicio, fin, cMail, 0);
         cadena = new String (cMail);  
         indiceTag = mail.indexOf(tag);
         indiceSalto = mail.indexOf(hasta,indiceTag);
         mail.getChars(indiceTag+tag.length(), indiceSalto-1, cMail, 0);
         cadenaChars = new String (cMail);
         cadena = cadenaChars.substring(0, cadenaChars.indexOf('\u0000'));
      }
      return cadena;
    }

   public String textoEvento(String mail, String tag)
   {
      int indiceTag = 0;
      int indiceSalto = 0;
      int inicio = 0;
      int fin = 0;
      char[] cMail;
      String cadena = "";
      String cadenaChars = "";
      indiceTag = mail.indexOf(tag);
      indiceSalto = mail.indexOf('\n',indiceTag);
      inicio = indiceTag + tag.length();
      fin = indiceSalto - 1;
      
      if(indiceTag > 0)
      {
         cMail = new char[mail.length()];
         mail.getChars(inicio, fin, cMail, 0);
         cadena = new String (cMail);  
         indiceTag = mail.indexOf(tag);
         indiceSalto = mail.indexOf('\n',indiceTag);
         mail.getChars(indiceTag+tag.length(), indiceSalto-1, cMail, 0);
         cadenaChars = new String (cMail);
         cadena = cadenaChars.substring(0, cadenaChars.indexOf('\u0000'));
      }
      return cadena;
    }




/*
   private static String textoEvento(String mail, String tag, char hasta)
   {
      int indiceTag = 0;
      int indiceSalto = 0;
      String cadena = "";
      String cadenaChars = "";
      char[] cMail = new char[mail.length()];
      indiceTag = mail.indexOf(tag);
      indiceSalto = mail.indexOf(hasta,indiceTag);
      if(indiceTag > 0)
      {
         mail.getChars(indiceTag+tag.length(), indiceSalto-1, cMail, 0);
         cadena = new String (cMail);
         cadenaChars = new String (cMail);
         cadena = cadenaChars.substring(0, cadenaChars.indexOf('\u0000'));
      }
      return cadena;
   }
*/


   
}
