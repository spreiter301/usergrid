/*******************************************************************************
 * Copyright (c) 2010, 2011 Ed Anuff and Usergrid, all rights reserved.
 * http://www.usergrid.com
 * 
 * This file is part of Usergrid Stack.
 * 
 * Usergrid Stack is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * Usergrid Stack is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along
 * with Usergrid Stack. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under GNU AGPL version 3 section 7
 * 
 * Linking Usergrid Stack statically or dynamically with other modules is making
 * a combined work based on Usergrid Stack. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 * 
 * In addition, as a special exception, the copyright holders of Usergrid Stack
 * give you permission to combine Usergrid Stack with free software programs or
 * libraries that are released under the GNU LGPL and with independent modules
 * that communicate with Usergrid Stack solely through:
 * 
 *   - Classes implementing the org.usergrid.services.Service interface
 *   - Apache Shiro Realms and Filters
 *   - Servlet Filters and JAX-RS/Jersey Filters
 * 
 * You may copy and distribute such a system following the terms of the GNU AGPL
 * for Usergrid Stack and the licenses of the other code concerned, provided that
 ******************************************************************************/
package org.usergrid.rest.management.organizations.applications;

import java.util.UUID;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.usergrid.management.ApplicationInfo;
import org.usergrid.management.OrganizationInfo;
import org.usergrid.rest.AbstractContextResource;
import org.usergrid.rest.ApiResponse;
import org.usergrid.rest.security.annotations.RequireOrganizationAccess;
import org.usergrid.security.oauth.ClientCredentialsInfo;

import com.sun.jersey.api.json.JSONWithPadding;

@Component("org.usergrid.rest.management.organizations.applications.ApplicationResource")
@Scope("prototype")
@Produces({ MediaType.APPLICATION_JSON, "application/javascript",
		"application/x-javascript", "text/ecmascript",
		"application/ecmascript", "text/jscript" })
public class ApplicationResource extends AbstractContextResource {

	OrganizationInfo organization;
	UUID applicationId;
	ApplicationInfo application;

	public ApplicationResource() {
	}

	public ApplicationResource init(OrganizationInfo organization,
			UUID applicationId) {
		this.organization = organization;
		this.applicationId = applicationId;
		return this;
	}

	public ApplicationResource init(OrganizationInfo organization,
			ApplicationInfo application) {
		this.organization = organization;
		applicationId = application.getId();
		this.application = application;
		return this;
	}

	@RequireOrganizationAccess
	@DELETE
	public JSONWithPadding deleteApplicationFromOrganizationByApplicationId(
			@Context UriInfo ui,
			@QueryParam("callback") @DefaultValue("callback") String callback)
			throws Exception {

		ApiResponse response = new ApiResponse(ui);
		response.setAction("delete application from organization");

		management.deleteOrganizationApplication(organization.getUuid(),
				applicationId);

		return new JSONWithPadding(response, callback);
	}

	@RequireOrganizationAccess
	@GET
	@Path("credentials")
	public JSONWithPadding getCredentials(@Context UriInfo ui,
			@QueryParam("callback") @DefaultValue("callback") String callback)
			throws Exception {

		ApiResponse response = new ApiResponse(ui);
		response.setAction("get application client credentials");

		ClientCredentialsInfo credentials = new ClientCredentialsInfo(
				management.getClientIdForApplication(applicationId),
				management.getClientSecretForApplication(applicationId));

		response.setCredentials(credentials);
		return new JSONWithPadding(response, callback);
	}

	@RequireOrganizationAccess
	@POST
	@Path("credentials")
	public JSONWithPadding generateCredentials(@Context UriInfo ui,
			@QueryParam("callback") @DefaultValue("callback") String callback)
			throws Exception {

		ApiResponse response = new ApiResponse(ui);
		response.setAction("generate application client credentials");

		ClientCredentialsInfo credentials = new ClientCredentialsInfo(
				management.getClientIdForApplication(applicationId),
				management.newClientSecretForApplication(applicationId));

		response.setCredentials(credentials);
		return new JSONWithPadding(response, callback);
	}

}