import React from "react";
import {
  Container,
  Title,
  Filters,
  SearchInput,
  Select,
  Table,
  Th,
  Td,
  EmployeeCell,
  Avatar,
  EmpName,
  EmpEmail,
  StatusBadge,
  TitleSection,
} from "../styles/MyTeamOverview.style";
import { ArrowDownSVG } from "../svgs/CommonSvgs.svs";

interface Employee {
  id: number;
  name: string;
  email: string;
  jobTitle: string;
  department: string;
  feedbackStatus: "Completed" | "In Progress";
  rating?: number;
  avatarUrl: string;
}

const MyTeamOverview: React.FC = () => {
  const employees: Employee[] = [
    {
      id: 1,
      name: "Bhagath",
      email: "bhagath@techactore.com",
      jobTitle: "UX Designer",
      department: "Design",
      feedbackStatus: "In Progress",
      rating: undefined,
      avatarUrl: "https://i.pravatar.cc/40?img=1",
    },
    {
      id: 2,
      name: "Shravya",
      email: "shravya@techactore.com",
      jobTitle: "UI/UX Designer",
      department: "Design",
      feedbackStatus: "Completed",
      rating: 4.5,
      avatarUrl: "https://i.pravatar.cc/40?img=2",
    },
    {
      id: 3,
      name: "Neha",
      email: "neha@techactore.com",
      jobTitle: "DevOps Engineer",
      department: "DevOps",
      feedbackStatus: "Completed",
      rating: 4.5,
      avatarUrl: "https://i.pravatar.cc/40?img=3",
    },
    {
      id: 4,
      name: "Madhan",
      email: "madhan@techactore.com",
      jobTitle: "DevOps Engineer",
      department: "Engineering",
      feedbackStatus: "In Progress",
      rating: 4.0,
      avatarUrl: "https://i.pravatar.cc/40?img=4",
    },
  ];

  return (
    <div>
<TitleSection>
        <span className="arrow">
          <ArrowDownSVG />
        </span>
        <Title>Evaluation Overview</Title>
      </TitleSection>
        <Container>

        <Filters>
            <SearchInput type="text" placeholder="Search" />
            <Select>
            <option>Status</option>
            </Select>
            <Select>
            <option>Department</option>
            </Select>
        </Filters>

        <Table>
            <thead>
            <tr>
                <Th>Employee Name</Th>
                <Th>Job Title</Th>
                <Th>Department</Th>
                <Th>Feedback Status</Th>
                <Th>Rating (Out of 5)</Th>
            </tr>
            </thead>
            <tbody>
            {employees.map((emp) => (
                <tr key={emp.id}>
                <Td>
                    <EmployeeCell>
                    <Avatar src={emp.avatarUrl} alt={emp.name} />
                    <div>
                        <EmpName>{emp.name}</EmpName>
                        <EmpEmail>{emp.email}</EmpEmail>
                    </div>
                    </EmployeeCell>
                </Td>
                <Td>{emp.jobTitle}</Td>
                <Td>{emp.department}</Td>
                <Td>
                    <StatusBadge status={emp.feedbackStatus}>
                    {emp.feedbackStatus}
                    </StatusBadge>
                </Td>
                <Td>{emp.rating ?? "-"}</Td>
                </tr>
            ))}
            </tbody>
        </Table>
        </Container>
    </div>
  );
};

export default MyTeamOverview;
