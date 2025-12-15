import React, { useEffect, useState } from 'react';
import {
  StyledTable,
  AddButton,
  FormContainer,
  SaveButton,
  CloseButton,
  ButtonGroup,
  DailyLogContainer,
  DescriptionText,
} from '../styles/TimeSheetStyles.style';
import {
  Contract,
  DailyLog,
  LogEntry,
  TimeSheetLogState,
  TimeSheetHandlers,
  TimeSheetReferenceData,
} from '../entities/TimeSheetEntity';
import LogAction from '../components/reusableComponents/LogAction';
import { toast } from 'sonner';

interface DailyLogTableProps {
  dateISO: string;
  dailyLogs: DailyLog[];
  logState: TimeSheetLogState;
  handlers: TimeSheetHandlers;
  referenceData: TimeSheetReferenceData;
}

const DailyLogTable: React.FC<DailyLogTableProps> = ({
  dateISO,
  dailyLogs,
  logState,
  handlers,
  referenceData,
}) => {
  const {
    selectedDate,
    logEntries,
    setLogEntries,
    addButtonClicked,
    setAddButtonClicked,
    isEditingMode,
    setIsEditingMode,
    setSelectedDate,
  } = logState;

  const { handleSaveLogEntries, onDelete, onEdit, onUpdate, onCancel } =
    handlers;

  const {
    projectOptions,
    getProjectName,
    getContractTitle,
    setContractLookup,
    fetchContractsForProject,
  } = referenceData;

  const [contractOptions, setContractOptions] = useState<
    Record<number, Contract[]>
  >({});

  const isEditing = isEditingMode;

  useEffect(() => {
    if (
      logEntries.length > 0 &&
      logEntries[0].projectId &&
      !contractOptions[0] &&
      logEntries[0].contractId
    ) {
      loadContractsForProject(logEntries[0].projectId, 0);
    }
  }, [logEntries, contractOptions, projectOptions]);

  const loadContractsForProject = async (projectId: string, index: number) => {
    try {
      const contracts = await fetchContractsForProject(projectId);

      setContractOptions((prev) => ({ ...prev, [index]: contracts }));

      setContractLookup((prev) => {
        const updated = { ...prev };
        contracts.forEach((c) => {
          updated[c.contractId] = c.contractTitle;
        });
        return updated;
      });
    } catch {
      toast.error('Failed to load contracts');
      setContractOptions((prev) => ({ ...prev, [index]: [] }));
    }
  };

  const handleInputChange = (
    index: number,
    field: keyof LogEntry,
    value: string
  ) => {
    setLogEntries((prevEntries) => {
      const updatedEntries = [...prevEntries];
      updatedEntries[index] = { ...updatedEntries[index], [field]: value };
      return updatedEntries;
    });
  };

  const addButtonEntries =
    logEntries.length === 0 ? null : (
      <FormContainer>
        {logEntries.map((entry, index) => (
          <React.Fragment key={`form-row-${index}-${entry.projectId || 'new'}`}>
            <div key={index} className="FormRow">
              <select
                value={entry.projectId}
                onChange={async (e) => {
                  const selectedProjectId = e.target.value;

                  handleInputChange(index, 'projectId', selectedProjectId);
                  handleInputChange(index, 'contractId', '');

                  if (selectedProjectId) {
                    await loadContractsForProject(selectedProjectId, index);
                  }
                }}
              >
                <option value="">Select Project</option>
                {projectOptions?.map((project) => (
                  <option key={project.projectId} value={project.projectId}>
                    {project.name}
                  </option>
                ))}
              </select>

              <select
                value={entry.contractId}
                disabled={!entry.projectId}
                onChange={(e) =>
                  handleInputChange(index, 'contractId', e.target.value)
                }
              >
                <option value="">Select Contract</option>

                {contractOptions[index]?.map((contract) => (
                  <option key={contract.contractId} value={contract.contractId}>
                    {contract.contractTitle}
                  </option>
                ))}
              </select>

              <select
                className="hoursSelect"
                value={entry.loghour}
                onChange={(e) =>
                  handleInputChange(index, 'loghour', e.target.value)
                }
              >
                {Array.from({ length: 16 }, (_, i) => {
                  const value = (i + 1) * 0.5;
                  return (
                    <option key={value} value={value.toString()}>
                      {value} hrs
                    </option>
                  );
                })}
              </select>

              <textarea
                placeholder="Description"
                value={entry.description}
                title={entry.description}
                rows={1}
                onChange={(e) => {
                  e.target.style.height = 'auto';
                  e.target.style.height = `${e.target.scrollHeight}px`;
                  handleInputChange(index, 'description', e.target.value);
                }}
              />
            </div>
            <div className="ButtonRow">
              <ButtonGroup>
                {isEditing ? (
                  <>
                    <CloseButton onClick={onCancel}>Cancel</CloseButton>

                    <SaveButton onClick={onUpdate}>Update</SaveButton>
                  </>
                ) : (
                  <SaveButton onClick={handleSaveLogEntries}>Save</SaveButton>
                )}
              </ButtonGroup>
            </div>
          </React.Fragment>
        ))}
      </FormContainer>
    );

  return (
    <DailyLogContainer>
      <StyledTable>
        <thead>
          <tr>
            <th className="projectWidth">Project</th>
            <th className="contractWidth">Contract</th>
            <th className="logHoursWidth">Log Hours</th>
            <th className="descriptionWidth">Description</th>
            <th className="actionWidth">Action</th>
          </tr>
        </thead>
        <tbody>
          {dailyLogs.length === 0 ? (
            <tr>
              <td className="no-entries" colSpan={5}>
                No entries yet.
              </td>
            </tr>
          ) : (
            dailyLogs.map((log, logIndex) => (
              <tr key={logIndex}>
                <td>{getProjectName(log.projectId)}</td>
                <td>{getContractTitle(log.contractId)}</td>
                <td>{log.loggedHours} hrs</td>
                <td>
                  <DescriptionText title={log.description}>
                    {log.description}
                  </DescriptionText>
                </td>
                <td className="Action">
                  <LogAction
                    onEdit={() => onEdit(log)}
                    onDelete={() => onDelete(log.Id)}
                  />
                </td>
              </tr>
            ))
          )}

          <tr>
            <td
              colSpan={5}
              style={{
                textAlign: 'right',
                paddingTop: '10px',
                paddingRight: '10px',
              }}
            >
              <AddButton
                onClick={() => {
                  if (selectedDate === dateISO && addButtonClicked) {
                    onCancel();
                    return;
                  }

                  setIsEditingMode(false);
                  setSelectedDate(dateISO);
                  setAddButtonClicked(true);

                  setLogEntries([
                    {
                      projectId: '',
                      contractId: '',
                      loghour: '',
                      description: '',
                    },
                  ]);
                }}
              >
                +
              </AddButton>
            </td>
          </tr>

          {selectedDate === dateISO && addButtonClicked && (
            <tr>
              <td colSpan={5} className="no-padding">
                {addButtonEntries}
              </td>
            </tr>
          )}
        </tbody>
      </StyledTable>
    </DailyLogContainer>
  );
};
export default DailyLogTable;
