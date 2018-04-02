import java.util.Properties;
import java.util.Date;
import javax.mail.Folder;
import javax.mail.internet.MimeMultipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.*;
import javax.mail.search.*;
import java.io.*;

public class Correo {

   Date fechaRecepcion;
   String asunto;
   String remitente;
   String cuerpo;

   // This method receives an html formatted string and returns its content without the tags, even considering the returns but replacing many returns by just one
   public String sinEtiquetas(String mail)
   {
      char c = 'a';
      String beerre = "";
      String espacio = "";
      String cadena = "";
      boolean tag = false;
      for(int i=0; i<mail.length(); i++){
         c = mail.charAt(i);
         if(c == '<')
            tag = true;
         if (tag){
            if ((beerre == "" && c == 'b') || (beerre == "b" && c == 'r'))
               beerre = beerre + c;
            else if (c == '>')
               tag = false;
         }
         else
            cadena = cadena + c;
      }
      return cadena;
   }


   public void setFechaRecepcion(Date fecha)
   {
      this.fechaRecepcion = fecha;
   }

   public void setFechaRecepcion(long fecha)
   {
      this.fechaRecepcion = new Date(fecha);
   }

  
   public void setAsunto(String asun)
   {
      this.asunto = asun;
   }

   public void setCuerpo(String body, boolean html)
   {
      if (html)
         this.cuerpo = sinEtiquetas(body);
      else
         this.cuerpo = body;
   }

   public void setRemitente(String sender)
   {
      this.remitente = sender;
   }

   public String getAsunto()
   {
      return this.asunto;
   }

   public String getCuerpo()
   {
      return this.cuerpo;
   }

   public String getRemitente()
   {
      return this.remitente;
   }
   public Date getFechaRecepcion()
   {
      return this.fechaRecepcion;
   }
}
