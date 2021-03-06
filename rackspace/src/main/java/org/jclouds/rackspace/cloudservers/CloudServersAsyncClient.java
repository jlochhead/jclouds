/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.rackspace.cloudservers;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.http.functions.ReturnFalseOn404;
import org.jclouds.rackspace.CloudServers;
import org.jclouds.rackspace.cloudservers.binders.BindAdminPassToJsonPayload;
import org.jclouds.rackspace.cloudservers.binders.BindBackupScheduleToJsonPayload;
import org.jclouds.rackspace.cloudservers.binders.BindConfirmResizeToJsonPayload;
import org.jclouds.rackspace.cloudservers.binders.BindCreateImageToJsonPayload;
import org.jclouds.rackspace.cloudservers.binders.BindRebootTypeToJsonPayload;
import org.jclouds.rackspace.cloudservers.binders.BindResizeFlavorToJsonPayload;
import org.jclouds.rackspace.cloudservers.binders.BindRevertResizeToJsonPayload;
import org.jclouds.rackspace.cloudservers.binders.BindServerNameToJsonPayload;
import org.jclouds.rackspace.cloudservers.binders.BindSharedIpGroupToJsonPayload;
import org.jclouds.rackspace.cloudservers.domain.Addresses;
import org.jclouds.rackspace.cloudservers.domain.BackupSchedule;
import org.jclouds.rackspace.cloudservers.domain.Flavor;
import org.jclouds.rackspace.cloudservers.domain.Image;
import org.jclouds.rackspace.cloudservers.domain.RebootType;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.domain.SharedIpGroup;
import org.jclouds.rackspace.cloudservers.functions.ParseAddressesFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseBackupScheduleFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseFlavorFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseFlavorListFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseImageFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseImageListFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseInetAddressListFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseServerFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseServerListFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseSharedIpGroupFromJsonResponse;
import org.jclouds.rackspace.cloudservers.functions.ParseSharedIpGroupListFromJsonResponse;
import org.jclouds.rackspace.cloudservers.options.CreateServerOptions;
import org.jclouds.rackspace.cloudservers.options.CreateSharedIpGroupOptions;
import org.jclouds.rackspace.cloudservers.options.ListOptions;
import org.jclouds.rackspace.cloudservers.options.RebuildServerOptions;
import org.jclouds.rackspace.filters.AddTimestampQuery;
import org.jclouds.rackspace.filters.AuthenticateRequest;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MapPayloadParam;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Cloud Servers via their REST API.
 * <p/>
 * All commands return a ListenableFuture of the result from Cloud Servers. Any exceptions incurred
 * during processing will be wrapped in an {@link ExecutionException} as documented in
 * {@link ListenableFuture#get()}.
 * 
 * @see CloudServersClient
 * @see <a href="http://docs.rackspacecloud.com/servers/api/cs-devguide-latest.pdf" />
 * @author Adrian Cole
 */
@SkipEncoding( { '/', '=' })
@RequestFilters( { AuthenticateRequest.class, AddTimestampQuery.class })
@Endpoint(CloudServers.class)
public interface CloudServersAsyncClient {

   /**
    * @see CloudServersClient#listServers
    */
   @GET
   @ResponseParser(ParseServerListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers")
   ListenableFuture<? extends List<Server>> listServers(ListOptions... options);

   /**
    * @see CloudServersClient#getServer
    */
   @GET
   @ResponseParser(ParseServerFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/servers/{id}")
   ListenableFuture<Server> getServer(@PathParam("id") int id);

   /**
    * @see CloudServersClient#deleteServer
    */
   @DELETE
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   @Path("/servers/{id}")
   ListenableFuture<Boolean> deleteServer(@PathParam("id") int id);

   /**
    * @see CloudServersClient#rebootServer
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   ListenableFuture<Void> rebootServer(@PathParam("id") int id,
            @BinderParam(BindRebootTypeToJsonPayload.class) RebootType rebootType);

   /**
    * @see CloudServersClient#resizeServer
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   ListenableFuture<Void> resizeServer(@PathParam("id") int id,
            @BinderParam(BindResizeFlavorToJsonPayload.class) int flavorId);

   /**
    * @see CloudServersClient#confirmResizeServer
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   ListenableFuture<Void> confirmResizeServer(
            @PathParam("id") @BinderParam(BindConfirmResizeToJsonPayload.class) int id);

   /**
    * @see CloudServersClient#revertResizeServer
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   ListenableFuture<Void> revertResizeServer(
            @PathParam("id") @BinderParam(BindRevertResizeToJsonPayload.class) int id);

   /**
    * @see CloudServersClient#createServer
    */
   @POST
   @ResponseParser(ParseServerFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers")
   @MapBinder(CreateServerOptions.class)
   ListenableFuture<Server> createServer(@MapPayloadParam("name") String name,
            @MapPayloadParam("imageId") int imageId, @MapPayloadParam("flavorId") int flavorId,
            CreateServerOptions... options);

   /**
    * @see CloudServersClient#rebuildServer
    */
   @POST
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/action")
   @MapBinder(RebuildServerOptions.class)
   ListenableFuture<Void> rebuildServer(@PathParam("id") int id, RebuildServerOptions... options);

   /**
    * @see CloudServersClient#shareIp
    */
   @PUT
   @Path("/servers/{id}/ips/public/{address}")
   @MapBinder(BindSharedIpGroupToJsonPayload.class)
   ListenableFuture<Void> shareIp(@PathParam("address") String addressToShare,
            @PathParam("id") int serverToTosignBindressTo,
            @MapPayloadParam("sharedIpGroupId") int sharedIpGroup,
            @MapPayloadParam("configureServer") boolean configureServer);

   /**
    * @see CloudServersClient#unshareIp
    */
   @DELETE
   @Path("/servers/{id}/ips/public/{address}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> unshareIp(@PathParam("address") String addressToShare,
            @PathParam("id") int serverToTosignBindressTo);

   /**
    * @see CloudServersClient#changeAdminPass
    */
   @PUT
   @Path("/servers/{id}")
   ListenableFuture<Void> changeAdminPass(@PathParam("id") int id,
            @BinderParam(BindAdminPassToJsonPayload.class) String adminPass);

   /**
    * @see CloudServersClient#renameServer
    */
   @PUT
   @Path("/servers/{id}")
   ListenableFuture<Void> renameServer(@PathParam("id") int id,
            @BinderParam(BindServerNameToJsonPayload.class) String newName);

   /**
    * @see CloudServersClient#listFlavors
    */
   @GET
   @ResponseParser(ParseFlavorListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/flavors")
   ListenableFuture<? extends List<Flavor>> listFlavors(ListOptions... options);

   /**
    * @see CloudServersClient#getFlavor
    */
   @GET
   @ResponseParser(ParseFlavorFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/flavors/{id}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Flavor> getFlavor(@PathParam("id") int id);

   /**
    * @see CloudServersClient#listImages
    */
   @GET
   @ResponseParser(ParseImageListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/images")
   ListenableFuture<? extends List<Image>> listImages(ListOptions... options);

   /**
    * @see CloudServersClient#getImage
    */
   @GET
   @ResponseParser(ParseImageFromJsonResponse.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/images/{id}")
   ListenableFuture<Image> getImage(@PathParam("id") int id);

   /**
    * @see CloudServersClient#deleteImage
    */
   @DELETE
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   @Path("/images/{id}")
   ListenableFuture<Boolean> deleteImage(@PathParam("id") int id);

   /**
    * @see CloudServersClient#createImageFromServer
    */
   @POST
   @ResponseParser(ParseImageFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @MapBinder(BindCreateImageToJsonPayload.class)
   @Path("/images")
   ListenableFuture<Image> createImageFromServer(@MapPayloadParam("imageName") String imageName,
            @MapPayloadParam("serverId") int serverId);

   /**
    * @see CloudServersClient#listSharedIpGroups
    */
   @GET
   @ResponseParser(ParseSharedIpGroupListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/shared_ip_groups")
   ListenableFuture<? extends List<SharedIpGroup>> listSharedIpGroups(ListOptions... options);

   /**
    * @see CloudServersClient#getSharedIpGroup
    */
   @GET
   @ResponseParser(ParseSharedIpGroupFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/shared_ip_groups/{id}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<SharedIpGroup> getSharedIpGroup(@PathParam("id") int id);

   /**
    * @see CloudServersClient#createSharedIpGroup
    */
   @POST
   @ResponseParser(ParseSharedIpGroupFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/shared_ip_groups")
   @MapBinder(CreateSharedIpGroupOptions.class)
   ListenableFuture<SharedIpGroup> createSharedIpGroup(@MapPayloadParam("name") String name,
            CreateSharedIpGroupOptions... options);

   /**
    * @see CloudServersClient#deleteSharedIpGroup
    */
   @DELETE
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   @Path("/shared_ip_groups/{id}")
   ListenableFuture<Boolean> deleteSharedIpGroup(@PathParam("id") int id);

   /**
    * @see CloudServersClient#listBackupSchedule
    */
   @GET
   @ResponseParser(ParseBackupScheduleFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/backup_schedule")
   ListenableFuture<BackupSchedule> getBackupSchedule(@PathParam("id") int serverId);

   /**
    * @see CloudServersClient#deleteBackupSchedule
    */
   @DELETE
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   @Path("/servers/{id}/backup_schedule")
   ListenableFuture<Boolean> deleteBackupSchedule(@PathParam("id") int serverId);

   /**
    * @see CloudServersClient#replaceBackupSchedule
    */
   @POST
   @ExceptionParser(ReturnFalseOn404.class)
   @Path("/servers/{id}/backup_schedule")
   ListenableFuture<Void> replaceBackupSchedule(@PathParam("id") int id,
            @BinderParam(BindBackupScheduleToJsonPayload.class) BackupSchedule backupSchedule);

   /**
    * @see CloudServersClient#listAddresses
    */
   @GET
   @ResponseParser(ParseAddressesFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips")
   ListenableFuture<Addresses> getAddresses(@PathParam("id") int serverId);

   /**
    * @see CloudServersClient#listPublicAddresses
    */
   @GET
   @ResponseParser(ParseInetAddressListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips/public")
   ListenableFuture<? extends List<String>> listPublicAddresses(@PathParam("id") int serverId);

   /**
    * @see CloudServersClient#listPrivateAddresses
    */
   @GET
   @ResponseParser(ParseInetAddressListFromJsonResponse.class)
   @QueryParams(keys = "format", values = "json")
   @Path("/servers/{id}/ips/private")
   ListenableFuture<? extends List<String>> listPrivateAddresses(@PathParam("id") int serverId);

}
