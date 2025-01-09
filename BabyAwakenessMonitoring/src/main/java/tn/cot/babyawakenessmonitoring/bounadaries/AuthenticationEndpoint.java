package tn.cot.babyawakenessmonitoring.bounadaries;


import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Response;
import tn.cot.babyawakenessmonitoring.entities.User;
import tn.cot.babyawakenessmonitoring.services.UserServices;
import tn.cot.babyawakenessmonitoring.utils.Oauth2Pkce;

@Path("/")
public class AuthenticationEndpoint {

    @Inject
    UserServices userServices;
    @Inject
    Oauth2Pkce oauth2Pkce;

    @POST
    @Path("/authenticate")
    public Response authenticate(@FormParam("email") String email, @FormParam("password") String password, @CookieParam("XSS-Cookie") Cookie xssCookie ) {
        if(email == null || password == null || xssCookie == null){
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("{\"message\":\"Invalid Credentials!\"}").build();
        }
        try {
            User AttemptedUser  = userServices.authenticateUser(email,password);
            return Response.ok()
                    .entity("{\"AuthorizationCode\":\""+oauth2Pkce.generateAuthorizationCode(xssCookie.getValue(),AttemptedUser)+"\"}") //return authorization code
                    .build();
        } catch (EJBException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\""+e.getMessage()+"\"}").build();
        }
    }
}