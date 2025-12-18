import React, { useEffect, useState, useRef, useLayoutEffect } from 'react';
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
import { useTranslation } from 'react-i18next';
import ToastMessage from '../components/reusableComponents/ToastMessage.component';

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
  const { t } = useTranslation();

  const [errorToast, setErrorToast] = useState<{
    heading: string;
    body: string;
  } | null>(null);

  const textareaRefs = useRef<(HTMLTextAreaElement | null)[]>([]);

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

  useLayoutEffect(() => {
    textareaRefs.current.forEach((textarea) => {
      if (textarea) {
        textarea.style.height = 'auto';
        textarea.style.height = `${textarea.scrollHeight}px`;
      }
    });
  }, [logEntries]);

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
      setErrorToast({
        heading: 'Error',
        body: t('Failed_to_load_contracts'),
      });
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
                <option value="">{t('Select_Project')}</option>
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
                ref={(el) => (textareaRefs.current[index] = el)}
                placeholder="Description"
                value={entry.description}
                title={entry.description}
                rows={1}
                onChange={(e) => {
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
            <th className="projectWidth">{t('Project')}</th>
            <th className="contractWidth">{t('Contract')}</th>
            <th className="logHoursWidth">{t('Log_Hours')}</th>
            <th className="descriptionWidth">{t('Description')}</th>
            <th className="actionWidth">{t('Action')}</th>
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
            <td colSpan={5} className="addButton">
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
      {errorToast && (
        <ToastMessage
          messageType="error"
          messageHeading={errorToast.heading}
          messageBody={errorToast.body}
          handleClose={() => setErrorToast(null)}
        />
      )}
    </DailyLogContainer>
  );
};
export default DailyLogTable;
