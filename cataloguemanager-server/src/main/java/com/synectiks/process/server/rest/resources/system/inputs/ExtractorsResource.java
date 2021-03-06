/*
 * */
package com.synectiks.process.server.rest.resources.system.inputs;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;
import com.synectiks.process.server.ConfigurationException;
import com.synectiks.process.server.audit.AuditEventTypes;
import com.synectiks.process.server.audit.jersey.AuditEvent;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.inputs.Input;
import com.synectiks.process.server.inputs.InputService;
import com.synectiks.process.server.inputs.converters.ConverterFactory;
import com.synectiks.process.server.inputs.extractors.ExtractorFactory;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.plugin.inputs.Converter;
import com.synectiks.process.server.plugin.inputs.Extractor;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.rest.models.system.inputs.extractors.requests.CreateExtractorRequest;
import com.synectiks.process.server.rest.models.system.inputs.extractors.requests.OrderExtractorsRequest;
import com.synectiks.process.server.rest.models.system.inputs.extractors.responses.ExtractorCreated;
import com.synectiks.process.server.rest.models.system.inputs.extractors.responses.ExtractorMetrics;
import com.synectiks.process.server.rest.models.system.inputs.extractors.responses.ExtractorSummary;
import com.synectiks.process.server.rest.models.system.inputs.extractors.responses.ExtractorSummaryList;
import com.synectiks.process.server.shared.inputs.PersistedInputs;
import com.synectiks.process.server.shared.metrics.MetricUtils;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;
import com.synectiks.process.server.shared.system.activities.Activity;
import com.synectiks.process.server.shared.system.activities.ActivityWriter;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RequiresAuthentication
@Api(value = "Extractors", description = "Extractors of an input")
@Path("/system/inputs/{inputId}/extractors")
public class ExtractorsResource extends RestResource {
    private static final Logger LOG = LoggerFactory.getLogger(ExtractorsResource.class);

    private final InputService inputService;
    private final ActivityWriter activityWriter;
    private final MetricRegistry metricRegistry;
    private final ExtractorFactory extractorFactory;
    private final ConverterFactory converterFactory;
    private final PersistedInputs persistedInputs;

