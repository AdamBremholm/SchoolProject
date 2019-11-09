package se.alten.schoolproject.rest;

import lombok.NoArgsConstructor;
import se.alten.schoolproject.dao.SchoolAccessLocal;
import se.alten.schoolproject.entity.Student;
import se.alten.schoolproject.exceptions.DuplicateException;
import se.alten.schoolproject.exceptions.MissingFieldException;
import se.alten.schoolproject.exceptions.NoSuchIdException;
import se.alten.schoolproject.exceptions.WrongHttpMethodException;
import se.alten.schoolproject.model.StudentModel;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Stateless
@NoArgsConstructor
@Path("/students")

public class StudentController {


    @Inject
    private SchoolAccessLocal sal;

    /**
     * Method both for listing all students and using query parameter by name.
     * example url /students?name=anna to list all students named anna or /students to list all students
     * @param name to search for, first or lastname, case-insensitive
     * @return List with StudentModel
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response showStudents(@QueryParam("name") String name) {
        List<Student> students;
        try {
            if(name!=null)
                students = sal.findStudentsByName(name);
            else
                students = sal.listAllStudents();

            students.forEach(System.out::println);

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
            StudentModel result = sal.findStudentById(id);
            return Response.ok(result).build();
        }
        catch (NoSuchIdException e ) {
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
            StudentModel answer = sal.addStudent(student);
            return Response.ok(answer).status(Response.Status.CREATED).build();
        }
        catch ( DuplicateException e ) {
            return Response.status(Response.Status.CONFLICT).entity("{\""+e.getClass().getSimpleName()+"\"}").build();
        }
        catch ( MissingFieldException e ) {
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
    @Path("{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            sal.removeStudent(id);
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        catch ( NoSuchIdException e ) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\""+e.getClass().getSimpleName()+"\"}").build();
        }
        catch ( Exception e ) {
            return Response.notModified(e.toString()).status(Response.Status.BAD_GATEWAY).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response replaceStudent(@PathParam("id") Long id, Student student) {
       try {
          StudentModel result = sal.updateStudentFull(id, student);
           return Response.ok(result).build();
       }
       catch ( NoSuchIdException e ) {
           return Response.status(Response.Status.NOT_FOUND).entity("{\""+e.getClass().getSimpleName()+"\"}").build();
       }
       catch ( WrongHttpMethodException e ) {
           return Response.status(Response.Status.METHOD_NOT_ALLOWED).entity("{\""+e.getClass().getSimpleName()+": " + e.getMessage()+"\"}").build();
       }
       catch (Exception e) {
           return Response.notModified(e.toString()).status(Response.Status.BAD_GATEWAY).build();
       }
    }

    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response updateStudentPartial(@PathParam("id") Long id, Student student) {
        try {
            StudentModel result = sal.updateStudentPartial(id, student);
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
