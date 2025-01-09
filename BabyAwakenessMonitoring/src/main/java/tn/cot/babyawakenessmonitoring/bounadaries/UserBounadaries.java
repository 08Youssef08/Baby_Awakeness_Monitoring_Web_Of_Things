package tn.cot.babyawakenessmonitoring.bounadaries;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mail.MessagingException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import tn.cot.babyawakenessmonitoring.entities.User;
import tn.cot.babyawakenessmonitoring.services.UserServices;
import jakarta.ejb.EJBException;

@Path("/")
public class UserBounadaries {


    @Inject
    UserServices userServices;


    @GET
    @Path("/")
    public Response getwelcome(){
        StreamingOutput stream = (output)->{
            try(var resourceStream = getClass().getResourceAsStream("/welcome.html")){
                assert resourceStream != null;
                output.write(resourceStream.readAllBytes());
            }
        }; return Response.ok()
                .entity(stream)
                .build();
    }

    @GET
    @Path("/signup")
    public Response signup(){
        StreamingOutput stream = (output)->{
            try(var resourceStream = getClass().getResourceAsStream("/signup.html")){
                assert resourceStream != null;
                output.write(resourceStream.readAllBytes());
            }
        }; return Response.ok()
                .entity(stream)
                .build();
    }

    @POST
    @Path("/users/register")
    public Response register(User user) {
        System.out.println(user.getFullName());
        System.out.println(user.getMobile());
        try {
            userServices.registerUser(user);
            StreamingOutput stream = (output)->{
                try(var resourceStream = getClass().getResourceAsStream("/activate.html")){
                    assert resourceStream != null;
                    output.write(resourceStream.readAllBytes());
                }
            };
            return Response.ok(Response.Status.CREATED)
                    .entity(stream)
                    .type(MediaType.APPLICATION_JSON)
                    .build();}
        catch (EJBException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (MessagingException e) {
            throw new EJBException(e);
        }
    }

    @GET
    @Path("/users/register/activate")
    public Response activate(@QueryParam("activation_code") String code) {
        String numericCode = code.replaceAll("[^\\d]", "");
        try {
            userServices.activateUser(numericCode);
            return Response.ok()
                    .entity("{\"message\": \"User successfully activated.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid or expired activation code.")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred while processing the activation.")
                    .build();
        }
    }
}
