package se.alten.schoolproject.rest;

import lombok.NoArgsConstructor;
import se.alten.schoolproject.dao.SchoolAccessLocal;
import se.alten.schoolproject.entity.Subject;
import se.alten.schoolproject.model.SubjectModel;

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
        } catch ( Exception e ) {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addSubject(Subject subject) {
        try {
            SubjectModel subjectModel = sal.addSubject(subject);
            return Response.ok(subjectModel).build();
        } catch (Exception e ) {
            return Response.status(404).entity(e.getCause().toString()).build();
        }
    }
}
