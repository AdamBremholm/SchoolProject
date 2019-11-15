package se.alten.schoolproject.rest;

import lombok.NoArgsConstructor;
import se.alten.schoolproject.dao.SchoolAccessLocal;
import se.alten.schoolproject.entity.Subject;
import se.alten.schoolproject.exceptions.DuplicateException;
import se.alten.schoolproject.exceptions.MissingFieldException;
import se.alten.schoolproject.exceptions.NoSuchIdException;
import se.alten.schoolproject.exceptions.NoSuchSubjectException;
import se.alten.schoolproject.model.SubjectModel;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Stateless
@NoArgsConstructor
@Path("/subjects")
public class SubjectController {

    @Inject
    private SchoolAccessLocal sal;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listSubjects() {
        try {
            List subject = sal.listAllSubjects();
            return Response.ok(subject).build();

        }  catch ( EJBException e ) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\""+e.getCausedByException()+"\"}").build();
        }
        catch ( Exception e ) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\""+e+"\"}").build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addSubject(Subject subject) {
        try {
            SubjectModel subjectModel = sal.addSubject(subject);
            return Response.ok(subjectModel).build();
        } catch ( DuplicateException e ) {
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
    public Response deleteSubject(@PathParam("uuid") String uuid) {
        try {
            sal.deleteSubjectByUuid(uuid);
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        catch ( NoSuchIdException e ) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\""+e.getClass().getSimpleName()+"\"}").build();
        }
        catch ( EJBException e ) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\""+e.getCausedByException()+"\"}").build();
        }
        catch ( Exception e ) {
            return Response.notModified(e.toString()).status(Response.Status.BAD_GATEWAY).build();
        }
    }
}
