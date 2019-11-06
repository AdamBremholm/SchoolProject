package se.alten.schoolproject.rest;

import lombok.NoArgsConstructor;
import se.alten.schoolproject.dao.SchoolAccessLocal;
import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.exceptions.NoSuchIdException;
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


    /**
     * Method both for listing all students and using query parameter by name.
     * example url /students?name=anna to list all students named anna or /students to list all students
     * @param name to search for, first or lastname, case-insensitive
     * @return List with StudentModel
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response showStudents(@QueryParam("name") String name) {
        List<StudentModel> students = null;
        try {
            if(name!=null)
            students = sal.findByName(name);
            else students = sal.listAll();
            return Response.ok(students).build();
        } catch ( Exception e ) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\""+e+"\"}").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response getStudentById(@PathParam("id")Long id) {
        try {
            StudentModel result = sal.findById(id);
            return Response.ok(result).build();
        }
        catch ( NoSuchIdException e ) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\""+e.getClass().getSimpleName()+"\"}").build();
        }

        catch ( Exception e ) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\""+e.getClass().getSimpleName()+"\"}").build();
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
            return Response.status(Response.Status.CONFLICT).entity("{\""+pe.getClass().getSimpleName()+"\"}").build();
        }

        catch ( Exception e ) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\""+e.getClass().getSimpleName()+"\"}").build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            sal.remove(id);
            return Response.ok().build();
        }
        catch ( NoSuchIdException e ) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\""+e.getClass().getSimpleName()+"\"}").build();
        }
        catch ( Exception e ) {
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
       }
       catch ( NoSuchIdException e ) {
           return Response.status(Response.Status.NOT_FOUND).entity("{\""+e.getClass().getSimpleName()+"\"}").build();
       }
       catch (Exception e) {
           return Response.notModified(e.toString()).status(Response.Status.BAD_GATEWAY).build();
       }
    }

}
