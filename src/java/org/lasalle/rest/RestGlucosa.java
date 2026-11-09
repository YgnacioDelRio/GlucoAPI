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
import java.util.ArrayList;
import java.util.List;
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
        ControllerUsuario cu = new ControllerUsuario();
        List<Usuario> lista = new ArrayList<>();
        Gson gson = new Gson();
        String out = "";
        try{
            lista = cu.getUsuarios();
            out = gson.toJson(lista);
        }catch (Exception e){
            e.printStackTrace();
            out = """
                  {"result": "Lista vacia"}
                  """;
        }
        
        return Response.ok(out).build();
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
        Gson gson = new Gson();
        
        if (nombre == null || nombre.isBlank() || email == null || email.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"result\":\"nombre y email son obligatorios\"}")
                    .build();
        }
        
        Usuario user = new Usuario();
        user.setNombre(nombre.trim());
        user.setPrimerApellido(primerApellido);
        user.setSegundoApellido(segundoApellido);
        user.setEmail(email.trim());
        
        try {
            ControllerUsuario cu = new ControllerUsuario();
            boolean insertado = cu.insertUsuario(user);
            if (!insertado) {
                return Response.serverError()
                        .entity("{\"result\":\"No se pudo guardar el usuario\"}")
                        .build();
            }
            return Response.status(Response.Status.CREATED)
                    .entity(gson.toJson(user))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("{\"result\":\"Error al guardar el usuario\"}")
                    .build();
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
        Gson gson = new Gson();
        
        if (idUsuario <= 0 || glucosa <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"result\":\"idUsuario y glucosa deben ser mayores que cero\"}")
                    .build();
        }
        
        Medida medida = new Medida();
        medida.setIdUsuario(idUsuario);
        medida.setGlucosa(glucosa);
        medida.setFechaHora(blankToNull(fechaHora));
        medida.setContexto(blankToNull(contexto));
        medida.setNotas(blankToNull(notas));
        
        try {
            ControllerMedida cm = new ControllerMedida();
            boolean insertado = cm.insertMedida(medida);
            if (!insertado) {
                return Response.serverError()
                        .entity("{\"result\":\"No se pudo guardar la medida \"}")
                        .build();
            }
            return Response.status(Response.Status.CREATED)
                    .entity(gson.toJson(medida))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("{\"result\":\"Error al guardar la medida\"}")
                    .build();
        }
    }
    
    @Path("getMedida")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMedida(
            @QueryParam("idMedida") int idMedida
    ){
        Gson gson = new Gson();
        
        if (idMedida <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"result\":\"idMedida debe ser mayor que cero\"}")
                    .build();
        }
        
        try {
            ControllerMedida cm = new ControllerMedida();
            Medida medida = cm.getMedida(idMedida);
            if (medida == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"result\":\"Medida no encontrada\"}")
                        .build();
            }
            return Response.ok(gson.toJson(medida)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("{\"result\":\"Error al consultar la medida\"}")
                    .build();
        }
    }
    
    @Path("getAllMedidas")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMedidas(){
        Gson gson = new Gson();
        
        try {
            ControllerMedida cm = new ControllerMedida();
            List<Medida> lista = cm.getMedidas();
            return Response.ok(gson.toJson(lista)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("{\"result\":\"Error al consultar las medidas\"}")
                    .build();
        }
    }
    
    private String blankToNull(String valor){
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}