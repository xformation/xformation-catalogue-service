/*
 * */
package com.synectiks.process.server.inputs;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.plugin.IOState;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.plugin.system.NodeId;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputCreated;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputDeleted;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputUpdated;
import com.synectiks.process.server.shared.inputs.InputLauncher;
import com.synectiks.process.server.shared.inputs.InputRegistry;
import com.synectiks.process.server.shared.inputs.NoSuchInputTypeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class InputEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(InputEventListener.class);
    private final InputLauncher inputLauncher;
    private final InputRegistry inputRegistry;
    private final InputService inputService;
    private final NodeId nodeId;

    @Inject
    public InputEventListener(EventBus eventBus,
                              InputLauncher inputLauncher,
                              InputRegistry inputRegistry,
                              InputService inputService,
                              NodeId nodeId) {
        this.inputLauncher = inputLauncher;
        this.inputRegistry = inputRegistry;
        this.inputService = inputService;
        this.nodeId = nodeId;
        eventBus.register(this);
    }

    @Subscribe
    public void inputCreated(InputCreated inputCreatedEvent) {
        final String inputId = inputCreatedEvent.id();
        LOG.debug("Input created: {}", inputId);
        final Input input;
        try {
            input = inputService.find(inputId);
        } catch (NotFoundException e) {
            LOG.warn("Received InputCreated event but could not find input {}", inputId, e);
            return;
        }

        final IOState<MessageInput> inputState = inputRegistry.getInputState(inputId);
        if (inputState != null) {
            inputRegistry.remove(inputState);
        }

        if (input.isGlobal() || this.nodeId.toString().equals(input.getNodeId())) {
            startInput(input);
        }
    }

    @Subscribe
    public void inputUpdated(InputUpdated inputUpdatedEvent) {
        final String inputId = inputUpdatedEvent.id();
        LOG.debug("Input updated: {}", inputId);
        final Input input;
        try {
            input = inputService.find(inputId);
        } catch (NotFoundException e) {
            LOG.warn("Received InputUpdated event but could not find input {}", inputId, e);
            return;
        }

        final boolean startInput;
        final IOState<MessageInput> inputState = inputRegistry.getInputState(inputId);
        if (inputState != null) {
            startInput = inputState.getState() == IOState.Type.RUNNING;
            inputRegistry.remove(inputState);
        } else {
            startInput = false;
        }

        if (startInput && (input.isGlobal() || this.nodeId.toString().equals(input.getNodeId()))) {
            startInput(input);
        }
    }

    private void startInput(Input input) {
        final MessageInput messageInput;
        try {
            messageInput = inputService.getMessageInput(input);
        } catch (NoSuchInputTypeException e) {
            LOG.warn("Input {} ({}) is of invalid type {}", input.getTitle(), input.getId(), input.getType(), e);
            return;
        }
        messageInput.initialize();

        final IOState<MessageInput> newInputState = inputLauncher.launch(messageInput);
        inputRegistry.add(newInputState);
    }

    @Subscribe
    public void inputDeleted(InputDeleted inputDeletedEvent) {
        LOG.debug("Input deleted: {}", inputDeletedEvent.id());
        final IOState<MessageInput> inputState = inputRegistry.getInputState(inputDeletedEvent.id());
        if (inputState != null) {
            inputRegistry.remove(inputState);
        }
    }
}
