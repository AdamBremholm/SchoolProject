package se.alten.schoolproject.rest;

import lombok.NoArgsConstructor;
import se.alten.schoolproject.dao.SchoolAccessLocal;
import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.model.StudentModel;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Stateless
@NoArgsConstructor
@Path("/students")

public class StudentController {


    @Inject
    private SchoolAccessLocal<Student, StudentModel> sal;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response showStudents() {
        try {
            List<StudentModel> students = sal.listAll();
            return Response.ok(students).build();
        } catch ( Exception e ) {
            return Response.notModified(e.toString()).status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response getStudentById(@PathParam("id")Long id) {
        try {
            StudentModel result = sal.findById(id);
            return Response.ok(result).build();
        } catch ( Exception e ) {
            return Response.notModified(e.toString()).status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStudentByName(@QueryParam("name") String name) {
        try {
            List<StudentModel> result = sal.findByName(name);
            return Response.ok(result).build();
        } catch ( Exception e ) {
            return Response.notModified(e.toString()).status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStudent(Student student) {
        try {
            StudentModel answer = sal.add(student);
            return Response.ok(answer).build();
        }
        catch ( PersistenceException pe ) {
            return Response.status(Response.Status.CONFLICT).entity("{\""+pe.toString()+"\"}").build();
        }

        catch ( Exception e ) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\""+e.toString()+"\"}").build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            sal.remove(id);
            return Response.ok().build();
        } catch ( Exception e ) {
            return Response.notModified(e.toString()).status(Response.Status.BAD_GATEWAY).build();
        }
    }

    @PUT
    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response updateStudentPut(@PathParam("id") Long id, Student student) {
       try {
          StudentModel result = sal.update(id, student);
           return Response.ok(result).build();
       } catch (Exception e) {
           return Response.notModified(e.toString()).status(Response.Status.BAD_GATEWAY).build();
       }
    }

}
