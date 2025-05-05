package tac.beeja.recruitmentapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantComment {
  private int id;
  private String message;
  private String commentedByName;
  private String commentedByEmail;
  private Date createdAt;
  private Date modifiedAt;



}
