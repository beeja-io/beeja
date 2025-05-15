import {
  BorderDivLine,
  DropDownContainer,
  MyProfileButton,
  MyProfileQuickDetails,
  MyProfileQuickDetailsMainContainer,
  QuickInfoActionButtions,
  QuickInfoContactContainer,
  QuickInfoDepartmentContainer,
  UserName,
  RolesDiv,
  ImagePreviewContainer,
  ImagePreview,
  ZoomSliderContainer,
  ZoomSlider,
  SliderIconContainer,
} from '../../styles/MyProfile.style';
import { EmployeeEntity } from '../../entities/EmployeeEntity';
import {
  ArrowDownSVG,
  BlueDotDividerSVG,
  GlobeIconSVG,
  MessageIconSVG,
  PhoneIconSVG,
} from '../../svgs/CommonSvgs.svs';
import React, { useEffect, useState, useRef, useContext } from 'react';
import html2canvas from 'html2canvas';
import { Button, StyledLink } from '../../styles/CommonStyles.style';
import { useUser } from '../../context/UserContext';
import { ApplicationContext } from '../../context/ApplicationContext';
import {
  updateEmployeeStatusByEmployeeId,
  getAllRolesInOrganization,
  downloadEmployeeFile,
  uploadProfilePicture,
  updateEmployeeRole,
} from '../../service/axiosInstance';
import CenterModal from './CenterModal.component';
import {
  EMPLOYEE_MODULE,
  PROFILE_PIC_MODULE,
} from '../../constants/PermissionConstants';
import { IRole } from '../../entities/RoleEntity';
import { Monogram } from '../../styles/EmployeeListStyles.style';
import { TIME_ZONES } from '../../utils/themeUtils';
import { hasPermission } from '../../utils/permissionCheck';
import { toast } from 'sonner';
import { useProfileImage } from '../../context/ProfileImageContext';
import { LargeSVG, SmallSVG } from '../../svgs/profilePictureSvgs.svg';
import { useTranslation } from 'react-i18next';
import useKeyPress from '../../service/keyboardShortcuts/onKeyPress';

