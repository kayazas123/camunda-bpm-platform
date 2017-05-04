package org.camunda.bpm.engine.impl;

import static org.camunda.bpm.engine.impl.util.EnsureUtil.ensureNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.camunda.bpm.engine.batch.Batch;
import org.camunda.bpm.engine.exception.NotValidException;
import org.camunda.bpm.engine.history.HistoricProcessInstanceQuery;
import org.camunda.bpm.engine.impl.batch.RestartProcessInstancesBatchCmd;
import org.camunda.bpm.engine.impl.cmd.AbstractProcessInstanceModificationCommand;
import org.camunda.bpm.engine.impl.cmd.ActivityAfterInstantiationCmd;
import org.camunda.bpm.engine.impl.cmd.ActivityBeforeInstantiationCmd;
import org.camunda.bpm.engine.impl.cmd.RestartProcessInstancesCmd;
import org.camunda.bpm.engine.impl.cmd.TransitionInstantiationCmd;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;
import org.camunda.bpm.engine.runtime.RestartProcessInstanceBuilder;

/**
 * @author Anna Pazola
 */
public class RestartProcessInstanceBuilderImpl implements RestartProcessInstanceBuilder {

  protected CommandExecutor commandExecutor;
  protected List<String> processInstanceIds;
  protected List<AbstractProcessInstanceModificationCommand> instructions;
  protected String processDefinitionId;
  protected HistoricProcessInstanceQuery query;
  
  public RestartProcessInstanceBuilderImpl(CommandExecutor commandExecutor, String processDefinitionId) {
    this.commandExecutor = commandExecutor;
    instructions = new ArrayList<AbstractProcessInstanceModificationCommand>();
    ensureNotNull(NotValidException.class, "processDefinitionId", processDefinitionId);
    this.processDefinitionId = processDefinitionId;
    processInstanceIds = new ArrayList<String>();
  }
  
  @Override
  public RestartProcessInstanceBuilder startBeforeActivity(String activityId) {
    ensureNotNull(NotValidException.class, "activityId", activityId);
    instructions.add(new ActivityBeforeInstantiationCmd(null, activityId));
    return this;
  }

  @Override
  public RestartProcessInstanceBuilder startAfterActivity(String activityId) {
    ensureNotNull(NotValidException.class, "activityId", activityId);
    instructions.add(new ActivityAfterInstantiationCmd(null, activityId));
    return this;
  }

  @Override
  public RestartProcessInstanceBuilder startTransition(String transitionId) {
    ensureNotNull(NotValidException.class, "activityId", transitionId);
    instructions.add(new TransitionInstantiationCmd(null, transitionId));
    return this;
  }

  public void execute() {
    execute(true);
  }
  
  public Batch executeAsync() {
    return commandExecutor.execute(new RestartProcessInstancesBatchCmd(commandExecutor, this));
  }

  public List<AbstractProcessInstanceModificationCommand> getInstructions() {
    return instructions;
  }

  public List<String> getProcessInstanceIds() {
    return processInstanceIds;
  }


  @Override
  public RestartProcessInstanceBuilder processInstanceIds(String... processInstanceIds) {
    this.processInstanceIds.addAll(Arrays.asList(processInstanceIds));
    return this;
  }

  @Override
  public RestartProcessInstanceBuilder historicProcessInstanceQuery(HistoricProcessInstanceQuery query) {
    this.query = query;
    return this;
  }
  
  public HistoricProcessInstanceQuery getHistoricProcessInstanceQuery() {
    return query;
  }

  public String getProcessDefinitionId() {
    return processDefinitionId;
  }

  public void setInstructions(List<AbstractProcessInstanceModificationCommand> instructions) {
    this.instructions = instructions;
  }

  public void setProcessDefinitionId(String processDefinitionId) {
    this.processDefinitionId = processDefinitionId;
  }

  @Override
  public RestartProcessInstanceBuilder processInstanceIds(List<String> ids) {
    processInstanceIds.addAll(ids);
    return this;
  }

  public void execute(boolean writeUserOperationLog) {
    commandExecutor.execute(new RestartProcessInstancesCmd(commandExecutor, this, writeUserOperationLog));
  }

}
