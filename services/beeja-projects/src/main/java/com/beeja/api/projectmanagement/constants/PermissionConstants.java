package com.beeja.api.projectmanagement.constants;

public class PermissionConstants {

  // Client-related permissions
  public static final String CREATE_CLIENT = "CCL";
  public static final String UPDATE_CLIENT = "UCL";
  public static final String GET_CLIENT = "GCL";
  public static final String DELETE_CLIENT = "DCL";

  // Contract-related permissions
  public static final String CREATE_CONTRACT = "CCON";
  public static final String UPDATE_CONTRACT = "UCON";
  public static final String GET_CONTRACT = "GCON";
  public static final String DELETE_CONTRACT = "DCON";

  // Project-related permissions
  public static final String CREATE_PROJECT = "CPT";
  public static final String UPDATE_PROJECT = "UPT";
  public static final String GET_PROJECT = "GPT";
  public static final String DELETE_PROJECT = "DPT";

  // Invoice-related permissions
  public static final String CREATE_INVOICE = "CIN";
  public static final String UPDATE_INVOICE = "UIN";
  public static final String GET_INVOICE = "GIN";
  public static final String DELETE_INVOICE = "DIN";
  public static final String UPDATE_STATUS_INVOICE = "USIN";

  // Documents
  public static final String READ_DOCUMENT = "RDCMT";
  public static final String CREATE_DOCUMENT = "CDCMT";
  public static final String DELETE_DOCUMENT = "DDCMT";
  public static final String UPDATE_DOCUMENT = "UDCMT";

  public static final String READ_ALL_DOCUMENTS = "RALDCMT";
  public static final String CREATE_ALL_DOCUMENT = "CALDCMT";
  public static final String DELETE_ALL_DOCUMENT = "DALDCMT";
  public static final String UPDATE_ALL_DOCUMENT = "UALDCMT";
}
