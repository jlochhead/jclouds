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
package org.jclouds.aws.s3;

import static org.jclouds.blobstore.attr.BlobScopes.CONTAINER;

import java.util.SortedSet;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.aws.s3.binders.BindACLToXMLPayload;
import org.jclouds.aws.s3.binders.BindBucketLoggingToXmlPayload;
import org.jclouds.aws.s3.binders.BindNoBucketLoggingToXmlPayload;
import org.jclouds.aws.s3.binders.BindPayerToXmlPayload;
import org.jclouds.aws.s3.binders.BindS3ObjectToPayload;
import org.jclouds.aws.s3.domain.AccessControlList;
import org.jclouds.aws.s3.domain.BucketLogging;
import org.jclouds.aws.s3.domain.BucketMetadata;
import org.jclouds.aws.s3.domain.ListBucketResponse;
import org.jclouds.aws.s3.domain.ObjectMetadata;
import org.jclouds.aws.s3.domain.Payer;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.filters.RequestAuthorizeSignature;
import org.jclouds.aws.s3.functions.BindRegionToXmlPayload;
import org.jclouds.aws.s3.functions.ObjectKey;
import org.jclouds.aws.s3.functions.ParseObjectFromHeadersAndHttpContent;
import org.jclouds.aws.s3.functions.ParseObjectMetadataFromHeaders;
import org.jclouds.aws.s3.functions.ReturnFalseIfBucketAlreadyOwnedByYou;
import org.jclouds.aws.s3.functions.ReturnTrueOn404OrNotFoundFalseIfNotEmpty;
import org.jclouds.aws.s3.options.CopyObjectOptions;
import org.jclouds.aws.s3.options.ListBucketOptions;
import org.jclouds.aws.s3.options.PutBucketOptions;
import org.jclouds.aws.s3.options.PutObjectOptions;
import org.jclouds.aws.s3.predicates.validators.BucketNameValidator;
import org.jclouds.aws.s3.xml.AccessControlListHandler;
import org.jclouds.aws.s3.xml.BucketLoggingHandler;
import org.jclouds.aws.s3.xml.CopyObjectHandler;
import org.jclouds.aws.s3.xml.ListAllMyBucketsHandler;
import org.jclouds.aws.s3.xml.ListBucketHandler;
import org.jclouds.aws.s3.xml.LocationConstraintHandler;
import org.jclouds.aws.s3.xml.PayerHandler;
import org.jclouds.blobstore.attr.BlobScope;
import org.jclouds.blobstore.functions.ReturnFalseOnContainerNotFound;
import org.jclouds.blobstore.functions.ReturnFalseOnKeyNotFound;
import org.jclouds.blobstore.functions.ReturnNullOnKeyNotFound;
import org.jclouds.blobstore.functions.ThrowContainerNotFoundOn404;
import org.jclouds.blobstore.functions.ThrowKeyNotFoundOn404;
import org.jclouds.http.functions.ParseETagHeader;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.HostPrefixParam;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.ParamValidators;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to S3 via their REST API.
 * <p/>
 * All commands return a ListenableFuture of the result from S3. Any exceptions incurred during
 * processing will be wrapped in an {@link ExecutionException} as documented in
 * {@link ListenableFuture#get()}.
 * 
 * @author Adrian Cole
 * @author James Murty
 * @see S3Client
 * @see <a href="http://docs.amazonwebservices.com/AmazonS3/2006-03-01/RESTAPI.html" />
 */
@VirtualHost
@SkipEncoding('/')
@RequestFilters(RequestAuthorizeSignature.class)
@Endpoint(S3.class)
@BlobScope(CONTAINER)
public interface S3AsyncClient {

   /**
    * Creates a default implementation of S3Object
    */
   public S3Object newS3Object();

   /**
    * @see S3Client#getObject
    */
   @GET
   @Path("{key}")
   @ExceptionParser(ReturnNullOnKeyNotFound.class)
   @ResponseParser(ParseObjectFromHeadersAndHttpContent.class)
   ListenableFuture<S3Object> getObject(
            @HostPrefixParam @ParamValidators( { BucketNameValidator.class }) String bucketName,
            @PathParam("key") String key, GetOptions... options);

   /**
    * @see S3Client#headObject
    */
   @HEAD
   @Path("{key}")
   @ExceptionParser(ReturnNullOnKeyNotFound.class)
   @ResponseParser(ParseObjectMetadataFromHeaders.class)
   ListenableFuture<ObjectMetadata> headObject(
            @HostPrefixParam @ParamValidators( { BucketNameValidator.class }) String bucketName,
            @PathParam("key") String key);

   /**
    * @see S3Client#objectExists
    */
   @HEAD
   @Path("{key}")
   @ExceptionParser(ReturnFalseOnKeyNotFound.class)
   ListenableFuture<Boolean> objectExists(
            @HostPrefixParam @ParamValidators( { BucketNameValidator.class }) String bucketName,
            @PathParam("key") String key);

   /**
    * @see S3Client#deleteObject
    */
   @DELETE
   @Path("{key}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteObject(
            @HostPrefixParam @ParamValidators( { BucketNameValidator.class }) String bucketName,
            @PathParam("key") String key);

   /**
    * @see S3Client#putObject
    */
   @PUT
   @Path("{key}")
   @ResponseParser(ParseETagHeader.class)
   ListenableFuture<String> putObject(
            @HostPrefixParam @ParamValidators( { BucketNameValidator.class }) String bucketName,
            @PathParam("key") @ParamParser(ObjectKey.class) @BinderParam(BindS3ObjectToPayload.class) S3Object object,
            PutObjectOptions... options);

   /**
    * @see S3Client#putBucketInRegion
    */
   @PUT
   @Path("/")
   @ExceptionParser(ReturnFalseIfBucketAlreadyOwnedByYou.class)
   ListenableFuture<Boolean> putBucketInRegion(
            // TODO endpoint based on region
            @BinderParam(BindRegionToXmlPayload.class) @Nullable String region,
            @HostPrefixParam @ParamValidators( { BucketNameValidator.class }) String bucketName,
            PutBucketOptions... options);

   /**
    * @see S3Client#deleteBucketIfEmpty
    */
   @DELETE
   @Path("/")
   @ExceptionParser(ReturnTrueOn404OrNotFoundFalseIfNotEmpty.class)
   ListenableFuture<Boolean> deleteBucketIfEmpty(
            @HostPrefixParam @ParamValidators( { BucketNameValidator.class }) String bucketName);

   /**
    * @see S3Client#bucketExists
    */
   @HEAD
   @Path("/")
   @QueryParams(keys = "max-keys", values = "0")
   @ExceptionParser(ReturnFalseOnContainerNotFound.class)
   ListenableFuture<Boolean> bucketExists(
            @HostPrefixParam @ParamValidators( { BucketNameValidator.class }) String bucketName);

   /**
    * @see S3Client#getBucketLocation
    */
   @GET
   @QueryParams(keys = "location")
   @Path("/")
   @XMLResponseParser(LocationConstraintHandler.class)
   ListenableFuture<String> getBucketLocation(
            @HostPrefixParam @ParamValidators( { BucketNameValidator.class }) String bucketName);

   /**
    * @see S3Client#getBucketPayer
    */
   @GET
   @QueryParams(keys = "requestPayment")
   @Path("/")
   @XMLResponseParser(PayerHandler.class)
   ListenableFuture<Payer> getBucketPayer(
            @HostPrefixParam @ParamValidators( { BucketNameValidator.class }) String bucketName);

   /**
    * @see S3Client#setBucketPayer
    */
   @PUT
   @QueryParams(keys = "requestPayment")
   @Path("/")
   ListenableFuture<Void> setBucketPayer(
            @HostPrefixParam @ParamValidators( { BucketNameValidator.class }) String bucketName,
            @BinderParam(BindPayerToXmlPayload.class) Payer payer);

   /**
    * @see S3Client#listBucket
    */
   @GET
   @Path("/")
   @XMLResponseParser(ListBucketHandler.class)
   ListenableFuture<ListBucketResponse> listBucket(
            @HostPrefixParam @ParamValidators( { BucketNameValidator.class }) String bucketName,
            ListBucketOptions... options);

   /**
    * @see S3Client#listOwnedBuckets
    */
   @GET
   @XMLResponseParser(ListAllMyBucketsHandler.class)
   @Path("/")
   ListenableFuture<? extends SortedSet<BucketMetadata>> listOwnedBuckets();

   /**
    * @see S3Client#copyObject
    */
   @PUT
   @Path("{destinationObject}")
   @Headers(keys = "x-amz-copy-source", values = "/{sourceBucket}/{sourceObject}")
   @XMLResponseParser(CopyObjectHandler.class)
   ListenableFuture<ObjectMetadata> copyObject(
            @PathParam("sourceBucket") String sourceBucket,
            @PathParam("sourceObject") String sourceObject,
            @HostPrefixParam @ParamValidators( { BucketNameValidator.class }) String destinationBucket,
            @PathParam("destinationObject") String destinationObject, CopyObjectOptions... options);

   /**
    * @see S3Client#getBucketACL
    */
   @GET
   @QueryParams(keys = "acl")
   @XMLResponseParser(AccessControlListHandler.class)
   @ExceptionParser(ThrowContainerNotFoundOn404.class)
   @Path("/")
   ListenableFuture<AccessControlList> getBucketACL(
            @HostPrefixParam @ParamValidators( { BucketNameValidator.class }) String bucketName);

   /**
    * @see S3Client#putBucketACL
    */
   @PUT
   @Path("/")
   @QueryParams(keys = "acl")
   ListenableFuture<Boolean> putBucketACL(
            @HostPrefixParam @ParamValidators( { BucketNameValidator.class }) String bucketName,
            @BinderParam(BindACLToXMLPayload.class) AccessControlList acl);

   /**
    * @see S3Client#getObjectACL
    */
   @GET
   @QueryParams(keys = "acl")
   @Path("{key}")
   @XMLResponseParser(AccessControlListHandler.class)
   @ExceptionParser(ThrowKeyNotFoundOn404.class)
   ListenableFuture<AccessControlList> getObjectACL(
            @HostPrefixParam @ParamValidators( { BucketNameValidator.class }) String bucketName,
            @PathParam("key") String key);

   /**
    * @see S3Client#putObjectACL
    */
   @PUT
   @QueryParams(keys = "acl")
   @Path("{key}")
   ListenableFuture<Boolean> putObjectACL(
            @HostPrefixParam @ParamValidators( { BucketNameValidator.class }) String bucketName,
            @PathParam("key") String key,
            @BinderParam(BindACLToXMLPayload.class) AccessControlList acl);

   /**
    * @see S3Client#getBucketLogging
    */
   @GET
   @QueryParams(keys = "logging")
   @XMLResponseParser(BucketLoggingHandler.class)
   @ExceptionParser(ThrowContainerNotFoundOn404.class)
   @Path("/")
   ListenableFuture<BucketLogging> getBucketLogging(
            @HostPrefixParam @ParamValidators( { BucketNameValidator.class }) String bucketName);

   /**
    * @see S3Client#enableBucketLogging
    */
   @PUT
   @Path("/")
   @QueryParams(keys = "logging")
   ListenableFuture<Void> enableBucketLogging(
            @HostPrefixParam @ParamValidators( { BucketNameValidator.class }) String bucketName,
            @BinderParam(BindBucketLoggingToXmlPayload.class) BucketLogging logging);

   /**
    * @see S3Client#putBucketLogging
    */
   @PUT
   @Path("/")
   @QueryParams(keys = "logging")
   ListenableFuture<Void> disableBucketLogging(
            @BinderParam(BindNoBucketLoggingToXmlPayload.class) @HostPrefixParam @ParamValidators( { BucketNameValidator.class }) String bucketName);

}
