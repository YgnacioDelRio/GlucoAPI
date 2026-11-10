/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.rest;

import com.google.gson.Gson;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.lasalle.controller.ControllerMedida;
import org.lasalle.controller.ControllerUsuario;
import org.lasalle.model.Medida;
import org.lasalle.model.Usuario;

/**
 *
 * @author Halo
 */

@Path("glucosa")
public class RestGlucosa {
    @Path("saludar")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response saludar(){
        String respuesta = """
                           {"response":"Hola desde Java"}
                           """;
        return Response.ok(respuesta).build();
    }
    
    @Path("saludarPersonalizado")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response saludarPersonalizado(
            @QueryParam("nombre") String nombre
    ){
        String respuesta = """
                           {"response":"Hola %s desde Java"}
                           """;
        respuesta = String.format(respuesta, nombre);
        return Response.ok(respuesta).build();
    }
    
    @Path("cuadrado")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response cuadrado(
            @QueryParam("num") int number
    ){
        int resultado = number * number;
        String respuesta = """
                           {"response":"El cuadrado de %s es: %s"}
                           """;
        respuesta = String.format(respuesta, number,  resultado);
        return Response.ok(respuesta).build();
    }
    
    @Path("getAllUsuarios")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsuario(){
        Gson gson = new Gson();
        try{
            ControllerUsuario cu = new ControllerUsuario();
            List<Usuario> lista = cu.getUsuarios();
            return Response.ok(gson.toJson(lista)).build();
        }catch (Exception e){
            return serverError("Error al consultar los usuarios", e);
        }
    }
    
    @Path("insertUsuario")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertUsuario(
            @FormParam("nombre") String nombre,
            @FormParam("primerApellido") String primerApellido,
            @FormParam("segundoApellido") String segundoApellido,
            @FormParam("email") String email
    ){
        String nombreLimpio = nombre == null ? null : nombre.trim();
        String primerApellidoLimpio = primerApellido == null ? null : primerApellido.trim();
        String segundoApellidoLimpio = blankToNull(segundoApellido);
        String emailLimpio = email == null ? null : email.trim();
        
        if (isBlank(nombreLimpio) || isBlank(primerApellidoLimpio) || isBlank(emailLimpio)) {
            return badRequest("nombre, primerApellido y email son obligatorios");
        }
        
        if (nombreLimpio.length() > 40
                || primerApellidoLimpio.length() > 40
                || (segundoApellidoLimpio != null && segundoApellidoLimpio.length() > 40)
                || emailLimpio.length() > 50) {
            return badRequest("Los campos exceden la longitud permitida");
        }
        
        Usuario user = new Usuario();
        user.setNombre(nombreLimpio);
        user.setPrimerApellido(primerApellidoLimpio);
        user.setSegundoApellido(segundoApellidoLimpio);
        user.setEmail(emailLimpio);
        
        try {
            ControllerUsuario cu = new ControllerUsuario();
            boolean insertado = cu.insertUsuario(user);
            if (!insertado) {
                return serverError("No se pudo guardar el usuario", new Exception("executeUpdate devolvió 0 filas"));
            }
            return Response.status(Response.Status.CREATED)
                    .entity(new Gson().toJson(user))
                    .build();
        } catch (Exception e) {
            return serverError("Error al guardar el usuario", e);
        }
    }
    
    @Path("insertMedida")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertMedida(
            @FormParam("idUsuario") int idUsuario,
            @FormParam("glucosa") int glucosa,
            @FormParam("fechaHora") String fechaHora,
            @FormParam("contexto") String contexto,
            @FormParam("notas") String notas
    ){
        if (idUsuario <= 0 || glucosa <= 0) {
            return badRequest("idUsuario y glucosa deben ser mayores que cero");
        }
        
        String fechaHoraNormalizada = normalizeFechaHora(fechaHora);
        if (!isBlank(fechaHora) && fechaHoraNormalizada == null) {
            return badRequest("fechaHora debe tener formato yyyy-MM-dd HH:mm:ss o ISO-8601");
        }
        
        Medida medida = new Medida();
        medida.setIdUsuario(idUsuario);
        medida.setGlucosa(glucosa);
        medida.setFechaHora(fechaHoraNormalizada);
        medida.setContexto(blankToNull(contexto));
        medida.setNotas(blankToNull(notas));
        
        try {
            ControllerUsuario cu = new ControllerUsuario();
            if (cu.getUsuario(idUsuario) == null) {
                return badRequest("idUsuario no existe");
            }
            
            ControllerMedida cm = new ControllerMedida();
            boolean insertado = cm.insertMedida(medida);
            if (!insertado) {
                return serverError("No se pudo guardar la medida", new Exception("executeUpdate devolvió 0 filas"));
            }
            return Response.status(Response.Status.CREATED)
                    .entity(new Gson().toJson(medida))
                    .build();
        } catch (Exception e) {
            return serverError("Error al guardar la medida", e);
        }
    }
    
    @Path("getMedida")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMedida(
            @QueryParam("idMedida") int idMedida
    ){
        if (idMedida <= 0) {
            return badRequest("idMedida debe ser mayor que cero");
        }
        
        try {
            ControllerMedida cm = new ControllerMedida();
            Medida medida = cm.getMedida(idMedida);
            if (medida == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"result\":\"Medida no encontrada\"}")
                        .build();
            }
            return Response.ok(new Gson().toJson(medida)).build();
        } catch (Exception e) {
            return serverError("Error al consultar la medida", e);
        }
    }
    
    @Path("getAllMedidas")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMedidas(){
        try {
            ControllerMedida cm = new ControllerMedida();
            List<Medida> lista = cm.getMedidas();
            return Response.ok(new Gson().toJson(lista)).build();
        } catch (Exception e) {
            return serverError("Error al consultar las medidas", e);
        }
    }
    
    private Response badRequest(String mensaje){
        Map<String, String> body = new HashMap<>();
        body.put("result", mensaje);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new Gson().toJson(body))
                .build();
    }
    
    private Response serverError(String mensaje, Exception e){
        e.printStackTrace();
        Map<String, String> body = new HashMap<>();
        body.put("result", mensaje);
        if (Boolean.parseBoolean(System.getenv().getOrDefault("DEBUG", "false"))) {
            body.put("detail", e.getMessage());
        }
        return Response.serverError()
                .entity(new Gson().toJson(body))
                .build();
    }
    
    private boolean isBlank(String valor){
        return valor == null || valor.isBlank();
    }
    
    private String blankToNull(String valor){
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
    
    private String normalizeFechaHora(String valor){
        if (valor == null || valor.isBlank()) {
            return null;
        }
        
        String limpio = valor.trim();
        if (limpio.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
            return limpio;
        }
        
        DateTimeFormatter salida = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            return OffsetDateTime.parse(limpio, DateTimeFormatter.ISO_DATE_TIME)
                    .toLocalDateTime()
                    .format(salida);
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(limpio, DateTimeFormatter.ISO_DATE_TIME)
                        .format(salida);
            } catch (DateTimeParseException e2) {
                return null;
            }
        }
    }
}