    @Inject
    public ExtractorsResource(final InputService inputService,
                              final ActivityWriter activityWriter,
                              final MetricRegistry metricRegistry,
                              final ExtractorFactory extractorFactory,
                              final ConverterFactory converterFactory,
                              final PersistedInputs persistedInputs) {
        this.inputService = inputService;
        this.activityWriter = activityWriter;
        this.metricRegistry = metricRegistry;
        this.extractorFactory = extractorFactory;
        this.converterFactory = converterFactory;
        this.persistedInputs = persistedInputs;
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Add an extractor to an input",
            response = ExtractorCreated.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No such input on this node."),
            @ApiResponse(code = 400, message = "No such extractor type."),
            @ApiResponse(code = 400, message = "Field the extractor should write on is reserved."),
            @ApiResponse(code = 400, message = "Missing or invalid configuration.")
    })
    @AuditEvent(type = AuditEventTypes.EXTRACTOR_CREATE)
    public Response create(@ApiParam(name = "inputId", required = true)
                           @PathParam("inputId") String inputId,
                           @ApiParam(name = "JSON body", required = true)
                           @Valid @NotNull CreateExtractorRequest cer) throws NotFoundException {
        checkPermission(RestPermissions.INPUTS_EDIT, inputId);

        final Input mongoInput = inputService.find(inputId);
        final String id = new com.synectiks.process.server.uuid.UUID().toString();
        final Extractor extractor = buildExtractorFromRequest(cer, id);

        try {
            inputService.addExtractor(mongoInput, extractor);
        } catch (ValidationException e) {
            final String msg = "Extractor persist validation failed.";
            LOG.error(msg, e);
            throw new BadRequestException(msg, e);
        }

        final String msg = "Added extractor <" + id + "> of type [" + cer.extractorType() + "] to input <" + inputId + ">.";
        LOG.info(msg);
        activityWriter.write(new Activity(msg, ExtractorsResource.class));

        final ExtractorCreated result = ExtractorCreated.create(id);
        final URI extractorUri = getUriBuilderToSelf().path(ExtractorsResource.class)
                .path("{inputId}")
                .build(mongoInput.getId());

        return Response.created(extractorUri).entity(result).build();
    }

    @PUT
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update an extractor")
    @Path("/{extractorId}")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No such input on this node."),
            @ApiResponse(code = 404, message = "No such extractor on this input."),
            @ApiResponse(code = 400, message = "No such extractor type."),
            @ApiResponse(code = 400, message = "Field the extractor should write on is reserved."),
            @ApiResponse(code = 400, message = "Missing or invalid configuration.")
    })
    @AuditEvent(type = AuditEventTypes.EXTRACTOR_UPDATE)
    public ExtractorSummary update(@ApiParam(name = "inputId", required = true)
                                      @PathParam("inputId") String inputId,
                                      @ApiParam(name = "extractorId", required = true)
                                      @PathParam("extractorId") String extractorId,
                                      @ApiParam(name = "JSON body", required = true)
                                      @Valid @NotNull CreateExtractorRequest cer) throws NotFoundException {
        checkPermission(RestPermissions.INPUTS_EDIT, inputId);

        final Input mongoInput = inputService.find(inputId);
        final Extractor originalExtractor = inputService.getExtractor(mongoInput, extractorId);
        final Extractor extractor = buildExtractorFromRequest(cer, originalExtractor.getId());

        try {
            inputService.updateExtractor(mongoInput, extractor);
        } catch (ValidationException e) {
            LOG.error("Extractor persist validation failed.", e);
            throw new BadRequestException(e);
        }

        final String msg = "Updated extractor <" + originalExtractor.getId() + "> of type [" + cer.extractorType() + "] in input <" + inputId + ">.";
        LOG.info(msg);
        activityWriter.write(new Activity(msg, ExtractorsResource.class));

        return toSummary(extractor);
    }

    @GET
    @Timed
    @ApiOperation(value = "List all extractors of an input")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No such input on this node.")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public ExtractorSummaryList list(@ApiParam(name = "inputId", required = true)
                                    @PathParam("inputId") String inputId) throws NotFoundException {
        checkPermission(RestPermissions.INPUTS_READ, inputId);

        final Input input = inputService.find(inputId);
        final List<ExtractorSummary> extractors = Lists.newArrayList();
        for (Extractor extractor : inputService.getExtractors(input)) {
            extractors.add(toSummary(extractor));
        }

        return ExtractorSummaryList.create(extractors);
    }

    @GET
    @Timed
    @ApiOperation(value = "Get information of a single extractor of an input")
    @Path("/{extractorId}")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No such input on this node."),
            @ApiResponse(code = 404, message = "No such extractor on this input.")
    })
    @Produces(MediaType.APPLICATION_JSON)
    public ExtractorSummary single(
            @ApiParam(name = "inputId", required = true)
            @PathParam("inputId") String inputId,
            @ApiParam(name = "extractorId", required = true)
            @PathParam("extractorId") final String extractorId) throws NotFoundException {
        checkPermission(RestPermissions.INPUTS_READ, inputId);

        final MessageInput input = persistedInputs.get(inputId);
        if (input == null) {
            LOG.error("Input <{}> not found.", inputId);
            throw new javax.ws.rs.NotFoundException("Couldn't find input " + inputId);
        }

        final Input mongoInput = inputService.find(input.getPersistId());
        final Extractor extractor = inputService.getExtractor(mongoInput, extractorId);

        return toSummary(extractor);
    }

    @DELETE
    @Timed
    @ApiOperation(value = "Delete an extractor")
    @Path("/{extractorId}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid request."),
            @ApiResponse(code = 404, message = "Input not found."),
            @ApiResponse(code = 404, message = "Extractor not found.")
    })
    @Produces(MediaType.APPLICATION_JSON)
    @AuditEvent(type = AuditEventTypes.EXTRACTOR_DELETE)
    public void terminate(
            @ApiParam(name = "inputId", required = true)
            @PathParam("inputId") String inputId,
            @ApiParam(name = "extractorId", required = true)
            @PathParam("extractorId") String extractorId) throws NotFoundException {
        checkPermission(RestPermissions.INPUTS_EDIT, inputId);

        final MessageInput input = persistedInputs.get(inputId);
        if (input == null) {
            LOG.error("Input <{}> not found.", inputId);
            throw new javax.ws.rs.NotFoundException("Couldn't find input " + inputId);
        }

        // Remove from Mongo.
        final Input mongoInput = inputService.find(input.getPersistId());
        final Extractor extractor = inputService.getExtractor(mongoInput, extractorId);
        inputService.removeExtractor(mongoInput, extractor.getId());

        final String msg = "Deleted extractor <" + extractorId + "> of type [" + extractor.getType() + "] " +
                "from input <" + inputId + ">.";
        LOG.info(msg);
        activityWriter.write(new Activity(msg, InputsResource.class));
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update extractor order of an input")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No such input on this node.")
    })
    @Path("order")
    @AuditEvent(type = AuditEventTypes.EXTRACTOR_ORDER_UPDATE)
    public void order(@ApiParam(name = "inputId", value = "Persist ID (!) of input.", required = true)
                      @PathParam("inputId") String inputPersistId,
                      @ApiParam(name = "JSON body", required = true) OrderExtractorsRequest oer) throws NotFoundException {
        checkPermission(RestPermissions.INPUTS_EDIT, inputPersistId);

        final Input mongoInput = inputService.find(inputPersistId);

        for (Extractor extractor : inputService.getExtractors(mongoInput)) {
            if (oer.order().containsValue(extractor.getId())) {
                extractor.setOrder(Tools.getKeyByValue(oer.order(), extractor.getId()));
            }

            // Docs embedded in MongoDB array cannot be updated atomically... :/
            inputService.removeExtractor(mongoInput, extractor.getId());
            try {
                inputService.addExtractor(mongoInput, extractor);
            } catch (ValidationException e) {
                LOG.warn("Validation error for extractor update.", e);
            }
        }

        LOG.info("Updated extractor ordering of input <persist:{}>.", inputPersistId);
    }

    private ExtractorSummary toSummary(Extractor extractor) {
        final ExtractorMetrics metrics = ExtractorMetrics.create(
                MetricUtils.buildTimerMap(metricRegistry.getTimers().get(extractor.getCompleteTimerName())),
                MetricUtils.buildTimerMap(metricRegistry.getTimers().get(extractor.getConditionTimerName())),
                MetricUtils.buildTimerMap(metricRegistry.getTimers().get(extractor.getExecutionTimerName())),
                MetricUtils.buildTimerMap(metricRegistry.getTimers().get(extractor.getConverterTimerName())),
                metricRegistry.getCounters().get(extractor.getConditionHitsCounterName()).getCount(),
                metricRegistry.getCounters().get(extractor.getConditionMissesCounterName()).getCount());

        return ExtractorSummary.create(
                extractor.getId(),
                extractor.getTitle(),
                extractor.getType().toString().toLowerCase(Locale.ENGLISH),
                extractor.getCursorStrategy().toString().toLowerCase(Locale.ENGLISH),
                extractor.getSourceField(),
                extractor.getTargetField(),
                extractor.getExtractorConfig(),
                extractor.getCreatorUserId(),
                extractor.converterConfigMap(),
                extractor.getConditionType().toString().toLowerCase(Locale.ENGLISH),
                extractor.getConditionValue(),
                extractor.getOrder(),
                extractor.getExceptionCount(),
                extractor.getConverterExceptionCount(),
                metrics);
    }

    private List<Converter> loadConverters(Map<String, Map<String, Object>> requestConverters) {
        List<Converter> converters = Lists.newArrayList();

        for (Map.Entry<String, Map<String, Object>> c : requestConverters.entrySet()) {
            try {
                converters.add(converterFactory.create(Converter.Type.valueOf(c.getKey().toUpperCase(Locale.ENGLISH)), c.getValue()));
            } catch (ConverterFactory.NoSuchConverterException e) {
                LOG.warn("No such converter [" + c.getKey() + "]. Skipping.", e);
            } catch (ConfigurationException e) {
                LOG.warn("Missing configuration for [" + c.getKey() + "]. Skipping.", e);
            }
        }

        return converters;
    }

    private Extractor buildExtractorFromRequest(CreateExtractorRequest cer, String id) {
        Extractor extractor;
        try {
            extractor = extractorFactory.factory(
                    id,
                    cer.title(),
                    cer.order(),
                    Extractor.CursorStrategy.valueOf(cer.cutOrCopy().toUpperCase(Locale.ENGLISH)),
                    Extractor.Type.valueOf(cer.extractorType().toUpperCase(Locale.ENGLISH)),
                    cer.sourceField(),
                    cer.targetField(),
                    cer.extractorConfig(),
                    getCurrentUser().getName(),
                    loadConverters(cer.converters()),
                    Extractor.ConditionType.valueOf(cer.conditionType().toUpperCase(Locale.ENGLISH)),
                    cer.conditionValue()
            );
        } catch (ExtractorFactory.NoSuchExtractorException e) {
            LOG.error("No such extractor type.", e);
            throw new BadRequestException(e);
        } catch (Extractor.ReservedFieldException e) {
            LOG.error("Cannot create extractor. Field is reserved.", e);
            throw new BadRequestException(e);
        } catch (ConfigurationException e) {
            LOG.error("Cannot create extractor. Missing configuration.", e);
            throw new BadRequestException(e);
        }
        return extractor;
    }
}