type QuickProfileProps = {
  employee: EmployeeEntity | undefined;
  fetchEmployeeAgain: () => void;
  isLoadingResponse?: boolean;
};
const MyProfileQuickDetailsComponent = ({
  employee,
  fetchEmployeeAgain,
  isLoadingResponse,
}: QuickProfileProps) => {
  const [roles, setRoles] = useState<string[]>([]);
  const { t } = useTranslation();


  useEffect(() => {
    if (employee?.account.roles) {
      setRoles(employee.account.roles.map((role) => role.name));
    }
  }, [employee]);

  const [allRolesInOrg, setAllRolesInOrg] = useState<IRole[]>();
  const [isLoadingResponseINTERNAL, setIsLoadingResponse] = useState(false);
  const [roleChangeType, setRoleChangeType] = useState<'add' | 'delete' | null>(
    null
  );
  const [isMonogramModalOpen, setIsMonogramModalOpen] = useState(false);
  const [isUploadOpen, setIsUploadOpen] = useState<boolean>(false);
  const [selectedImage, setSelectedImage] = useState<File | null>(null);
  const [scale, setScale] = useState(1.5);
  const [offsetX, setOffsetX] = useState(0);
  const [offsetY, setOffsetY] = useState(0);
  const imageRef = useRef<HTMLImageElement | null>(null);
  const [isDragging, setIsDragging] = useState(false);
  const [startDrag, setStartDrag] = useState<{ x: number; y: number } | null>(
    null
  );
  const [croppedImage] = useState<string | null>(null);
  const { employeeList, updateEmployeeList } = useContext(ApplicationContext);
  const { profileImageUrl, setProfileImageUrl } = useProfileImage();

  const fetchRoles = async () => {
    const response = await getAllRolesInOrganization();
    setAllRolesInOrg(response.data);
  };

  useEffect(() => {
    fetchRoles();
  }, []);

  const { user } = useUser();
  const handleStatusChange = async () => {
    setIsLoadingResponse(true);
    try {
      employee &&
        (await updateEmployeeStatusByEmployeeId(employee.account.employeeId));
      fetchEmployeeAgain();
      handleIsActiveModalOpen();
    } catch {
      setIsLoadingResponse(false);
    } finally {
      setIsLoadingResponse(false);
    }
  };
  const handleUpdateRoles = async () => {
    setIsLoadingResponse(true);
    try {
      employee &&
        (await updateEmployeeRole(employee.account.employeeId, roles));

      setAddRoleButtonText('Add Role');
      setDeleteRoleButtonText('Delete Role');
      handleIsUpdateButtonShow();
      fetchEmployeeAgain();
      setIsUpdateButtonShow(false);
      setIsLoadingResponse(false);
      if (roleChangeType === 'add') {
        toast.success('Role Added successfully');
      } else if (roleChangeType === 'delete') {
        toast.success('Role Deleted successfully');
      }
      fetchRoles();
    } catch (error) {
      throw new Error('Error Updating roles: ' + error);
    } finally {
      setIsLoadingResponse(false);
    }
  };

  const addRole = (newRole: string) => {
    handleIsUpdateButtonShow();
    setRoleChangeType('add');
    setRoles((prevRoles) => {
      if (!prevRoles.includes(newRole)) {
        return [...prevRoles, newRole];
      }
      return prevRoles;
    });
  };

  const deleteRole = (roleToDelete: string) => {
    handleIsUpdateButtonShow();
    setRoleChangeType('delete');
    setRoles((prevRoles) => {
      return prevRoles.filter((role) => role !== roleToDelete);
    });
  };

  const [isAddRoleDropDownOpen, setIsAddRoleDropDownOpen] = useState(false);
  const handleIsAddRoleDropDownOpen = () => {
    setIsAddRoleDropDownOpen(!isAddRoleDropDownOpen);
    setIsDeleteRoleDropdown(false);
  };

  const [isDeleteRoleDropDown, setIsDeleteRoleDropdown] = useState(false);
  const handleIsDeleteRoleDropDown = () => {
    setIsDeleteRoleDropdown(!isDeleteRoleDropDown);
    setIsAddRoleDropDownOpen(false);
  };
  const isAddRoleDropDownCloseRef = useRef<HTMLDivElement>(null);
  const isDeleteRoleDropDownCloseRef = useRef<HTMLDivElement>(null);
  useEffect(() => {
    const handleClickOutSide = (event: MouseEvent) => {
      if (
        isAddRoleDropDownCloseRef.current &&
        !isAddRoleDropDownCloseRef.current?.contains(event.target as Node)
      ) {
        setIsAddRoleDropDownOpen(false);
      }
      if (
        isDeleteRoleDropDownCloseRef.current &&
        !isDeleteRoleDropDownCloseRef.current?.contains(event.target as Node)
      ) {
        setIsDeleteRoleDropdown(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutSide);
    return () => {
      document.removeEventListener('mousedown', handleClickOutSide);
    };
  }, []);

  const [addRoleButtonText, setAddRoleButtonText] = useState('Add Role');
  const handleAddRoleButtonText = (role: string) => {
    setAddRoleButtonText(role);
  };

  const [deleteRoleButtonText, setDeleteRoleButtonText] =
    useState('Delete Role');
  const HandleDeleteRoleButtonText = (role: string) => {
    setDeleteRoleButtonText(role);
  };

  const [isUpdateButtonShow, setIsUpdateButtonShow] = useState(false);
  const handleIsUpdateButtonShow = () => {
    setIsUpdateButtonShow(true);
  };

  const [isActiveModalOpen, setIsActiveModalOpen] = useState(false);
  const handleIsActiveModalOpen = () => {
    setIsActiveModalOpen(!isActiveModalOpen);
  };
  const hasContactPhone =
    employee?.employee.contact && employee?.employee?.contact?.phone;

  const canEditOwnProfilePic = user?.roles.some((role) =>
    role.permissions.includes(PROFILE_PIC_MODULE.UPDATE_PROFILE_PHOTO_SELF)
  );

  const canEditOtherProfilePic = user?.roles.some((role) =>
    role.permissions.includes(PROFILE_PIC_MODULE.UPDATE_PROFILE_PHOTO_ALL)
  );

  const showHoverEffect =
    employee && user?.employeeId === employee.employee.employeeId;

  const handleMonogramUpload = async () => {
    await setIsUploadOpen(true);
    await setIsMonogramModalOpen(false);
  };
  const handleMonogramClick = () => {
    if (
      (employee &&
        user?.employeeId == employee.employee.employeeId &&
        canEditOwnProfilePic) ||
      canEditOtherProfilePic
    ) {
      setIsMonogramModalOpen(true);
    }
  };

  const handleModalClose = () => {
    setIsMonogramModalOpen(false);
    setIsUploadOpen(false);
    setSelectedImage(null);
  };

  const handleModalSave = () => {
    setIsMonogramModalOpen(false);
  };

  const handleSaveAndUpload = async () => {
    const toastId = toast.loading(t('UPDATING_PROFILE_HAS_PICTURE'));
    try {
      await uploadCroppedImage();
      toast.success(t('PROFILE_HAS_BEEN_SUCCESSFULLY_ADDED'), { id: toastId });
      handleModalClose();
    } catch (error) {
      toast.error(t('FAILED_TO_UPDATE_PROFILE_PICTURE'), { id: toastId });
    }
  };

  const handleMouseDown = (event: React.MouseEvent) => {
    setIsDragging(true);
    setStartDrag({ x: event.clientX, y: event.clientY });
  };

  const handleMouseMove = (event: React.MouseEvent) => {
    event.preventDefault();
    if (!isDragging || !startDrag) return;
    const dx = event.clientX - startDrag.x;
    const dy = event.clientY - startDrag.y;
    setOffsetX((prev) => prev + dx);
    setOffsetY((prev) => prev + dy);
    setStartDrag({ x: event.clientX, y: event.clientY });
  };

  const handleMouseUp = () => {
    setIsDragging(false);
    setStartDrag(null);
  };

  const handleFileUpload = () => {
    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.accept = 'image/png, image/jpeg';

    fileInput.onchange = (event) => {
      const target = event.target as HTMLInputElement | null;
      if (target && target.files) {
        const file = target.files[0];
        const validImageTypes = ['image/png', 'image/jpeg'];
        if (!validImageTypes.includes(file.type)) {
          alert('Please upload a .png or .jpg file.');
          return;
        }
        setSelectedImage(file);
      }
    };
    fileInput.click();
  };

  const cropImage = (): Promise<File> => {
    return new Promise((resolve, reject) => {
      const previewDiv = document.getElementById('previewDiv');
      if (!previewDiv) return reject('Preview div not found.');
      html2canvas(previewDiv, {
        backgroundColor: '#333333',
        useCORS: true,
      })
        .then((canvas) => {
          canvas.toBlob((blob) => {
            if (blob) {
              const file = new File([blob], 'cropped-preview.png', {
                type: 'image/png',
              });
              resolve(file);
            } else {
              reject('Blob creation failed');
            }
          }, 'image/png');
        })
        .catch((error) => {
          reject(`Failed to capture image: ${error.message}`);
        });
    });
  };

  const uploadCroppedImage = async () => {
    try {
      const croppedImageFile = await cropImage();
      const entityId = employee?.employee.employeeId;

      if (!entityId) {
        return;
      }
      const uploadResponse = await uploadProfilePicture(
        croppedImageFile,
        entityId
      );

      const newFileId = uploadResponse.data.profilePictureId;
      if (!newFileId) {
        return;
      }
      const updatedEmployeeList = employeeList?.map((emp) =>
        emp.employee.id === employee?.employee.id
          ? {
              ...emp,
              employee: { ...emp.employee, profilePictureId: newFileId },
            }
          : emp
      );
      if (updatedEmployeeList) updateEmployeeList(updatedEmployeeList);

      const response = await downloadEmployeeFile(newFileId);
      if (!response.data || response.data.size === 0) {
        throw new Error('Received empty or invalid blob data');
      }
      const reader = new FileReader();
      reader.onloadend = () => {
        const imageUrl = reader.result as string;
        const isOwnProfile = user?.employeeId === employee?.employee.employeeId;
        setProfileImageUrl(imageUrl, isOwnProfile);
      };
      reader.onerror = () => {
        throw new Error('Error converting blob to base64');
      };
      reader.readAsDataURL(response.data);
    } catch (error) {
      throw new Error('Error uploading or fetching cropped image: ' + error);
    }
  };
  /* eslint-disable react-hooks/exhaustive-deps */
  useEffect(() => {
    const fetchProfileImage = async () => {
      if (employee?.employee.profilePictureId) {
        try {
          const response = await downloadEmployeeFile(
            employee.employee.profilePictureId
          );
          if (!response.data || response.data.size === 0) {
            throw new Error('Received empty or invalid blob data');
          }

          const reader = new FileReader();
          reader.onloadend = () => {
            const imageUrl = reader.result as string;

            const isOwnProfile =
              user?.employeeId === employee?.employee.employeeId;

            setProfileImageUrl(imageUrl, isOwnProfile);
          };
          reader.onerror = () => {
            throw new Error('Error converting blob to base64');
          };
          reader.readAsDataURL(response.data);
        } catch (error) {
          throw new Error('Error fetching profile image:' + error);
        }
      }
    };

    fetchProfileImage();
    return () => {
      setProfileImageUrl(null, false);
    };
  }, [employee && employee.employee.id]);
  /* eslint-enable react-hooks/exhaustive-deps */
  useKeyPress(27, () => {
    setIsActiveModalOpen(false);
  });

  return (
    <>
      {isLoadingResponseINTERNAL || isLoadingResponse ? (
        <>
          <MyProfileQuickDetailsMainContainer>
            <MyProfileQuickDetails>
              <Monogram
                className={`unique-monogram ${
                  (showHoverEffect && canEditOwnProfilePic) ||
                  canEditOtherProfilePic
                    ? 'unique-monogram--hover-enabled'
                    : ''
                } ${employee ? employee.account.firstName.charAt(0).toUpperCase() : ''} quickDetails`}
                onClick={handleMonogramClick}
              >
                {profileImageUrl ? (
                  <img
                    key={profileImageUrl}
                    src={profileImageUrl}
                    ref={imageRef}
                    alt="PROFILE"
                    style={{
                      borderRadius: '50%',
                      width: '100%',
                      height: '100%',
                    }}
                  />
                ) : (
                  employee && (
                    <>
                      {employee.account.firstName.charAt(0).toUpperCase()}
                      {employee.account.lastName.charAt(0).toUpperCase()}
                    </>
                  )
                )}
              </Monogram>
              <UserName style={{ width: '100%' }}>
                <span className="skeleton skeleton-text">&nbsp;</span>
                <span className="skeleton skeleton-text skeleton-text-short">
                  &nbsp;
                </span>
              </UserName>
              <RolesDiv>
                <span className="skeleton skeleton-text skeleton-text-short">
                  &nbsp;
                </span>{' '}
              </RolesDiv>
              <BorderDivLine />
              <QuickInfoContactContainer>
                <div>
                  <span>
                    <MessageIconSVG />
                  </span>
                  <span className="skeleton skeleton-text">&nbsp;</span>{' '}
                </div>
                <div className="contactInfo">
                  <span>
                    <PhoneIconSVG />
                  </span>
                  <span className="skeleton skeleton-text">&nbsp;</span>{' '}
                </div>
                <div className="contactInfo">
                  <span>
                    <GlobeIconSVG isActive={false} />
                  </span>
                  {/* FIXME - Update this time zome */}
                  <span className="skeleton skeleton-text">&nbsp;</span>{' '}
                </div>
              </QuickInfoContactContainer>
              <BorderDivLine />
              <QuickInfoDepartmentContainer>
                <div>
                <span>{t('Department')}</span>
                  <span className="skeleton skeleton-text">&nbsp;</span>{' '}
                </div>
              </QuickInfoDepartmentContainer>
            </MyProfileQuickDetails>
          </MyProfileQuickDetailsMainContainer>
        </>
      ) : (
        <MyProfileQuickDetailsMainContainer>
          <MyProfileQuickDetails>
            <Monogram
              className={`unique-monogram ${
                (showHoverEffect && canEditOwnProfilePic) ||
                canEditOtherProfilePic
                  ? 'unique-monogram--hover-enabled'
                  : ''
              } ${employee ? employee.account.firstName.charAt(0).toUpperCase() : ''} quickDetails`}
              onClick={handleMonogramClick}
            >
              {profileImageUrl ? (
                <img
                  key={profileImageUrl}
                  src={profileImageUrl}
                  ref={imageRef}
                  alt="PROFILE"
                  style={{
                    borderRadius: '50%',
                    width: '100%',
                    height: '100%',
                  }}
                />
              ) : (
                employee && (
                  <>
                    {employee.account.firstName.charAt(0).toUpperCase()}
                    {employee.account.lastName.charAt(0).toUpperCase()}
                  </>
                )
              )}
            </Monogram>
            <UserName>
              <span>
                {employee?.account.firstName && employee?.account
                  ? employee?.account.firstName
                  : ''}
                &nbsp;
              </span>
              <span>
                {employee?.account.lastName && employee?.account
                  ? employee?.account.lastName
                  : ''}
              </span>
            </UserName>
            <RolesDiv>
              {/* Used name deleteRolesConstant, because deleteRolesConstant already contains all the roles which user has.. */}
              {employee &&
                employee.account.roles.map((role, index) => (
                  <React.Fragment key={index}>
                    {role.name} &nbsp;
                    {index !== employee.account.roles.length - 1 && (
                      <>
                        <BlueDotDividerSVG /> &nbsp;
                      </>
                    )}{' '}
                  </React.Fragment>
                ))}
            </RolesDiv>
            <span className="statusChangeButton">
              <MyProfileButton
                style={{ cursor: 'auto' }}
                className={
                  employee && !employee.account.active ? 'inactiveButton' : ''
                }
              >
                {employee?.account.active ? t('ACTIVE') : t('INACTIVE')} &nbsp;{' '}
              </MyProfileButton>
              {user &&
                hasPermission(user, EMPLOYEE_MODULE.CHANGE_STATUS) &&
                employee &&
                user.employeeId != employee.account.employeeId && (
                  <span
                    className="arrowIcon"
                    title={`${
                      !employee.account.active ? 'Active' : 'Inactive'
                    } user`}
                    onClick={() => handleIsActiveModalOpen()}
                  >
                    <ArrowDownSVG />
                  </span>
                )}
            </span>
            <BorderDivLine />
            <QuickInfoContactContainer>
              <div>
                <span>
                  <StyledLink
                    to={
                      'mailto:' +
                      (employee?.account && employee?.account
                        ? employee?.account.email
                        : '-')
                    }
                  >
                    <MessageIconSVG />
                  </StyledLink>
                </span>
                <span>
                  <StyledLink
                    to={
                      'mailto:' +
                      (employee?.account && employee?.account
                        ? employee?.account.email
                        : '-')
                    }
                  >
                    <span
                      title={
                        employee?.account && employee?.account
                          ? employee?.account.email
                          : '-'
                      }
                    >
                      {employee?.account && employee?.account
                        ? employee?.account.email
                        : '-'}
                    </span>
                  </StyledLink>
                </span>
              </div>
              <div className="contactInfo">
                <span>
                  {hasContactPhone ? (
                    <StyledLink
                      to={'tel:' + (employee?.employee?.contact?.phone || '-')}
                    >
                      <PhoneIconSVG />
                    </StyledLink>
                  ) : (
                    <PhoneIconSVG />
                  )}
                </span>
                <span>
                  {hasContactPhone ? (
                    <span
                      title={
                        employee?.employee &&
                        employee.employee.contact &&
                        employee?.employee.contact.phone
                          ? employee?.employee.contact.phone
                          : '-'
                      }
                    >
                      <StyledLink
                        to={
                          'tel:' + (employee?.employee?.contact?.phone || '-')
                        }
                      >
                        {employee?.employee &&
                        employee.employee.contact &&
                        employee?.employee.contact.phone
                          ? employee?.employee.contact.phone
                          : '-'}
                      </StyledLink>
                    </span>
                  ) : (
                    <span>
                      {employee?.employee &&
                      employee.employee.contact &&
                      employee?.employee.contact.phone
                        ? employee?.employee.contact.phone
                        : '-'}
                    </span>
                  )}
                </span>
              </div>
              <div className="contactInfo">
                <span>
                  <GlobeIconSVG isActive={false} />
                </span>
                {/* FIXME - Update this time zome */}
                <span
                  title={
                    employee &&
                    TIME_ZONES[
                      employee.account.organizations.preferences.timeZone
                    ]
                  }
                >
                  {employee &&
                    TIME_ZONES[
                      employee.account.organizations.preferences.timeZone
                    ]}
                </span>
              </div>
            </QuickInfoContactContainer>
            <BorderDivLine />
            <QuickInfoDepartmentContainer>
              <div>
                <span>{t('Department')}</span>
                <span>
                  {employee &&
                  employee.employee.jobDetails &&
                  employee.employee.jobDetails.department
                    ? employee.employee.jobDetails.department
                    : '-'}
                </span>
              </div>
              {/*<div title="Feature Not Available">
                {user &&
                hasPermission(user, EMPLOYEE_MODULE.UPDATE_ALL_EMPLOYEES)
                  ? '>'
                  : null}
              </div> */}
            </QuickInfoDepartmentContainer>

            {employee &&
              employee.account.active &&
              user &&
              hasPermission(
                user,
                EMPLOYEE_MODULE.UPDATE_ROLES_AND_PERMISSIONS
              ) &&
              user.employeeId !== employee.account.employeeId && (
                <QuickInfoActionButtions>
                  <span className="statusChangeButtons">
                    <MyProfileButton onClick={handleIsAddRoleDropDownOpen}>
                      &nbsp;{addRoleButtonText} &nbsp; <ArrowDownSVG />
                    </MyProfileButton>
                    {allRolesInOrg && isAddRoleDropDownOpen && (
                      <div ref={isAddRoleDropDownCloseRef}>
                        <AddDropdown
                          handleTextChange={handleAddRoleButtonText}
                          handleIsAddRoleDropDownOpen={
                            handleIsAddRoleDropDownOpen
                          }
                          addRoles={addRole}
                          options={allRolesInOrg
                            .filter(
                              (role) =>
                                !employee.account.roles.some(
                                  (userRole) => userRole.name === role.name
                                )
                            )
                            .map((role) => role.name)}
                        />
                      </div>
                    )}
                  </span>
                  <span className="statusChangeButtons">
                    <MyProfileButton onClick={handleIsDeleteRoleDropDown}>
                      {deleteRoleButtonText} {<ArrowDownSVG />}
                    </MyProfileButton>
                    {isDeleteRoleDropDown &&
                      employee.account.roles.length > 1 && (
                        <div ref={isDeleteRoleDropDownCloseRef}>
                          <DeleteDropdown
                            handleTextChange={HandleDeleteRoleButtonText}
                            handleIsDeleteRoleDropDownOpen={
                              handleIsDeleteRoleDropDown
                            }
                            deleteRoles={deleteRole}
                            options={employee.account.roles.map(
                              (role) => role.name
                            )}
                          />
                        </div>
                      )}
                  </span>
                </QuickInfoActionButtions>
              )}
          </MyProfileQuickDetails>
          {isUpdateButtonShow && (
            <Button
              fontSize="14px"
              className="submit"
              style={{ marginTop: '50px' }}
              onClick={() => {
                handleUpdateRoles();
              }}
            >
              Update
            </Button>
          )}
        </MyProfileQuickDetailsMainContainer>
      )}

      {isActiveModalOpen && employee && (
        <CenterModal
          modalHeading={`${!employee.account.active ? 'Active' : 'Inactive'}`}
          modalContent={`Are you sure to want to ${
            !employee.account.active ? 'active' : 'inactive'
          } '${employee.account.firstName}'`}
          handleModalLeftButtonClick={handleIsActiveModalOpen}
          handleModalClose={handleIsActiveModalOpen}
          handleModalSubmit={handleStatusChange}
          isResponseLoading={isLoadingResponseINTERNAL || isLoadingResponse}
        />
      )}
      {isMonogramModalOpen && (
        <CenterModal
          modalContent={
            <Monogram
              className={`${employee ? employee.account.firstName.charAt(0).toUpperCase() : ''} quickDetails modal-monogram`}
              onClick={handleMonogramClick}
            >
              {profileImageUrl ? (
                <img
                  src={profileImageUrl}
                  ref={imageRef}
                  alt="PROFILE"
                  style={{ borderRadius: '50%', width: '100%', height: '100%' }}
                />
              ) : (
                employee && (
                  <>
                    {employee.account.firstName.charAt(0).toUpperCase()}
                    {employee.account.lastName.charAt(0).toUpperCase()}
                  </>
                )
              )}
            </Monogram>
          }
          handleModalSubmit={handleModalSave}
          modalRightButtonText="Cancel"
          modalLeftButtonText="Upload"
          handleModalLeftButtonClick={handleMonogramUpload}
          handleModalClose={handleModalClose}
          isMonogramView={true}
          compactButtonStyle={true}
          isExpanded={true}
          editText="Edit Profile Picture"
        />
      )}
      {isUploadOpen && employee && (
        <CenterModal
          modalContent={
            <div>
              {croppedImage ? (
                <img
                  src={croppedImage}
                  alt="Cropped Monogram"
                  style={{ borderRadius: '50%' }}
                />
              ) : null}
            </div>
          }
          handleModalClose={handleModalClose}
          handleModalSubmit={handleModalClose}
          handleModalLeftButtonClick={handleFileUpload}
          modalRightButtonText="Cancel"
          modalLeftButtonText="Browse"
          isImageSelected={!!croppedImage}
          isDashedBox={!croppedImage}
          compactButtonStyle={true}
          isExpanded={true}
          editText="Edit Profile Picture"
          dragText="Drag and drop or Browse"
        />
      )}

      {selectedImage && employee && (
        <CenterModal
          modalContent={
            <>
              {selectedImage && (
                <ImagePreviewContainer isDragging={isDragging} id="previewDiv">
                  <ImagePreview
                    src={URL.createObjectURL(selectedImage)}
                    ref={imageRef}
                    alt="Selected for cropping"
                    offsetX={offsetX}
                    offsetY={offsetY}
                    scale={scale}
                    onMouseDown={handleMouseDown}
                    onMouseMove={handleMouseMove}
                    onMouseUp={handleMouseUp}
                    onMouseLeave={handleMouseUp}
                  />
                </ImagePreviewContainer>
              )}

              <ZoomSliderContainer>
                <SliderIconContainer>
                  <SmallSVG />
                </SliderIconContainer>

                <ZoomSlider
                  id="zoom-slider"
                  type="range"
                  min="0.5"
                  max="3"
                  step="0.1"
                  value={scale}
                  onChange={(e) => setScale(parseFloat(e.target.value))}
                />

                <SliderIconContainer>
                  <LargeSVG />
                </SliderIconContainer>
              </ZoomSliderContainer>
            </>
          }
          handleModalSubmit={handleModalClose}
          modalRightButtonText="Cancel"
          modalLeftButtonText="Save"
          handleModalLeftButtonClick={handleSaveAndUpload}
          handleModalClose={handleModalClose}
          isImageSelected={!!selectedImage}
          isMonogramView={false}
          compactButtonStyle={true}
          isExpanded={true}
          editText="Edit Profile Picture"
        />
      )}
    </>
  );
};

export default MyProfileQuickDetailsComponent;

type AddDropDownProps = {
  options: string[];
  handleTextChange: (param: string) => void;
  handleIsAddRoleDropDownOpen: () => void;
  addRoles: (param: string) => void;
};

const AddDropdown = (props: AddDropDownProps) => {
  return (
    <DropDownContainer>
      <ul>
        {props.options.map((option) => (
          <li
            key={option}
            onClick={() => {
              props.handleTextChange(option);
              props.handleIsAddRoleDropDownOpen();
              props.addRoles(option);
            }}
          >
            {option}
          </li>
        ))}
      </ul>
    </DropDownContainer>
  );
};

type DeleteDropDownProps = {
  options: string[];
  handleTextChange: (param: string) => void;
  handleIsDeleteRoleDropDownOpen: () => void;
  deleteRoles: (param: string) => void;
};

const DeleteDropdown = (props: DeleteDropDownProps) => {
  return (
    <DropDownContainer>
      <ul>
        {props.options.map((option) => (
          <li
            key={option}
            onClick={() => {
              props.handleTextChange(option);
              props.handleIsDeleteRoleDropDownOpen();
              props.deleteRoles(option);
            }}
          >
            {option}
          </li>
        ))}
      </ul>
    </DropDownContainer>
  );
};
