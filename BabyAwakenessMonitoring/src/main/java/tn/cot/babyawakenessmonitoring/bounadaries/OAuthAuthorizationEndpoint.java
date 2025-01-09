package tn.cot.babyawakenessmonitoring.bounadaries;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import tn.cot.babyawakenessmonitoring.utils.Oauth2Pkce;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Path("/")
public class OAuthAuthorizationEndpoint {

    @Inject
    Oauth2Pkce oauth2Pkce;
    @Context
    private UriInfo uriInfo;

    @GET
    @Path("/authorize")
    public Response authorize(@QueryParam("client_id") String client_id, @QueryParam("code_challenge") String code_challenge) {
        if (code_challenge == null || code_challenge.isEmpty() || client_id == null || client_id.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("clientId or codeChallenge is missing").build();
        }
        oauth2Pkce.addChallenge(client_id, code_challenge);
        var secureCookie = new NewCookie.Builder("XSS-Cookie")
                .httpOnly(true)
                .secure(true)
                .sameSite(NewCookie.SameSite.STRICT)
                .domain(uriInfo.getRequestUri().getHost())
                .expiry(Date.from(Instant.now().plus(17, ChronoUnit.MINUTES)))
                .value(client_id)
                .build();
        URI redirectUri = UriBuilder.fromPath("/login/authorization").build();
        return Response.status(Response.Status.FOUND).location(redirectUri).cookie(secureCookie).build();
    }

    @GET
    @Path("/login/authorization")
    public Response loginAuthorization(@CookieParam("xss-Cookie") Cookie xssCookie){
        StreamingOutput stream = (output)->{
            try(var resourceStream = getClass().getResourceAsStream("/signin.html")){
                assert resourceStream != null;
                output.write(resourceStream.readAllBytes());
            }
        };
        return Response.ok().entity(stream).build();
    }
}



