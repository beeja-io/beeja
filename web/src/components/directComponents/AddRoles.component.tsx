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
  ORGANIZATION_MODULE,
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
  const permissionDependencies: Record<string, string[]> = {
    CIN: ['GCON'],
    GIN: ['GCON'],
    DIN: ['GCON'],
  };
  const { t } = useTranslation();

  const isPartOfAnotherFullAccess = (
    dep: string,
    permissionsList: string[]
  ) => {
    const fullContractPermissions = ['CCON', 'UCON', 'DCON', 'GCON'];
    if (
      dep === 'GCON' &&
      fullContractPermissions.every((p) => permissionsList.includes(p))
    ) {
      return true;
    }
    return false;
  };

  useEffect(() => {
    if (editingRole) {
      setRoleName(editingRole.name);
      setDescription(editingRole.description || '');
      setPermissions(editingRole.permissions);
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
    if (
      (permissions.includes(PERMISSION_MODULE.CREATE_ROLES_OF_ORGANIZATIONS) ||
        permissions.includes(PERMISSION_MODULE.UPDATE_ROLES_OF_ORGANIZATIONS) ||
        permissions.includes(
          PERMISSION_MODULE.DELETE_ROLES_OF_ORGANIZATIONS
        )) &&
      !permissions.includes(ORGANIZATION_MODULE.READ_ORGANIZATIONS)
    ) {
      setPermissions([...permissions, ORGANIZATION_MODULE.READ_ORGANIZATIONS]);
    }
  }, [permissions]);

  const handleCheckboxChange = (permission: string) => {
    let updatedPermissions = [...permissions];

    if (updatedPermissions.includes(permission)) {
      updatedPermissions = updatedPermissions.filter((p) => p !== permission);
      if (permissionDependencies[permission]) {
        permissionDependencies[permission].forEach((dep) => {
          const stillRequired = Object.entries(permissionDependencies).some(
            ([key, deps]) =>
              key !== permission &&
              updatedPermissions.includes(key) &&
              deps.includes(dep)
          );
          if (
            !stillRequired &&
            !isPartOfAnotherFullAccess(dep, updatedPermissions)
          ) {
            updatedPermissions = updatedPermissions.filter((p) => p !== dep);
          }
        });
      }
    } else {
      updatedPermissions.push(permission);
      if (permissionDependencies[permission]) {
        updatedPermissions = Array.from(
          new Set([
            ...updatedPermissions,
            ...permissionDependencies[permission],
          ])
        );
      }
    }

    setPermissions(updatedPermissions);
  };

  const handleFullAccessToggle = (
    isChecked: boolean,
    subsectionPermissions: Permission[]
  ) => {
    let updatedPermissions = [...permissions];

    const permissionValues = subsectionPermissions
      .map((perm) => perm.value)
      .filter((val) => val !== '');

    if (isChecked) {
      updatedPermissions = Array.from(
        new Set([...updatedPermissions, ...permissionValues])
      );
      permissionValues.forEach((perm) => {
        if (permissionDependencies[perm]) {
          updatedPermissions = Array.from(
            new Set([...updatedPermissions, ...permissionDependencies[perm]])
          );
        }
      });
    } else {
      updatedPermissions = updatedPermissions.filter(
        (perm) => !permissionValues.includes(perm)
      );
      permissionValues.forEach((perm) => {
        if (permissionDependencies[perm]) {
          permissionDependencies[perm].forEach((dep) => {
            const stillRequired = Object.entries(permissionDependencies).some(
              ([key, deps]) =>
                key !== perm &&
                updatedPermissions.includes(key) &&
                deps.includes(dep)
            );
            if (
              !stillRequired &&
              !isPartOfAnotherFullAccess(dep, updatedPermissions)
            ) {
              updatedPermissions = updatedPermissions.filter((p) => p !== dep);
            }
          });
        }
      });
    }

    setPermissions(updatedPermissions);
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const newRole = {
      name: editingRole && editingRole.name === roleName ? undefined : roleName,
      description,
      permissions,
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
