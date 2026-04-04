package com.example.trabajos.services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    private static final String EMAIL_FROM = "mares10isra@gmail.com";
    private static final String EMAIL_PASSWORD = "yfty qhrm yasi xsvd";

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    public boolean enviarCorreo(String destinatario, String asunto, String mensaje) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.trust", SMTP_HOST);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);
            message.setText(mensaje);

            Transport.send(message);
            System.out.println("✅ Correo enviado a: " + destinatario);
            return true;

        } catch (MessagingException e) {
            System.err.println("❌ Error al enviar correo: " + e.getMessage());
            return false;
        }
    }

    public boolean enviarCodigoRecuperacion(String destinatario, String codigo) {
        String asunto = "🔐 Código de recuperación - PETAM";

        String mensaje = String.format(
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "        PLATAFORMA DE EMPLEO TEMPORAL\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "Hola,\n\n" +
                        "Has solicitado recuperar tu contraseña.\n\n" +
                        "🔑 TU CÓDIGO DE VERIFICACIÓN ES:\n\n" +
                        "        %s\n\n" +
                        "⏱️  Este código es válido por 5 minutos.\n\n" +
                        "Si no solicitaste este cambio, ignora este mensaje.\n\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "Equipo de Soporte - PETAM\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                codigo
        );

        return enviarCorreo(destinatario, asunto, mensaje);
    }

    public boolean enviarConfirmacionCambio(String destinatario) {
        String asunto = "✅ Contraseña actualizada - PETAM";

        String mensaje = String.format(
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "        PLATAFORMA DE EMPLEO TEMPORAL\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "Hola,\n\n" +
                        "Te confirmamos que tu contraseña ha sido\n" +
                        "actualizada correctamente.\n\n" +
                        "Si no realizaste este cambio, por favor\n" +
                        "contacta con nuestro soporte de inmediato.\n\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "Equipo de Soporte - PETAM\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        );

        return enviarCorreo(destinatario, asunto, mensaje);
    }
}