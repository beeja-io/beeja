import React, { useState, useEffect } from 'react';
import { postRole, putRole } from '../../service/axiosInstance';
import {
  Container,
  Input,
  Label,
  Row,
} from '../../styles/OrganizationSettingsStyles.style';
import {
  BorderDivLine,
  TabContentMainContainer,
  TabContentMainContainerHeading,
} from '../../styles/MyProfile.style';
import { Checkbox } from '../../styles/LoanApplicationStyles.style';
import { Button } from '../../styles/CommonStyles.style';
import { IRole } from '../../entities/RoleEntity';
import roleData from '../../constants/RoleData';
import { toast } from 'sonner';
import { InfoCircleSVG } from '../../svgs/NavBarSvgs.svg';
import {
  CLIENT_MODULE,
  CONTRACT_MODULE,
  ORGANIZATION_MODULE,
  PERFORMANCE_MODULE,
  PERMISSION_MODULE,
} from '../../constants/PermissionConstants';
import { useTranslation } from 'react-i18next';

interface Permission {
  value: string;
  label: string;
}

interface Props {
  initialPermissions: string[];
  editingRole: IRole | null;
  onClose: () => void;
  onSubmit: () => void;
}

const AddRoleComponent: React.FC<Props> = ({
  initialPermissions,
  editingRole,
  onClose,
  onSubmit,
}) => {
  const [roleName, setRoleName] = useState('');
  const [description, setDescription] = useState('');
  const [permissions, setPermissions] = useState<string[]>(initialPermissions);
  const [isLoading, setIsLoading] = useState(false);
  const { t } = useTranslation();
  const READ_DOCUMENT = 'RDCMT';
  const READ_CLIENT_DOCUMENT = 'RCLDOC';
  const READ_PROJECT_DOCUMENT = 'RPRDOC';
  const READ_CONTRACT_DOCUMENT = 'RCONDC';
  const MODULE_DOC_PERMISSIONS = [
    READ_CLIENT_DOCUMENT,
    READ_PROJECT_DOCUMENT,
    READ_CONTRACT_DOCUMENT,
  ];

  useEffect(() => {
    if (editingRole) {
      setRoleName(editingRole.name);
      setDescription(editingRole.description || '');

      let loadedPermissions = editingRole.permissions;
      const hasGranularDocPermission = MODULE_DOC_PERMISSIONS.some((p) =>
        loadedPermissions.includes(p)
      );
      if (loadedPermissions.includes(READ_DOCUMENT)) {
        if (!hasGranularDocPermission) {
          MODULE_DOC_PERMISSIONS.forEach((p) => {
            if (!loadedPermissions.includes(p)) loadedPermissions.push(p);
          });
        }
        loadedPermissions = loadedPermissions.filter(
          (p) => p !== READ_DOCUMENT
        );
      }
      setPermissions(loadedPermissions);
    } else {
      setRoleName('');
      setDescription('');
      setPermissions((prev) => [
        ...prev,
        ...roleData.flatMap((role) =>
          role.subsections.flatMap((sub) =>
            sub.permissions
              .filter((prem) => prem.default)
              .map((prem) => prem.value)
          )
        ),
      ]);
    }
  }, [editingRole]);
  useEffect(() => {
    const dependencyMap = [
      {
        actions: [
          PERMISSION_MODULE.CREATE_ROLES_OF_ORGANIZATIONS,
          PERMISSION_MODULE.UPDATE_ROLES_OF_ORGANIZATIONS,
          PERMISSION_MODULE.DELETE_ROLES_OF_ORGANIZATIONS,
        ],
        required: ORGANIZATION_MODULE.READ_ORGANIZATIONS,
      },
      {
        actions: [CLIENT_MODULE.GENERATE_INVOICE, CLIENT_MODULE.DELETE_INVOICE],
        required: CONTRACT_MODULE.READ_CONTRACT,
      },
      {
        actions: [
          PERFORMANCE_MODULE.CREATE_REVIEW_CYCLE,
          PERFORMANCE_MODULE.UPDATE_REVIEW_CYCLE,
          PERFORMANCE_MODULE.DELETE_REVIEW_CYCLE,

        ],
        required: PERFORMANCE_MODULE.READ_REVIEW_CYCLE,
      },
      {
        actions: [
          PERFORMANCE_MODULE.READ_PROVIDER,
          PERFORMANCE_MODULE.UPDATE_PROVIDER,
          PERFORMANCE_MODULE.ASSIGN_PROVIDER,
          PERFORMANCE_MODULE.ASSIGN_RECEIVER,
          PERFORMANCE_MODULE.UPDATE_RECEIVER,
        ],
        required: PERFORMANCE_MODULE.READ_RECEIVER,
      }
    ];

    dependencyMap.forEach(({ actions, required }) => {
      if (
        actions.some((a) => permissions.includes(a)) &&
        !permissions.includes(required)
      ) {
        setPermissions((prev) => [...prev, required]);
      }
    });
  }, [permissions]);

  const handleCheckboxChange = (permission: string) => {
    if (permissions.includes(permission)) {
      setPermissions(permissions.filter((p) => p !== permission));
    } else {
      setPermissions([...permissions, permission]);
    }
  };

  const handleFullAccessToggle = (
    isChecked: boolean,
    subsectionPermissions: Permission[]
  ) => {
    if (isChecked) {
      const newPermissions = permissions.concat(
        subsectionPermissions
          .map((perm) => perm.value)
          .filter((value) => value !== '')
      );
      setPermissions(newPermissions);
    } else {
      const newPermissions = permissions.filter(
        (perm) =>
          !subsectionPermissions.map((perm) => perm.value).includes(perm)
      );
      setPermissions(newPermissions);
    }
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    let permissionsToSend = [...permissions];
    const requiresGenericReadDocument = MODULE_DOC_PERMISSIONS.some((p) =>
      permissions.includes(p)
    );
    if (
      requiresGenericReadDocument &&
      !permissionsToSend.includes(READ_DOCUMENT)
    ) {
      permissionsToSend.push(READ_DOCUMENT);
    }

    const newRole = {
      name: editingRole && editingRole.name === roleName ? undefined : roleName,
      description,
      permissions: permissionsToSend,
    };
    toast.promise(
      async () => {
        setIsLoading(true);
        if (editingRole) {
          await putRole(editingRole.id, newRole);
        } else {
          await postRole(newRole);
        }
        onSubmit();
        onClose();
        setIsLoading(false);
      },
      {
        loading: t('SUBMITTING'),
        closeButton: true,
        success: () => {
          setIsLoading(false);
          return `Role ${editingRole ? 'edited' : 'saved'} successfully`;
        },
        error: (error) => {
          setIsLoading(false);
          if (error.response.status === 403) {
            setIsLoading(false);
            return t('NO_PERMISSION_TO_PERFORM_ACTION');
          }
          return t('FAILED_TO_SAVE_CHANGES');
        },
      }
    );
  };

  return (
    <Container>
      <span className="roleInfo">
        <InfoCircleSVG />
        <span>{t('ROLE_MAIN_INFO')}</span>
      </span>
      <form onSubmit={handleSubmit}>
        <TabContentMainContainer>
          <TabContentMainContainerHeading>
            <h4>{editingRole ? 'Edit Role' : 'Add Role'}</h4>
          </TabContentMainContainerHeading>
          <BorderDivLine width="100%" />
          <br />
          <Row>
            <Label>{t('ROLE_NAME')}</Label>
            <Input
              name="name"
              type="text"
              autoComplete="off"
              required
              value={roleName}
              placeholder={t('ENTER_ROLE_NAME')}
              onChange={(e) => setRoleName(e.target.value)}
            />
          </Row>
          <Row>
            <Label>{t('DESCRIPTION')}</Label>
            <Input
              name="description"
              type="text"
              autoComplete="off"
              placeholder={t('ENTER_THE_DESCRIPTION')}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              maxLength={100}
            />
          </Row>
        </TabContentMainContainer>

        {roleData.map((section, index) => (
          <TabContentMainContainer key={index} className="rolesPermissions">
            <TabContentMainContainerHeading>
              <h4>{section.heading}</h4>
            </TabContentMainContainerHeading>
            <BorderDivLine width="100%" />
            <section className="scrollableTable">
              <table>
                <thead>
                  <tr key={index}>
                    <th></th>
                    {section.labels.map((label, labelIndex) => (
                      <td className="checkBoxes" key={labelIndex}>
                        {label}
                      </td>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {section.subsections.map((subsection, subIndex) => (
                    <tr key={subIndex}>
                      <th>{subsection.moduleName}</th>
                      {section.heading !== 'Employees' && (
                        <td key={subIndex} className="checkBoxes">
                          <Checkbox
                            type="checkbox"
                            checked={
                              permissions.length > 0 &&
                              subsection.permissions
                                .filter((perm) => perm.value !== '')
                                .every((perm) =>
                                  permissions.includes(perm.value)
                                )
                            }
                            onChange={(e) =>
                              handleFullAccessToggle(
                                e.target.checked,
                                subsection.permissions
                              )
                            }
                          />
                        </td>
                      )}
                      {subsection.permissions.map((permission, permIndex) => (
                        <>
                          <td key={permIndex} className="checkBoxes">
                            <Checkbox
                              type="checkbox"
                              value={permission.value}
                              checked={Boolean(
                                permissions.includes(permission.value) ||
                                  permission.default
                              )}
                              onChange={() =>
                                handleCheckboxChange(permission.value)
                              }
                              disabled={
                                permission.value === '' ||
                                Boolean(permission.default)
                              }
                            />
                          </td>
                        </>
                      ))}
                    </tr>
                  ))}
                </tbody>
              </table>
            </section>
          </TabContentMainContainer>
        ))}

        <Button
          type="submit"
          className={`submit ${isLoading ? t('LOADING') : ''}`}
        >
          {isLoading ? '' : t('SUBMIT')}
        </Button>
      </form>
    </Container>
  );
};

export default AddRoleComponent;
