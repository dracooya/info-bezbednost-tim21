import {OrganizationData} from "./OrganizationData";

export interface CertificateDetails {
  serialNumber: string;
  ownerEmail: string;
  certificateType: string;
  validFrom: Date;
  validUntil: Date;
  organizationData: OrganizationData;
}
