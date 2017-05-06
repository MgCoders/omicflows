package com.mgcoders.api;

import com.mgcoders.entities.MongoClientProvider;
import com.mgcoders.entities.User;
import com.mgcoders.utils.KeyGenerator;
import com.mgcoders.utils.PasswordUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * Created by rsperoni on 05/05/17.
 */
@Path("/users")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Transactional
public class UserService {

    @EJB
    MongoClientProvider mongoClientProvider;
    @Inject
    private KeyGenerator keyGenerator;
    @Context
    private UriInfo uriInfo;
    @Inject
    private Logger logger;

    @POST
    @Path("/login")
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response authenticateUser(@FormParam("email") String email,
                                     @FormParam("password") String password) {
        try {

            // Authenticate the user using the credentials provided
            authenticate(email, password);


            // Issue a token for the user
            String token = issueToken(email);

            // Return the token on the response
            String json = "{\"token\":" + "\"Bearer " + token + "\"}";
            return Response.ok(json).build();
            //return Response.ok().header(AUTHORIZATION, "Bearer " + token).build();

        } catch (Exception e) {
            return Response.status(UNAUTHORIZED).build();
        }
    }

    @POST
    public Response create(User user) {
        user.setPassword(PasswordUtils.digestPassword(user.getPassword()));
        mongoClientProvider.getUserCollection().insertOne(user);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(user.getEmail()).build()).build();
    }


    @GET
    public Response findAllUsers() {
        List<User> allUsers = mongoClientProvider.getUserCollection().find(User.class).into(new ArrayList<>());
        if (allUsers == null)
            return Response.status(NOT_FOUND).build();
        return Response.ok(allUsers).build();
    }


    private void authenticate(String email, String password) throws Exception {
        User user = mongoClientProvider.getUserCollection()
                .find()
                .filter(and(eq("email", email), eq("password", PasswordUtils.digestPassword(password))))
                .first();

        if (user == null)
            throw new SecurityException("Invalid user/password");
    }

    private String issueToken(String login) {
        Key key = keyGenerator.generateKey();
        String jwtToken = Jwts.builder()
                .setSubject(login)
                .setIssuer(uriInfo.getAbsolutePath().toString())
                .setIssuedAt(new Date())
                .setExpiration(toDate(LocalDateTime.now().plusMinutes(15L)))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
        return jwtToken;
    }

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
