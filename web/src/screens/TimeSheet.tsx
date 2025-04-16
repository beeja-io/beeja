import React, { useState } from "react";
import {
  TimesheetContainer,
  Filters,
  SearchInput,
  Dropdown,
  SearchBox,
  TimesheetRow,
  NavigationButtons,
  RotateArrow, FormContainer
} from "../styles/TimeSheetStyles.style";
import { ArrowDownSVG, EditWhitePenSVG } from "../svgs/CommonSvgs.svs";
import { useNavigate } from "react-router-dom";
import { SearchSVG } from "../svgs/NavBarSvgs.svg";

const Timesheet: React.FC = () => {
  const [expandedWeek, setExpandedWeek] = useState<string | null>(null);
  const [selectedDate, setSelectedDate] = useState<string | null>(null);
  const [selectedView, setSelectedView] = useState<'day' | 'week'>('week');
  const [isFormVisible, setIsFormVisible] = useState(false);
  const [selectedProject, setSelectedProject] = useState<string>("All");
  const [selectedContract, setSelectedContract] = useState<string>("All");
  const [logEntries, setLogEntries] = useState<{ project: string; contract: string; hours: number | ""; description: string }[]>([]);

  const weekTimesheetData = [
    { week: "Week-40", startDate: "Feb 3", endDate: "Feb 9", hours: 25 },
    { week: "Week-41", startDate: "Oct 7", endDate: "Oct 13", hours: 0 },
    { week: "Week-42", startDate: "Oct 14", endDate: "Oct 20", hours: 5 },
    { week: "Week-43", startDate: "Oct 21", endDate: "Oct 27", hours: 10 },
    { week: "Week-44", startDate: "Oct 28", endDate: "Nov 3", hours: 0 },
  ];

  const dailyTimesheetData: { [key: string]: any[] } = {
    "Week-40": [
      { date: "Feb 1", hours: 7 },
      { date: "Feb 2", hours: 7 },
      { date: "Feb 3", hours: 3 },
      { date: "Feb 4", hours: 7 },
      { date: "Feb 5", hours: 1 },
      { date: "Feb 6", hours: 7 },
      { date: "Feb 7", hours: 7 },
      { date: "Feb 8", hours: 7 },
      { date: "Feb 9", hours: 7 },
      { date: "Feb 10", hours: 7 },

    ],
    "Week-41": [{ date: "Oct 14", hours: 0 }],
    "Week-42": [{ date: "Oct 19", hours: 5 }],
    "Week-43": [{ date: "Oct 21", hours: 10 }],
    "Week-44": [{ date: "Oct 28", hours: 0 }],
  };

  const getDayColor = (dateString: string) => {
    const currentYear = new Date().getFullYear();
    const date = new Date(`${dateString}, ${currentYear}`);
    const day = date.getDay();
    const today = new Date();

    if (date.toDateString() === today.toDateString()) {
      return { backgroundColor: "#E6F4EA", color: "black" };
    }
    if (day === 6 || day === 0) {
      return {
        color: "#EA4335", background: "#FFF4F499"
      };
    }
    return {};
  };

  const toggleWeek = (week: string) => {
    setExpandedWeek(expandedWeek === week ? null : week);
  };

  const handleProjectFilter = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedProject(event.target.value);
    setSelectedView("day");
  };

  const handleContractFilter = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedContract(event.target.value);
    setSelectedView("day");
  };

  const handleDateClick = (date: string) => {
    setSelectedDate(date);
    setIsFormVisible(true);
    setLogEntries([{ project: "", contract: "", hours: "", description: "" }]);
  };

  const addNewEntry = () => {
    setLogEntries([...logEntries, { project: "", contract: "", hours: "", description: "" }]);
  };

  const handleInputChange = (index: number, field: string, value: string | number) => {
    const updatedEntries = [...logEntries];
    (updatedEntries[index] as any)[field] = value;
    setLogEntries(updatedEntries);
  };

  const handleSave = () => {
    console.log("Saved:", { date: selectedDate, logEntries });
    setIsFormVisible(false);
  };

  const TimeLogForm: React.FC<{ date: string; onClose: () => void }> = () => {
    // const [project, setProject] = useState("");
    // const [contract, setContract] = useState("");
    // const [hours, setHours] = useState<number | "">("");
    // const [description, setDescription] = useState("");

    // const handleSave = () => {
    //   console.log("Saved:", { date, project, contract, hours, description });
    //   onClose();
    // };

    return (
      <FormContainer >
        <div className="Form_Headings">
          <span>Project</span>
          <span>Contract</span>
          <span>Log Hours</span>
          <span>Description</span>
          <span>Action</span>
        </div>

        {logEntries.map((entry, index) => (
          <div key={index} className="Form_Row">
            <input
              type="text"
              placeholder="Project"
              value={entry.project}
              onChange={(e) => handleInputChange(index, "project", e.target.value)}
            />
            <input
              type="text"
              placeholder="Contract"
              value={entry.contract}
              onChange={(e) => handleInputChange(index, "contract", e.target.value)}
            />
            <input
              type="number"
              placeholder="Hours"
              value={entry.hours}
              onChange={(e) => handleInputChange(index, "hours", Number(e.target.value))}
            />
            <textarea
              placeholder="Description"
              value={entry.description}
              onChange={(e) => handleInputChange(index, "description", e.target.value)}
            />
            <div><EditWhitePenSVG /></div>
          </div>
        ))}
        {/* <button onClick={handleSave}>Save</button>
        <button onClick={() => setIsFormVisible(false)}>Cancel</button> */}
        {/* <button onClick={addNewEntry}>
          ++ Add Entry
        </button> */}
      </FormContainer>

    );
  };

  const navigate = useNavigate();
  const goToPreviousPage = () => {
    navigate(-1);
  };

  return (
    <TimesheetContainer>
      <div className="heading">
        <span onClick={goToPreviousPage}>
          <ArrowDownSVG />
        </span>
        Time Sheet
      </div>
      <div className="TimeSheet_Container">
        <div className="TimeSheet_Heading">
          <p className="TimeSheetTitle underline">List of Time Sheets</p>
        </div>
        <div className="Filter_Container">
          <Filters>
            <SearchBox>
              <SearchInput type="text" placeholder="Search for any thing" />
              <span>
                <SearchSVG />
              </span>
            </SearchBox>
            <Dropdown onChange={handleProjectFilter} value={selectedProject}>
              <option value="All">All Projects</option>
              <option value="Project A">Project A</option>
              <option value="Project B">Project B</option>
            </Dropdown>
            <Dropdown onChange={handleContractFilter} value={selectedContract}>
              <option value="All">All Contracts</option>
              <option value="Contract1">Contract1</option>
              <option value="Contract2">Contract2</option>
            </Dropdown>
          </Filters>
          <div className="Export">
            Export
          </div>
        </div>
        <div>
          {weekTimesheetData.map((entry, index) => (
            <div key={index}>
              <TimesheetRow onClick={() => toggleWeek(entry.week)}>
                <span>
                  {entry.week} [{entry.startDate} - {entry.endDate}, 24]
                </span>
                <span>Weekly Logged Hours: {entry.hours} hrs
                  <RotateArrow isExpanded={expandedWeek === entry.week}>
                    <ArrowDownSVG />
                  </RotateArrow>
                </span>

              </TimesheetRow>
              {expandedWeek === entry.week && (
                <div>
                  {dailyTimesheetData[entry.week].map((day, i) => (
                    <div key={i}>
                      <TimesheetRow onClick={() => handleDateClick(day.date)} style={getDayColor(day.date)}>
                        <span>{day.date}</span>
                        <span>Logged Hours: {day.hours} hrs</span>
                      </TimesheetRow>
                      {isFormVisible && selectedDate === day.date && (
                        <TimeLogForm date={selectedDate || ""} onClose={() => setIsFormVisible(false)} />
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>
          ))}
        </div>

        <NavigationButtons>
          <button>Previous Week</button>
          <button>Next Week</button>
        </NavigationButtons>
      </div>
    </TimesheetContainer>
  );
};

export default Timesheet;


