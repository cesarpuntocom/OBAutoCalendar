import java.util.Properties;
import javax.mail.Folder;
import javax.mail.internet.MimeMultipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.*;
import javax.mail.search.*;
import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


public class DescargaCorreos{

   // This method gets the mails from the server (GMail in this case) and returns a list of Correo objects with the mails relevant information 
   public static List<Correo> getCorreos(String email, String pass, String carpeta)
   {
      //set properties 
      Properties properties = new Properties();
      //We use imaps, *s -Secured
      properties.put("mail.store.protocol", "imaps");
      //Host Address of GMail
      properties.put("mail.imaps.host", "imap.gmail.com");
      //Port number of GMail for imaps
      properties.put("mail.imaps.port", "993");

      Message[] mensajes = null;
      Multipart mp = null;  
      String cadCorreo = "";
      Correo corr = null;
      List<Correo> lCorreos = new ArrayList<Correo>();

      try {
         //create a session  
         Session session = Session.getDefaultInstance(properties, null);
         //SET the store for IMAPS
         Store store = session.getStore("imaps");
         //Trying to connect IMAP server
         store.connect(email, pass);
         Folder inbox = store.getFolder(carpeta);
         //We set READ_WRITE for the mails to set in read, and so we will download the unread ones every time (*We can set READ instead)
         inbox.open(Folder.READ_WRITE);
     
         // When we change it to get unread only, we will touch the lines below:
         // Flags seen = new Flags(Flags.Flag.SEEN);
         // FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
         // mensajes = inbox.search(unseenFlagTerm);
      
         // We downloads the messages to our array mensajes
         mensajes = inbox.getMessages();
         for(Message m : mensajes)
         {
           if(m != null)
            {
               // We extract the multipart from the message, a different representation of the same message, and will extract the parts with the method getText()
               mp = (MimeMultipart)m.getContent();
               corr = new Correo();
               // We get the index 1 of the multipart provided array, because this is the one that always contains the data we need. 
               cadCorreo = getText(mp.getBodyPart(1)) + "\n";
               // We fill the Correo type object corr with the propper data in the mail, and add it to the Correo array 
               corr.setAsunto(m.getSubject());
               corr.setFechaRecepcion(m.getReceivedDate());
               corr.setCuerpo(cadCorreo, true);
               corr.setRemitente(m.getReplyTo()[0].toString());
               lCorreos.add(corr);
            }
         }
         inbox.close(true);
         store.close();
 
      } catch (Exception e) {
         e.printStackTrace();
      }
      return lCorreos;
   }


           
      

   // This method receives a Part type object and returns its body and its parts content as a String
   private static String getText(Part p) throws MessagingException, IOException 
   {
      boolean textIsHtml;
      if (p.isMimeType("text/*")) {
         String s = (String)p.getContent();
         textIsHtml = p.isMimeType("text/html");
         return s;
      }
      if (p.isMimeType("multipart/alternative")) {
         // prefer html text over plain text
         Multipart mp = (Multipart)p.getContent();
         String text = null;
         for (int i = 0; i < mp.getCount(); i++) {
            Part bp = mp.getBodyPart(i);
            if (bp.isMimeType("text/plain")) {
               if (text == null)
                  text = getText(bp);
               continue;
            } else if (bp.isMimeType("text/html")) {
               String s = getText(bp);
               if (s != null)
                  return s;
            } else {
               return getText(bp);
            }
         }
         return text;
      } else if (p.isMimeType("multipart/*")) {
         Multipart mp = (Multipart)p.getContent();
         for (int i = 0; i < mp.getCount(); i++) {
            String s = getText(mp.getBodyPart(i));
            if (s != null)
               return s;
         }
      }
      return null;
   } 


}
