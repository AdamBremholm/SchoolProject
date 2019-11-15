package se.alten.schoolproject.rest;


import lombok.NoArgsConstructor;
import se.alten.schoolproject.dao.SchoolAccessLocal;
import se.alten.schoolproject.entity.Teacher;
import se.alten.schoolproject.exceptions.*;
import se.alten.schoolproject.model.TeacherModel;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Stateless
@NoArgsConstructor
@Path("/teachers")

public class TeacherController {


    @Inject
    private SchoolAccessLocal sal;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response showTeachers() {
        List<TeacherModel> teachers;
        try {
            teachers = sal.listAllTeachers();
            return Response.ok(teachers).build();
        }
        catch ( EJBException e ) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\""+e.getCausedByException()+"\"}").build();
        }
        catch ( Exception e ) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\""+e+"\"}").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{uuid}")
    public Response getTeacherByUuid(@PathParam("uuid")String uuid) {
        try {
            TeacherModel result = sal.findTeacherByUuid(uuid);
            return Response.ok(result).build();
        }
        catch (NoSuchIdException e ) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\""+e.getClass().getSimpleName()+"\"}").build();
        }
        catch ( EJBException e ) {
            if(e.getCausedByException() instanceof NoResultException)
                return Response.status(Response.Status.NOT_FOUND).entity("{\""+e.getCausedByException()+"\"}").build();
            else
                return Response.status(Response.Status.BAD_REQUEST).entity("{\""+e.getCausedByException()+"\"}").build();
        }

        catch ( Exception e ) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\""+e.getClass().getSimpleName()+"\"}").build();
        }
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addTeacher(Teacher teacher) {
        try {
            TeacherModel answer = sal.addTeacher(teacher);
            return Response.ok(answer).status(Response.Status.CREATED).build();
        }
        catch ( DuplicateException e ) {
            return Response.status(Response.Status.CONFLICT).entity("{\""+e.getClass().getSimpleName()+"\"}").build();
        }
        catch ( NoSuchSubjectException | MissingFieldException e ) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\""+e.getMessage()+"\"}").build();
        }
        catch ( EJBException e ) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\""+e.getCausedByException()+"\"}").build();
        }
        catch ( Exception e ) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\""+e.getClass().getSimpleName()+"\"}").build();
        }
    }

    @DELETE
    @Path("{uuid}")
    public Response deleteTeacher(@PathParam("uuid") String uuid) {
        try {
            sal.removeTeacher(uuid);
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        catch ( NoSuchIdException e ) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\""+e.getClass().getSimpleName()+"\"}").build();
        }
        catch ( EJBException e ) {
            if(e.getCausedByException() instanceof NoResultException)
                return Response.status(Response.Status.NOT_FOUND).entity("{\""+e.getCausedByException()+"\"}").build();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\""+e.getCausedByException()+"\"}").build();
        }
        catch ( Exception e ) {
            return Response.notModified(e.toString()).status(Response.Status.BAD_GATEWAY).build();
        }
    }


    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{uuid}")
    public Response updateTeacherPartial(@PathParam("uuid") String uuid, Teacher teacher) {
        try {
            TeacherModel result = sal.updateTeacherPartial(uuid, teacher);
            return Response.ok(result).build();
        }
        catch ( NoSuchIdException e ) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\""+e.getClass().getSimpleName()+"\"}").build();
        }
        catch ( NoSuchSubjectException e ) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\""+e.getMessage()+"\"}").build();
        }
        catch ( EJBException e ) {
            if(e.getCausedByException() instanceof NoResultException)
                return Response.status(Response.Status.NOT_FOUND).entity("{\""+e.getCausedByException()+"\"}").build();
            return Response.status(Response.Status.BAD_REQUEST).entity("{\""+e.getCausedByException()+"\"}").build();
        }
        catch (Exception e) {
            return Response.notModified(e.toString()).status(Response.Status.BAD_GATEWAY).build();
        }
    }

}

