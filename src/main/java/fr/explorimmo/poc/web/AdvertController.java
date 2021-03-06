/**
 * 
 */
package fr.explorimmo.poc.web;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import fr.explorimmo.poc.domain.Advert;
import fr.explorimmo.poc.domain.business.Facade;

/**
 * @author louis.gueye@gmail.com
 */
@Component
@Path(value = "/advert")
@Scope("request")
public class AdvertController {

    @Autowired
    private Facade facade;

    @Context
    UriInfo uriInfo;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertController.class);

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response create(final Advert advert) throws Throwable {

        final Long id = facade.createAdvert(advert);

        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(id)).build();

        return Response.created(uri).build();

    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam(value = "id") final Long id) throws Throwable {

        facade.deleteAdvert(id);

        return Response.noContent().build();

    }

    @POST
    @Path(value = "find")
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response find(final @FormParam("description") String description, final @FormParam("name") String name,
            final @FormParam("address.streetAddress") String streetAddress,
            final @FormParam("address.city") String city, final @FormParam("address.postalCode") String postalCode,
            final @FormParam("address.countryCode") String countryCode) throws Throwable {

        final Advert criteria = new Advert();
        criteria.setName(name);
        criteria.setDescription(description);
        criteria.getAddress().setStreetAddress(streetAddress);
        criteria.getAddress().setCity(city);
        criteria.getAddress().setPostalCode(postalCode);
        criteria.getAddress().setCountryCode(countryCode);

        final List<Advert> results = facade.findAdvertsByCriteria(criteria);

        if (CollectionUtils.isEmpty(results)) {
            LOGGER.info("No results found");
        }

        final GenericEntity<List<Advert>> entity = new GenericEntity<List<Advert>>(results) {};

        return Response.ok(entity).build();

    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response get(@PathParam(value = "id") final Long id) throws Throwable {

        final Advert advert = facade.readAdvert(id);

        if (advert == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.ok(advert).build();

    }
}
